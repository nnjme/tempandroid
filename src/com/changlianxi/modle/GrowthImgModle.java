package com.changlianxi.modle;

import java.io.Serializable;

public class GrowthImgModle implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 成长记录中图片信息
	 */
	String id = "";// 图片id
	String img = "";// 图片地址
	String Img_200 = "";// 图片小图地址
	String Img_100 = "";// 图片小图地址
	String Img_60 = "";// 图片小图地址
	String Img_500 = "";// 图片小图地址

	public String getImg_500() {
		return Img_500;
	}

	public void setImg_500(String img_500) {
		Img_500 = img_500;
	}

	public String getImg_200() {
		return Img_200;
	}

	public void setImg_200(String img_200) {
		Img_200 = img_200;
	}

	public String getImg_100() {
		return Img_100;
	}

	public void setImg_100(String img_100) {
		Img_100 = img_100;
	}

	public String getImg_60() {
		return Img_60;
	}

	public void setImg_60(String img_60) {
		Img_60 = img_60;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

}
