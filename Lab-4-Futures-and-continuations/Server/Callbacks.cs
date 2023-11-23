// A server that accepts pairs of numbers, transmitted as text and separated by whitespace, and sends back their sums

using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace Server
{
	class Callbacks
	{
		enum State { BeforeRequest, ReadingResponse }

		private Callbacks()
		{

		}

		static async Task<IPAddress> DnsLookupAsync(string host)
		{
			return (await Dns.GetHostAddressesAsync(host))[0];
		}

		private static void HandleUrl(String url)
		{
			var index = url.IndexOf('/');
			var baseUrl = index < 0 ? url : url[..index];
			var urlPath = index < 0 ? "/" : url[index..];

			var ipAddress = Dns.GetHostAddresses(baseUrl)[0];
			var endPoint = new IPEndPoint(ipAddress, 80);

			var socket = new Socket(SocketType.Stream, ProtocolType.Tcp);
			socket.BeginConnect(endPoint, (IAsyncResult ar) => ConnectCallback(ar, socket, new Url(baseUrl, urlPath)), null);
		}

		private static void ConnectCallback(IAsyncResult ar, Socket socket, Url url)
		{
			socket.EndConnect(ar);

			string request = $"GET {url.pathUrl} HTTP/1.1\r\nHost: {url.baseUrl}\r\nContent-Length: 0\r\n\r\n";
			byte[] requestData = Encoding.ASCII.GetBytes(request);
			socket.BeginSend(requestData, 0, requestData.Length, 0, (IAsyncResult ar) => SendCallback(ar, socket, url), null);
		}

		private static void SendCallback(IAsyncResult ar, Socket socket, Url url)
		{
			int bytesSent = socket.EndSend(ar);

			// Start receiving response header
			var buffer = new byte[1000000];
			socket.BeginReceive(buffer, 0, buffer.Length, 0, (IAsyncResult ar) => ReceiveCallback(ar, socket, url, buffer), null);
		}

		private static void ReceiveCallback(IAsyncResult ar, Socket socket, Url url, byte[] buffer)
		{
			int bytesRead = socket.EndReceive(ar);

			if (bytesRead > 0)
			{
				string responseHeader = HttpParser.ParseHeader(buffer, bytesRead);
				int contentLength = HttpParser.ParseContentLength(responseHeader);

				Console.WriteLine("------------------------------------------------------------");
				Console.WriteLine($"Downloaded {url}\n");
				Console.WriteLine("------------------------------------------------------------");
				Console.WriteLine($"Header: {responseHeader}\n");
				Console.WriteLine("------------------------------------------------------------");
				Console.WriteLine($"Content Length: {contentLength}");
				Console.WriteLine("------------------------------------------------------------");

				socket.Close();
			}
			else
			{
				// Connection closed by the server
				socket.Close();
			}
		}

		public static void Run(string[] urls)
		{
			try
			{
				foreach (string url in urls)
				{
					HandleUrl(url);
				}
				while (true)
				{
					Thread.Sleep(100000);
				}
			}
			catch (Exception ex)
			{
				Console.WriteLine("Exception caught: {0}", ex);
			}
		}
	}
}