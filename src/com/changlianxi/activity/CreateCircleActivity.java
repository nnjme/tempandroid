package com.changlianxi.activity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.UpLoadPic;
import com.changlianxi.modle.CircleModle;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.modle.SelectPicModle;
import com.changlianxi.modle.SmsPrevieModle;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.task.CircleLogoAsyncTask;
import com.changlianxi.task.IinviteUserTask;
import com.changlianxi.task.IinviteUserTask.IinviteUser;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.task.UpLoadPicAsyncTask;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;
import com.changlianxi.view.Home;

public class CreateCircleActivity extends Activity implements OnClickListener,
		UpLoadPic, PostCallBack {
	private List<SmsPrevieModle> contactsList = new ArrayList<SmsPrevieModle>();
	private List<SmsPrevieModle> smsList = new ArrayList<SmsPrevieModle>();// չʾʹ��
	private ImageView btnBack;
	private ImageView editClean;
	private EditText editCirName;
	private CircularImage cirImg;
	private SelectPicPopwindow popWindow;
	private String cirIconPath = "";
	private Button createCir;
	private EditText description;
	private ProgressDialog progressDialog;
	private String cid = "";// ����Ȧ�ӷ��ص�uid �����Ա���ϴ� logo��
	private String type;// more ��ʾ ��������Ա��one��ʾ ���һ����Ա
	private MemberInfoModle infoModle;
	private String pid = ""; // �ϴ������û�ʱ���ص�pid ���ϴ��û�ͷ��ʱʹ��
	private String cmids = "";// �����Աʱ���ص������Ա��id
	private String code = "";// ��������ֵ ��Ҫ����ʱ��ֵ

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_create_ciecle);
		getActivityValue();
		btnBack = (ImageView) findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		editClean = (ImageView) findViewById(R.id.editClean);
		editClean.setOnClickListener(this);
		editCirName = (EditText) findViewById(R.id.circleName);
		description = (EditText) findViewById(R.id.description);
		cirImg = (CircularImage) findViewById(R.id.circleIcon);
		cirImg.setOnClickListener(this);
		// WigdtContorl.setViewWidth(cirImg, this, 4, 5, 15, 0, 5);
		createCir = (Button) findViewById(R.id.createCircle);
		createCir.setOnClickListener(this);
	}

	/**
	 * �õ���һ��activi��������ֵ
	 */
	@SuppressWarnings("unchecked")
	private void getActivityValue() {
		Bundle bundle = getIntent().getExtras();
		type = getIntent().getStringExtra("type");
		if (type.equals("one")) {
			infoModle = (MemberInfoModle) bundle.getSerializable("modle");
			SmsPrevieModle smsModle = new SmsPrevieModle();
			smsModle.setName(infoModle.getName());
			smsModle.setNum(infoModle.getCellPhone());
			smsList.add(smsModle);
			return;
		}
		contactsList = (List<SmsPrevieModle>) bundle
				.getSerializable("contactsList");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD
				&& resultCode == RESULT_OK && data != null) {
			SelectPicModle modle = BitmapUtils.getPickPic(this, data);
			cirIconPath = modle.getPicPath();
			// cirImg.setImageBitmap(modle.getBmp());
			BitmapUtils.startPhotoZoom(this, data.getData());

		}// ����ͼƬ
		else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
			if (resultCode != RESULT_OK) {
				return;
			}
			String fileName = popWindow.getTakePhotoPath();
			// bitmap = BitmapUtils.FitSizeImg(fileName);
			cirIconPath = fileName;
			BitmapUtils.startPhotoZoom(this, Uri.fromFile(new File(fileName)));
			// cirImg.setImageBitmap(bitmap);
		} else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_DROP) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				cirImg.setImageBitmap(photo);
			}
		}

	}

	/**
	 * ����һ����Ա
	 */
	private void IiviteOneMember() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cid", cid);
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("name", infoModle.getName());
		map.put("cellphone", infoModle.getCellPhone());
		map.put("email", infoModle.getEmail());
		map.put("gendar", infoModle.getGendar());
		map.put("birthday", infoModle.getBirthday());
		map.put("employer", infoModle.getEmployer());
		map.put("jobtitle", infoModle.getJobTitle());
		PostAsyncTask task = new PostAsyncTask(this, map, "/people/iinviteOne");
		task.setTaskCallBack(this);
		task.execute();
	}

	/**
	 * ��ת������Ԥ������
	 */
	private void intentSmsPreviewActivity() {
		if (type.equals("one")) {
			smsList.get(0).setContent(
					"�װ���" + infoModle.getName() + ",����������"
							+ editCirName.getText().toString()
							+ "Ȧ��.�����Է���http://clx.teeker.com/a/b/" + "ada"
							+ "�鿴����");
		}
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("contactsList", (Serializable) smsList);
		intent.putExtras(bundle);
		intent.putExtra("cmids", cmids);
		intent.putExtra("cid", cid);
		intent.setClass(CreateCircleActivity.this, SmsPreviewActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * �������ع������ַ���
	 * 
	 * @param details
	 *            ʵ�� ����"details":" 1,4,EO2VqrHI,6;1,5,E6zBbKeg,7"�ַ����������Էֺŷָ
	 *            ��Ӧÿ��person��������
	 *            ��Ȼ���Զ��ŷָ���Ĳ��֣���һ���ֱ�ʾ����Ƿ�ɹ���1�ɹ�����1���ɹ����ڶ�������pid����������������code
	 *            �����Ĳ�����cmid��
	 */
	private void getDetails(String details) {
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
				modle.setContent("�װ���" + contactsList.get(i).getName()
						+ ",����������" + editCirName.getText().toString()
						+ "Ȧ��.�����Է���http://clx.teeker.com/a/b/" + str[2]
						+ "�鿴����");
				modle.setName(contactsList.get(i).getName());
				modle.setNum(contactsList.get(i).getNum());
				smsList.add(modle);
			}

		}

	}

	/**
	 * ����Ȧ��
	 * 
	 */
	class CreateCirTask extends AsyncTask<String, Integer, String> {

		// �ɱ䳤�������������AsyncTask.exucute()��Ӧ
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("token", SharedUtils.getString("token", ""));
			map.put("name", editCirName.getText().toString());
			map.put("description", description.getText().toString());
			map.put("roles", "");
			String result = HttpUrlHelper.postData(map, "/circles/iadd");
			Logger.debug(this, "CreateCirTask:" + result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (isCreateSuccess(result)) {
				// �ϴ�Ȧ��logo
				CircleLogoAsyncTask cirTask = new CircleLogoAsyncTask(
						cirIconPath, cid);
				cirTask.setCallBack(CreateCircleActivity.this);
				cirTask.execute();
			}
		}

		@Override
		protected void onPreExecute() {
			// ����������������������ʾһ���Ի�������򵥴���
			progressDialog = new ProgressDialog(CreateCircleActivity.this);
			progressDialog.show();
		}
	}

	/**
	 * �Ƿ񴴽��ɹ�
	 * 
	 * @param str
	 * @return
	 */
	private boolean isCreateSuccess(String str) {
		try {
			JSONObject object = new JSONObject(str);
			int rt = object.getInt("rt");
			if (rt == 1) {
				cid = object.getString("cid");
				insertDB(cid);
				return true;
			} else {
				Utils.showToast("Ȧ�Ӵ���ʧ��!");
				progressDialog.dismiss();
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * ���뱾�����ݿ�
	 * 
	 * @param cid
	 */
	private void insertDB(String cid) {
		ContentValues values = new ContentValues();
		// ��ö����в����ֵ�ԣ����м���������ֵ��ϣ�����뵽��һ�е�ֵ��ֵ��������ݿ⵱�е���������һ��
		values.put("cirID", cid);
		values.put("cirName", editCirName.getText().toString());
		values.put("cirImg", cirIconPath);
		DBUtils.insertData("circlelist", values);
		CircleModle modle = new CircleModle();
		modle.setCirIcon(cirIconPath);
		modle.setCirID(cid);
		modle.setCirName(editCirName.getText().toString());
		Home.refreshCircleList(modle);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.editClean:
			editCirName.setText("");
			break;
		case R.id.circleIcon:
			popWindow = new SelectPicPopwindow(this, v);
			popWindow.show();
			break;
		case R.id.createCircle:
			if (editCirName.getText().toString().length() == 0) {
				Utils.showToast("������Ȧ������");
				return;
			}
			new CreateCirTask().execute();
			break;
		default:
			break;
		}
	}

	/**
	 * �ϴ�ͼƬ���ؽ������ ����Ȧ��logo�Լ� �����û�ͷ��
	 */
	@Override
	public void upLoadFinish(boolean flag) {
		if (flag) {
			if (type.equals("one")) {// �ϴ�������Ա
				IiviteOneMember();
				return;
			}
			// ��Ӵ�ͨѶ¼ѡ�����ϵ��
			IinviteUserTask task = new IinviteUserTask(cid, contactsList);
			task.setTaskCallBack(new IinviteUser() {

				@Override
				public void inviteUser(String rt, String details) {
					if (rt.equals("1")) {
						getDetails(details);
						Utils.showToast("����ɹ���");
						if (code.contains("null")) {
							finish();
							return;
						}
						intentSmsPreviewActivity();
					} else {
						Utils.showToast("����ʧ�ܣ�");
					}
				}
			});
			task.execute();
		} else {
			Utils.showToast("Ȧ��ͼ���ϴ�ʧ��!");
			progressDialog.dismiss();
		}
	}

	/**
	 * �����û�ʱ�ķ��ؽ��������
	 */
	@Override
	public void taskFinish(String result) {
		// String rep = ""; // �ó�Ա�Ƿ��Ѿ�����1-YES,0-NO
		try {
			JSONObject object = new JSONObject(result);
			int rt = object.getInt("rt");
			if (rt == 1) {
				pid = object.getString("pid");
				// rep = object.getString("rep");
				cmids = object.getString("cmid");
				// if (rep.equals("1")) {
				// Utils.showToast("���û��Ѵ���");
				// // finish();// �Ѿ�ע��ֱ�ӷ���
				// return;
				// }
				if (!infoModle.getAvator().equals("")) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("cid", cid);
					map.put("uid", SharedUtils.getString("uid", ""));
					map.put("token", SharedUtils.getString("token", ""));
					map.put("pid", pid);
					UpLoadPicAsyncTask picTask = new UpLoadPicAsyncTask(map,
							"/people/iuploadAvatar", infoModle.getAvator(),
							"avatar");
					picTask.setCallBack(new UpLoadPic() {
						@Override
						public void upLoadFinish(boolean flag) {
							progressDialog.dismiss();
							if (flag) {
								Utils.showToast("����ɹ�");
								intentSmsPreviewActivity();
							} else {
								Utils.showToast("����ʧ��");
							}
						}
					});
					picTask.execute();
					return;
				}
				Utils.showToast("��ӳɹ�");
				intentSmsPreviewActivity();
			} else {
				Utils.showToast("���ʧ��");
				progressDialog.dismiss();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (progressDialog == null) {
			return;
		}
		if (progressDialog.isShowing() && progressDialog != null) {
			progressDialog.dismiss();
		}
	}
}
