package com.changlianxi.modle;

/**
 * 私信内容
 * 
 * @author teeker_bin
 * 
 */
public class MessageModle {
	// private String id;// 私信消息ID
	// private String cid;// 私信消息所属圈子ID
	// private String uid;// 私信发送者用户ID
	// private String type;// 私信类型
	private String content;// 私信内容，依据不同类型，内容含义不一样
	private String time;// 私信发送时间
	private String avatar;
	private boolean isSelf;// 是否是自己发送

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public boolean isSelf() {
		return isSelf;
	}

	public void setSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}

	// public String getId() {
	// return id;
	// }
	//
	// public void setId(String id) {
	// this.id = id;
	// }
	//
	// public String getCid() {
	// return cid;
	// }
	//
	// public void setCid(String cid) {
	// this.cid = cid;
	// }
	//
	// public String getUid() {
	// return uid;
	// }
	//
	// public void setUid(String uid) {
	// this.uid = uid;
	// }
	//
	// public String getType() {
	// return type;
	// }
	//
	// public void setType(String type) {
	// this.type = type;
	// }

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
