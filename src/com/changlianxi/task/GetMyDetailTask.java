package com.changlianxi.task;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.changlianxi.db.DBUtils;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.UserInfoUtils;

/**
 * 获取个人信息接口
 * 
 * @author teeker_bin
 */
public class GetMyDetailTask extends AsyncTask<String, Integer, String> {
	private GetMyDetail callBack;
	private String avatarUrl = "";

	public void setTaskCallBack(GetMyDetail callBack) {
		this.callBack = callBack;
	}

	// 可变长的输入参数，与AsyncTask.exucute()对应
	@Override
	protected String doInBackground(String... params) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		String json = HttpUrlHelper.postData(map, "/people/imyDetail");
		try {
			JSONObject jsonobject = new JSONObject(json);
			if (!jsonobject.getString("rt").equals("1")) {
				return "";
			}
			DBUtils.clearTableData("mydetail");// 清空本地表 保存最新数据
			String pid = jsonobject.getString("pid");
			JSONArray jsonarray = jsonobject.getJSONArray("person");
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject object = (JSONObject) jsonarray.opt(i);
				String start = "";
				String end = "";
				String id = object.getString("id");// 属性id
				String key = object.getString("type");// 属性名称
				String value = object.getString("value");// 属性值
				if (key.equals("D_AVATAR")) {
					avatarUrl = value;
				}
				if (Arrays.toString(UserInfoUtils.eduStr).contains(key)) {
					start = object.getString("start");//
					end = object.getString("end");//
				}
				insertData(id, pid, key, value, start, end);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "1";// 正常访问
	}

	@Override
	protected void onPostExecute(String result) {
		// 任务结束
		callBack.getMydetail(avatarUrl);

	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}

	public interface GetMyDetail {
		void getMydetail(String avatarUrl);
	}

	/**
	 * 存入本地数据库
	 * 
	 * @param tid
	 * @param pid
	 * @param key
	 * @param value
	 * @param start
	 * @param end
	 */
	private void insertData(String tid, String pid, String key, String value,
			String start, String end) {
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("tid", tid);
		values.put("pid", pid);
		values.put("key", key);
		values.put("value", value);
		values.put("startDate", start);
		values.put("endDate", end);
		DBUtils.insertData("mydetail", values);
	}
}
