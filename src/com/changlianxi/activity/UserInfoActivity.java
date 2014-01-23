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
import android.os.AsyncTask.Status;
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
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.PersonDetail;
import com.changlianxi.data.enums.PersonDetailType;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DataBase;
import com.changlianxi.modle.Info;
import com.changlianxi.popwindow.AddKeyAndValuePopwindow;
import com.changlianxi.popwindow.AddKeyAndValuePopwindow.OnSelectKey;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.CircleMemberIdetailTask;
import com.changlianxi.task.GetUserDetailsTask;
import com.changlianxi.task.GetUserDetailsTask.GetValuesTask;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.UserInfoUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 用户资料显示界面
 * 
 * @author teeker_bin
 * 
 */
public class UserInfoActivity extends BaseActivity implements OnClickListener,
		GetValuesTask {
	private String iconPath;
	private int pid;// 用户pid
	private int uid;// 用户uid
	private int cid;// 圈子id
	private String username;
	private List<String> showGroupkey = new ArrayList<String>();
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
	private BasicValueAdapter basicAdapter;
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
	private LinearLayout laybasic;
	private LinearLayout laycontact;
	private LinearLayout laysocial;
	private LinearLayout layadress;
	private LinearLayout layedu;
	private LinearLayout layword;
	private LinearLayout layChild;
	private DataBase dbase = DataBase.getInstance();
	private SQLiteDatabase db = dbase.getWritableDatabase();
	private RelativeLayout layParent;
	private RelativeLayout layTop;
	private TextView txtnews;
	private CircleMemberIdetailTask task;
//	private Handler mHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case 0:
//				getUserDetails(pid+"");
//				setValuesAdapter();
//				getDetailsFromServer();
//				break;
//			default:
//				break;
//			}
//		}
//	};
	private CircleMember circleMember;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info_show);
		iconPath = getIntent().getStringExtra("iconImg");
		username = getIntent().getStringExtra("username");
		pid = getIntent().getIntExtra("pid", 0);
		cid = getIntent().getIntExtra("cid",0);
		uid = getIntent().getIntExtra("uid",0);
		imageLoader = CLXApplication.getImageLoader();
		options = CLXApplication.getUserOptions();
		initView();
		setValuesAdapter();
		filldata();
		setOnClickListener();
		initData();
		//mHandler.sendEmptyMessageDelayed(0, 100);

	}
	private void filldata() {
		dialog = DialogUtil.getWaitDialog(this, "请稍后");
		dialog.show();
		circleMember = new CircleMember(cid, pid, uid);
		if(task == null)
			task = new CircleMemberIdetailTask();
		if(task.getStatus() == Status.RUNNING){
			task.cancel(true);
		}
		task.setTaskCallBack(new BaseAsyncTask.PostCallBack<RetError>() {

			@Override
			public void taskFinish(RetError result) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				//分类
				List<PersonDetail> details = circleMember.getDetails();
				for(PersonDetail detail : details){
					valuesClassification(detail.getId()+"",detail.getType().name(),detail.getValue(),detail.getStart(),detail.getEnd());
				}
				notifyData(showBasicList, showContactList, showSocialList, showAddressList, showEduList,
						showWorkList);
			}

		});
		task.executeWithCheckNet(circleMember);
		
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

	private void getDetailsFromServer() {
		if (showBasicList.size() == 0 && showContactList.size() == 0
				&& showSocialList.size() == 0 && showEduList.size() == 0
				&& showWorkList.size() == 0 && showAddressList.size() == 0) {
			dialog = DialogUtil.getWaitDialog(this, "请稍后");
			dialog.show();
		}
		GetUserDetailsTask task = new GetUserDetailsTask(cid+"", pid+"");
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
		basicListView = (ListView) findViewById(R.id.basicListView);
		contactListView = (ListView) findViewById(R.id.contactListView);
		socialListView = (ListView) findViewById(R.id.socialListView);
		addressListView = (ListView) findViewById(R.id.addressListView);
		eduListView = (ListView) findViewById(R.id.eduListView);
		workListView = (ListView) findViewById(R.id.workListView);
		laybasic = (LinearLayout) findViewById(R.id.laybasic);
		laycontact = (LinearLayout) findViewById(R.id.laycontact);
		laysocial = (LinearLayout) findViewById(R.id.laysocial);
		layadress = (LinearLayout) findViewById(R.id.layaddress);
		layedu = (LinearLayout) findViewById(R.id.layedu);
		layword = (LinearLayout) findViewById(R.id.laywork);
		txtnews = (TextView) findViewById(R.id.txtnews);
		layChild = (LinearLayout) findViewById(R.id.layChild);
	}

	private void setOnClickListener() {
		back.setOnClickListener(this);
		btnCall.setOnClickListener(this);
		btnMessage.setOnClickListener(this);
		btnEdit.setOnClickListener(this);
	}

	private void setValuesAdapter() {
		delName();
		basicAdapter = new BasicValueAdapter(showBasicList);
		socialAdapter = new ValueAdapter(showSocialList);
		contactAdapter = new ContactValueAdapter(showContactList, true);
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
		if (showContactList.size() > 0) {
			txtnews.setText("移动电话：" + showContactList.get(0).getValue());
		}
		setLayVisible();
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
						getResources().getColor(R.color.f6));

			}
		}
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

	class BasicValueAdapter extends BaseAdapter {
		List<Info> valuesList = new ArrayList<Info>();
		final int TYPE_1 = 0;
		final int TYPE_2 = 1;

		public BasicValueAdapter(List<Info> valuesList) {
			this.valuesList = valuesList;
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
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return valuesList.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder1 holder1 = null;
			ViewHolder2 holder2 = null;
			int type = getItemViewType(position);
			String key = valuesList.get(position).getKey();
			if (convertView == null) {
				switch (type) {
				case TYPE_1:
					holder1 = new ViewHolder1();
					convertView = LayoutInflater.from(UserInfoActivity.this)
							.inflate(R.layout.user_info_show_gendar, null);
					holder1.radioBoy = (RadioButton) convertView
							.findViewById(R.id.radioboy);
					holder1.radioGirl = (RadioButton) convertView
							.findViewById(R.id.radiogirl);
					holder1.key = (TextView) convertView.findViewById(R.id.key);
					convertView.setTag(holder1);
					break;
				case TYPE_2:
					convertView = LayoutInflater
							.from(UserInfoActivity.this)
							.inflate(
									R.layout.user_info_show_list_item_key_value,
									null);
					holder2 = new ViewHolder2();
					holder2.key = (TextView) convertView.findViewById(R.id.key);
					holder2.value = (TextView) convertView
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
			switch (type) {
			case TYPE_1:
				holder1.key.setText(key);
				holder1.radioBoy.setClickable(false);
				holder1.radioGirl.setClickable(false);
				if (valuesList.get(position).getValue().equals("1")) {
					holder1.radioBoy.setChecked(true);
				} else if (valuesList.get(position).getValue().equals("2")) {
					holder1.radioGirl.setChecked(true);
				}
				break;
			case TYPE_2:
				holder2.key.setText(key);
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
		RadioButton radioGirl;

	}

	class ViewHolder2 {
		TextView key;
		TextView value;
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
			if (convertView == null) {
				convertView = LayoutInflater.from(UserInfoActivity.this)
						.inflate(R.layout.user_info_show_list_item_key_value,
								null);
				holderValues = new ViewHolderValues();
				holderValues.key = (TextView) convertView
						.findViewById(R.id.key);
				holderValues.value = (TextView) convertView
						.findViewById(R.id.value);
				convertView.setTag(holderValues);
			} else {
				holderValues = (ViewHolderValues) convertView.getTag();
			}
			holderValues.key.setText(valuesList.get(position).getKey());
			holderValues.value.setText(valuesList.get(position).getValue());
			return convertView;
		}
	}

	class ViewHolderValues {
		TextView key;
		TextView value;
	}

	class ContactValueAdapter extends BaseAdapter {
		List<Info> contactValuesList = new ArrayList<Info>();
		boolean falg;

		public ContactValueAdapter(List<Info> valuesList, boolean flag) {
			this.contactValuesList = valuesList;
			this.falg = flag;
		}

		@Override
		public int getCount() {
			return contactValuesList.size();
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
			ContactViewHolderValues holderValues = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(UserInfoActivity.this)
						.inflate(R.layout.user_info_show_list_item_call_sms,
								null);
				holderValues = new ContactViewHolderValues();
				holderValues.key = (TextView) convertView
						.findViewById(R.id.key);
				holderValues.value = (TextView) convertView
						.findViewById(R.id.value);
				holderValues.iconSms = (LinearLayout) convertView
						.findViewById(R.id.icon_sms);
				holderValues.iconCall = (LinearLayout) convertView
						.findViewById(R.id.icon_call);
				convertView.setTag(holderValues);
			} else {
				holderValues = (ContactViewHolderValues) convertView.getTag();
			}
			if (falg) {
				holderValues.iconCall.setVisibility(View.VISIBLE);
				holderValues.iconSms.setVisibility(View.VISIBLE);

			} else {
				holderValues.iconCall.setVisibility(View.GONE);
				holderValues.iconSms.setVisibility(View.GONE);
			}
			String values = contactValuesList.get(position).getValue();
			holderValues.iconCall.setOnClickListener(new BtnClick(values));
			holderValues.iconSms.setOnClickListener(new BtnClick(values));
			holderValues.key.setText(contactValuesList.get(position).getKey());
			holderValues.value.setText(values);
			return convertView;
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
				convertView = LayoutInflater.from(UserInfoActivity.this)
						.inflate(R.layout.user_info_show_list_item_edu_work,
								null);
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
			String end = DateUtils.interceptDateStr(eduValuesList.get(position).getEndDate(),"yyyy.MM.dd");
			String start = DateUtils.interceptDateStr(eduValuesList.get(position).getStartDate(),"yyyy.MM.dd");
			holderValues.endTime.setText(end);
			holderValues.startTime.setText(start);
			return convertView;
		}
	}

	class EduHolderValues {
		TextView key;
		TextView value;
		TextView startTime;
		TextView endTime;
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

	class ContactViewHolderValues {
		TextView key;
		TextView value;
		LinearLayout iconSms;
		LinearLayout iconCall;
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
			it.putExtra("circleMumber", circleMember);
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
//		clearData();
//		this.showBasicList.addAll(basicList);
//		this.showContactList.addAll(contactList);
//		this.showSocialList.addAll(socialList);
//		this.showAddressList.addAll(addressList);
//		this.showEduList.addAll(eduList);
//		this.showWorkList.addAll(workList);
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
		if (showContactList.size() > 0) {
			txtnews.setText("移动电话：" + showContactList.get(0).getValue());
		}
		setLayVisible();
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
