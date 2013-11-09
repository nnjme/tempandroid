package com.changlianxi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class AddCircleMemberActivity extends Activity implements
		OnClickListener {
	private Button add;
	private Button input;
	private ImageView back;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_circle_member);
		back = (ImageView) findViewById(R.id.back);
		add = (Button) findViewById(R.id.addFromAddBook);
		input = (Button) findViewById(R.id.inputContact);
		add.setOnClickListener(this);
		input.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.addFromAddBook:
			intent.setClass(this, SelectContactsActivity.class);
			startActivity(intent);
			break;
		case R.id.inputContact:
			intent.setClass(this, AddOneMemberActivity.class);
			intent.putExtra("type", "create");// ´´½¨È¦×Ó
			startActivity(intent);
			break;
		case R.id.back:
			break;
		default:
			break;
		}
		finish();
	}

}
