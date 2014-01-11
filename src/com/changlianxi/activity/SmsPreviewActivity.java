package com.changlianxi.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.changlianxi.adapter.SmsAdaprter;
import com.changlianxi.modle.SmsPrevieModle;
import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;
import com.changlianxi.R;

/**
 * 短信预览界面
 * 
 * @author teeker_bin
 * 
 */
public class SmsPreviewActivity extends BaseActivity implements OnClickListener {
	private ImageView back;
	private ListView listview;
	private List<SmsPrevieModle> contactsList = new ArrayList<SmsPrevieModle>();
	private String cmids;
	private SmsAdaprter adapter;
	private Button btnsend;
	private String cid;
	private LinearLayout title;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms_preview);
		CLXApplication.addInviteActivity(this);
		Bundle bundle = getIntent().getExtras();
		contactsList = (List<SmsPrevieModle>) bundle
				.getSerializable("contactsList");
		cid = getIntent().getStringExtra("cid");
		cmids = getIntent().getStringExtra("cmids");
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(this);
		btnsend = (Button) findViewById(R.id.btnsend);
		btnsend.setOnClickListener(this);
		title = (LinearLayout) findViewById(R.id.title);
		listview = (ListView) findViewById(R.id.listView1);
		adapter = new SmsAdaprter(this, contactsList, title);
		listview.setAdapter(adapter);
	}
	/**设置页面统计
	 * 
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getClass().getName() + "");
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getName() + "");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			Utils.rightOut(this);
			break;
		case R.id.btnsend:
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("contactsList", (Serializable) contactsList);
			intent.putExtras(bundle);
			intent.putExtra("cmids", cmids);
			intent.putExtra("cid", cid);
			intent.setClass(this, SmsInviteActivity.class);
			startActivity(intent);
			// finish();
			// Utils.rightOut(this);
			Utils.leftOutRightIn(this);
			break;
		default:
			break;
		}

	}

}
