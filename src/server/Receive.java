package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import jdbc.DataDeal;

public class Receive implements Runnable {

	private BufferedReader reader = null;
	private Socket client;
	private String message;
	private DataDeal dataDeal = new DataDeal(client);
	private long receiveTime = System.currentTimeMillis();

	public Receive(Socket socket,DataDeal dataDeal) {
		client = socket;
		this.dataDeal = dataDeal;
	}

	public void run() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(!Thread.currentThread().isInterrupted()) {
					if(System.currentTimeMillis()-receiveTime>10*1000) {
						dataDeal.signout();
					}
				}
			}
		}).start();
		
		try {
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));		
			while ((message = reader.readLine())!=null) {
				receiveTime = System.currentTimeMillis();
				dataDeal.deal(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
