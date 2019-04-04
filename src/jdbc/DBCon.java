package jdbc;

import java.sql.*;
public class DBCon {

	// JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost:3306/sign?useSSL=false&serverTimezone=UTC";
    // 数据库的用户名与密码
    static final String USER = "root";
    static final String PASS = "5780..an";
    Connection con =null;
    Statement stmt = null;
    ResultSet rs = null;
    
    public DBCon() {
    	try {
    		Class.forName(JDBC_DRIVER);
    		con=DriverManager.getConnection(DB_URL,USER,PASS);
    		stmt=con.createStatement();
    	}catch(Exception e) {
    		e.getStackTrace();
    	}
    }
    
    //查询
    public ResultSet executeQuery(String sql) {
    	try {
    		rs=stmt.executeQuery(sql);
    	}catch(Exception e) {    		
    		e.getStackTrace();
    	}
    	return rs;
    }
    
    //增、删、改
    public int exercuteUpdate(String sql) {
    	int rowCount = 0;
    	try {
    		rowCount=stmt.executeUpdate(sql);
    	}catch(Exception e) {
    		e.getStackTrace();
    	}
    	return rowCount;
    }
    //关闭
    public void close() {
    	try {
    		con.close();
    		con=null;
    	}catch(Exception e) {
    		e.getStackTrace();
    	}
    }
}
