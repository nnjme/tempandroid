package com.changlianxi.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.activity.CLXApplication;
import com.changlianxi.activity.MyCardEditActivity;
import com.changlianxi.db.DataBase;
import com.changlianxi.modle.Info;
import com.changlianxi.task.GetMyDetailTask;
import com.changlianxi.task.GetMyDetailTask.GetMyDetailValues;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.UserInfoUtils;
import com.changlianxi.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * 用户资料显示View 主要对用户资料的分类显示
 * 
 * @author teeker_bin
 * 
 */
public class MyCardShow1 implements OnClickListener, GetMyDetailValues {
	private Context mContext;
	private View myCard;
	private List<String> showGroupkey = new ArrayList<String>();
	private List<Info> showBasicList = new ArrayList<Info>();// 存放基本信息数据
	private List<Info> showContactList = new ArrayList<Info>();// 存放联系方式数据
	private List<Info> showSocialList = new ArrayList<Info>();// 存放社交账号数据
	private List<Info> showAddressList = new ArrayList<Info>();// 存放地址数据
	private List<Info> showEduList = new ArrayList<Info>();// 存放教育经历
	private List<Info> showWorkList = new ArrayList<Info>();// 存放工作经历
	private OnButtonClickListener callBack;
	private ImageView back;
	private String strName;
	private String avatarURL;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private TextView txtname;
	private CircularImage avatar;
	private Button btnEdit;
	private ValueAdapter basicAdapter;
	private ValueAdapter socialAdapter;
	private ContactValueAdapter contactAdapter;
	private ValueAdapter addressAdapter;
	private EduValueAdapter eduAdapter;
	private EduValueAdapter workAdapter;
	private ListView basicListView;
	private ListView contactListView;
	private ListView socialListView;
	private ListView addressListView;
	private ListView eduListView;
	private ListView workListView;
	private Dialog dialog;
	private DataBase dbase = DataBase.getInstance();
	private SQLiteDatabase db = dbase.getWritableDatabase();
	private LinearLayout laybasic;
	private LinearLayout laycontact;
	private LinearLayout laysocial;
	private LinearLayout layadress;
	private LinearLayout layedu;
	private LinearLayout layword;
	private LinearLayout layChild;
	private boolean changed;
	private LinearLayout layChanged;
	private RelativeLayout layTop;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				initData();
				getMyDetails();
				setValuesAdapter();
				getDetailsFromServer();
				break;
			default:
				break;
			}
		}
	};

	public MyCardShow1(Context context) {
		this.mContext = context;
		imageLoader = CLXApplication.getImageLoader();
		options = CLXApplication.getOptions();
		initView();
		setOnClickListener();
		mHandler.sendEmptyMessageDelayed(0, 400);
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
		String change = "0";
		Cursor cursor = db.query(Constants.MYDETAIL, null, null, null, null,
				null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String tid = cursor.getString(cursor.getColumnIndex("tid"));
				String key = cursor.getString(cursor.getColumnIndex("key"));
				String value = cursor.getString(cursor.getColumnIndex("value"));
				if (key.equals("D_AVATAR")) {
					avatarURL = value;
				} else if (key.equals("D_NAME")) {
					strName = value;
				}
				String start = cursor.getString(cursor
						.getColumnIndex("startDate"));
				String end = cursor.getString(cursor.getColumnIndex("endDate"));
				change = cursor.getString(cursor.getColumnIndex("changed"));
				valuesClassification(tid, key, value, start, end);
				cursor.moveToNext();
			}
		}
		txtname.setText(strName);
		// imageLoader.displayImage(avatarURL, avatar, options);
		loadAvatar();
		isDetailChange(change);
		cursor.close();
	}

	private void loadAvatar() {
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
				layTop.setBackground(BitmapUtils.convertBimapToDrawable(bmp));

			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {

			}
		});

	}

	public void isDetailChange(String change) {
		changed = change.equals("1") ? true : false;
		if (changed) {
			layChanged.setVisibility(View.VISIBLE);
		} else {
			layChanged.setVisibility(View.GONE);
		}
	}

	private void setLayVisible() {
		if (showBasicList.size() == 0) {
			laybasic.setVisibility(View.GONE);
		} else {
			laybasic.setVisibility(View.VISIBLE);
		}
		if (showContactList.size() == 0) {
			laycontact.setVisibility(View.GONE);
		} else {
			laycontact.setVisibility(View.VISIBLE);
		}
		if (showSocialList.size() == 0) {
			laysocial.setVisibility(View.GONE);
		} else {
			laysocial.setVisibility(View.VISIBLE);
		}
		if (showAddressList.size() == 0) {
			layadress.setVisibility(View.GONE);
		} else {
			layadress.setVisibility(View.VISIBLE);
		}
		if (showEduList.size() == 0) {
			layedu.setVisibility(View.GONE);
		} else {
			layedu.setVisibility(View.VISIBLE);
		}
		if (showWorkList.size() == 0) {
			layword.setVisibility(View.GONE);
		} else {
			layword.setVisibility(View.VISIBLE);
		}
		List<View> view = new ArrayList<View>();
		for (int i = 0; i < layChild.getChildCount(); i++) {
			View v = layChild.getChildAt(i);
			if (v.getVisibility() == View.VISIBLE) {
				view.add(v);
			}

		}
		for (int i = 0; i < view.size(); i++) {
			if (i % 2 == 0) {
				view.get(i).setBackgroundColor(Color.WHITE);
			} else {
				view.get(i).setBackgroundColor(
						mContext.getResources().getColor(R.color.f6));

			}
		}
	}

	private void getDetailsFromServer() {
		if (showBasicList.size() == 0) {
			dialog = DialogUtil.getWaitDialog(mContext, "请稍后");
			dialog.show();
		}
		GetMyDetailTask task = new GetMyDetailTask();
		task.setValuesCallBack(this);
		task.execute();

	}

	public void setName(String strName) {
		txtname.setText(strName);
	}

	private void delName() {
		for (int i = showBasicList.size() - 1; i >= 0; i--) {
			String type = showBasicList.get(i).getType();
			if (type.equals("D_NAME")) {
				showBasicList.remove(i);
			}
		}
	}

	private void initView() {
		myCard = LayoutInflater.from(mContext).inflate(R.layout.my_card_show,
				null);
		back = (ImageView) myCard.findViewById(R.id.back);
		layTop = (RelativeLayout) myCard.findViewById(R.id.top);

		txtname = (TextView) myCard.findViewById(R.id.name);
		avatar = (CircularImage) myCard.findViewById(R.id.avatar);
		txtname.setText(strName);
		btnEdit = (Button) myCard.findViewById(R.id.btnedit);
		basicListView = (ListView) myCard.findViewById(R.id.basicListView);
		contactListView = (ListView) myCard.findViewById(R.id.contactListView);
		socialListView = (ListView) myCard.findViewById(R.id.socialListView);
		addressListView = (ListView) myCard.findViewById(R.id.addressListView);
		eduListView = (ListView) myCard.findViewById(R.id.eduListView);
		workListView = (ListView) myCard.findViewById(R.id.workListView);
		laybasic = (LinearLayout) myCard.findViewById(R.id.laybasic);
		laycontact = (LinearLayout) myCard.findViewById(R.id.laycontact);
		laysocial = (LinearLayout) myCard.findViewById(R.id.laysocial);
		layadress = (LinearLayout) myCard.findViewById(R.id.layaddress);
		layedu = (LinearLayout) myCard.findViewById(R.id.layedu);
		layword = (LinearLayout) myCard.findViewById(R.id.laywork);
		layChild = (LinearLayout) myCard.findViewById(R.id.layChild);
		layChanged = (LinearLayout) myCard.findViewById(R.id.changed);

	}

	private void setOnClickListener() {
		back.setOnClickListener(this);
		btnEdit.setOnClickListener(this);
		layChanged.setOnClickListener(this);
	}

	private void setValuesAdapter() {
		delName();
		basicAdapter = new ValueAdapter(showBasicList);
		socialAdapter = new ValueAdapter(showSocialList);
		contactAdapter = new ContactValueAdapter(showContactList);
		addressAdapter = new ValueAdapter(showAddressList);
		eduAdapter = new EduValueAdapter(showEduList);
		workAdapter = new EduValueAdapter(showWorkList);
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
		setLayVisible();
	}

	@SuppressLint("NewApi")
	public void setAvatar(Bitmap bmp, String avatarPath) {
		avatar.setImageBitmap(bmp);
		layTop.setBackground(BitmapUtils.convertBimapToDrawable(bmp));
		if (!avatarPath.equals("")) {
			avatarURL = avatarPath;
		}
	}

	public View getView() {
		return myCard;
	}

	public void initData() {
		for (int i = 0; i < UserInfoUtils.infoTitleKey.length; i++) {
			showGroupkey.add(UserInfoUtils.infoTitleKey[i]);
		}
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
				showBasicList.add(info);
			}
		}
		if (Arrays.toString(UserInfoUtils.socialStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			showSocialList.add(info);
		} else if (Arrays.toString(UserInfoUtils.contactStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			showContactList.add(info);

		} else if (Arrays.toString(UserInfoUtils.addressStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			showAddressList.add(info);

		} else if (Arrays.toString(UserInfoUtils.eduStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setStartDate(start);
			info.setEndDate(end);
			showEduList.add(info);
		} else if (Arrays.toString(UserInfoUtils.workStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setStartDate(start);
			info.setEndDate(end);
			showWorkList.add(info);
		}

	}

	class EduValueAdapter extends BaseAdapter {
		List<Info> eduValuesList = new ArrayList<Info>();
		boolean falg;

		public EduValueAdapter(List<Info> valuesList) {
			this.eduValuesList = valuesList;
		}

		@Override
		public int getCount() {
			return eduValuesList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			EduHolderValues holderValues = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.user_info_show_list_item_edu_work, null);
				holderValues = new EduHolderValues();
				holderValues.key = (TextView) convertView
						.findViewById(R.id.key);
				holderValues.value = (TextView) convertView
						.findViewById(R.id.value);
				holderValues.endTime = (TextView) convertView
						.findViewById(R.id.endTime);
				holderValues.startTime = (TextView) convertView
						.findViewById(R.id.startTime);

				convertView.setTag(holderValues);
			} else {
				holderValues = (EduHolderValues) convertView.getTag();
			}

			String values = eduValuesList.get(position).getValue();
			holderValues.key.setText(eduValuesList.get(position).getKey());
			holderValues.value.setText(values);
			holderValues.endTime.setText(eduValuesList.get(position)
					.getEndDate());
			holderValues.startTime.setText(eduValuesList.get(position)
					.getStartDate());
			return convertView;
		}
	}

	class EduHolderValues {
		TextView key;
		TextView value;
		TextView startTime;
		TextView endTime;
	}

	class ValueAdapter extends BaseAdapter {
		List<Info> valuesList = new ArrayList<Info>();

		public ValueAdapter(List<Info> valuesList) {
			this.valuesList = valuesList;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolderValues holderValues = null;
			String key = valuesList.get(position).getKey();
			if (key.equals("性別")) {
				holderValues = new ViewHolderValues();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.user_info_show_gendar, null);
				holderValues.radioBoy = (RadioButton) convertView
						.findViewById(R.id.radioboy);
				holderValues.radioGirl = (RadioButton) convertView
						.findViewById(R.id.radiogirl);
				holderValues.key = (TextView) convertView
						.findViewById(R.id.key);
				holderValues.key.setText(key);
				holderValues.radioBoy.setClickable(false);
				holderValues.radioGirl.setClickable(false);
				if (valuesList.get(position).getValue().equals("1")) {
					holderValues.radioBoy.setChecked(true);
				} else if (valuesList.get(position).getValue().equals("2")) {
					holderValues.radioGirl.setChecked(true);
				}
			} else {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.user_info_show_list_item_key_value, null);
				holderValues = new ViewHolderValues();
				holderValues.key = (TextView) convertView
						.findViewById(R.id.key);
				holderValues.value = (TextView) convertView
						.findViewById(R.id.value);
				convertView.setTag(holderValues);
				holderValues.key.setText(key);
				holderValues.value.setText(valuesList.get(position).getValue());
			}
			return convertView;
		}
	}

	class ViewHolderValues {
		TextView key;
		TextView value;
		RadioButton radioBoy;
		RadioButton radioGirl;

	}

	class ContactValueAdapter extends BaseAdapter {
		List<Info> contactValuesList = new ArrayList<Info>();

		public ContactValueAdapter(List<Info> valuesList) {
			this.contactValuesList = valuesList;
		}

		@Override
		public int getCount() {
			return contactValuesList.size();
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
			ContactViewHolderValues holderValues = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.my_card_list_item_contacts, null);
				holderValues = new ContactViewHolderValues();
				holderValues.key = (TextView) convertView
						.findViewById(R.id.key);
				holderValues.value = (TextView) convertView
						.findViewById(R.id.value);

				convertView.setTag(holderValues);
			} else {
				holderValues = (ContactViewHolderValues) convertView.getTag();
			}
			String values = contactValuesList.get(position).getValue();

			holderValues.key.setText(contactValuesList.get(position).getKey());
			holderValues.value.setText(values);
			return convertView;
		}
	}

	class ContactViewHolderValues {
		TextView key;
		TextView value;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			callBack.onBackClick();
			break;
		case R.id.btnedit:
			Intent it = new Intent();
			it.putExtra("name", txtname.getText());
			it.putExtra("avatar", avatarURL);
			it.setClass(mContext, MyCardEditActivity.class);
			((Activity) mContext).startActivityForResult(it, 2);
			Utils.leftOutRightIn(mContext);
			break;
		case R.id.changed:
			callBack.onChangeClick(strName, avatarURL);
			break;
		default:
			break;
		}
	}

	public void setOnBack(OnButtonClickListener callBack) {
		this.callBack = callBack;
	}

	public interface OnButtonClickListener {
		void onBackClick();

		void onChangeClick(String name, String avatarURL);
	}

	private void clearData() {
		showBasicList.clear();
		showContactList.clear();
		showSocialList.clear();
		showAddressList.clear();
		showEduList.clear();
		showWorkList.clear();

	}

	public void notifyData(List<Info> basicList, List<Info> contactList,
			List<Info> socialList, List<Info> addressList, List<Info> eduList,
			List<Info> workList) {
		clearData();
		this.showBasicList.addAll(basicList);
		this.showContactList.addAll(contactList);
		this.showSocialList.addAll(socialList);
		this.showAddressList.addAll(addressList);
		this.showEduList.addAll(eduList);
		this.showWorkList.addAll(workList);
		delName();
		basicAdapter.notifyDataSetChanged();
		socialAdapter.notifyDataSetChanged();
		contactAdapter.notifyDataSetChanged();
		addressAdapter.notifyDataSetChanged();
		eduAdapter.notifyDataSetChanged();
		workAdapter.notifyDataSetChanged();
		Utils.setListViewHeightBasedOnChildren(basicListView);
		Utils.setListViewHeightBasedOnChildren(contactListView);
		Utils.setListViewHeightBasedOnChildren(socialListView);
		Utils.setListViewHeightBasedOnChildren(addressListView);
		Utils.setListViewHeightBasedOnChildren(eduListView);
		Utils.setListViewHeightBasedOnChildren(workListView);
		setLayVisible();
	}

	@Override
	public void getMyDetailsValues(String change, String name, String pid,
			String avatarURL, List<Info> basicList, List<Info> contactList,
			List<Info> socialList, List<Info> addressList, List<Info> eduList,
			List<Info> workList) {
		if (dialog != null) {
			dialog.dismiss();
		}
		isDetailChange(change);
		strName = name;
		txtname.setText(name);
		this.avatarURL = avatarURL;
		// imageLoader.displayImage(avatarURL, avatar, options);
		loadAvatar();
		notifyData(basicList, contactList, socialList, addressList, eduList,
				workList);
	}

}
