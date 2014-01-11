package com.changlianxi.util;

import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;

/**
 * 常量类
 * 
 * @author teeker_bin
 * 
 */
public class Constants {
	/** 广播action ***************/
	public static final String MESSAGE_PROMPT = "com.changlianxi.MessagePrompt";// 左侧菜单个人私信提示
	public static final String MYCARD_PROMPT = "com.changlianxi.updateMycardPrompt";// 左侧菜单个人名片的提示
	public static final String UPDECIRNAME = "com.changlianxi.updateCircleName";// 修改圈子名称
	public static final String PUSH_TYPE = "com.changlianxi.pushType";// 推送类型
	public static final String REMOVE_PROMPT_COUNT = "com.changlianxi.remorePromptCount";// /减少圈子的提示数量
	public static final String EXIT_CIRCLE = "com.changlianxi.exitCircle";// 退出圈子
	public static final String ACCEPT_OR_REFUSE_INVITE = "refuse";// 接受或拒绝新圈子邀请
	public static final String REFRESH_CIRCLE_LIST = "com.changlianxi.refreshCircleList";// 更新圈子列表
	/***************************************************/
	public static final String CIRCLELIST_TABLE = "circlelist";// 圈子列表
	public static final String USERLIST_TABLE = "userlist";// 成员列表
	public static final String GROWTH_TABLE = "growthlist";// 成长列表
	public static final String NEWS_TABLE = "newslist";// 动态列表
	public static final String CIRCLEDETAIL = "circledetail";// 圈子资料表
	public static final String USERDETAIL = "userdetail";// 成员资料表
	public static final String MYDETAIL = "mydetail";// 个人资料表
	public static final String CHATLIST_TABLE_NAME = "chatlist";// 聊天记录表
	public static final String MESSAGELIST_TABLE_NAME = "messagelist";// 私信记录表
	public static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0;// 选择图片
	public static final int REQUEST_CODE_GETIMAGE_BYCAMERA = 1;// 拍照
	public static final int REQUEST_CODE_GETIMAGE_DROP = 2;// 拍照
	/** 获取库Phon表字段 **/
	public static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo._ID, Phone.CONTACT_ID };
	/** 联系人显示名称 **/
	public static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** 电话号码 **/
	public static final int PHONES_NUMBER_INDEX = 1;

	/** 头像ID **/
	public static final int PHONES_PHOTO_ID_INDEX = 2;

	/** 联系人的ID **/
	public static final int PHONES_CONTACT_ID_INDEX = 3;

}
