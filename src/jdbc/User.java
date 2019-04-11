package jdbc;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
	
	private JSONObject json = new JSONObject();
	private String userName = "";
	private String passWord = "";
	private String realName = "";
	private int phonenum = 0;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public int getPhonenum() {
		return phonenum;
	}
	public void setPhonenum(int phonenum) {
		this.phonenum = phonenum;
	}
	public String toString() {
		try {
			json.put("用户名", userName);
			json.put("密码", passWord);
			json.put("真实姓名",realName);
			json.put("手机号", phonenum);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString()+"\n";
	}
}
