package com.changlianxi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.util.Utils;

public class AddCircleMemberActivity extends BaseActivity implements
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
		setContentView(R.layout.activity_add_circle_member);
		CLXApplication.addInviteActivity(this);
		back = (ImageView) findViewById(R.id.back);
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText("添加第一批成员");
		add = (Button) findViewById(R.id.addFromAddBook);
		input = (Button) findViewById(R.id.inputContact);
		add.setOnClickListener(this);
		input.setOnClickListener(this);
		back.setOnClickListener(this);
		type = getIntent().getStringExtra("type");
		if (type.equals("add")) {
			titleTxt.setText("添加成员");
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
			// overridePendingTransition(R.anim.push_bottom_in,
			// R.anim.out_to_left);
			Utils.leftOutRightIn(this);
			break;
		case R.id.inputContact:
			intent.setClass(this, AddOneMemberActivity.class);
			intent.putExtra("cid", cid);
			intent.putExtra("cirName", cirName);
			intent.putExtra("type", type);
			startActivity(intent);
			Utils.leftOutRightIn(this);
			break;
		case R.id.back:
			finish();
			Utils.rightOut(this);
			break;
		default:
			break;
		}
		// finish();
		// Utils.rightOut(this);
	}

}
