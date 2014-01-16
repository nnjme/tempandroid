package com.changlianxi.activity;

import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

public class BaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CLXApplication.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	/**
	 * 数据统计
	 */
	@Override
	    protected void onResume() {
	    	// TODO Auto-generated method stub
	    	super.onResume();
	    	MobclickAgent.onPageStart(getClass().getName() + "");
	    MobclickAgent.onResume(this);
	    }
	    @Override
	    protected void onPause() {
	    	// TODO Auto-generated method stub
	    	super.onPause();
	    	MobclickAgent.onPageEnd(getClass().getName() + "");
	    	MobclickAgent.onPause(this);
	    }

	public void exit() {
		finish();
		Utils.rightOut(this);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			Utils.rightOut(this);
		}
		return super.onKeyDown(keyCode, event);

	}
}
