package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import jdbc.DataDeal;

public class ServiceThread implements Runnable {

	private BufferedReader reader;
	private PrintWriter writer;
	private Socket client;
	private String sendmsg = "";
	private String receivemsg = "";
	DataDeal dataDeal = new DataDeal();
	private List<Socket> socketList= new ArrayList<>();

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

	public ServiceThread(Socket socket ,List<Socket> socketList) {
		client = socket;
		this.socketList=socketList;
	}

	public void run() {
		try {
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			writer = new PrintWriter(client.getOutputStream(), true);
			// 接收消息线程
			new Thread("receive") {
				public void run() {
					try {
						receivemsg = reader.readLine();
						while (receivemsg.length()>0) {
							System.out.println(Thread.currentThread()+"客户端" + client.getInetAddress() + "：" + receivemsg);
							dataDeal.deal(receivemsg);
							receivemsg = reader.readLine();
						}
					} catch (Exception e) {
						try {
							
							writer.close();
							client.close();
							socketList.remove(client);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						e.printStackTrace();
					}
				}
			}.start();
					
			//发送消息
			while (true) {
				sendmsg = dataDeal.get();
				writer.println(sendmsg);
			}		
		} catch (Exception e) {
			try {
				reader.close();
				client.close();
				socketList.remove(client);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.getStackTrace();
		}
	}
}
