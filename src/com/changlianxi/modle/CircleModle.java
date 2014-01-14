package com.changlianxi.modle;

import java.io.Serializable;

import com.changlianxi.data.Circle;

/**
 * 圈子对象信息
 * 
 * @author teeker_bin
 */
@Deprecated
public abstract class CircleModle implements Serializable {
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
	
	@Deprecated
	public int getNewMemberCount() {
		return newMemberCount;
	}
	@Deprecated
	public void setNewMemberCount(int newMemberCount) {
		this.newMemberCount = newMemberCount;
	}
	@Deprecated
	public int getNewGrowthCount() {
		return newGrowthCount;
	}
	@Deprecated
	public void setNewGrowthCount(int newGrowthCount) {
		this.newGrowthCount = newGrowthCount;
	}
	@Deprecated
	public int getNewChatCount() {
		return newChatCount;
	}
	@Deprecated
	public void setNewChatCount(int newChatCount) {
		this.newChatCount = newChatCount;
	}
	@Deprecated
	public int getNewDynamicCount() {
		return newDynamicCount;
	}
	@Deprecated
	public void setNewDynamicCount(int newDynamicCount) {
		this.newDynamicCount = newDynamicCount;
	}
	@Deprecated
	public int getNewCommentCount() {
		return newCommentCount;
	}
	@Deprecated
	public void setNewCommentCount(int newCommentCount) {
		this.newCommentCount = newCommentCount;
	}
	@Deprecated
	public int getPromptCount() {
		return promptCount;
	}
	@Deprecated
	public void setPromptCount(int promptCount) {
		this.promptCount = promptCount;
	}
	@Deprecated
	public CircleModle() {
	}
	@Deprecated
	public String getInviterID() {
		return inviterID;
	}
	@Deprecated
	public void setInviterID(String inviterID) {
		this.inviterID = inviterID;
	}
	@Deprecated
	public boolean isNew() {
		return isNew;
	}
	@Deprecated
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	@Deprecated
	public String getCirID() {
		return cirID;
	}
	@Deprecated
	public void setCirID(String cirID) {
		this.cirID = cirID;
	}
	@Deprecated
	public String getCirName() {
		return cirName;
	}
	@Deprecated
	public void setCirName(String cirName) {
		this.cirName = cirName;
	}
	@Deprecated
	public String getCirIcon() {
		return cirIcon;
	}
	@Deprecated
	public void setCirIcon(String cirIcon) {
		this.cirIcon = cirIcon;
	}

//	public int getType() {
//		return type;
//	}
//
//	public void setType(int type) {
//		this.type = type;
//	}

}
