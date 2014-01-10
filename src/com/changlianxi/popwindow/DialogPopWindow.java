package com.changlianxi.popwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.util.Utils;

/**
 * 圈子设置提示�?
 * 
 * @author teeker_bin
 * 
 */
public class DialogPopWindow implements OnClickListener {
	private PopupWindow popupWindow;
	private Context mContext;
	private View v;
	private View view;
	private OnButtonOnclick callback;
	private LinearLayout bg;
	private EditText editNickName;
	private Button ok;
	private Button cancle;

	public DialogPopWindow(Context context, View v) {
		this.mContext = context;
		this.v = v;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.dialog_popwindow, null);
		initView();
		initPopwindow();
	}

	private void initView() {
		bg = (LinearLayout) view.findViewById(R.id.layoutBg);
		bg.getBackground().setAlpha(200);
		editNickName = (EditText) view.findViewById(R.id.editNickName);
		ok = (Button) view.findViewById(R.id.ok);
		cancle = (Button) view.findViewById(R.id.cancle);
		ok.setOnClickListener(this);
		cancle.setOnClickListener(this);
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
		popupWindow.showAsDropDown(v);
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

	public void setOnlistOnclick(OnButtonOnclick callback) {
		this.callback = callback;
	}

	public interface OnButtonOnclick {
		void onclick(String str);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok:
			String nickname = editNickName.getText().toString();
			if (nickname.length() == 0) {
				Utils.showToast("请输入昵称");
				return;
			}
			callback.onclick(nickname);
			break;
		case R.id.cancle:
			break;
		default:
			break;
		}
		dismiss();

	}
}
