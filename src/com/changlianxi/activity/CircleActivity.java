package com.changlianxi.activity;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;

import com.changlianxi.util.ResolutionPushJson;

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
	private TextView cy, lt, dt, cz;
	private TextView ltPrompt, dtPrompt, czPrompt;
	private RelativeLayout layCY, layLT, layDT, layCZ;
	private TabHost mTabHost;// 用来承载activity的TabHost
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
	private int newGrowthCount = 0;// 新成长数、
	private int newChatCount = 0;// 新聊天数、
	private int newDynamicCount = 0;// 新动态数、
	private int newCommentCount = 0;// 新评论数。
	private boolean firstLT = true;// 聊天Tab
	private boolean firstDT = true;// 动态tab
	private boolean firstCZ = true;// 成长tab
	private LinearLayout layTab;

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
		newGrowthCount = getIntent().getIntExtra("newGrowthCount", 0);
		newChatCount = getIntent().getIntExtra("newChatCount", 0);
		newDynamicCount = getIntent().getIntExtra("newDynamicCount", 0);
		newCommentCount = getIntent().getIntExtra("newCommentCount", 0);
		initIntent();
		initview();
		if (type.equals("push")) {
			mTabHost.setCurrentTab(2);
			setSelectColor(lt, cy, dt, cz);
			return;
		}
		mTabHost.setCurrentTab(0);
	}
	

	private void initview() {
		layTab = (LinearLayout) findViewById(R.id.layTabParent);
		cy = (TextView) findViewById(R.id.cy);
		lt = (TextView) findViewById(R.id.lt);
		cz = (TextView) findViewById(R.id.cz);
		dt = (TextView) findViewById(R.id.dt);
		ltPrompt = (TextView) findViewById(R.id.ltPrompt);
		dtPrompt = (TextView) findViewById(R.id.dtPrompt);
		czPrompt = (TextView) findViewById(R.id.czPrompt);
		layCY = (RelativeLayout) findViewById(R.id.layCY);
		layCZ = (RelativeLayout) findViewById(R.id.layCZ);
		layDT = (RelativeLayout) findViewById(R.id.layDT);
		layLT = (RelativeLayout) findViewById(R.id.layLT);
		layCY.setOnClickListener(this);
		layCZ.setOnClickListener(this);
		layDT.setOnClickListener(this);
		layLT.setOnClickListener(this);
		btnMore = (LinearLayout) findViewById(R.id.more);
		btnMore.setOnClickListener(this);
		mTabHost = (TabHost) findViewById(R.id.tabhost);
		mTabHost.setup(this.getLocalActivityManager());
		mTabHost.addTab(mTabHost.newTabSpec("chengyuan")
				.setIndicator("chengyuan").setContent(intent));
		mTabHost.addTab(mTabHost.newTabSpec("cz").setIndicator("cz")
				.setContent(gintent));
		mTabHost.addTab(mTabHost.newTabSpec("lt").setIndicator("lt")
				.setContent(chatIntent));
		mTabHost.addTab(mTabHost.newTabSpec("dt").setIndicator("dt")
				.setContent(newsIntent));
		setPrompt();
		registerBoradcastReceiver();
	}

	private void setPrompt() {
		if (newChatCount > 0) {
			ltPrompt.setVisibility(View.VISIBLE);
			ltPrompt.setText(newChatCount + "");
		}
		if (newGrowthCount + newCommentCount > 0) {
			czPrompt.setVisibility(View.VISIBLE);
			czPrompt.setText(newGrowthCount + newCommentCount + "");
		}
		if (newDynamicCount > 0) {
			dtPrompt.setVisibility(View.VISIBLE);
			dtPrompt.setText(newDynamicCount + "");
		}
	}

	private void hidePrompt(View v) {
		v.setVisibility(View.GONE);

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
		gintent.putExtra("commentCount", newCommentCount);
		chatIntent = new Intent();
		chatIntent.setClass(this, ChatActivity.class);
		chatIntent.putExtra("cirID", id);
		chatIntent.putExtra("cirName", ciecleName);
		newsIntent = new Intent();
		newsIntent.setClass(this, NewsActivity.class);
		newsIntent.putExtra("cirID", id);
		newsIntent.putExtra("cirName", ciecleName);
	}

	private void setSelectColor(TextView v1, TextView v2, TextView v3,
			TextView v4) {
		v1.setTextColor(getResources().getColor(R.color.black));
		v2.setTextColor(getResources().getColor(R.color.tabfond));
		v3.setTextColor(getResources().getColor(R.color.tabfond));
		v4.setTextColor(getResources().getColor(R.color.tabfond));

	}

	public void finishExit() {
		finish();
		Utils.rightOut(context);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layCY:
			mTabHost.setCurrentTab(0);
			setSelectColor(cy, lt, dt, cz);
			break;
		case R.id.layLT:
			if (firstLT) {
				sendBroad(id, newChatCount, 2);
				newChatCount = 0;

			}
			mTabHost.setCurrentTab(2);
			setSelectColor(lt, cy, dt, cz);
			firstLT = false;
			hidePrompt(ltPrompt);
			layTab.setVisibility(View.GONE);

			break;
		case R.id.layCZ:
			if (firstCZ) {
				sendBroad(id, newGrowthCount, 1);
				newGrowthCount = 0;
			}
			mTabHost.setCurrentTab(1);
			setSelectColor(cz, lt, dt, cy);
			firstCZ = false;
			hidePrompt(czPrompt);
			layTab.setVisibility(View.GONE);

			break;
		case R.id.layDT:
			if (firstDT) {
				sendBroad(id, newDynamicCount, 3);
				newDynamicCount = 0;

			}
			mTabHost.setCurrentTab(3);
			setSelectColor(dt, lt, cy, cz);
			firstDT = false;
			hidePrompt(dtPrompt);
			layTab.setVisibility(View.GONE);

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
	
	private void sendBroad(String cid, int promptCount, int position) {
		Intent intent = new Intent(Constants.REMOVE_PROMPT_COUNT);
		intent.putExtra("promptCount", promptCount);
		intent.putExtra("position", position);
		intent.putExtra("cid", cid);
		BroadCast.sendBroadCast(this, intent);

	}

	/**
	 * 注册该广播
	 */
	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(Constants.PUSH_TYPE);
		myIntentFilter.addAction(Constants.CHANGE_TAB);
		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	/**
	 * 定义广播
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Constants.PUSH_TYPE)) {// 推送消息
				String cid = intent.getStringExtra("cid");
				String type = intent.getStringExtra("type");
				int promptCount = intent.getIntExtra("promptCount", 0);
				if (!id.equals(cid)) {
					return;
				}
				// setPromptCount(type, promptCount);
			} else if (action.equals(Constants.CHANGE_TAB)) {// 切换tab页
				mTabHost.setCurrentTab(0);
				layTab.setVisibility(View.VISIBLE);
				setSelectColor(cy, lt, dt, cz);

			}
		}
	};

	private void setPromptCount(String type, int promptCount) {
		if (type.equals(ResolutionPushJson.COMMENT_TYPE)
				|| type.equals(ResolutionPushJson.GROWTH_TYPE)) {
			czPrompt.setVisibility(View.VISIBLE);
			czPrompt.setText(newGrowthCount + newCommentCount + promptCount
					+ "");
		} else if (type.equals(ResolutionPushJson.NEW_TYPE)) {
			dtPrompt.setVisibility(View.VISIBLE);
			dtPrompt.setText(newDynamicCount + promptCount + "");
		} else if (type.equals(ResolutionPushJson.CHAT_TYPE)) {
			ltPrompt.setVisibility(View.VISIBLE);
			ltPrompt.setText(newChatCount + promptCount + "");
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
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
