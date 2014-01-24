package com.changlianxi.task;

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

public class CircleMemberIdetailTask extends
		BaseAsyncTask<CircleMember, Void, RetError> {

	@Override
	protected RetError doInBackground(CircleMember... arg0) {
		if (isCancelled() || arg0 == null) {
			return null;
		}
		try {
			CircleMember circleMember = arg0[0];
			circleMember.read(DBUtils.db);
			circleMember.readDetails(DBUtils.db);
			RetError refresh = circleMember.refresh(CLXApplication.circleMemberLastRefreshTime);
			circleMember.writeDetails(DBUtils.db);
			circleMember.write(DBUtils.db);
			return refresh;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
