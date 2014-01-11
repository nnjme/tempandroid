package com.changlianxi.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.Info;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
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
	private GetMyDetailValues valuesCallBack;
	private String avatarUrl = "";
	private String uid;
	public List<Info> basicList = new ArrayList<Info>();// 存放基本信息数据
	public List<Info> contactList = new ArrayList<Info>();// 存放联系方式数据
	public List<Info> socialList = new ArrayList<Info>();// 存放社交账号数据
	public List<Info> addressList = new ArrayList<Info>();// 存放地址数据
	public List<Info> eduList = new ArrayList<Info>();// 存放教育经历
	public List<Info> workList = new ArrayList<Info>();// 存放工作经历
	String pid = "";
	String name = "";
	private String change = "0";

	public void setTaskCallBack(GetMyDetail callBack) {
		this.callBack = callBack;
	}

	public void setValuesCallBack(GetMyDetailValues valuesCallBack) {
		this.valuesCallBack = valuesCallBack;
	}

	// 可变长的输入参数，与AsyncTask.exucute()对应
	@Override
	protected String doInBackground(String... params) {
		if (isCancelled()) {
			return null;
		}
		uid = SharedUtils.getString("uid", "");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("token", SharedUtils.getString("token", ""));
		String json = HttpUrlHelper.postData(map, "/people/imyDetail");
		try {
			JSONObject jsonobject = new JSONObject(json);
			if (!jsonobject.getString("rt").equals("1")) {
				return "";
			}
			DBUtils.clearTableData(Constants.MYDETAIL);// 清空本地表 保存最新数据
			pid = jsonobject.getString("pid");
			change = jsonobject.getString("changed");
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
				} else if (key.equals("D_NAME")) {
					name = value;
				}
				if (Arrays.toString(UserInfoUtils.eduStr).contains(key)
						|| Arrays.toString(UserInfoUtils.workStr).contains(key)) {
					start = DateUtils.interceptDateStr(
							object.getString("start"), "yyyy.MM.dd");
					end = DateUtils.interceptDateStr(object.getString("end"),
							"yyyy.MM.dd");
				}
				valuesClassification(id, key, value, start, end);
				insertData(id, pid, uid, name, key, value, start, end, change);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "1";// 正常访问
	}

	@Override
	protected void onPostExecute(String result) {
		// 任务结束
		if (callBack != null) {
			callBack.getMydetail(avatarUrl);
		}
		if (valuesCallBack != null) {
			valuesCallBack.getMyDetailsValues(change, name, pid, avatarUrl,
					basicList, contactList, socialList, addressList, eduList,
					workList);
		}
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}

	public interface GetMyDetail {
		void getMydetail(String avatarUrl);
	}

	/**
	 * 数据分类
	 * 
	 * @param id
	 * @param key
	 * @param value
	 */
	public void valuesClassification(String id, String key, String value,
			String start, String end) {
		Info info = new Info();
		info.setValue(value);
		info.setId(id);
		info.setType(key);
		String typekey = "";
		for (int i = 0; i < UserInfoUtils.basicStr.length; i++) {
			if (key.equals(UserInfoUtils.basicStr[i])) {
				typekey = UserInfoUtils.convertToChines(key);
				info.setKey(typekey);
				basicList.add(info);
			}
		}
		if (Arrays.toString(UserInfoUtils.socialStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			socialList.add(info);
		} else if (Arrays.toString(UserInfoUtils.contactStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			contactList.add(info);

		} else if (Arrays.toString(UserInfoUtils.addressStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			addressList.add(info);

		} else if (Arrays.toString(UserInfoUtils.eduStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setStartDate(start);
			info.setEndDate(end);
			eduList.add(info);
		} else if (Arrays.toString(UserInfoUtils.workStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setStartDate(start);
			info.setEndDate(end);
			workList.add(info);
		}

	}

	public interface GetMyDetailValues {
		void getMyDetailsValues(String change, String name, String pid,
				String avatar, List<Info> basicList, List<Info> contactList,
				List<Info> socialList, List<Info> addressList,
				List<Info> eduList, List<Info> workList);

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
	private void insertData(String tid, String pid, String uid, String name,
			String key, String value, String start, String end, String changed) {
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("tid", tid);
		values.put("pid", pid);
		values.put("uid", uid);
		values.put("name", name);
		values.put("key", key);
		values.put("value", value);
		values.put("startDate", start);
		values.put("endDate", end);
		values.put("changed", changed);// 1修改 0 没修改
		DBUtils.insertData(Constants.MYDETAIL, values);
	}
}
