package com.changlianxi.popwindow;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.changlianxi.activity.R;
import com.changlianxi.util.Constants;
import com.changlianxi.util.FileUtils;

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
	private String fileName;

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
	@SuppressWarnings("deprecation")
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

	/**
	 * 返回拍照之后保存路径
	 */
	public String getTakePhotoPath() {
		return fileName;
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
			String dir = "/clx/camera/";
			FileUtils.createDir(dir);
			String name = FileUtils.getFileName() + ".jpg";
			fileName = FileUtils.getgetAbsoluteDir(dir) + name;
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// 下面这句指定调用相机拍照后的照片存储的路径
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(fileName)));
			((Activity) mContext).startActivityForResult(intent,
					Constants.REQUEST_CODE_GETIMAGE_BYCAMERA);
			break;
		default:
			break;
		}
	}
}
