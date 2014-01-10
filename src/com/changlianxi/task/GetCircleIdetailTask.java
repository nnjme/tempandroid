package com.changlianxi.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.CircleIdetailModle;
import com.changlianxi.modle.CircleRoles;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;

/**
 * 获取圈子的详细资料
 * 
 * @author teeker_bin
 */
public class GetCircleIdetailTask extends AsyncTask<String, Integer, String> {
	private GetCircleIdetail callBack;
	private CircleIdetailModle modle;
	private String cid;
	private String errorCode;

	public GetCircleIdetailTask(String cid) {
		this.cid = cid;
	}

	public void setTaskCallBack(GetCircleIdetail callBack) {
		this.callBack = callBack;
	}

	@Override
	protected String doInBackground(String... params) {
		if (isCancelled()) {
			return null;
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("cid", cid);
		String result = HttpUrlHelper.postData(map, "/circles/idetail");
		try {
			JSONObject jsonobject = new JSONObject(result);
			if (!jsonobject.getString("rt").equals("1")) {
				errorCode = jsonobject.getString("err");
				return null;
			}
			DBUtils.delCiecleDetails(cid);
			String cid = jsonobject.getString("cid");
			JSONObject json = jsonobject.getJSONObject("circle");
			modle = new CircleIdetailModle();
			String name = json.getString("name");
			String description = json.getString("description");
			String logo = json.getString("logo");
			String creator = json.getString("creator");
			String created = json.getString("created");
			modle.setCid(cid);
			modle.setName(name);
			modle.setCreatedTime(created);
			modle.setCreator(creator);
			modle.setLogo(logo);
			modle.setDescription(description);
			JSONObject jsonCount = jsonobject.getJSONObject("membersCount");
			int total = jsonCount.getInt("total");
			int inviting = jsonCount.getInt("inviting");
			int verified = jsonCount.getInt("verified");
			int unverified = jsonCount.getInt("unverified");
			modle.setMembersTotal(total);
			modle.setMembersInviting(inviting);
			modle.setMembersUnverified(unverified);
			modle.setMembersVerified(verified);
			JSONArray jsonroles = jsonobject.getJSONArray("roles");
			List<CircleRoles> circleRoles = new ArrayList<CircleRoles>();
			for (int j = 0; j < jsonroles.length(); j++) {
				JSONObject roleobject = (JSONObject) jsonroles.opt(j);
				CircleRoles rolesModle = new CircleRoles();
				String roleId = roleobject.getString("id");
				String roleName = roleobject.getString("name");
				rolesModle.setRoleId(roleId);
				rolesModle.setRoleName(roleName);
				circleRoles.add(rolesModle);
			}
			modle.setRolesModle(circleRoles);
			DBUtils.saveCircleDetail(cid, name, logo, description, total + "",
					verified + "", creator);
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
		callBack.getIdetail(modle);
		return;

	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}

	public interface GetCircleIdetail {
		void getIdetail(CircleIdetailModle modle);
	}
}
