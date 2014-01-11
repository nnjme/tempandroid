package com.changlianxi.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.modle.SmsPrevieModle;
import com.changlianxi.popwindow.AddKeyAndValuePopwindow;
import com.changlianxi.popwindow.AddKeyAndValuePopwindow.OnSelectKey;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.EditWather;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.Switch;
import com.umeng.analytics.MobclickAgent;

public class AddOneMemberActivity extends BaseActivity implements
		OnClickListener, PostCallBack {
	private List<String> moreInfo = new ArrayList<String>();
	private LinearLayout addLay;
	private LinearLayout addInfo;
	private String gendar = "";
	private String birthday = "";
	private String employer = "";
	private String jobtitle = "";
	private Button btnNext;
	private EditText editName;
	private EditText editMobile;
	private String cid = "";
	private EditText editEmail;
	private ImageView back;
	private ImageView img;
	private Dialog pd;
	private String pid = "";// ����ɹ��ĳ�ԱID
	private String type;// add ��ӳ�Ա create ����Ȧ��
	private String cmids = "";// ���������е������룬�����������������ӣ�����Ҫ�������������²Ż���ֵ
	private String code = "";// ��ԱȦ�����ID���ڷ���������Žӿ�������
	private String cirName = "";
	private String rep = "0"; // �ó�Ա�Ƿ��Ѿ�����
	private TextView titleTxt;
	private Switch btnswitch;
	private LinearLayout parent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_one_member);
		CLXApplication.addInviteActivity(this);
		type = getIntent().getStringExtra("type");
		if (type.equals("add")) {
			cid = getIntent().getStringExtra("cid");
			cirName = getIntent().getStringExtra("cirName");
		}
		initView();
		setListener();
		initData();
	}
	/**����ҳ��ͳ��
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
	 * ��ʼ���ؼ�
	 */
	private void initView() {
		parent = (LinearLayout) findViewById(R.id.parent);
		addLay = (LinearLayout) findViewById(R.id.layAdd);
		addInfo = (LinearLayout) findViewById(R.id.addInfo);
		btnNext = (Button) findViewById(R.id.next);
		editMobile = (EditText) findViewById(R.id.editmobile);
		editMobile.addTextChangedListener(new EditWather(editMobile));
		editName = (EditText) findViewById(R.id.editname);
		editEmail = (EditText) findViewById(R.id.editemail);
		back = (ImageView) findViewById(R.id.back);
		img = (ImageView) findViewById(R.id.avatarImg);
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText("������ϵ��");
		btnswitch = (Switch) findViewById(R.id.switchBtn);
	}

	/**
	 * ���ü����¼�
	 */
	@SuppressLint("NewApi")
	private void setListener() {
		addLay.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		back.setOnClickListener(this);
		img.setOnClickListener(this);
		if (type.equals("add")) {
			btnNext.setText("���");
		}
		btnswitch.setChecked(false);
	}

	private void initData() {
		moreInfo.add("�Ա�");
		moreInfo.add("����");
		moreInfo.add("������λ");
		moreInfo.add("����ְλ");

	}

	/**
	 * ���ְ��
	 */
	private void addView(String str) {
		final View view = LayoutInflater.from(this).inflate(
				R.layout.input_contact, null);
		addInfo.addView(view);
		TextView txt = (TextView) view.findViewById(R.id.zhiwuName);
		EditText edit = (EditText) view.findViewById(R.id.zhiwu);
		txt.setText(str + "��");
		setTag(str, edit);

	}

	/**
	 * ���ñ�ʾtag ��ȡֵ��ʱ��ʹ��
	 * 
	 * @param str
	 */
	private void setTag(String str, EditText txt) {
		if (str.equals("�Ա�")) {
			txt.setTag("gendar");
		} else if (str.equals("����")) {
			txt.setTag("birthday");
		} else if (str.equals("������λ")) {
			txt.setTag("employer");
		} else if (str.equals("����ְλ")) {
			txt.setTag("jobtitle");
		}
	}

	/**
	 * ��ȡ��ӵĻ�����Ϣ
	 */
	private String getValue() {
		String value = "";
		for (int i = 0; i < addInfo.getChildCount(); i++) {
			EditText edit = (EditText) addInfo.getChildAt(i).findViewById(
					R.id.zhiwu);
			getValueByTag(edit);
		}
		return value;
	}

	/**
	 * �������õ�tag����ȡֵ
	 * 
	 * @param txt
	 * @param edit
	 */
	private void getValueByTag(EditText edit) {
		if (edit.getTag().equals("gendar")) {
			gendar = edit.getText().toString();
		} else if (edit.getTag().equals("birthday")) {
			birthday = edit.getText().toString();
		} else if (edit.getTag().equals("employer")) {
			employer = edit.getText().toString();
		} else if (edit.getTag().equals("jobtitle")) {
			jobtitle = edit.getText().toString();
		}

	}

	/**
	 * ��ʾ������Ϣ�б�
	 * 
	 * @param str
	 */
	private void showTypeDialog(final String str[]) {
		AddKeyAndValuePopwindow pop = new AddKeyAndValuePopwindow(this, parent,
				str, "ѡ���������");
		pop.setCallBack(new OnSelectKey() {

			@Override
			public void getSelectKey(String str) {
				addView(str);
				moreInfo.remove(str);// ɾ����ѡ�� ÿ��ֻ�����һ��
			}
		});
		pop.show();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layAdd:
			showTypeDialog(moreInfo.toArray(new String[moreInfo.size()]));
			break;
		case R.id.next:
			getValue();
			String name = editName.getText().toString();
			String mobile = editMobile.getText().toString().replace("-", "");
			if (mobile.length() == 0) {
				Utils.showToast("�ֻ��Ų���Ϊ��!");
				return;
			}
			if (!Utils.isPhoneNum(mobile)) {
				Utils.showToast("��������Ч���ֻ�����");
				return;
			}
			if (name.length() == 0) {
				Utils.showToast("��������Ϊ��!");
				return;
			}
			if (type.equals("create")) {
				MemberInfoModle modle = new MemberInfoModle();
				modle.setBirthday(birthday);
				modle.setCellPhone(mobile);
				modle.setEmail(editEmail.getText().toString());
				modle.setEmployer(employer);
				modle.setGendar(gendar);
				modle.setJobTitle(jobtitle);
				modle.setName(name);
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("modle", modle);
				intent.putExtras(bundle);
				intent.setClass(this, CreateCircleActivity.class);
				intent.putExtra("type", "one");
				startActivity(intent);
				finish();
				Utils.rightOut(this);
				return;
			}
			pd = DialogUtil.getWaitDialog(this, "���Ժ�");
			pd.show();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cid", cid);
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("token", SharedUtils.getString("token", ""));
			map.put("name", name);
			map.put("cellphone", mobile);
			map.put("email", editEmail.getText().toString());
			map.put("gendar", gendar);
			map.put("birthday", birthday);
			map.put("employer", employer);
			map.put("jobtitle", jobtitle);
			PostAsyncTask task = new PostAsyncTask(this, map,
					"/people/iinviteOne");
			task.setTaskCallBack(this);
			task.execute();
			if (btnswitch.isChecked()) {
				testInsert(name, mobile);
			}
			break;
		case R.id.back:
			finish();
			Utils.rightOut(this);

			break;
		case R.id.avatarImg:
			break;
		default:
			break;
		}
	}

	/**
	 * �����󷵻صĽ��������
	 */
	@Override
	public void taskFinish(String result) {
		try {
			JSONObject object = new JSONObject(result);
			int rt = object.getInt("rt");
			if (rt == 1) {
				pid = object.getString("pid");
				rep = object.getString("rep");
				cmids = object.getString("cmid");
				code = object.getString("code");
				if (rep.equals("0")) {
					finish();
					intentSmsPreviewActivity();
					return;
				}
				Utils.showToast("��ӳɹ�");
				pd.dismiss();
				finish();
				CLXApplication.exitSmsInvite();
			} else {
				pd.dismiss();
				String errorCoce = object.getString("err");
				Utils.showToast(ErrorCodeUtil.convertToChines(errorCoce));
			}
		} catch (JSONException e) {
			pd.dismiss();
			e.printStackTrace();
		}
	}

	/**
	 * ��ת������Ԥ������
	 */
	private void intentSmsPreviewActivity() {
		String name = DBUtils.getMyName(SharedUtils.getString("uid", ""));
		List<SmsPrevieModle> listModle = new ArrayList<SmsPrevieModle>();
		SmsPrevieModle modle = new SmsPrevieModle();
		modle.setName(editName.getText().toString());
		modle.setNum(editMobile.getText().toString().replace("-", ""));
		String data = getResources().getString(R.string.sms_content);
		data = String.format(data, editName.getText().toString(), name,
				cirName, code);
		modle.setContent(data);
		listModle.add(modle);
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("contactsList", (Serializable) listModle);
		intent.putExtras(bundle);
		intent.putExtra("cmids", cmids);
		intent.putExtra("cid", cid);
		intent.setClass(this, SmsPreviewActivity.class);
		startActivity(intent);

		Utils.leftOutRightIn(this);

	}

	@Override
	protected void onDestroy() {
		if (pd != null) {
			pd.dismiss();
		}
		super.onDestroy();
	}

	public void testInsert(String name, String num) {
		ContentResolver resolver = this.getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		ContentValues values = new ContentValues();
		// ��raw_contacts����һ������ID֮��, ����ȫ��ΪNULL�ļ�¼, ID���Զ����ɵ�
		long id = ContentUris.parseId(resolver.insert(uri, values));
		// �����ϵ������
		uri = Uri.parse("content://com.android.contacts/data");
		values.put("raw_contact_id", id);
		values.put("data2", name);
		values.put("mimetype", "vnd.android.cursor.item/name");
		resolver.insert(uri, values);
		// �����ϵ�˵绰
		values.clear(); // ����ϴε�����
		values.put("raw_contact_id", id);
		values.put("data1", num);
		values.put("data2", "2");
		values.put("mimetype", "vnd.android.cursor.item/phone_v2");
		resolver.insert(uri, values);

	}

}
