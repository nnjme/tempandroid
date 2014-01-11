package com.changlianxi.activity;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

/**
 * 点击圈子之后进入的界面
 * 
 * @author teeker_bin
 * 
 */
@SuppressWarnings("deprecation")
public class CircleActivity extends ActivityGroup implements OnClickListener {
	private Button cy, lt, dt, cz;
	private TabHost mTabHost;// 用来承载activity的TabHost
	public static LinearLayout btParent;
	private String id = "";
	private Intent intent;
	private Intent gintent;// 成长itent
	private Intent chatIntent;
	private Intent newsIntent;
	private String ciecleName;// 圈子名称
	private LinearLayout btnMore;
	private boolean isNew;
	private String type = "";// push 推送跳转
	private static Activity context;
	private String inviterID = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_circle);
		context = this;
		CLXApplication.addActivity(this);
		setInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		id = getIntent().getStringExtra("cirID");
		type = getIntent().getStringExtra("type");
		ciecleName = getIntent().getStringExtra("name");
		isNew = getIntent().getBooleanExtra("is_New", false);
		inviterID = getIntent().getStringExtra("inviterID");
		initIntent();
		initview();
		if (type.equals("push")) {
			mTabHost.setCurrentTab(1);
			setSelectColor(lt);
			return;
		}
		mTabHost.setCurrentTab(0);
	}
	

	private void initview() {
		btParent = (LinearLayout) findViewById(R.id.btParent);
		cy = (Button) findViewById(R.id.cy);
		lt = (Button) findViewById(R.id.lt);
		cy.setOnClickListener(this);
		lt.setOnClickListener(this);
		cz = (Button) findViewById(R.id.cz);
		cz.setOnClickListener(this);
		dt = (Button) findViewById(R.id.dt);
		dt.setOnClickListener(this);
		btnMore = (LinearLayout) findViewById(R.id.more);
		btnMore.setOnClickListener(this);
		mTabHost = (TabHost) findViewById(R.id.tabhost);
		mTabHost.setup(this.getLocalActivityManager());
		mTabHost.addTab(mTabHost.newTabSpec("chengyuan")
				.setIndicator("chengyuan").setContent(intent));
		mTabHost.addTab(mTabHost.newTabSpec("lt").setIndicator("lt")
				.setContent(chatIntent));
		mTabHost.addTab(mTabHost.newTabSpec("cz").setIndicator("cz")
				.setContent(gintent));
		mTabHost.addTab(mTabHost.newTabSpec("dt").setIndicator("dt")
				.setContent(newsIntent));
	}

	private void initIntent() {
		intent = new Intent();
		intent.setClass(this, CircleUserActivity.class);
		intent.putExtra("cirID", id);
		intent.putExtra("is_New", isNew);
		intent.putExtra("inviterID", inviterID);
		intent.putExtra("cirName", ciecleName);
		gintent = new Intent();
		gintent.setClass(this, GrowthActivity.class);
		gintent.putExtra("cirID", id);
		gintent.putExtra("cirName", ciecleName);
		chatIntent = new Intent();
		chatIntent.setClass(this, ChatActivity.class);
		chatIntent.putExtra("cirID", id);
		chatIntent.putExtra("cirName", ciecleName);
		newsIntent = new Intent();
		newsIntent.setClass(this, NewsActivity.class);
		newsIntent.putExtra("cirID", id);
		newsIntent.putExtra("cirName", ciecleName);
	}

	private void setSelectColor(View v) {
		for (int i = 0; i < btParent.getChildCount() - 2; i++) {
			Button bt = (Button) btParent.getChildAt(i);
			if (bt.getId() == v.getId()) {
				bt.setTextColor(Color.BLACK);
			} else {
				bt.setTextColor(getResources().getColor(
						R.color.default_font_color));
			}
		}

	}

	public void finishExit() {
		finish();
		Utils.rightOut(context);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cy:
			mTabHost.setCurrentTab(0);
			setSelectColor(v);
			break;
		case R.id.lt:
			mTabHost.setCurrentTab(1);
			setSelectColor(v);
			break;
		case R.id.cz:
			mTabHost.setCurrentTab(2);
			setSelectColor(v);
			break;
		case R.id.dt:
			mTabHost.setCurrentTab(3);
			setSelectColor(v);
			break;
		case R.id.more:
			Intent intent = new Intent();
			intent.setClass(this, CircleInfoActivity.class);
			intent.putExtra("cid", id);
			startActivityForResult(intent, 1);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

			break;
		default:
			break;
		}
	}

	public static void setInputMode(int mode) {
		context.getWindow().setSoftInputMode(mode);
	}

	@Override
	protected void onResume() {
		SharedUtils.setBoolean("isBackHome", false);// 后台运行
		super.onResume();
		MobclickAgent.onPageStart(getClass().getName() + "");
		// MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		SharedUtils.setBoolean("isBackHome", true);// 后台运行
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getName() + "");
		//MobclickAgent.onPause(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && data != null) {
			boolean flag = data.getBooleanExtra("flag", false);
			if (flag) {
				finish();
				Utils.rightOut(this);

			}
		}
	}
}
