package com.changlianxi.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.activity.AddCircleMemberActivity;
import com.changlianxi.activity.CircleActivity;
import com.changlianxi.activity.R;
import com.changlianxi.adapter.CircleAdapter;
import com.changlianxi.db.DBUtils;
import com.changlianxi.db.DataBase;
import com.changlianxi.modle.CircleModle;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.BounceScrollView.OnRefreshComplete;
import com.changlianxi.view.FlipperLayout.OnOpenListener;

/**
 * 圈子首页界面的view
 * 
 * @author teeker_bin
 * 
 */
public class Home implements OnClickListener, OnRefreshComplete {
	private Context mcontext;
	private View mHome;
	private LinearLayout mMenu;
	private OnOpenListener mOnOpenListener;
	private static List<CircleModle> listModle = new ArrayList<CircleModle>();
	private List<CircleModle> serverListModle = new ArrayList<CircleModle>();
	private GridView gView;
	private static CircleAdapter adapter;
	private ImageView createCircle;
	private BounceScrollView scrollView;
	private EditText search;
	private static DataBase dbase = DataBase.getInstance();
	private static SQLiteDatabase db;

	public Home(Context context) {
		mcontext = context;
		db = dbase.getWritableDatabase();
		mHome = LayoutInflater.from(context).inflate(R.layout.home, null);
		findViewById();
		setListener();
		listModle = DBUtils.getCircleList();
		showAdapter();
	}

	private void showAdapter() {
		CircleModle modle = new CircleModle();
		modle.setCirIcon("addroot");
		modle.setCirName("新建圈子");
		serverListModle.add(modle);
		listModle.add(modle);
		adapter = new CircleAdapter(mcontext, listModle, gView,
				(Activity) mcontext);
		gView.setAdapter(adapter);
		if (Utils.isNetworkAvailable()) {
			new GetCircleListTask().execute();
		} else {
			Utils.showToast("请检查网络");
		}
	}

	/**
	 * 获取成员列表
	 * 
	 */
	class GetCircleListTask extends AsyncTask<String, Integer, String> {
		ProgressDialog progressDialog;

		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("token", SharedUtils.getString("token", ""));
			map.put("timestamp", 0);
			String result = HttpUrlHelper.postData(map, "/circles/ilist/"
					+ SharedUtils.getString("uid", ""));
			// 你要执行的方法
			try {
				JSONObject jsonobject = new JSONObject(result);
				JSONArray jsonarray = jsonobject.getJSONArray("circles");
				if (jsonarray != null) {
					DBUtils.clearTableData("circlelist");// 清空本地表 保存最新数据
				}
				for (int i = jsonarray.length() - 1; i >= 0; i--) {
					JSONObject object = (JSONObject) jsonarray.opt(i);
					CircleModle modle = new CircleModle();
					String id = object.getString("id");
					String logo = object.getString("logo");
					String name = object.getString("name");
					String isNew = object.getString("is_new");
					if (isNew.equals("1")) {
						modle.setNew(true);
					} else {
						modle.setNew(false);

					}
					modle.setCirImg(1);
					modle.setCirID(id);
					modle.setCirIcon(StringUtils.JoinString(logo, "_200x200"));
					modle.setCirName(name);
					serverListModle.add(serverListModle.size() - 1, modle);
					insertData(id, name,
							StringUtils.JoinString(logo, "_200x200"));
					creatTable("circle" + id);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result == null) {// 异常时不执行一下操作
				return;
			}
			listModle.clear();
			listModle = serverListModle;
			adapter.setData(listModle);
			progressDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			progressDialog = new ProgressDialog(mcontext);
			if (listModle.size() > 1) {
				return;
			}
			progressDialog.show();
		}
	}

	/**
	 * 创建表圈子所对应的表
	 */
	private static void creatTable(String circleName) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		// 创建圈子所对应的表
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ circleName
				+ " ( _id integer PRIMARY KEY AUTOINCREMENT ,personID varchar,userID varchar,userName varchar, userImg varchar,employer varchar,sortkey varchar)");
		db.execSQL("create table IF NOT EXISTS "
				+ circleName
				+ "userlist"
				+ "( _id integer PRIMARY KEY AUTOINCREMENT ,tID varchar,personID varchar,key varchar, value varchar,startDate varchar,endDate)");
	}

	/**
	 * 更新圈子列表
	 * 
	 * @param modle
	 */
	public static void refreshCircleList(CircleModle modle) {
		listModle.add(listModle.size() - 1, modle);
		adapter.setData(listModle);
		creatTable("circle" + modle.getCirID());
	}

	/**
	 * 插入数据库
	 * 
	 * @param name
	 * @param num
	 */
	private void insertData(String id, String name, String img) {
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("cirID", id);
		values.put("cirName", name);
		values.put("cirImg", img);
		DBUtils.insertData("circlelist", values);
	}

	/**
	 * 初始化控件
	 */
	private void findViewById() {
		mMenu = (LinearLayout) mHome.findViewById(R.id.home_menu);
		gView = (GridView) mHome.findViewById(R.id.gridView1);
		createCircle = (ImageView) mHome.findViewById(R.id.createCircle);
		scrollView = (BounceScrollView) mHome
				.findViewById(R.id.bounceScrollView);
		search = (EditText) mHome.findViewById(R.id.search);
	}

	private void setListener() {
		createCircle.setOnClickListener(this);
		scrollView.setOnRefreshComplete(this);
		mMenu.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mOnOpenListener != null) {
					mOnOpenListener.open();
				}
			}
		});
		gView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				if (position == listModle.size() - 1) {
					Intent intent = new Intent();
					intent.setClass(mcontext, AddCircleMemberActivity.class);
					mcontext.startActivity(intent);
					return;
				}
				TextView txt = (TextView) v.findViewById(R.id.circleName);
				String name = txt.getText().toString();
				Intent it = new Intent();
				it.setClass(mcontext, CircleActivity.class);
				it.putExtra("name", name);
				it.putExtra("is_New", listModle.get(position).isNew());
				it.putExtra("cirID", listModle.get(position).getCirID());
				mcontext.startActivity(it);
			}
		});
	}

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		mOnOpenListener = onOpenListener;
	}

	public View getView() {
		return mHome;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.createCircle:
			break;
		default:
			break;
		}

	}

	@Override
	public void onComplete() {
		search.setVisibility(View.VISIBLE);
	}

}