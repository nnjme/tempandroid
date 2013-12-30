package com.changlianxi.modle;

/**
 * 私信内容
 * 
 * @author teeker_bin
 * 
 */
public class MessageModle {
	// private String id;// 消息ID
	private String cid;// 消息所属圈子ID
	private String uid;// 发送或接收用户ID
	private int type;// 类型0 文本1图片
	private String content;// 内容，依据不同类型，内容含义不一样
	private String time;// 发送时间
	private String avatar;
	private boolean isSelf;// 是否是自己发送
	private String name;// 发送者姓名

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
