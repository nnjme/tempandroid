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

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.UserInfoUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

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
	private Bundle bundle;
	private SelectPicPopwindow pop;
	private RelativeLayout avatarLay;
	private String selectPicPath = "";
	private ImageView avatarReplace;
	private MyCardAvatar modle;
	private Calendar cal = Calendar.getInstance();
	private String nameID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info_edit);
		getData();
		imageLoader = CLXApplication.getImageLoader();
		options = CLXApplication.getOptions();
		modle = new MyCardAvatar();
		initView();
		initData();
		setValuesAdapter();
		setListener();
	}

	@SuppressWarnings("unchecked")
	private void getData() {
		bundle = getIntent().getExtras();
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
				nameID = basicList.get(i).getId();
				basicList.remove(i);
			}
		}
	}

	public void initData() {
		for (int i = 0; i < UserInfoUtils.infoTitleKey.length; i++) {
			groupkey.add(UserInfoUtils.infoTitleKey[i]);
		}
	}

	private void initView() {
		layParent = (RelativeLayout) findViewById(R.id.parent);
		back = (ImageView) findViewById(R.id.back);
		avatar = (CircularImage) findViewById(R.id.avatar);
		editName = (EditText) findViewById(R.id.editName);
		editName.setText(strName);
		editName.setEnabled(true);
		imageLoader.displayImage(avatarURL, avatar, options);
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

			convertView = LayoutInflater.from(MyCardEditActivity.this).inflate(
					R.layout.user_info_edit_list_item_key_value, null);
			holderValues = new ViewHolderValues();
			holderValues.btnDel = (ImageView) convertView
					.findViewById(R.id.btnDel);
			holderValues.btnDel.setTag(tag);
			holderValues.key = (TextView) convertView.findViewById(R.id.key);
			holderValues.value = (EditText) convertView
					.findViewById(R.id.value);
			holderValues.btnDel.setOnClickListener(new BtnDelClick(position,
					(String) holderValues.btnDel.getTag()));
			String key = valuesList.get(position).getKey();
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
			convertView = LayoutInflater.from(MyCardEditActivity.this).inflate(
					R.layout.info_edu_word__edit_item, null);
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

	/**
	 * 构建上传的字符串
	 */
	private void BuildJson() {
		if (!strName.equals(editName.getText().toString())) {
			BuildEditJson("D_NAME", editName.getText().toString(), nameID);
		}
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
			if (editType == 2) {
				String keyType = workList.get(i).getType();
				String value = workList.get(i).getValue();
				BuildAddJson(keyType, value);
			} else if (editType == 3) {
				String keyType = workList.get(i).getType();
				String value = workList.get(i).getValue();
				BuildEditJson(keyType, value, workList.get(i).getId());
			}

		}
		upLoadEditDetails();
	}

	private void upLoadEditDetails() {
		if (jsonAry.length() == 0) {
			Intent it = new Intent();
			Bundle bundle = new Bundle();
			bundle.putParcelable("avatar", modle.getBitmap());
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
		Bitmap bitmap = null;
		if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD
				&& resultCode == RESULT_OK && data != null) {
			SelectPicModle modle = BitmapUtils.getPickPic(this, data);
			selectPicPath = modle.getPicPath();
			BitmapUtils.startPhotoZoom(this, data.getData());

		}// 拍摄图片
		else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
			if (resultCode != RESULT_OK) {
				return;
			}
			String fileName = pop.getTakePhotoPath();
			selectPicPath = fileName;
			BitmapUtils.startPhotoZoom(this, Uri.fromFile(new File(fileName)));
		} else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_DROP
				&& data != null) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				bitmap = photo;
			}
			modle.setBitmap(bitmap);
			avatar.setImageBitmap(bitmap);
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
				"/people/iuploadMyAvatar", selectPicPath, "avatar");
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

	@Override
	public void upLoadFinish(boolean flag) {
		dialog.dismiss();
		if (flag) {
			Utils.showToast("上传成功");
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
			it.putExtras(bundle);
			setResult(2, it);
			finish();
			Utils.rightOut(this);
		} else {
			Utils.showToast(ErrorCodeUtil.convertToChines(errCode));
		}
	}
}
