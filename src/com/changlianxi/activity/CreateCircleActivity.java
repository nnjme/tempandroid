package com.changlianxi.activity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.data.Circle;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.UpLoadPic;
import com.changlianxi.modle.CircleModle;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.modle.SelectPicModle;
import com.changlianxi.modle.SmsPrevieModle;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.CircleLogoAsyncTask;
import com.changlianxi.task.CreateNewCircleTask;
import com.changlianxi.task.IinviteUserTask;
import com.changlianxi.task.IinviteUserTask.IinviteUser;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.task.UpLoadPicAsyncTask;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;
import com.changlianxi.view.Home;
import com.umeng.analytics.MobclickAgent;

public class CreateCircleActivity extends BaseActivity implements
		OnClickListener, UpLoadPic, PostCallBack {
	private List<SmsPrevieModle> contactsList = new ArrayList<SmsPrevieModle>();
	private List<SmsPrevieModle> smsList = new ArrayList<SmsPrevieModle>();// 展示使用
	private ImageView btnBack;
	private EditText editCirName;
	private CircularImage cirImg;
	private SelectPicPopwindow popWindow;
	private String cirIconPath = "";
	private Button createCir;
	private EditText description;
	private Dialog progressDialog;
	private String cid = "";// 创建圈子返回的uid 邀请成员和上传 logo用
	private String type;// more 标示 邀请多个成员；one标示 添加一个成员
	private MemberInfoModle infoModle;
	private String pid = ""; // 上传单个用户时返回的pid 供上传用户头像时使用
	private String cmids = "";// 邀请成员时返回的邀请成员的id
	private String code = "";// 邀请链接值 需要邀请时有值
	private TextView titleTxt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_ciecle);
		CLXApplication.addInviteActivity(this);
		getActivityValue();
		btnBack = (ImageView) findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		editCirName = (EditText) findViewById(R.id.circleName);
		description = (EditText) findViewById(R.id.description);
		cirImg = (CircularImage) findViewById(R.id.circleIcon);
		cirImg.setImageResource(R.drawable.pic);
		cirImg.setOnClickListener(this);
		createCir = (Button) findViewById(R.id.createCircle);
		createCir.setOnClickListener(this);
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText("创建圈子");
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
	
	/**
	 * 得到上一个activi传过来的值
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

		}// 拍摄图片
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
	 * 跳转到短信预览界面
	 */
	private void intentSmsPreviewActivity() {
		if (type.equals("one")) {
			smsList.get(0).setContent(
					"亲爱的" + infoModle.getName() + ",邀请您加入"
							+ editCirName.getText().toString()
							+ "圈子.您可以访问http://clx.teeker.com/a/b/" + "ada"
							+ "查看详情");
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
		Utils.rightOut(this);

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
	private void getDetails(String details) {
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
						editCirName.getText().toString(), str[2]);
				modle.setContent(data);
				modle.setName(contactsList.get(i).getName());
				modle.setNum(contactsList.get(i).getNum());
				smsList.add(modle);
			}

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
			map.put("roles", "");
			String result = HttpUrlHelper.postData(map, "/circles/iadd");
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
			progressDialog = DialogUtil.getWaitDialog(
					CreateCircleActivity.this, "请稍后");
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
		Circle modle = new Circle(Integer.parseInt(cid));
		modle.setCirIcon(cirIconPath);
		modle.setCirID(cid);
		modle.setCirName(editCirName.getText().toString());
		BroadCast.sendBroadCast(this, Constants.REFRESH_CIRCLE_LIST);// 发送广播更新圈子列表
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			Utils.rightOut(this);
			break;
		case R.id.circleIcon:
			Utils.hideSoftInput(this);
			popWindow = new SelectPicPopwindow(this, v);
			popWindow.show();
			break;
		case R.id.createCircle:
			if (editCirName.getText().toString().length() == 0) {
				Utils.showToast("请输入圈子名称");
				return;
			}
			createNewCirTask();
			break;
		default:
			break;
		}
	}

	private void createNewCirTask() {
//		progressDialog = DialogUtil.getWaitDialog(
//				CreateCircleActivity.this, "请稍后");
//		progressDialog.show();
//		// TODO Auto-generated method stub
//		Circle circle = new Circle(0, editCirName.getText().toString(), description.getText().toString(), cirIconPath);
//		CreateNewCircleTask circleTask = new CreateNewCircleTask();
//		circleTask.setTaskCallBack(new BaseAsyncTask.PostCallBack<RetError>() {
//
//			@Override
//			public void taskFinish(RetError result) {
//				// TODO Auto-generated method stub
//				progressDialog.dismiss();
//			}
//		});
//		circleTask.executeWithCheckNet(circle);
		new CreateCirTask().execute();
	}
	/**
	 * 上传图片返回结果处理 包括圈子logo以及 个人用户头像
	 */
	@Override
	public void upLoadFinish(boolean flag) {
		insertDB(cid);// logo上传后更新本地圈子列表
		if (flag) {
			if (type.equals("one")) {// 上传单个成员
				IiviteOneMember();
				return;
			}
			// 添加从通讯录选择的联系人
			IinviteUserTask task = new IinviteUserTask(cid, contactsList);
			task.setTaskCallBack(new IinviteUser() {

				@Override
				public void inviteUser(String rt, String details, String err) {
					if (rt.equals("1")) {
						getDetails(details);
						if (code.contains("null") && smsList.size() == 0) {
							finish();
							CLXApplication.exitSmsInvite();
							Utils.rightOut(CreateCircleActivity.this);
							return;
						}
						intentSmsPreviewActivity();
					} else {
						Utils.showToast(ErrorCodeUtil.convertToChines(err));
					}
				}
			});
			task.execute();
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
		try {
			JSONObject object = new JSONObject(result);
			int rt = object.getInt("rt");
			String rep = "";
			if (rt == 1) {
				rep = object.getString("rep");
				pid = object.getString("pid");
				cmids = object.getString("cmid");
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
								Utils.showToast("邀请成功");
								intentSmsPreviewActivity();
							} else {
								Utils.showToast("邀请失败");
							}
						}
					});
					picTask.execute();
					return;
				}
				Utils.showToast("添加成功");
				if (rep.equals("1")) {
					finish();
					CLXApplication.exitSmsInvite();
					Utils.rightOut(this);
					return;
				}
				intentSmsPreviewActivity();

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
		super.onDestroy();
		if (progressDialog == null) {
			return;
		}
		if (progressDialog.isShowing() && progressDialog != null) {
			progressDialog.dismiss();
		}
	}
}
