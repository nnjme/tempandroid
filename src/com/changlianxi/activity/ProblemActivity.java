package com.changlianxi.activity;

import com.changlianxi.util.Utils;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 常见问题
 * 
 * @author teeker_bin
 * 
 */
public class ProblemActivity extends BaseActivity {
	private ImageView back;
	private TextView titleTxt;
	private WebView wb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_problem);
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				Utils.rightOut(ProblemActivity.this);

			}
		});
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText("常见问题");
		wb = (WebView) findViewById(R.id.webView1);
		wb.loadUrl("file:///android_asset/problem.html");
	}

}
