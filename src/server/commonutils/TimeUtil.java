package server.commonutils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeUtil {

	public final static String defaultDateSeparator = "-";
	public final static String defaultTimeSeparator = ":";

	public TimeUtil() {

	}

	/**
	 * 获得当前运行时系统的日期（yyyy-mm-dd）
	 * 
	 * @return
	 */
	public static String getCurrentDateString() {

		Calendar cal = Calendar.getInstance();
		return TimeUtil.formatDBDateString(TimeUtil.getYear(cal),
				TimeUtil.getMonth(cal), TimeUtil.getDay(cal));

	}

	/**
	 * 获得当前运行时系统的时间（hh:mm:ss）
	 * 
	 * @return
	 */
	public static String getCurrentTimeString() {

		Calendar cal = Calendar.getInstance();
		return TimeUtil.formatDBTimeString(TimeUtil.getHour(cal),
				TimeUtil.getMinute(cal), TimeUtil.getSecond(cal));
	}

	public static Integer getYear(Calendar cal) {

		Integer ret = null;
		try {
			ret = cal.get(Calendar.YEAR);
		} catch (Exception e) {
			ret = null;
		}

		return ret;
	}

	public static Integer getMonth(Calendar cal) {
		Integer ret = null;
		try {
			ret = cal.get(Calendar.MONTH) + 1;
		} catch (Exception e) {
			ret = null;
		}
		return ret;
	}

	public static Integer getDay(Calendar cal) {
		Integer ret = null;
		try {
			ret = cal.get(Calendar.DAY_OF_MONTH);
		} catch (Exception e) {
			ret = null;
		}
		return ret;
	}

	public static Integer getHour(Calendar cal) {

		Integer ret = null;
		try {
			ret = cal.get(Calendar.HOUR_OF_DAY);
		} catch (Exception e) {
			ret = null;
		}
		return ret;
	}

	public static Integer getMinute(Calendar cal) {

		Integer ret = null;
		try {
			ret = cal.get(Calendar.MINUTE);
		} catch (Exception e) {
			ret = null;
		}
		return ret;
	}

	public static Integer getSecond(Calendar cal) {

		Integer ret = null;
		try {
			ret = cal.get(Calendar.SECOND);
		} catch (Exception e) {
			ret = null;
		}
		return ret;
	}

	/**
	 * 根据Date对象，生成日期字符串，yyyy-mm-dd
	 * @param date
	 * @return
	 */
	public static String formatDBDateString(Date date) {

		if (null == date)
			return null;
		return TimeUtil.formatDBDateString(date.getYear(), date.getMonth(),
				date.getDay());
	}

	/**
	 * 日期字符串格式转换
	 * @param date 原日期字符串
	 * @param separator 原日期字符串分隔符
	 * @return 转换后的日期字符串
	 */
	public static String formatDBDateStr(String date, String separator) {

		if (date == null)
			return null;
		if (null == separator)
			separator = defaultDateSeparator;
		String ret = null;
		try {
			String[] datearr = date.split(separator);
			if (datearr.length != 3)
				ret = null;
			else {
				int year = Integer.parseInt(datearr[0]);
				int month = Integer.parseInt(datearr[1]);
				int day = Integer.parseInt(datearr[2]);
				ret = TimeUtil.formatDBDateString(year, month, day);
			}
		} catch (Exception e) {
			ret = null;
		}

		return ret;
	}

	/**
	 * 根据年月日 生成字符串，yyyy-mm-dd
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static String formatDBDateString(Integer year, Integer month,
			Integer day) {

		if (year == null || month == null || day == null)
			return null;
		String ret = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DATE, day);
			// 如果日期不合法,执行下述语句,必定抛出异常.
			cal.get(Calendar.YEAR);
			ret = String.format("%04d", year) + defaultDateSeparator
					+ String.format("%02d", month) + defaultDateSeparator
					+ String.format("%02d", day);
		} catch (Exception e) {
			ret = null;
		}

		return ret;
	}

	/**
	 * 根据Date对象，生成时间字符串，hh:mm:ss
	 * @param date
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String formatTimeString(Date date) {

		if (null == date)
			return null;
		return TimeUtil.formatDBTimeString(date.getHours(), date.getMinutes(),
				date.getSeconds());
	}

	/**
	 * 时间字符串转换
	 * @param strTime 转换前的时间字符串
	 * @param separator 原时间字符串分隔符
	 * @return 转换后的时间字符串分隔符，hh:mm:ss
	 */
	public static String formatDBTimeString(String strTime, String separator) {

		if (null == strTime || strTime.isEmpty())
			return null;
		if (null == separator)
			separator = defaultTimeSeparator;
		String ret = null;
		try {
			String[] times = strTime.split(separator);
			if (3 != times.length)
				ret = null;
			else
				ret = TimeUtil.formatDBTimeString(Integer.parseInt(times[0]),
						Integer.parseInt(times[1]), Integer.parseInt(times[2]));
		} catch (Exception e) {
			ret = null;
		}
		return ret;
	}

	/**
	 * 根据参数，形成时间字符串，hh:mm:ss
	 * @param hour
	 * @param minute
	 * @param sec
	 * @return
	 */
	public static String formatDBTimeString(Integer hour, Integer minute,
			Integer sec) {

		if (hour == null || minute == null || sec == null)
			return null;
		String ret = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, sec);
			cal.get(Calendar.HOUR_OF_DAY);
			ret = String.format("%02d", hour) + defaultTimeSeparator
					+ String.format("%02d", minute) + defaultTimeSeparator
					+ String.format("%02d", sec);
		} catch (Exception e) {
			ret = null;
		}

		return ret;
	}

	public static Calendar getCurrentTime24Format() {

		Calendar ret = Calendar.getInstance();
		return ret;
	}

	/**
	 * 根据日期字符串，得到日历结构
	 * 
	 * @param date 日期字符串，yyyy-mm-dd
	 * @return 日期格式不正确时返回null
	 */
	public static Calendar fromDateStringToCalendar(String date) {

		if (date == null)
			return null;
		return TimeUtil.fromDateStringToCalendar(date, defaultDateSeparator);
	}

	/**
	 * 根据时间字符串，生成Calendar对象；如果time的格式错误或时间不正确，返回null
	 * @param time 时间字符串，hh:mm:ss
	 * @param seperator 时间字符串的分隔符
	 * @return Calendar对象，其中的时分秒由time生成
	 */
	public static Calendar fromTimeStringToCalendar(String time,
			String seperator) {

		if (time == null)
			return null;
		if (null == seperator || seperator.isEmpty())
			seperator = defaultTimeSeparator;
		Calendar ret = Calendar.getInstance();
		try {
			String[] timearr = time.split(seperator);
			if (timearr.length == 3) {
				ret.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timearr[0]));
				ret.set(Calendar.MINUTE, Integer.parseInt(timearr[1]));
				ret.set(Calendar.SECOND, Integer.parseInt(timearr[2]));
				ret.get(Calendar.HOUR_OF_DAY);
			}
		} catch (Exception e) {
			ret = null;
		}
		return ret;

	}

	/**
	 * 分离字符串，得到日历结构
	 * 
	 * @param date
	 *            字符串，yyyy-mm-dd
	 * @return 日期格式不正确时返回null
	 */
	public static Calendar fromDateStringToCalendar(String date,
			String separator) {

		if (date == null)
			return null;
		if (null == separator)
			separator = defaultDateSeparator;
		Calendar ret = null;
		try {
			String[] datearr = date.split(separator);
			if (datearr.length != 3)
				ret = null;
			else {
				int year = Integer.parseInt(datearr[0]);
				int month = Integer.parseInt(datearr[1]) - 1;
				int day = Integer.parseInt(datearr[2]);
				ret = GregorianCalendar.getInstance();
				ret.setLenient(false);
				ret.set(Calendar.YEAR, year);
				ret.set(Calendar.MONTH, month);
				ret.set(Calendar.DATE, day);
				// 如果日期不合法,执行下述语句,必定抛出异常.
				ret.get(Calendar.YEAR);
			}
		} catch (Exception e) {
			ret = null;
		}

		return ret;
	}

}
