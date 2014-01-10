package com.changlianxi.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.adapter.NewsCommentsListAdapter;
import com.changlianxi.modle.NewsComments;
import com.changlianxi.task.GetCommentsForMeTask;
import com.changlianxi.task.GetCommentsForMeTask.GetCommentsForMeCallBack;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.R;

/**
 * ��ȡȦ�ӳɳ��и�����ص������б�����
 * 
 * @author teeker_bin
 * 
 */
public class CommentsListActivity extends BaseActivity implements
		OnClickListener, GetCommentsForMeCallBack, OnItemClickListener {
	private ImageView back;
	private ListView listView;
	private TextView title;
	private String cid = "";
	private NewsCommentsListAdapter adapter;
	private List<NewsComments> listModles = new ArrayList<NewsComments>();
	private Dialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comments_list);
		cid = getIntent().getStringExtra("cid");
		initView();
		setListener();
		setAdapter();
		getComments();
	}

	private void initView() {
		back = (ImageView) findViewById(R.id.back);
		listView = (ListView) findViewById(R.id.listView);
		title = (TextView) findViewById(R.id.titleTxt);
		title.setText("��Ϣ�б�");

	}

	private void setListener() {
		back.setOnClickListener(this);
		listView.setOnItemClickListener(this);
	}

	public void setAdapter() {
		adapter = new NewsCommentsListAdapter(this, listModles);
		listView.setAdapter(adapter);
	}

	private void getComments() {
		dialog = DialogUtil.getWaitDialog(this, "���Ժ�");
		dialog.show();
		GetCommentsForMeTask task = new GetCommentsForMeTask(this, cid,
				SharedUtils.getString("exitGrowthListTime", "0"),
				DateUtils.phpTime(System.currentTimeMillis()));
		task.setTaskCallBack(this);
		task.execute();

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

	@Override
	public void getCommentList(List<NewsComments> listModle) {
		if (dialog != null) {
			dialog.dismiss();
		}
		listModles.addAll(listModle);
		adapter.setDate(listModles);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Intent intent = new Intent();
		intent.putExtra("gid", listModles.get(position).getGid());
		intent.putExtra("cid", cid);
		intent.setClass(this, CommentsListItemActivity.class);
		startActivity(intent);
		finish();
		Utils.leftOutRightIn(this);

	}

	@Override
	protected void onDestroy() {
		SharedUtils.setString("exitGrowthListTime",
				DateUtils.phpTime(System.currentTimeMillis()));
		super.onDestroy();
	}
}
