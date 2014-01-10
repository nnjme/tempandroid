package com.changlianxi.task;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;

/**
 * 
 * 获取圈子中的数据更新情况，主要包括：新加入成员、新成长、新聊天、新动态、新评论（跟用户相关）等。
 * 
 * @author teeker_bin
 */
public class GetCieclesNotifyTask extends AsyncTask<String, Integer, String> {
	private GetCirclesNotify callBack;
	private String errorCode;
	private String rt = "";
	private String startTime = "";
	private String cids = "";// 以逗号分割的圈子ID列表
	private String resultArray = "";

	public GetCieclesNotifyTask(String time, String cids) {
		this.startTime = time;
		this.cids = cids;
	}

	public void setTaskCallBack(GetCirclesNotify callBack) {
		this.callBack = callBack;
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
		map.put("cids", cids);
		String result = HttpUrlHelper.postData(map, "/circles/inotify");
		// 你要执行的方法
		try {
			JSONObject jsonobject = new JSONObject(result);
			rt = jsonobject.getString("rt");
			if (!rt.equals("1")) {
				errorCode = jsonobject.getString("err");
				return null;
			}
			resultArray = jsonobject.getString("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return resultArray;
	}

	@Override
	protected void onPostExecute(String result) {
		// 任务结束
		if (result == null) {
			Utils.showToast(ErrorCodeUtil.convertToChines(errorCode));
		}
		callBack.getCirclesNotify(result);
		return;
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}

	public interface GetCirclesNotify {
		void getCirclesNotify(String result);
	}
}
