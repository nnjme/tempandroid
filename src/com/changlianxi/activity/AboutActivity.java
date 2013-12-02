package com.changlianxi.activity;

import com.changlianxi.util.Utils;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends Activity implements OnClickListener {
	private ImageView back;
	private TextView titleTxt;
	private TextView version;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_about);
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(this);
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText("关于");
		version = (TextView) findViewById(R.id.version);
		version.setText("常联系 " + Utils.getVersionName(this) + " For Android");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}

}
