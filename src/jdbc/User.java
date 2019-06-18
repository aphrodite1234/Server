package jdbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class User {

	private String userName = null;
	private String passWord = null;
	private String realName = null;
	private String phonenum = null;
	private InputStream photo = null;
	private String sex = null;
	private String locate = null;
	private String birthday;
	private String signature = null;

	public FileInputStream getImageByte(String infile) {// 读取图片的二进制流
		FileInputStream imageByte = null;
		File file = new File(infile);
		try {
			imageByte = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return imageByte;
	}

	public void readBlob(InputStream inputStream, String path) {// 二进制流读出为图片
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(path);
			byte[] buffer = new byte[inputStream.available()];
			int len = 0;
			while ((len = inputStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, len);
			}
			inputStream.close();
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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

	public String getPhonenum() {
		return phonenum;
	}

	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}

	public InputStream getPhoto() {
		return photo;
	}

	public void setPhoto(InputStream photo) {
		this.photo = photo;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getLocate() {
		return locate;
	}

	public void setLocate(String locate) {
		this.locate = locate;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String sd = sdf.format(birthday);   
		this.birthday = sd;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
