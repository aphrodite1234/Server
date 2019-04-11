package jdbc;

import java.sql.ResultSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONObject;

public class DataDeal {

	private String message = "";
	private JSONObject object;
	private ReentrantLock mLock = new ReentrantLock();
	private Condition mCondition = mLock.newCondition();
	private Login login = new Login();

	public Login getLogin() {
		return login;
	}

	public void setLogin(Login login) {
		this.login = login;
	}

	public DataDeal() {

	}

	public void deal(String str) {
		mLock.lock();
		message = sql(str);
		System.out.println(Thread.currentThread() + "datadeal:    " + message);
		mCondition.signal();
		mLock.unlock();
	}

	public String get() {
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
		DBCon db = new DBCon();
		User user = new User();
		ResultSet rs;
		String message = "";
		try {
			object = new JSONObject(str);
			String signal = object.getString("信号");

			// 用户登录
			if (signal.equals("登录")) {
				String username = object.getString("用户名");
				String password = object.getString("密码");
				System.out.println(username + password);
				String sql = "SELECT password FROM user WHERE username = '" + username + "'";
				rs = db.executeQuery(sql);
				while (rs.next()) {
					user.setPassWord(rs.getString("password"));
				}
				if (password.equals(user.getPassWord())) {
					message = "true";
					String sql2 = "UPDATE login SET lip='" + login.getIp() + "',lport='" + login.getPort() + "',ldate='"
							+ login.getDate() + "', lstatus='" + login.getStatus() + "' WHERE username = '" + username
							+ "'";
					db.exercuteUpdate(sql2);
				} else {
					message = "false";
				}
				db.close();
			} else if (signal.equals("改密")) { // 更改密码
				String username = object.getString("用户名");
				String password = object.getString("密码");
				String sql = "UPDATE user SET password = '" + password + "' WHERE username ='" + username + "'";
				db.exercuteUpdate(sql);
				db.close();
				message = "更改成功";
			} else if (signal.equals("注册")) { // 新用户注册
				String username = object.getString("用户名");
				String password = object.getString("密码");
				String sql1 = "SELECT password FROM user WHERE username = '" + username + "'";
				rs = db.executeQuery(sql1);
				while (rs.next()) {
					user.setPassWord(rs.getString("password"));
				}
				if (user.getPassWord().length() > 0) {
					db.close();
					message = "用户名已存在";
				} else {
					String sql = "INSERT INTO user (username,password) VALUES ( '" + username + "', '" + password
							+ "')";
					String sql2 = "INSERT INTO login (username,lip,lport,ldate,lstatus) VALUES ( '" + username + "', '"
							+ login.getIp() + "', '" + login.getPort() + "', '" + login.getDate() + "', '"
							+ login.getStatus() + "')";
					db.exercuteUpdate(sql);
					db.exercuteUpdate(sql2);
					db.close();
					message = "注册成功";
				}
			} else if (signal.equals("更新")) { // 更新用户信息
				String username = object.getString("用户名");
				String password = object.getString("密码");
				String sql = "UPDATE user SET password = '" + password + "' WHERE username ='" + username + "'";
				db.exercuteUpdate(sql);
				db.close();
				message = "更新成功";
			} else if (signal.equals("查询")) {
				String sql1 = "SELECT * FROM user ";
				rs = db.executeQuery(sql1);
				message = "";
				while (rs.next()) {
					user.setUserName(rs.getString("username"));
					user.setPassWord(rs.getString("password"));
					message += user.toString();
				}
				if (message.length() <= 0) {
					message = "空";
				}
			} else {
				message = "false";
			}
		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}
		return message;
	}
}
