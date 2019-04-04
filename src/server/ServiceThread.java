package server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import org.json.JSONObject;

import jdbc.DataDeal;

public class ServiceThread extends Thread {

	private BufferedReader reader;
	private PrintWriter writer;
	private Socket client;
	private String sendmsg = "";
	private String receivemsg = "";
	DataDeal dataDeal = new DataDeal();

	public String getSendmsg() {
		return sendmsg;
	}

	public void setSendmsg(String sendmsg) {
		this.sendmsg = sendmsg;
	}

	public String getReceivemsg() {
		return receivemsg;
	}

	public void setReceivemsg(String receivemsg) {
		this.receivemsg = receivemsg;
	}

	public ServiceThread() {

	}

	public ServiceThread(Socket socket,DataDeal dataDeal) {
		client = socket;
		this.dataDeal = dataDeal;
	}

	public void run() {
		try {
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			// 接收消息线程
			new Thread() {
				public void run() {
					try {
						receivemsg = reader.readLine();
						while (receivemsg.length()>0) {
							System.out.println("客户端" + client.getInetAddress() + "：" + receivemsg);
							dataDeal.deal(receivemsg);
							receivemsg = reader.readLine();
						}
					} catch (Exception e) {
						try {
							client.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						e.printStackTrace();
					}
				}
			}.start();
			// 发送消息
			//BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			writer = new PrintWriter(client.getOutputStream(), true);
			while (true) {
				sendmsg = dataDeal.get();
				writer.println(sendmsg);
			}
		} catch (Exception e) {
			try {
				client.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.getStackTrace();
		}
	}
}
