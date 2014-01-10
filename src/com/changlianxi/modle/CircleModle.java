package com.changlianxi.modle;

import java.io.Serializable;

/**
 * 圈子对象信息
 * 
 * @author teeker_bin
 */
public class CircleModle implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String cirID = "";// 圈子id
	private String cirName = ""; // 圈子名称
	private String cirIcon = "";// 圈子图标
	private boolean isNew = false;// 是否是新邀请的圈子
	private String inviterID = "";
	private int type = 0;
	private int promptCount = 0;// 圈子提示数量
	private int newMemberCount = 0;// 新成员数
	private int newGrowthCount = 0;// 新成长数、
	private int newChatCount = 0;// 新聊天数、
	private int newDynamicCount = 0;// 新动态数、
	private int newCommentCount = 0;// 新评论数。

	public int getNewMemberCount() {
		return newMemberCount;
	}

	public void setNewMemberCount(int newMemberCount) {
		this.newMemberCount = newMemberCount;
	}

	public int getNewGrowthCount() {
		return newGrowthCount;
	}

	public void setNewGrowthCount(int newGrowthCount) {
		this.newGrowthCount = newGrowthCount;
	}

	public int getNewChatCount() {
		return newChatCount;
	}

	public void setNewChatCount(int newChatCount) {
		this.newChatCount = newChatCount;
	}

	public int getNewDynamicCount() {
		return newDynamicCount;
	}

	public void setNewDynamicCount(int newDynamicCount) {
		this.newDynamicCount = newDynamicCount;
	}

	public int getNewCommentCount() {
		return newCommentCount;
	}

	public void setNewCommentCount(int newCommentCount) {
		this.newCommentCount = newCommentCount;
	}

	public int getPromptCount() {
		return promptCount;
	}

	public void setPromptCount(int promptCount) {
		this.promptCount = promptCount;
	}

	public CircleModle() {
	}

	public String getInviterID() {
		return inviterID;
	}

	public void setInviterID(String inviterID) {
		this.inviterID = inviterID;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
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

}
