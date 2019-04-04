package server;

import jdbc.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class SerCon {

	private List<Socket> socketList= new ArrayList<>();
	private final int PORT = 5678;
	private ServerSocket server;
	private Socket client;
	private String sendmsg = "";
	private String receivemsg = "";
	
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
	public SerCon() {
		try {
			server = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void invoke() {
		DataDeal dataDeal = new DataDeal();
		try
	    {       
	        while (true)
	        {     
	            client =server.accept();   
	        	//socketList.add(client);
	        	System.out.println("客户端"+client.getInetAddress());
	        	//new ServiceThread(client).start();
	        	ServiceThread serviceThread = new ServiceThread(client,dataDeal);
	        	serviceThread.start();
	        }       
	    } catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	}
}
