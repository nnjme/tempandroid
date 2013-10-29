package com.changlianxi.activity;

import android.app.Application;

import com.changlianxi.util.CrashHandler;
import com.changlianxi.util.Logger;
import com.changlianxi.util.Logger.Level;

public class CLXApplication extends Application {
	private static CLXApplication instance;

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
		CrashHandler catchHandler = CrashHandler.getInstance();
		catchHandler.init(this);
		super.onCreate();
	}
}
