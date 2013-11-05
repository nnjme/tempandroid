package com.changlianxi.util;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import android.os.Environment;

public class FileUtils {

	/**
	 * 根据文件绝对路径获取文件名称
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		if (StringUtils.isBlank(filePath))
			return "";
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
	}

	/**
	 * 获取根目录
	 */
	public static String getRootDir() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	/**
	 * 使用当前时间戳拼接一个唯�?��文件�?
	 * 
	 * @param format
	 * @return
	 */
	public static String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SS");
		String fileName = format.format(new Timestamp(System
				.currentTimeMillis()));
		return fileName;
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileFormat(String fileName) {
		if (StringUtils.isBlank(fileName))
			return "";

		int point = fileName.lastIndexOf('.');
		return fileName.substring(point + 1);
	}

}