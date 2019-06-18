package jdbc;

public class GroupMember {

	private int gmid;
	private int groupid;
	private String groupname;
	private String username;
	private String userphone;
	public int getGmid() {
		return gmid;
	}
	public void setGmid(int gmid) {
		this.gmid = gmid;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserphone() {
		return userphone;
	}
	public void setUserphone(String userphone) {
		this.userphone = userphone;
	}
	public int getGroupid() {
		return groupid;
	}
	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}
}
