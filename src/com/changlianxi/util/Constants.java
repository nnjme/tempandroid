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
	public static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0;// 选择图片
	public static final int REQUEST_CODE_GETIMAGE_BYCAMERA = 1;// 拍照
	public static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo._ID, Phone.CONTACT_ID };
	/** 电话号码 **/
	public static final int PHONES_NUMBER_INDEX = 1;

	/** 联系人显示名称 **/
	public static final int PHONES_DISPLAY_NAME_INDEX = 0;
	/** 联系人头像id **/
	public static final int PHONE_PHOTO_ID = 2;
	/** 联系人id **/
	public static final int PHONE_CONTACT_ID = 3;
}
