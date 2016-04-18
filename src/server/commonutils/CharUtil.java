package server.commonutils;

import java.util.regex.Pattern;

public class CharUtil {

	public CharUtil() {

	}

	/**
	 * 根据Unicode编码判断中文汉字和符号
	 * 
	 * @param c
	 *            字符
	 * @return 字符C是不是中文字符或标点等符号
	 */
	private static boolean isChinese(char c) {

		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;

	}

	/**
	 * 完整的判断中文汉字和符号
	 * 
	 * @param strName
	 *            判断字符串中是否存在中文标点或符号
	 * @return 字符串中存在中文标点或符号返回true
	 */
	public static boolean containChinese(String strName) {

		if (strName == null || "".equals(strName))
			return false;
		char[] ch = strName.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isChinesePattern(String str){
		
		if (str == null || "".equals(str))
			return false;
		
		char[] ch = str.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (!isChinese(c)) return false;
		}
		return true;
	}
	

	public static boolean isEnglishPattern(String strName) {

		if (strName == null || "".equals(strName))
			return false;
		return strName.matches("[a-zA-Z0-9]*");
	}

	public static boolean isChineseByREG(String str) {

		if (str == null) {
			return false;
		}
		Pattern pattern = Pattern.compile("[\\u4E00-\\u9FBF]+");
		return pattern.matcher(str.trim()).find();
	}

	// 只能判断部分CJK字符（CJK统一汉字）
	public static boolean isChineseByName(String str) {
		if (str == null) {
			return false;
		}
		// 大小写不同：\\p 表示包含，\\P 表示不包含
		// \\p{Cn} 的意思为 Unicode 中未被定义字符的编码，\\P{Cn} 就表示 Unicode中已经被定义字符的编码
		String reg = "\\p{InCJK Unified Ideographs}&&\\P{Cn}";
		Pattern pattern = Pattern.compile(reg);
		return pattern.matcher(str.trim()).find();
	}

}
