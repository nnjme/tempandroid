package com.changlianxi.popwindow;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.changlianxi.R;

/**
 * 下载新版本
 * 
 * @author teeker_bin
 * 
 */
public class NewVersionPopWindow implements OnClickListener {
	private PopupWindow popupWindow;
	private Context mContext;
	private View v;
	private View view;
	private LinearLayout bg;
	private Button ok;
	private Button cancle;
	private TextView version;
	private String strVersion;
	private String versionLink;

	public NewVersionPopWindow(Context context, View v, String strVersion,
			String link) {
		this.mContext = context;
		this.v = v;
		this.strVersion = strVersion;
		this.versionLink = link;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.new_version, null);
		initView();
		initPopwindow();
	}

	private void initView() {
		bg = (LinearLayout) view.findViewById(R.id.layoutBg);
		bg.getBackground().setAlpha(150);
		ok = (Button) view.findViewById(R.id.ok);
		cancle = (Button) view.findViewById(R.id.cancle);
		ok.setOnClickListener(this);
		cancle.setOnClickListener(this);
		version = (TextView) view.findViewById(R.id.txtVersion);
		version.setText(strVersion);
	}

	/**
	 * 初始化popwindow
	 */
	@SuppressWarnings("deprecation")
	private void initPopwindow() {
		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		// 这个是为了点击�?返回Back”也能使其消失，并且并不会影响你的背景（很神奇的�?
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	/**
	 * popwindow的显�?
	 */
	public void show() {
		popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
		// popupWindow.showAsDropDown(v);
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 刷新状�?
		popupWindow.update();
	}

	// 隐藏
	public void dismiss() {
		popupWindow.dismiss();
	}

	class ViewHolder {
		TextView text;
		LinearLayout laybg;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok:
			Intent intent = new Intent();
			intent.setAction("com.changlianxi.service.versionservice");
			intent.putExtra("url", versionLink);
			mContext.startService(intent);
			dismiss();
			break;
		case R.id.cancle:
			dismiss();
			break;
		default:
			break;
		}

	}
}
