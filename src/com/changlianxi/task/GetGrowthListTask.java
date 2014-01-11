package com.changlianxi.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.GrowthImgModle;
import com.changlianxi.modle.GrowthModle;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.StringUtils;

/**
 * 获取成长列表
 * 
 * @author teeker_bin
 * 
 */
public class GetGrowthListTask extends AsyncTask<String, Integer, String> {
	private GroGrowthList callBack;
	private List<GrowthModle> listData = new ArrayList<GrowthModle>();
	private Map<String, Object> map;

	public GetGrowthListTask(Map<String, Object> map) {
		this.map = map;
	}

	public void setTaskCallBack(GroGrowthList callBack) {
		this.callBack = callBack;
	}

	@Override
	protected String doInBackground(String... params) {
		if (isCancelled()) {
			return null;
		}
		String result = HttpUrlHelper.postData(map, "/growth/ilist");
		try {
			JSONObject jsonobject = new JSONObject(result);
			String cid = jsonobject.getString("cid");
			String num = jsonobject.getString("num");
			JSONArray jsonarray = jsonobject.getJSONArray("growths");
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject object = (JSONObject) jsonarray.opt(i);
				GrowthModle modle = new GrowthModle();
				String id = object.getString("id");
				String uid = object.getString("uid");
				String content = object.getString("content");
				String location = object.getString("location");
				String happen = object.getString("happen");
				int praise = object.getInt("praise");
				int comment = object.getInt("comment");
				String publish = object.getString("publish");
				String isPraise = object.getString("mypraise");
				JSONArray imgrray = object.getJSONArray("images");
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
				modle.setName(md.getName());
				modle.setPersonImg(md.getAvator());
				if (isPraise.equals("1")) {
					modle.setIspraise(true);
				} else {
					modle.setIspraise(false);
				}
				modle.setImgModle(imgModle);
				modle.setCid(cid);
				modle.setNum(num);
				modle.setId(id);
				modle.setUid(uid);
				modle.setContent(content);
				modle.setLocation(location);
				modle.setHappen(happen);
				modle.setPraise(praise);
				modle.setComment(comment);
				modle.setPublish(publish);
				listData.add(modle);
			}
		} catch (JSONException e) {
			Logger.error(this, e);

			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		// 任务结束
		callBack.getGrowthList(listData);
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}

	public interface GroGrowthList {
		void getGrowthList(List<GrowthModle> listModle);
	}
}
