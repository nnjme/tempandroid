package com.changlianxi.task;

import com.changlianxi.data.CircleMember;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

public class UpLoadCircleMemberIdetailTask extends
		BaseAsyncTask<CircleMember, Void, RetError> {

	@Override
	protected RetError doInBackground(CircleMember... arg0) {
		// TODO Auto-generated method stub
		CircleMember oldCircleMember = arg0[0];
		CircleMember newCircleMember = arg0[1];
		RetError uploadAfterEdit = oldCircleMember.uploadAfterEdit(newCircleMember);
		try {
			oldCircleMember.writeDetails(DBUtils.db);
			oldCircleMember.write(DBUtils.db);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			uploadAfterEdit = RetError.UNKOWN;
		}
		return uploadAfterEdit;
	}

}
