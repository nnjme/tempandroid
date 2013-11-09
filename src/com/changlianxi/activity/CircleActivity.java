package com.changlianxi.activity;

import android.app.ActivityGroup;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.changlianxi.popwindow.CircleSettingPopwindow;
import com.changlianxi.popwindow.CircleSettingPopwindow.ExitCircleCallBack;

/**
 * 点击圈子之后进入的界面
 * 
 * @author teeker_bin
 * 
 */
public class CircleActivity extends ActivityGroup implements OnClickListener {
	private Button cy, lt, dt, cz;
	private TabHost mTabHost;// 用来承载activity的TabHost
	private LinearLayout btParent;
	private String id = "";
	private Intent intent;
	private Intent gintent;// 成长itent
	private Intent chatIntent;
	private Intent newsIntent;
	private String ciecleName;// 圈子名称
	private Button btnMore;
	private boolean isNew;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_circle);
		ciecleName = getIntent().getStringExtra("name");
		id = getIntent().getStringExtra("cirID");
		isNew = getIntent().getBooleanExtra("is_New", false);
		intent = new Intent();
		intent.setClass(this, AsyncLoadListImageActivity.class);
		intent.putExtra("cirID", id);
		intent.putExtra("is_New", isNew);
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
		init();
	}

	private void init() {
		btParent = (LinearLayout) findViewById(R.id.btParent);
		cy = (Button) findViewById(R.id.cy);
		lt = (Button) findViewById(R.id.lt);
		cy.setOnClickListener(this);
		lt.setOnClickListener(this);
		cz = (Button) findViewById(R.id.cz);
		cz.setOnClickListener(this);
		dt = (Button) findViewById(R.id.dt);
		dt.setOnClickListener(this);
		btnMore = (Button) findViewById(R.id.more);
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
		mTabHost.setCurrentTab(0);
	}

	private void setSelectColor(View v) {
		for (int i = 0; i < btParent.getChildCount(); i++) {
			Button bt = (Button) btParent.getChildAt(i);
			if (bt.getId() == v.getId()) {
				bt.setTextColor(Color.BLACK);
			} else {
				bt.setTextColor(getResources().getColor(
						R.color.default_font_color));
			}
		}

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
			CircleSettingPopwindow pop = new CircleSettingPopwindow(this, v,
					id, new ExitCircleCallBack() {
						@Override
						public void exitCircle() {
							finish();
						}
					});
			pop.show();
			break;
		default:
			break;
		}
	}
}
