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
 * ��̬����
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
	private boolean loadMore;// �Ƿ���ظ���
	private boolean isRefresh;// �Ƿ�����ˢ��
	private boolean isShowPd = true;// �Ƿ���ʾ���ȿ�

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
		// // ���ÿ����Զ���ȡ���� �������һ���Զ���ȡ �ĳ�false�������Զ���ȡ����
		// mPullDownView.enableAutoFetchMore(false, 1);
		// // ���� ������β��
		// mPullDownView.setHideFooter();
		// // ��ʾ�������Զ���ȡ����
		// mPullDownView.setShowFooter();
		// // ���ز��ҽ���ͷ��ˢ��
		// mPullDownView.setHideHeader();
		// // ��ʾ���ҿ���ʹ��ͷ��ˢ��
		// mPullDownView.setShowHeader();
		mPullDownView.notifyDidMore();
		mPullDownView.setFooterVisible(false);
	}

	/**
	 * ��ȡ��̬�б�
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

	/** ˢ���¼��ӿ� ����Ҫע����ǻ�ȡ������ Ҫ�ر� ˢ�µĽ�����RefreshComplete() **/
	@Override
	public void onRefresh() {
		isRefresh = true;
		loadMore = false;
		isShowPd = false;
		end = DateUtils.phpTime(System.currentTimeMillis());
		getSeverNewsList();
	}

	/** ˢ���¼��ӿ� ����Ҫע����ǻ�ȡ������ Ҫ�ر� ����Ľ����� notifyDidMore() **/
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
