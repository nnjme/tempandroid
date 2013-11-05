package com.changlianxi.modle;

/**
 * 私信列表
 * 
 * @author teeker_bin
 * 
 */
public class MessagesListModle {
	private String uid;// 发送或接收用户ID
	private String mid;// 私信消息ID
	private String cid;// 私信所属圈子ID
	private String type;// 私信类型
	private String msg;// 私信内容，依据不同类型，内容含义不一样
	private String time;// 私信发送时间
	private String newCount;// 未读私信数量

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getNewCount() {
		return newCount;
	}

	public void setNewCount(String newCount) {
		this.newCount = newCount;
	}

}
