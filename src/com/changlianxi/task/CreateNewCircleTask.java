package com.changlianxi.task;

import com.changlianxi.data.Circle;
import com.changlianxi.data.enums.RetError;

public class CreateNewCircleTask extends BaseAsyncTask<Circle, Void, RetError>{
	
	@Override
	protected RetError doInBackground(Circle... params) {
		// TODO Auto-generated method stub
		Circle circle = params[0];
		RetError reError = circle.uploadForAdd();
		if(reError != RetError.NONE)
			return reError;
		reError = circle.uploadLogo(circle.getLogo());
		if(reError != RetError.NONE)
			return reError;
		return reError;
	}
	
}
