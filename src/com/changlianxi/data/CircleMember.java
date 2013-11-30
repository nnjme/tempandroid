package com.changlianxi.data;

/**
 * 圈子成员
 * 
 * @author nnjme
 * 
 */
public class CircleMember {
	public static enum Status {
		INVINTING, ENTER_ACCEPT, ENTER_REJECT, VERIFIED, KICKOFFING, KICKOUT, QUIT, OTHER
	};

	private String uid = "0";
	private String pid = "0";
	private String name = "";
	private String avatar = "";
	private String employer = "";
	private String jobtitle = "";
	private Status status = Status.INVINTING;

	public CircleMember(String uid, String pid, String name) {
		this.uid = uid;
		this.pid = pid;
		this.name = name;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
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

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public String getJobtitle() {
		return jobtitle;
	}

	public void setJobtitle(String jobtitle) {
		this.jobtitle = jobtitle;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "CircleMember [uid=" + uid + ", pid=" + pid + ", name=" + name
				+ ", employer=" + employer + ", jobtitle=" + jobtitle
				+ ", status=" + status + "]";
	}

}
