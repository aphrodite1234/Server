package jdbc;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.ResultSet;
import org.json.JSONObject;

public class DataDeal {

	private boolean empty;
	private String message = "";
	private JSONObject object;
	
	public DataDeal() {
		
	}
	public synchronized void deal(String str) {	
		while(!empty) {
			try {
				wait();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		message = sql(str);
		System.out.println("datadeal:"+message);
		empty = false;
		notify();
	}
	public synchronized String get() {
		while(empty) {
			try {
				wait();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		String str = message;
		message = "";
		empty = true;
		notify();
		return str;
	}
	
	private String sql(String str) {
		DBCon db = new DBCon();
		User user = new User();
		ResultSet rs;
		String message = "";
		try {
			object = new JSONObject(str);
			String signal = object.getString("信号");
			
			//用户登录
			if(signal.equals("登录")) {
				String username = object.getString("用户名");
				String password = object.getString("密码");
				System.out.println(username+password);
				String sql = "SELECT password FROM user WHERE username = '"+username+"'";
				System.out.println(sql);
				rs = db.executeQuery(sql);
				while(rs.next()) {
					user.setPassWord(rs.getString("password"));
				}
				if(password.equals(user.getPassWord())) {
					message = "true";
				}else {
					message = "false";
				}
				db.close();
			}else if(signal.equals("改密")) {				//更改密码
				String username = object.getString("用户名");
				String password = object.getString("密码");
				String sql = "UPDATE user SET password = '"+password+"' WHERE username ='"+username+"'";
				db.exercuteUpdate(sql);
				db.close();
				message="更改成功";
			}else if(signal.equals("注册")){    //新用户注册
				String username = object.getString("用户名");
				String password = object.getString("密码");			
				String sql1 = "SELECT password FROM user WHERE username = '"+username+"'";
				rs = db.executeQuery(sql1);
				while(rs.next()) {
					user.setPassWord(rs.getString("password"));
				}
				if(user.getPassWord().length()>0) {
					db.close();
					message="用户名已存在";
				}else {
					String sql = "INSERT INTO user (username,password) VALUES ( '"+username+"', '"+password+"')";
					db.exercuteUpdate(sql);
					db.close();
					message = "注册成功";
				}
			}else if(signal.equals("更新")) {      //更新用户信息
				String username = object.getString("用户名");
				String password = object.getString("密码");
				String sql = "UPDATE user SET password = '"+password+"' WHERE username ='"+username+"'";
				db.exercuteUpdate(sql);
				db.close();
				message="更新成功";
			}else {
				message="false";
			}
		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}		
		return message;
	}
}
