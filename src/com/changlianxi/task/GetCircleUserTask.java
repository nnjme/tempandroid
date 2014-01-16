package com.changlianxi.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.MemberModle;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.MyComparator;
import com.changlianxi.util.PinyinUtils;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;

/**
 * 获取圈子成员列表
 * 
 * @author teeker_bin
 */
public class GetCircleUserTask extends AsyncTask<String, Integer, String> {
	private GetCircleUserList callBack;
	private List<MemberModle> listModles = new ArrayList<MemberModle>();// 存储成员列表
	private String cid;

	public GetCircleUserTask(String cid) {
		this.cid = cid;
	}

	public void setTaskCallBack(GetCircleUserList callBack) {
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
		map.put("cid", cid);
		String result = HttpUrlHelper.postData(map, "/circles/imembers/" + cid);
		try {
			JSONObject jsonobject = new JSONObject(result);
			JSONArray jsonarray = jsonobject.getJSONArray("members");
			if (jsonarray != null) {
				DBUtils.delUserListByCid(cid);
			}
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject object = (JSONObject) jsonarray.opt(i);
				MemberModle modle = new MemberModle();
				String id = object.getString("id");
				String logo = object.getString("avatar");
				String name = object.getString("name");
				String employer = object.getString("employer");
				String uid = object.getString("uid");
				String sortkey = PinyinUtils.getPinyin(name).toUpperCase();
				String pinyin = PinyinUtils.getPinyinFrt(name).toLowerCase();
				String mobileNum = "";
				String auth = object.getString("auth");
				String location = object.getString("location");
				JSONArray newsarray = object.getJSONArray("news");
				if (newsarray.length() > 0) {
					mobileNum = newsarray.get(0).toString();
				}
				modle.setId(id);
				modle.setName(name);
				modle.setEmployer(employer.equals("null") ? "" : employer);
				// modle.setImg(StringUtils.JoinString(logo, "_100x100"));
				modle.setImg(logo);
				modle.setSort_key(sortkey);
				modle.setUid(uid);
				modle.setMobileNum(mobileNum);
				modle.setKey_pinyin_fir(pinyin);
				modle.setAuth(auth.equals("1") ? true : false);
				modle.setLocation(location);
				listModles.add(modle);
				DBUtils.insertCircleUser(cid, id, uid, name,
						StringUtils.JoinString(logo, "_100x100"), employer,
						mobileNum, auth, location, sortkey, pinyin);
			}
			MyComparator compartor = new MyComparator();
			Collections.sort(listModles, compartor);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		// 任务结束
		if (callBack == null) {
			return;
		}
		callBack.getCircleUserList(listModles);
		return;
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}

	public interface GetCircleUserList {
		void getCircleUserList(List<MemberModle> listModles);
	}
}
