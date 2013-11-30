package com.changlianxi.data;

/**
 * 圈子成长中的图片
 * 
 * @author nnjme
 * 
 */
public class GrowthImage {
	private String id = "0";
	private String img = "";

	public GrowthImage(String id, String img) {
		this.id = id;
		this.img = img;
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
