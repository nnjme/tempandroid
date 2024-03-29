package com.changlianxi.modle;

import java.io.Serializable;

/**
 * . 圈子成员列表显示信息
 * 
 * @author teeker_bin
 * 
 */
public class MemberModle implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name = "";// 姓名
	private String img = "";// 头像地址
	private String employer = "";// 工作单位
	private String job = "";// 工作
	private String sort_key = "";// 用来排序的关键字
	private String key_pinyin_fir = "";// 名字首字母//搜索时使用
	private String id = "";// id personID
	private String uid = "";// 用户id
	private String circleName = "";
	private String cid = "";// 所属圈子id
	private String mobileNum = "";
	private boolean auth;// 成员认证状态，
	private String location = "";

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public String getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCircleName() {
		return circleName;
	}

	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}

	public String getKey_pinyin_fir() {
		return key_pinyin_fir;
	}

	public void setKey_pinyin_fir(String key_pinyin_fir) {
		this.key_pinyin_fir = key_pinyin_fir;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSort_key() {
		return sort_key;
	}

	public void setSort_key(String sort_key) {
		this.sort_key = sort_key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

}
