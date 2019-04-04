package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Send extends Thread{

	private PrintWriter writer;
	private String message;
	private Socket client;
	
	public String getMeassage() {
		return message;
	}
	public void setMeassage(String message) {
		this.message = message;
	}
	public Send(Socket socket) {
		client = socket;
	}
	public void run() {
		try {
			writer = new PrintWriter(client.getOutputStream(),true);
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));		
			while((message = reader.readLine())!=null) {
				writer.println(message);
			}
		}catch(Exception e) {
			e.getStackTrace();
		}
	}
}