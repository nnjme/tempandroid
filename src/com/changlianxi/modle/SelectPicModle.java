package com.changlianxi.modle;

import android.graphics.Bitmap;

/**
 * 选择图片，或者拍照
 * 
 * @author teeker_bin
 * 
 */
public class SelectPicModle {
	String picPath = "";// 选择或则拍照的图片地址
	Bitmap bmp = null;// 选择或者拍照的图像

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public Bitmap getBmp() {
		return bmp;
	}

	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}

}
