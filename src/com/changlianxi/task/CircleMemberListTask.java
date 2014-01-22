package com.changlianxi.task;

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.data.CircleMemberList;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

public class CircleMemberListTask extends BaseAsyncTask<CircleMemberList, Void, RetError> {

	
	@Override
	protected RetError doInBackground(CircleMemberList... params) {
		// TODO Auto-generated method stub
		if (isCancelled() || params == null) {
			return null;
		}
		try {
			CircleMemberList circleMemberList = params[0];
			circleMemberList.read(DBUtils.db);
			circleMemberList.refresh(CLXApplication.circleMemberListLastRefreshTime);
			circleMemberList.write(DBUtils.db);
			return RetError.NONE;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return RetError.UNKOWN;
	}

}
