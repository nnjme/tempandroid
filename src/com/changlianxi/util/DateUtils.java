package com.changlianxi.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

/**
 * 
 */
public class DateUtils {

	private static final Map<String, DateFormat> DFS = new HashMap<String, DateFormat>();

	private DateUtils() {
	}

	/**
	 * 获取当前时间字符串，格式为："yyyy-MM-dd HH:mm:ss"
	 * 
	 * @return
	 */
	public static String getCurrDateStr() {
		return getCurrDateStr(null);
	}

	/**
	 * 返回指定格式的当前时间字符串，如果format为空或者为null则使用默认格式："yyyy-MM-dd HH:mm:ss"
	 * 
	 * @Throws 如果传入的格式不支持则抛出 IllegalArgumentException 异常
	 * @param format
	 * @return
	 */
	public static String getCurrDateStr(String format) {
		if (TextUtils.isEmpty(format)) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		return format(new Date(), format);
	}

	public static DateFormat getFormat(String pattern) {
		DateFormat format = DFS.get(pattern);
		if (format == null) {
			format = new SimpleDateFormat(pattern);
			DFS.put(pattern, format);
		}
		return format;
	}

	public static String format(Date date, String pattern) {
		if (date == null) {
			return null;
		}
		return getFormat(pattern).format(date);
	}

	private static Calendar convert(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * 按一定格式截取字符串日期
	 * 
	 * @param str
	 * @return
	 */
	public static String interceptDateStr(String str, String format) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat df1 = new SimpleDateFormat(format);
		String strdate = "";
		try {
			Date date = df.parse(str);
			strdate = df1.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return strdate;

	}

	/**
	 * 返回指定日期相应位移后的日期
	 * 
	 * @param date
	 *            参考日期
	 * @param field
	 *            位移单位，见 {@link Calendar}
	 * @param offset
	 *            位移数量，正数表示之后的时间，负数表示之前的时间
	 * @return 位移后的日期
	 */
	public static Date offsetDate(Date date, int field, int offset) {
		Calendar calendar = convert(date);
		calendar.add(field, offset);
		return calendar.getTime();
	}

}