package com.changlianxi.activity;

import java.io.File;
import java.util.HashMap;
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
import com.changlianxi.inteface.UpLoadCircleLogo;
import com.changlianxi.modle.CircleModle;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.task.CircleLogoAsyncTask;
import com.changlianxi.util.AsyncImageLoader;
import com.changlianxi.util.AsyncImageLoader.ImageCallback;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.FileUtils;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.util.WigdtContorl;

public class CircleSettingActivity extends Activity implements OnClickListener,
		UpLoadCircleLogo {
	private ImageView btnBack;
	private ImageView editClean;
	private EditText editCirName;
	private ImageView cirImg;
	private SelectPicPopwindow popWindow;
	private String cirIconPath = "";
	private String newCirIconPath = "";// �ı�֮��ĵ�ַ
	private LinearLayout layadd;
	private LinearLayout zhiwu;
	private int count;// ���ְ������
	private Button btnSave;
	private EditText description;
	private JSONArray jsonAry = new JSONArray();
	private JSONObject jsonObj;
	private ProgressDialog progressDialog;
	private String cid = "";// Ҫ�޸�Ȧ�ӵ�id
	private TextView cirName;
	private AsyncImageLoader ImageLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_circle_setting);
		cid = getIntent().getStringExtra("cid");
		ImageLoader = new AsyncImageLoader(this);
		cirName = (TextView) findViewById(R.id.cirName);
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
		btnSave = (Button) findViewById(R.id.btn_save);
		btnSave.setOnClickListener(this);
		getCircleInfo(cid);
	}

	private void getCircleInfo(String cid) {
		CircleModle modle = DBUtils.findCircleInfoById(cid);
		cirName.setText(modle.getCirName());
		editCirName.setText(modle.getCirName());
		String path = modle.getCirIcon();
		cirIconPath = path;
		// �첽����ͼƬ
		Bitmap cachedImage = ImageLoader.loaDrawable(path, new ImageCallback() {
			@Override
			public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
				System.out.println("imageUrl" + imageUrl + "  imageDrawable:"
						+ imageDrawable.getWidth());
			}
		});
		if (cachedImage != null) {
			cirImg.setImageBitmap(cachedImage);

		}
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
			newCirIconPath = cursor.getString(column_index);
			Logger.debug(this, "cirIconPath:" + newCirIconPath);
			Bitmap bitmap = getLocalPicByPath(newCirIconPath);
			if (bitmap != null) {
				cirImg.setImageBitmap(BitmapUtils.toRoundBitmap(bitmap));
			}
		}
	}

	/**
	 * .��ȡͼƬ����ͼ
	 * 
	 * @param path
	 * @return
	 */
	private Bitmap getLocalPicByPath(String path) {
		String imgName = FileUtils.getFileName(path);
		Bitmap bitmap = BitmapUtils.loadImgThumbnail(imgName,
				MediaStore.Images.Thumbnails.MICRO_KIND,
				CircleSettingActivity.this);
		return bitmap;
	}

	/**
	 * ����ְ��json��
	 * 
	 * @param name
	 *            ְ������
	 */
	private void BuildAddJson(String name) {
		try {
			jsonObj = new JSONObject();
			jsonObj.put("name", name);
			jsonObj.put("op", "new");
			jsonObj.put("id", "0");
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
		for (int i = 0; i < zhiwu.getChildCount(); i++) {
			EditText t = (EditText) zhiwu.getChildAt(i)
					.findViewById(R.id.zhiwu);
			BuildAddJson(t.getText().toString());
		}
		return jsonAry.toString();
	}

	/**
	 * �༭Ȧ�ӽӿ�
	 * 
	 */
	class EditCirTask extends AsyncTask<String, Integer, String> {

		// �ɱ䳤�������������AsyncTask.exucute()��Ӧ
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cid", cid);
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("token", SharedUtils.getString("token", ""));
			map.put("name", editCirName.getText().toString());
			map.put("description", description.getText().toString());
			map.put("roles", getValue());
			String result = HttpUrlHelper.postData(map, "/circles/iedit");
			Logger.debug(this, result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			String cid;
			String roles;
			try {
				JSONObject object = new JSONObject(result);
				int rt = object.getInt("rt");
				if (rt == 1) {
					cid = object.getString("cid");
					roles = object.getString("roles");
					Logger.debug(this, "cid:" + cid + "  roles:" + roles);
					if (newCirIconPath.equals("")) {
						Utils.showToast("Ȧ���޸ĳɹ�!");
						editDB(cid);
						progressDialog.dismiss();
						finish();
						return;
					}
					// �ϴ�Ȧ��logo
					CircleLogoAsyncTask cirTask = new CircleLogoAsyncTask(
							newCirIconPath, cid);
					cirTask.setCallBack(CircleSettingActivity.this);
					cirTask.execute();
					return;
				} else {
					Utils.showToast("Ȧ���޸�ʧ��!");
					progressDialog.dismiss();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// ����������������������ʾһ���Ի�������򵥴���
			progressDialog = new ProgressDialog(CircleSettingActivity.this);
			progressDialog.show();
		}
	}

	/**
	 * �޸ı������ݿ�
	 * 
	 * @param cid
	 */
	private void editDB(String cid) {
		ContentValues values = new ContentValues();
		// ��ö����в����ֵ�ԣ����м���������ֵ��ϣ�����뵽��һ�е�ֵ��ֵ��������ݿ⵱�е���������һ��
		values.put("cirName", editCirName.getText().toString());
		if (newCirIconPath.equals("")) {
			values.put("cirImg", cirIconPath);
		} else {
			values.put("cirImg", newCirIconPath);
		}
		DBUtils.editCircleInfo(values, cid);
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
		case R.id.btn_save:
			new EditCirTask().execute();
			break;
		default:
			break;
		}
	}

	@Override
	public void upLoadFinish(boolean flag) {
		progressDialog.dismiss();
		if (flag) {
			Utils.showToast("�޸ĳɹ�!");
			editDB(cid);
			finish();
			return;
		}
		Utils.showToast("Ȧ��ͼ���ϴ�ʧ��!");

	}

}
