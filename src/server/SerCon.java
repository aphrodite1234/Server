package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import jdbc.DataDeal;
import jdbc.Login;

public class SerCon {

	Map<String,Socket> socketMap= new HashMap<>();
	private final int PORT = 5679;
	private ServerSocket server;
	private Socket client;
	
	public SerCon() {
		try {
			server = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void invoke() {
		try
	    {       
	        while (true)
	        {     
	        	client =server.accept();
	    		DataDeal dataDeal = new DataDeal();
	    		dataDeal.setSocketMap(socketMap);
	    		dataDeal.setSocket(client);
	    		
	    		Login login = new Login();//储存登录信息
	            dataDeal.setLogin(login);	        	
	        	Date date = new Date();//Socket链接时间
	        	String sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	        	login.setIp(client.getInetAddress().toString());
	        	login.setPort(client.getPort());
	        	System.out.println(sd+login.getIp()+"/"+login.getPort());

	        	Send send = new Send(client,dataDeal);
	        	send.setSocketMap(socketMap);
	        	Thread sendt = new Thread(send,"SEND");
	        	sendt.start();
	        	
	        	Receive receive = new Receive(client,dataDeal,sendt);
	        	receive.setLogin(login);
	        	new Thread(receive,"RECEIVE").start();
	        }       
	    } catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	}
}
