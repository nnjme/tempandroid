package com.changlianxi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AddCircleMemberActivity extends Activity implements
		OnClickListener {
	private Button add;
	private Button input;
	private ImageView back;
	private String type;
	private String cid;
	private String cirName;
	private TextView titleTxt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_circle_member);
		back = (ImageView) findViewById(R.id.back);
		titleTxt=(TextView)findViewById(R.id.titleTxt);
		titleTxt.setText("添加第一批成员");
		add = (Button) findViewById(R.id.addFromAddBook);
		input = (Button) findViewById(R.id.inputContact);
		add.setOnClickListener(this);
		input.setOnClickListener(this);
		back.setOnClickListener(this);
		type = getIntent().getStringExtra("type");
		if (type.equals("add")) {
			cid = getIntent().getStringExtra("cid");
			cirName = getIntent().getStringExtra("cirName");
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.addFromAddBook:
			intent.setClass(this, SelectContactsActivity.class);
			intent.putExtra("type", type);
			intent.putExtra("cid", cid);
			intent.putExtra("cirName", cirName);
			startActivity(intent);
			break;
		case R.id.inputContact:
			intent.setClass(this, AddOneMemberActivity.class);
			intent.putExtra("cid", cid);
			intent.putExtra("cirName", cirName);
			intent.putExtra("type", type);
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
