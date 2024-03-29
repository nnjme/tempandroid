package com.changlianxi.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.CircleModle;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;

/**
 * 获取圈子列表
 * 
 * @author teeker_bin
 */
public class GetCircleListTask extends AsyncTask<String, Integer, String> {
	private GetCircleList callBack;
	private String errorCode;
	private List<CircleModle> serverListModle = new ArrayList<CircleModle>();
	private String rt = "";

	public void setTaskCallBack(GetCircleList callBack) {
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
		map.put("timestamp", 0);
		String result = HttpUrlHelper.postData(map, "/circles/ilist/"
				+ SharedUtils.getString("uid", ""));
		// 你要执行的方法
		try {
			JSONObject jsonobject = new JSONObject(result);
			rt = jsonobject.getString("rt");
			if (!rt.equals("1")) {
				errorCode = jsonobject.getString("err");
				return null;
			}
			JSONArray jsonarray = jsonobject.getJSONArray("circles");
			if (jsonarray != null) {
				DBUtils.clearTableData("circlelist");// 清空本地表 保存最新数据
			}
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject object = (JSONObject) jsonarray.opt(i);
				CircleModle modle = new CircleModle();
				String id = object.getString("id");
				String logo = object.getString("logo");
				String name = object.getString("name");
				String inviter = object.getString("inviter");
				String isNew = object.getString("is_new");
				if (isNew.equals("1")) {
					modle.setNew(true);
				} else {
					modle.setNew(false);
				}
				modle.setInviterID(inviter);
				modle.setCirID(id);
				modle.setCirIcon(logo);
				modle.setCirName(name);
				serverListModle.add(modle);
				insertData(id, name, logo, String.valueOf(isNew));
				GetCircleUserTask task = new GetCircleUserTask(id);
				task.execute();
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
		if (result == null) {
			Utils.showToast(ErrorCodeUtil.convertToChines(errorCode));
		}
		callBack.getCircleList(serverListModle);
		return;
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}

	/**
	 * 插入数据库
	 * 
	 * @param name
	 * @param num
	 */
	private void insertData(String id, String name, String img, String status) {
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("cirID", id);
		values.put("cirName", name);
		values.put("cirImg", img);
		values.put("isNew", status);
		DBUtils.insertData("circlelist", values);
	}

	public interface GetCircleList {
		void getCircleList(List<CircleModle> listModle);
	}
}
