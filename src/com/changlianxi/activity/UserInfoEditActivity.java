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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changlianxi.R;
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
import com.umeng.analytics.MobclickAgent;

public class UserInfoEditActivity extends BaseActivity implements
		OnClickListener, PostCallBack {
	private ListView basicListView;
	private ListView contactListView;
	private ListView socialListView;
	private ListView addressListView;
	private ListView eduListView;
	private ListView workListView;
	private BasicValueAdapter basicAdapter;
	private ValueAdapter socialAdapter;
	private ValueAdapter contactAdapter;
	private ValueAdapter addressAdapter;
	private EduValueAdapter eduAdapter;
	private EduValueAdapter workAdapter;
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
	private RelativeLayout layParent;
	private JSONArray jsonAry = new JSONArray();
	private JSONObject jsonObj;
	private Button btnSave;
	private String pid = "";
	private String cid = "";
	private Dialog dialog;
	private ImageView basicAdd;
	private ImageView contactAdd;
	private ImageView socialAdd;
	private ImageView addressAdd;
	private ImageView eduAdd;
	private ImageView workAdd;
	private Calendar cal = Calendar.getInstance();
	private DataBase dbase = DataBase.getInstance();
	private SQLiteDatabase db = dbase.getWritableDatabase();
	private RelativeLayout layTop;

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
		setContentView(R.layout.user_info_edit);
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
	/**设置页面统计
	 * 
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getClass().getName() + "");
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getName() + "");
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
		layTop = (RelativeLayout) findViewById(R.id.top);
		layParent = (RelativeLayout) findViewById(R.id.parent);
		back = (ImageView) findViewById(R.id.back);
		avatar = (CircularImage) findViewById(R.id.avatar);
		editName = (EditText) findViewById(R.id.editName);
		btnSave = (Button) findViewById(R.id.btnSave);
		basicListView = (ListView) findViewById(R.id.basicListView);
		contactListView = (ListView) findViewById(R.id.contactListView);
		socialListView = (ListView) findViewById(R.id.socialListView);
		addressListView = (ListView) findViewById(R.id.addressListView);
		eduListView = (ListView) findViewById(R.id.eduListView);
		workListView = (ListView) findViewById(R.id.workListView);
		basicAdd = (ImageView) findViewById(R.id.basicAdd);
		contactAdd = (ImageView) findViewById(R.id.contactAdd);
		socialAdd = (ImageView) findViewById(R.id.socialAdd);
		addressAdd = (ImageView) findViewById(R.id.addressAdd);
		eduAdd = (ImageView) findViewById(R.id.eduAdd);
		workAdd = (ImageView) findViewById(R.id.workAdd);
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
		basicAdapter = new BasicValueAdapter(basicList, groupkey.get(0));
		contactAdapter = new ValueAdapter(contactList, groupkey.get(1));
		socialAdapter = new ValueAdapter(socialList, groupkey.get(2));
		addressAdapter = new ValueAdapter(addressList, groupkey.get(3));
		eduAdapter = new EduValueAdapter(eduList, groupkey.get(4));
		workAdapter = new EduValueAdapter(workList, groupkey.get(5));
		basicListView.setAdapter(basicAdapter);
		contactListView.setAdapter(contactAdapter);
		socialListView.setAdapter(socialAdapter);
		addressListView.setAdapter(addressAdapter);
		eduListView.setAdapter(eduAdapter);
		workListView.setAdapter(workAdapter);
		Utils.setListViewHeightBasedOnChildren(basicListView);
		Utils.setListViewHeightBasedOnChildren(contactListView);
		Utils.setListViewHeightBasedOnChildren(socialListView);
		Utils.setListViewHeightBasedOnChildren(addressListView);
		Utils.setListViewHeightBasedOnChildren(eduListView);
		Utils.setListViewHeightBasedOnChildren(workListView);

	}

	private void setListener() {
		back.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		basicAdd.setOnClickListener(new BtnAddClick(0));
		contactAdd.setOnClickListener(new BtnAddClick(1));
		socialAdd.setOnClickListener(new BtnAddClick(2));
		addressAdd.setOnClickListener(new BtnAddClick(3));
		eduAdd.setOnClickListener(new BtnAddClick(4));
		workAdd.setOnClickListener(new BtnAddClick(5));

	}

	class BasicValueAdapter extends BaseAdapter {
		List<Info> valuesList = new ArrayList<Info>();
		String tag;
		final int TYPE_1 = 0;
		final int TYPE_2 = 1;

		public BasicValueAdapter(List<Info> datalist, String strTag) {
			valuesList = datalist;
			tag = strTag;
		}

		@Override
		public int getCount() {
			return valuesList.size();
		}

		@Override
		public int getItemViewType(int position) {
			String key = valuesList.get(position).getKey();
			if (key.equals("性別")) {
				return TYPE_1;
			} else {
				return TYPE_2;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public Object getItem(int arg0) {
			return valuesList.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// ViewHolderValues holderValues = null;
			// String key = valuesList.get(position).getKey();
			ViewHolder1 holder1 = null;
			ViewHolder2 holder2 = null;
			int type = getItemViewType(position);
			if (convertView == null) {
				switch (type) {
				case TYPE_1:
					holder1 = new ViewHolder1();
					convertView = LayoutInflater
							.from(UserInfoEditActivity.this).inflate(
									R.layout.user_info_edit_gendar, null);
					holder1.btnDel = (ImageView) convertView
							.findViewById(R.id.btnDel);
					holder1.btnDel.setTag(tag);
					holder1.key = (TextView) convertView.findViewById(R.id.key);
					holder1.radioBoy = (RadioButton) convertView
							.findViewById(R.id.radioboy);
					holder1.radioGirl = (RadioButton) convertView
							.findViewById(R.id.radiogirl);
					convertView.setTag(holder1);
					break;
				case TYPE_2:
					convertView = LayoutInflater
							.from(UserInfoEditActivity.this)
							.inflate(
									R.layout.user_info_edit_list_item_key_value,
									null);
					holder2 = new ViewHolder2();
					holder2.btnDel = (ImageView) convertView
							.findViewById(R.id.btnDel);
					holder2.btnDel.setTag(tag);
					holder2.key = (TextView) convertView.findViewById(R.id.key);
					holder2.value = (EditText) convertView
							.findViewById(R.id.value);
					convertView.setTag(holder2);
					break;
				default:
					break;
				}
			} else {
				switch (type) {
				case TYPE_1:
					holder1 = (ViewHolder1) convertView.getTag();
					break;
				case TYPE_2:
					holder2 = (ViewHolder2) convertView.getTag();
					break;
				default:
					break;
				}
			}
			String key = valuesList.get(position).getKey();
			switch (type) {
			case TYPE_1:
				holder1.key.setText(key);
				holder1.btnDel.setEnabled(false);
				holder1.radioBoy.setClickable(false);
				holder1.radioGirl.setClickable(false);
				if (valuesList.get(position).getValue().equals("1")) {
					holder1.radioBoy.setChecked(true);
				} else if (valuesList.get(position).getValue().equals("2")) {
					holder1.radioGirl.setChecked(true);
				}
				break;
			case TYPE_2:

				if (key.equals("昵称") || key.equals("备注")) {
					holder2.btnDel.setEnabled(true);
					holder2.value.setEnabled(true);
					holder2.key.setText(key);
					holder2.value.setText(valuesList.get(position).getValue());
					int editType = valuesList.get(position).getEditType();
					holder2.value.addTextChangedListener(new EditTextWatcher(
							valuesList, position, editType));
				} else {
					holder2.value.setFocusable(false);
					holder2.btnDel.setEnabled(false);
					holder2.key.setText(key);
					holder2.value.setText(valuesList.get(position).getValue());
				}

				break;

			default:
				break;
			}

			return convertView;
		}
	}

	class ViewHolder1 {
		TextView key;
		RadioButton radioBoy;
		RadioButton radioGirl;
		ImageView btnDel;
	}

	class ViewHolder2 {
		TextView key;
		EditText value;
		ImageView btnDel;
	}

	class ValueAdapter extends BaseAdapter {
		List<Info> valuesList = new ArrayList<Info>();
		String tag;

		public ValueAdapter(List<Info> datalist, String strTag) {
			valuesList = datalist;
			tag = strTag;
		}

		@Override
		public int getCount() {
			return valuesList.size();
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolderValues holderValues = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(UserInfoEditActivity.this)
						.inflate(R.layout.user_info_edit_list_item_key_value,
								null);
				holderValues = new ViewHolderValues();
				holderValues.btnDel = (ImageView) convertView
						.findViewById(R.id.btnDel);
				holderValues.key = (TextView) convertView
						.findViewById(R.id.key);
				holderValues.value = (EditText) convertView
						.findViewById(R.id.value);
				convertView.setTag(holderValues);
			} else {
				holderValues = (ViewHolderValues) convertView.getTag();
			}

			holderValues.btnDel.setOnClickListener(new BtnDelClick(position,
					(String) holderValues.btnDel.getTag()));
			holderValues.btnDel.setTag(tag);
			int editType = valuesList.get(position).getEditType();
			String key = valuesList.get(position).getKey();
			if (Arrays.toString(UserInfoUtils.contacChinesetStr).contains(key)) {
				holderValues.value.setInputType(InputType.TYPE_CLASS_NUMBER);
			} else if (Arrays.toString(UserInfoUtils.socialChineseStr)
					.contains(key)) {
				holderValues.value
						.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
			}
			holderValues.key.setText(key);
			holderValues.value.addTextChangedListener(new EditTextWatcher(
					valuesList, position, editType));
			holderValues.value.setText(valuesList.get(position).getValue());
			return convertView;
		}
	}

	class ViewHolderValues {
		TextView key;
		EditText value;
		ImageView btnDel;

	}

	class EduValueAdapter extends BaseAdapter {
		List<Info> valuesList = new ArrayList<Info>();
		String tag;

		public EduValueAdapter(List<Info> datalist, String strTag) {
			valuesList = datalist;
			tag = strTag;
		}

		@Override
		public int getCount() {
			return valuesList.size();
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			EduHolderValues holderValues = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(UserInfoEditActivity.this)
						.inflate(R.layout.info_edu_word__edit_item, null);
				holderValues = new EduHolderValues();
				holderValues.btnDel = (ImageView) convertView
						.findViewById(R.id.btnDel);
				holderValues.key = (TextView) convertView
						.findViewById(R.id.key);
				holderValues.value = (EditText) convertView
						.findViewById(R.id.value);
				holderValues.startTime = (EditText) convertView
						.findViewById(R.id.startTime);
				holderValues.endTime = (EditText) convertView
						.findViewById(R.id.endTime);
				convertView.setTag(holderValues);
			} else {
				holderValues = (EduHolderValues) convertView.getTag();
			}
			holderValues.btnDel.setTag(tag);
			holderValues.btnDel.setOnClickListener(new BtnDelClick(position,
					(String) holderValues.btnDel.getTag()));
			holderValues.key.setText(valuesList.get(position).getKey());
			String value = valuesList.get(position).getValue();
			if (value.equals("")) {
				if (tag.equals(groupkey.get(4))) {
					holderValues.value.setHint("输入学校名称");
				} else {
					holderValues.value.setHint("输入单位名称");
				}
			}
			holderValues.value.setText(value);
			holderValues.startTime.setText(valuesList.get(position)
					.getStartDate());
			holderValues.endTime.setText(valuesList.get(position).getEndDate());
			int editType = valuesList.get(position).getEditType();
			holderValues.value.addTextChangedListener(new EditTextWatcher(
					valuesList, position, editType));
			holderValues.endTime.setOnClickListener(new OnTimeClick("endTime",
					valuesList, position, editType));
			holderValues.startTime.setOnClickListener(new OnTimeClick(
					"startTime", valuesList, position, editType));
			return convertView;
		}
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
			basicAdapter.notifyDataSetChanged();
		}
		eduAdapter.notifyDataSetChanged();
		workAdapter.notifyDataSetChanged();
	}

	class EduHolderValues {
		TextView key;
		EditText value;
		EditText startTime;
		EditText endTime;
		ImageView btnDel;
	}

	class EduEditOnClick implements OnClickListener {
		public EduEditOnClick() {
		}

		@Override
		public void onClick(View v) {

		}

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
				basicAdapter.notifyDataSetChanged();
				Utils.setListViewHeightBasedOnChildren(basicListView);
			} else if (tag.equals(groupkey.get(1))) {
				if (contactList.get(position).getEditType() != 2) {
					BuildDelJson(contactList.get(position).getType(),
							contactList.get(position).getValue(), contactList
									.get(position).getId());
				}
				contactList.remove(position);
				contactAdapter.notifyDataSetChanged();
				Utils.setListViewHeightBasedOnChildren(contactListView);

			} else if (tag.equals(groupkey.get(2))) {
				if (socialList.get(position).getEditType() != 2) {
					BuildDelJson(socialList.get(position).getType(), socialList
							.get(position).getValue(), socialList.get(position)
							.getId());
				}
				socialList.remove(position);
				socialAdapter.notifyDataSetChanged();
				Utils.setListViewHeightBasedOnChildren(socialListView);

			} else if (tag.equals(groupkey.get(3))) {
				if (addressList.get(position).getEditType() != 2) {
					BuildDelJson(addressList.get(position).getType(),
							addressList.get(position).getValue(), addressList
									.get(position).getId());
				}
				addressList.remove(position);
				addressAdapter.notifyDataSetChanged();
				Utils.setListViewHeightBasedOnChildren(addressListView);

			} else if (tag.equals(groupkey.get(4))) {
				if (eduList.get(position).getEditType() != 2) {
					BuildDelJson(eduList.get(position).getType(),
							eduList.get(position).getValue(),
							eduList.get(position).getId());
				}
				eduList.remove(position);
				eduAdapter.notifyDataSetChanged();
				Utils.setListViewHeightBasedOnChildren(eduListView);

			} else if (tag.equals(groupkey.get(5))) {
				if (workList.get(position).getEditType() != 2) {
					BuildDelJson(workList.get(position).getType(), workList
							.get(position).getValue(), workList.get(position)
							.getId());
				}
				workList.remove(position);
				workAdapter.notifyDataSetChanged();
				Utils.setListViewHeightBasedOnChildren(workListView);

			}
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
					UserInfoEditActivity.this, layParent, array, "标签");
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
						basicAdapter.notifyDataSetChanged();
						Utils.setListViewHeightBasedOnChildren(basicListView);
						break;
					case 1:
						contactList.add(info);
						contactAdapter.notifyDataSetChanged();
						Utils.setListViewHeightBasedOnChildren(contactListView);
						break;
					case 2:
						socialList.add(info);
						socialAdapter.notifyDataSetChanged();
						Utils.setListViewHeightBasedOnChildren(socialListView);
						break;
					case 3:
						addressList.add(info);
						addressAdapter.notifyDataSetChanged();
						Utils.setListViewHeightBasedOnChildren(addressListView);
						break;
					case 4:
						eduList.add(info);
						eduAdapter.notifyDataSetChanged();
						Utils.setListViewHeightBasedOnChildren(eduListView);
						break;
					case 5:
						workList.add(info);
						workAdapter.notifyDataSetChanged();
						Utils.setListViewHeightBasedOnChildren(workListView);
						break;
					default:
						break;
					}

				}

			});

			pop.show();
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
