package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import jdbc.DataDeal;

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
		DataDeal dataDeal = new DataDeal();
		try
	    {       
	        while (true)
	        {     
	            client =server.accept();   
	        	socketList.add(client);
	        	System.out.println("客户端"+client.getInetAddress());

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
