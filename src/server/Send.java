package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import jdbc.DataDeal;

public class Send implements Runnable{

	private PrintWriter writer;
	private Socket client;
	private String message;
	DataDeal dataDeal = new DataDeal();
	private Map<String,Socket> socketMap= new HashMap<>();
	
	public Map<String,Socket> getSocketMap() {
		return socketMap;
	}

	public void setSocketMap(Map<String,Socket> socketMap) {
		this.socketMap = socketMap;
	}

	public Send(Socket socket,DataDeal dataDeal) {
		client = socket;
		this.dataDeal = dataDeal;
	}
	
	public Send() {
	}

	public void run() {
		try {
			writer = new PrintWriter(client.getOutputStream(),true);
			while(!Thread.currentThread().isInterrupted()) {
				message = dataDeal.get();
				writer.println(message);
			}
		}catch(Exception e) {
			e.printStackTrace();
			try {
				writer.close();
				client.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}