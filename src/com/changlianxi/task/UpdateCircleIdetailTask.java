package com.changlianxi.task;

import com.changlianxi.data.Circle;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

/**
 * 获取圈子的详细资料
 * 
 * @author teeker_bin
 */
public class UpdateCircleIdetailTask extends BaseAsyncTask<Void, Void, RetError> {
	private Circle newCircle;
	private Circle oldCircle;

	public UpdateCircleIdetailTask(Circle oldCircle,Circle newCircle) {
		this.newCircle = newCircle;
		this.oldCircle = oldCircle;
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

}
