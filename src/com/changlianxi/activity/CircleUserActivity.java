package com.changlianxi.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.changlianxi.task.GetCircleUserTask;
import com.changlianxi.task.GetCircleUserTask.GetCircleUserList;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
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
import com.umeng.analytics.MobclickAgent;

/**
 * 显示圈子成员列表的界面
 * 
 * @author teeker_bin
 * 
 */
public class CircleUserActivity extends BaseActivity implements
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
	private ImageView btadd;
	private ImageView btback;
	private TextView txtciecleName;
	private Dialog progressDialog;
	private boolean isnew;// 是否是新邀请的圈子
	private LinearLayout layInvitate;
	private Button btnAccecpt;// 接受邀请按钮
	private Button btnFefuse;// 拒绝邀请按钮
	private int status;// 0 拒绝 1 接受
	private SearchEditText editSearch;
	private String inviterID = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		cid = getIntent().getStringExtra("cirID");
		isnew = getIntent().getBooleanExtra("is_New", false);
		inviterID = getIntent().getStringExtra("inviterID");
		circleName = "circle" + cid;
		creatDir();
		listModles = DBUtils.getUserList(circleName);
		initView();
		setMyAdapter();
		getServerList();

	}
	

	private void getServerList() {
		if (Utils.isNetworkAvailable()) {
			if (listModles.size() == 0) {
				progressDialog = DialogUtil.getWaitDialog(this, "请稍后");
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
		getServerList();

	}

	@Override
	protected void onResume() {
		super.onResume();
		CircleActivity
				.setInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		MobclickAgent.onPageStart(getClass().getName() + "");
		// MobclickAgent.onResume(this);
	}

	/**
	 * 数据统计
	 */

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getName() + "");
		// MobclickAgent.onPause(this);
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
			TextView inviteName = (TextView) findViewById(R.id.inviterName);
			String iName = DBUtils.getUserNameByUid(circleName, inviterID);
			inviteName.setText(iName + "邀请你加入圈子");
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
		editSearch = (SearchEditText) view.findViewById(R.id.search);
		editSearch.addTextChangedListener(new EditWather());
		listView.addHeaderView(view);
		indexBar = (QuickAlphabeticBar) findViewById(R.id.indexBar);
		indexBar.setOnTouchingLetterChangedListener(this);
		indexBar.getBackground().setAlpha(0);
		indexBar.setOnTouchUp(this);
		selectedChar = (TextView) findViewById(R.id.selected_tv);
		selectedChar.setVisibility(View.INVISIBLE);
		listView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				listView.onRefreshComplete();
			}
		});
	}

	class EditWather implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {
			String key = s.toString().toLowerCase();
			if (key.length() == 0) {
				Utils.hideSoftInput(CircleUserActivity.this);
				adapter.setData(listModles);
				indexBar.setVisibility(View.VISIBLE);
				editSearch.setCompoundDrawables(null, null, null, null);
				return;
			}
			Drawable del = getResources().getDrawable(R.drawable.del);
			del.setBounds(0, 0, del.getMinimumWidth(), del.getMinimumHeight());
			editSearch.setCompoundDrawables(null, null, del, null);
			indexBar.setVisibility(View.GONE);
			searchListModles.clear();
			for (int i = 0; i < listModles.size(); i++) {
				String name = listModles.get(i).getName();
				String pinyin = listModles.get(i).getSort_key().toLowerCase();
				String pinyinFir = listModles.get(i).getKey_pinyin_fir();
				String mobileNum = listModles.get(i).getMobileNum();
				if (name.contains(key) || pinyin.contains(key)
						|| pinyinFir.contains(key) || mobileNum.contains(key)) {
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
		indexBar.getBackground().setAlpha(200);
		selectedChar.setText(s);
		selectedChar.setVisibility(View.VISIBLE);
		position = (findIndexer(s)) + 2;
		listView.setSelection(position);
	}

	@Override
	public void onTouchUp() {
		indexBar.getBackground().setAlpha(0);
		selectedChar.setVisibility(View.GONE);
		listView.setSelection(position);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int position = arg2 - 2;
		String pid = "";
		String uid = "";
		String iconImg = "";
		String username = "";
		if (searchListModles.size() > 0) {
			uid = searchListModles.get(position).getUid();
			iconImg = searchListModles.get(position).getImg();
			username = searchListModles.get(position).getName();
			pid = searchListModles.get(position).getId();

		} else {
			uid = listModles.get(position).getUid();
			iconImg = listModles.get(position).getImg();
			username = listModles.get(position).getName();
			pid = listModles.get(position).getId();
		}
		Intent it = new Intent();
		it.setClass(this, UserInfoActivity.class);
		it.putExtra("cid", cid);
		it.putExtra("pid", pid);
		it.putExtra("uid", uid);
		it.putExtra("username", username);
		it.putExtra("iconImg", iconImg);
		startActivity(it);
		this.getParent().overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);

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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			this.getParent().overridePendingTransition(R.anim.right_in,
					R.anim.right_out);
			break;
		case R.id.btadd:
			Intent it = new Intent();
			it.setClass(this, AddCircleMemberActivity.class);
			it.putExtra("cid", cid);
			it.putExtra("type", "add");// 添加成员
			it.putExtra("cirName", txtciecleName.getText().toString());
			startActivity(it);
			this.getParent().overridePendingTransition(R.anim.in_from_right,
					R.anim.out_to_left);

			break;
		case R.id.btnAccetpt:
			acceptOrRefuse("/circles/iacceptInvitation");
			status = 1;
			break;
		case R.id.btnrefuse:
			acceptOrRefuse("/circles/irefuseInvitation");
			status = 0;
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
		progressDialog = DialogUtil.getWaitDialog(this, "请稍后");
		progressDialog.show();
	}

	@Override
	protected void onDestroy() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
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
			} else {
				Home.exitCircle(cid);
				finish();
				this.getParent().overridePendingTransition(R.anim.right_in,
						R.anim.right_out);
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