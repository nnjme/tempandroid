package com.changlianxi.util;

import java.io.File;

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