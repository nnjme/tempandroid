package com.changlianxi.popwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.changlianxi.activity.R;

/**
 * 短信邀请界面 设置昵称弹出框
 * 
 * @author teeker_bin
 * 
 */
public class SmsSetNickNamePopWindow {
	private PopupWindow popupWindow;
	private Context mContext;
	private View view;
	private View v;
	private Button btnSetNickName;
	private int width;

	public SmsSetNickNamePopWindow(Context context, View v,
			OnClickListener lister, int width) {
		this.mContext = context;
		view = LayoutInflater.from(mContext).inflate(
				R.layout.sms_nickname_popwindow, null);
		this.v = v;
		this.width = width;
		btnSetNickName = (Button) view.findViewById(R.id.btnSetNickName);
		btnSetNickName.setOnClickListener(lister);
		initPopwindow();
	}

	/**
	 * 初始化popwindow
	 */
	@SuppressWarnings("deprecation")
	private void initPopwindow() {
		popupWindow = new PopupWindow(view, width, LayoutParams.WRAP_CONTENT);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// popupWindow.setAnimationStyle(R.style.AnimBottom);
	}

	/**
	 * popwindow的显示
	 */
	public void show() {
		popupWindow.showAsDropDown(v, -20, 0);
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 刷新状态
		popupWindow.update();
	}

	// 隐藏
	public void dismiss() {
		popupWindow.dismiss();
	}
}
