package com.changlianxi.task;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.changlianxi.inteface.UpLoadPic;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;

/**
 * 成长记录图片上传AsyncTask
 * 
 * @author teeker_bin
 * 
 */
public class UpLoadGrowthPicTask extends AsyncTask<String, Integer, String> {
	private List<String> picPath;// 存放要上传的图片地址列表
	private String rt = "1";
	private String cid = "";// 圈子id
	private String gid = "";// 成长记录id
	private UpLoadPic growCallBack;

	public UpLoadGrowthPicTask(List<String> picPath, String cid, String gid) {
		this.picPath = picPath;
		this.cid = cid;
		this.gid = gid;
	}

	public void setGrowthCallBack(UpLoadPic callback) {
		this.growCallBack = callback;
	}

	@Override
	protected String doInBackground(String... params) {
		String result = "";
		for (int i = 0; i < picPath.size(); i++) {
			File file = new File(picPath.get(i));
			if (file != null) {
				result = HttpUrlHelper.postDataFile(HttpUrlHelper.strUrl
						+ "/growth/iuploadImage", file, cid,
						SharedUtils.getString("uid", ""), gid,
						SharedUtils.getString("token", ""));
				Logger.debug(this, "Picresult:" + result);
			}
			try {
				JSONObject jsonobject = new JSONObject(result);
				rt = jsonobject.getString("rt");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Logger.error(this, e);

				e.printStackTrace();
			}
		}

		return rt;
	}

	@Override
	protected void onPostExecute(String result) {
		// 任务结束
		if (result.equals("1")) {
			growCallBack.upLoadFinish(true);
		} else {
			growCallBack.upLoadFinish(false);
		}
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理
	}
}
