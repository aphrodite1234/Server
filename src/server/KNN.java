package server;

import java.sql.ResultSet;
import java.sql.SQLException;

import jdbc.DBCon;

public class KNN {

	private int id;
	private int groupid;
	private double longitude;// 签到人经度
	private double latitude;// 签到人纬度
	private double region;// KNN范围

	private double[][] location() {//存储经度、纬度、id
		double[][] location = null;
		int i;
		DBCon db = new DBCon();
		ResultSet rs;
		String sql = "SELECT membernum FROM mgroup WHERE groupid = " + groupid;
		rs = db.executeQuery(sql);
		try {
			rs.next();
			i = rs.getInt("membernum");
			location = new double[i][3];
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();
		return location;
	}

	public void knn() {
		int count=0;
		double[][] location = location();//已签到
		double[][] location1 = location();//未签到
		DBCon db = new DBCon();
		DBCon db1 = new DBCon();
		ResultSet rs,rs1;
		String sql = "SELECT id,rlongitude,rlatitude FROM signin WHERE groupid =" + groupid + " AND done = 1 AND state = 0",
				sql1 = "SELECT id,rlongitude,rlatitude,region FROM signin WHERE groupid =" + groupid + " AND done = 0 AND state = 0";
		try {
			rs1 = db1.executeQuery(sql1);
			while(rs1.next()) {
				id=rs1.getInt("id");
				region = Double.parseDouble(rs1.getString("region"));
				longitude = Double.parseDouble(rs1.getString("rlongitude"));
				latitude = Double.parseDouble(rs1.getString("rlatitude"));
				location1[count][0] = longitude;
				location1[count][1] = latitude;
				location1[count][2] = id;
				count++;
			}

			count=0;
			rs = db.executeQuery(sql);
			while(rs.next()) {
				id=rs.getInt("id");
				longitude = Double.parseDouble(rs.getString("rlongitude"));
				latitude = Double.parseDouble(rs.getString("rlatitude"));
				location[count][0] = longitude;
				location[count][1] = latitude;
				location[count][2] = id;
				count++;
			}
			for(int i=0;i<location.length;i++) {
				if(location[i][0]!=0) {
					for(int j=0;j<location.length;j++) {
						if(location1[j][0]!=0) {
							if(k(location[i][0],location[i][1],location1[j][0],location1[j][1])) {
								String sql2="UPDATE signin SET done = 1 WHERE id="+location1[j][2];
								db.exercuteUpdate(sql2);
								for(int x=0;x<location.length;x++) {
									if(location[x][0]==0) {
										location[x][0]=location1[j][0];
										location[x][1]=location1[j][1];
										location[x][2]=location1[j][2];
										break;
									}
								}
								location1[j][0]=location1[j][1]=location1[j][2]=0;
							}
						}
					}
				}else
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void knn1() {
		int count=0;
		double[][] location = location();//已签到
		DBCon db = new DBCon();
		ResultSet rs;
		String sql = "SELECT id,rlongitude,rlatitude FROM signin WHERE groupid =" + groupid + " AND done = 1 AND state = 0";
		try {
			count=0;
			rs = db.executeQuery(sql);
			while(rs.next()) {
				location[count][0] = Double.parseDouble(rs.getString("rlongitude"));
				location[count][1] = Double.parseDouble(rs.getString("rlatitude"));
				location[count][2] = rs.getInt("id");
				count++;
			}
			for(int i=0;i<location.length;i++) {
				if(k(location[i][1],location[i][0],latitude,longitude)) {
					String sql2="UPDATE signin SET done = 1 WHERE id="+id;
					db.exercuteUpdate(sql2);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean k(double lat, double lon, double lat1, double lon1) {
		double dou = Distance(lat,lon,lat1,lon1)*1000;
		
		if (dou <= region)
			return true;
		else
			return false;
	}
	
	 public static double HaverSin(double theta)
     {
		 double v = Math.sin(theta / 2);
         return v * v;
     }

     static double EARTH_RADIUS = 6371.0;//km 地球半径 平均值，千米

     /// <summary>
     /// 给定的经度1，纬度1；经度2，纬度2. 计算2个经纬度之间的距离。
     /// </summary>
     /// <param name="lat1">经度1</param>
     /// <param name="lon1">纬度1</param>
     /// <param name="lat2">经度2</param>
     /// <param name="lon2">纬度2</param>
     /// <returns>距离（公里、千米）</returns>
     public static double Distance(double lat1,double lon1, double lat2,double lon2)
     {
         //用haversine公式计算球面两点间的距离。
         //经纬度转换成弧度
         lat1 = ConvertDegreesToRadians(lat1);
         lon1 = ConvertDegreesToRadians(lon1);
         lat2 = ConvertDegreesToRadians(lat2);
         lon2 = ConvertDegreesToRadians(lon2);

         //差值
         double vLon = Math.abs(lon1 - lon2);
         double vLat = Math.abs(lat1 - lat2);

         //h is the great circle distance in radians, great circle就是一个球体上的切面，它的圆心即是球心的一个周长最大的圆。
         double h = HaverSin(vLat) + Math.cos(lat1) * Math.cos(lat2) * HaverSin(vLon);

         double distance = 2 * EARTH_RADIUS * Math.asin(Math.sqrt(h));

         return distance;
     }

     /// <summary>
     /// 将角度换算为弧度。
     /// </summary>
     /// <param name="degrees">角度</param>
     /// <returns>弧度</returns>
     public static double ConvertDegreesToRadians(double degrees)
     {
         return degrees * Math.PI / 180;
     }

     public static double ConvertRadiansToDegrees(double radian)
     {
         return radian * 180.0 / Math.PI;
     }

	public int getGroupid() {
		return groupid;
	}

	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getRegion() {
		return region;
	}

	public void setRegion(double region) {
		this.region = region;
	}

}
