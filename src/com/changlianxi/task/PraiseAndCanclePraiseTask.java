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
 * 赞和取消赞
 * 
 * @author teeker_bin
 * 
 */
public class PraiseAndCanclePraiseTask extends
		AsyncTask<String, Integer, String> {
	private String cid;
	private String gid;
	private String type;
	private String url;
	private String err;
	private int count;
	private PraiseAndCancle callBack;

	public PraiseAndCanclePraiseTask(String cid, String gid, String type,
			String url) {
		this.url = url;
		this.cid = cid;
		this.gid = gid;
		this.type = type;
	}

	public void setPraiseCallBack(PraiseAndCancle callback) {
		this.callBack = callback;
	}

	// 可变长的输入参数，与AsyncTask.exucute()对应
	@Override
	protected String doInBackground(String... params) {
		String rt = "";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cid", cid);
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("gid", gid);
		map.put("token", SharedUtils.getString("token", ""));
		String result = HttpUrlHelper.postData(map, url);
		try {
			JSONObject jsonobject = new JSONObject(result);
			rt = jsonobject.getString("rt");
			if (rt.equals("1")) {
				count = jsonobject.getInt("count");
			} else {
				err = jsonobject.getString("err");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rt;
	}

	@Override
	protected void onPostExecute(String result) {
		if (!result.equals("1")) {
			Utils.showToast(ErrorCodeUtil.convertToChines(err));
		}
		callBack.praiseAndCancle(type, count);
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理
	}

	public interface PraiseAndCancle {
		void praiseAndCancle(String type, int count);
	}
}
