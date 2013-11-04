package com.changlianxi.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.adapter.MyAdapter;
import com.changlianxi.db.DBUtils;
import com.changlianxi.db.DataBase;
import com.changlianxi.modle.MemberModle;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.HttpUtil;
import com.changlianxi.util.Logger;
import com.changlianxi.util.MyComparator;
import com.changlianxi.util.PinyinUtils;
import com.changlianxi.util.SharedUtils;
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
		OnClickListener {
	private QuickAlphabeticBar indexBar;// 右侧字母拦
	private DataBase dbase = DataBase.getInstance();
	private SQLiteDatabase db;
	private MyAdapter adapter;
	private MyListView listView;
	private TextView selectedChar;// 显示选择字母
	private File cache;// 缓存文件夹
	private List<MemberModle> listModles = new ArrayList<MemberModle>();// 存储成员列表
	private List<MemberModle> serverListModles = new ArrayList<MemberModle>();// 获取服务器成员列表
	int position;// 当前字母子listview中所对应的位置
	private String id;// 圈子ID
	private String ciecleName;// 圈子名称
	private String circleUser;// 圈子成员表名称
	private ImageView btadd;
	private ImageView btback;
	private TextView txtciecleName;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		id = getIntent().getStringExtra("cirID");
		ciecleName = "circle" + id;
		circleUser = ciecleName + "userlist";
		db = dbase.getWritableDatabase();
		creatTable();
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

	private void creatTable() {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		// 创建圈子所对应的表
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ ciecleName
				+ " ( _id integer PRIMARY KEY AUTOINCREMENT ,userID varchar,userName varchar, userImg varchar,employer varchar,sortkey varchar)");
		db.execSQL("create table IF NOT EXISTS "
				+ circleUser
				+ "( _id integer PRIMARY KEY AUTOINCREMENT ,tID varchar,personID varchar,key varchar, value varchar,startDate varchar,endDate)");
		// 创建缓存目录，系统一运行就得创建缓存目录的，
		cache = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "clxcache");
		if (!cache.exists()) {
			cache.mkdir();
		}
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
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
	private void insertData(String id, String name, String img,
			String employer, String sortkey) {
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("userID", id);
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
			String jsonStr = HttpUtil.queryStringForGet(HttpUrlHelper.strUrl
					+ "/circles/imembers/" + id);
			try {
				JSONObject jsonobject = new JSONObject(jsonStr);
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
					String sortkey = PinyinUtils.getPinyin(name);
					modle.setId(id);
					modle.setName(name);
					modle.setEmployer(employer);
					modle.setImg(logo);
					modle.setSort_key(sortkey);
					meModle.add(modle);
					insertData(id, name, logo, employer, sortkey);
				}
				MyComparator compartor = new MyComparator();
				Collections.sort(meModle, compartor);
				// listModles.addAll(meModle);
				serverListModles.addAll(meModle);
			} catch (JSONException e) {
				Logger.error(this, e);
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			listModles.clear();
			listModles = serverListModles;
			adapter.setData(listModles);
			// adapter.notifyDataSetChanged();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == 2) {
			{
				MemberModle modle = null;
				Bundle bundle = data.getExtras();
				modle = (MemberModle) bundle.getSerializable("modle");
				System.out.println("patha:" + modle.getImg());
				listModles.add(modle);
				MyComparator compartor = new MyComparator();
				Collections.sort(listModles, compartor);
				adapter.setData(listModles);
				insertData(modle.getId(), modle.getName(), modle.getImg(),
						modle.getEmployer(), modle.getSort_key());
			}

		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int position = arg2 - 2;
		String pid = listModles.get(position).getId();
		Logger.debug(
				this,
				"cid:" + id + "uid:" + SharedUtils.getString("uid", "")
						+ "pid:" + pid + "token:"
						+ SharedUtils.getString("token", ""));

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
			startActivityForResult(it, 1);
			break;
		default:
			break;
		}
	}

}