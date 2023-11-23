// A server that accepts pairs of numbers, transmitted as text and separated by whitespace, and sends back their sums

using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace Server
{
	class AsyncAwait
	{
		private static async void HandleUrl(String url)
		{
			var index = url.IndexOf('/');
			var baseUrl = index < 0 ? url : url[..index];
			var urlPath = index < 0 ? "/" : url[index..];
			var splittedUrl = new Url(baseUrl, urlPath);

			var ipAddress = Dns.GetHostAddresses(baseUrl)[0];
			var endPoint = new IPEndPoint(ipAddress, 80);

			var socket = new Socket(SocketType.Stream, ProtocolType.Tcp);
			var buffer = new byte[100000];

			await Connect(socket, endPoint, new Url(baseUrl, urlPath));
			await Send(socket, splittedUrl);
			int bytesRead = await Receive(socket, splittedUrl, buffer);

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
			}
			else
			{
				Console.WriteLine("Connection closed by the server");
			}
			socket.Close();
		}

		private static Task Connect(Socket socket, IPEndPoint endPoint, Url url)
		{
			TaskCompletionSource promise = new TaskCompletionSource();
			socket.BeginConnect(endPoint, (IAsyncResult ar) => { socket.EndConnect(ar); promise.SetResult(); }, null);
			return promise.Task;
		}

		private static Task<int> Send(Socket socket, Url url)
		{
			TaskCompletionSource<int> promise = new TaskCompletionSource<int>();
			string request = $"GET {url.pathUrl} HTTP/1.1\r\nHost: {url.baseUrl}\r\nContent-Length: 0\r\n\r\n";
			byte[] requestData = Encoding.ASCII.GetBytes(request);
			socket.BeginSend(requestData, 0, requestData.Length, 0, (IAsyncResult ar) => promise.SetResult(socket.EndSend(ar)), null);
			return promise.Task;
		}

		private static Task<int> Receive(Socket socket, Url url, byte[] buffer)
		{
			TaskCompletionSource<int> promise = new TaskCompletionSource<int>();
			socket.BeginReceive(buffer, 0, buffer.Length, 0, (IAsyncResult ar) => promise.SetResult(socket.EndReceive(ar)), null);
			return promise.Task;
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