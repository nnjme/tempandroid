package com.changlianxi.activity;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.modle.SmsPrevieModle;
import com.changlianxi.task.IinviteUserTask;
import com.changlianxi.task.IinviteUserTask.IinviteUser;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Utils;

/**
 * 从通讯录导入圈子程序界面
 * 
 * @author teeker_bin
 * 
 */
public class SelectContactsActivity extends Activity implements
		OnClickListener, OnItemClickListener {
	private ListView listview;// 显示联系人的列表
	private LinearLayout layBot;// 用来显示或隐藏选择数量
	private Button btfinish;
	private ImageView back;
	private LinearLayout addicon;
	private Cursor cursor;
	private ContactsAdapter adapter;
	private ContentResolver resolver;
	private String type;
	private String cid;
	private String cirName;
	private String cmids = "";// 邀请成员时返回的邀请成员的id
	private String code = "";// 邀请链接值 需要邀请时有值
	private List<SmsPrevieModle> smsList = new ArrayList<SmsPrevieModle>();// 展示使用

	/** 获取库Phon表字段 **/
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo._ID, Phone.CONTACT_ID };
	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** 头像ID **/
	private static final int PHONES_PHOTO_ID_INDEX = 2;

	/** 联系人的ID **/
	private static final int PHONES_CONTACT_ID_INDEX = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_contacts);
		type = getIntent().getStringExtra("type");
		if (type.equals("add")) {
			cid = getIntent().getStringExtra("cid");
			cirName = getIntent().getStringExtra("cirName");
		}
		resolver = this.getContentResolver();
		initView();
		refreshData();
	}

	/**
	 * 初始各个化控件
	 */
	private void initView() {
		addicon = (LinearLayout) findViewById(R.id.addicon);
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(this);
		btfinish = (Button) findViewById(R.id.btnfinish);
		btfinish.setOnClickListener(this);
		layBot = (LinearLayout) findViewById(R.id.layBottom);
		listview = (ListView) findViewById(R.id.contactList);
		listview.setOnItemClickListener(this);
		listview.setCacheColorHint(0);
	}

	private void delicon(String position) {
		if (position != null) {
			for (int i = 0; i < addicon.getChildCount(); i++) {
				if (addicon.getChildAt(i).getTag().equals(position)) {
					addicon.removeViewAt(i);
					break;
				}
			}
		}

	}

	private void addImg(Bitmap bmp, String tag) {
		ImageView img = new ImageView(this);
		int width = Utils.getSecreenWidth(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width / 7,
				width / 7);
		lp.setMargins(3, 3, 3, 3);
		img.setLayoutParams(lp);
		img.setTag(tag);
		if (bmp == null) {
			img.setImageResource(R.drawable.home_image);
		} else {
			img.setImageBitmap(BitmapUtils.toRoundBitmap(bmp));
		}
		addicon.addView(img);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.check.toggle();
		adapter.selectedMap.put(position, holder.check.isChecked());
		adapter.notifyDataSetChanged();
		if (holder.check.isChecked()) {
			// ImageView对象(img)必须做如下设置后，才能获取其中的图像
			holder.img.setDrawingCacheEnabled(true);
			Bitmap bmp = Bitmap.createBitmap(holder.img.getDrawingCache());
			addImg(bmp, position + "");
		} else {
			delicon(position + "");
		}
		btfinish.setText("完成(" + addicon.getChildCount() + ")");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.btnfinish:
			List<SmsPrevieModle> listModle = new ArrayList<SmsPrevieModle>();
			for (int i = 0; i < adapter.getCount(); i++) {
				if (!adapter.selectedMap.get(i)) {
					continue;
				}
				Cursor cur = (Cursor) adapter.getItem(i);
				String name = cur.getString(PHONES_DISPLAY_NAME_INDEX);
				String num = cur.getString(PHONES_NUMBER_INDEX);
				SmsPrevieModle smsModle = new SmsPrevieModle();
				smsModle.setName(name);
				smsModle.setNum(num.replace(" ", ""));
				listModle.add(smsModle);
			}
			if (listModle.size() == 0) {
				Utils.showToast("至少选择一位联系人");
				return;
			}
			if (type.equals("add")) {
				addContacts(listModle);
				return;
			}
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("contactsList", (Serializable) listModle);
			intent.putExtras(bundle);
			intent.setClass(this, CreateCircleActivity.class);
			intent.putExtra("type", "more");
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}

	}

	/**
	 * 添加圈子成员
	 * 
	 * @param contactsList
	 */
	private void addContacts(final List<SmsPrevieModle> contactsList) {
		// 添加从通讯录选择的联系人
		IinviteUserTask task = new IinviteUserTask(cid, contactsList);
		task.setTaskCallBack(new IinviteUser() {

			@Override
			public void inviteUser(String rt, String details) {
				if (rt.equals("1")) {
					getDetails(details, contactsList);
					Utils.showToast("添加成功！");
					if (code.contains("null")) {
						finish();
						return;
					}
					intentSmsPreviewActivity();
				} else {
					Utils.showToast("邀请失败！");
				}
			}
		});
		task.execute();
	}

	/**
	 * 跳转到短信预览界面
	 */
	private void intentSmsPreviewActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("contactsList", (Serializable) smsList);
		intent.putExtras(bundle);
		intent.putExtra("cmids", cmids);
		intent.putExtra("cid", cid);
		intent.setClass(this, SmsPreviewActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 解析返回过来的字符串
	 * 
	 * @param details
	 *            实例 　　"details":" 1,4,EO2VqrHI,6;1,5,E6zBbKeg,7"字符串，首先以分号分割，
	 *            对应每个person的邀请结果
	 *            ，然后以逗号分割的四部分（第一部分表示结果是否成功，1成功，非1不成功；第二部分是pid，第三部分是邀请code
	 *            ，第四部分是cmid）
	 */
	private void getDetails(String details, List<SmsPrevieModle> contactsList) {
		String detail[] = details.split(";");
		for (int i = 0; i < detail.length; i++) {
			String str[] = detail[i].split(",");
			if (str[0].equals("1")) {
				System.out.println("name:" + contactsList.get(i).getName()
						+ "  code:" + str[2]);
				if (str[2].equals("") || str[2] == null) {
					str[2] = "null";
				}
				code += str[2];
				cmids += str[3] + ",";
				SmsPrevieModle modle = new SmsPrevieModle();
				modle.setContent("亲爱的" + contactsList.get(i).getName()
						+ ",邀请您加入" + cirName
						+ "圈子.您可以访问http://clx.teeker.com/a/b/" + str[2]
						+ "查看详情");
				modle.setName(contactsList.get(i).getName());
				modle.setNum(contactsList.get(i).getNum());
				smsList.add(modle);
			}

		}

	}

	private void setViewWidth(ImageView img) {
		int width = Utils.getSecreenWidth(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width / 7,
				width / 7);
		lp.setMargins(5, 5, 5, 5);
		img.setLayoutParams(lp);
	}

	// 查询数据库
	private void refreshData() {
		cursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null,
				null, "sort_key COLLATE LOCALIZED asc");
		adapter = new ContactsAdapter(this, cursor);
		listview.setAdapter(adapter);
	}

	class ContactsAdapter extends BaseAdapter {
		Cursor cur;
		Map<Integer, Boolean> selectedMap;
		HashSet<String> delContactsIdSet;
		ViewHolder holder = null;

		public ContactsAdapter(Context context, Cursor c) {
			cur = c;
			// 保存每条记录是否被选中的状态
			selectedMap = new HashMap<Integer, Boolean>();
			// 保存被选中记录作数据库表中的Id
			delContactsIdSet = new HashSet<String>();

			for (int i = 0; i < cur.getCount(); i++) {
				selectedMap.put(i, false);
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(SelectContactsActivity.this)
						.inflate(R.layout.contact_list_item, null);
				holder = new ViewHolder();
				holder.laybg = (LinearLayout) convertView
						.findViewById(R.id.laybg);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.check = (CheckBox) convertView
						.findViewById(R.id.checkBox1);
				holder.num = (TextView) convertView.findViewById(R.id.num);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			cur.moveToPosition(position);
			String name = cur.getString(PHONES_DISPLAY_NAME_INDEX);
			holder.name.setText(name);
			String num = cur.getString(PHONES_NUMBER_INDEX);
			if (!TextUtils.isEmpty(num) && num.length() > 10) {
				holder.num.setText(num);
			}
			Long photoid = cur.getLong(PHONES_PHOTO_ID_INDEX);
			Long contactid = cur.getLong(PHONES_CONTACT_ID_INDEX);
			setViewWidth(holder.img);
			Bitmap bitmap = setImage(photoid, contactid);
			holder.img.setImageBitmap(bitmap);
			holder.check.setChecked(selectedMap.get(position));
			return convertView;
		}

		@Override
		public int getCount() {
			return cur.getCount();
		}

		@Override
		public Object getItem(int position) {
			if (cur.moveToPosition(position)) {
				return cur;
			} else {
				return null;
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

	private Bitmap setImage(long photoid, long contactid) {
		// 得到联系人头像Bitamp
		Bitmap contactPhoto = null;

		// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
		if (photoid > 0) {
			Uri uri = ContentUris.withAppendedId(
					ContactsContract.Contacts.CONTENT_URI, contactid);
			InputStream input = ContactsContract.Contacts
					.openContactPhotoInputStream(resolver, uri);
			contactPhoto = BitmapFactory.decodeStream(input);
		} else {
			// contactPhoto = BitmapFactory.decodeResource(getResources(),
			// R.drawable.hand_pic);

		}
		if (contactPhoto == null) {
			contactPhoto = BitmapFactory.decodeResource(getResources(),
					R.drawable.hand_pic);
		}
		return BitmapUtils.toRoundBitmap(contactPhoto);
	}

	class ViewHolder {
		LinearLayout laybg;
		TextView name;
		CheckBox check;
		TextView num;
		ImageView img;
	}
}
