package com.changlianxi.util;

import android.text.TextUtils;

public class StringUtils {
	/**
	 * 判断给定字符串是否空白串�?br> 空白串是指由空格、制表符、回车符、换行符组成的字符串<br>
	 * 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isBlank(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 字符串拼接
	 * 
	 * @param str
	 * @return
	 */
	public static String JoinString(String str, String joinStr) {
		if (str.equals("") || str == null) {
			return "";
		}
		int point = str.lastIndexOf('.');
		return str.substring(0, point) + joinStr + str.substring(point);
	}

	/**
	 * 返回str中最后一个separator子串后面的字符串 当str == null || str == "" || separator == ""
	 * 时返回str； 当separator==null || 在str中不存在子串separator 时返回 ""
	 * 
	 * @param str
	 *            源串
	 * @param separator
	 *            子串
	 * @return
	 */
	public static String substringAfterLast(String str, String separator) {
		if (TextUtils.isEmpty(str) || "".equals(separator)) {
			return str;
		}

		if (separator == null) {
			return "";
		}
		int idx = str.lastIndexOf(separator);
		if (idx < 0) {
			return str;
		}

		return str.substring(idx + separator.length());
	}

	/**
	 * 去除字符串头部字符 比如 +86
	 * 
	 * @param srcStr
	 * @param head
	 * @return
	 */
	public static String cutHead(String srcStr, String head) {
		if (TextUtils.isEmpty(srcStr))
			return srcStr;
		if (srcStr.startsWith(head))
			return substringAfter(srcStr, head);
		return srcStr;
	}

	/**
	 * 返回str中separator子串后面的字符串 当str == null || str == "" || separator == ""
	 * 时返回str； 当separator==null || 在str中不存在子串separator 时返回 ""
	 * 
	 * @param str
	 *            源串
	 * @param separator
	 *            子串
	 * @return
	 */
	public static String substringAfter(String str, String separator) {
		if (TextUtils.isEmpty(str) || "".equals(separator)) {
			return str;
		}

		if (separator == null) {
			return "";
		}
		int idx = str.indexOf(separator);
		if (idx < 0) {
			return "";
		}

		return str.substring(idx + separator.length());
	}

	/***
	 * 半角转换为全角
	 * 
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	/**
	 * 倒叙输出一个字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String reverseSort(String str) {
		String str2 = "";
		for (int i = str.length() - 1; i > -1; i--) {
			str2 += String.valueOf(str.charAt(i));
		}

		return str2;
	}

	/**
	 * 表情删除时使用 获取标签"："的位置
	 * 
	 * @param str
	 * @return
	 */
	public static int getPositionEmoj(String str) {
		String[] arr = new String[str.length()];
		for (int i = str.length() - 2; i >= 0; i--) {
			arr[i] = str.substring(i, (i + 1));
			if (":".equals(arr[i])) {
				return i;
			}
		}
		return 0;
	}
}