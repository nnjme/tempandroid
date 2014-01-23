package com.changlianxi.activity;

import java.io.File;
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
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.db.DBUtils;
import com.changlianxi.db.DataBase;
import com.changlianxi.inteface.UpLoadPic;
import com.changlianxi.modle.Info;
import com.changlianxi.modle.MyCardAvatar;
import com.changlianxi.modle.SelectPicModle;
import com.changlianxi.popwindow.AddKeyAndValuePopwindow;
import com.changlianxi.popwindow.AddKeyAndValuePopwindow.OnSelectKey;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.task.UpLoadPicAsyncTask;
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
import com.umeng.analytics.MobclickAgent;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * 我的名片修改界面
 * 
 * @author teeker_bin
 * 
 */
@TargetApi(16)
public class MyCardEditActivity extends BaseActivity implements
		OnClickListener, PostCallBack, UpLoadPic {
	private ListView basicListView;
	private ListView contactListView;
	private ListView socialListView;
	private ListView addressListView;
	private ListView eduListView;
	private ListView workListView;
	private ValueAdapter basicAdapter;
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
	private String strOldName;
	private String avatarURL;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private RelativeLayout layParent;
	private JSONArray jsonAry = new JSONArray();
	private JSONObject jsonObj;
	private Button btnSave;
	private String pid = "";
	private Dialog dialog;
	private ImageView basicAdd;
	private ImageView contactAdd;
	private ImageView socialAdd;
	private ImageView addressAdd;
	private ImageView eduAdd;
	private ImageView workAdd;
	private SelectPicPopwindow pop;
	private RelativeLayout avatarLay;
	private String selectPicPath = "";
	private ImageView avatarReplace;
	private MyCardAvatar modle;
	private Calendar cal = Calendar.getInstance();
	private String nameID;
	private DataBase dbase = DataBase.getInstance();
	private SQLiteDatabase db = dbase.getWritableDatabase();
	private RelativeLayout layTop;
	private boolean isCamera;
	private String upLoadPath = "";
	private Bitmap loadBmp = null;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				for (int i = basicList.size() - 1; i >= 0; i--) {
					String type = basicList.get(i).getType();
					if (type.equals("D_NAME")) {
						nameID = basicList.get(i).getId();
						basicList.remove(i);
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
		modle = new MyCardAvatar();
		pid = DBUtils.getPidByUid(SharedUtils.getString("uid", ""));
		strName = getIntent().getStringExtra("name");
		avatarURL = getIntent().getStringExtra("avatar");
		strOldName = strName;
		if (!avatarURL.startsWith("http")) {
			avatarURL = "file://" + avatarURL;
		}
		initView();
		initData();
		setListener();
		mHandler.sendEmptyMessageDelayed(1, 100);
	}

	/**设置页面统计
	 * 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getName() + "");
	}
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getName() + "");
	}
	
	@SuppressWarnings("unchecked")
	private void getData() {
		new Thread() {
			public void run() {
				getMyDetails();
				mHandler.sendEmptyMessage(0);
			}
		}.start();

	}

	/**
	 * 获取成员资料信息
	 * 
	 * @param view
	 * @param pid
	 */
	private void getMyDetails() {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		Cursor cursor = db.query(Constants.MYDETAIL, null, null, null, null,
				null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String tid = cursor.getString(cursor.getColumnIndex("tid"));
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
		for (int i = 0; i < UserInfoUtils.basicStr.length; i++) {
			if (key.equals(UserInfoUtils.basicStr[i])) {
				typekey = UserInfoUtils.convertToChines(key);
				info.setKey(typekey);
				basicList.add(info);
			}
		}
		if (Arrays.toString(UserInfoUtils.socialStr).contains(key)) {
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

		} else if (Arrays.toString(UserInfoUtils.eduStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setStartDate(start);
			info.setEndDate(end);
			eduList.add(info);
		} else if (Arrays.toString(UserInfoUtils.workStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setStartDate(start);
			info.setEndDate(end);
			workList.add(info);
		}

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
		editName.setText(strName);
		editName.setEnabled(true);
		setAvatar();
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
		avatarLay = (RelativeLayout) findViewById(R.id.avatarLay);
		avatarReplace = (ImageView) findViewById(R.id.avatarRelace);
		avatarReplace.setVisibility(View.VISIBLE);
		avatarReplace.getBackground().setAlpha(200);
	}

	private void setValuesAdapter() {
		basicAdapter = new ValueAdapter(basicList, groupkey.get(0));
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
		avatarLay.setOnClickListener(this);
	}

	private void setAvatar() {
		imageLoader.loadImage(avatarURL, options, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String arg0, View arg1) {

			}

			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {

			}

			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap bmp) {
				if (bmp == null) {
					avatar.setImageResource(R.drawable.head_bg);
					return;
				}
				avatar.setImageBitmap(bmp);
				// layTop.setBackground(BitmapUtils.convertBimapToDrawable(bmp));
				layTop.setBackgroundDrawable(BitmapUtils
						.convertBimapToDrawable(bmp));

			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {

			}
		});

	}

	class ValueAdapter extends BaseAdapter {
		List<Info> valuesList = new ArrayList<Info>();
		String tag;
		final int TYPE_1 = 0;
		final int TYPE_2 = 1;

		public ValueAdapter(List<Info> datalist, String strTag) {
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
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder1 holder1 = null;
			ViewHolder2 holder2 = null;
			int type = getItemViewType(position);
			if (convertView == null) {
				switch (type) {
				case TYPE_1:
					holder1 = new ViewHolder1();
					convertView = LayoutInflater.from(MyCardEditActivity.this)
							.inflate(R.layout.user_info_edit_gendar, null);
					holder1.btnDel = (ImageView) convertView
							.findViewById(R.id.btnDel);
					holder1.key = (TextView) convertView.findViewById(R.id.key);
					holder1.gendarGroup = (RadioGroup) convertView
							.findViewById(R.id.gendarRradioGroup);
					holder1.radioBoy = (RadioButton) convertView
							.findViewById(R.id.radioboy);
					holder1.radioGirl = (RadioButton) convertView
							.findViewById(R.id.radiogirl);
					convertView.setTag(holder1);
					break;
				case TYPE_2:
					convertView = LayoutInflater
							.from(MyCardEditActivity.this)
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
				holder1.btnDel.setTag(tag);
				holder1.btnDel.setOnClickListener(new BtnDelClick(position,
						(String) holder1.btnDel.getTag()));
				holder1.key.setText(valuesList.get(position).getKey());
				if (valuesList.get(position).getValue().equals("1")) {
					holder1.radioBoy.setChecked(true);
				} else if (valuesList.get(position).getValue().equals("2")) {
					holder1.radioGirl.setChecked(true);
				}
				holder1.gendarGroup
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(RadioGroup group,
									int checkedId) {
								// 获取变更后的选中项的ID
								int radioButtonId = group
										.getCheckedRadioButtonId();
								if (valuesList.get(position).getEditType() != 2) {
									valuesList.get(position).setEditType(3);
								}
								if (radioButtonId == R.id.radioboy) {
									valuesList.get(position).setValue("1");
								} else if (radioButtonId == R.id.radiogirl) {
									valuesList.get(position).setValue("2");
								}
							}
						});
				break;
			case TYPE_2:
				holder2.btnDel.setTag(tag);
				holder2.btnDel.setOnClickListener(new BtnDelClick(position,
						(String) holder2.btnDel.getTag()));
				int editType = valuesList.get(position).getEditType();
				if (key.equals("QQ")) {
					holder2.value.setInputType(InputType.TYPE_CLASS_NUMBER);
				} else if (Arrays.toString(UserInfoUtils.contacChinesetStr)
						.contains(key)) {
					holder2.value.setInputType(InputType.TYPE_CLASS_NUMBER);
				} else if (Arrays.toString(UserInfoUtils.socialChineseStr)
						.contains(key)) {
					holder2.value
							.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
				} else if (key.equals("生日")) {
					holder2.value.setFocusable(false);
					holder2.value.setOnClickListener(new OnTimeClick("生日",
							valuesList, position, editType));
				}
				holder2.key.setText(key);
				holder2.value.addTextChangedListener(new EditTextWatcher(
						valuesList, position, editType));
				holder2.value.setText(valuesList.get(position).getValue());
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
		RadioGroup gendarGroup;
		RadioButton radioGirl;
		ImageView btnDel;
	}

	class ViewHolder2 {
		TextView key;
		EditText value;
		ImageView btnDel;
	}

	class ViewHolderValues {
		TextView key;
		EditText value;
		ImageView btnDel;
		RadioGroup gendarGroup;
		RadioButton radioBoy;
		RadioButton radioGirl;

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
				convertView = LayoutInflater.from(MyCardEditActivity.this)
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

	class EduHolderValues {
		TextView key;
		EditText value;
		EditText startTime;
		EditText endTime;
		ImageView btnDel;
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
	 * 新增职务json串
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
					String ketType = contactList.get(position).getType();
					if (ketType.equals("D_CELLPHONE")) {
						Utils.showToast("注册手机号不能被删除");
						return;
					}
					BuildDelJson(ketType, contactList.get(position).getValue(),
							contactList.get(position).getId());
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
				array = UserInfoUtils.basicChineseStr;
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
				list = Arrays.asList(UserInfoUtils.basicChineseStr);
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
					MyCardEditActivity.this, layParent, array, "标签");
			pop.setCallBack(new OnSelectKey() {

				@Override
				public void getSelectKey(String str) {
					Info info = new Info();
					info.setValue("");
					info.setKey(str);
					info.setType(UserInfoUtils.convertToEnglish(str));
					info.setEditType(2);
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
		db.insert(Constants.MYDETAIL, null, cv);
	}

	private void upDateDB(ContentValues cv, String tid) {
		db.update(Constants.MYDETAIL, cv, "tID=?", new String[] { tid });
	}

	/**
	 * 构建上传的字符串
	 */
	private void BuildJson() {
		String naem = editName.getText().toString();
		if (!naem.equals(strOldName)) {
			BuildEditJson("D_NAME", naem, nameID);
			ContentValues values = new ContentValues();
			values.put("userName", naem);
			DBUtils.updateInfo(Constants.USERLIST_TABLE, values, "userID=?",
					new String[] { SharedUtils.getString("uid", "") });
		}
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
			Intent it = new Intent();
			Bundle bundle = new Bundle();
			bundle.putParcelable("avatar", modle.getBitmap());
			it.putExtra("avatarPath", selectPicPath);
			it.putExtra("name", editName.getText().toString());
			it.putExtras(bundle);
			setResult(2, it);
			finish();
			Utils.rightOut(this);
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("pid", pid);
		map.put("token", SharedUtils.getString("token", ""));
		map.put("person", jsonAry.toString());

		dialog = DialogUtil.getWaitDialog(this, "请稍后");
		dialog.show();
		PostAsyncTask task = new PostAsyncTask(this, map, "/people/imyEdit");
		task.setTaskCallBack(this);
		task.execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD
				&& resultCode == RESULT_OK && data != null) {
			SelectPicModle modle = BitmapUtils.getPickPic(this, data);
			selectPicPath = modle.getPicPath();
			upLoadPath = BitmapUtils.startPhotoZoom(this, data.getData());
			isCamera = false;

		}// 拍摄图片
		else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
			if (resultCode != RESULT_OK) {
				return;
			}
			String fileName = pop.getTakePhotoPath();
			selectPicPath = fileName;
			upLoadPath = BitmapUtils.startPhotoZoom(this,
					Uri.fromFile(new File(fileName)));
			isCamera = true;

		} else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_DROP
				&& data != null) {
			if (isCamera) {
				File file = new File(selectPicPath);
				if (file.isFile() && file.exists()) {
					file.delete();
				}
			}
			Bundle extras = data.getExtras();
			if (extras != null) {
				loadBmp = extras.getParcelable("data");
			}

			upLoadAvatar();
		}

	}

	/**
	 * 上传头像
	 */
	public void upLoadAvatar() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("pid", pid);
		UpLoadPicAsyncTask picTask = new UpLoadPicAsyncTask(map,
				"/people/iuploadMyAvatar", upLoadPath, "avatar");
		picTask.setCallBack(this);
		picTask.execute();
		dialog = DialogUtil.getWaitDialog(this, "头像上传中");
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			Intent it = new Intent();
			Bundle bundle = new Bundle();
			bundle.putParcelable("avatar", modle.getBitmap());
			it.putExtra("avatarPath", selectPicPath);
			it.putExtra("name", editName.getText().toString());
			it.putExtras(bundle);
			setResult(2, it);
			finish();
			Utils.rightOut(this);
			break;
		case R.id.btnSave:
			BuildJson();
			break;
		case R.id.avatarLay:
			pop = new SelectPicPopwindow(this, v);
			pop.show();
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void upLoadFinish(boolean flag) {
		dialog.dismiss();
		if (flag) {
			Utils.showToast("上传成功");
			modle.setBitmap(loadBmp);
			avatar.setImageBitmap(loadBmp);
			// layTop.setBackground(BitmapUtils.convertBimapToDrawable(loadBmp));
			layTop.setBackgroundDrawable(BitmapUtils
					.convertBimapToDrawable(loadBmp));
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
			bundle.putParcelable("avatar", modle.getBitmap());
			bundle.putSerializable("basicList", (Serializable) basicList);
			bundle.putSerializable("contactList", (Serializable) contactList);
			bundle.putSerializable("socialList", (Serializable) socialList);
			bundle.putSerializable("addressList", (Serializable) addressList);
			bundle.putSerializable("eduList", (Serializable) eduList);
			bundle.putSerializable("workList", (Serializable) workList);
			it.putExtra("name", editName.getText().toString());
			it.putExtra("avatarPath", selectPicPath);
			it.putExtras(bundle);
			setResult(2, it);
			finish();
			Utils.rightOut(this);
		} else {
			Utils.showToast(ErrorCodeUtil.convertToChines(errCode));
		}
	}
}
