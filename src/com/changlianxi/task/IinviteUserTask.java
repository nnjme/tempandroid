package com.changlianxi.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.changlianxi.modle.SmsPrevieModle;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.SharedUtils;

/**
 * 邀请成员
 * 
 * @author teeker_bin
 */
public class IinviteUserTask extends AsyncTask<String, Integer, String> {
	private IinviteUser callBack;
	private String cid;
	private List<SmsPrevieModle> contactsList = new ArrayList<SmsPrevieModle>();
	private String rt = "";
	private JSONArray jsonAry = new JSONArray();
	private JSONObject jsonObj;
	private String details = "";

	public IinviteUserTask(String cid, List<SmsPrevieModle> contactsList) {
		this.cid = cid;
		this.contactsList = contactsList;
	}

	public void setTaskCallBack(IinviteUser callBack) {
		this.callBack = callBack;
	}

	// 可变长的输入参数，与AsyncTask.exucute()对应

	@Override
	protected String doInBackground(String... params) {
		for (int i = 0; i < contactsList.size(); i++) {
			BuildJson(contactsList.get(i).getName(), contactsList.get(i)
					.getNum());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cid", cid);
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("persons", jsonAry.toString());
		String json = HttpUrlHelper.postData(map, "/people/iinviteMore");
		try {
			JSONObject object = new JSONObject(json);
			rt = object.getString("rt");
			if (rt.equals("1")) {
				details = object.getString("details");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return rt;
	}

	@Override
	protected void onPostExecute(String result) {
		callBack.inviteUser(rt, details);
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理
	}

	public interface IinviteUser {
		void inviteUser(String rt, String details);
	}

	/**
	 * 构建json串
	 * 
	 */
	private void BuildJson(String name, String num) {
		try {
			jsonObj = new JSONObject();
			jsonObj.put("name", name);
			jsonObj.put("cellphone", num);
			jsonAry.put(jsonObj);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
