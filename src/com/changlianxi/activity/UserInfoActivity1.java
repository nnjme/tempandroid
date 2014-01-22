package com.changlianxi.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import com.changlianxi.db.DataBase;
import com.changlianxi.modle.Info;
import com.changlianxi.popwindow.AddKeyAndValuePopwindow;
import com.changlianxi.popwindow.AddKeyAndValuePopwindow.OnSelectKey;
import com.changlianxi.task.GetUserDetailsTask;
import com.changlianxi.task.GetUserDetailsTask.GetValuesTask;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.UserInfoUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * 用户资料显示界面
 * 
 * @author teeker_bin
 * 
 */
public class UserInfoActivity1 extends BaseActivity implements OnClickListener,
		GetValuesTask {
	private String iconPath;
	private String pid;// 用户pid
	private String uid;// 用户uid
	private String cid;// 圈子id
	private String username;
	private List<String> showGroupkey = new ArrayList<String>();
	private List<Info> listData = new ArrayList<Info>();
	private List<Info> showBasicList = new ArrayList<Info>();// 存放基本信息数据
	private List<Info> showContactList = new ArrayList<Info>();// 存放联系方式数据
	private List<Info> showSocialList = new ArrayList<Info>();// 存放社交账号数据
	private List<Info> showAddressList = new ArrayList<Info>();// 存放地址数据
	private List<Info> showEduList = new ArrayList<Info>();// 存放教育经历
	private List<Info> showWorkList = new ArrayList<Info>();// 存放工作经历
	private ImageView back;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private TextView name;
	private CircularImage avatar;
	private LinearLayout btnCall;
	private LinearLayout btnMessage;
	private Button btnEdit;
	private ValueAdapter adapter;
	private ListView listView;
	private Dialog dialog;
	private DataBase dbase = DataBase.getInstance();
	private SQLiteDatabase db = dbase.getWritableDatabase();
	private RelativeLayout layParent;
	private RelativeLayout layTop;
	private TextView txtnews;
	private int count;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				getUserDetails(pid);
				setValuesAdapter();
				getDetailsFromServer();
				break;
			default:
				break;
			}
		}
	};

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info_activity1);
		iconPath = getIntent().getStringExtra("iconImg");
		username = getIntent().getStringExtra("username");
		pid = getIntent().getStringExtra("pid");
		cid = getIntent().getStringExtra("cid");
		uid = getIntent().getStringExtra("uid");
		imageLoader = CLXApplication.getImageLoader();
		options = CLXApplication.getOptions();
		initView();
		setOnClickListener();
		initData();
		mHandler.sendEmptyMessageDelayed(0, 100);

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

	private void getDetailsFromServer() {
		if (showBasicList.size() == 0 && showContactList.size() == 0
				&& showSocialList.size() == 0 && showEduList.size() == 0
				&& showWorkList.size() == 0 && showAddressList.size() == 0) {
			dialog = DialogUtil.getWaitDialog(this, "请稍后");
			dialog.show();
		}
		GetUserDetailsTask task = new GetUserDetailsTask(cid, pid);
		task.setTaskCallBack(this);
		task.execute();

	}

	private void initView() {
		layParent = (RelativeLayout) findViewById(R.id.parent);
		layTop = (RelativeLayout) findViewById(R.id.top);
		back = (ImageView) findViewById(R.id.back);
		btnCall = (LinearLayout) findViewById(R.id.btncall);
		btnMessage = (LinearLayout) findViewById(R.id.btnmessage);
		name = (TextView) findViewById(R.id.name);
		avatar = (CircularImage) findViewById(R.id.avatar);
		name.setText(username);
		setAvatar();
		btnEdit = (Button) findViewById(R.id.btnedit);
		txtnews = (TextView) findViewById(R.id.txtnews);
		listView = (ListView) findViewById(R.id.ListView);
	}

	private void setOnClickListener() {
		back.setOnClickListener(this);
		btnCall.setOnClickListener(this);
		btnMessage.setOnClickListener(this);
		btnEdit.setOnClickListener(this);
	}

	private void setValuesAdapter() {
		delName();
		addData();
		adapter = new ValueAdapter(listData);
		listView.setAdapter(adapter);
		if (showContactList.size() > 0) {
			txtnews.setText("移动电话：" + showContactList.get(0).getValue());
		}
	}

	private void addData() {
		listData.addAll(showBasicList);
		listData.addAll(showContactList);
		listData.addAll(showSocialList);
		listData.addAll(showAddressList);
		listData.addAll(showEduList);
		listData.addAll(showWorkList);
	}

	private void setAvatar() {
		imageLoader.loadImage(iconPath, options, new ImageLoadingListener() {

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

	public void initData() {
		for (int i = 0; i < UserInfoUtils.infoTitleKey.length; i++) {
			showGroupkey.add(UserInfoUtils.infoTitleKey[i]);
		}
	}

	private void delName() {
		for (int i = showBasicList.size() - 1; i >= 0; i--) {
			String type = showBasicList.get(i).getType();
			if (type.equals("D_NAME")) {
				showBasicList.remove(i);
			}
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
		for (int i = 0; i < UserInfoUtils.basicUserStr.length; i++) {
			if (key.equals(UserInfoUtils.basicUserStr[i])) {
				typekey = UserInfoUtils.convertToChines(key);
				info.setKey(typekey);
				info.setTitleKey(showGroupkey.get(0));
				showBasicList.add(info);
			}
		}
		if (Arrays.toString(UserInfoUtils.socialStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setTitleKey(showGroupkey.get(2));
			showSocialList.add(info);
		} else if (Arrays.toString(UserInfoUtils.contactStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setTitleKey(showGroupkey.get(1));
			showContactList.add(info);

		} else if (Arrays.toString(UserInfoUtils.addressStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setTitleKey(showGroupkey.get(3));
			showAddressList.add(info);

		} else if (Arrays.toString(UserInfoUtils.eduStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setStartDate(start);
			info.setEndDate(end);
			info.setTitleKey(showGroupkey.get(4));
			showEduList.add(info);
		} else if (Arrays.toString(UserInfoUtils.workStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setStartDate(start);
			info.setTitleKey(showGroupkey.get(5));
			info.setEndDate(end);
			showWorkList.add(info);
		}
	}

	class ValueAdapter extends BaseAdapter {
		List<Info> valuesList = new ArrayList<Info>();

		public ValueAdapter(List<Info> listData) {
			this.valuesList = listData;
		}

		@Override
		public int getCount() {
			return valuesList.size();
		}

		public void setData(List<Info> valuesList) {
			this.valuesList = valuesList;
			notifyDataSetChanged();
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
			if (convertView == null) {
				convertView = LayoutInflater.from(UserInfoActivity1.this)
						.inflate(R.layout.user_info_show_1_item_item, null);
				holderValues = new ViewHolderValues();
				holderValues.layParent = (LinearLayout) convertView
						.findViewById(R.id.parent);
				holderValues.txtTitleKey = (TextView) convertView
						.findViewById(R.id.titleKey);
				holderValues.txtKey = (TextView) convertView
						.findViewById(R.id.key);
				holderValues.txtValue = (TextView) convertView
						.findViewById(R.id.value);
				convertView.setTag(holderValues);
			} else {
				holderValues = (ViewHolderValues) convertView.getTag();
			}
			holderValues.txtTitleKey.setText(valuesList.get(position)
					.getTitleKey());
			holderValues.txtKey.setText(valuesList.get(position).getKey());
			holderValues.txtValue.setText(valuesList.get(position).getValue());
			showTitlekey(holderValues, valuesList, position);
			return convertView;
		}
	}

	/**
	 * 添加Item
	 * 
	 * @param list
	 * @param layParent
	 */
	private void addView(List<Info> list, LinearLayout layParent) {
		if (list.get(0).getTitleKey().equals(showGroupkey.get(1))) {
			addContactView(list, layParent);
		} else if (list.get(0).getTitleKey().equals(showGroupkey.get(4))) {
			addEduView(list, layParent);
		} else if (list.get(0).getTitleKey().equals(showGroupkey.get(5))) {
			addEduView(list, layParent);
		} else {
			addOtherView(list, layParent);
		}

	}

	private void addOtherView(List<Info> list, LinearLayout layParent) {
		layParent.removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			String titleKey = list.get(i).getTitleKey();
			String key = list.get(i).getKey();
			String value = list.get(i).getValue();
			if (key.equals("性別")) {
				addGender(titleKey, key, value, layParent);
			} else {
				View view = LayoutInflater.from(this).inflate(
						R.layout.user_info_show_1_item_item, null);
				TextView txtTitleKey = (TextView) view
						.findViewById(R.id.titleKey);
				TextView txtKey = (TextView) view.findViewById(R.id.key);
				TextView txtValue = (TextView) view.findViewById(R.id.value);
				txtKey.setText(key);
				txtTitleKey.setText(titleKey);
				txtValue.setText(value);
				// showTitlekey(txtTitleKey, list, i);
				layParent.addView(view);
			}
		}
	}

	/**
	 * 添加联系人布局
	 * 
	 * @param list
	 * @param layParent
	 */
	private void addContactView(List<Info> list, LinearLayout layParent) {
		layParent.removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			String titleKey = list.get(i).getTitleKey();
			String key = list.get(i).getKey();
			String value = list.get(i).getValue();
			View view = LayoutInflater.from(this).inflate(
					R.layout.user_info_show_1_item_item_contact, null);
			TextView txtTitleKey = (TextView) view.findViewById(R.id.titleKey);
			TextView txtKey = (TextView) view.findViewById(R.id.key);
			TextView txtValue = (TextView) view.findViewById(R.id.value);
			LinearLayout iconSms = (LinearLayout) view
					.findViewById(R.id.icon_sms);
			iconSms.setTag(value);
			LinearLayout iconCall = (LinearLayout) view
					.findViewById(R.id.icon_call);
			iconCall.setTag(value);
			iconCall.setOnClickListener(new BtnClick(iconCall.getTag()
					.toString()));
			iconSms.setOnClickListener(new BtnClick(iconSms.getTag().toString()));
			txtKey.setText(key);
			txtTitleKey.setText(titleKey);
			txtValue.setText(value);
			// showTitlekey(txtTitleKey, list, i);
			layParent.addView(view);
		}
	}

	/**
	 * 添加性别
	 */
	private void addGender(String titleKey, String key, String value,
			LinearLayout layParent) {
		String titleKeyStr = titleKey;
		String keyStr = key;
		String valueStr = value;
		View view = LayoutInflater.from(this).inflate(
				R.layout.user_info_show_1_item_item_gender, null);
		TextView txtTitleKey = (TextView) view.findViewById(R.id.titleKey);
		TextView txtKey = (TextView) view.findViewById(R.id.key);
		RadioButton radioBoy = (RadioButton) view.findViewById(R.id.radioboy);
		RadioButton radioGirl = (RadioButton) view.findViewById(R.id.radiogirl);
		radioBoy.setClickable(false);
		radioGirl.setClickable(false);
		txtTitleKey.setText(titleKeyStr);
		txtKey.setText(keyStr);
		if (valueStr.equals("1")) {
			radioBoy.setChecked(true);
		} else if (valueStr.equals("2")) {
			radioGirl.setChecked(true);
		}
		layParent.addView(view);
	}

	/**
	 * 添加教育经历和工作经历布局
	 * 
	 * @param list
	 * @param layParent
	 */
	private void addEduView(List<Info> list, LinearLayout layParent) {
		layParent.removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			String titleKey = list.get(i).getTitleKey();
			String key = list.get(i).getKey();
			String value = list.get(i).getValue();
			String startTime = list.get(i).getStartDate();
			String endTime = list.get(i).getEndDate();
			View view = LayoutInflater.from(this).inflate(
					R.layout.user_info_show_1_item_item_edu_work, null);
			TextView txtTitleKey = (TextView) view.findViewById(R.id.titleKey);
			TextView txtKey = (TextView) view.findViewById(R.id.key);
			TextView txtValue = (TextView) view.findViewById(R.id.value);
			TextView txtendTime = (TextView) view.findViewById(R.id.endTime);
			TextView txtstartTime = (TextView) view
					.findViewById(R.id.startTime);
			txtKey.setText(key);
			txtTitleKey.setText(titleKey);
			txtValue.setText(value);
			txtendTime.setText(endTime);
			txtstartTime.setText(startTime);
			// showTitlekey(h, list, i);
			layParent.addView(view);
		}
	}

	private void showTitlekey(ViewHolderValues holder, List<Info> list,
			int position) {
		// 当前titleKey
		String currentStr = list.get(position).getTitleKey();
		// 上一个titleKey
		String previewStr = (position - 1) >= 0 ? list.get(position - 1)
				.getTitleKey() : " ";

		if (!previewStr.equals(currentStr)) {
			holder.txtTitleKey.setVisibility(View.VISIBLE);
			count += 1;
		} else {
			holder.txtTitleKey.setVisibility(View.INVISIBLE);
		}
		if (count % 2 == 0) {
			holder.layParent.setBackgroundColor(Color.WHITE);
		} else {
			holder.layParent.setBackgroundColor(this.getResources().getColor(
					R.color.f6));
		}
	}

	class ViewHolderValues {
		LinearLayout layParent;
		TextView txtTitleKey;
		TextView txtKey;
		TextView txtValue;

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2 && data != null) {
			Bundle bundle = data.getExtras();
			List<Info> basicList = (List<Info>) bundle
					.getSerializable("basicList");
			List<Info> contactList = (List<Info>) bundle
					.getSerializable("contactList");
			List<Info> socialList = (List<Info>) bundle
					.getSerializable("socialList");
			List<Info> addressList = (List<Info>) bundle
					.getSerializable("addressList");
			List<Info> eduList = (List<Info>) bundle.getSerializable("eduList");
			List<Info> workList = (List<Info>) bundle
					.getSerializable("workList");
			notifyData(basicList, contactList, socialList, addressList,
					eduList, workList);
		}
	}

	/**
	 * 拨打电话
	 * 
	 * @param num
	 */
	private void callPhone(String num) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + num));
		startActivity(intent);

	}

	/**
	 * 发短信
	 * 
	 * @param num
	 */
	private void sendMessage(String num) {
		Uri uri = Uri.parse("smsto:" + num);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		it.putExtra("sms_body", "");
		startActivity(it);
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
			case R.id.icon_sms:
				sendMessage(str);
				break;
			case R.id.icon_call:
				callPhone(str);
				break;

			default:
				break;
			}

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			Utils.rightOut(this);
			break;
		case R.id.btnedit:
			Intent it = new Intent();
			it.putExtra("name", username);
			it.putExtra("cid", cid);
			it.putExtra("pid", pid);
			it.putExtra("avatar", iconPath);
			it.setClass(this, UserInfoEditActivity.class);
			startActivityForResult(it, 2);
			Utils.leftOutRightIn(this);
			break;
		case R.id.btncall:
			if (showContactList.size() == 1) {
				callPhone(showContactList.get(0).getValue());
			} else {
				List<String> mobile = new ArrayList<String>();
				for (int i = 0; i < showContactList.size(); i++) {
					if (showContactList.get(i).getType().equals("D_MOBILE")
							|| showContactList.get(i).getType()
									.equals("D_WORK_PHONE")
							|| showContactList.get(i).getType()
									.equals("D_HOME_PHONE")) {
						mobile.add(showContactList.get(i).getValue());

					}
				}
				String mobileArray[] = (mobile
						.toArray(new String[mobile.size()]));
				callPhone(mobileArray);

			}
			break;
		case R.id.btnmessage:
			Intent intent = new Intent();
			intent.putExtra("ruid", uid);
			intent.putExtra("cid", cid);
			intent.putExtra("name", username);
			intent.putExtra("type", "write");
			intent.setClass(this, MessageActivity.class);
			startActivity(intent);
			Utils.leftOutRightIn(this);
			break;
		default:
			break;
		}
	}

	/**
	 * 显示电话列表
	 * 
	 * @param str
	 */
	private void callPhone(final String str[]) {
		AddKeyAndValuePopwindow pop = new AddKeyAndValuePopwindow(this,
				layParent, str, "选择手机号码");
		pop.setCallBack(new OnSelectKey() {

			@Override
			public void getSelectKey(String str) {
				callPhone(str);

			}
		});
		pop.show();

	}

	private void clearData() {
		showBasicList.clear();
		showContactList.clear();
		showSocialList.clear();
		showAddressList.clear();
		showEduList.clear();
		showWorkList.clear();

	}

	private void notifyData(List<Info> basicList, List<Info> contactList,
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
		listData.clear();
		addData();
		adapter.setData(listData);
		if (showContactList.size() > 0) {
			txtnews.setText("移动电话：" + showContactList.get(0).getValue());
		}
	}

	@Override
	public void onTaskFinish(List<Info> basicList, List<Info> contactList,
			List<Info> socialList, List<Info> addressList, List<Info> eduList,
			List<Info> workList) {
		if (isFinishing()) {
			return;
		}
		if (dialog != null) {
			dialog.dismiss();
		}
		notifyData(basicList, contactList, socialList, addressList, eduList,
				workList);

	}
}
