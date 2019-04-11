package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import jdbc.DataDeal;
import jdbc.Login;

public class SerCon {

	private List<Socket> socketList= new ArrayList<>();
	private final int PORT = 5678;
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
	        	socketList.add(client);
	    		DataDeal dataDeal = new DataDeal();
	    		Login login = new Login();//储存登录信息
	            dataDeal.setLogin(login);
	        	
	        	Date date = new Date();//登录时间
	        	String sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	        	Timestamp goodsC_date = Timestamp.valueOf(sd);//mysql时间
	        	login.setDate(goodsC_date);
	        	login.setIp(client.getInetAddress().toString());
	        	login.setPort(client.getPort());
	        	login.setStatus(1);
	        	System.out.println(sd+"/客户端/"+login.getIp()+"/"+login.getPort()+"/"+login.getStatus());

	        	Send send = new Send(client,dataDeal);
	        	Thread sendt = new Thread(send,"SEND");
	        	sendt.start();
	        	Receive receive = new Receive(client,dataDeal,sendt);
	        	new Thread(receive,"RECEIVE").start();
	        }       
	    } catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	}
}
