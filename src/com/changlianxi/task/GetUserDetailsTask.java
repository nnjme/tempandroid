package com.changlianxi.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.Info;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.UserInfoUtils;
import com.changlianxi.util.Utils;

/**
 * 获取成员信息接口
 * 
 * @author teeker_bin
 */
public class GetUserDetailsTask extends AsyncTask<String, Integer, String> {
	private GetValuesTask callBack;
	private String cid = "";
	private String pid = "";
	private String errorCode;
	public List<Info> basicList = new ArrayList<Info>();// 存放基本信息数据
	public List<Info> contactList = new ArrayList<Info>();// 存放联系方式数据
	public List<Info> socialList = new ArrayList<Info>();// 存放社交账号数据
	public List<Info> addressList = new ArrayList<Info>();// 存放地址数据
	public List<Info> eduList = new ArrayList<Info>();// 存放教育经历
	public List<Info> workList = new ArrayList<Info>();// 存放工作经历

	public GetUserDetailsTask(String cid, String pid) {
		this.pid = pid;
		this.cid = cid;
	}

	public void setTaskCallBack(GetValuesTask callBack) {
		this.callBack = callBack;
	}

	// 可变长的输入参数，与AsyncTask.exucute()对应
	@Override
	protected String doInBackground(String... params) {
		if (isCancelled()) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cid", cid);
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("pid", pid);
		map.put("token", SharedUtils.getString("token", ""));
		String json = HttpUrlHelper.postData(map, "/people/idetail");
		try {
			JSONObject jsonobject = new JSONObject(json);
			if (!jsonobject.getString("rt").equals("1")) {
				errorCode = jsonobject.getString("err");
				return null;
			}
			DBUtils.delUserDetails(pid);// 清空本地表 保存最新数据
			JSONArray jsonarray = jsonobject.getJSONArray("person");
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject object = (JSONObject) jsonarray.opt(i);
				String start = "";
				String end = "";
				String id = object.getString("id");// 属性id
				String key = object.getString("type");// 属性名称
				String value = object.getString("value");// 属性值
				if (Arrays.toString(UserInfoUtils.eduStr).contains(key)
						|| Arrays.toString(UserInfoUtils.workStr).contains(key)) {
					start = DateUtils.interceptDateStr(
							object.getString("start"), "yyyy.MM.dd");
					end = DateUtils.interceptDateStr(object.getString("end"),
							"yyyy.MM.dd");
				}
				valuesClassification(id, key, value, start, end);
				DBUtils.insertUserDetails(cid, id, pid, key, value, start, end);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "1";// 正常访问
	}

	@Override
	protected void onPostExecute(String result) {
		// 任务结束
		if (result == null) {
			Utils.showToast(ErrorCodeUtil.convertToChines(errorCode));
		}
		callBack.onTaskFinish(basicList, contactList, socialList, addressList,
				eduList, workList);

	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

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
		for (int i = 0; i < UserInfoUtils.basicUserChineseStr.length; i++) {
			if (key.equals(UserInfoUtils.basicUserStr[i])) {
				typekey = UserInfoUtils.convertToChines(key);
				info.setKey(typekey);
				info.setTitleKey("基本信息");
				basicList.add(info);
			}
		}
		if (Arrays.toString(UserInfoUtils.socialStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setTitleKey("社交账号");

			socialList.add(info);
		} else if (Arrays.toString(UserInfoUtils.contactStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setTitleKey("联系方式");
			contactList.add(info);

		} else if (Arrays.toString(UserInfoUtils.addressStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setTitleKey("通讯地址");
			addressList.add(info);

		} else if (Arrays.toString(UserInfoUtils.eduStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setStartDate(start);
			info.setEndDate(end);
			info.setTitleKey("教育经历");
			eduList.add(info);
		} else if (Arrays.toString(UserInfoUtils.workStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setStartDate(start);
			info.setEndDate(end);
			info.setTitleKey("工作经历");
			workList.add(info);
		}

	}

	public interface GetValuesTask {
		void onTaskFinish(List<Info> basicList, List<Info> contactList,
				List<Info> socialList, List<Info> addressList,
				List<Info> eduList, List<Info> workList);

	}

}
