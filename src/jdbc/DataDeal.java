package jdbc;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;

import server.KNN;
import server.Send;

public class DataDeal {

	private String message = null;
	private volatile Map<String, Socket> socketMap = new HashMap<>();
	private Socket socket = new Socket();
	private Gson gson = new Gson();
	private volatile Message rMessage = new Message();
	private volatile User user = new User();
	private volatile Group group = new Group();
	private volatile GroupMember groupMember = new GroupMember();
	private Send send;
	private Signin signin = new Signin();

	public DataDeal(Socket socket) {
		this.socket = socket;
		this.send = new Send(socket);
	}

	public void deal(String str) {// 收到的消息传进来
		message = ddeal(str);
		if (message != null) {
			System.out.println(Thread.currentThread() + "ddel    :    " + message);
			rMessage.setContent(message);
			str = gson.toJson(rMessage);
			send.send(str);
		}
	}

	private String ddeal(String str) {
		String rstr = null;
		rMessage = gson.fromJson(str, Message.class);
		String type = rMessage.getType();
		if (!type.equals("心跳")) {
			System.out.println(Thread.currentThread() + str);
		}

		if (type.equals("群消息")) {
			save();
			send();
			rstr = null;
		} else if (type.equals("登录成功")) {
			sendusr();
			myGroup();
			sendoff();
			rstr = null;
		} else if (type.equals("心跳")) {
			// System.out.println("心跳消息");
			if (user.getPhonenum() != null) {
				reLogin();
			}
			rstr = null;
		} else if (type.equals("签到消息")) {
			signin = gson.fromJson(rMessage.getContent(), Signin.class);
			saveSignin();
			send();
			rstr = null;
		} else if (type.equals("用户签到")) {
			signin = gson.fromJson(rMessage.getContent(), Signin.class);
			updateSignin();
		}else if (type.equals("签到截止")) {
			signin = gson.fromJson(rMessage.getContent(), Signin.class);
			updateState();
		} else if (type.equals("搜索群")) {
			rstr = searchGroup();
		} else if (type.equals("创建群")) {
			group = gson.fromJson(rMessage.getContent(), Group.class);
			rstr = createGroup();
		} else if (type.equals("加入群")) {
			groupMember = gson.fromJson(rMessage.getContent(), GroupMember.class);
			rstr = joinGroup();
		} else if (type.equals("退出群")) {
			rstr = quitGroup();
		} else if (type.equals("解散群")) {
			deleteGroup();
		} else {
			user = gson.fromJson(rMessage.getContent(), User.class);
			if (type.equals("登录")) {
				rstr = signin();
			} else if (type.equals("重置密码")) {
				rstr = expw();
			} else if (type.equals("注册")) {
				rstr = registe();
			} else if (type.equals("更新")) {
				rstr = upinfo();
			} else {
				rstr = "false";
			}
		}
		return rstr;
	}

	public String searchGroup() {// 搜索群
		List<String> mGroup = new ArrayList<>();
		String str = null, sql, sql1;
		DBCon db = new DBCon();
		ResultSet rs, rs1;
		try {
			sql = "SELECT * FROM mgroup WHERE groupid =  '" + rMessage.getGroupid() + "'";
			rs = db.executeQuery(sql);
			while (rs.next()) {
				group.setGroupid(rs.getInt("groupid"));
				group.setGroupname(rs.getString("groupname"));
				group.setGroupowner(rs.getString("groupowner"));
				group.setOwnername(rs.getString("ownername"));
				group.setMembernum(rs.getInt("membernum"));
				mGroup.add(gson.toJson(group));
			}
			rMessage.setGroup(mGroup);
			sql1 = "SELECT * FROM groupmember WHERE groupid ='" + rMessage.getGroupid() + "' AND userphone='"
					+ rMessage.getSenderphone() + "'";
			rs1 = db.executeQuery(sql1);
			if (rs1.next()) {
				str = "true";
			} else {
				str = "false";
			}
		} catch (Exception e) {
			str = "else";
			e.printStackTrace();
		}
		db.close();
		return str;
	}

	private void myGroup() {// 已加入的群
		List<String> mGroup = new ArrayList<>();
		List<Integer> groupid = new ArrayList<>();
		DBCon db = new DBCon();
		DBCon db1 = new DBCon();
		DBCon db2 = new DBCon();
		ResultSet rs, rs1;
		String sql, sql1, sql2;
		try {
			sql = "SELECT mgroup.* FROM mgroup,groupmember WHERE mgroup.groupid = groupmember.groupid AND groupmember.userphone = '"
					+ rMessage.getSenderphone() + "'";
			rs = db.executeQuery(sql);
			while (rs.next()) {
				group.setGroupid(rs.getInt("groupid"));
				group.setGroupname(rs.getString("groupname"));
				group.setGroupowner(rs.getString("groupowner"));
				group.setOwnername(rs.getString("ownername"));
				group.setMembernum(rs.getInt("membernum"));
				mGroup.add(gson.toJson(group));
				groupid.add(group.getGroupid());
			}
			rMessage.setType("我的群");
			rMessage.setContent(null);
			rMessage.setGroup(mGroup);
			send.send(gson.toJson(rMessage));
			mGroup.clear();
			for (int id : groupid) {
				sql1 = "SELECT * FROM groupmember WHERE groupid = " + id;
				sql2 = "SELECT * FROM signin WHERE groupid = " + id;
				rs1 = db1.executeQuery(sql1);
				while (rs1.next()) {
					groupMember.setGmid(rs1.getInt("gmid"));
					groupMember.setGroupid(rs1.getInt("groupid"));
					groupMember.setGroupname(rs1.getString("groupname"));
					groupMember.setUsername(rs1.getString("username"));
					groupMember.setUserphone(rs1.getString("userphone"));
					mGroup.add(gson.toJson(groupMember));
				}
				rs = db2.executeQuery(sql2);
				while (rs.next()) {
					signin.setId(rs.getInt("id"));
					signin.setGroupid(rs.getInt("groupid"));
					signin.setOriginator(rs.getString("originator"));
					signin.setLongitude(rs.getString("longitude"));
					signin.setTime(rs.getTimestamp("time"));
					signin.setLatitude(rs.getString("latitude"));
					signin.setRegion(rs.getString("region"));
					signin.setReceiver(rs.getString("receiver"));
					signin.setRlongitude(rs.getString("rlongitude"));
					signin.setRlatitude(rs.getString("rlatitude"));
					signin.setState(rs.getInt("state"));
					signin.setDone(rs.getInt("done"));
					signin.setResult(rs.getString("result"));

					rMessage.setGroupid(rs.getInt("groupid"));
					rMessage.setSenderphone(rs.getString("originator"));
					rMessage.setReceiverphone(rs.getString("receiver"));
					rMessage.setType("签到消息");
					rMessage.setContent(gson.toJson(signin));
					rMessage.setGroup(null);

					send.send(gson.toJson(rMessage));
				}
			}
			rMessage.setType("群成员");
			rMessage.setContent(null);
			rMessage.setGroup(mGroup);
			send.send(gson.toJson(rMessage));
		} catch (Exception e) {
			e.printStackTrace();
		}
		db.close();
		db1.close();
		db2.close();
	}

	private String createGroup() {// 创建群
		DBCon db = new DBCon();
		ResultSet rs;
		String str, sql = null, sql1 = null, sql2;
		try {
			sql = "INSERT INTO mgroup(groupname,groupowner,ownername,membernum) VALUES ( '" + group.getGroupname()
					+ "','" + group.getGroupowner() + "','" + group.getOwnername() + "', 1)";
			db.exercuteUpdate(sql);
			sql2 = "SELECT groupid FROM mgroup ORDER BY groupid DESC limit 1";
			rs = db.executeQuery(sql2);
			rs.next();
			sql1 = "INSERT INTO groupmember(groupid,groupname,userphone,username) VALUES ( " + rs.getInt("groupid")
					+ ",'" + group.getGroupname() + "','" + group.getGroupowner() + "','" + group.getOwnername() + "')";
			db.exercuteUpdate(sql1);
			str = "true";
		} catch (Exception e) {
			str = "false";
			e.printStackTrace();
		}
		db.close();
		return str;
	}

	private String joinGroup() {// 加入群
		DBCon db = new DBCon();
		String str = null, sql = null, sql1;
		try {
			sql = "INSERT INTO groupmember(groupid,groupname,userphone,username) VALUES ( " + groupMember.getGroupid()
					+ ",'" + groupMember.getGroupname() + "','" + groupMember.getUserphone() + "','"
					+ groupMember.getUsername() + "')";
			sql1 = "UPDATE mgroup SET membernum=membernum+1 WHERE groupid = " + rMessage.getGroupid();
			db.exercuteUpdate(sql);
			db.exercuteUpdate(sql1);
			str = "true";
		} catch (Exception e) {
			str = "false";
			e.printStackTrace();
		}
		db.close();
		return str;
	}

	private String quitGroup() {// 退出群
		DBCon db = new DBCon();
		String str = null, sql = null, sql1;
		try {
			sql = "DELETE FROM groupmember WHERE userphone = '" + rMessage.getSenderphone() + "' AND groupid = "
					+ rMessage.getGroupid();
			sql1 = "UPDATE mgroup SET membernum=membernum-1 WHERE groupid = " + rMessage.getGroupid();
			db.exercuteUpdate(sql);
			db.executeQuery(sql1);
			str = "true";
		} catch (Exception e) {
			str = "false";
			e.printStackTrace();
		}
		db.close();
		return str;
	}

	private void deleteGroup() {// 解散群
		DBCon db = new DBCon();
		String sql, sql1,sql2,sql3;
		try {
			sql = "DELETE FROM mgroup WHERE groupowner = '" + rMessage.getSenderphone() + "' AND groupid = "
					+ rMessage.getGroupid();
			sql1 = "DELETE FROM groupmember WHERE groupid = " + rMessage.getGroupid();
			sql2 = "DELETE FROM tsmessage WHERE groupid = " + rMessage.getGroupid();
			sql3 = "DELETE FROM signin WHERE groupid = " + rMessage.getGroupid();
			db.exercuteUpdate(sql);
			db.exercuteUpdate(sql1);
			db.exercuteUpdate(sql2);
			db.exercuteUpdate(sql3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		db.close();
		rMessage.setType("解散群");
		send();
	}

	private void save() {// 保存群消息
		List<String> userphone = new ArrayList<>();
		List<String> username = new ArrayList<>();
		DBCon db = new DBCon();
		ResultSet rs;
		String sql, sql1;
		try {
			sql1 = "SELECT userphone,username FROM groupmember WHERE groupid = '" + rMessage.getGroupid()
					+ "'AND userphone <> '" + rMessage.getSenderphone() + "'";
			rs = db.executeQuery(sql1);
			while (rs.next()) {
				userphone.add(rs.getString("userphone"));
				username.add(rs.getString("username"));
			}
			for (int i = 0; i < userphone.size(); i++) {
				sql = "INSERT INTO tsmessage(type,sender,receiver,content,date,state,sendername,receivername,groupid) VALUES ('"
						+ rMessage.getType() + "','" + rMessage.getSenderphone() + "','" + userphone.get(i) + "','"
						+ rMessage.getContent() + "','" + rMessage.getDate() + "','" + 0 + "','"
						+ rMessage.getSendername() + "','" + username.get(i) + "','" + rMessage.getGroupid() + "')";
				db.exercuteUpdate(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();
	}

	private void saveSignin() {// 保存签到信息
		List<String> userphone = new ArrayList<>();
		DBCon db = new DBCon();
		ResultSet rs;
		String sql, sql1, sql2;
		try {
			sql1 = "SELECT userphone FROM groupmember WHERE groupid = '" + rMessage.getGroupid()+ "'";
			rs = db.executeQuery(sql1);
			while (rs.next()) {
				userphone.add(rs.getString("userphone"));
			}
			for (int i = 0; i < userphone.size(); i++) {
				sql = "INSERT INTO signin(groupid,originator,time,longitude,latitude,region,receiver,rlongitude,rlatitude,state,done,result) VALUES ("
						+ signin.getGroupid() + ",'" + signin.getOriginator() + "','" + signin.getTime() + "','"
						+ signin.getLongitude() + "','" + signin.getLatitude() + "','" + signin.getRegion() + "','"
						+ userphone.get(i) + "','" + 0 + "','" + 0 + "'," + signin.getState() + "," + signin.getDone()
						+ ",'" + signin.getResult() + "')";
				db.exercuteUpdate(sql);
			}
			sql2 = "UPDATE signin SET rlongitude = '" + signin.getRlongitude() + "',rlatitude ='"
					+ signin.getRlatitude() + "', done = 1 WHERE originator = receiver AND TO_DAYS(time) = TO_DAYS( '"
					+ signin.getTime() + "') AND groupid=" + signin.getGroupid();
			db.exercuteUpdate(sql2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		db.close();
	}

	private void updateSignin() {// 用户签到
		int id=signin.getId();
		int state = 0, done = 0;
    	KNN knn = new KNN();
		DBCon db = new DBCon();
		ResultSet rs;
		String sql;
		knn.setGroupid(signin.getGroupid());
		try {
			sql = "UPDATE signin SET rlongitude = '" + signin.getRlongitude() + "', rlatitude = '"
					+ signin.getRlatitude() + "' WHERE id = " + id;
			String sql1 = "SELECT state,done FROM signin WHERE id=" + id;
			db.exercuteUpdate(sql);
			
			knn.setGroupid(signin.getGroupid());
			knn.setId(id);
			knn.setLatitude(Double.parseDouble(signin.getRlatitude()));
			knn.setLongitude(Double.parseDouble(signin.getRlongitude()));
			knn.setRegion(Double.parseDouble(signin.getRegion()));
			knn.knn1();
			
			rs = db.executeQuery(sql1);
			rs.next();
			state = rs.getInt("state");
			done = rs.getInt("done");
			signin.setId(id);
			signin.setState(state);
			signin.setDone(done);
			rMessage.setContent(gson.toJson(signin));
			rMessage.setType("用户签到");
			send.send(gson.toJson(rMessage));
		} catch (Exception e) {
			e.printStackTrace();
		}
		db.close();
	}
	
	private void updateState() {//签到截止
		DBCon db = new DBCon();
		String sql;
		try {
			sql="UPDATE signin SET state = 1 WHERE groupid="+signin.getGroupid()+" AND TO_DAYS(time) = TO_DAYS( '" + signin.getTime() + "')";
			db.exercuteUpdate(sql);
			send();
		}catch(Exception e) {
			e.printStackTrace();
		}
		db.close();
	}

	private void sendoff() {// 发送离线消息
		List<Integer> list = new ArrayList<>();
		DBCon db = new DBCon();
		ResultSet rs;
		String sql = "SELECT * FROM tsmessage WHERE state = 0 AND receiver = '" + user.getPhonenum() + "'";
		try {
			rs = db.executeQuery(sql);
			while (rs.next()) {
				list.add(rs.getInt("id"));
				rMessage.setGroupid(rs.getInt("groupid"));
				rMessage.setSenderphone(rs.getString("sender"));
				rMessage.setSendername(rs.getString("sendername"));
				rMessage.setReceivername(rs.getString("receivername"));
				rMessage.setReceiverphone(rs.getString("receiver"));
				rMessage.setContent(rs.getString("content"));
				rMessage.setType("群消息");
				rMessage.setDate(rs.getTimestamp("date"));
				rMessage.setGroup(null);
				send.send(gson.toJson(rMessage));
			}
			for (int in : list) {
				String sql1 = "UPDATE tsmessage SET state = 1 WHERE id = " + in;
				db.exercuteUpdate(sql1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		db.close();
	}

	public void sendusr() {// 下载用户信息
		DBCon db = new DBCon();
		ResultSet rs;
		String sql = "SELECT * FROM user WHERE phonenum = '" + user.getPhonenum() + "'";
		try {
			rs = db.executeQuery(sql);
			while (rs.next()) {
				user.setUserName(rs.getString("username"));
				user.setBirthday(rs.getDate("birthday"));
				user.setLocate(rs.getString("locate"));
				user.setSex(rs.getString("sex"));
				user.setSignature(rs.getString("signature"));
				user.setRealName(rs.getString("realname"));
				rMessage.setType("更新信息");
				rMessage.setContent(gson.toJson(user));
			}
			send.send(gson.toJson(rMessage));
		} catch (Exception e) {
			e.printStackTrace();
		}
		db.close();
	}

	public void send() {// 消息推送
		System.out.println("在线用户：" + socketMap);
		DBCon db = new DBCon();
		DBCon db1 = new DBCon();
		ResultSet rs;
		List<String> userphone = new ArrayList<String>();
		try {// 查找在线群成员
			String sql = "SELECT login.phonenum FROM groupmember,login WHERE groupmember.groupid ="
					+ rMessage.getGroupid()+ " AND groupmember.userphone = login.phonenum" + " AND login.lstatus = 1 ";
			rs = db.executeQuery(sql);
			while (rs.next()) {
				// System.out.println(rs.getString("login.phonenum"));
				userphone.add(rs.getString("login.phonenum"));
			}
			for (String string : userphone) {
				Socket socket = socketMap.get(string);// 给在线群成员推送消息
				if (socket != null && !string.equals(user.getPhonenum())) {
					PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

					if (rMessage.getType().equals("群消息")) {
						String sql1 = "UPDATE tsmessage SET state = 1 WHERE receiver = '" + string
								+ "' AND TO_DAYS(date) = TO_DAYS( '" + rMessage.getDate() + "') AND groupid = "
								+ rMessage.getGroupid();
						db.exercuteUpdate(sql1);
					} else if (rMessage.getType().equals("签到消息")) {
						String sql3 = "SELECT id FROM signin WHERE receiver = '" + string
								+ "' AND TO_DAYS(time) = TO_DAYS( '" + signin.getTime() + "') AND groupid="
								+ signin.getGroupid();
						rs = db1.executeQuery(sql3);
						if (rs.next()) {
							signin.setId(rs.getInt("id"));
						}
						rMessage.setContent(gson.toJson(signin));
					}
					writer.println(gson.toJson(rMessage));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		db.close();
		db1.close();
	}

	private void reLogin() {// 更新登录信息
		DBCon db = new DBCon();
		String phonenum;
		try {
			phonenum = user.getPhonenum();
			socketMap.put(phonenum, socket);
			Date date = new Date();// 登录时间
			String sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
			Timestamp goodsC_date = Timestamp.valueOf(sd);// mysql时间

			String sql2 = "UPDATE login SET lip='" + socket.getInetAddress().toString() + "',lport=" + socket.getPort()
					+ ",ldate='" + goodsC_date + "', lstatus= 1 WHERE phonenum = '" + phonenum + "'";
			db.exercuteUpdate(sql2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String signin() {// 登录
		DBCon db = new DBCon();
		ResultSet rs;
		String phonenum = null, password = null, str = null, sql = null;
		try {
			phonenum = user.getPhonenum();
			sql = "SELECT password FROM user WHERE phonenum = '" + phonenum + "'";
			rs = db.executeQuery(sql);
			while (rs.next()) {
				password = rs.getString("password");
			}
			if (password.equals(user.getPassWord())) {// 密码正确
				str = "true";
				reLogin();
			} else {
				str = "false";
			}
		} catch (Exception e) {
			str = "false";
			e.printStackTrace();
		}
		db.close();
		return str;
	}

	public void signout() {// 退出登录
		DBCon db = new DBCon();
		socketMap.remove(user.getPhonenum());
		System.out.println(socketMap);
		System.out.println(user.getPhonenum() + "已下线~");
		String sql = "UPDATE login SET lstatus = 0 WHERE phonenum ='" + user.getPhonenum() + "'";
		db.exercuteUpdate(sql);
		db.close();
		Thread.currentThread().interrupt();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String registe() {// 注册
		DBCon db = new DBCon();
		ResultSet rs;
		String phonenum = null, password = null, str = null;
		try {
			phonenum = user.getPhonenum();
			String sql1 = "SELECT password FROM user WHERE phonenum = '" + phonenum + "'";
			rs = db.executeQuery(sql1);
			while (rs.next()) {
				password = rs.getString("password");
			}
			if (password != null) {
				str = "用户已存在";
			} else {
				String sql = "INSERT INTO user (phonenum,password) VALUES ( '" + phonenum + "','" + user.getPassWord()+ "')";
				String sql2 = "INSERT INTO login (phonenum) VALUES ( '" + phonenum + "')";
				db.exercuteUpdate(sql);
				db.exercuteUpdate(sql2);
				str = "true";
			}
		} catch (Exception e) {
			str = "注册失败";
			e.printStackTrace();
		}
		db.close();
		return str;
	}

	private String expw() {// 重置密码
		DBCon db = new DBCon();
		String str = null;
		try {
			String password = user.getPassWord(), phone = user.getPhonenum();
			String sql = "UPDATE user SET password = '" + password + "' WHERE phonenum ='" + phone + "'";
			db.exercuteUpdate(sql);
			str = "true";
		} catch (Exception e) {
			str = "重置失败";
			e.printStackTrace();
		}
		db.close();
		return str;
	}

	private String upinfo() {// 更新用户信息
		DBCon db = new DBCon();
		String str = null;
		try {
			String sql = "UPDATE user SET username = '" + user.getUserName() + "',realname = '" + user.getRealName()
					+ "',birthday ='" + user.getBirthday() + "',sex = '" + user.getSex() + "',locate = '"
					+ user.getLocate() + "' WHERE phonenum ='" + user.getPhonenum() + "'",
					sql1 = "UPDATE login SET username = '" + user.getUserName() + "' WHERE phonenum ='"
							+ user.getPhonenum() + "'",
					sql2 = "UPDATE mgroup SET ownername = '" + user.getUserName() + "' WHERE groupowner = '"
							+ user.getPhonenum() + "'",
					sql3 = "UPDATE groupmember SET username = '" + user.getUserName() + "' WHERE userphone = '"
							+ user.getPhonenum() + "'",
					sql4 = "UPDATE tsmessage SET sendername = '" + user.getUserName() + "' WHERE sender = '"
							+ user.getPhonenum() + "'",
					sql5 = "UPDATE tsmessage SET receivername = '" + user.getUserName() + "' WHERE receiver = '"
							+ user.getPhonenum() + "'",
					sql6 = "UPDATE user SET photo = ? WHERE phonenum = '" + user.getPhonenum() + "'";
			db.exercuteUpdate(sql);
			db.exercuteUpdate(sql1);
			db.exercuteUpdate(sql2);
			db.exercuteUpdate(sql3);
			db.exercuteUpdate(sql4);
			db.exercuteUpdate(sql5);
			db.ps = db.con.prepareStatement(sql6);
			db.ps.setBinaryStream(1, user.getPhoto());
			db.ps.executeUpdate();
			db.close();
			str = "true";
		} catch (Exception e) {
			str = "false";
			e.printStackTrace();
		}
		return str;
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
}
