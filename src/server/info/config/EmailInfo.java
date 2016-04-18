package server.info.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class EmailInfo {

	//找回密码时，发送邮件的邮箱信息
	private static String ms_email;
	private static String ms_passwd;
	
	public final static String emailAddress(){
		return ms_email;
	}
	public final static String passwd(){
		return ms_passwd;
	}
	
	/**
	 * 初始化邮箱地址
	 */
	public static void initEmailAddr() {

		File file = new File(ConfigFilePath.getConfigFileRoot() + "hostemail");
		try {
			FileReader reader = new FileReader(file);
			BufferedReader bf = new BufferedReader(reader);
			String str = null;
			str = bf.readLine();
			if (null != str && !str.isEmpty()) ms_email = str;
			str = bf.readLine();
			if (null != str && !str.isEmpty()) ms_passwd = str;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
