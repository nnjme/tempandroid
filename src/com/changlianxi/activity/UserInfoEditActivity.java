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
import android.content.Intent;
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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.changlianxi.modle.Info;
import com.changlianxi.popwindow.AddKeyAndValuePopwindow;
import com.changlianxi.popwindow.AddKeyAndValuePopwindow.OnSelectKey;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
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

public class UserInfoEditActivity extends BaseActivity implements
		OnClickListener, PostCallBack {
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
	private Bundle bundle;
	private Calendar cal = Calendar.getInstance();
	private RelativeLayout topBg;
	private ScrollView scroll;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				editName.setText(strName);
				setAvatar();
				setValuesAdapter();
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
		options = CLXApplication.getOptions();
		initView();
		initData();
		setListener();
		new getDateThread().start();
	}

	@SuppressWarnings("unchecked")
	private void getData() {
		bundle = getIntent().getExtras();
		cid = getIntent().getStringExtra("cid");
		pid = getIntent().getStringExtra("pid");
		strName = getIntent().getStringExtra("name");
		avatarURL = getIntent().getStringExtra("avatar");
		basicList = (List<Info>) bundle.getSerializable("basicList");
		contactList = (List<Info>) bundle.getSerializable("contactList");
		socialList = (List<Info>) bundle.getSerializable("socialList");
		addressList = (List<Info>) bundle.getSerializable("addressList");
		eduList = (List<Info>) bundle.getSerializable("eduList");
		workList = (List<Info>) bundle.getSerializable("workList");
		for (int i = basicList.size() - 1; i >= 0; i--) {
			String type = basicList.get(i).getType();
			if (type.equals("D_NAME")) {
				basicList.remove(i);
			}
		}

	}

	class getDateThread extends Thread {
		public void run() {
			getData();
			mHandler.sendEmptyMessage(0);

		}

	}

	public void initData() {
		for (int i = 0; i < UserInfoUtils.infoTitleKey.length; i++) {
			groupkey.add(UserInfoUtils.infoTitleKey[i]);
		}
	}

	private void initView() {
		topBg = (RelativeLayout) findViewById(R.id.top);
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
		scroll = (ScrollView) findViewById(R.id.scrollView1);

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
					avatar.setImageResource(R.drawable.pic);
					return;
				}
				avatar.setImageBitmap(bmp);
				// topBg.setBackground(BitmapUtils.convertBimapToDrawable(bmp));

			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {

			}
		});
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
			String key = valuesList.get(position).getKey();
			// System.out.println("key::::" + key);
			// if ("性别".contains(key)) {
			// System.out.println("性别性别性别");
			// holderValues = new ViewHolderValues();
			// convertView = LayoutInflater.from(UserInfoEditActivity.this)
			// .inflate(R.layout.user_info_edit_gendar, null);
			// final View v = convertView;
			// holderValues.btnDel = (ImageView) convertView
			// .findViewById(R.id.btnDel);
			// holderValues.btnDel.setTag(tag);
			// holderValues.key = (TextView) convertView
			// .findViewById(R.id.key);
			// holderValues.btnDel.setOnClickListener(new BtnDelClick(
			// position, (String) holderValues.btnDel.getTag()));
			// holderValues.key.setText(valuesList.get(position).getKey());
			// holderValues.gendarGroup = (RadioGroup) convertView
			// .findViewById(R.id.gendarRradioGroup);
			// holderValues.gendarGroup
			// .setOnCheckedChangeListener(new OnCheckedChangeListener() {
			//
			// @Override
			// public void onCheckedChanged(RadioGroup group,
			// int checkedId) {
			// // 获取变更后的选中项的ID
			// int radioButtonId = group
			// .getCheckedRadioButtonId();
			// // 根据ID获取RadioButton的实例
			// RadioButton rb = (RadioButton) v
			// .findViewById(radioButtonId);
			// // 更新文本内容，以符合选中项
			// Utils.showToast("您的性别是：" + rb.getText());
			// }
			// });
			// } else {

			convertView = LayoutInflater.from(UserInfoEditActivity.this)
					.inflate(R.layout.user_info_edit_list_item_key_value, null);
			holderValues = new ViewHolderValues();
			holderValues.btnDel = (ImageView) convertView
					.findViewById(R.id.btnDel);
			holderValues.btnDel.setTag(tag);
			holderValues.key = (TextView) convertView.findViewById(R.id.key);
			holderValues.value = (TextView) convertView
					.findViewById(R.id.value);

			holderValues.btnDel.setOnClickListener(new BtnDelClick(position,
					(String) holderValues.btnDel.getTag()));
			final int editType = valuesList.get(position).getEditType();
			if (Arrays.toString(UserInfoUtils.contacChinesetStr).contains(key)) {
				holderValues.value.setInputType(InputType.TYPE_CLASS_NUMBER);
			} else if (Arrays.toString(UserInfoUtils.socialChineseStr)
					.contains(key)) {
				holderValues.value
						.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
			} else if (key.equals("生日")) {
				holderValues.value.setFocusable(false);
				holderValues.value.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						showDateDialog(valuesList, position, "生日", editType);
					}
				});
			}
			holderValues.key.setText(key);
			holderValues.value.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {

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
			});
			holderValues.value.setText(valuesList.get(position).getValue());
			// }
			return convertView;
		}
	}

	class ViewHolderValues {
		TextView key;
		TextView value;
		ImageView btnDel;
		RadioGroup gendarGroup;
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
			convertView = LayoutInflater.from(UserInfoEditActivity.this)
					.inflate(R.layout.info_edu_word__edit_item, null);
			holderValues = new EduHolderValues();
			holderValues.btnDel = (ImageView) convertView
					.findViewById(R.id.btnDel);
			holderValues.btnDel.setTag(tag);
			holderValues.key = (TextView) convertView.findViewById(R.id.key);
			holderValues.value = (EditText) convertView
					.findViewById(R.id.value);
			holderValues.startTime = (EditText) convertView
					.findViewById(R.id.startTime);
			holderValues.endTime = (EditText) convertView
					.findViewById(R.id.endTime);
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
			final int editType = valuesList.get(position).getEditType();
			holderValues.value.addTextChangedListener(new EduTextWatcher(
					valuesList, position, editType));
			holderValues.endTime.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showDateDialog(valuesList, position, "endTime", editType);
				}
			});
			holderValues.startTime.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showDateDialog(valuesList, position, "startTime", editType);

				}
			});
			return convertView;
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

	class EduTextWatcher implements TextWatcher {
		List<Info> valuesList;
		int position;
		int editType;

		public EduTextWatcher(List<Info> valuesList, int position, int type) {
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
					// scroll.post(new Runnable() {
					// public void run() {
					// scroll.fullScroll(ScrollView.FOCUS_DOWN);
					// }
					// });
				}

			});

			pop.show();
		}
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
				BuildAddJson(keyType, value);
			} else if (editType == 3) {
				String keyType = basicList.get(i).getType();
				String value = basicList.get(i).getValue();
				BuildEditJson(keyType, value, basicList.get(i).getId());
			}
		}
		for (int i = 0; i < contactList.size(); i++) {
			int editType = contactList.get(i).getEditType();
			if (editType == 2) {
				String keyType = contactList.get(i).getType();
				String value = contactList.get(i).getValue();
				BuildAddJson(keyType, value);
			} else if (editType == 3) {
				String keyType = contactList.get(i).getType();
				String value = contactList.get(i).getValue();
				BuildEditJson(keyType, value, contactList.get(i).getId());
			}

		}
		for (int i = 0; i < socialList.size(); i++) {
			int editType = socialList.get(i).getEditType();
			if (editType == 2) {
				String keyType = socialList.get(i).getType();
				String value = socialList.get(i).getValue();
				BuildAddJson(keyType, value);
			} else if (editType == 3) {
				String keyType = socialList.get(i).getType();
				String value = socialList.get(i).getValue();
				BuildEditJson(keyType, value, socialList.get(i).getId());
			}

		}
		for (int i = 0; i < addressList.size(); i++) {
			int editType = addressList.get(i).getEditType();
			if (editType == 2) {
				String keyType = addressList.get(i).getType();
				String value = addressList.get(i).getValue();
				BuildAddJson(keyType, value);
			} else if (editType == 3) {
				String keyType = addressList.get(i).getType();
				String value = addressList.get(i).getValue();
				BuildEditJson(keyType, value, addressList.get(i).getId());
			}

		}

		for (int i = 0; i < eduList.size(); i++) {
			int editType = eduList.get(i).getEditType();
			String keyType = eduList.get(i).getType();
			String value = eduList.get(i).getValue();
			String start = eduList.get(i).getStartDate();
			String end = eduList.get(i).getEndDate();
			if (editType == 2) {
				BuildAddEduAndWorkJson(keyType, value, start, end);
			} else if (editType == 3) {
				BuildEditEduAndWorkJson(keyType, value, eduList.get(i).getId(),
						start, end);
			}
		}
		for (int i = 0; i < workList.size(); i++) {
			int editType = workList.get(i).getEditType();
			String keyType = workList.get(i).getType();
			String value = workList.get(i).getValue();
			String start = workList.get(i).getStartDate();
			String end = workList.get(i).getEndDate();
			if (editType == 2) {
				BuildAddEduAndWorkJson(keyType, value, start, end);
			} else if (editType == 3) {
				BuildEditEduAndWorkJson(keyType, value,
						workList.get(i).getId(), start, end);
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
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cid", cid);
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("pid", pid);
		map.put("token", SharedUtils.getString("token", ""));
		map.put("person", jsonAry.toString());
		dialog = DialogUtil.getWaitDialog(this, "请稍后");
		dialog.show();
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
