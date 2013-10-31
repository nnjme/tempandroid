package com.changlianxi.popwindow;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.changlianxi.activity.CircleSettingActivity;
import com.changlianxi.activity.R;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;

/**
 * 圈子设置提示框
 * 
 * @author teeker_bin
 * 
 */
public class CircleSettingPopwindow implements OnClickListener {
	private PopupWindow popupWindow;
	private Context mContext;
	private View v;
	private Button btnCircleSet;
	private Button btnCancle;
	private View view;
	private String cid = "";
	private Button btnExitCircle;
	private ExitCircleCallBack exitCir;

	public CircleSettingPopwindow(Context context, View v, String cid,
			ExitCircleCallBack exitCir) {
		this.mContext = context;
		this.v = v;
		this.cid = cid;
		this.exitCir = exitCir;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.cricle_setting_dialog, null);
		initView();
		initPopwindow();
	}

	private void initView() {
		btnCancle = (Button) view.findViewById(R.id.btn_cancel);
		btnCircleSet = (Button) view.findViewById(R.id.btn_circle_setting);
		btnExitCircle = (Button) view.findViewById(R.id.btn_exit_circle);
		btnExitCircle.setOnClickListener(this);
		btnCancle.setOnClickListener(this);
		btnCircleSet.setOnClickListener(this);
	}

	/**
	 * 初始化popwindow
	 */
	private void initPopwindow() {
		popupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
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

	class ExitCircleTask extends AsyncTask<String, Integer, String> {
		ProgressDialog pd;
		// 可变长的输入参数，与AsyncTask.exucute()对应
		String rt = "1";

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cid", cid);
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("token", SharedUtils.getString("token", ""));
			result = HttpUrlHelper.postData(map, "/circles/iquit");
			Logger.debug(this, result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			pd.dismiss();
			try {
				JSONObject object = new JSONObject(result);
				int rt = object.getInt("rt");
				if (rt == 1) {
					Utils.showToast("退出成功!");
					exitCir.exitCircle();
				} else {
					Utils.showToast("退出失败!");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			pd = new ProgressDialog(mContext);
			pd.show();
		}
	}

	@Override
	public void onClick(View v) {
		dismiss();
		switch (v.getId()) {
		case R.id.btn_cancel:
			break;
		case R.id.btn_circle_setting:
			Intent intent = new Intent();
			intent.setClass(mContext, CircleSettingActivity.class);
			intent.putExtra("cid", cid);
			mContext.startActivity(intent);
			break;
		case R.id.btn_exit_circle:
			new ExitCircleTask().execute();
			break;
		default:
			break;
		}
	}

	/**
	 * 退出圈子接口
	 * 
	 * @author teeker_bin
	 * 
	 */
	public interface ExitCircleCallBack {
		public void exitCircle();
	}
}
