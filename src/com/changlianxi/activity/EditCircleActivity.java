package com.changlianxi.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.UpLoadPic;
import com.changlianxi.modle.CircleIdetailModle;
import com.changlianxi.modle.SelectPicModle;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.task.GetCircleIdetailTask.GetCircleIdetail;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.task.UpLoadPicAsyncTask;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

/**
 * 编辑圈子界面
 * 
 * @author teeker_bin
 * 
 */
public class EditCircleActivity extends BaseActivity implements
		OnClickListener, GetCircleIdetail, PostCallBack {
	private Button btnSave;
	private EditText circleName;// 圈子名称
	private TextView titleName;
	private EditText circleDescription;// 圈子描述
	private ImageView circleLogo;
	private String cid;
	private Dialog pd;
	private String logoPath = "";
	private String upLoadPath = "";
	private ImageView back;
	private SelectPicPopwindow pop;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private Bitmap cirBmp = null;
	private CircleIdetailModle modle = new CircleIdetailModle();
	private boolean isCamera;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_circle);
		imageLoader = CLXApplication.getImageLoader();
		options = CLXApplication.getOptions();
		cid = getIntent().getStringExtra("cid");
		findViewByID();
		setListener();
		getSercverData();
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
	
	private void getSercverData() {
		modle = DBUtils.getCircleDetail(cid);
		circleDescription.setText(modle.getDescription());
		circleName.setText(modle.getName());
		titleName.setText(modle.getName());
		String path = modle.getLogo();
		if (!path.startsWith("http")) {
			path = "file://" + path;
		}
		imageLoader.displayImage(path, circleLogo, options);
	}

	private void findViewByID() {
		btnSave = (Button) findViewById(R.id.btnsave);
		titleName = (TextView) findViewById(R.id.titleName);
		circleName = (EditText) findViewById(R.id.circleName);
		circleDescription = (EditText) findViewById(R.id.circleDis);
		circleLogo = (ImageView) findViewById(R.id.circleLogo);
		back = (ImageView) findViewById(R.id.back);

	}

	private void setListener() {
		btnSave.setOnClickListener(this);
		circleLogo.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnsave:
			if (circleName.getText().toString().length() == 0) {
				Utils.showToast("圈子名称不能为空");
				return;
			}
			saveInfo();
			break;
		case R.id.circleLogo:
			Utils.hideSoftInput(this);
			pop = new SelectPicPopwindow(this, v);
			pop.show();
			break;
		case R.id.back:
			exitSuccess();
			break;

		default:
			break;
		}
	}

	@Override
	public void getIdetail(CircleIdetailModle modle) {
		pd.dismiss();
		if (modle == null) {
			return;
		}
		circleDescription.setText(modle.getDescription());
		circleName.setText(modle.getName());
		titleName.setText(modle.getName());
		String logo = StringUtils.JoinString(modle.getLogo(), "_200x200");
		imageLoader.displayImage(logo, circleLogo, options);
	}

	/**
	 * 保存修改信息
	 * 
	 * @param url
	 */
	private void saveInfo() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("cid", cid);
		map.put("name", circleName.getText());
		map.put("description", circleDescription.getText().toString());
		PostAsyncTask task = new PostAsyncTask(this, map, "/circles/iedit");
		task.setTaskCallBack(this);
		task.execute();
		pd = DialogUtil.getWaitDialog(this, "请稍后");
		pd.show();

	}

	/**
	 * 上传圈子logo
	 */
	private void upLoadCircleLogo() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cid", cid);
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		UpLoadPicAsyncTask picTask = new UpLoadPicAsyncTask(map,
				"/circles/iuploadLogo", upLoadPath, "logo");
		picTask.setCallBack(new UpLoadPic() {
			@Override
			public void upLoadFinish(boolean flag) {
				pd.dismiss();
				if (flag) {
					Utils.showToast("修改成功");
					BroadCast.sendBroadCast(EditCircleActivity.this,
							Constants.REFRESH_CIRCLE_LIST);// 发送广播更新圈子列表
					exitSuccess();
				} else {
					Utils.showToast("修改失败");
				}
			}
		});
		picTask.execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		SelectPicModle modle = new SelectPicModle();
		if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD
				&& resultCode == RESULT_OK && data != null) {
			modle = BitmapUtils.getPickPic(this, data);
			logoPath = modle.getPicPath();
			upLoadPath = BitmapUtils.startPhotoZoom(this, data.getData());
			isCamera = false;
		}// 拍摄图片
		else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
			logoPath = pop.getTakePhotoPath();
			upLoadPath = BitmapUtils.startPhotoZoom(this,
					Uri.fromFile(new File(logoPath)));
			isCamera = true;
		} else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_DROP
				&& data != null) {
			if (isCamera) {
				File file = new File(logoPath);
				if (file.isFile() && file.exists()) {
					file.delete();
				}
			}
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				circleLogo.setImageBitmap(photo);
				cirBmp = photo;
			}
		}
	}

	@Override
	public void taskFinish(String result) {
		try {
			JSONObject object = new JSONObject(result);
			int rt = object.getInt("rt");
			if (rt != 1) {
				String errCode = object.getString("err");
				Utils.showToast(ErrorCodeUtil.convertToChines(errCode));
				pd.dismiss();
				return;
			}
			ContentValues values = new ContentValues();
			values.put("cirName", circleName.getText().toString());
			values.put("cirDescribe", circleDescription.getText().toString());
			if (!logoPath.equals("")) {
				values.put("cirIcon", logoPath);
			}
			DBUtils.updateInfo(Constants.CIRCLEDETAIL, values, "cid=?",
					new String[] { cid });
			if (logoPath.equals("")) {
				pd.dismiss();
				Utils.showToast("修改成功");
				exitSuccess();
				return;
			}
			upLoadCircleLogo();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void exitSuccess() {
		Intent intent = new Intent();
		intent.putExtra("flag", false);
		intent.putExtra("circleName", circleName.getText().toString());
		intent.putExtra("circleDescription", circleDescription.getText()
				.toString());
		intent.putExtra("cirBmp", cirBmp);
		setResult(2, intent);
		finish();
		Utils.rightOut(this);

	}
}
