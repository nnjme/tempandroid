package com.changlianxi.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.activity.R;
import com.changlianxi.db.DataBase;
import com.changlianxi.inteface.ChangeView;
import com.changlianxi.modle.Info;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.ImageManager;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.UserInfoUtils;
import com.changlianxi.util.Utils;

/**
 * 用户资料显示View 主要对用户资料的分类显示
 * 
 * @author teeker_bin
 * 
 */

public class MyCardShow {
	private Context mContext;
	private View cardShow;
	private List<Info> list = new ArrayList<Info>();;
	private List<String> groupkey = new ArrayList<String>();
	private List<Info> basicList = new ArrayList<Info>();// 存放基本信息数据
	private List<Info> socialList = new ArrayList<Info>();// 存放社交账号数据
	private List<Info> contactList = new ArrayList<Info>();// 存放联系方式数据
	private List<Info> addressList = new ArrayList<Info>();// 存放地址数据
	private List<Info> otherList = new ArrayList<Info>();// 存放其他数据
	private List<Info> eduList = new ArrayList<Info>();// 存放教育经历
	private List<Info> workList = new ArrayList<Info>();// 存放工作经历
	private ListView listview;
	private MyAdapter adapter;
	private ChangeView cv;
	public String pid = "";
	private String avatarPath = "";
	private ImageView avatar;
	private DataBase dbase = DataBase.getInstance();
	private SQLiteDatabase db;
	private String uid;

	/**
	 * 构造函数
	 * 
	 * @param context
	 */
	public MyCardShow(Context context, ImageView avatar) {
		this.mContext = context;
		uid = SharedUtils.getString("uid", "");
		db = dbase.getWritableDatabase();
		this.avatar = avatar;
		cardShow = LayoutInflater.from(context).inflate(R.layout.mycard_show,
				null);
		listview = (ListView) cardShow.findViewById(R.id.listView1);
		initData();
		adapter = new MyAdapter();
		listview.setAdapter(adapter);
		db.execSQL("create table IF NOT EXISTS "
				+ "mydetail"
				+ "( _id integer PRIMARY KEY AUTOINCREMENT ,tID varchar,uid varchar,key varchar, value varchar,startDate varchar,endDate)");
		new GetDataTask().execute();
		// getUserInfo("mydetail", uid);
	}

	public View getView() {
		return cardShow;
	}

	public void initData() {
		for (int i = 0; i < UserInfoUtils.infoTitleKey.length; i++) {
			groupkey.add(UserInfoUtils.infoTitleKey[i]);
		}
		Info basicKey = new Info();
		basicKey.setKey(UserInfoUtils.infoTitleKey[0]);
		list.add(basicKey);
		list.addAll(basicList);
		Info contactKey = new Info();
		contactKey.setKey(UserInfoUtils.infoTitleKey[1]);
		list.add(contactKey);
		list.addAll(contactList);
		Info socialKey = new Info();
		socialKey.setKey(UserInfoUtils.infoTitleKey[2]);
		list.add(socialKey);
		list.addAll(socialList);
		Info addressKey = new Info();
		addressKey.setKey(UserInfoUtils.infoTitleKey[3]);
		list.add(addressKey);
		list.addAll(addressList);
		Info otherKey = new Info();
		otherKey.setKey(UserInfoUtils.infoTitleKey[4]);
		list.add(otherKey);
		list.addAll(otherList);
		Info eduKey = new Info();
		eduKey.setKey(UserInfoUtils.infoTitleKey[5]);
		list.add(eduKey);
		list.addAll(eduList);
		Info wordKey = new Info();
		wordKey.setKey(UserInfoUtils.infoTitleKey[6]);
		list.add(wordKey);
		list.addAll(workList);
	}

	/**
	 * 获取本地信息
	 * 
	 * @param userlistNmae
	 * @param pid
	 */
	private void getUserInfo(String userlistNmae, String uid) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		Cursor cursor = db.query(userlistNmae, null, "uid='" + uid + "'", null,
				null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String tid = cursor.getString(cursor.getColumnIndex("tID"));
				String key = cursor.getString(cursor.getColumnIndex("key"));
				String value = cursor.getString(cursor.getColumnIndex("value"));
				String start = cursor.getString(cursor
						.getColumnIndex("startDate"));
				String end = cursor.getString(cursor.getColumnIndex("endDate"));
				dataClassification(tid, key, value, start, end);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();
	}

	/**
	 * 才从服务器获取数据
	 * 
	 */
	class GetDataTask extends AsyncTask<String, Integer, String> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		String errorCoce = "";
		ProgressDialog pd;

		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("token", SharedUtils.getString("token", ""));
			String json = HttpUrlHelper.postData(map, "/people/imyDetail");
			try {
				JSONObject jsonobject = new JSONObject(json);
				if (!jsonobject.getString("rt").equals("1")) {
					errorCoce = jsonobject.getString("err");
					return "error";
				}
				pid = jsonobject.getString("pid");
				JSONArray jsonarray = jsonobject.getJSONArray("person");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject object = (JSONObject) jsonarray.opt(i);
					String start = "";
					String end = "";
					String id = object.getString("id");// 属性id
					String key = object.getString("type");// 属性名称
					String value = object.getString("value");// 属性值
					if (key.equals("D_AVATAR")) {
						avatarPath = value;
					}
					if (Arrays.toString(UserInfoUtils.eduStr).contains(key)) {
						start = object.getString("start");//
						end = object.getString("end");//
					}
					dataClassification(id, key, value, start, end);
					// insertData(id, uid, key, value, start, end);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return "1";// 正常访问
		}

		@Override
		protected void onPostExecute(String result) {
			pd.dismiss();
			if (result.equals("error")) {
				Utils.showToast(ErrorCodeUtil.convertToChines(errorCoce));
				return;
			}
			ImageManager.from(mContext).displayImage(avatar, avatarPath, -1,
					avatar.getWidth(), avatar.getWidth());
			System.out.println("ava:::" + avatarPath);
			list.clear();
			initData();
			adapter.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			pd = new ProgressDialog(mContext);
			pd.show();
		}
	}

	/**
	 * 存入本地数据库
	 * 
	 * @param tid
	 * @param personId
	 * @param key
	 * @param value
	 * @param start
	 * @param end
	 */
	private void insertData(String tid, String personId, String key,
			String value, String start, String end) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("tID", tid);
		values.put("personID", personId);
		values.put("key", key);
		values.put("value", value);
		values.put("startDate", start);
		values.put("endDate", end);
		System.out
				.println("insert:     " + "key:" + key + "    value:" + value);
		db.insert("mydetail", null, values);
	}

	/**
	 * 数据分类
	 * 
	 * @param id
	 * @param key
	 * @param value
	 */
	private void dataClassification(String id, String key, String value,
			String start, String end) {
		Info info = new Info();
		info.setValue(value);
		info.setId(id);
		info.setType(key);
		String typekey = "";
		if (Arrays.toString(UserInfoUtils.basicStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			basicList.add(info);
		} else if (Arrays.toString(UserInfoUtils.socialStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			socialList.add(info);
		} else if (Arrays.toString(UserInfoUtils.contactStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			contactList.add(info);
		} else if (Arrays.toString(UserInfoUtils.addressStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			addressList.add(info);
		} else if (Arrays.toString(UserInfoUtils.otherStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			otherList.add(info);
		} else if (Arrays.toString(UserInfoUtils.eduStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setStartDate(start);
			info.setEndDate(end);
			eduList.add(info);
		} else if (Arrays.toString(UserInfoUtils.workStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			workList.add(info);
		}
	}

	/**
	 * 从编辑界面回到显示界面之后进行数据更新
	 * 
	 * @param data
	 *            修改之後的数据
	 * @param infoType
	 *            修改的资料类型
	 */
	public void refushData(List<Info> data, int infoType) {
		switch (infoType) {
		case 0:
			basicList = data;
			break;
		case 1:
			contactList = data;
			break;
		case 2:
			socialList = data;
			break;
		case 3:
			addressList = data;
			break;
		case 4:
			otherList = data;
			break;
		case 5:
			eduList = data;
			break;
		case 6:
			workList = data;
			break;
		default:
			break;
		}
		list.clear();
		initData();
		adapter.notifyDataSetChanged();

	}

	/**
	 * 自定义的Adapter
	 * 
	 * @author teeker_bin
	 * 
	 */
	private class MyAdapter extends BaseAdapter {
		ViewHolder holder = null;

		public MyAdapter() {
			holder = new ViewHolder();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean isEnabled(int position) {
			if (groupkey.contains(getItem(position))) {
				return false;
			}
			return super.isEnabled(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (groupkey.contains(list.get(position).getKey())) {

				view = LayoutInflater.from(mContext).inflate(
						R.layout.info_show_list_item_tag, null);
				holder.titleKey = (TextView) view.findViewById(R.id.key);
				holder.titleKey.setText(list.get(position).getKey());
				holder.edit = (ImageView) view.findViewById(R.id.imgEdit);
				holder.edit.setOnClickListener(new BtnClick(holder.titleKey
						.getText().toString()));
				if (position == 0) {
					holder.line = (ImageView) view
							.findViewById(R.id.imageView1);
					holder.line.setVisibility(View.GONE);
				}
				return view;
			} else {
				if (Arrays.toString(UserInfoUtils.eduStr).contains(
						list.get(position).getType())) {
					view = LayoutInflater.from(mContext).inflate(
							R.layout.info_edu_work_show_item, null);
					holder.startTimeAndEndTime = (TextView) view
							.findViewById(R.id.startTimeAndEndTime);
					holder.eduAndWordContent = (TextView) view
							.findViewById(R.id.eduAndWordContent);
					holder.startTimeAndEndTime.setText(DateUtils
							.interceptDateStr(
									list.get(position).getStartDate(),
									"yyyy-MM")
							+ "-"
							+ DateUtils.interceptDateStr(list.get(position)
									.getEndDate(), "yyyy-MM"));
					holder.eduAndWordContent.setText(list.get(position)
							.getValue());
					return view;
				}
				view = LayoutInflater.from(mContext).inflate(
						R.layout.info_show_list_item, null);
				holder.key = (TextView) view.findViewById(R.id.key);
				holder.value = (TextView) view.findViewById(R.id.value);
				holder.sms = (ImageView) view.findViewById(R.id.imgSms);
				holder.tel = (ImageView) view.findViewById(R.id.imgTel);
				holder.email = (ImageView) view.findViewById(R.id.imgEmail);
				holder.email.setOnClickListener(new BtnClick(list.get(position)
						.getValue()));
				holder.tel.setOnClickListener(new BtnClick(list.get(position)
						.getValue()));
				holder.sms.setOnClickListener(new BtnClick(list.get(position)
						.getValue()));
				holder.key.setText(list.get(position).getKey() + ":");
				holder.value.setText(list.get(position).getValue());
				return view;
			}
		}
	}

	class BtnClick implements OnClickListener {
		String str;

		public BtnClick() {
		}

		public BtnClick(String str) {
			this.str = str;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.imgEdit:
				if (str.equals(UserInfoUtils.infoTitleKey[0])) {
					cv.setViewData(basicList, 0, "", pid, "");
				} else if (str.equals(UserInfoUtils.infoTitleKey[1])) {
					cv.setViewData(contactList, 1, "", pid, "");
				} else if (str.equals(UserInfoUtils.infoTitleKey[2])) {
					cv.setViewData(socialList, 2, "", pid, "");
				} else if (str.equals(UserInfoUtils.infoTitleKey[3])) {
					cv.setViewData(addressList, 3, "", pid, "");
				} else if (str.equals(UserInfoUtils.infoTitleKey[4])) {
					cv.setViewData(otherList, 4, "", pid, "");
				} else if (str.equals(UserInfoUtils.infoTitleKey[5])) {
					cv.setViewData(eduList, 5, "", pid, "");
				} else if (str.equals(UserInfoUtils.infoTitleKey[6])) {
					cv.setViewData(workList, 6, "", pid, "");
				}
				break;
			default:
				break;
			}

		}

	}

	public void setChangeView(ChangeView cv) {
		this.cv = cv;
	}

	class ViewHolder {
		ImageView line;
		TextView key;
		TextView value;
		ImageView sms;
		ImageView tel;
		ImageView email;
		ImageView edit;
		TextView titleKey;
		TextView startTimeAndEndTime;
		TextView eduAndWordContent;
	}

}
