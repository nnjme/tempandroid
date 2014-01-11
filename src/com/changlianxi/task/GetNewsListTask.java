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
import com.changlianxi.inteface.GetNewsList;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.modle.NewsModle;
import com.changlianxi.util.HttpUrlHelper;

/**
 * 获取动态列表
 * 
 * @author teeker_bin
 * 
 */
public class GetNewsListTask extends AsyncTask<String, Integer, String> {
	private GetNewsList callBack;
	private Map<String, Object> map;
	private String url;
	private List<NewsModle> listModle = new ArrayList<NewsModle>();
	private String cid = "";

	public GetNewsListTask(Context context, Map<String, Object> map,
			String url, String cid) {
		this.map = map;
		this.url = url;
		this.cid = cid;
	}

	public void setTaskCallBack(GetNewsList callBack) {
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
			String usercid = jsonobject.getString("cid");
			JSONArray jsonarray = jsonobject.getJSONArray("news");
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject object = (JSONObject) jsonarray.opt(i);
				NewsModle modle = new NewsModle();
				String id = object.getString("id");
				String type = object.getString("type");
				String user1 = object.getString("user1");
				String user2 = object.getString("user2");
				String person2 = object.getString("person2");
				String createdTime = object.getString("created");
				String content = object.getString("content");
				String detail = object.getString("detail");
				String need_approve = object.getString("need_approve");
				String user1Name = "";
				String user2Name = "";
				String avatarURL = "";
				MemberInfoModle infomodle = DBUtils.findMemberInfo(user1,
						person2, user2, usercid);
				user1Name = infomodle.getName();
				avatarURL = infomodle.getAvator();
				modle.setUser1Name(user1Name);
				modle.setAvatarUrl(avatarURL);
				if (!user2.equals("0") || !person2.equals("0")) {
					MemberInfoModle user2modle = DBUtils.findMemberInfo(user2,
							person2, user2, usercid);
					user2Name = user2modle.getName();
					modle.setUser2Name(user2Name);
				}
				modle.setNeed_approve(need_approve);
				modle.setCid(cid);
				modle.setContent(content);
				modle.setCreatedTime(createdTime);
				modle.setDetail(detail);
				modle.setId(id);
				modle.setPerson2(person2);
				modle.setType(type);
				modle.setUser1(user1);
				modle.setUser2(user2);
				listModle.add(modle);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		// 任务结束
		callBack.getNewsList(listModle);
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}

}
