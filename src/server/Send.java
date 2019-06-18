package server;

import java.io.PrintWriter;
import java.net.Socket;

public class Send{

	private PrintWriter writer;
	private Socket client;

	public Send(Socket socket) {
		client = socket;
	}
	
	public Send() {
	}

	public void send(String string) {
		try {
			writer = new PrintWriter(client.getOutputStream(),true);
			writer.println(string);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}