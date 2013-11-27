package com.changlianxi.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.UserInfoUtils;
import com.changlianxi.util.Utils;

/**
 * �û�������ʾView ��Ҫ���û����ϵķ�����ʾ
 * 
 * @author teeker_bin
 * 
 */

public class UserInfoShow {
	private Context mContext;
	private View infoShow;
	private List<Info> list = new ArrayList<Info>();;
	private List<String> groupkey = new ArrayList<String>();
	private List<Info> basicList = new ArrayList<Info>();// ��Ż�����Ϣ����
	private List<Info> socialList = new ArrayList<Info>();// ����罻�˺�����
	private List<Info> contactList = new ArrayList<Info>();// �����ϵ��ʽ����
	private List<Info> addressList = new ArrayList<Info>();// ��ŵ�ַ����
	private List<Info> otherList = new ArrayList<Info>();// �����������
	private List<Info> eduList = new ArrayList<Info>();// ��Ž�������
	private List<Info> workList = new ArrayList<Info>();// ��Ź�������
	private ListView listview;
	private ChangeView cv;
	private MyAdapter adapter;
	private String tableName;
	private DataBase dbase = DataBase.getInstance();
	private SQLiteDatabase db;
	private String pid;
	private String cid;

	/**
	 * 
	 * @param context
	 * @param str
	 *            Ҫ����������Json��
	 * @param tableName�洢�ı���
	 * @param pid
	 *            �û�id
	 */
	public UserInfoShow(Context context, String tableName, String pid,
			String cid) {
		this.mContext = context;
		this.tableName = tableName;
		this.pid = pid;
		this.cid = cid;
		db = dbase.getWritableDatabase();
		infoShow = LayoutInflater.from(context).inflate(
				R.layout.user_info_show, null);
		listview = (ListView) infoShow.findViewById(R.id.listView_list);
		// if (DBUtils.isExistOfPersonId(tableName, pid)) {
		// getUserInfo(tableName, pid);
		// Logger.debug(this, "��������");
		// } else {
		new GetDetailTask().execute();
		Logger.debug(this, "����������");
		// }
		initData();
		adapter = new MyAdapter();
		setMyAdapter();
	}

	public View getView() {
		return infoShow;
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
	 * ����Ȧ�ӳ�Ա��Ϣ
	 * 
	 * @param userlistNmaeȦ�ӳ�Ա����
	 * @param pid��ԱID
	 * @return
	 */
	private void getUserInfo(String userlistNmae, String pid) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		Cursor cursor = db.query(userlistNmae, null, "personID='" + pid + "'",
				null, null, null, null);
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

	private void setMyAdapter() {
		listview.setAdapter(adapter);
	}

	/**
	 * �����绰ʱ listview�ƶ�����ϵ��ʽ
	 */
	public void moveToCall() {
		for (int i = 0; i < list.size(); i++) {
			if ("��ϵ��ʽ".equals(list.get(i).getKey())) {
				listview.setSelection(i);
				break;

			}
		}

	}

	/**
	 * �Ŵӷ�������ȡ����
	 * 
	 */
	class GetDetailTask extends AsyncTask<String, Integer, String> {
		// �ɱ䳤�������������AsyncTask.exucute()��Ӧ
		String errorCoce = "";

		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cid", cid);
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("pid", pid);
			map.put("token", SharedUtils.getString("token", ""));
			Logger.debug(this, "detail:	" + "cid:" + cid + "   pid:" + pid);
			String json = HttpUrlHelper.postData(map, "/people/idetail");
			try {
				JSONObject jsonobject = new JSONObject(json);
				if (!jsonobject.getString("rt").equals("1")) {
					errorCoce = jsonobject.getString("err");
					return "error";
				}
				JSONArray jsonarray = jsonobject.getJSONArray("person");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject object = (JSONObject) jsonarray.opt(i);
					String start = "";
					String end = "";
					String id = object.getString("id");// ����id
					String key = object.getString("type");// ��������
					String value = object.getString("value");// ����ֵ
					if (Arrays.toString(UserInfoUtils.eduStr).contains(key)) {
						start = object.getString("start");//
						end = object.getString("end");//
					}
					dataClassification(id, key, value, start, end);
					// insertData(id, pid, key, value, start, end);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result == null) {
				list.clear();
				initData();
				adapter.notifyDataSetChanged();
				return;
			}
			if (result.equals("error")) {
				Utils.showToast(ErrorCodeUtil.convertToChines(errorCoce));
				return;
			}

		}

		@Override
		protected void onPreExecute() {
			// ����������������������ʾһ���Ի�������򵥴���
		}
	}

	/**
	 * ���ݷ���
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
	 * �ӱ༭����ص���ʾ����֮��������ݸ���
	 * 
	 * @param data
	 *            �޸�֮�������
	 * @param infoType
	 *            �޸ĵ���������
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

	private void insertData(String tid, String personId, String key,
			String value, String start, String end) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		// ��ö����в����ֵ�ԣ����м���������ֵ��ϣ�����뵽��һ�е�ֵ��ֵ��������ݿ⵱�е���������һ��
		values.put("tID", tid);
		values.put("personID", personId);
		values.put("key", key);
		values.put("value", value);
		values.put("startDate", start);
		values.put("endDate", end);
		System.out
				.println("insert:     " + "key:" + key + "    value:" + value);
		db.insert(tableName, null, values);
		db.close();
	}

	/**
	 * �Զ����Adapter
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
					holder.line = (View) view.findViewById(R.id.line);
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
				if (list.get(position).getKey().contains("�ֻ�")) {
					holder.tel.setVisibility(View.VISIBLE);
					holder.sms.setVisibility(View.VISIBLE);
				} else if (list.get(position).getKey().contains("�绰")) {
					holder.tel.setVisibility(View.VISIBLE);
					holder.sms.setVisibility(View.GONE);
				} else if (list.get(position).getKey().contains("E-Mail")) {
					holder.tel.setVisibility(View.GONE);
					holder.sms.setVisibility(View.GONE);
					holder.email.setVisibility(View.VISIBLE);

				} else {
					holder.tel.setVisibility(View.GONE);
					holder.sms.setVisibility(View.GONE);
					holder.email.setVisibility(View.GONE);
				}

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
			case R.id.imgSms:
				sendMessage(str);
				break;
			case R.id.imgTel:
				callPhone(str);
				break;
			case R.id.imgEmail:
				sendEmail(str);
				break;
			case R.id.imgEdit:
				if (str.equals(UserInfoUtils.infoTitleKey[0])) {
					cv.setViewData(basicList, 0, cid, pid, tableName);
				} else if (str.equals(UserInfoUtils.infoTitleKey[1])) {
					cv.setViewData(contactList, 1, cid, pid, tableName);
				} else if (str.equals(UserInfoUtils.infoTitleKey[2])) {
					cv.setViewData(socialList, 2, cid, pid, tableName);
				} else if (str.equals(UserInfoUtils.infoTitleKey[3])) {
					cv.setViewData(addressList, 3, cid, pid, tableName);
				} else if (str.equals(UserInfoUtils.infoTitleKey[4])) {
					cv.setViewData(otherList, 4, cid, pid, tableName);
				} else if (str.equals(UserInfoUtils.infoTitleKey[5])) {
					cv.setViewData(eduList, 5, cid, pid, tableName);
				} else if (str.equals(UserInfoUtils.infoTitleKey[6])) {
					cv.setViewData(workList, 6, cid, pid, tableName);
				}
				break;
			default:
				break;
			}

		}

	}

	/**
	 * ����绰
	 * 
	 * @param num
	 */
	private void callPhone(String num) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + num));
		mContext.startActivity(intent);

	}

	/**
	 * @param num
	 */
	private void sendMessage(String num) {
		Uri uri = Uri.parse("smsto:" + num);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		it.putExtra("sms_body", "");
		mContext.startActivity(it);
	}

	/**
	 * ����email
	 * 
	 * @param num
	 */
	private void sendEmail(String str) {
		// ϵͳ�ʼ�ϵͳ�Ķ���Ϊandroid.content.Intent.ACTION_SEND
		Uri uri = Uri.parse("mailto:" + str);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		mContext.startActivity(it);
	}

	class ViewHolder {
		View line;
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

	public void setChangeView(ChangeView cv) {
		this.cv = cv;
	}

}
