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
public class UpdateCircleIdetailTask extends AsyncTask<Void, Void, RetError> {
	private GetCircleIdetail callBack;
	private String errorCode;
	private Circle newCircle;
	private Circle oldCircle;

	public UpdateCircleIdetailTask(Circle oldCircle,Circle newCircle) {
		this.newCircle = newCircle;
		this.oldCircle = oldCircle;
	}

	public void setTaskCallBack(GetCircleIdetail callBack) {
		this.callBack = callBack;
	}

	@Override
	protected RetError doInBackground(Void... params) {
		if (isCancelled()) {
			return null;
		}
		RetError retError = null;
		try {
			retError = oldCircle.uploadAfterEdit(newCircle);
			if(retError != RetError.NONE){
				return retError;
			}
			retError = oldCircle.uploadLogo(newCircle.getLogo());
			if(retError != retError.NONE){
				return retError;
			}
			oldCircle.write(DBUtils.db);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retError = null;
			e.printStackTrace();
		}
		return retError;
	}

	@Override
	protected void onPostExecute(RetError result) {
		boolean b = true;
		if(result == null){
			b = false;
		}else if (result != RetError.NONE) {
			Utils.showToast(ErrorCodeUtil.convertToChines(result.name()));
			b = false;
		}
		callBack.finishUpdate(b);
		return;

	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理

	}

	public interface GetCircleIdetail {
		void finishUpdate(boolean b);
	}
}
