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
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;

/**
 * 动态界面
 * 
 * @author teeker_bin
 * 
 */
public class NewsActivity extends Activity implements OnClickListener,
		GetNewsList {
	private ImageView back;
	private TextView cirName;
	private String cid = "";
	private String txtCirName;
	private ProgressDialog pd;
	private ListView listview;
	private NewsListAdapter adapter;
	private List<NewsModle> listModle = new ArrayList<NewsModle>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dt);
		cid = getIntent().getStringExtra("cirID");
		txtCirName = getIntent().getStringExtra("cirName");
		findViewByID();
		setListener();
		getSeverNewsList();
	}

	private void findViewByID() {
		back = (ImageView) findViewById(R.id.back);
		cirName = (TextView) findViewById(R.id.circleName);
		listview = (ListView) findViewById(R.id.listView1);
		adapter = new NewsListAdapter(this, listModle);
	}

	private void setListener() {
		back.setOnClickListener(this);
		cirName.setText(txtCirName);
		listview.setAdapter(adapter);
	}

	/**
	 * 获取动态列表
	 */
	private void getSeverNewsList() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("cid", cid);
		map.put("timestamp", System.currentTimeMillis());
		pd = new ProgressDialog(this);
		pd.show();
		GetNewsListTask task = new GetNewsListTask(this, map, "/news/ilist");
		task.setTaskCallBack(this);
		task.execute();

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
		pd.dismiss();
		listModle = list;
		adapter.setData(listModle);
		Logger.debug(this, "size:" + listModle.size());
	}
}
