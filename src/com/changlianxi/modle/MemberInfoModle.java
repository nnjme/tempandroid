package com.changlianxi.modle;

import java.io.Serializable;

public class MemberInfoModle implements Serializable {
	/**
	 * Ȧ�ӳ�Ա������Ϣ
	 */
	private static final long serialVersionUID = -4036530540458973531L;
	private String name = "";// ����
	private String cellPhone = "";// �绰
	private String email = "";// ����
	private String avator = "";// ͷ���ַ
	private String gendar = "";// �Ա�
	private String birthday = "";// ����
	private String employer = "";// ������λ
	private String jobTitle = "";// ����ְλ

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvator() {
		return avator;
	}

	public void setAvator(String avator) {
		this.avator = avator;
	}

	public String getGendar() {
		return gendar;
	}

	public void setGendar(String gendar) {
		this.gendar = gendar;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
}
