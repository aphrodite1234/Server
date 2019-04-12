package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import jdbc.DataDeal;
import jdbc.Login;

public class Receive implements Runnable {

	private BufferedReader reader = null;
	private Socket client;
	private String message;
	private DataDeal dataDeal = new DataDeal();
	private Thread send = new Thread();
	private Login login = new Login();

	public Login getLogin() {
		return login;
	}

	public void setLogin(Login login) {
		this.login = login;
	}

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
				System.out.println(Thread.currentThread().toString()+client.getInetAddress() + "：" + message);
				dataDeal.deal(message);
				message = reader.readLine();
			}
		} catch (Exception e) {
			dataDeal.signout();
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
