package com.changlianxi.task;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.changlianxi.inteface.UpLoadCircleLogo;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;

/**
 * 上传圈子logo异步线程
 * 
 */
public class CircleLogoAsyncTask extends AsyncTask<String, Integer, String> {
	private String cirIconPath = "";
	private String cid = "";
	private UpLoadCircleLogo upload;// 上传图片完成接口

	public CircleLogoAsyncTask(String cirPath, String cid) {
		this.cirIconPath = cirPath;
		this.cid = cid;
	}

	public void setCallBack(UpLoadCircleLogo upload) {
		this.upload = upload;
	}

	// 可变长的输入参数，与AsyncTask.exucute()对应
	String rt = "1";

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理
	}

	@Override
	protected String doInBackground(String... params) {
		String result = "";
		File file = new File(cirIconPath);
		result = HttpUrlHelper.postCircleLogo(HttpUrlHelper.strUrl
				+ "/circles/iuploadLogo", file, cid,
				SharedUtils.getString("uid", ""),
				SharedUtils.getString("token", ""));
		Logger.debug(this, result);
		try {
			JSONObject jsonobject = new JSONObject(result);
			rt = jsonobject.getString("rt");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return rt;
	}

	@Override
	protected void onPostExecute(String result) {
		if (result.equals("1")) {
			upload.upLoadFinish(true);
		} else {
			upload.upLoadFinish(false);
		}
	}
}
