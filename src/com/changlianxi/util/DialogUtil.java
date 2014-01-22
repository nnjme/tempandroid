package com.changlianxi.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.inteface.ConfirmDialog;

public class DialogUtil {
	public static Dialog getWaitDialog(Context context, String str) {

		final Dialog dialog = new Dialog(context, R.style.Dialog);
		dialog.setContentView(R.layout.firset_dialog_view);
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		int screenW = Utils.getSecreenWidth(context);
		lp.width = (int) (0.4 * screenW);
		lp.height = (int) (0.4 * screenW);
		TextView titleTxtv = (TextView) dialog.findViewById(R.id.tvLoad);
		titleTxtv.setText(str + "...");
		return dialog;
	}

	/**
	 * 确认对话框
	 * 
	 * @param context
	 * @param title
	 * @param content
	 */
	public static Dialog confirmDialog(Context context, String title,
			String content, final ConfirmDialog callBack) {
		Dialog alertDialog = new AlertDialog.Builder(context).setTitle(title)
				.setMessage(content)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						callBack.onOKClick();

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						callBack.onCancleClick();
					}
				}).create();
		return alertDialog;

	}
}