package com.changlianxi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.changlianxi.activity.CLXApplication;

/**
 * * SharedPreferences 的公具类
 * 
 * @author teeker_bin
 * 
 */
public class SharedUtils {
	private static SharedPreferences sharedPreferences = CLXApplication
			.getInstance().getSharedPreferences("clx", Context.MODE_PRIVATE);
	private static Editor editor = sharedPreferences.edit();

	public static String getString(String key, String defaultValue) {
		return sharedPreferences.getString(key, defaultValue);
	}

	public static int getInt(String key, int defaultValue) {
		return sharedPreferences.getInt(key, defaultValue);
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		return sharedPreferences.getBoolean(key, defaultValue);
	}

	public static void setString(String key, String value) {
		editor.putString(key, value);
		editor.commit();

	}

	public static void setInt(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public static void setBoolean(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static void setChannelID(String value) {
		editor.putString("channel_id", value);
		editor.commit();

	}

	public static void setUserID(String value) {
		editor.putString("user_id", value);
		editor.commit();

	}

	public static String getChannelID() {
		return sharedPreferences.getString("channel_id", "");
	}

	public static String getUserID() {
		return sharedPreferences.getString("user_id", "");
	}
	
	public static void setCircleListLastRefreshTime(long time){
		editor.putLong("CircleListLastRefreshTime", time);
		editor.commit();
	}
	public static long getCircleListLastRefreshTime(){
		return sharedPreferences.getLong("CircleListLastRefreshTime", 0);
	}
}
