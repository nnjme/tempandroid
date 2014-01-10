package com.changlianxi.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.activity.GrowthCommentActivity.RecordOperation;
import com.changlianxi.adapter.GrowthAdapter;
import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.GrowthModle;
import com.changlianxi.task.GetGrowthListTask;
import com.changlianxi.task.GetGrowthListTask.GroGrowthList;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
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
public class GrowthActivity extends BaseActivity implements OnClickListener,
		GroGrowthList, OnItemClickListener, OnPullDownListener {
	private String cid = "";
	private List<GrowthModle> listData = new ArrayList<GrowthModle>();
	private PullDownView mPullDownView;
	private ListView mListView;
	private GrowthAdapter adapter;
	private ImageView btnRelease;// 发布成长按钮
	private String circleName;
	private TextView txtCirName;
	private ImageView btback;
	private String start = "";
	private String end = "";
	private boolean loadMore;// 是否加载更多
	private boolean isRefresh;// 是否下拉刷新
	private TextView promptCount;
	private int commentCount;
	private boolean firstLoad = true;
	private GetGrowthListTask task;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_czjl);
		end = DateUtils.phpTime(System.currentTimeMillis());
		cid = getIntent().getStringExtra("cirID");
		circleName = getIntent().getStringExtra("cirName");
		commentCount = getIntent().getIntExtra("commentCount", 0);
		initView();
		setListener();
		listData = DBUtils.getGrowthList(cid);
		setAdapter();
	}

	private void setAdapter() {
		adapter = new GrowthAdapter(this, listData);
		mListView.setAdapter(adapter);
		mPullDownView.Refresh();
		end = DateUtils.phpTime(System.currentTimeMillis());
		getGrowthList();
	}

	@Override
	protected void onRestart() {
		isRefresh = false;
		loadMore = false;
		listData.clear();
		end = DateUtils.phpTime(System.currentTimeMillis());
		getGrowthList();
		super.onRestart();
	}

	private void initView() {
		mPullDownView = (PullDownView) findViewById(R.id.PullDownlistView);
		mListView = mPullDownView.getListView();
		btnRelease = (ImageView) findViewById(R.id.btnRelease);
		txtCirName = (TextView) findViewById(R.id.circleName);
		txtCirName.setText(circleName);
		btback = (ImageView) findViewById(R.id.back);
		promptCount = (TextView) findViewById(R.id.promptCount);
		if (commentCount > 0) {
			promptCount.setVisibility(View.VISIBLE);
			promptCount.setText(commentCount + "条回复消息");
		}
	}

	private void setListener() {
		mPullDownView.setOnPullDownListener(this);
		mListView.setAdapter(adapter);
		mPullDownView.notifyDidMore();
		mPullDownView.setFooterVisible(false);
		btback.setOnClickListener(this);
		btnRelease.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
		promptCount.setOnClickListener(this);

	}

	private void getGrowthList() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cid", cid);
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("start", start);
		map.put("end", end);
		task = new GetGrowthListTask(map);
		task.setTaskCallBack(this);
		task.execute();

	}

	@Override
	public void onClick(View v) {
		Intent it = new Intent();
		switch (v.getId()) {
		case R.id.btnRelease:
			it.setClass(this, ReleaseGrowthActivity.class);
			it.putExtra("cid", cid);
			it.putExtra("type", "add");
			startActivityForResult(it, 2);
			getParent().overridePendingTransition(R.anim.in_from_right,
					R.anim.out_to_left);
			break;
		case R.id.back:
			finish();
			this.getParent().overridePendingTransition(R.anim.right_in,
					R.anim.right_out);
			break;
		case R.id.promptCount:
			it.setClass(this, CommentsListActivity.class);
			it.putExtra("cid", cid);
			startActivity(it);
			getParent().overridePendingTransition(R.anim.in_from_right,
					R.anim.out_to_left);
			promptCount.setVisibility(View.GONE);
			// Home.remorePromptCount(cid, commentCount, 4);
			Intent intent = new Intent();
			intent.setAction(Constants.REMOVE_PROMPT_COUNT);
			intent.putExtra("promptCount", commentCount);
			intent.putExtra("position", 4);
			intent.putExtra("cid", cid);
			BroadCast.sendBroadCast(this, intent);
			break;
		default:
			break;
		}
	}

	@Override
	public void getGrowthList(List<GrowthModle> list) {
		firstLoad = false;
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
	public void onRefresh() {
		isRefresh = true;
		loadMore = false;
		end = DateUtils.phpTime(System.currentTimeMillis());
		if (firstLoad) {
			return;
		}
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

	@Override
	protected void onDestroy() {
		if (task != null && task.getStatus() == Status.RUNNING) {
			task.cancel(true); // 如果Task还在运行，则先取消它
		}
		DBUtils.delGrowthById(cid);
		int size = listData.size() > 20 ? 20 : listData.size();
		for (int i = 0; i < size; i++) {
			GrowthModle modle = listData.get(i);
			JSONArray jsonAry = new JSONArray();
			JSONObject jsonObj = new JSONObject();

			try {
				for (int j = 0; j < modle.getImgModle().size(); j++) {
					jsonObj.put("img", modle.getImgModle().get(j).getImg());
					jsonAry.put(jsonObj);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DBUtils.saveGrowth(cid, modle.getName(), modle.getPersonImg(),
					modle.getId(), modle.getUid(), modle.getContent(),
					modle.getLocation(), modle.getHappen(), modle.getPublish(),
					modle.getPraise(), modle.getComment(),
					modle.isIspraise() == true ? 1 : 0, jsonAry.toString());

		}
		super.onDestroy();
	}
}
