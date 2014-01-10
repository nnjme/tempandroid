package com.changlianxi.task;

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;

import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;

/**
 * 客户端用户 登录后，用来设置用户信息的接口，支持记录用户的登录设备、客户端版本、客户端推送id、登录时间等
 * 
 * @author teeker_bin
 * 
 */
public class SetClientInfo extends AsyncTask<String, Integer, String> {
	private ClientCallBack afterCallBack;

	public SetClientInfo(ClientCallBack afterLoginCallBack) {
		this.afterCallBack = afterLoginCallBack;
	}

	@Override
	protected String doInBackground(String... params) {
		if (isCancelled()) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("device", Utils.getModelAndRelease());
		map.put("token", SharedUtils.getString("token", ""));
		map.put("version", "1.0");
		map.put("os", Utils.getOS());
		map.put("channel_id", SharedUtils.getChannelID());
		map.put("user_id", SharedUtils.getUserID());
		String result = HttpUrlHelper.postData(map, "/users/iclient");
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		afterCallBack.afterLogin(result);
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}

	public interface ClientCallBack {
		void afterLogin(String result);
	}
}
