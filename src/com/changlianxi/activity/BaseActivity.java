package com.changlianxi.activity;

import com.changlianxi.util.Utils;

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
