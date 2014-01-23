package com.changlianxi.activity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.activity.UserInfoEditActivity.EditTextWatcher;
import com.changlianxi.db.DataBase;
import com.changlianxi.modle.Info;
import com.changlianxi.popwindow.AddKeyAndValuePopwindow;
import com.changlianxi.popwindow.AddKeyAndValuePopwindow.OnSelectKey;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.UserInfoUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * 圈子成员信息编辑界面
 * 
 * @author teeker_bin
 * 
 */
public class UserInfoEditActivity1 extends BaseActivity implements
		OnClickListener, PostCallBack {
	private List<String> groupkey = new ArrayList<String>();
	private List<Info> basicList = new ArrayList<Info>();// 存放基本信息数据
	private List<Info> contactList = new ArrayList<Info>();// 存放联系方式数据
	private List<Info> socialList = new ArrayList<Info>();// 存放社交账号数据
	private List<Info> addressList = new ArrayList<Info>();// 存放地址数据
	private List<Info> eduList = new ArrayList<Info>();// 存放教育经历
	private List<Info> workList = new ArrayList<Info>();// 存放工作经历
	private ImageView back;
	private CircularImage avatar;
	private EditText editName;
	private String strName;
	private String avatarURL;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private JSONArray jsonAry = new JSONArray();
	private JSONObject jsonObj;
	private Button btnSave;
	private String pid = "";
	private String cid = "";
	private Dialog dialog;
	private Calendar cal = Calendar.getInstance();
	private DataBase dbase = DataBase.getInstance();
	private SQLiteDatabase db = dbase.getWritableDatabase();
	private RelativeLayout layTop;
	private ListView editListView;
	private List<List<Info>> listDates = new ArrayList<List<Info>>();
	private MyAdapter adapter;
	private RelativeLayout layParent;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				for (int i = basicList.size() - 1; i >= 0; i--) {
					String type = basicList.get(i).getType();
					if (type.equals("D_NAME")) {
						basicList.remove(i);
						break;
					}
				}
				setValuesAdapter();
				break;
			case 1:
				getData();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info_edit_activity1);
		imageLoader = CLXApplication.getImageLoader();
		options = CLXApplication.getUserOptions();
		cid = getIntent().getStringExtra("cid");
		pid = getIntent().getStringExtra("pid");
		strName = getIntent().getStringExtra("name");
		avatarURL = getIntent().getStringExtra("avatar");
		initView();
		initData();
		setListener();
		editName.setText(strName);
		setAvatar();
		mHandler.sendEmptyMessageDelayed(1, 100);
	}

	/**
	 * 获取成员资料信息
	 * 
	 * @param view
	 * @param pid
	 */
	private void getUserDetails(String pid) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		Cursor cursor = db.query(Constants.USERDETAIL, null, "personID='" + pid
				+ "'", null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String tid = cursor.getString(cursor.getColumnIndex("tID"));
				String key = cursor.getString(cursor.getColumnIndex("key"));
				String value = cursor.getString(cursor.getColumnIndex("value"));
				String start = cursor.getString(cursor
						.getColumnIndex("startDate"));
				String end = cursor.getString(cursor.getColumnIndex("endDate"));
				valuesClassification(tid, key, value, start, end);
				cursor.moveToNext();
			}
		}
		cursor.close();
	}

	/**
	 * 数据分类
	 * 
	 * @param id
	 * @param key
	 * @param value
	 */
	public void valuesClassification(String id, String key, String value,
			String start, String end) {
		Info info = new Info();
		info.setValue(value);
		info.setId(id);
		info.setType(key);
		String typekey = "";
		for (int i = 0; i < UserInfoUtils.basicUserStr.length; i++) {
			if (key.equals(UserInfoUtils.basicUserStr[i])) {
				typekey = UserInfoUtils.convertToChines(key);
				info.setKey(typekey);
				info.setTitleKey(groupkey.get(0));
				basicList.add(info);
			}
		}
		if (Arrays.toString(UserInfoUtils.socialStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setTitleKey(groupkey.get(2));
			socialList.add(info);
		} else if (Arrays.toString(UserInfoUtils.contactStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setTitleKey(groupkey.get(1));
			contactList.add(info);

		} else if (Arrays.toString(UserInfoUtils.addressStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setTitleKey(groupkey.get(3));
			addressList.add(info);

		} else if (Arrays.toString(UserInfoUtils.eduStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setStartDate(start);
			info.setEndDate(end);
			info.setTitleKey(groupkey.get(4));

			eduList.add(info);
		} else if (Arrays.toString(UserInfoUtils.workStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setStartDate(start);
			info.setTitleKey(groupkey.get(5));

			info.setEndDate(end);
			workList.add(info);
		}

	}

	private void getData() {
		new Thread() {
			public void run() {
				getUserDetails(pid);
				mHandler.sendEmptyMessage(0);
			}
		}.start();

	}

	public void initData() {
		for (int i = 0; i < UserInfoUtils.infoTitleKey.length; i++) {
			groupkey.add(UserInfoUtils.infoTitleKey[i]);
		}
	}

	private void initView() {
		layParent = (RelativeLayout) findViewById(R.id.parent);
		layTop = (RelativeLayout) findViewById(R.id.top);
		back = (ImageView) findViewById(R.id.back);
		avatar = (CircularImage) findViewById(R.id.avatar);
		editName = (EditText) findViewById(R.id.editName);
		btnSave = (Button) findViewById(R.id.btnSave);
		editListView = (ListView) findViewById(R.id.editListView);
	}

	private void setAvatar() {
		imageLoader.loadImage(avatarURL, options, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String arg0, View arg1) {

			}

			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {

			}

			@SuppressLint("NewApi")
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap bmp) {
				if (bmp == null) {
					avatar.setImageResource(R.drawable.head_bg);
					return;
				}
				avatar.setImageBitmap(bmp);
				layTop.setBackground(BitmapUtils.convertBimapToDrawable(bmp));
			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {

			}
		});

	}

	private void setValuesAdapter() {
		listDates.add(basicList);
		listDates.add(contactList);
		listDates.add(socialList);
		listDates.add(addressList);
		listDates.add(eduList);
		listDates.add(workList);
		adapter = new MyAdapter();
		editListView.setAdapter(adapter);
	}

	private void setListener() {
		back.setOnClickListener(this);
		btnSave.setOnClickListener(this);

	}

	class MyAdapter extends BaseAdapter {
		ChildAdapter adapter = null;

		@Override
		public int getCount() {
			return listDates.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new MyViewHolder();
				convertView = LayoutInflater.from(UserInfoEditActivity1.this)
						.inflate(R.layout.user_info_edit1_item, null);
				viewHolder.imgAdd = (ImageView) convertView
						.findViewById(R.id.imgAdd);
				viewHolder.listview = (ListView) convertView
						.findViewById(R.id.listview);
				viewHolder.titleKey = (TextView) convertView
						.findViewById(R.id.titleKey);
				viewHolder.layParent = (LinearLayout) convertView
						.findViewById(R.id.parent);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (MyViewHolder) convertView.getTag();
			}
			viewHolder.imgAdd.setOnClickListener(new BtnAddClick(position));
			viewHolder.titleKey.setText(groupkey.get(position));
			adapter = new ChildAdapter(listDates.get(position));
			viewHolder.listview.setAdapter(adapter);
			Utils.setListViewHeightBasedOnChildren(viewHolder.listview);
			if (position % 2 == 0) {
				viewHolder.layParent.setBackgroundColor(getResources()
						.getColor(R.color.white));
			} else {
				viewHolder.layParent.setBackgroundColor(getResources()
						.getColor(R.color.f6));
			}
			return convertView;
		}
	}

	class MyViewHolder {
		ListView listview;
		TextView titleKey;
		ImageView imgAdd;
		LinearLayout layParent;
	}

	class ChildAdapter extends BaseAdapter {
		List<Info> lists;
		final int TYPE_1 = 1;
		final int TYPE_2 = 2;
		final int TYPE_3 = 3;

		public ChildAdapter(List<Info> lists) {
			this.lists = lists;
		}

		@Override
		public int getCount() {
			return lists.size();
		}

		public void setDa(List<Info> lists) {
			this.lists = lists;
			notifyDataSetChanged();
		}

		@Override
		public int getItemViewType(int position) {
			String titkeKey = lists.get(position).getTitleKey();
			String key = lists.get(position).getKey();
			if (titkeKey.equals("教育经历") || titkeKey.equals("工作经历")) {
				return TYPE_2;
			} else {
				if (key.equals("性別")) {
					return TYPE_3;
				}
				return TYPE_1;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			ChildVEduiewHolder eduHolder = null;
			ChildViewHolder viewholder = null;
			ChildGendarHolder gendarHolder = null;
			String titleKey = lists.get(position).getTitleKey();
			String key = lists.get(position).getKey();
			int editType = lists.get(position).getEditType();
			if (convertView == null) {
				switch (type) {
				case TYPE_1:
					convertView = LayoutInflater.from(
							UserInfoEditActivity1.this).inflate(
							R.layout.user_info_edit_list_item_key_value, null);
					viewholder = new ChildViewHolder();
					viewholder.btnDel = (ImageView) convertView
							.findViewById(R.id.btnDel);
					viewholder.key = (TextView) convertView
							.findViewById(R.id.key);
					viewholder.value = (EditText) convertView
							.findViewById(R.id.value);
					viewholder.layBg = (LinearLayout) convertView
							.findViewById(R.id.layBg);
					convertView.setTag(viewholder);
					break;
				case TYPE_2:
					convertView = LayoutInflater.from(
							UserInfoEditActivity1.this).inflate(
							R.layout.info_edu_word__edit_item, null);
					eduHolder = new ChildVEduiewHolder();
					eduHolder.btnDel = (ImageView) convertView
							.findViewById(R.id.btnDel);
					eduHolder.key = (TextView) convertView
							.findViewById(R.id.key);
					eduHolder.value = (EditText) convertView
							.findViewById(R.id.value);
					eduHolder.startTime = (EditText) convertView
							.findViewById(R.id.startTime);
					eduHolder.endTime = (EditText) convertView
							.findViewById(R.id.endTime);
					convertView.setTag(eduHolder);
					break;
				case TYPE_3:
					gendarHolder = new ChildGendarHolder();
					convertView = LayoutInflater.from(
							UserInfoEditActivity1.this).inflate(
							R.layout.user_info_edit_gendar, null);
					gendarHolder.key = (TextView) convertView
							.findViewById(R.id.key);
					gendarHolder.radioBoy = (RadioButton) convertView
							.findViewById(R.id.radioboy);
					gendarHolder.radioGirl = (RadioButton) convertView
							.findViewById(R.id.radiogirl);
					gendarHolder.layBg = (LinearLayout) convertView
							.findViewById(R.id.layBg);
					convertView.setTag(gendarHolder);
					break;
				default:
					break;
				}
			} else {
				switch (type) {
				case TYPE_1:
					viewholder = (ChildViewHolder) convertView.getTag();
					break;
				case TYPE_2:
					eduHolder = (ChildVEduiewHolder) convertView.getTag();
					break;
				case TYPE_3:
					gendarHolder = (ChildGendarHolder) convertView.getTag();
					break;
				default:
					break;
				}
			}
			switch (type) {
			case TYPE_1:
				viewholder.btnDel.setOnClickListener(new BtnDelClick(position,
						titleKey));
				viewholder.key.setText(key);
				viewholder.value.setText(lists.get(position).getValue());
				if (titleKey.equals("基本信息")) {
					viewholder.value.setEnabled(false);
					viewholder.layBg.setBackgroundColor(getResources()
							.getColor(R.color.f6));
					if (key.equals("昵称") || key.equals("备注")) {
						viewholder.value.setEnabled(true);
						viewholder.layBg.setBackgroundColor(getResources()
								.getColor(R.color.white));
						viewholder.btnDel.setOnClickListener(new BtnDelClick(
								position, titleKey));
						viewholder.value
								.addTextChangedListener(new EditTextWatcher(
										lists, position, editType));
					}
				}

				break;
			case TYPE_2:
				eduHolder.value.addTextChangedListener(new EditTextWatcher(
						lists, position, editType));
				eduHolder.btnDel.setOnClickListener(new BtnDelClick(position,
						titleKey));
				eduHolder.key.setText(key);
				String value = lists.get(position).getValue();
				eduHolder.value.setText(value);
				eduHolder.startTime.setText(lists.get(position).getStartDate());
				eduHolder.endTime.setText(lists.get(position).getEndDate());
				break;
			case TYPE_3:
				gendarHolder.key.setText(key);
				gendarHolder.radioBoy.setClickable(false);
				gendarHolder.radioGirl.setClickable(false);
				gendarHolder.layBg
						.setBackgroundColor(UserInfoEditActivity1.this
								.getResources().getColor(R.color.f6));
				if (lists.get(position).getValue().equals("1")) {
					gendarHolder.radioBoy.setChecked(true);
				} else if (lists.get(position).getValue().equals("2")) {
					gendarHolder.radioGirl.setChecked(true);
				}
				break;
			default:
				break;
			}
			return convertView;
		}
	}

	class ChildViewHolder {
		TextView key;
		EditText value;
		ImageView btnDel;
		LinearLayout layBg;

	}

	class ChildGendarHolder {
		TextView key;
		RadioButton radioBoy;
		RadioButton radioGirl;
		LinearLayout layBg;
	}

	class ChildVEduiewHolder {
		TextView key;
		EditText value;
		EditText startTime;
		EditText endTime;
		ImageView btnDel;

	}

	class BtnDelClick implements OnClickListener {
		int position;
		String tag;

		public BtnDelClick(int posi, String strTag) {
			position = posi;
			tag = strTag;
		}

		@Override
		public void onClick(View v) {
			if (tag.equals(groupkey.get(0))) {
				if (basicList.get(position).getEditType() != 2) {
					BuildDelJson(basicList.get(position).getType(), basicList
							.get(position).getValue(), basicList.get(position)
							.getId());
				}
				basicList.remove(position);
			} else if (tag.equals(groupkey.get(1))) {
				if (contactList.get(position).getEditType() != 2) {
					String ketType = contactList.get(position).getType();
					if (ketType.equals("D_CELLPHONE")) {
						Utils.showToast("注册手机号不能被删除");
						return;
					}
					BuildDelJson(ketType, contactList.get(position).getValue(),
							contactList.get(position).getId());
				}
				contactList.remove(position);
			} else if (tag.equals(groupkey.get(2))) {
				if (socialList.get(position).getEditType() != 2) {
					BuildDelJson(socialList.get(position).getType(), socialList
							.get(position).getValue(), socialList.get(position)
							.getId());
				}
				socialList.remove(position);
			} else if (tag.equals(groupkey.get(3))) {
				if (addressList.get(position).getEditType() != 2) {
					BuildDelJson(addressList.get(position).getType(),
							addressList.get(position).getValue(), addressList
									.get(position).getId());
				}
				addressList.remove(position);
			} else if (tag.equals(groupkey.get(4))) {
				if (eduList.get(position).getEditType() != 2) {
					BuildDelJson(eduList.get(position).getType(),
							eduList.get(position).getValue(),
							eduList.get(position).getId());
				}
				eduList.remove(position);

			} else if (tag.equals(groupkey.get(5))) {
				if (workList.get(position).getEditType() != 2) {
					BuildDelJson(workList.get(position).getType(), workList
							.get(position).getValue(), workList.get(position)
							.getId());
				}
				workList.remove(position);

			}
			adapter.notifyDataSetChanged();
		}
	}

	class BtnAddClick implements OnClickListener {
		int position;
		String array[];

		public BtnAddClick(int posi) {
			position = posi;
			switch (position) {
			case 0:
				array = UserInfoUtils.basicUserChineseStr;
				break;
			case 1:
				array = UserInfoUtils.contacChinesetStr;
				break;
			case 2:
				array = UserInfoUtils.socialChineseStr;
				break;
			case 3:
				array = UserInfoUtils.addressChineseStr;

				break;

			case 4:
				array = UserInfoUtils.eduChinesStr;

				break;
			case 5:
				array = UserInfoUtils.workChineseStr;

				break;

			default:
				break;
			}
		}

		@Override
		public void onClick(View v) {
			List<String> list = null;
			List<String> arrayList = null;
			switch (position) {
			case 0:
				// list = Arrays.asList(UserInfoUtils.basicChineseStr);
				list = Arrays.asList(new String[] { "昵称", "备注" });
				arrayList = new ArrayList<String>(list);
				for (int i = 0; i < basicList.size(); i++) {
					if (arrayList.contains(basicList.get(i).getKey())) {
						arrayList.remove(basicList.get(i).getKey());
					}
				}
				array = (arrayList.toArray(new String[arrayList.size()]));
				break;
			case 1:
				list = Arrays.asList(UserInfoUtils.contacChinesetStr);
				arrayList = new ArrayList<String>(list);
				for (int i = 0; i < arrayList.size(); i++) {
					if (arrayList.get(i).equals("注册手机号")) {
						arrayList.remove(i);
						break;
					}
				}
				array = (arrayList.toArray(new String[arrayList.size()]));
				break;
			case 2:
				list = Arrays.asList(UserInfoUtils.socialChineseStr);
				arrayList = new ArrayList<String>(list);
				for (int i = 0; i < socialList.size(); i++) {
					if (arrayList.contains(socialList.get(i).getKey())) {
						arrayList.remove(socialList.get(i).getKey());
					}
				}
				array = (arrayList.toArray(new String[arrayList.size()]));
				break;
			case 3:
				list = Arrays.asList(UserInfoUtils.addressChineseStr);
				arrayList = new ArrayList<String>(list);
				for (int i = 0; i < addressList.size(); i++) {
					if (arrayList.contains(addressList.get(i).getKey())) {
						arrayList.remove(addressList.get(i).getKey());
					}
				}
				array = (arrayList.toArray(new String[arrayList.size()]));
				break;
			default:
				break;
			}
			AddKeyAndValuePopwindow pop = new AddKeyAndValuePopwindow(
					UserInfoEditActivity1.this, layParent, array, "标签");
			pop.setCallBack(new OnSelectKey() {

				@Override
				public void getSelectKey(String str) {
					Info info = new Info();
					info.setKey(str);
					info.setType(UserInfoUtils.convertToEnglish(str));
					info.setEditType(2);
					info.setValue("");
					switch (position) {
					case 0:
						basicList.add(info);
						break;
					case 1:
						contactList.add(info);
						break;
					case 2:
						socialList.add(info);
						break;
					case 3:
						addressList.add(info);
						break;
					case 4:
						eduList.add(info);
						break;
					case 5:
						workList.add(info);
						break;
					default:
						break;
					}
					adapter.notifyDataSetChanged();
				}

			});
			pop.show();
		}
	}

	class ViewHolder1 {
		TextView key;
		RadioButton radioBoy;
		RadioButton radioGirl;
		ImageView btnDel;
		LinearLayout layBg;
	}

	class ViewHolder2 {
		TextView key;
		EditText value;
		ImageView btnDel;
		LinearLayout layBg;
	}

	class ViewHolderValues {
		TextView key;
		EditText value;
		ImageView btnDel;
		LinearLayout layBg;

	}

	class OnTimeClick implements OnClickListener {
		String type = "";
		List<Info> valuesList;
		int position;
		int editType;

		public OnTimeClick(String type, List<Info> valuesList, int position,
				int editType) {
			this.type = type;
			this.valuesList = valuesList;
			this.position = position;
			this.editType = editType;
		}

		@Override
		public void onClick(View v) {
			showDateDialog(valuesList, position, type, editType);

		}

	}

	// 日期选择对话框的 DateSet 事件监听器
	class DateListener implements OnDateSetListener {
		List<Info> valuesList;
		int position;
		String tag;
		int editType;

		public DateListener(List<Info> valuesList, int position, String tag,
				int editType) {
			this.position = position;
			this.tag = tag;
			this.valuesList = valuesList;
			this.editType = editType;
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, monthOfYear);
			cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateDate(valuesList, position, tag, editType);

		}

	}

	private void showDateDialog(List<Info> valuesList, int position,
			String tag, int editType) {
		new DatePickerDialog(this, new DateListener(valuesList, position, tag,
				editType), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH)).show();
	}

	// 当 DatePickerDialog 关闭，更新日期显示
	private void updateDate(List<Info> valuesList, int position, String tag,
			int editType) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
		String date = df.format(cal.getTime());
		if (tag.equals("startTime")) {
			String endTime = valuesList.get(position).getEndDate();
			if (!endTime.equals("")) {
				if (!DateUtils.compareDate(valuesList.get(position)
						.getStartDate(), date)) {
					Utils.showToast("开始时间要小于结束时间");
					return;
				}
			}
			if (editType == 2) {
				valuesList.get(position).setStartDate(date);
			} else {
				String values = valuesList.get(position).getStartDate();
				if (!values.equals(date.toString())) {
					valuesList.get(position).setStartDate(date);
					valuesList.get(position).setEditType(3);
				}
			}
		} else if (tag.equals("endTime")) {
			String startTime = valuesList.get(position).getStartDate();
			if (!startTime.equals("")) {
				if (DateUtils.compareDate(valuesList.get(position)
						.getStartDate(), date)) {
					Utils.showToast("结束时间要大于开始时间");
					return;
				}
			}
			if (editType == 2) {
				valuesList.get(position).setEndDate(date);
			} else {
				String values = valuesList.get(position).getEndDate();
				if (!values.equals(date.toString())) {
					valuesList.get(position).setEndDate(date);
					valuesList.get(position).setEditType(3);
				}
			}
		} else if (tag.equals("生日")) {
			if (editType == 2) {
				valuesList.get(position).setValue(date);
			} else {
				String values = valuesList.get(position).getEndDate();
				if (!values.equals(date.toString())) {
					valuesList.get(position).setValue(date);
					valuesList.get(position).setEditType(3);
				}
			}
		}
		adapter.notifyDataSetChanged();
	}

	class EditTextWatcher implements TextWatcher {
		List<Info> valuesList;
		int position;
		int editType;

		public EditTextWatcher(List<Info> valuesList, int position, int type) {
			this.valuesList = valuesList;
			this.position = position;
			this.editType = type;
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (editType == 2) {
				valuesList.get(position).setValue(s.toString());
			} else {
				String values = valuesList.get(position).getValue();
				if (!values.equals(s.toString())) {
					valuesList.get(position).setValue(s.toString());
					valuesList.get(position).setEditType(3);
				}
			}
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
	 * 新增职务json串
	 * 
	 * @param name
	 *            职务名称
	 */
	private void BuildAddEduAndWorkJson(String type, String value,
			String start, String end) {
		try {
			jsonObj = new JSONObject();
			jsonObj.put("op", "new");
			jsonObj.put("id", "0");
			jsonObj.put("t", type);
			jsonObj.put("v", value);
			jsonObj.put("start", start);
			jsonObj.put("end", end);
			jsonAry.put(jsonObj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 新增职务json串
	 * 
	 * @param name
	 *            职务名称
	 */
	private void BuildAddJson(String type, String value) {
		try {
			jsonObj = new JSONObject();
			jsonObj.put("op", "new");
			jsonObj.put("id", "0");
			jsonObj.put("t", type);
			jsonObj.put("v", value);
			jsonAry.put(jsonObj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 编辑职务json串
	 * 
	 * @param name
	 *            职务名称
	 */
	private void BuildEditJson(String type, String value, String id) {
		try {
			jsonObj = new JSONObject();
			jsonObj.put("op", "edit");
			jsonObj.put("id", id);
			jsonObj.put("t", type);
			jsonObj.put("v", value);
			jsonAry.put(jsonObj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 编辑职务json串
	 * 
	 * @param name
	 *            职务名称
	 */
	private void BuildEditEduAndWorkJson(String type, String value, String id,
			String start, String end) {
		try {
			jsonObj = new JSONObject();
			jsonObj.put("op", "edit");
			jsonObj.put("id", id);
			jsonObj.put("t", type);
			jsonObj.put("v", value);
			jsonObj.put("start", start);
			jsonObj.put("end", end);
			jsonAry.put(jsonObj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 新增职务json串
	 * 
	 * @param name
	 *            职务名称
	 */
	private void BuildDelJson(String type, String value, String id) {
		try {
			jsonObj = new JSONObject();
			jsonObj.put("op", "del");
			jsonObj.put("id", id);
			jsonObj.put("t", type);
			jsonObj.put("v", value);
			jsonAry.put(jsonObj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void insertDB(String key, String value, String start, String end) {
		ContentValues cv = new ContentValues();
		cv.put("key", key);
		cv.put("value", value);
		cv.put("personID", pid);
		cv.put("startDate", start);
		cv.put("endDate", end);
		db.insert(Constants.USERDETAIL, null, cv);
	}

	private void upDateDB(ContentValues cv, String tid) {
		db.update(Constants.USERDETAIL, cv, "personID=? and tID=?",
				new String[] { pid, tid });
	}

	/**
	 * 构建上传的字符串
	 */
	private void BuildJson() {
		for (int i = 0; i < basicList.size(); i++) {
			int editType = basicList.get(i).getEditType();
			if (editType == 2) {
				String keyType = basicList.get(i).getType();
				String value = basicList.get(i).getValue();
				if (value.equals("")) {
					Utils.showToast(basicList.get(i).getKey() + "不能为空");
					return;
				}
				insertDB(keyType, value, "", "");
				BuildAddJson(keyType, value);
			} else if (editType == 3) {
				String keyType = basicList.get(i).getType();
				String value = basicList.get(i).getValue();
				String tid = basicList.get(i).getId();
				if (value.equals("")) {
					Utils.showToast(basicList.get(i).getKey() + "不能为空");
					return;
				}
				ContentValues cv = new ContentValues();
				cv.put("value", value);
				upDateDB(cv, tid);
				BuildEditJson(keyType, value, tid);
			}
		}
		for (int i = 0; i < contactList.size(); i++) {
			int editType = contactList.get(i).getEditType();
			if (editType == 2) {
				String keyType = contactList.get(i).getType();
				String value = contactList.get(i).getValue();
				if (value.equals("")) {
					Utils.showToast(contactList.get(i).getKey() + "不能为空");
					return;
				}
				insertDB(keyType, value, "", "");
				BuildAddJson(keyType, value);
			} else if (editType == 3) {
				String keyType = contactList.get(i).getType();
				String value = contactList.get(i).getValue();
				if (value.equals("")) {
					Utils.showToast(contactList.get(i).getKey() + "不能为空");
					return;
				}
				String tid = contactList.get(i).getId();
				ContentValues cv = new ContentValues();
				cv.put("value", value);
				upDateDB(cv, tid);
				BuildEditJson(keyType, value, tid);
			}

		}
		for (int i = 0; i < socialList.size(); i++) {
			int editType = socialList.get(i).getEditType();
			if (editType == 2) {
				String keyType = socialList.get(i).getType();
				String value = socialList.get(i).getValue();
				if (value.equals("")) {
					Utils.showToast(socialList.get(i).getKey() + "不能为空");
					return;
				}
				insertDB(keyType, value, "", "");
				BuildAddJson(keyType, value);
			} else if (editType == 3) {
				String keyType = socialList.get(i).getType();
				String value = socialList.get(i).getValue();
				if (value.equals("")) {
					Utils.showToast(socialList.get(i).getKey() + "不能为空");
					return;
				}
				String tid = socialList.get(i).getId();
				ContentValues cv = new ContentValues();
				cv.put("value", value);
				upDateDB(cv, tid);
				BuildEditJson(keyType, value, tid);
			}
		}
		for (int i = 0; i < addressList.size(); i++) {
			int editType = addressList.get(i).getEditType();
			if (editType == 2) {
				String keyType = addressList.get(i).getType();
				String value = addressList.get(i).getValue();
				if (value.equals("")) {
					Utils.showToast(addressList.get(i).getKey() + "不能为空");
					return;
				}
				insertDB(keyType, value, "", "");
				BuildAddJson(keyType, value);
			} else if (editType == 3) {
				String keyType = addressList.get(i).getType();
				String value = addressList.get(i).getValue();
				if (value.equals("")) {
					Utils.showToast(addressList.get(i).getKey() + "不能为空");
					return;
				}
				String tid = addressList.get(i).getId();
				ContentValues cv = new ContentValues();
				cv.put("value", value);
				upDateDB(cv, tid);
				BuildEditJson(keyType, value, tid);
			}

		}

		for (int i = 0; i < eduList.size(); i++) {
			int editType = eduList.get(i).getEditType();
			String keyType = eduList.get(i).getType();
			String value = eduList.get(i).getValue();
			String start = eduList.get(i).getStartDate();
			String end = eduList.get(i).getEndDate();
			if (value.equals("")) {
				Utils.showToast(eduList.get(i).getKey() + "不能为空");
				return;
			}
			if (start.equals("")) {
				Utils.showToast(eduList.get(i).getKey() + "的开始时间不能为空");
				return;
			}
			if (end.equals("")) {
				Utils.showToast(eduList.get(i).getKey() + "的结束时间不能为空");
				return;
			}
			if (editType == 2) {
				insertDB(keyType, value, start, end);
				BuildAddEduAndWorkJson(keyType, value, start, end);
			} else if (editType == 3) {
				String tid = eduList.get(i).getId();
				ContentValues cv = new ContentValues();
				cv.put("value", value);
				upDateDB(cv, tid);
				BuildEditEduAndWorkJson(keyType, value, tid, start, end);
			}
		}
		for (int i = 0; i < workList.size(); i++) {
			int editType = workList.get(i).getEditType();
			String keyType = workList.get(i).getType();
			String value = workList.get(i).getValue();
			String start = workList.get(i).getStartDate();
			String end = workList.get(i).getEndDate();
			if (value.equals("")) {
				Utils.showToast(workList.get(i).getKey() + "不能为空");
				return;
			}
			if (start.equals("")) {
				Utils.showToast(workList.get(i).getKey() + "的开始时间不能为空");
				return;
			}
			if (end.equals("")) {
				Utils.showToast(workList.get(i).getKey() + "的结束时间不能为空");
				return;
			}
			if (editType == 2) {
				insertDB(keyType, value, start, end);
				BuildAddEduAndWorkJson(keyType, value, start, end);
			} else if (editType == 3) {
				String tid = workList.get(i).getId();
				ContentValues cv = new ContentValues();
				cv.put("value", value);
				upDateDB(cv, tid);
				BuildEditEduAndWorkJson(keyType, value, tid, start, end);
			}
		}
		upLoadEditDetails();
	}

	private void upLoadEditDetails() {
		if (jsonAry.length() == 0) {
			setResult(2);
			finish();
			Utils.rightOut(this);
			return;
		}
		dialog = DialogUtil.getWaitDialog(this, "请稍后");
		dialog.show();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cid", cid);
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("pid", pid);
		map.put("token", SharedUtils.getString("token", ""));
		map.put("person", jsonAry.toString());
		System.out.println("json:::::uid:" + SharedUtils.getString("uid", "")
				+ "token:" + SharedUtils.getString("token", "") + " cid: "
				+ cid + "  pid " + pid + " person  " + jsonAry.toString());
		PostAsyncTask task = new PostAsyncTask(this, map, "/people/iedit");
		task.setTaskCallBack(this);
		task.execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			setResult(2);
			finish();
			Utils.rightOut(this);
			break;
		case R.id.btnSave:
			BuildJson();
			break;
		default:
			break;
		}
	}

	@Override
	public void taskFinish(String result) {
		System.out.println("result:::" + result);
		dialog.dismiss();
		String rt = "";
		String errCode = "";
		try {
			JSONObject jsonobject = new JSONObject(result);
			rt = jsonobject.getString("rt");
			if (!rt.equals("1")) {
				errCode = jsonobject.getString("err");
			}
		} catch (JSONException e) {
		}
		if (rt.equals("1")) {
			Utils.showToast("修改成功");
			Intent it = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("basicList", (Serializable) basicList);
			bundle.putSerializable("contactList", (Serializable) contactList);
			bundle.putSerializable("socialList", (Serializable) socialList);
			bundle.putSerializable("addressList", (Serializable) addressList);
			bundle.putSerializable("eduList", (Serializable) eduList);
			bundle.putSerializable("workList", (Serializable) workList);
			it.putExtras(bundle);
			setResult(2, it);
			finish();
			Utils.rightOut(this);
		} else {
			Utils.showToast(ErrorCodeUtil.convertToChines(errCode));
		}
	}
}
