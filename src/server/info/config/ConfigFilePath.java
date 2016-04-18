package server.info.config;

import java.io.File;

/**
 * 在这个类里面放一些配置文件的路径常量，相关的路径信息应该是在服务器启动时第一个初始化的（第一个Listener中调用这个类的init()）
 * 其中的值的获取应该是不依赖于其他类、文件（涉及路径）、数据库（可能涉及路径）
 * @author zcl
 */
public class ConfigFilePath {

	//项目运行时根目录，如果编程时文件放在WebRoot目录下面，就是这个目录
	private static String projectRoot;
	//项目classpath，如果编程时把文件放在了src目录下，在运行时，文件就会在这个目录下面
	private static String classpath;
	//配置文件位置
	private static String configFileRoot;
	//UserXML文件根目录
	private static String userXMLRoot;
	
	public final static String getProjectRoot() {
		return projectRoot;
	}
	public final static String getClasspath(){
		return classpath;
	}
	public final static String getConfigFileRoot(){
		return configFileRoot;
	}
	public final static String getProxoolConfigFilePath(){
		return classpath+"proxool.properties";
	}
	public final static String getUserXMLFileRoot(){
		return userXMLRoot;
	}
	
	/**
	 * 服务器初始化时通过一个Listener来调用，可以初始化字符串常量值
	 * @param webRoot 项目根目录
	 */
	public static void init(String webRoot) {

		projectRoot = webRoot;
		classpath=projectRoot+"WEB-INF/classes/";
		configFileRoot = projectRoot + "configure/";
		userXMLRoot=projectRoot+"UserXMLFiles/";
		
		File f = new File(configFileRoot);
		if (!f.exists()) f.mkdirs();
		f=new File(userXMLRoot);
		if(!f.exists()) f.mkdirs();
	}

}
