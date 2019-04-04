package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Receive extends Thread {

	private BufferedReader reader = null;
	private String message;
	private Socket client;

	public String getMeassage() {
		return message;
	}

	public void setMeassage(String message) {
		this.message = message;
	}

	public Receive(Socket socket) {
		client = socket;
	}

	public void run() {
		while(true) {
			try {
				reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
				while (true) {
					message = reader.readLine();
					if (message != null) {
						System.out.println(message);
					}
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
	}
}
