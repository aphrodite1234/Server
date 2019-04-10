package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import jdbc.DataDeal;

public class Receive implements Runnable {

	private BufferedReader reader = null;
	private Socket client;
	private String message;
	DataDeal dataDeal = new DataDeal();
	Thread send = new Thread();

	public Receive(Socket socket,DataDeal dataDeal,Thread send) {
		client = socket;
		this.dataDeal = dataDeal;
		this.send=send;
	}

	public void run() {
		try {
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			message = reader.readLine();
			while (message.length()>0) {
				System.out.println(Thread.currentThread()+"客户端" + client.getInetAddress() + "：" + message);
				dataDeal.deal(message);
				message = reader.readLine();
			}
		} catch (Exception e) {
			send.interrupt();
			e.printStackTrace();
			try {
				reader.close();
				client.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
