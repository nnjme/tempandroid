package com.changlianxi.task;

import com.changlianxi.data.enums.RetError;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.Utils;

import android.os.AsyncTask;

public abstract class BaseAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>{
	
	private PostCallBack<Result> callBack;
	public void setTaskCallBack(PostCallBack<Result> callBack) {
		this.callBack = callBack;
	}
	private boolean checkNet(){
		if (!Utils.isNetworkAvailable()) {
			Utils.showToast("网络异常,请检查网络...");
			return false;
		}
		return true;
	}
	
	public void executeWithCheckNet(Params... params){
		if(checkNet())
			super.execute(params);
	}
	@Override
	protected void onPostExecute(Result result) {
		if(result instanceof RetError){
			// 任务结束
			if (result == null || result != RetError.NONE) {
				Utils.showToast(ErrorCodeUtil.convertToChines(((RetError)result).name()));
				return;
			}
		}
		// 任务结束
		callBack.taskFinish(result);
	}
	/**
	 * 回调
	 * 
	 */
	public interface PostCallBack<Result> {
		public void taskFinish(Result result);
	}
}
