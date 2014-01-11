package com.changlianxi.activity;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.ContactModle;
import com.changlianxi.modle.MemberModle;
import com.changlianxi.modle.SmsPrevieModle;
import com.changlianxi.task.IinviteUserTask;
import com.changlianxi.task.IinviteUserTask.IinviteUser;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.PinyinUtils;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;
import com.changlianxi.view.QuickAlphabeticBar;
import com.changlianxi.view.QuickAlphabeticBar.OnTouchingLetterChangedListener;
import com.changlianxi.view.QuickAlphabeticBar.touchUp;
import com.changlianxi.view.SearchEditText;
import com.umeng.analytics.MobclickAgent;

/**
 * 从通讯录导入圈子程序界面
 * 
 * @author teeker_bin
 * 
 */
public class SelectContactsActivity extends BaseActivity implements
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
	private SearchEditText editSearch;
	private Dialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contacts);
		CLXApplication.addInviteActivity(this);
		type = getIntent().getStringExtra("type");
		if (type.equals("add")) {
			cid = getIntent().getStringExtra("cid");
			cirName = getIntent().getStringExtra("cirName");
		}
		initView();
		adapter = new ContactsAdapter(this, listModle);
		listview.setAdapter(adapter);
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		dialog = DialogUtil.getWaitDialog(this, "请稍后");
		dialog.show();
		init();

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

	private void init() {
		Uri uri = Uri.parse("content://com.android.contacts/data/phones"); // 联系人的Uri
		// 联系人的Uri
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
			if (dialog != null) {
				dialog.dismiss();
			}
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					String name = cursor.getString(0);
					String number = cursor.getString(1).replace(" ", "");
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
					adapter.add(modle);

				}

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
		editSearch = (SearchEditText) view.findViewById(R.id.search);
		editSearch.addTextChangedListener(new EditWather());
		listview.addHeaderView(view);
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText("添加第一批成员");
		indexBar = (QuickAlphabeticBar) findViewById(R.id.indexBar);
		indexBar.setOnTouchingLetterChangedListener(this);
		indexBar.getBackground().setAlpha(0);
		indexBar.setOnTouchUp(this);
		selectedChar = (TextView) findViewById(R.id.selected_tv);
		selectedChar.setVisibility(View.INVISIBLE);
		listview.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Utils.hideSoftInput(SelectContactsActivity.this);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});

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
				editSearch.setCompoundDrawables(null, null, null, null);
				return;
			}
			Drawable del = getResources().getDrawable(R.drawable.del);
			del.setBounds(0, 0, del.getMinimumWidth(), del.getMinimumHeight());
			editSearch.setCompoundDrawables(null, null, del, null);
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

	private void addImg(Bitmap bmp, String tag, String name) {
		View view = LayoutInflater.from(this).inflate(
				R.layout.select_contact_buttom, null);
		CircularImage img = (CircularImage) view.findViewById(R.id.img);
		TextView lastName = (TextView) view.findViewById(R.id.lastName);
		lastName.setText(name.substring(name.length() - 1));
		view.setTag(tag);
		if (bmp == null) {
			lastName.setVisibility(View.VISIBLE);
			img.setVisibility(View.GONE);
		} else {
			img.setImageBitmap(bmp);
			lastName.setVisibility(View.GONE);
			img.setVisibility(View.VISIBLE);
		}
		addicon.addView(view);

		// hScroll.scrollBy(hScroll.getRight(), 0);
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
				addImg(bmp, position + "", searchListModles.get(position)
						.getName());
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
			addImg(bmp, position + "", listModle.get(position).getName());
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
			Utils.rightOut(this);

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
			Utils.rightOut(this);
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
		final Dialog dialog = DialogUtil.getWaitDialog(this, "请稍后");
		dialog.show();
		IinviteUserTask task = new IinviteUserTask(cid, contactsList);
		task.setTaskCallBack(new IinviteUser() {

			@Override
			public void inviteUser(String rt, String details, String err) {
				dialog.dismiss();
				if (rt.equals("1")) {
					getDetails(details, contactsList);
					setModle(contactsList);
					if (code.contains("null") && smsList.size() == 0) {
						Utils.showToast("邀请成员已存在圈子中");
						finish();
						CLXApplication.exitSmsInvite();
						Utils.rightOut(SelectContactsActivity.this);
						return;
					}
					intentSmsPreviewActivity();
				} else {
					Utils.showToast(ErrorCodeUtil.convertToChines(err));
				}
			}
		});
		task.execute();
	}

	/**
	 * 添加完成之后的返回值 供列表界面刷新使用
	 * 
	 * @return
	 */
	private void setModle(List<SmsPrevieModle> contactsList) {
		List<MemberModle> listModles = new ArrayList<MemberModle>();
		for (int i = 0; i < contactsList.size(); i++) {
			MemberModle modle = new MemberModle();
			String name = contactsList.get(i).getName();
			modle.setName(name);
			modle.setImg("");
			modle.setSort_key(PinyinUtils.getPinyin(name));
			listModles.add(modle);
		}
		finish();
		Utils.rightOut(this);

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
		// finish();
		// Utils.rightOut(this);
		Utils.leftOutRightIn(this);

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
		String name = DBUtils.getMyName(SharedUtils.getString("uid", ""));
		for (int i = 0; i < detail.length; i++) {
			String str[] = detail[i].split(",");
			if (str[0].equals("1")) {
				if (str[2].equals("") || str[2] == null) {
					str[2] = "null";
					code += str[2];
					continue;
				}
				code += str[2];
				cmids += str[3] + ",";
				SmsPrevieModle modle = new SmsPrevieModle();
				String data = getResources().getString(R.string.sms_content);
				data = String.format(data, contactsList.get(i).getName(), name,
						cirName, str[2]);
				modle.setContent(data);
				modle.setName(contactsList.get(i).getName());
				modle.setNum(contactsList.get(i).getNum());
				smsList.add(modle);
			}

		}

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
				holder.img = (CircularImage) convertView.findViewById(R.id.img);
				holder.check = (CheckBox) convertView
						.findViewById(R.id.checkBox1);
				holder.num = (TextView) convertView.findViewById(R.id.num);
				holder.lastName = (TextView) convertView
						.findViewById(R.id.lastName);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// setViewWidth(holder.img);
			String name = listData.get(position).getName();
			// 绘制联系人名称
			holder.name.setText(name);
			holder.lastName.setText(name.substring(name.length() - 1));
			// 绘制联系人号码
			holder.num.setText(listData.get(position).getNum());
			// 绘制联系人头像
			if (0 == listModle.get(position).getPhotoid()) {
				// holder.img.setImageResource(R.drawable.pic_bg);
				holder.img.setVisibility(View.GONE);
				holder.lastName.setVisibility(View.VISIBLE);
			} else {
				Uri uri = ContentUris.withAppendedId(
						ContactsContract.Contacts.CONTENT_URI,
						listModle.get(position).getContactid());
				InputStream input = ContactsContract.Contacts
						.openContactPhotoInputStream(
								SelectContactsActivity.this
										.getContentResolver(), uri);
				Bitmap contactPhoto = BitmapFactory.decodeStream(input);
				// Bitmap roundPhoto = BitmapUtils.toRoundBitmap(contactPhoto);
				listModle.get(position).setBmp(contactPhoto);
				holder.img.setImageBitmap(contactPhoto);
				holder.lastName.setVisibility(View.GONE);
				holder.img.setVisibility(View.VISIBLE);
			}
			holder.check.setChecked(listData.get(position).isChecked());
			if (position % 2 == 0) {
				holder.laybg.setBackgroundColor(Color.WHITE);
			} else {
				holder.laybg.setBackgroundColor(getResources().getColor(
						R.color.f6));
			}
			showAlpha(position, holder, listData);
			return convertView;
		}

		@Override
		public int getCount() {
			return listData.size();
		}

		public void add(ContactModle modle) {
			listModle.add(modle);
			listData.add(modle);
			notifyDataSetChanged();
		}

		public void setData(List<ContactModle> list) {
			listData.clear();
			listData.addAll(list);
			notifyDataSetChanged();
		}

		@Override
		public Object getItem(int position) {
			return listData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

	private void showAlpha(int position, ViewHolder holder,
			List<ContactModle> listData) {
		// 当前联系人的sortKey
		String currentStr = getAlpha(listData.get(position).getSort_key());
		// 上一个联系人的sortKey
		String previewStr = (position - 1) >= 0 ? getAlpha(listData.get(
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
		CircularImage img;
		TextView lastName;
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
		indexBar.getBackground().setAlpha(200);
		selectedChar.setText(s);
		selectedChar.setVisibility(View.VISIBLE);
		position = (findIndexer(s)) + 1;
		listview.setSelection(position);
	}

	@Override
	public void onTouchUp() {
		indexBar.getBackground().setAlpha(0);
		selectedChar.setVisibility(View.GONE);
		listview.setSelection(position);

	}
}