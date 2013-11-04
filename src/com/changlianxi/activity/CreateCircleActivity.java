package com.changlianxi.activity;

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
import android.graphics.Bitmap;
import android.os.AsyncTask;
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

import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.PostCallBack;
import com.changlianxi.inteface.UpLoadPic;
import com.changlianxi.modle.CircleModle;
import com.changlianxi.modle.ContactModle;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.modle.SelectPicModle;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.task.CircleLogoAsyncTask;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.UpLoadPicAsyncTask;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.InfoHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;
import com.changlianxi.view.Home;

public class CreateCircleActivity extends Activity implements OnClickListener,
		UpLoadPic, PostCallBack {
	private List<ContactModle> contactsList = new ArrayList<ContactModle>();
	private ImageView btnBack;
	private ImageView editClean;
	private EditText editCirName;
	private CircularImage cirImg;
	private SelectPicPopwindow popWindow;
	private String cirIconPath;
	private LinearLayout layadd;
	private LinearLayout zhiwu;
	private int count;// 添加职务数量
	private Button createCir;
	private EditText description;
	private JSONArray jsonAry = new JSONArray();
	private JSONObject jsonObj;
	private ProgressDialog progressDialog;
	private String cid = "";// 创建圈子返回的uid 邀请成员和上传 logo用
	private String type;// more 标示 邀请多个成员；one标示 添加一个成员
	private MemberInfoModle infoModle;
	private String pid = ""; // 上传单个用户时返回的pid 供上传用户头像时使用

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
		layadd = (LinearLayout) findViewById(R.id.layAdd);
		zhiwu = (LinearLayout) findViewById(R.id.zhiwu);
		layadd.setOnClickListener(this);
		createCir = (Button) findViewById(R.id.createCircle);
		createCir.setOnClickListener(this);
	}

	/**
	 * 得到上一个activi传过来的值
	 */
	@SuppressWarnings("unchecked")
	private void getActivityValue() {
		Bundle bundle = getIntent().getExtras();
		type = getIntent().getStringExtra("type");
		if (type.equals("one")) {
			infoModle = (MemberInfoModle) bundle.getSerializable("modle");
			return;
		}
		contactsList = (List<ContactModle>) bundle
				.getSerializable("contactsList");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bitmap bitmap = null;
		if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD
				&& resultCode == RESULT_OK && data != null) {
			SelectPicModle modle = BitmapUtils.getPickPic(this, data);
			cirIconPath = modle.getPicPath();
			cirImg.setImageBitmap(modle.getBmp());
		}// 拍摄图片
		else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
			if (resultCode != RESULT_OK) {
				return;
			}
			super.onActivityResult(requestCode, resultCode, data);
			Bundle bundle = data.getExtras();
			bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

			if (bitmap != null) {
				String dir = "/clx/camera/";
				Utils.createDir(dir);
				String name = InfoHelper.getFileName() + ".jpg";
				String fileName = Utils.getgetAbsoluteDir(dir) + name;
				BitmapUtils.createImgToFile(bitmap, fileName);
				cirIconPath = fileName;
				cirImg.setImageBitmap(bitmap);
			}

		}
	}

	/**
	 * 构建json串
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
	 * 添加职务
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
	 * 获取职务名称
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
	 * 邀请一个成员
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
	 * 邀请多个成员
	 * 
	 */
	class IinviteMoreitTask extends AsyncTask<String, Integer, String> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
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
					Utils.showToast("邀请成功！");
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
					Utils.showToast("邀请失败！");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
		}
	}

	/**
	 * 创建圈子
	 * 
	 */
	class CreateCirTask extends AsyncTask<String, Integer, String> {

		// 可变长的输入参数，与AsyncTask.exucute()对应
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
				// 上传圈子logo
				CircleLogoAsyncTask cirTask = new CircleLogoAsyncTask(
						cirIconPath, cid);
				cirTask.setCallBack(CreateCircleActivity.this);
				cirTask.execute();
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			progressDialog = new ProgressDialog(CreateCircleActivity.this);
			progressDialog.show();
		}
	}

	/**
	 * 是否创建成功
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
				Utils.showToast("圈子创建失败!");
				progressDialog.dismiss();
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * 插入本地数据库
	 * 
	 * @param cid
	 */
	private void insertDB(String cid) {
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
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
				Utils.showToast("请输入圈子名称");
				return;
			}
			new CreateCirTask().execute();
			break;
		default:
			break;
		}
	}

	/**
	 * 上传图片返回结果处理 包括圈子logo以及 个人用户头像
	 */
	@Override
	public void upLoadFinish(boolean flag) {
		if (flag) {
			if (type.equals("one")) {// 上传单个成员
				IiviteOneMember();
				return;
			}
			new IinviteMoreitTask().execute();
		} else {
			Utils.showToast("圈子图标上传失败!");
			progressDialog.dismiss();
		}
	}

	/**
	 * 单个用户时的返回结果做处理
	 */
	@Override
	public void taskFinish(String result) {
		String rep = ""; // 该成员是否已经存在1-YES,0-NO
		try {
			JSONObject object = new JSONObject(result);
			int rt = object.getInt("rt");
			if (rt == 1) {
				pid = object.getString("pid");
				rep = object.getString("rep");
				if (rep.equals("1")) {
					Utils.showToast("该用户已存在");
					return;
				}
				if (!infoModle.getAvator().equals("")) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("cid", cid);
					map.put("uid", SharedUtils.getString("uid", ""));
					map.put("token", SharedUtils.getString("token", ""));
					map.put("pid", pid);
					UpLoadPicAsyncTask picTask = new UpLoadPicAsyncTask(map,
							"/people/iuploadAvatar", infoModle.getAvator());
					picTask.setCallBack(new UpLoadPic() {
						@Override
						public void upLoadFinish(boolean flag) {
							progressDialog.dismiss();
							if (flag) {
								Utils.showToast("邀请成功");
								finish();
							} else {
								Utils.showToast("邀请失败");
							}
						}
					});
					picTask.execute();
					return;
				}
				Utils.showToast("添加成功");
				finish();
			} else {
				Utils.showToast("添加失败");
				progressDialog.dismiss();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		if (progressDialog.isShowing() && progressDialog != null) {
			progressDialog.dismiss();
		}
		super.onDestroy();
	}
}
