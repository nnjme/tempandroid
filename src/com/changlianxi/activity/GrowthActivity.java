package com.changlianxi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.adapter.GrowthAdapter;
import com.changlianxi.modle.GrowthModle;
import com.changlianxi.popwindow.GrowthCommentsPopwindow;
import com.changlianxi.popwindow.GrowthCommentsPopwindow.RecordOperation;
import com.changlianxi.task.GetGrowthListTask;
import com.changlianxi.task.GetGrowthListTask.GroGrowthList;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.view.PullDownView;
import com.changlianxi.view.PullDownView.OnPullDownListener;

/**
 * 成长记录显示界面
 * 
 * @author teeker_bin
 * 
 */
public class GrowthActivity extends Activity implements OnClickListener,
		GroGrowthList, OnItemClickListener, OnPullDownListener {
	private String cid = "";
	private List<GrowthModle> listData = new ArrayList<GrowthModle>();
	private PullDownView mPullDownView;
	private ListView mListView;
	private ProgressDialog progressDialog;
	private GrowthAdapter adapter;
	private ImageView btnRelease;// 发布成长按钮
	private String circleName;
	private TextView txtCirName;
	private ImageView btback;
	private String start = "0";
	private String end = "";
	private boolean loadMore;// 是否加载更多
	private boolean isRefresh;// 是否下拉刷新
	private boolean isShowPd = true;// 是否显示进度框

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_czjl);
		cid = getIntent().getStringExtra("cirID");
		circleName = getIntent().getStringExtra("cirName");
		mPullDownView = (PullDownView) findViewById(R.id.PullDownlistView);
		mListView = mPullDownView.getListView();
		adapter = new GrowthAdapter(this, listData);
		mListView.setAdapter(adapter);
		mListView.setCacheColorHint(0);
		btnRelease = (ImageView) findViewById(R.id.btnRelease);
		txtCirName = (TextView) findViewById(R.id.circleName);
		txtCirName.setText(circleName);
		btback = (ImageView) findViewById(R.id.back);
		setListener();
	}

	private void setListener() {
		mPullDownView.setOnPullDownListener(this);
		mListView.setAdapter(adapter);
		mPullDownView.notifyDidMore();
		mPullDownView.setFooterVisible(false);
		btback.setOnClickListener(this);
		btnRelease.setOnClickListener(this);
		mListView.setOnItemClickListener(this);

	}

	/**
	 * 发布完记录 重新加载数据
	 */
	@Override
	protected void onStart() {
		listData.clear();
		getGrowthList();
		super.onStart();
	}

	private void getGrowthList() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cid", cid);
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("start", start);
		map.put("end", end);
		GetGrowthListTask task = new GetGrowthListTask(map);
		task.setTaskCallBack(this);
		task.execute();
		if (isShowPd) {
			progressDialog = new ProgressDialog(this);
			progressDialog.show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRelease:
			Intent it = new Intent();
			it.setClass(this, ReleaseGrowthActivity.class);
			it.putExtra("cid", cid);
			it.putExtra("type", "add");
			startActivity(it);
			break;
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void getGrowthList(List<GrowthModle> list) {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		mPullDownView.notifyDidMore();
		mPullDownView.RefreshComplete();
		if (list.size() == 0) {
			return;
		}
		if (list.size() == 20) {
			mPullDownView.setFooterVisible(true);
		}
		if (isRefresh) {
			listData.clear();
			listData = list;
		} else if (loadMore) {
			list.remove(0);
			listData.addAll(listData.size(), list);
		} else {
			listData = list;
		}
		adapter.setData(listData);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		GrowthCommentsPopwindow pop = new GrowthCommentsPopwindow(this, v,
				listData.get(position - 1), position - 1);
		pop.setRecordOperation(new RecordOperation() {
			@Override
			public void delRecord(int pisition) {
				listData.remove(pisition);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void setComment(int position, String count) {
				listData.get(position).setComment(Integer.valueOf(count));
				adapter.notifyDataSetChanged();

			}
		});
		pop.show();
		pop.show();

	}

	@Override
	public void onRefresh() {
		isRefresh = true;
		loadMore = false;
		isShowPd = false;
		end = DateUtils.phpTime(System.currentTimeMillis());
		getGrowthList();
	}

	@Override
	public void onMore() {
		loadMore = true;
		isShowPd = false;
		isRefresh = false;
		start = "0";
		end = DateUtils.phpTime(DateUtils.convertToDate(listData.get(
				listData.size() - 1).getPublish()));
		getGrowthList();
	}

}
