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
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
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

import com.changlianxi.modle.ContactModle;
import com.changlianxi.modle.SmsPrevieModle;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Utils;

/**
 * ��ͨѶ¼����Ȧ�ӳ������
 * 
 * @author teeker_bin
 * 
 */
public class SelectContactsActivity extends Activity implements
		OnClickListener, OnItemClickListener {
	private ListView listview;// ��ʾ��ϵ�˵��б�
	private List<ContactModle> data = new ArrayList<ContactModle>();// ���������ϵ��
	private LinearLayout layBot;// ������ʾ������ѡ������
	private Button btfinish;
	private ImageView back;
	private LinearLayout addicon;
	private Cursor cursor;
	private ContactsAdapter adapter;
	// private CheckboxAdapter adapter;
	private ContentResolver resolver;

	/** ��ȡ��Phon���ֶ� **/
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo._ID, Phone.CONTACT_ID };
	/** ��ϵ����ʾ���� **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** �绰���� **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** ͷ��ID **/
	private static final int PHONES_PHOTO_ID_INDEX = 2;

	/** ��ϵ�˵�ID **/
	private static final int PHONES_CONTACT_ID_INDEX = 3;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				data.add((ContactModle) msg.obj);
				// adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_contacts);
		resolver = this.getContentResolver();
		initView();
		// new SelectContacts().execute();
		refreshData();
	}

	/**
	 * ��ʼ�������ؼ�
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
		// adapter = new CheckboxAdapter();
		// listview.setAdapter(adapter);
	}

	// class SelectContacts extends AsyncTask<String, Integer, String> {
	//
	// // �ɱ䳤�������������AsyncTask.exucute()��Ӧ
	// @Override
	// protected String doInBackground(String... params) {
	// getPhoneContacts();
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	//
	// }
	//
	// @Override
	// protected void onPreExecute() {
	// // ����������������������ʾһ���Ի�������򵥴���
	// }
	// }

	// private void getPhoneContacts() {
	// ContentResolver resolver = this.getContentResolver();
	//
	// // ��ȡ�ֻ���ϵ��
	// Cursor phoneCursor = resolver
	// .query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null,
	// "sort_key COLLATE LOCALIZED asc");
	//
	// if (phoneCursor != null) {
	// while (phoneCursor.moveToNext()) {
	//
	// // �õ��ֻ�����
	// String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
	// // ���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
	// if (TextUtils.isEmpty(phoneNumber))
	// continue;
	//
	// // �õ���ϵ������
	// String contactName = phoneCursor
	// .getString(PHONES_DISPLAY_NAME_INDEX);
	//
	// // �õ���ϵ��ID
	// Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
	//
	// // �õ���ϵ��ͷ��ID
	// Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
	//
	// // �õ���ϵ��ͷ��Bitamp
	// Bitmap contactPhoto = null;
	//
	// // photoid ����0 ��ʾ��ϵ����ͷ�� ���û�и���������ͷ�������һ��Ĭ�ϵ�
	// if (photoid > 0) {
	// Uri uri = ContentUris.withAppendedId(
	// ContactsContract.Contacts.CONTENT_URI, contactid);
	// InputStream input = ContactsContract.Contacts
	// .openContactPhotoInputStream(resolver, uri);
	// contactPhoto = BitmapFactory.decodeStream(input);
	// } else {
	// contactPhoto = BitmapFactory.decodeResource(getResources(),
	// R.drawable.hand_pic);
	// }
	//
	// ContactModle modle = new ContactModle();
	// modle.setName(contactName);
	// modle.setNum(phoneNumber.replace(" ", ""));
	// modle.setBmp(contactPhoto);
	// modle.setSelected(false);
	// // data.add(modle);
	// setAdapter(modle);
	// }
	//
	// phoneCursor.close();
	// }
	// }

	// private void setAdapter(ContactModle modle) {
	// Message msg = mHandler.obtainMessage(0, modle);
	// mHandler.sendMessage(msg);
	//
	// }

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
			// ImageView����(img)�������������ú󣬲��ܻ�ȡ���е�ͼ��
			holder.img.setDrawingCacheEnabled(true);
			Bitmap bmp = Bitmap.createBitmap(holder.img.getDrawingCache());
			addImg(bmp, position + "");
		} else {
			delicon(position + "");
		}
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
				smsModle.setNum(num);
				listModle.add(smsModle);
			}
			if (listModle.size() == 0) {
				Utils.showToast("����ѡ��һλ��ϵ��");
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

	private void setViewWidth(ImageView img) {
		int width = Utils.getSecreenWidth(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width / 7,
				width / 7);
		lp.setMargins(5, 5, 5, 5);
		img.setLayoutParams(lp);
	}

	// ��ѯ���ݿ�
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
			// ����ÿ����¼�Ƿ�ѡ�е�״̬
			selectedMap = new HashMap<Integer, Boolean>();
			// ���汻ѡ�м�¼�����ݿ���е�Id
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
			if (num.length() > 10) {
				holder.num.setText(num);
			}
			Long photoid = cur.getLong(PHONES_PHOTO_ID_INDEX);
			Long contactid = cur.getLong(PHONES_CONTACT_ID_INDEX);
			setViewWidth(holder.img);
			Bitmap bitmap = setImage(photoid, contactid);
			holder.img.setImageBitmap(bitmap);
			holder.check.setChecked(selectedMap.get(position));
			// // �����¼Id
			// if (selectedMap.get(position)) {
			// delContactsIdSet.add(String.valueOf(cur
			// .getInt(PHONES_CONTACT_ID_INDEX)));
			// } else {
			// delContactsIdSet.remove(String.valueOf(cur
			// .getInt(PHONES_CONTACT_ID_INDEX)));
			// }
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
		// �õ���ϵ��ͷ��Bitamp
		Bitmap contactPhoto = null;

		// photoid ����0 ��ʾ��ϵ����ͷ�� ���û�и���������ͷ�������һ��Ĭ�ϵ�
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

	// class CheckboxAdapter extends BaseAdapter {
	// // ��¼checkbox��״̬
	// public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();
	//
	// @Override
	// public int getCount() {
	// // TODO Auto-generated method stub
	// return data.size();
	// }
	//
	// @Override
	// public Object getItem(int position) {
	// // TODO Auto-generated method stub
	// return data.get(position);
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// // TODO Auto-generated method stub
	// return position;
	// }
	//
	// // ��дView
	// @Override
	// public View getView(final int position, View convertView,
	// ViewGroup parent) {
	// final ViewHolder holder;
	// if (convertView == null) {
	// holder = new ViewHolder();
	// LayoutInflater mInflater = LayoutInflater
	// .from(SelectContactsActivity.this);
	// convertView = mInflater.inflate(R.layout.contact_list_item,
	// null);
	// holder.laybg = (LinearLayout) convertView
	// .findViewById(R.id.laybg);
	// holder.name = (TextView) convertView.findViewById(R.id.name);
	// holder.img = (ImageView) convertView.findViewById(R.id.img);
	// holder.check = (CheckBox) convertView
	// .findViewById(R.id.checkBox1);
	// holder.num = (TextView) convertView.findViewById(R.id.num);
	// convertView.setTag(holder);
	//
	// } else {
	// holder = (ViewHolder) convertView.getTag();
	// }
	// setViewWidth(holder.img);
	// if (data.get(position).getBmp() == null) {
	// holder.img.setImageResource(R.drawable.root_default);
	// } else {
	// holder.img.setImageBitmap(BitmapUtils.toRoundBitmap(data.get(
	// position).getBmp()));
	// }
	// holder.name.setText(data.get(position).getName());
	// holder.num.setText(data.get(position).getNum());
	// holder.check.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// if (holder.check.isChecked()) {
	// state.put(position, true);
	// layBot.setVisibility(View.VISIBLE);
	// addImg(data.get(position).getBmp(), state.size() - 1
	// + "");
	// data.get(position).setPosition(state.size() - 1 + "");
	// // System.out.println("size:"
	// // + listData.get(position).getPosition());
	// btfinish.setText("���(" + state.size() + ")");
	// } else {
	// System.out.println("aa:"
	// + data.get(position).getPosition());
	// state.remove(position);
	// btfinish.setText("���(" + state.size() + ")");
	// delicon(data.get(position).getPosition());
	//
	// }
	// }
	// });
	// holder.check
	// .setChecked((state.get(position) == null ? false : true));
	// return convertView;
	// }
	//
	// }

	class ViewHolder {
		LinearLayout laybg;
		TextView name;
		CheckBox check;
		TextView num;
		ImageView img;
	}
}
