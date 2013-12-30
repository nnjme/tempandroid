package com.changlianxi.util;

import java.io.File;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.activity.R;
import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.modle.MessageModle;

/**
 * 公用工具类
 * 
 * @author teeker_bin
 * 
 */
public class Utils {

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
	 * 显示提示信息
	 * 
	 * @param str
	 */
	public static void showViewToast(String str, Context context) {
		Toast toast = new Toast(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(R.layout.toast, null);
		TextView title = (TextView) layout.findViewById(R.id.txt);
		title.setText(str);
		title.getBackground().setAlpha(125);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

	}

	/**
	 * 隐藏软键盘
	 */
	public static void hideSoftInput(Context context) {
		if (context == null) {
			return;
		}
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm == null) {
			return;
		}
		imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
	 * 获取应用的当前版本号
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getVersionName(Context context) {
		String version = "";
		try {

			// 获取packagemanager的实例
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo;
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
			version = packInfo.versionName;

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
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
		int type = 0;
		try {
			JSONObject json = new JSONObject(content);
			contetn = json.getString("c");
			time = json.getString("m");
			uid = json.getString("uid");
			if (uid.equals(SharedUtils.getString("uid", ""))) {
				return null;
			}
			cid = json.getString("cid");
			String ct = json.getString("ct");
			if (ct.equals("TYPE_TEXT")) {
				type = 0;
			} else if (ct.equals("TYPE_IMAGE")) {
				type = 1;
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
		modle.setType(type);
		modle.setUid(uid);
		return modle;
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * 
	 * @param pxValue
	 * @param scale
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	// 设置切换动画，从右边进入，左边退出
	public static void leftOutRightIn(Context context) {
		((Activity) context).overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	/**
	 * 右侧退出
	 * 
	 * @param context
	 */
	public static void rightOut(Context context) {
		((Activity) context).overridePendingTransition(R.anim.right_in,
				R.anim.right_out);

	}
}
