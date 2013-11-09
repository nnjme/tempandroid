package com.changlianxi.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.GetMessagesCallBack;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.modle.MessageModle;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Utils;

/**
 * 获取私信内容 线程
 * 
 * @author teeker_bin
 * 
 */
public class GetMessagesTask extends AsyncTask<String, Integer, String> {
	private GetMessagesCallBack callBack;
	private Map<String, Object> map;
	private String url;
	private List<MessageModle> listModle = new ArrayList<MessageModle>();

	public GetMessagesTask(Context context, Map<String, Object> map, String url) {
		this.map = map;
		this.url = url;
	}

	public void setTaskCallBack(GetMessagesCallBack callBack) {
		this.callBack = callBack;
	}

	@Override
	protected String doInBackground(String... params) {
		String result = HttpUrlHelper.postData(map, url);
		try {
			JSONObject jsonobject = new JSONObject(result);
			String rt = jsonobject.getString("rt");
			if (!rt.equals("1")) {
				return null;
			}
			JSONArray jsonarray = jsonobject.getJSONArray("messages");
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject object = (JSONObject) jsonarray.opt(i);
				MessageModle modle = new MessageModle();
				String name = "";
				String avatarPath = "";
				String uid = object.getString("uid");
				String content = object.getString("content");
				String time = object.getString("time");
				String cid = object.getString("cid");
				MemberInfoModle info = DBUtils.selectNameAndImgByID("circle"
						+ cid, uid);
				if (info != null) {
					avatarPath = info.getAvator();
					name = info.getName();
				}
				modle.setAvatar(avatarPath);
				modle.setName(name);
				modle.setContent(content);
				modle.setTime(time);
				modle.setSelf(false);
				listModle.add(modle);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		// 任务结束
		if (result != null) {
			callBack.getMessages(listModle);
			return;
		}
		callBack.getMessages(null);
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}
}
