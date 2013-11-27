package com.changlianxi.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.modle.MessageModle;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.SharedUtils;

/**
 * 获取某个圈子的聊天内容列表 *
 * 
 * @author teeker_bin
 */
public class GetChatListTask extends AsyncTask<String, Integer, String> {
	private GetChatsList callBack;
	private List<MessageModle> listModle = new ArrayList<MessageModle>();
	private String cid;
	private String start;
	private String end;

	public GetChatListTask(String cid, String start, String end) {
		this.cid = cid;
		this.start = start;
		this.end = end;
	}

	public void setTaskCallBack(GetChatsList callBack) {
		this.callBack = callBack;
	}

	@Override
	protected String doInBackground(String... params) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("cid", cid);
		map.put("start", DateUtils.phpTime(DateUtils.convertToDate(start)));
		map.put("end", end);
		String result = HttpUrlHelper.postData(map, "/chats/ilist");
		try {
			JSONObject jsonobject = new JSONObject(result);
			if (!jsonobject.getString("rt").equals("1")) {
				return null;
			}
			JSONArray jsonarray = jsonobject.getJSONArray("chats");
			for (int i = jsonarray.length() - 1; i > 0; i--) {
				JSONObject object = (JSONObject) jsonarray.opt(i);
				MessageModle modle = new MessageModle();
				String name = "";
				String avatarPath = "";
				String id = object.getString("id");
				String type = object.getString("type");
				String senderid = object.getString("sender");// 发言用户id
				MemberInfoModle info = DBUtils.selectNameAndImgByID("circle"
						+ cid, senderid);
				if (info != null) {
					avatarPath = info.getAvator();
					name = info.getName();
				}
				String content = object.getString("content");
				String time = object.getString("time");
				if (senderid.equals(SharedUtils.getString("uid", ""))) {
					modle.setSelf(true);
				} else {
					modle.setSelf(false);
				}
				modle.setContent(content);
				modle.setTime(time);
				modle.setAvatar(avatarPath);
				modle.setName(name);
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
		callBack.getChatsList(listModle);
		return;

	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}

	public interface GetChatsList {
		void getChatsList(List<MessageModle> listModle);
	}
}
