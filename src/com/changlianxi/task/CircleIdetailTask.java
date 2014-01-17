package com.changlianxi.task;

import com.changlianxi.data.Circle;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

/**
 * 获取圈子的详细资料
 * 
 * @author teeker_bin
 */
public class CircleIdetailTask extends BaseAsyncTask<Void, Void, RetError>{
	private Circle circle;

	public CircleIdetailTask(Circle circle) {
		this.circle = circle;
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

}
