package com.changlianxi.task;

import android.os.AsyncTask;

import com.changlianxi.data.Circle;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.Utils;

/**
 * 获取圈子的详细资料
 * 
 * @author teeker_bin
 */
public class CircleIdetailTask extends AsyncTask<Void, Integer, RetError> {
	private GetCircleIdetail callBack;
	private String errorCode;
	private Circle circle;

	public CircleIdetailTask(Circle circle) {
		this.circle = circle;
	}

	public void setTaskCallBack(GetCircleIdetail callBack) {
		this.callBack = callBack;
	}

	@Override
	protected RetError doInBackground(Void... params) {
		if (isCancelled()) {
			return null;
		}
		RetError result = null;
		try {
			circle.read(DBUtils.db);
			result = circle.refresh();
			circle.write(DBUtils.db);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected void onPostExecute(RetError result) {
		// 任务结束
		if (result == null || result != RetError.NONE) {
			Utils.showToast(ErrorCodeUtil.convertToChines(errorCode));
		}
		callBack.getIdetail(circle);
		return;

	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}

	public interface GetCircleIdetail {
		void getIdetail(Circle modle);
	}
}
