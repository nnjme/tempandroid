package com.changlianxi.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;

import com.changlianxi.BuildConfig;
import com.changlianxi.R;
import com.changlianxi.util.Logger;
import com.changlianxi.util.Logger.Level;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class CLXApplication extends Application {
	private static CLXApplication instance;
	private NotificationManager mNotificationManager;
	private MediaPlayer mMediaPlayer;
	private static DisplayImageOptions circlOoptions;
	private static DisplayImageOptions userOptions;
	private static ImageLoader imageLoader;
	private static List<Activity> activityList = new ArrayList<Activity>();
	private static List<Activity> smsInviteAactivityList = new ArrayList<Activity>();
	public static long circleListLastRefreshTime;//圈子列表的上次请求时间
	public static long circleMemberLastRefreshTime;//圈子成员上次请求时间
	public static long circleMemberListLastRefreshTime;//圈子成员列表上次请求时间
	public static long GrowthListLastRefreshTime;//圈子成员列表上次请求时间

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
		initImageLoader();
		super.onCreate();
	}
	

	// 添加Activity到容器中
	public static void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// 添加創建圈子Activity到容器中
	public static void addInviteActivity(Activity activity) {
		smsInviteAactivityList.add(activity);
	}

	// 删除对应的Activity
	public static void removeActivity(Activity activity) {
		activityList.remove(activity);
	}

	// 遍历所有Activity并finish
	public static void exit(boolean flag) {
		for (int i = 0; i < activityList.size(); i++) {
			Activity activity = activityList.get(i);
			if (activity != null) {
				activity.finish();
			}
		}
		if (flag) {
			System.exit(0);
		}
	}

	// 遍历创建圈子activity并finish
	public static void exitSmsInvite() {
		for (int i = 0; i < smsInviteAactivityList.size(); i++) {
			Activity activity = smsInviteAactivityList.get(i);
			if (activity != null) {
				activity.finish();
			}
		}
	}

	private void initImageLoader() {
		// 初始化图片缓存
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				this).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO);
		if (BuildConfig.DEBUG) {
			builder.writeDebugLogs();
		}
		ImageLoader.getInstance().init(builder.build());
		circlOoptions = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.pic)
				.showImageForEmptyUri(R.drawable.pic)
				.showImageOnFail(R.drawable.pic).cacheInMemory(true).cacheOnDisc(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.ARGB_8888)
				.build();
		userOptions = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.head_bg)
				.showImageForEmptyUri(R.drawable.head_bg).cacheOnDisc(true)
				.showImageOnFail(R.drawable.head_bg).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.ARGB_8888)
				.build();
		imageLoader = ImageLoader.getInstance();
	}

	public static ImageLoader getImageLoader() {
		return imageLoader;
	}

	public static DisplayImageOptions getOptions() {
		return circlOoptions;
	}

	public static DisplayImageOptions getUserOptions() {
		return userOptions;
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

}
