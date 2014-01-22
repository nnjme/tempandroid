package com.changlianxi.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.util.Utils;

/**
 * 关于界面
 * 
 * @author teeker_bin
 * 
 */
public class AboutActivity extends BaseActivity implements OnClickListener {
	private ImageView back;
	private TextView titleTxt;
	private TextView version;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
			Utils.rightOut(this);
			break;

		default:
			break;
		}
	}

}
