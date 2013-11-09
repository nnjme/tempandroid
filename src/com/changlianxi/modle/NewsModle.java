package com.changlianxi.modle;

/**
 * 动态信息
 * 
 * @author teeker_bin
 * 
 */
public class NewsModle {
	private String id;// 动态ID
	private String type;// 动态类型
	private String user1;// 第一关联用户ID
	private String user2;// 第二关联用户ID
	private String person2;// 第二关联成员ID
	private String createdTime;// 动态创建时间
	private String content;// 动态内容
	private String detail;// 动态详情

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUser1() {
		return user1;
	}

	public void setUser1(String user1) {
		this.user1 = user1;
	}

	public String getUser2() {
		return user2;
	}

	public void setUser2(String user2) {
		this.user2 = user2;
	}

	public String getPerson2() {
		return person2;
	}

	public void setPerson2(String person2) {
		this.person2 = person2;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

}
