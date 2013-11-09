package com.changlianxi.modle;

import android.graphics.Bitmap;

/**
 * 圈子对象信息
 * 
 * @author teeker_bin
 * 
 */
public class CircleModle {
	private String cirID = "";// 圈子id
	private String cirName = ""; // 圈子名称
	private String cirIcon = "";// 圈子图标
	private boolean isNew = false;// 是否是新邀请的圈子
	private int type = 0;
	private int cirImg;
	private Bitmap bmp;

	public CircleModle() {
	}

	public Bitmap getBmp() {
		return bmp;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}

	public String getCirID() {
		return cirID;
	}

	public void setCirID(String cirID) {
		this.cirID = cirID;
	}

	public String getCirName() {
		return cirName;
	}

	public void setCirName(String cirName) {
		this.cirName = cirName;
	}

	public String getCirIcon() {
		return cirIcon;
	}

	public void setCirIcon(String cirIcon) {
		this.cirIcon = cirIcon;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCirImg() {
		return cirImg;
	}

	public void setCirImg(int cirImg) {
		this.cirImg = cirImg;
	}

}
