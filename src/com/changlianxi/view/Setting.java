package com.changlianxi.view;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.changlianxi.R;
import com.changlianxi.activity.AboutActivity;
import com.changlianxi.activity.AdviceFeedBackActivity;
import com.changlianxi.activity.CLXApplication;
import com.changlianxi.activity.ChangePassswordActivity;
import com.changlianxi.activity.NoticesActivity;
import com.changlianxi.activity.ProblemActivity;
import com.changlianxi.popwindow.NewVersionPopWindow;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.FlipperLayout.OnOpenListener;

/**
 * 私信列表展示界面
 * 
 * @author teeker_bin
 * 
 */
public class Setting implements OnClickListener, PostCallBack {
	private Context mContext;
	private View mSetting;
	private OnOpenListener mOnOpenListener;
	private LinearLayout mMenu;
	private LinearLayout revisePasswordWord;
	private LinearLayout adviceFeedBack;
	private LinearLayout newVersion;
	private LinearLayout aboutCLX;
	private LinearLayout problem;
	private LinearLayout notices;
	private Button exitLogin;
	private Dialog dialog;
	private LinearLayout parent;

	public Setting(Context context) {
		this.mContext = context;
		mSetting = LayoutInflater.from(context).inflate(
				R.layout.setting_layout, null);
		initView();
		setListener();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		parent = (LinearLayout) mSetting.findViewById(R.id.parent);
		mMenu = (LinearLayout) mSetting.findViewById(R.id.home_menu);
		revisePasswordWord = (LinearLayout) mSetting
				.findViewById(R.id.revisePasswordWord);
		adviceFeedBack = (LinearLayout) mSetting
				.findViewById(R.id.adviceFeedBack);
		newVersion = (LinearLayout) mSetting.findViewById(R.id.newVersion);
		aboutCLX = (LinearLayout) mSetting.findViewById(R.id.aboutCLX);
		problem = (LinearLayout) mSetting.findViewById(R.id.problem);
		notices = (LinearLayout) mSetting.findViewById(R.id.notices);
		exitLogin = (Button) mSetting.findViewById(R.id.exitLogin);

	}

	private void setListener() {
		mMenu.setOnClickListener(this);
		revisePasswordWord.setOnClickListener(this);
		adviceFeedBack.setOnClickListener(this);
		newVersion.setOnClickListener(this);
		aboutCLX.setOnClickListener(this);
		problem.setOnClickListener(this);
		notices.setOnClickListener(this);
		exitLogin.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.home_menu:
			if (mOnOpenListener != null) {
				mOnOpenListener.open();
			}
			break;
		case R.id.revisePasswordWord:
			intent.setClass(mContext, ChangePassswordActivity.class);
			mContext.startActivity(intent);
			Utils.leftOutRightIn(mContext);
			break;
		case R.id.adviceFeedBack:
			intent.setClass(mContext, AdviceFeedBackActivity.class);
			mContext.startActivity(intent);
			Utils.leftOutRightIn(mContext);
			break;
		case R.id.newVersion:
			getNewVersion();
			break;
		case R.id.aboutCLX:
			intent.setClass(mContext, AboutActivity.class);
			mContext.startActivity(intent);
			Utils.leftOutRightIn(mContext);
			break;
		case R.id.problem:
			intent.setClass(mContext, ProblemActivity.class);
			mContext.startActivity(intent);
			Utils.leftOutRightIn(mContext);
			break;
		case R.id.notices:
			intent.setClass(mContext, NoticesActivity.class);
			mContext.startActivity(intent);
			Utils.leftOutRightIn(mContext);

			break;
		case R.id.exitLogin:
			exit();
			break;
		default:
			break;
		}
	}

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		mOnOpenListener = onOpenListener;
	}

	public View getView() {
		return mSetting;
	}

	/**
	 * 退出
	 */
	private void exit() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		PostAsyncTask task = new PostAsyncTask(mContext, map, "/users/ilogout");
		task.setTaskCallBack(this);
		task.execute();
		dialog = DialogUtil.getWaitDialog(mContext, "正在退出");
		dialog.show();
	}

	private void getNewVersion() {
		dialog = DialogUtil.getWaitDialog(mContext, "检查新版本");
		dialog.show();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		PostAsyncTask task = new PostAsyncTask(mContext, map,
				"/users/inewVersion");
		task.setTaskCallBack(new PostCallBack() {

			@Override
			public void taskFinish(String result) {
				dialog.dismiss();
				String version = Utils.getVersionName(mContext);
				String serverVersion = "";
				String versionLink = "";
				try {
					JSONObject json = new JSONObject(result);
					String rt = json.getString("rt");
					if (rt.equals("1")) {
						serverVersion = json.getString("android");
						versionLink = json.getString("androidLink");
						if (version.equals(serverVersion)) {
							Utils.showToast("当前已是最新版本");
							return;
						}
						NewVersionPopWindow pop = new NewVersionPopWindow(
								mContext, parent, serverVersion, versionLink);
						pop.show();
					} else {
						String err = json.getString("err");
						String errorString = ErrorCodeUtil.convertToChines(err);
						Utils.showToast(errorString);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		task.execute();

	}

	private void finishApp() {
		Utils.showToast("退出");
		SharedUtils.setString("uid", "");
		SharedUtils.setString("token", "");
		SharedUtils.setBoolean("isFristLogin", true);
		// CLXApplication.exit(true);
		// Intent intent = new Intent();
		// intent.setClass(mContext, LoginActivity.class);
		// mContext.startActivity(intent);
	}

	@Override
	public void taskFinish(String result) {
		try {
			JSONObject json = new JSONObject(result);
			int rt = json.getInt("rt");
			if (rt == 1) {
				finishApp();
			} else {
				String err = json.getString("err");
				if (err.equals("TOKEN_INVALID")) {
					finishApp();
					return;
				}
				String errorString = ErrorCodeUtil.convertToChines(err);
				Utils.showToast(errorString);
				dialog.dismiss();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
