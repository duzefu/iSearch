package server.commonutils;

public class MyStringChecker {

	/**
	 * 判断字符串是否为null或者空字符串
	 * @param str 字符串
	 * @return 如果str是null或者""，返回true；否则返回false。注："\t“会返回false，这个函数不检查空白字符
	 */
	public final static boolean isBlank(String str){
		return str==null||0==str.length();
	}
	
	/**
	 * 检查字符串是否是空白字符
	 * @param str 字符串
	 * @return 如果字符串是null、""或者只由\t，\n以及空格组成，返回true，有其他字符返回false
	 */
	public final static boolean isWhitespace(String str)
	{
		boolean ret=true;
		if(MyStringChecker.isBlank(str)) return ret;
		ret= str.matches("^\\s*$");
		return ret;
	}
}
