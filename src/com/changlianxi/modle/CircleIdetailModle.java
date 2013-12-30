package com.changlianxi.modle;

import java.util.List;

/**
 * 圈子的详细信息对象
 * 
 * @author teeker_bin
 * 
 */
public class CircleIdetailModle {
	String cid = ""; // 请求的圈子ID
	String name = ""; // 圈子名称
	String description = ""; // 圈子描述
	String logo = ""; // 圈子logo图片地址
	String creator; // 圈子创建者，用户ID
	String createdTime = ""; // 圈子创建时间
	List<CircleRoles> rolesModle;// 职务
	int membersTotal;// 圈子成员总数（包括邀请中的，同意加入的，认证通过的）
	int membersInviting;// 邀请中的成员数量
	int membersVerified;// 加入但未认证通过的成员数量
	int membersUnverified;// 认证通过的成员数量

	public int getMembersTotal() {
		return membersTotal;
	}

	public void setMembersTotal(int membersTotal) {
		this.membersTotal = membersTotal;
	}

	public int getMembersInviting() {
		return membersInviting;
	}

	public void setMembersInviting(int membersInviting) {
		this.membersInviting = membersInviting;
	}

	public int getMembersVerified() {
		return membersVerified;
	}

	public void setMembersVerified(int membersVerified) {
		this.membersVerified = membersVerified;
	}

	public int getMembersUnverified() {
		return membersUnverified;
	}

	public void setMembersUnverified(int membersUnverified) {
		this.membersUnverified = membersUnverified;
	}

	public List<CircleRoles> getRolesModle() {
		return rolesModle;
	}

	public void setRolesModle(List<CircleRoles> rolesModle) {
		this.rolesModle = rolesModle;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

}
