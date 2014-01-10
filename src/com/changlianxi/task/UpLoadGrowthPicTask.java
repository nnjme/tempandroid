package com.changlianxi.task;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.changlianxi.inteface.UpLoadPic;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.Utils;

/**
 * 成长记录图片上传AsyncTask
 * 
 * @author teeker_bin
 * 
 */
public class UpLoadGrowthPicTask extends AsyncTask<String, Integer, String> {
	private List<String> picPath;// 存放要上传的图片地址列表
	private String rt = "1";
	private UpLoadPic growCallBack;
	private Map<String, Object> map;
	private String errCode;

	public UpLoadGrowthPicTask(List<String> picPath, Map<String, Object> map) {
		this.picPath = picPath;
		this.map = map;
	}

	public void setGrowthCallBack(UpLoadPic callback) {
		this.growCallBack = callback;
	}

	@Override
	protected String doInBackground(String... params) {
		if (isCancelled()) {
			return null;
		}
		String result = "";
		for (int i = 0; i < picPath.size(); i++) {
			String path = picPath.get(i);
			File file = BitmapUtils.getImageFile(path);
			// File file = new File(picPath.get(i));
			if (file != null) {
				result = HttpUrlHelper.upLoadPic(HttpUrlHelper.strUrl
						+ "/growth/iuploadImage", map, file, "img");
			}
			try {
				JSONObject jsonobject = new JSONObject(result);
				rt = jsonobject.getString("rt");
				if (!rt.equals("1")) {
					errCode = jsonobject.getString("err");
				}
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
			Utils.showToast(ErrorCodeUtil.convertToChines(errCode));
			growCallBack.upLoadFinish(false);
		}
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理
	}
}
