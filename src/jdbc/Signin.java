package jdbc;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Signin {

	private int  id;
	private int groupid;//签到群id
	private String originator;//发起人手机号
	private String time;//发起时间
	private String longitude;//发起人经度
	private String latitude;//发起人纬度
	private String region;//签到地理范围
	private String receiver;//签到人手机号
	private String rlongitude;//签到人经度
	private String rlatitude;//签到人纬度
	private int state;//签到是否结束
	private int done;//签到人是否签到
	private String result;//签到结果
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getGroupid() {
		return groupid;
	}
	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}
	public String getOriginator() {
		return originator;
	}
	public void setOriginator(String originator) {
		this.originator = originator;
	}
	public String getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(time);
		this.time = sd;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getRlongitude() {
		return rlongitude;
	}
	public void setRlongitude(String rlongitude) {
		this.rlongitude = rlongitude;
	}
	public String getRlatitude() {
		return rlatitude;
	}
	public void setRlatitude(String rlatitude) {
		this.rlatitude = rlatitude;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getDone() {
		return done;
	}
	public void setDone(int done) {
		this.done = done;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
}
