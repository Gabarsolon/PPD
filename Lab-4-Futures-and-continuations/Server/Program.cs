// A server that accepts pairs of numbers, transmitted as text and separated by whitespace, and sends back their sums

using Server;
using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Threading.Tasks;

class Program
{
	public static void Main(string[] args)
	{
		string[] urls = new string[]{
			"www.google.ro",
			"www.scs.ubbcluj.ro/~trie3244",
			"www.youtube.ro"
		};
		Callbacks.Run(urls);
	}
}