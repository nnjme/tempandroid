package com.changlianxi.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;

import com.changlianxi.modle.Info;
import com.changlianxi.modle.MemberModle;
import com.changlianxi.util.CrashHandler;
import com.changlianxi.util.Logger;
import com.changlianxi.util.Logger.Level;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class CLXApplication extends Application {
	private static CLXApplication instance;
	private static List<MemberModle> modle;
	private NotificationManager mNotificationManager;
	private MediaPlayer mMediaPlayer;
	private static DisplayImageOptions options;
	private static ImageLoader imageLoader;
	private static List<Activity> activityList = new ArrayList<Activity>();
	private static List<Activity> smsInviteAactivityList = new ArrayList<Activity>();
	public static List<Info> basicList = new ArrayList<Info>();// 存放基本信息数据
	public static List<Info> contactList = new ArrayList<Info>();// 存放联系方式数据
	public static List<Info> socialList = new ArrayList<Info>();// 存放社交账号数据
	public static List<Info> addressList = new ArrayList<Info>();// 存放地址数据
	public static List<Info> eduList = new ArrayList<Info>();// 存放教育经历
	public static List<Info> workList = new ArrayList<Info>();// 存放工作经历
	public static String name;
	public static String pid;
	public static String cid;
	public static String avatar;

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
	public static void exit() {
		for (int i = 0; i < activityList.size(); i++) {
			Activity activity = activityList.get(i);
			if (activity != null) {
				activity.finish();
			}
		}
		// System.exit(0);
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
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.pic)
				.showImageForEmptyUri(R.drawable.pic)
				.showImageOnFail(R.drawable.pic).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.ARGB_8888)
				.build();
		imageLoader = ImageLoader.getInstance();
	}

	public static ImageLoader getImageLoader() {
		return imageLoader;
	}

	public static DisplayImageOptions getOptions() {
		return options;
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

	public static void setEditData(List<Info> bList, List<Info> cList,
			List<Info> sList, List<Info> addList, List<Info> eList,
			List<Info> wList, String nameStr, String cidStr, String pidStr,
			String avatarStr) {
		basicList = bList;
		contactList = cList;
		socialList = sList;
		addressList = addList;
		eduList = eList;
		workList = wList;
		name = nameStr;
		cid = cidStr;
		pid = pidStr;
		avatar = avatarStr;
	}

	public static void clearData() {
		basicList = null;
		contactList = null;
		socialList = null;
		addressList = null;
		eduList = null;
		workList = null;
		name = null;
		cid = null;
		pid = null;
		avatar = null;
	}
}
