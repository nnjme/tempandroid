package com.changlianxi.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.inteface.UpLoadPic;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.modle.MemberModle;
import com.changlianxi.modle.SelectPicModle;
import com.changlianxi.modle.SmsPrevieModle;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.task.UpLoadPicAsyncTask;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.EditWather;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.FileUtils;
import com.changlianxi.util.Logger;
import com.changlianxi.util.PinyinUtils;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;

public class AddOneMemberActivity extends Activity implements OnClickListener,
		PostCallBack, UpLoadPic {
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
	private CircularImage img;
	private String imgPath = "";
	private ProgressDialog pd;
	private String pid = "";// ����ɹ��ĳ�ԱID
	private String type;// add ��ӳ�Ա create ����Ȧ��
	private String cmids = "";// ���������е������룬�����������������ӣ�����Ҫ�������������²Ż���ֵ
	private String code = "";// ��ԱȦ�����ID���ڷ���������Žӿ�������
	private String cirName = "";
	private String rep = "0"; // �ó�Ա�Ƿ��Ѿ�����

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_one_member);
		type = getIntent().getStringExtra("type");
		if (type.equals("add")) {
			cid = getIntent().getStringExtra("cid");
			cirName = getIntent().getStringExtra("cirName");
		}
		initView();
		setListener();
		initData();
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initView() {
		addLay = (LinearLayout) findViewById(R.id.layAdd);
		addInfo = (LinearLayout) findViewById(R.id.addInfo);
		btnNext = (Button) findViewById(R.id.next);
		editMobile = (EditText) findViewById(R.id.editmobile);
		editMobile.addTextChangedListener(new EditWather(editMobile));
		editName = (EditText) findViewById(R.id.editname);
		editEmail = (EditText) findViewById(R.id.editemail);
		back = (ImageView) findViewById(R.id.back);
		img = (CircularImage) findViewById(R.id.avatarImg);
	}

	/**
	 * ���ü����¼�
	 */
	private void setListener() {
		addLay.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		back.setOnClickListener(this);
		img.setOnClickListener(this);
		if (type.equals("add")) {
			btnNext.setText("���");
		}
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
				R.layout.layout_zhiwu, null);
		addInfo.addView(view);
		TextView txt = (TextView) view.findViewById(R.id.text);
		txt.setText(str + ":");
		setTag(str, txt);
		txt.setVisibility(View.VISIBLE);
		EditText edit = (EditText) view.findViewById(R.id.zhiwu);
		edit.setHint("����" + str);
		ImageView imbDel = (ImageView) view.findViewById(R.id.delView);
		imbDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addInfo.removeView(view);
			}
		});
	}

	/**
	 * ���ñ�ʾtag ��ȡֵ��ʱ��ʹ��
	 * 
	 * @param str
	 */
	private void setTag(String str, TextView txt) {
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
			TextView txt = (TextView) addInfo.getChildAt(i).findViewById(
					R.id.text);
			EditText edit = (EditText) addInfo.getChildAt(i).findViewById(
					R.id.zhiwu);
			getValueByTag(txt, edit);
		}
		return value;
	}

	/**
	 * �������õ�tag����ȡֵ
	 * 
	 * @param txt
	 * @param edit
	 */
	private void getValueByTag(TextView txt, EditText edit) {
		if (txt.getTag().equals("gendar")) {
			gendar = edit.getText().toString();
		} else if (txt.getTag().equals("birthday")) {
			birthday = edit.getText().toString();
		} else if (txt.getTag().equals("employer")) {
			employer = edit.getText().toString();
		} else if (txt.getTag().equals("jobtitle")) {
			jobtitle = edit.getText().toString();
		}

	}

	/**
	 * ��ʾ������Ϣ�б�
	 * 
	 * @param str
	 */
	private void showTypeDialog(final String str[]) {
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int which) {
				addView(str[which]);
				moreInfo.remove(str[which]);// ɾ����ѡ�� ÿ��ֻ�����һ��

			}
		};
		new AlertDialog.Builder(this).setTitle("ѡ���������")
				.setItems(str, listener).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		SelectPicModle modle = new SelectPicModle();
		if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD
				&& resultCode == RESULT_OK && data != null) {
			modle = BitmapUtils.getPickPic(this, data);
			imgPath = modle.getPicPath();
			img.setImageBitmap(modle.getBmp());
		}// ����ͼƬ
		else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
			if (resultCode != RESULT_OK) {
				return;
			}
			if (resultCode != RESULT_OK) {
				return;
			}
			super.onActivityResult(requestCode, resultCode, data);
			Bundle bundle = data.getExtras();
			Bitmap bitmap = (Bitmap) bundle.get("data");// ��ȡ������ص����ݣ���ת��ΪBitmapͼƬ��ʽ

			if (bitmap != null) {
				String dir = "/clx/camera/";
				Utils.createDir(dir);
				String name = FileUtils.getFileName() + ".jpg";
				String fileName = Utils.getgetAbsoluteDir(dir) + name;
				BitmapUtils.createImgToFile(bitmap, fileName);
				imgPath = fileName;
				img.setImageBitmap(bitmap);
			}
		}
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
			if (name.length() == 0) {
				Utils.showToast("��������Ϊ��!");
				return;
			}
			if (type.equals("create")) {
				MemberInfoModle modle = new MemberInfoModle();
				modle.setAvator(imgPath);
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
				return;
			}
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
			pd = new ProgressDialog(this);
			pd.show();
			break;
		case R.id.back:
			finish();
			break;
		case R.id.avatarImg:
			SelectPicPopwindow pop = new SelectPicPopwindow(this, v);
			pop.show();
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
		Logger.debug(this, "result:" + result);
		try {
			JSONObject object = new JSONObject(result);
			int rt = object.getInt("rt");
			if (rt == 1) {
				pid = object.getString("pid");
				rep = object.getString("rep");
				cmids = object.getString("cmid");
				code = object.getString("code");
				// if (rep.equals("1")) {
				// Utils.showToast("���û��Ѵ���");
				// pd.dismiss();
				// return;
				// }
				if (!imgPath.equals("")) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("cid", cid);
					map.put("uid", SharedUtils.getString("uid", ""));
					map.put("token", SharedUtils.getString("token", ""));
					map.put("pid", pid);
					UpLoadPicAsyncTask picTask = new UpLoadPicAsyncTask(map,
							"/people/iuploadAvatar", imgPath);
					picTask.setCallBack(this);
					picTask.execute();
					return;
				}
				Utils.showToast("��ӳɹ�");
				pd.dismiss();
				setModle();
				if (rep.equals("0")) {
					intentSmsPreviewActivity();
				}
			} else {
				pd.dismiss();
				String errorCoce = object.getString("err");
				Utils.showToast(ErrorCodeUtil.convertToChines(errorCoce));
			}
		} catch (JSONException e) {
			pd.dismiss();
			Utils.showToast("�쳣����");
			e.printStackTrace();
		}
	}

	/**
	 * ��ת������Ԥ������
	 */
	private void intentSmsPreviewActivity() {
		List<SmsPrevieModle> listModle = new ArrayList<SmsPrevieModle>();
		SmsPrevieModle modle = new SmsPrevieModle();
		modle.setName(editName.getText().toString());
		modle.setNum(editMobile.getText().toString().replace("-", ""));
		modle.setContent("�װ���" + editName.getText().toString() + ",����������"
				+ cirName + "Ȧ��.�����Է���http://clx.teeker.com/" + code + "�鿴����");
		listModle.add(modle);
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("contactsList", (Serializable) listModle);
		intent.putExtras(bundle);
		intent.putExtra("cmids", cmids);
		intent.putExtra("cid", cid);
		intent.setClass(this, SmsPreviewActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * ������֮��ķ���ֵ ���б����ˢ��ʹ��
	 * 
	 * @return
	 */
	private void setModle() {
		MemberModle modle = new MemberModle();
		modle.setId(pid);
		modle.setName(editName.getText().toString());
		modle.setEmployer(employer);
		modle.setImg(imgPath);
		modle.setSort_key(PinyinUtils.getPinyin(editName.getText().toString()));
		CLXApplication.setModle(modle);
		finish();
	}

	/**
	 * ͼƬ�ϴ���ɽӿ�
	 */
	@Override
	public void upLoadFinish(boolean flag) {
		pd.dismiss();
		if (flag) {
			Utils.showToast("��ӳɹ�");
			setModle();
			if (rep.equals("0")) {
				intentSmsPreviewActivity();
			}
		} else {
			Utils.showToast("ͼ���ϴ�ʧ��");
		}
	}
}
