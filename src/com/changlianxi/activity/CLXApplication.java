package com.changlianxi.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.app.NotificationManager;
import android.media.MediaPlayer;

import com.changlianxi.modle.MemberModle;
import com.changlianxi.modle.MessageModle;
import com.changlianxi.util.Logger;
import com.changlianxi.util.Logger.Level;
import com.changlianxi.util.Utils;

public class CLXApplication extends Application {
	private static CLXApplication instance;
	private static MemberModle modle;
	private static List<MessageModle> listModle = new ArrayList<MessageModle>();
	private NotificationManager mNotificationManager;
	private MediaPlayer mMediaPlayer;

	public static CLXApplication getInstance() {
		return instance;
	}

	public void setInstance(CLXApplication instance) {
		this.instance = instance;
	}

	@Override
	public void onCreate() {
		setInstance(this);
		Logger.setWriteFile(false); // 设置日志是写文件还是使用标准输出
		Logger.setLogLevel(Level.DEBUG); // 日志级别
		// CrashHandler catchHandler = CrashHandler.getInstance();
		// catchHandler.init(this);
		initData();
		super.onCreate();
	}

	private void initData() {
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		mMediaPlayer = MediaPlayer.create(this, R.raw.office);
	}

	public synchronized MediaPlayer getMediaPlayer() {
		if (mMediaPlayer == null)
			mMediaPlayer = MediaPlayer.create(this, R.raw.office);
		return mMediaPlayer;
	}

	public NotificationManager getNotificationManager() {
		if (mNotificationManager == null)
			mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		return mNotificationManager;
	}

	/**
	 * 当不在聊天界面时保存聊天信息
	 * 
	 * @param content
	 */
	public static void saveChatModle(String content) {
		MessageModle modle = Utils.getChatModle(content);
		listModle.add(modle);
	}

	/**
	 * 获取聊天信息
	 * 
	 * @return
	 */
	public static List<MessageModle> getChatModle() {
		return listModle;
	}

	/**
	 * 清除聊天信息
	 */
	public static void removeChatModle() {
		listModle.clear();
	}

	public static MemberModle getModle() {
		return modle;
	}

	public static void setModle(MemberModle modle) {
		CLXApplication.modle = modle;
	}

	public static void setModleNull() {
		modle = null;
	}
}
