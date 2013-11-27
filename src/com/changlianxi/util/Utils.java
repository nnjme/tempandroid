package com.changlianxi.util;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.activity.R;
import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.ContactModle;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.modle.MessageModle;

/**
 * 公用工具类
 * 
 * @author teeker_bin
 * 
 */
public class Utils {
	private static List<ContactModle> contactList = new ArrayList<ContactModle>();
	private static TelephonyManager mTelephonyManager;

	/**
	 * 手机号码验证
	 * 
	 * @param
	 * @return
	 */
	public static boolean isPhoneNum(String strPhoneNum) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(strPhoneNum);
		return m.matches();
	}

	/**
	 * 邮箱验证
	 * 
	 * @param strEmail
	 * @return
	 */
	public static boolean isEmail(String strEmail) {
		String strPattern = "^[a-zA-Z0-9]*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}

	/**
	 * 显示提示信息
	 * 
	 * @param str
	 */
	public static void showToast(String str) {
		Toast toast = Toast.makeText(CLXApplication.getInstance(), str,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

	}

	/**
	 * 隐藏软键盘如果输入法在窗口上已经显示，则隐藏，反之则显示)
	 */
	public static void hideSoftInput(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

	}

	/***
	 * 半角转换为全角
	 * 
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	/**
	 * 检测SIM 的相关状态
	 * 
	 * @param context
	 * @return
	 */
	private static int getSimState(Context context) {
		mTelephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyManager.getSimState();
	}

	/**
	 * 获取手机和SIM卡上的通讯录
	 * 
	 * @param context
	 * @return
	 */
	public static List<ContactModle> getContactList(Context context) {
		queryContactList(context);
		return contactList;
	}

	/**
	 * 查询手机和SIM卡中的联系人，不管系统里是否缓存了通讯录，先清空缓存，再重新加载联系人信息至缓存中
	 * 
	 * @param context
	 * @return
	 */
	public static void queryContactList(Context context) {
		contactList.clear();
		ContentResolver resolver = context.getContentResolver();
		// 获取手机上的联系人信息
		getContactList(resolver, Phone.CONTENT_URI, context);

		if (getSimState(context) == TelephonyManager.SIM_STATE_READY) { // 判断SIM是否存在
			// 获取SIM卡上的联系人信息
			getContactList(resolver, Uri.parse("content://icc/adn"), context);
		}

	}

	/**
	 * 获取联系人列表
	 * 
	 * @param resolver
	 * @param uri
	 * @return
	 */
	private static List<ContactModle> getContactList(ContentResolver resolver,
			Uri uri, Context mContext) {
		// 获取查询结果游标
		Cursor phoneCursor = resolver.query(uri, Constants.PHONES_PROJECTION,
				null, null, null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				// 得到手机号码
				String phoneNumber = phoneCursor
						.getString(Constants.PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(Constants.PHONES_DISPLAY_NAME_INDEX);
				// 得到联系人ID
				Long contactid = phoneCursor
						.getLong(Constants.PHONE_CONTACT_ID);
				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(Constants.PHONE_PHOTO_ID);
				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;
				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (photoid > 0) {
					Uri ur = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(resolver, ur);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(
							mContext.getResources(), R.drawable.root_default);
				}
				ContactModle modle = new ContactModle();
				modle.setName(contactName);
				modle.setNum(phoneNumber);
				modle.setBmp(contactPhoto);
				contactList.add(modle);
			}
			phoneCursor.close();
		}
		return contactList;
	}

	/**
	 * 获取联系人信息
	 */
	public static List<ContactModle> getPhoneContacts(Context mContext) {
		List<ContactModle> listmodle = new ArrayList<ContactModle>();
		ContentResolver resolver = mContext.getContentResolver();
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				Constants.PHONES_PROJECTION, null, null, null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				// 得到手机号码
				String phoneNumber = phoneCursor
						.getString(Constants.PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(Constants.PHONES_DISPLAY_NAME_INDEX);
				// 得到联系人ID
				Long contactid = phoneCursor
						.getLong(Constants.PHONE_CONTACT_ID);
				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(Constants.PHONE_PHOTO_ID);
				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;
				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (photoid > 0) {
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(
							mContext.getResources(), R.drawable.root_default);
				}
				ContactModle modle = new ContactModle();
				modle.setName(contactName);
				modle.setNum(phoneNumber);
				modle.setBmp(contactPhoto);
				listmodle.add(modle);
			}
			phoneCursor.close();
		}
		return listmodle;
	}

	/**
	 * 根据url生成缓存文件完整路径名
	 * 
	 * @param url
	 * @return
	 */
	public static String urlToFilePath(String url) {

		// 扩展名位置
		int index = url.lastIndexOf('.');
		if (index == -1) {
			return null;
		}
		StringBuilder filePath = new StringBuilder();

		// 图片存取路径
		// filePath.append(myapp.getCacheDir().toString()).append('/');
		filePath.append(
				Environment.getExternalStorageDirectory().getAbsolutePath()
						.toString()).append('/').append("ing");

		// 图片文件名
		filePath.append(MD5.Md5(url)).append(url.substring(index));

		return filePath.toString();
	}

	/**
	 * 解決scrollview中嵌套listview显示不全问题
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	/**
	 * 根据uil获取本地路径
	 * 
	 * @param key
	 * @return
	 */
	public static String createFilePath(String key) {
		try {
			return "/mnt/sdcard/thumbnails" + File.separator + "cache_"
					+ URLEncoder.encode(key.replace("*", ""), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getSecreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		return screenWidth;
	}

	/**
	 * 获取屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getSecreenHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		int screenHeight = dm.heightPixels;
		return screenHeight;
	}

	/**
	 * 
	 * @Description 检查网络状态
	 * @param context
	 * @return boolean
	 */
	public static boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) CLXApplication
				.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	// 获取AppKey
	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				apiKey = metaData.getString(metaKey);
			}
		} catch (NameNotFoundException e) {

		}
		return apiKey;
	}

	/**
	 * 设备型号
	 */
	public static String getModelAndRelease() {
		String model = "Model:" + android.os.Build.MODEL; // 手机型号
		return model;
	}

	/**
	 * 获取系统版本号
	 * 
	 * @return
	 */
	public static String getOS() {
		String release = "Release:" + android.os.Build.VERSION.RELEASE; // android系统版本号
		return "android:" + release;

	}

	/**
	 * 解析聊天内容
	 * 
	 * @param content
	 */
	public static MessageModle getChatModle(String content) {
		String contetn = "";
		String time = "";
		String uid = "";
		String cid = "";
		String name = "";
		String avatarPath = "";
		try {
			JSONObject json = new JSONObject(content);
			contetn = json.getString("c");
			time = json.getString("m");
			uid = json.getString("uid");
			cid = json.getString("cid");
			if (uid.equals(SharedUtils.getString("uid", ""))) {
				return null;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		MemberInfoModle info = DBUtils
				.selectNameAndImgByID("circle" + cid, uid);
		if (info == null) {
			Utils.showToast("未知错误 tableName:" + "circle" + cid + "  uid:" + uid);
		} else {
			avatarPath = info.getAvator();
			name = info.getName();
		}
		MessageModle modle = new MessageModle();
		modle.setContent(contetn);
		modle.setSelf(false);
		modle.setTime(time);
		modle.setAvatar(avatarPath);
		modle.setName(name);
		modle.setCid(cid);
		return modle;
	}
}
