package jdbc;

import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;
import org.json.JSONObject;

public class DataDeal {

	private String message = "";
	private JSONObject object;
	private ReentrantLock mLock = new ReentrantLock();
	private Condition mCondition = mLock.newCondition();
	private Login login = new Login();
	private User user = new User();
	private Map<String, Socket> socketMap = new HashMap<>();
	private Socket socket = new Socket();

	public Login getLogin() {
		return login;
	}

	public void setLogin(Login login) {
		this.login = login;
	}

	public Map<String, Socket> getSocketMap() {
		return socketMap;
	}

	public void setSocketMap(Map<String, Socket> socketMap) {
		this.socketMap = socketMap;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public DataDeal() {

	}

	public void deal(String str) {// 收到的消息传进来
		mLock.lock();
		message = sql(str);
		System.out.println(Thread.currentThread() + "datadeal:    " + message);
		mCondition.signal();
		mLock.unlock();
	}

	public String get() {// 处理完的消息传出去
		String str;
		try {
			mLock.lockInterruptibly();
			while (message == null) {
				mCondition.await();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
		str = message;
		message = null;
		mLock.unlock();
		return str;
	}

	private String sql(String str) {
		try {
			object = new JSONObject(str);
			String signal = object.getString("信号");
			if (signal.equals("登录")) {
				str = signin(str, object);
			} else if (signal.equals("改密")) {
				str = expw(str, object);
			} else if (signal.equals("注册")) {
				str = registe(str, object);
			} else if (signal.equals("更新")) {
				str = upinfo(str, object);
			} else if (signal.equals("查询")) {
				str = query(str, object);
			} else if (signal.equals("发送")) {
				send(object);
				str = null;
			} else {
				str = "错误";
			}
		} catch (JSONException e) {
			e.printStackTrace();
			str = e.getMessage();
		}
		return str;
	}

	private void send(JSONObject object) {// 发送群消息
		System.out.println(socketMap);
		DBCon db = new DBCon();
		ResultSet rs;
		List<String> qunList = new ArrayList<String>();
		try {
			String qun = object.getString("用户名");
			String message = object.getString("密码");
			String sql = "SELECT login.username FROM groupmember,login WHERE groupname ='" + qun// 查找在线群成员
					+ "' AND groupmember.username = login.username" + " AND login.lstatus = 1 ";
			rs = db.executeQuery(sql);
			while (rs.next()) {
				System.out.println(rs.getString("login.username"));
				qunList.add(rs.getString("login.username"));
			}
			for (String string : qunList) {
				Socket socket = socketMap.get(string);// 给在线群成员发消息
				if (socket != null && !string.equals(user.getUserName())) {
					PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
					writer.println(user.getUserName() + " : " + message);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String signin(String str, JSONObject object) {// 登录
		DBCon db = new DBCon();
		ResultSet rs;
		String username;
		String password;
		try {
			username = object.getString("用户名");
			password = object.getString("密码");
			String sql = "SELECT password FROM user WHERE username = '" + username + "'";
			rs = db.executeQuery(sql);
			while (rs.next()) {
				user.setPassWord(rs.getString("password"));
			}
			if (password.equals(user.getPassWord())) {// 密码正确
				str = "true";
				socketMap.put(username, socket);

				Date date = new Date();// 登录时间
				String sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
				Timestamp goodsC_date = Timestamp.valueOf(sd);// mysql时间
				login.setDate(goodsC_date);
				login.setStatus(1);

				String sql2 = "UPDATE login SET lip='" + login.getIp() + "',lport=" + login.getPort() + ",ldate='"// 更新登录信息
						+ login.getDate() + "', lstatus=" + login.getStatus() + " WHERE username = '" + username + "'";
				user.setUserName(username);
				db.exercuteUpdate(sql2);
			} else {
				str = "false";
			}
			db.close();
		} catch (Exception e) {
			str = "登录失败";
			e.printStackTrace();
		}
		return str;
	}

	public void signout() {// 退出登录
		DBCon db = new DBCon();
		// System.out.println(socketMap);
		socketMap.remove(user.getUserName());
		// System.out.println(socketMap);
		String sql = "UPDATE login SET lstatus = 0 WHERE username ='" + user.getUserName()+ "'";
		db.exercuteUpdate(sql);
		db.close();
	}

	private String registe(String str, JSONObject object) {// 注册
		DBCon db = new DBCon();
		ResultSet rs;
		String username;
		String password;
		try {
			username = object.getString("用户名");
			password = object.getString("密码");
			String sql1 = "SELECT password FROM user WHERE username = '" + username + "'";
			rs = db.executeQuery(sql1);
			while (rs.next()) {
				user.setPassWord(rs.getString("password"));
			}
			if (user.getPassWord().length() > 0) {
				db.close();
				str = "用户名已存在";
			} else {
				String sql = "INSERT INTO user (username,password) VALUES ( '" + username + "', '" + password + "')";
				String sql2 = "INSERT INTO login (username,lip,lport,ldate,lstatus) VALUES ( '" + username + "', '"
						+ login.getIp() + "', '" + login.getPort() + "', '" + login.getDate() + "', "
						+ login.getStatus() + ")";
				db.exercuteUpdate(sql);
				db.exercuteUpdate(sql2);
				db.close();
				str = "注册成功";
			}
		} catch (Exception e) {
			str = "注册失败";
			e.printStackTrace();
		}
		return str;
	}

	private String expw(String str, JSONObject object) {// 更改密码
		DBCon db = new DBCon();
		try {
			String username = object.getString("用户名");
			String password = object.getString("密码");
			String sql = "UPDATE user SET password = '" + password + "' WHERE username ='" + username + "'";
			db.exercuteUpdate(sql);
			db.close();
			str = "更改成功";
		} catch (Exception e) {
			str = "更改失败";
			e.printStackTrace();
		}
		return str;
	}

	private String upinfo(String str, JSONObject object) {// 更新用户信息
		DBCon db = new DBCon();
		try {
			String username = object.getString("用户名");
			String password = object.getString("密码");
			String sql = "UPDATE user SET password = '" + password + "' WHERE username ='" + username + "'";
			db.exercuteUpdate(sql);
			db.close();
			str = "更新成功";
		} catch (Exception e) {
			str = "更新失败";
			e.printStackTrace();
		}
		return str;
	}

	private String query(String str, JSONObject object) {// 查询
		DBCon db = new DBCon();
		ResultSet rs;
		try {
			String sql1 = "SELECT * FROM user ";
			rs = db.executeQuery(sql1);
			str = "";
			while (rs.next()) {
				user.setUserName(rs.getString("username"));
				user.setPassWord(rs.getString("password"));
				str += user.toString();
			}
			if (str.length() <= 0) {
				str = "空";
			}
		} catch (Exception e) {
			str = "查询失败";
			e.printStackTrace();
		}
		return str;
	}
}
