package com.changlianxi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.adapter.NewsListAdapter;
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
public class NewsActivity extends Activity implements OnClickListener,
		OnPullDownListener, GetNewsList {
	private ImageView back;
	private TextView cirName;
	private String cid = "";
	private String txtCirName;
	private ProgressDialog pd;
	private PullDownView mPullDownView;
	private NewsListAdapter adapter;
	private List<NewsModle> listModle = new ArrayList<NewsModle>();
	private ListView mListView;
	private String start = "0";
	private String end = "";
	private boolean loadMore;// 是否加载更多
	private boolean isRefresh;// 是否下拉刷新
	private boolean isShowPd = true;// 是否显示进度框

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dt);
		cid = getIntent().getStringExtra("cirID");
		txtCirName = getIntent().getStringExtra("cirName");
		end = DateUtils.phpTime(System.currentTimeMillis());
		findViewByID();
		setListener();
		getSeverNewsList();
	}

	private void findViewByID() {
		back = (ImageView) findViewById(R.id.back);
		cirName = (TextView) findViewById(R.id.circleName);
		mPullDownView = (PullDownView) findViewById(R.id.PullDownlistView);
		adapter = new NewsListAdapter(this, listModle);
		mListView = mPullDownView.getListView();

	}

	private void setListener() {
		back.setOnClickListener(this);
		cirName.setText(txtCirName);
		mPullDownView.setOnPullDownListener(this);
		mListView.setAdapter(adapter);
		// // 设置可以自动获取更多 滑到最后一个自动获取 改成false将禁用自动获取更多
		// mPullDownView.enableAutoFetchMore(false, 1);
		// // 隐藏 并禁用尾部
		// mPullDownView.setHideFooter();
		// // 显示并启用自动获取更多
		// mPullDownView.setShowFooter();
		// // 隐藏并且禁用头部刷新
		// mPullDownView.setHideHeader();
		// // 显示并且可以使用头部刷新
		// mPullDownView.setShowHeader();
		mPullDownView.notifyDidMore();
		mPullDownView.setFooterVisible(false);
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
		GetNewsListTask task = new GetNewsListTask(this, map, "/news/ilist");
		task.setTaskCallBack(this);
		task.execute();
		if (isShowPd) {
			pd = new ProgressDialog(this);
			pd.show();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
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
			listModle.clear();
			listModle = list;
		} else if (loadMore) {
			list.remove(0);
			listModle.addAll(listModle.size(), list);
		} else {
			listModle = list;
		}
		adapter.setData(listModle);
	}

	/** 刷新事件接口 这里要注意的是获取更多完 要关闭 刷新的进度条RefreshComplete() **/
	@Override
	public void onRefresh() {
		isRefresh = true;
		loadMore = false;
		isShowPd = false;
		end = DateUtils.phpTime(System.currentTimeMillis());
		getSeverNewsList();
	}

	/** 刷新事件接口 这里要注意的是获取更多完 要关闭 更多的进度条 notifyDidMore() **/
	@Override
	public void onMore() {
		loadMore = true;
		isShowPd = false;
		isRefresh = false;
		start = "0";
		end = DateUtils.phpTime(DateUtils.convertToDate(listModle.get(
				listModle.size() - 1).getCreatedTime()));
		getSeverNewsList();

	}
}
