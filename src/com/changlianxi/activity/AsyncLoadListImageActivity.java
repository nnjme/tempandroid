package com.changlianxi.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.adapter.MyAdapter;
import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.MemberModle;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.MyComparator;
import com.changlianxi.util.PinyinUtils;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.MyListView;
import com.changlianxi.view.MyListView.OnRefreshListener;
import com.changlianxi.view.QuickAlphabeticBar;
import com.changlianxi.view.QuickAlphabeticBar.OnTouchingLetterChangedListener;
import com.changlianxi.view.QuickAlphabeticBar.touchUp;

/**
 * 显示圈子成员列表的界面
 * 
 * @author teeker_bin
 * 
 */
public class AsyncLoadListImageActivity extends Activity implements
		OnTouchingLetterChangedListener, touchUp, OnItemClickListener,
		OnClickListener, PostCallBack {
	private QuickAlphabeticBar indexBar;// 右侧字母拦
	private MyAdapter adapter;
	private MyListView listView;
	private TextView selectedChar;// 显示选择字母
	private File cache;// 缓存文件夹
	private List<MemberModle> listModles = new ArrayList<MemberModle>();// 存储成员列表
	private List<MemberModle> serverListModles = new ArrayList<MemberModle>();// 获取服务器成员列表
	private int position;// 当前字母子listview中所对应的位置
	private String id;// 圈子ID
	private String ciecleName;// 圈子名称
	private String circleUser;// 圈子成员表名称
	private ImageView btadd;
	private ImageView btback;
	private TextView txtciecleName;
	private ProgressDialog progressDialog;
	private boolean isnew;// 是否是新邀请的圈子
	private LinearLayout layInvitate;
	private Button btnAccecpt;// 接受邀请按钮
	private Button btnFefuse;// 拒绝邀请按钮

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		id = getIntent().getStringExtra("cirID");
		isnew = getIntent().getBooleanExtra("is_New", false);
		ciecleName = "circle" + id;
		circleUser = ciecleName + "userlist";
		creatDir();
		listModles = DBUtils.getUserList(ciecleName);
		initView();
		setMyAdapter();
		if (Utils.isNetworkAvailable()) {
			new GetUserListTask().execute();
		} else {
			Utils.showToast("请检查网络");
		}
	}

	private void setMyAdapter() {
		adapter = new MyAdapter(AsyncLoadListImageActivity.this, listModles,
				listView);
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
		insertData(modle.getId(), modle.getUid(), modle.getName(),
				modle.getImg(), modle.getEmployer(), modle.getSort_key());
		CLXApplication.setModleNull();
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
		listView.addHeaderView(view);
		indexBar = (QuickAlphabeticBar) findViewById(R.id.indexBar);
		indexBar.setOnTouchingLetterChangedListener(this);
		indexBar.getBackground().setAlpha(125);
		indexBar.setOnTouchUp(this);
		selectedChar = (TextView) findViewById(R.id.selected_tv);
		selectedChar.setVisibility(View.INVISIBLE);
		selectedChar.getBackground().setAlpha(125);
		listView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				listView.onRefreshComplete();
			}
		});
	}

	/**
	 * 插入数据库
	 * 
	 * @param name
	 * @param num
	 */
	private void insertData(String pid, String uid, String name, String img,
			String employer, String sortkey) {
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("personID", pid);
		values.put("userID", uid);
		values.put("userName", name);
		values.put("userImg", img);
		values.put("employer", employer);
		values.put("sortkey", sortkey);
		DBUtils.insertData(ciecleName, values);
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
			Logger.debug(this, "sortKey:" + sortkey + "   " + s);
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
		Logger.debug(this, "potition" + position);
		listView.setSelection(position);
	}

	@Override
	public void onTouchUp() {
		selectedChar.setVisibility(View.GONE);
		listView.setSelection(position);

	}

	/**
	 * 获取成员列表
	 * 
	 */
	class GetUserListTask extends AsyncTask<String, Integer, String> {
		List<MemberModle> meModle = new ArrayList<MemberModle>();

		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("token", SharedUtils.getString("token", ""));
			map.put("timestamp", 0);
			map.put("cid", id);
			String result = HttpUrlHelper.postData(map, "/circles/imembers/"
					+ id);
			try {
				JSONObject jsonobject = new JSONObject(result);
				JSONArray jsonarray = jsonobject.getJSONArray("members");
				if (jsonarray != null) {
					DBUtils.clearTableData(ciecleName);// 清空本地表 保存最新数据
				}
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject object = (JSONObject) jsonarray.opt(i);
					MemberModle modle = new MemberModle();
					String id = object.getString("id");
					String logo = object.getString("avatar");
					String name = object.getString("name");
					String employer = object.getString("employer");
					String uid = object.getString("uid");
					String sortkey = PinyinUtils.getPinyin(name).toUpperCase();
					modle.setId(id);
					modle.setName(name);
					modle.setEmployer(employer);
					modle.setImg(StringUtils.JoinString(logo, "_100x100"));
					modle.setSort_key(sortkey);
					modle.setUid(uid);
					meModle.add(modle);
					insertData(id, uid, name,
							StringUtils.JoinString(logo, "_100x100"), employer,
							sortkey);
				}
				MyComparator compartor = new MyComparator();
				Collections.sort(meModle, compartor);
				serverListModles.addAll(meModle);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			listModles.clear();
			listModles = serverListModles;
			adapter.setData(listModles);
			progressDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			progressDialog = new ProgressDialog(AsyncLoadListImageActivity.this);
			progressDialog.show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int position = arg2 - 2;
		String pid = listModles.get(position).getId();
		Intent it = new Intent();
		it.putExtra("cid", id);
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
			it.setClass(this, AddOneMemberActivity.class);
			it.putExtra("cid", id);
			it.putExtra("type", "add");// 添加成员
			it.putExtra("cirName", txtciecleName.getText().toString());
			startActivity(it);
			break;
		case R.id.btnAccetpt:
			acceptOrRefuse("/circles/iacceptInvitation");
			break;
		case R.id.btnrefuse:
			acceptOrRefuse("/circles/irefuseInvitation");
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
		map.put("cid", id);
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
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}