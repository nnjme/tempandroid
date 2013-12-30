package com.changlianxi.modle;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class MyCardAvatar implements Parcelable {
	/**
	 * 
	 */
	Bitmap bitmap;

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}
}
