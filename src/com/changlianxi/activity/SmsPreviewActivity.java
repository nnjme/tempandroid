package com.changlianxi.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.changlianxi.adapter.SmsAdaprter;
import com.changlianxi.modle.ContactModle;
import com.changlianxi.util.Logger;

/**
 * ∂Ã–≈‘§¿¿ΩÁ√Ê
 * 
 * @author teeker_bin
 * 
 */
public class SmsPreviewActivity extends Activity implements OnClickListener {
	private ImageView back;
	private ListView listview;
	private List<ContactModle> contactsList = new ArrayList<ContactModle>();
	private String circleName;
	private SmsAdaprter adapter;
	private Button btnsend;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms_preview);
		Bundle bundle = getIntent().getExtras();
		contactsList = (List<ContactModle>) bundle
				.getSerializable("contactsList");
		Logger.debug(this, "contactsList:" + contactsList.size());
		circleName = getIntent().getStringExtra("circleName");
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(this);
		btnsend = (Button) findViewById(R.id.btnsend);
		btnsend.setOnClickListener(this);
		listview = (ListView) findViewById(R.id.listView1);
		adapter = new SmsAdaprter(this, contactsList, circleName);
		listview.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.btnsend:
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("contactsList", (Serializable) contactsList);
			intent.putExtras(bundle);
			intent.setClass(this, SmsInviteActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}

	}

}
