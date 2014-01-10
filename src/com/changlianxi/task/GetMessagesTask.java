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
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.SharedUtils;
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
	private String err;
	private String ruid;// 保存时使用

	public GetMessagesTask(Context context, Map<String, Object> map,
			String url, String ruid) {
		this.map = map;
		this.url = url;
		this.ruid = ruid;
	}

	public void setTaskCallBack(GetMessagesCallBack callBack) {
		this.callBack = callBack;
	}

	@Override
	protected String doInBackground(String... params) {
		if (isCancelled()) {
			return null;
		}
		String result = HttpUrlHelper.postData(map, url);
		try {
			JSONObject jsonobject = new JSONObject(result);
			String rt = jsonobject.getString("rt");
			if (!rt.equals("1")) {
				err = jsonobject.getString("err");
				return null;
			}
			JSONArray jsonarray = jsonobject.getJSONArray("messages");
			for (int i = jsonarray.length() - 1; i >= 0; i--) {
				JSONObject object = (JSONObject) jsonarray.opt(i);
				MessageModle modle = new MessageModle();
				String name = "";
				String avatarPath = "";
				String uid = object.getString("uid");
				String content = object.getString("content");
				String time = object.getString("time");
				String cid = object.getString("cid");
				String type = object.getString("type");
				MemberInfoModle info = DBUtils.selectNameAndImgByID(uid);
				if (info != null) {
					avatarPath = info.getAvator();
					name = info.getName();
				}
				if (uid.equals(SharedUtils.getString("uid", ""))) {
					modle.setSelf(true);
				} else {
					modle.setSelf(false);
				}
				if (type.equals("TYPE_TEXT")) {
					modle.setType(0);
				} else if (type.equals("TYPE_IMAGE")) {
					modle.setType(1);
				}
				modle.setUid(uid);
				modle.setAvatar(avatarPath);
				modle.setName(name);
				modle.setContent(content);
				modle.setTime(time);
				listModle.add(modle);
				DBUtils.saveMessage(modle, ruid);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		// 任务结束
		if (result == null) {
			Utils.showToast(ErrorCodeUtil.convertToChines(err));
		}
		callBack.getMessages(listModle);

	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}
}
