package com.changlianxi.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.modle.MydetailChangeModle;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.SharedUtils;

/**
 * 获取动态列表
 * 
 * @author teeker_bin
 * 
 */
public class MyDetailChangeTask extends AsyncTask<String, Integer, String> {
	private GetChangedDetail callBack;
	private List<MydetailChangeModle> listModle = new ArrayList<MydetailChangeModle>();

	public void setTaskCallBack(GetChangedDetail callBack) {
		this.callBack = callBack;
	}

	@Override
	protected String doInBackground(String... params) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		String result = HttpUrlHelper.postData(map, "/people/imyAmendments");
		try {
			JSONObject jsonobject = new JSONObject(result);
			JSONArray jsonarray = jsonobject.getJSONArray("amendments");
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject object = (JSONObject) jsonarray.opt(i);
				MydetailChangeModle modle = new MydetailChangeModle();
				String id = object.getString("id");
				String cid = object.getString("cid");
				String uid = object.getString("uid");
				String content = object.getString("content");
				String time = object.getString("time");
				MemberInfoModle info = DBUtils.selectNameAndImgByID("circle"
						+ cid, uid);
				if (info != null) {
					modle.setName(info.getName());
					modle.setAvatarURL(info.getAvator());
				}
				modle.setCid(cid);
				modle.setUid(uid);
				modle.setContent(content);
				modle.setTime(time);
				modle.setId(id);
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
		callBack.getChangeDeatils(listModle);
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}

	public interface GetChangedDetail {
		void getChangeDeatils(List<MydetailChangeModle> listModle);
	}
}
