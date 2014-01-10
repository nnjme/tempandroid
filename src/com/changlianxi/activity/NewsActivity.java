package com.changlianxi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.adapter.NewsListAdapter;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.GetNewsList;
import com.changlianxi.modle.NewsModle;
import com.changlianxi.task.GetNewsListTask;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.view.PullDownView;
import com.changlianxi.view.PullDownView.OnPullDownListener;

/**
 * 动态界面
 * 
 * @author teeker_bin
 * 
 */
public class NewsActivity extends BaseActivity implements OnClickListener,
		OnPullDownListener, GetNewsList {
	private ImageView back;
	private TextView cirName;
	private String cid = "";
	private String txtCirName;
	private Dialog pd;
	private PullDownView mPullDownView;
	private NewsListAdapter adapter;
	private List<NewsModle> listModle = new ArrayList<NewsModle>();
	private ListView mListView;
	private String start = "0";
	private String end = "";
	private boolean loadMore;// 是否加载更多
	private boolean isRefresh;// 是否下拉刷新
	// private LinearLayout newsWarn;
	// private TextView newsCount;
	private GetNewsListTask task;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				// newsWarn.setVisibility(View.GONE);
				break;
			case 1:
				getNewsListFromDB();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dt);
		cid = getIntent().getStringExtra("cirID");
		txtCirName = getIntent().getStringExtra("cirName");
		end = DateUtils.phpTime(System.currentTimeMillis());
		findViewByID();
		setListener();
		mHandler.sendEmptyMessageDelayed(1, 50);
	}

	private void findViewByID() {
		back = (ImageView) findViewById(R.id.back);
		cirName = (TextView) findViewById(R.id.titleTxt);
		mPullDownView = (PullDownView) findViewById(R.id.PullDownlistView);
		adapter = new NewsListAdapter(this, listModle);
		mListView = mPullDownView.getListView();
		mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// newsWarn = (LinearLayout) findViewById(R.id.newsWarn);
		// newsCount = (TextView) findViewById(R.id.newsCount);
	}

	private void setListener() {
		back.setOnClickListener(this);
		cirName.setText(txtCirName);
		mPullDownView.setOnPullDownListener(this);
		mListView.setAdapter(adapter);
		mPullDownView.setHideFooter();

	}

	private void getNewsListFromDB() {
		listModle = DBUtils.getNewsList(cid);
		if (listModle.size() > 0) {
			if (listModle.size() > 19) {
				mPullDownView.setShowFooter();
			}
			adapter.setData(listModle);
			mPullDownView.notifyDidMore();
			// return;
		}
		mPullDownView.Refresh();
		getSeverNewsList();
	}

	/**
	 * 获取动态列表
	 */
	private void getSeverNewsList() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("cid", cid);
		map.put("start", start);
		map.put("end", end);
		task = new GetNewsListTask(this, map, "/news/ilist", cid);
		task.setTaskCallBack(this);
		task.execute();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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
	public void getNewsList(List<NewsModle> list) {
		if (pd != null) {
			pd.dismiss();
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
			// listModle.clear();
			// listModle = list;
			list.remove(0);
			list.addAll(0, list);
			// newsWarn.setVisibility(View.VISIBLE);
			// newsCount.setText(list.size() + "条新动态");
			// mHandler.sendEmptyMessageDelayed(0, 1000);
		} else if (loadMore) {
			list.remove(0);
			listModle.addAll(listModle.size(), list);
		} else {
			listModle = list;
		}
		if (listModle.size() > 19) {
			mPullDownView.setShowFooter();
		}
		adapter.setData(listModle);

	}

	/** 刷新事件接口 这里要注意的是获取更多完 要关闭 刷新的进度条RefreshComplete() **/
	@Override
	public void onRefresh() {
		isRefresh = true;
		loadMore = false;
		if (listModle.size() > 0) {
			start = DateUtils.phpTime(DateUtils.convertToDate(listModle.get(0)
					.getCreatedTime()));
		} else {
			start = DateUtils.phpTime(System.currentTimeMillis());
		}
		end = "0";
		getSeverNewsList();
	}

	/** 刷新事件接口 这里要注意的是获取更多完 要关闭 更多的进度条 notifyDidMore() **/
	@Override
	public void onMore() {
		loadMore = true;
		isRefresh = false;
		start = "0";
		end = DateUtils.phpTime(DateUtils.convertToDate(listModle.get(
				listModle.size() - 1).getCreatedTime()));
		getSeverNewsList();

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
		DBUtils.delNewsList(cid);
		int size = listModle.size() > 20 ? 20 : listModle.size();
		for (int i = 0; i < size; i++) {
			NewsModle modle = listModle.get(i);
			DBUtils.insertNews(cid, modle.getId(), modle.getUser1(),
					modle.getUser2(), modle.getPerson2(),
					modle.getCreatedTime(), modle.getContent(),
					modle.getDetail(), modle.getUser1Name(),
					modle.getUser2Name(), modle.getAvatarUrl(),
					modle.getNeed_approve());
		}
		super.onDestroy();
	}

}
