package com.changlianxi.task;

import java.io.File;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.changlianxi.inteface.UpLoadPic;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Utils;

public class UpLoadPicAsyncTask extends AsyncTask<String, Integer, String> {
	private UpLoadPic upload;// 上传图片完成接口
	private Map<String, Object> map;
	private String url;
	private String picPath = "";
	private String rt = "";
	private String avatar;
	private String errCode;

	public UpLoadPicAsyncTask(Map<String, Object> map, String url,
			String picPath, String avatar) {
		this.map = map;
		this.url = url;
		this.picPath = picPath;
		this.avatar = avatar;
	}

	public void setCallBack(UpLoadPic upload) {
		this.upload = upload;
	}

	// 可变长的输入参数，与AsyncTask.exucute()对应
	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理
	}

	@Override
	protected String doInBackground(String... params) {
		if (isCancelled()) {
			return null;
		}
		// File file = new File(picPath);
		File file = BitmapUtils.getImageFile(picPath);
		if (file == null) {
			return "error";
		}
		String result = HttpUrlHelper.upLoadPic(HttpUrlHelper.strUrl + url,
				map, file, avatar);
		file.delete();
		try {
			JSONObject jsonobject = new JSONObject(result);
			rt = jsonobject.getString("rt");
			if (!rt.equals("1")) {
				errCode = jsonobject.getString("err");
			}
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
			String content = ErrorCodeUtil.convertToChines(errCode);
			if (content != null) {
				Utils.showToast(content);
			}
			upload.upLoadFinish(false);
		}
	}
}
