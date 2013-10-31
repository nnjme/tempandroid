package com.changlianxi.activity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.CircleModle;
import com.changlianxi.modle.ContactModle;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.FileUtils;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.util.WigdtContorl;
import com.changlianxi.view.Home;

public class CreateCircleActivity extends Activity implements OnClickListener {
	private List<ContactModle> contactsList = new ArrayList<ContactModle>();
	private ImageView btnBack;
	private ImageView editClean;
	private EditText editCirName;
	private ImageView cirImg;
	private SelectPicPopwindow popWindow;
	private String cirIconPath;
	private LinearLayout layadd;
	private LinearLayout zhiwu;
	private int count;// ���ְ������
	private Button createCir;
	private EditText description;
	private JSONArray jsonAry = new JSONArray();
	private JSONObject jsonObj;
	private ProgressDialog progressDialog;
	private String cid = "";// ����Ȧ�ӷ��ص�uid �����Ա���ϴ� logo��

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_create_ciecle);
		Bundle bundle = getIntent().getExtras();
		contactsList = (List<ContactModle>) bundle
				.getSerializable("contactsList");
		System.out.println("size:" + contactsList.size());
		btnBack = (ImageView) findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		editClean = (ImageView) findViewById(R.id.editClean);
		editClean.setOnClickListener(this);
		editCirName = (EditText) findViewById(R.id.circleName);
		description = (EditText) findViewById(R.id.description);
		cirImg = (ImageView) findViewById(R.id.circleIcon);
		cirImg.setOnClickListener(this);
		WigdtContorl.setViewWidth(cirImg, this, 5, 5, 15, 0, 5);
		layadd = (LinearLayout) findViewById(R.id.layAdd);
		zhiwu = (LinearLayout) findViewById(R.id.zhiwu);
		layadd.setOnClickListener(this);
		createCir = (Button) findViewById(R.id.createCircle);
		createCir.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("onActivityResultonActivityResult");
		if (requestCode == Utils.REQUEST_CODE_GETIMAGE_BYSDCARD
				&& resultCode == RESULT_OK && data != null) {
			Uri thisUri = data.getData();// ���ͼƬ��uri
			// ���￪ʼ�ĵڶ����֣���ȡͼƬ��·����
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(thisUri, proj, null, null, null);
			// ���Ҹ������ ����ǻ���û�ѡ���ͼƬ������ֵ
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			// ����������ֵ��ȡͼƬ·��
			cirIconPath = cursor.getString(column_index);
			String imgName = FileUtils.getFileName(cirIconPath);
			Logger.debug(this, "cirIconPath:" + cirIconPath);
			Bitmap bitmap = BitmapUtils.loadImgThumbnail(imgName,
					MediaStore.Images.Thumbnails.MICRO_KIND,
					CreateCircleActivity.this);
			if (bitmap == null) {
				System.out.println("nummnullnll");
			}
			if (bitmap != null) {
				cirImg.setImageBitmap(BitmapUtils.toRoundBitmap(bitmap));
			}
		}
	}

	/**
	 * ����json��
	 * 
	 */
	private void BuildJson(String name, String num) {
		try {
			jsonObj = new JSONObject();
			jsonObj.put("name", name);
			jsonObj.put("cellphone", num);
			jsonAry.put(jsonObj);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���ְ��
	 */
	private void addView() {
		count++;
		final View view = LayoutInflater.from(this).inflate(
				R.layout.layout_zhiwu, null);
		zhiwu.addView(view);
		TextView txt = (TextView) view.findViewById(R.id.text);
		if (count == 1) {
			txt.setVisibility(View.VISIBLE);
		}
		ImageView imbDel = (ImageView) view.findViewById(R.id.delView);
		imbDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				zhiwu.removeView(view);
				count--;
			}
		});
	}

	/**
	 * ��ȡְ������
	 */
	private String getValue() {
		String value = "";
		for (int i = 0; i < zhiwu.getChildCount(); i++) {
			EditText t = (EditText) zhiwu.getChildAt(i)
					.findViewById(R.id.zhiwu);
			value += t.getText().toString() + ",";

		}
		return value;
	}

	/**
	 * ��������Ա
	 * 
	 */
	class IinviteMoreitTask extends AsyncTask<String, Integer, String> {
		// �ɱ䳤�������������AsyncTask.exucute()��Ӧ
		@Override
		protected String doInBackground(String... params) {
			for (int i = 0; i < contactsList.size(); i++) {
				BuildJson(contactsList.get(i).getName(), contactsList.get(i)
						.getNum());
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cid", cid);
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("token", SharedUtils.getString("token", ""));
			map.put("persons", jsonAry.toString());
			String json = HttpUrlHelper.postData(map, "/people/iinviteMore");
			Logger.debug(this, json);
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			try {
				JSONObject object = new JSONObject(result);
				int rt = object.getInt("rt");
				if (rt == 1) {
					Utils.showToast("����ɹ���");
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable("contactsList",
							(Serializable) contactsList);
					intent.putExtras(bundle);
					intent.putExtra("circleName", editCirName.getText()
							.toString());
					intent.setClass(CreateCircleActivity.this,
							SmsPreviewActivity.class);
					startActivity(intent);
					finish();
				} else {
					Utils.showToast("����ʧ�ܣ�");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// ����������������������ʾһ���Ի�������򵥴���
		}
	}

	/**
	 * �ϴ�Ȧ��logo
	 * 
	 */
	class UpLoadLogoTask extends AsyncTask<Object, Integer, Object> {
		// �ɱ䳤�������������AsyncTask.exucute()��Ӧ
		String rt = "1";

		@Override
		protected Object doInBackground(Object... params) {
			String result = "";
			File file = new File(cirIconPath);
			result = HttpUrlHelper.postCircleLogo(HttpUrlHelper.strUrl
					+ "/circles/iuploadLogo", file, cid,
					SharedUtils.getString("uid", ""),
					SharedUtils.getString("token", ""));
			Logger.debug(this, result);
			try {
				JSONObject jsonobject = new JSONObject(result);
				rt = jsonobject.getString("rt");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return rt;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (result.equals("1")) {
				new IinviteMoreitTask().execute();
			} else {
				Utils.showToast("Ȧ��ͼ���ϴ�ʧ��!");
				progressDialog.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			// ����������������������ʾһ���Ի�������򵥴���
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
			map.put("roles", getValue());
			String result = HttpUrlHelper.postData(map, "/circles/iadd");
			Logger.debug(this, "CreateCirTask:" + result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (isCreateSuccess(result)) {
				new UpLoadLogoTask().execute();
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
		case R.id.layAdd:
			addView();
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

}
