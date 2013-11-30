package com.changlianxi.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 圈子成长
 * 
 * @author nnjme
 * 
 */
public class Growth {
	private String id = "0";
	private String publisher = "0";
	private String content = "";
	private String location = "";
	private String happened = ""; // happen time
	private String published = ""; // publish time
	private int praised = 0;
	private int commented = 0;
	private List<GrowthImage> images = null;

	// TODO private boolean ispraise;// 用来标记本地是否点赞

	public Growth(String id, String publisher, String content, String location,
			String happened, String published) {
		this.id = id;
		this.publisher = publisher;
		this.content = content;
		this.location = location;
		this.happened = happened;
		this.published = published;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getHappened() {
		return happened;
	}

	public void setHappened(String happened) {
		this.happened = happened;
	}

	public String getPublished() {
		return published;
	}

	public void setPublished(String published) {
		this.published = published;
	}

	public int getPraised() {
		return praised;
	}

	public void setPraised(int praised) {
		this.praised = praised;
	}

	public int getCommented() {
		return commented;
	}

	public void setCommented(int commented) {
		this.commented = commented;
	}

	public void addImage(GrowthImage image) {
		if (this.images == null) {
			this.images = new ArrayList<GrowthImage>();
		}
		images.add(image);
	}

	public List<GrowthImage> getImages() {
		return images;
	}

	public void setImages(List<GrowthImage> images) {
		this.images = images;
	}

	@Override
	public String toString() {
		return "Growth [id=" + id + ", publisher=" + publisher + ", content="
				+ content + ", location=" + location + ", happened=" + happened
				+ ", published=" + published + ", praised=" + praised
				+ ", commented=" + commented + ", images=" + ((images != null) ? 0
				: images.size()) + "]";
	}

}
