package com.changlianxi.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.modle.NewsComments;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;

/**
 * 获取圈子成长中跟我相关的评论列表内容，跟我相关是指评论我发的成长或者在评论中回复我的评论。
 * 
 * @author teeker_bin
 * 
 */
public class GetCommentsForMeTask extends AsyncTask<String, Integer, String> {
	private GetCommentsForMeCallBack callBack;
	private List<NewsComments> listModle = new ArrayList<NewsComments>();
	private String err;
	private String startTime = "";
	private String endTime = "";
	private String cid = "";

	public GetCommentsForMeTask(Context context, String cid, String startTime,
			String endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.cid = cid;

	}

	public void setTaskCallBack(GetCommentsForMeCallBack callBack) {
		this.callBack = callBack;
	}

	@Override
	protected String doInBackground(String... params) {
		if (isCancelled()) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("cid", cid);
		map.put("token", SharedUtils.getString("token", ""));
		map.put("start", startTime);
		map.put("end", endTime);
		String result = HttpUrlHelper.postData(map, "/growth/icommentsForMe");
		try {
			JSONObject jsonobject = new JSONObject(result);
			String rt = jsonobject.getString("rt");
			if (!rt.equals("1")) {
				err = jsonobject.getString("err");
				return null;
			}
			int num = jsonobject.getInt("num");
			JSONArray jsonarray = jsonobject.getJSONArray("comments");
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject object = (JSONObject) jsonarray.opt(i);
				NewsComments modle = new NewsComments();
				String name = "";
				String avatarPath = "";
				String id = object.getString("id");
				String publisher = object.getString("publisher");
				String gid = object.getString("gid");
				String uid = object.getString("uid");
				String replyid = object.getString("replyid");
				String content = object.getString("content");
				String time = object.getString("time");
				MemberInfoModle info = DBUtils.selectNameAndImgByID(uid);
				if (info != null) {
					avatarPath = info.getAvator();
					name = info.getName();
				}
				modle.setName(name);
				modle.setAvatar(avatarPath);
				modle.setId(id);
				modle.setGid(gid);
				modle.setNum(num);
				modle.setPublisher(publisher);
				modle.setReplyid(replyid);
				modle.setUid(uid);
				modle.setTime(DateUtils.interceptDateStr(time, "yyyy-MM-dd"));
				modle.setContent(content);
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
		if (result == null) {
			Utils.showToast(ErrorCodeUtil.convertToChines(err));
		}
		callBack.getCommentList(listModle);

	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}

	public interface GetCommentsForMeCallBack {
		void getCommentList(List<NewsComments> listModle);
	}
}
