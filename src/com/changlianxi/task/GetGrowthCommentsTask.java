package com.changlianxi.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.changlianxi.modle.CommentsModle;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.SharedUtils;

/**
 * 获取成长记录评论列表
 * 
 * @author teeker_bin
 */
public class GetGrowthCommentsTask extends AsyncTask<String, Integer, String> {
	private GetGrowthComments callBack;
	private List<CommentsModle> listModle = new ArrayList<CommentsModle>();
	private String cid = "";
	private String gid = "";

	public GetGrowthCommentsTask(String cid, String gid) {
		this.cid = cid;
		this.gid = gid;
	}

	public void setTaskCallBack(GetGrowthComments callBack) {
		this.callBack = callBack;
	}

	@Override
	protected String doInBackground(String... params) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cid", cid);
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("gid", gid);
		String result = HttpUrlHelper.postData(map, "/growth/icomments");
		try {
			JSONObject jsonobject = new JSONObject(result);
			String cid = jsonobject.getString("cid");
			String gid = jsonobject.getString("gid");
			String num = jsonobject.getString("num");
			JSONArray jsonarray = jsonobject.getJSONArray("comments");
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject object = (JSONObject) jsonarray.opt(i);
				CommentsModle modle = new CommentsModle();
				String id = object.getString("id");
				String uid = object.getString("uid");
				String content = object.getString("content");
				String replyid = object.getString("replyid");
				String time = object.getString("time");
				modle.setCid(cid);
				modle.setContent(content);
				modle.setGid(gid);
				modle.setId(id);
				modle.setNum(num);
				modle.setReplyid(replyid);
				modle.setUid(uid);
				modle.setTime(time);
				listModle.add(modle);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		// 任务结束
		callBack.getGrowthComments(listModle);
		return;

	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}

	public interface GetGrowthComments {
		void getGrowthComments(List<CommentsModle> listModle);
	}
}
