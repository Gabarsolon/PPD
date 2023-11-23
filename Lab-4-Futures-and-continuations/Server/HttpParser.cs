using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Server
{
	internal class HttpParser
	{
		public HttpParser() { }
		public static String ParseHeader(byte[] buffer, int bytesRead)
		{
			String responseToString =  Encoding.ASCII.GetString(buffer, 0, bytesRead);
			var index  = responseToString.IndexOf("\r\n\r\n");
			return responseToString[..index];
		}
		public static int ParseContentLength(string response)
		{
			int contentLengthIndex = response.IndexOf("Content-Length:", StringComparison.OrdinalIgnoreCase);
			if (contentLengthIndex != -1)
			{
				int startIndex = contentLengthIndex + "Content-Length:".Length;
				int endIndex = response.IndexOf('\r', startIndex);
				if (int.TryParse(response.Substring(startIndex, endIndex - startIndex).Trim(), out int contentLength))
				{
					return contentLength;
				}
			}

			return -1; // Invalid or missing Content-Length header
		}
	}
}
