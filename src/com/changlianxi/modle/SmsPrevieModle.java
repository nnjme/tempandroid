package com.changlianxi.modle;

import java.io.Serializable;

/**
 * 邀请成员时 短信预览 使用
 * 
 * @author teeker_bin
 * 
 */
public class SmsPrevieModle implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name = "";
	private String num = "";
	private String content = ""; // 展示的内容
	private String type = "";// 1 可以接受邀请 非1不可以

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
