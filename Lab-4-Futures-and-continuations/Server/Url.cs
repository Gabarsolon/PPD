using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace Server
{
	internal class Url
	{
		public string baseUrl { get; set; }
		public string pathUrl { get; set; }
		public Url(string baseUrl, string pathUrl)
		{
			this.baseUrl = baseUrl;
			this.pathUrl = pathUrl;
		}
		public override string ToString()
		{
			return baseUrl + pathUrl;
		}
	}
}
