package jdbc;

import javax.xml.crypto.Data;

public class Signin {

	private int  id;
	private int groupid;//签到群id
	private int originator;//发起人id
	private Data time;//发起时间
	private String longitude;//发起人经度
	private String latitude;//发起人纬度
	private String region;//签到地理范围
	private int receiver;//签到人id
	private String rlongitude;//签到人经度
	private String rlatitude;//签到人纬度
	private int state;//签到是否结束
	private int done;//签到人是否签到
	private String result;//签到结果
}
