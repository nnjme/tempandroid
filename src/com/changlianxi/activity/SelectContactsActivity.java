package com.changlianxi.activity;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.changlianxi.task.IinviteUserTask;
import com.changlianxi.task.IinviteUserTask.IinviteUser;
import com.changlianxi.util.PinyinUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.QuickAlphabeticBar;
import com.changlianxi.view.SearchEditText;
import com.changlianxi.view.QuickAlphabeticBar.OnTouchingLetterChangedListener;
import com.changlianxi.view.QuickAlphabeticBar.touchUp;

/**
 * 从通讯录导入圈子程序界面
 * 
 * @author teeker_bin
 * 
 */
public class SelectContactsActivity extends Activity implements
		OnClickListener, OnItemClickListener, OnTouchingLetterChangedListener,
		touchUp {
	private ListView listview;// 显示联系人的列表
	private LinearLayout layBot;// 用来显示或隐藏选择数量
	private Button btfinish;
	private ImageView back;
	private LinearLayout addicon;
	private ContactsAdapter adapter;
	private String type;
	private String cid;
	private String cirName;
	private String cmids = "";// 邀请成员时返回的邀请成员的id
	private String code = "";// 邀请链接值 需要邀请时有值
	private List<SmsPrevieModle> smsList = new ArrayList<SmsPrevieModle>();// 展示使用
	private List<ContactModle> listModle = new ArrayList<ContactModle>();
	private List<ContactModle> searchListModles = new ArrayList<ContactModle>();// 存储搜索列表
	private TextView titleTxt;
	private AsyncQueryHandler asyncQuery;
	private QuickAlphabeticBar indexBar;// 右侧字母拦
	private TextView selectedChar;// 显示选择字母
	private int position;// 当前字母子listview中所对应的位置

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
		initView();
		adapter = new ContactsAdapter(this, listModle);
		listview.setAdapter(adapter);
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		init();
	}

	private void init() {
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人的Uri
		String[] projection = {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER, "sort_key",
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.PHOTO_ID }; // 查询的列
		asyncQuery.startQuery(0, null, uri, projection, null, null,
				"sort_key COLLATE LOCALIZED asc"); // 按照sort_key升序查询
	}

	/**
	 * 数据库异步查询类AsyncQueryHandler
	 * 
	 * @author administrator
	 * 
	 */
	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		/**
		 * 查询结束的回调函数
		 */
		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {

				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					String name = cursor.getString(0);
					String number = cursor.getString(1);
					if (!Utils.isPhoneNum(StringUtils.cutHead(number, "+86"))) {
						continue;
					}
					String sortKey = cursor.getString(2);
					int contactId = cursor.getInt(3);
					Long photoId = cursor.getLong(4);
					ContactModle modle = new ContactModle();
					modle.setName(name);
					modle.setNum(number);
					modle.setSort_key(sortKey);
					modle.setPhotoid(photoId);
					modle.setKey_pinyin_fir(PinyinUtils.getPinyinFrt(name));
					modle.setContactid((long) contactId);
					// listModle.add(modle);
					adapter.add(modle);

				}
				// if (listModle.size() > 0) {
				// adapter.setData(listModle);
				// }
			}
		}

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
		layBot.setVisibility(View.GONE);
		listview = (ListView) findViewById(R.id.contactList);
		listview.setOnItemClickListener(this);
		listview.setCacheColorHint(0);
		View view = LayoutInflater.from(this).inflate(R.layout.header, null);
		SearchEditText editSearch = (SearchEditText) view
				.findViewById(R.id.search);
		editSearch.addTextChangedListener(new EditWather());
		listview.addHeaderView(view);
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText("添加第一批成员");
		indexBar = (QuickAlphabeticBar) findViewById(R.id.indexBar);
		indexBar.setOnTouchingLetterChangedListener(this);
		indexBar.getBackground().setAlpha(125);
		indexBar.setOnTouchUp(this);
		selectedChar = (TextView) findViewById(R.id.selected_tv);
		selectedChar.setVisibility(View.INVISIBLE);
	}

	class EditWather implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {
			String key = s.toString().toLowerCase();
			if (key.length() == 0) {
				adapter.setData(listModle);
				searchListModles.clear();
				indexBar.setVisibility(View.VISIBLE);
				Utils.hideSoftInput(SelectContactsActivity.this);
				return;
			}
			indexBar.setVisibility(View.GONE);
			layBot.setVisibility(View.GONE);
			searchListModles.clear();
			for (int i = 0; i < listModle.size(); i++) {
				String name = listModle.get(i).getName();
				String pinyin = listModle.get(i).getSort_key().toLowerCase();
				String pinyinFir = listModle.get(i).getKey_pinyin_fir()
						.toLowerCase();
				String mobileNum = listModle.get(i).getNum();
				if (name.contains(key) || pinyin.contains(key)
						|| pinyinFir.contains(key) || mobileNum.contains(key)) {
					ContactModle modle = listModle.get(i);
					searchListModles.add(modle);

				}
			}
			adapter.setData(searchListModles);
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

	/** 得到手机通讯录联系人信息 **/

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
			img.setImageResource(R.drawable.pic);
		} else {
			img.setImageBitmap(bmp);
		}
		addicon.addView(img);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long id) {
		layBot.setVisibility(View.VISIBLE);
		int position = arg2 - 1;
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.check.toggle();
		if (searchListModles.size() > 0) {
			searchListModles.get(position).setChecked(holder.check.isChecked());
			adapter.notifyDataSetChanged();
			if (holder.check.isChecked()) {
				Bitmap bmp = searchListModles.get(position).getBmp();
				addImg(bmp, position + "");
			} else {
				delicon(position + "");
			}
			btfinish.setText("完成(" + addicon.getChildCount() + ")");
			return;
		}
		listModle.get(position).setChecked(holder.check.isChecked());
		adapter.notifyDataSetChanged();
		if (holder.check.isChecked()) {
			Bitmap bmp = listModle.get(position).getBmp();
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
			List<SmsPrevieModle> Modles = new ArrayList<SmsPrevieModle>();
			for (int i = 0; i < listModle.size(); i++) {
				if (!listModle.get(i).isChecked()) {
					continue;
				}
				ContactModle modle = (ContactModle) adapter.getItem(i);
				String name = modle.getName();
				String num = modle.getNum();
				SmsPrevieModle smsModle = new SmsPrevieModle();
				smsModle.setName(name);
				smsModle.setNum(num.replace(" ", ""));
				Modles.add(smsModle);
			}
			if (Modles.size() == 0) {
				Utils.showToast("至少选择一位联系人");
				return;
			}
			if (type.equals("add")) {
				addContacts(Modles);
				return;
			}
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("contactsList", (Serializable) Modles);
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

	class ContactsAdapter extends BaseAdapter {
		ViewHolder holder = null;
		List<ContactModle> listData = new ArrayList<ContactModle>();

		public ContactsAdapter(Context context, List<ContactModle> list) {
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(SelectContactsActivity.this)
						.inflate(R.layout.contact_list_item, null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
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
			// 绘制联系人名称
			holder.name.setText(listData.get(position).getName());
			// 绘制联系人号码
			holder.num.setText(listData.get(position).getNum());
			// 绘制联系人头像
			if (0 == listModle.get(position).getPhotoid()) {
				holder.img.setImageResource(R.drawable.pic);
			} else {
				Uri uri = ContentUris.withAppendedId(
						ContactsContract.Contacts.CONTENT_URI,
						listModle.get(position).getContactid());
				InputStream input = ContactsContract.Contacts
						.openContactPhotoInputStream(
								SelectContactsActivity.this
										.getContentResolver(), uri);
				Bitmap contactPhoto = BitmapFactory.decodeStream(input);
				holder.img.setImageBitmap(contactPhoto);
			}
			holder.check.setChecked(listData.get(position).isChecked());
			if (position % 2 == 0) {
				holder.laybg.setBackgroundColor(Color.WHITE);
			} else {
				holder.laybg.setBackgroundColor(getResources().getColor(
						R.color.f6));
			}
			showAlpha(position, holder);
			return convertView;
		}

		@Override
		public int getCount() {
			return listData.size();
		}

		public void add(ContactModle modle) {
			listModle.add(modle);
			listData = listModle;
			notifyDataSetChanged();
		}

		public void setData(List<ContactModle> list) {
			listData = list;
			notifyDataSetChanged();
		}

		@Override
		public Object getItem(int position) {
			return listModle.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

	private void showAlpha(int position, ViewHolder holder) {
		// 当前联系人的sortKey
		String currentStr = getAlpha(listModle.get(position).getSort_key());
		// 上一个联系人的sortKey
		String previewStr = (position - 1) >= 0 ? getAlpha(listModle.get(
				position - 1).getSort_key()) : " ";
		/**
		 * 判断显示#、A-Z的TextView隐藏与可�?
		 */
		if (!previewStr.equals(currentStr)) { //
			// 当前联系人的sortKey�?上一个联系人的sortKey，说明当前联系人是新组�?
			holder.alpha.setVisibility(View.VISIBLE);
			holder.alpha.setText(currentStr);
		} else {
			holder.alpha.setVisibility(View.GONE);
		}
	}

	/**
	 * 提取英文的首字母，非英文字母�?代替�?
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}

		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase(); // 大写输出
		} else {
			return "#";
		}

	}

	class ViewHolder {
		TextView alpha;
		LinearLayout laybg;
		TextView name;
		CheckBox check;
		TextView num;
		ImageView img;
	}

	/**
	 * 设置listview的当前选中值
	 * 
	 * @param s
	 * @return
	 */
	public int findIndexer(String s) {
		int position = 0;
		for (int i = 0; i < listModle.size(); i++) {
			String sortkey = listModle.get(i).getSort_key().toUpperCase();
			if (sortkey.startsWith(s)) {
				position = i;
				break;
			}
		}
		return position;
	}

	@Override
	public void onTouchingLetterChanged(String s) {
		selectedChar.setText(s);
		selectedChar.setVisibility(View.VISIBLE);
		position = (findIndexer(s)) + 1;
		listview.setSelection(position);
	}

	@Override
	public void onTouchUp() {
		selectedChar.setVisibility(View.GONE);
		listview.setSelection(position);

	}
}