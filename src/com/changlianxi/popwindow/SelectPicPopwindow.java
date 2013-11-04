package com.changlianxi.popwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.changlianxi.activity.R;
import com.changlianxi.util.Constants;

/**
 * 选择图片 拍照 选择框
 * 
 * @author teeker_bin
 * 
 */
public class SelectPicPopwindow implements OnClickListener {
	private PopupWindow popupWindow;
	private Context mContext;
	private View v;
	private Button btnTakePhoto;
	private Button btnPickPhoto;
	private Button btnCancle;
	private View view;

	public SelectPicPopwindow(Context context, View v) {
		this.mContext = context;
		this.v = v;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.alert_dialog, null);
		initView();
		initPopwindow();
	}

	private void initView() {
		btnCancle = (Button) view.findViewById(R.id.btn_cancel);
		btnPickPhoto = (Button) view.findViewById(R.id.btn_pick_photo);
		btnTakePhoto = (Button) view.findViewById(R.id.btn_take_photo);
		btnCancle.setOnClickListener(this);
		btnPickPhoto.setOnClickListener(this);
		btnTakePhoto.setOnClickListener(this);
	}

	/**
	 * 初始化popwindow
	 */
	private void initPopwindow() {
		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setAnimationStyle(R.style.AnimBottom);
	}

	/**
	 * popwindow的显示
	 */
	public void show() {
		popupWindow.showAtLocation(v, Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL, 0, 0);
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

	@Override
	public void onClick(View v) {
		dismiss();
		switch (v.getId()) {
		case R.id.btn_cancel:
			break;
		case R.id.btn_pick_photo:
			Intent it = new Intent(Intent.ACTION_GET_CONTENT);
			it.setType("image/*");
			((Activity) mContext).startActivityForResult(it,
					Constants.REQUEST_CODE_GETIMAGE_BYSDCARD);
			break;
		case R.id.btn_take_photo:
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			((Activity) mContext).startActivityForResult(intent,
					Constants.REQUEST_CODE_GETIMAGE_BYCAMERA);
			break;
		default:
			break;
		}
	}

}
