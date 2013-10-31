package com.changlianxi.activity;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.AsyncQueryHandler;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.modle.ContactModle;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Utils;

/**
 * ��ͨѶ¼����Ȧ�ӳ������
 * 
 * @author teeker_bin
 * 
 */
public class SelectContactsActivity extends Activity implements OnClickListener {
	private ListView listview;// ��ʾ��ϵ�˵��б�
	private List<ContactModle> data;// ���������ϵ��
	private AsyncQueryHandler asyncQuery;
	private LinearLayout layBot;// ������ʾ������ѡ������
	private Button btfinish;
	private ImageView back;
	private LinearLayout addicon;
	private CheckboxAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_contacts);
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		initView();
	}

	protected void onResume() {
		super.onResume();
		Uri uri = Uri.parse("content://com.android.contacts/data/phones"); // ��ϵ�˵�Uri
		String[] projection = new String[] { Phone.DISPLAY_NAME, Phone.NUMBER,
				Photo._ID, Phone.CONTACT_ID };// ��ѯ����
		asyncQuery.startQuery(0, null, uri, projection, null, null,
				"sort_key COLLATE LOCALIZED asc"); // ����sort_key�����ѯ
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
		// listview.setOnItemClickListener(this);
		listview.setCacheColorHint(0);

	}

	/**
	 * ���ݿ��첽��ѯ��AsyncQueryHandler
	 * 
	 * @author teeker_bin
	 * 
	 */
	private class MyAsyncQueryHandler extends AsyncQueryHandler {
		private ContentResolver resolver;

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
			this.resolver = cr;

		}

		/**
		 * ��ѯ�����Ļص�����
		 */
		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				data = new ArrayList<ContactModle>();
				while (cursor.moveToNext()) {
					// �õ��ֻ�����
					String phoneNumber = cursor.getString(1);
					if (phoneNumber.length() < 11) {// ���˵��绰����
						continue;
					}
					// ���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
					if (TextUtils.isEmpty(phoneNumber))
						continue;
					// �õ���ϵ������
					String contactName = cursor.getString(0);
					// �õ���ϵ��ID
					Long contactid = cursor.getLong(3);
					// �õ���ϵ��ͷ��ID
					Long photoid = cursor.getLong(2);
					// �õ���ϵ��ͷ��Bitamp
					Bitmap contactPhoto = null;
					// photoid ����0 ��ʾ��ϵ����ͷ�� ���û�и���������ͷ�������һ��Ĭ�ϵ�
					if (photoid > 0) {
						Uri ur = ContentUris.withAppendedId(
								ContactsContract.Contacts.CONTENT_URI,
								contactid);
						InputStream input = ContactsContract.Contacts
								.openContactPhotoInputStream(resolver, ur);
						contactPhoto = BitmapFactory.decodeStream(input);
					}
					ContactModle modle = new ContactModle();
					modle.setName(contactName);
					modle.setNum(phoneNumber.replace(" ", ""));
					modle.setBmp(contactPhoto);
					modle.setSelected(false);
					data.add(modle);
				}
				setAdapter(data);
			}

		}
	}

	private void setAdapter(List<ContactModle> list) {
		adapter = new CheckboxAdapter(this, data);
		listview.setAdapter(adapter);

	}

	class ViewHolder {
		LinearLayout laybg;
		TextView name;
		CheckBox check;
		TextView num;
		ImageView img;
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.btnfinish:
			List<ContactModle> listModle = new ArrayList<ContactModle>();
			HashMap<Integer, Boolean> state = adapter.state;
			for (int j = 0; j < adapter.getCount(); j++) {
				if (state.get(j) != null) {
					ContactModle modle = (ContactModle) adapter.getItem(j);
					modle.setBmp(null);
					listModle.add(modle);
				}
			}
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("contactsList", (Serializable) listModle);
			intent.putExtras(bundle);
			intent.setClass(this, CreateCircleActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}

	}

	class CheckboxAdapter extends BaseAdapter {

		private Context context;
		private List<ContactModle> listData;
		// ��¼checkbox��״̬
		public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

		// ���캯��
		public CheckboxAdapter(Context context, List<ContactModle> listData) {
			this.context = context;
			this.listData = listData;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		// ��дView
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater mInflater = LayoutInflater.from(context);
				convertView = mInflater.inflate(R.layout.contact_list_item,
						null);
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
			setViewWidth(holder.img);
			if (listData.get(position).getBmp() == null) {
				holder.img.setImageResource(R.drawable.root_default);
			} else {
				holder.img.setImageBitmap(BitmapUtils.toRoundBitmap(listData
						.get(position).getBmp()));
			}
			holder.name.setText(listData.get(position).getName());
			holder.num.setText(listData.get(position).getNum());
			holder.check.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (holder.check.isChecked()) {
						state.put(position, true);
						layBot.setVisibility(View.VISIBLE);
						addImg(listData.get(position).getBmp(), state.size()
								- 1 + "");
						listData.get(position).setPosition(
								state.size() - 1 + "");
						// System.out.println("size:"
						// + listData.get(position).getPosition());
						btfinish.setText("���(" + state.size() + ")");
					} else {
						System.out.println("aa:"
								+ listData.get(position).getPosition());
						state.remove(position);
						btfinish.setText("���(" + state.size() + ")");
						delicon(listData.get(position).getPosition());

					}
				}
			});
			holder.check
					.setChecked((state.get(position) == null ? false : true));
			return convertView;
		}

		private void setViewWidth(ImageView img) {
			int width = Utils.getSecreenWidth(context);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					width / 7, width / 7);
			lp.setMargins(5, 5, 5, 5);
			img.setLayoutParams(lp);
		}

		class ViewHolder {
			LinearLayout laybg;
			TextView name;
			CheckBox check;
			TextView num;
			ImageView img;
		}
	}
}
