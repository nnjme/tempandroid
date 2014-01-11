package com.changlianxi.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.activity.LoginActivity;

/**
 * 解析推送过来的消息
 * 
 * @author teeker_bin
 * 
 */
public class ResolutionPushJson {
	public static final String COMMENT_TYPE = "GROWTH_COMMENT";
	public static final String NEW_TYPE = "NEW_NEWS";
	public static final String GROWTH_TYPE = "NEW_GROWTH";
	public static final String CHAT_TYPE = "CHAT";
	public static final String QUIT_TYPE = "FORCE_QUIT";

	public static void resolutionJson(String jsonStr) {
		try {
			JSONObject json = new JSONObject(jsonStr);
			String type = json.getString("t");
			if (type.equals(QUIT_TYPE)) {
				finish();
				return;
			}
			String cid = json.getString("cid");
			if (type.equals(COMMENT_TYPE)) {
				sendBroad(cid, 1, COMMENT_TYPE);
			} else if (type.equals(GROWTH_TYPE)) {
				sendBroad(cid, 1, GROWTH_TYPE);
			} else if (type.equals(NEW_TYPE)) {
				sendBroad(cid, 1, NEW_TYPE);
			} else if (type.equals(CHAT_TYPE)) {
				sendBroad(cid, 1, CHAT_TYPE);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static void finish() {
		SharedUtils.setString("uid", "");
		SharedUtils.setString("token", "");
		CLXApplication.exit(false);
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(CLXApplication.getInstance(), LoginActivity.class);
		CLXApplication.getInstance().startActivity(intent);
	}

	private static void sendBroad(String cid, int promptCount, String type) {
		Intent intent = new Intent();
		intent.setAction(Constants.PUSH_TYPE);
		intent.putExtra("promptCount", promptCount);
		intent.putExtra("type", type);
		intent.putExtra("cid", cid);
		BroadCast.sendBroadCast(CLXApplication.getInstance(), intent);

	}
}
