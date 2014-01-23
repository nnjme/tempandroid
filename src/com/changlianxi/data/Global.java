package com.changlianxi.data;

import com.changlianxi.util.SharedUtils;

public class Global {

	public static String getUid() {
		return SharedUtils.getString("uid", "");
	}

	public static String getUserToken() {
		return SharedUtils.getString("token", "");
	}
}
