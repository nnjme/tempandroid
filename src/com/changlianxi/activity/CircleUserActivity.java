package com.changlianxi.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.adapter.MyAdapter;
import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.MemberModle;
import com.changlianxi.popwindow.UserSortPopwindow;
import com.changlianxi.task.GetCircleUserTask;
import com.changlianxi.task.GetCircleUserTask.GetCircleUserList;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.MyComparator;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.Home;
import com.changlianxi.view.MyListView;
import com.changlianxi.view.MyListView.OnRefreshListener;
import com.changlianxi.view.QuickAlphabeticBar;
import com.changlianxi.view.QuickAlphabeticBar.OnTouchingLetterChangedListener;
import com.changlianxi.view.QuickAlphabeticBar.touchUp;
import com.changlianxi.view.SearchEditText;

/**
 * 显示圈子成员列表的界面
 * 
 * @author teeker_bin
 * 
 */
public class CircleUserActivity extends Activity implements
		OnTouchingLetterChangedListener, touchUp, OnItemClickListener,
		OnClickListener, PostCallBack, GetCircleUserList {
	private QuickAlphabeticBar indexBar;// 右侧字母拦
	private MyAdapter adapter;
	private MyListView listView;
	private TextView selectedChar;// 显示选择字母
	private File cache;// 缓存文件夹
	private List<MemberModle> listModles = new ArrayList<MemberModle>();// 存储成员列表
	private List<MemberModle> searchListModles = new ArrayList<MemberModle>();// 存储搜索列表
	private int position;// 当前字母子listview中所对应的位置
	private String cid;// 圈子ID
	private String circleName;// 圈子名称
	private String circleUser;// 圈子成员表名称
	private ImageView btadd;
	private ImageView btback;
	private TextView txtciecleName;
	private ProgressDialog progressDialog;
	private boolean isnew;// 是否是新邀请的圈子
	private LinearLayout layInvitate;
	private Button btnAccecpt;// 接受邀请按钮
	private Button btnFefuse;// 拒绝邀请按钮
	private ImageView px;// 排序
	private int status;// 0 拒绝 1 接受

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		cid = getIntent().getStringExtra("cirID");
		isnew = getIntent().getBooleanExtra("is_New", false);
		circleName = "circle" + cid;
		circleUser = circleName + "userlist";
		creatDir();
		listModles = DBUtils.getUserList(circleName);
		initView();
		setMyAdapter();
		getServerList();

	}

	private void getServerList() {
		if (Utils.isNetworkAvailable()) {
			if (listModles.size() == 0) {
				progressDialog = new ProgressDialog(this);
				progressDialog.show();
			}
			GetCircleUserTask task = new GetCircleUserTask(cid, circleName);
			task.setTaskCallBack(this);
			task.execute();
		} else {
			Utils.showToast("请检查网络");
		}
	}

	private void setMyAdapter() {
		adapter = new MyAdapter(CircleUserActivity.this, listModles);
		listView.setAdapter(adapter);
	}

	private void creatDir() {
		// 创建缓存目录，系统一运行就得创建缓存目录的，
		cache = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "clxcache");
		if (!cache.exists()) {
			cache.mkdir();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		MemberModle modle = CLXApplication.getModle();
		if (modle == null) {
			return;
		}
		listModles.add(modle);
		MyComparator compartor = new MyComparator();
		Collections.sort(listModles, compartor);
		adapter.setData(listModles);
		DBUtils.insertCircleUser(circleName, modle.getId(), modle.getUid(),
				modle.getName(), modle.getImg(), modle.getEmployer(),
				modle.getSort_key());
		CLXApplication.setModleNull();
	}

	@Override
	protected void onResume() {
		super.onResume();
		CircleActivity
				.setInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {

		btnAccecpt = (Button) findViewById(R.id.btnAccetpt);
		btnFefuse = (Button) findViewById(R.id.btnrefuse);
		btnAccecpt.setOnClickListener(this);
		btnFefuse.setOnClickListener(this);
		layInvitate = (LinearLayout) findViewById(R.id.layInvitate);
		if (isnew) {
			layInvitate.setVisibility(View.VISIBLE);
		}
		btback = (ImageView) findViewById(R.id.back);
		btadd = (ImageView) findViewById(R.id.btadd);
		btadd.setOnClickListener(this);
		btback.setOnClickListener(this);
		txtciecleName = (TextView) findViewById(R.id.circleName);
		String name = getIntent().getStringExtra("cirName");
		txtciecleName.setText(name);
		listView = (MyListView) findViewById(R.id.cy_list);
		listView.setCacheColorHint(0);
		listView.setOnItemClickListener(this);
		MyComparator compartor = new MyComparator();
		Collections.sort(listModles, compartor);
		View view = LayoutInflater.from(this).inflate(R.layout.header, null);
		SearchEditText editSearch = (SearchEditText) view
				.findViewById(R.id.search);
		editSearch.addTextChangedListener(new EditWather());
		listView.addHeaderView(view);
		indexBar = (QuickAlphabeticBar) findViewById(R.id.indexBar);
		indexBar.setOnTouchingLetterChangedListener(this);
		indexBar.getBackground().setAlpha(125);
		indexBar.setOnTouchUp(this);
		selectedChar = (TextView) findViewById(R.id.selected_tv);
		selectedChar.setVisibility(View.INVISIBLE);
		selectedChar.getBackground().setAlpha(80);
		listView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				listView.onRefreshComplete();
			}
		});
		px = (ImageView) findViewById(R.id.px);
		px.setOnClickListener(this);
	}

	class EditWather implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {
			String key = s.toString().toLowerCase();
			if (s.length() == 0) {
				adapter.setData(listModles);
				indexBar.setVisibility(View.VISIBLE);
				return;
			}
			indexBar.setVisibility(View.GONE);
			searchListModles.clear();
			for (int i = 0; i < listModles.size(); i++) {
				String name = listModles.get(i).getName();
				String pinyin = listModles.get(i).getSort_key().toLowerCase();
				String pinyinFir = listModles.get(i).getKey_pinyin_fir();
				if (name.contains(key) || pinyin.contains(key)
						|| pinyinFir.contains(key)) {
					MemberModle modle = listModles.get(i);
					searchListModles.add(modle);

				}
			}
			adapter.setData(searchListModles);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

	}

	/**
	 * 设置listview的当前选中值
	 * 
	 * @param s
	 * @return
	 */
	public int findIndexer(String s) {
		int position = 0;
		for (int i = 0; i < listModles.size(); i++) {
			String sortkey = listModles.get(i).getSort_key();
			if (sortkey.startsWith(s)) {
				position = i;
				break;
			}
		}
		return position;
	}

	@Override
	public void onTouchingLetterChanged(String s) {
		selectedChar.setText(s);
		selectedChar.setVisibility(View.VISIBLE);
		position = (findIndexer(s)) + 2;
		listView.setSelection(position);
	}

	@Override
	public void onTouchUp() {
		selectedChar.setVisibility(View.GONE);
		listView.setSelection(position);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int position = arg2 - 2;
		String pid = listModles.get(position).getId();
		Intent it = new Intent();
		it.putExtra("cid", cid);
		it.putExtra("pid", pid);
		it.putExtra("username", listModles.get(position).getName());
		it.putExtra("userlistname", circleUser);
		it.setClass(this, UserInfoActivity.class);
		it.putExtra("iconImg", listModles.get(position).getImg());
		startActivity(it);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.btadd:
			Intent it = new Intent();
			it.setClass(this, AddCircleMemberActivity.class);
			it.putExtra("cid", cid);
			it.putExtra("type", "add");// 添加成员
			it.putExtra("cirName", txtciecleName.getText().toString());
			startActivity(it);
			break;
		case R.id.btnAccetpt:
			acceptOrRefuse("/circles/iacceptInvitation");
			status = 1;
			break;
		case R.id.btnrefuse:
			acceptOrRefuse("/circles/irefuseInvitation");
			status = 0;
			break;
		case R.id.px:
			UserSortPopwindow pop = new UserSortPopwindow(this, v);
			pop.show();
			break;
		default:
			break;
		}
	}

	/**
	 * 同意或者拒绝邀请加入圈子
	 * 
	 * @param url
	 */
	private void acceptOrRefuse(String url) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("cid", cid);
		PostAsyncTask task = new PostAsyncTask(this, map, url);
		task.setTaskCallBack(this);
		task.execute();
		progressDialog = new ProgressDialog(this);
		progressDialog.dismiss();
	}

	@Override
	protected void onDestroy() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		DBUtils.close();
		super.onDestroy();
	}

	@Override
	public void taskFinish(String result) {
		layInvitate.setVisibility(View.GONE);
		progressDialog.dismiss();
		try {
			JSONObject json = new JSONObject(result);
			int rt = json.getInt("rt");
			if (rt != 1) {
				String errorCode = json.getString("err");
				String err = ErrorCodeUtil.convertToChines(errorCode);
				Utils.showToast(err);
				return;
			}
			if (status == 1) {
				Home.acceptOrRefuseInvite(cid, false);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getCircleUserList(List<MemberModle> listModle) {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		if (listModle.size() == 0) {
			return;
		}
		listModles.clear();
		listModles = listModle;
		adapter.setData(listModles);
	}
}