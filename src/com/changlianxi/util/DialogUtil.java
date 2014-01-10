package com.changlianxi.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.changlianxi.R;

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

}