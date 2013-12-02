package com.changlianxi.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.changlianxi.activity.AboutActivity;
import com.changlianxi.activity.AdviceFeedBackActivity;
import com.changlianxi.activity.ChangePassswordActivity;
import com.changlianxi.activity.NoticesActivity;
import com.changlianxi.activity.ProblemActivity;
import com.changlianxi.activity.R;
import com.changlianxi.util.Utils;
import com.changlianxi.view.FlipperLayout.OnOpenListener;

/**
 * 私信列表展示界面
 * 
 * @author teeker_bin
 * 
 */
public class Setting implements OnClickListener {
	private Context mContext;
	private View mSetting;
	private OnOpenListener mOnOpenListener;
	private LinearLayout mMenu;
	private LinearLayout setNickName;
	private LinearLayout revisePasswordWord;
	private LinearLayout adviceFeedBack;
	private LinearLayout newVersion;
	private LinearLayout aboutCLX;
	private LinearLayout problem;
	private LinearLayout notices;

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
		mMenu = (LinearLayout) mSetting.findViewById(R.id.home_menu);
		setNickName = (LinearLayout) mSetting.findViewById(R.id.setNickName);
		revisePasswordWord = (LinearLayout) mSetting
				.findViewById(R.id.revisePasswordWord);
		adviceFeedBack = (LinearLayout) mSetting
				.findViewById(R.id.adviceFeedBack);
		newVersion = (LinearLayout) mSetting.findViewById(R.id.newVersion);
		aboutCLX = (LinearLayout) mSetting.findViewById(R.id.aboutCLX);
		problem = (LinearLayout) mSetting.findViewById(R.id.problem);
		notices = (LinearLayout) mSetting.findViewById(R.id.notices);

	}

	private void setListener() {
		mMenu.setOnClickListener(this);
		setNickName.setOnClickListener(this);
		revisePasswordWord.setOnClickListener(this);
		adviceFeedBack.setOnClickListener(this);
		newVersion.setOnClickListener(this);
		aboutCLX.setOnClickListener(this);
		problem.setOnClickListener(this);
		notices.setOnClickListener(this);
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
			break;
		case R.id.adviceFeedBack:
			intent.setClass(mContext, AdviceFeedBackActivity.class);
			mContext.startActivity(intent);
			break;
		case R.id.newVersion:
			Utils.showViewToast("当前版本" + Utils.getVersionName(mContext)
					+ "已是最新版本", mContext);
			break;
		case R.id.aboutCLX:
			intent.setClass(mContext, AboutActivity.class);
			mContext.startActivity(intent);
			break;
		case R.id.problem:
			intent.setClass(mContext, ProblemActivity.class);
			mContext.startActivity(intent);
			break;
		case R.id.notices:
			intent.setClass(mContext, NoticesActivity.class);
			mContext.startActivity(intent);
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

}
