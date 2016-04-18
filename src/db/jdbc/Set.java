package db.jdbc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import server.commonutils.MyStringChecker;
import server.info.config.ConfigFilePath;

public class Set {
	
	private static final String default_username="root";
	private static final String default_passwd="root";
	private static String username;
	private static String password;
	private final static String driver = "com.mysql.jdbc.Driver";
	private final static String url = "jdbc:mysql://localhost:3306/isearch?useUnicode=true&characterEncoding=UTF-8";
	
	public static String getDriver() {
		return driver;
	}
	public static String getUrl() {
		return url;
	}
	public static String getUsername() {
		if(null==username){
			synchronized (Set.class) {
				if(null==username) username=getUsernameFromHibernateConfigFile();
			}
		}
		return username;
	}
	
	public static String getPassword() {
		if(null==password){
			synchronized (Set.class) {
				if(null==password) password=getPasswdFromHibernateConfigFile();
			}
		}
		return password;
	}
	
	private static String getUsernameFromHibernateConfigFile(){
		
		String ret=default_username;
		try{
		String path=ConfigFilePath.getProxoolConfigFilePath();
		File f=new File(path);
		BufferedReader br=new BufferedReader(new FileReader(f));
		for(String line=br.readLine();!MyStringChecker.isWhitespace(line);line=br.readLine()){
			if(!line.contains(".user=")) continue;
			int index=line.indexOf(".user=")+".user=".length();
			ret=line.substring(index, line.length());
			break;
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret.trim();
	}
	
	private static String getPasswdFromHibernateConfigFile(){
		
		String ret = default_passwd;
		try {
			String path = ConfigFilePath.getProxoolConfigFilePath();
			File f = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(f));
			for (String line = br.readLine(); !MyStringChecker.isWhitespace(line);line=br.readLine()) {
				if (!line.contains(".password=")) continue;
				int index = line.indexOf(".password=") + ".password=".length();
				ret = line.substring(index, line.length());
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret.trim();
	}
}
