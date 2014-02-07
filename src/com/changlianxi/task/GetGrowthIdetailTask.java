package com.changlianxi.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.changlianxi.data.Growth;
import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.GrowthImgModle;
import com.changlianxi.modle.GrowthModle;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;

/**
 * 获取单条成长记录详情接口
 * 
 * @author teeker_bin
 * 
 */
public class GetGrowthIdetailTask extends AsyncTask<String, Integer, String> {
	private GetGrowthIdetail callBack;
	private GrowthModle modles = new GrowthModle();
	private String cid = "";
	private String gid = "";

	public GetGrowthIdetailTask(String cid, String gid) {
		this.cid = cid;
		this.gid = gid;
	}

	public void setTaskCallBack(GetGrowthIdetail callBack) {
		this.callBack = callBack;
	}

	@Override
	protected String doInBackground(String... params) {
		if (isCancelled()) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cid", cid);
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("gid", gid);
		String result = HttpUrlHelper.postData(map, "/growth/idetail");
		try {
			JSONObject jsonobject = new JSONObject(result);
			JSONObject jsonarray = jsonobject.getJSONObject("growth");
			String id = jsonarray.getString("id");
			String uid = jsonarray.getString("uid");
			String content = jsonarray.getString("content");
			String location = jsonarray.getString("location");
			String happen = jsonarray.getString("happen");
			int praise = jsonarray.getInt("praise");
			int comment = jsonarray.getInt("comment");
			String publish = jsonarray.getString("publish");
			String isPraise = jsonarray.getString("mypraise");
			JSONArray imgrray = jsonarray.getJSONArray("images");
			List<GrowthImgModle> imgModle = new ArrayList<GrowthImgModle>();
			for (int j = 0; j < imgrray.length(); j++) {
				GrowthImgModle im = new GrowthImgModle();
				JSONObject imgObj = (JSONObject) imgrray.opt(j);
				String imgId = imgObj.getString("imgid");
				String img = imgObj.getString("img");
				im.setId(imgId);
				im.setImg(img);
				im.setImg_200(StringUtils.JoinString(img, "_200x200"));
				im.setImg_100(StringUtils.JoinString(img, "_100x100"));
				im.setImg_60(StringUtils.JoinString(img, "_60x60"));
				im.setImg_500(StringUtils.JoinString(img, "_500x500"));
				imgModle.add(im);
			}
			MemberInfoModle md = DBUtils.selectNameAndImgByID(uid);
			modles.setName(md.getName());
			modles.setPersonImg(md.getAvator());
			if (isPraise.equals("1")) {
				modles.setIspraise(true);
			} else {
				modles.setIspraise(false);
			}
			modles.setImgModle(imgModle);
			modles.setCid(cid);
			modles.setId(id);
			modles.setUid(uid);
			modles.setContent(content);
			modles.setLocation(location);
			modles.setHappen(happen);
			modles.setPraise(praise);
			modles.setComment(comment);
			modles.setPublish(publish);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		// 任务结束
		//callBack.getGrowthIdetail(modles);
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}

	public interface GetGrowthIdetail {
		void getGrowthIdetail(Growth models);
	}
}
