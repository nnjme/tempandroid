package com.changlianxi.task;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.SharedUtils;

/**
 * 获取个人信息接口
 * 
 * @author teeker_bin
 */
public class GetMyNotifyTask extends AsyncTask<String, Integer, String> {
	private GetMyNotify callBack;
	private int newCircleCount;// 新圈子数量
	private int newMessageCount;// 新私信数量
	private int namendments;// 资料修改次数
	private String startTime;

	public void setTaskCallBack(GetMyNotify callBack) {
		this.callBack = callBack;
	}

	public GetMyNotifyTask(String startTime) {
		this.startTime = startTime;
	}

	// 可变长的输入参数，与AsyncTask.exucute()对应
	@Override
	protected String doInBackground(String... params) {
		if (isCancelled()) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("start", startTime);
		String json = HttpUrlHelper.postData(map, "/users/imyNotify");
		try {
			JSONObject jsonobject = new JSONObject(json);
			if (!jsonobject.getString("rt").equals("1")) {
				return "";
			}
			newCircleCount = jsonobject.getInt("circles");
			newMessageCount = jsonobject.getInt("messages");
			namendments = jsonobject.getInt("amendments");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "1";// 正常访问
	}

	@Override
	protected void onPostExecute(String result) {
		callBack.getMyNotify(newCircleCount > 0 ? true : false,
				newMessageCount > 0 ? true : false, namendments > 0 ? true
						: false);
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}

	public interface GetMyNotify {
		void getMyNotify(boolean newCircle, boolean newMessge,
				boolean namendments);

	}

}
