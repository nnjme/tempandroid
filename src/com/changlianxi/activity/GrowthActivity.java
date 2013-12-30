package com.changlianxi.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.activity.GrowthCommentActivity.RecordOperation;
import com.changlianxi.adapter.GrowthAdapter;
import com.changlianxi.modle.GrowthModle;
import com.changlianxi.task.GetGrowthListTask;
import com.changlianxi.task.GetGrowthListTask.GroGrowthList;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.view.PullDownView;
import com.changlianxi.view.PullDownView.OnPullDownListener;

/**
 * 成长记录显示界面
 * 
 * @author teeker_bin
 * 
 */
public class GrowthActivity extends BaseActivity implements OnClickListener,
		GroGrowthList, OnItemClickListener, OnPullDownListener {
	private String cid = "";
	private List<GrowthModle> listData = new ArrayList<GrowthModle>();
	private PullDownView mPullDownView;
	private ListView mListView;
	private Dialog progressDialog;
	private GrowthAdapter adapter;
	private ImageView btnRelease;// 发布成长按钮
	private String circleName;
	private TextView txtCirName;
	private ImageView btback;
	private String start = "";
	private String end = "";
	private boolean loadMore;// 是否加载更多
	private boolean isRefresh;// 是否下拉刷新

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_czjl);
		end = DateUtils.phpTime(System.currentTimeMillis());
		cid = getIntent().getStringExtra("cirID");
		circleName = getIntent().getStringExtra("cirName");
		mPullDownView = (PullDownView) findViewById(R.id.PullDownlistView);
		mListView = mPullDownView.getListView();
		adapter = new GrowthAdapter(this, listData);
		mListView.setAdapter(adapter);
		// mListView.setCacheColorHint(0);
		btnRelease = (ImageView) findViewById(R.id.btnRelease);
		txtCirName = (TextView) findViewById(R.id.circleName);
		txtCirName.setText(circleName);
		btback = (ImageView) findViewById(R.id.back);
		setListener();
		getGrowthList();
	}

	@Override
	protected void onRestart() {
		end = DateUtils.phpTime(System.currentTimeMillis());
		getGrowthList();
		super.onRestart();
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
		if (listData.size() == 0) {
			progressDialog = DialogUtil.getWaitDialog(this, "请稍后");
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
			startActivityForResult(it, 2);
			getParent().overridePendingTransition(R.anim.in_from_right,
					R.anim.out_to_left);
			// startActivity(it);
			break;
		case R.id.back:
			finish();
			this.getParent().overridePendingTransition(R.anim.right_in,
					R.anim.right_out);
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
	public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
		int position = arg2 - 1;
		GrowthModle modle = listData.get(position);
		Intent intent = new Intent(this, GrowthCommentActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("modle", (Serializable) modle);
		intent.putExtras(bundle);
		intent.putExtra("position", position);
		startActivity(intent);
		this.getParent().overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
		GrowthCommentActivity.setRecordOperation(new RecordOperation() {
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

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2 && data != null) {
			boolean flag = data.getBooleanExtra("flag", false);
			if (flag) {
				end = DateUtils.phpTime(System.currentTimeMillis());
				// 发布完记录 重新加载数据
				getGrowthList();
			}
		} else if (requestCode == 3 && data != null) {

		}
	}

	@Override
	public void onRefresh() {
		isRefresh = true;
		loadMore = false;
		end = DateUtils.phpTime(System.currentTimeMillis());
		getGrowthList();
	}

	@Override
	public void onMore() {
		loadMore = true;
		isRefresh = false;
		start = "0";
		end = DateUtils.phpTime(DateUtils.convertToDate(listData.get(
				listData.size() - 1).getPublish()));
		getGrowthList();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			getParent().overridePendingTransition(R.anim.right_in,
					R.anim.right_out);
		}
		return super.onKeyDown(keyCode, event);

	}
}
