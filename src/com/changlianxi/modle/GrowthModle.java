package com.changlianxi.modle;

import java.util.List;

/**
 * �ɳ���¼��ϸ��Ϣ
 * 
 * @author teeker_bin
 * 
 */
public class GrowthModle {
	private String personImg = "";// �����ɳ���¼�˵�ͷ���ַ
	private String name = "";// �����ɳ���¼�˵�����
	private String cid = "";// Ȧ��id
	private String num = "";// Ȧ������
	private String oldts = "";// ��������е�ʱ���
	private String newts = "";// ���η��ʷ������˵�ʱ���
	private String id = "";// �ɳ�id
	private String uid = "";// ������id
	private String content = "";// ��������
	private String location = "";// �����ص�
	private String happen = "";// ����ʱ��
	private int praise;// �޵�����
	private int comment;// ���۵�����
	private String imgid = "";// ͼƬid
	private String imgurl = "";// ͼƬ��ַ
	private String publish = "";// ����ʱ��
	private boolean ispraise;// ������Ǳ����Ƿ����
	private List<GrowthImgModle> imgModle;

	public String getPersonImg() {
		return personImg;
	}

	public void setPersonImg(String personImg) {
		this.personImg = personImg;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isIspraise() {
		return ispraise;
	}

	public List<GrowthImgModle> getImgModle() {
		return imgModle;
	}

	public void setImgModle(List<GrowthImgModle> imgModle) {
		this.imgModle = imgModle;
	}

	public void setIspraise(boolean ispraise) {
		this.ispraise = ispraise;
	}

	public String getPublish() {
		return publish;
	}

	public void setPublish(String publish) {
		this.publish = publish;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getOldts() {
		return oldts;
	}

	public void setOldts(String oldts) {
		this.oldts = oldts;
	}

	public String getNewts() {
		return newts;
	}

	public void setNewts(String newts) {
		this.newts = newts;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getHappen() {
		return happen;
	}

	public void setHappen(String happen) {
		this.happen = happen;
	}

	public int getPraise() {
		return praise;
	}

	public void setPraise(int praise) {
		this.praise = praise;
	}

	public int getComment() {
		return comment;
	}

	public void setComment(int comment) {
		this.comment = comment;
	}

	public String getImgid() {
		return imgid;
	}

	public void setImgid(String imgid) {
		this.imgid = imgid;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

}
