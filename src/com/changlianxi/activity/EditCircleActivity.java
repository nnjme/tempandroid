package com.changlianxi.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.changlianxi.modle.CircleIdetailModle;
import com.changlianxi.modle.SelectPicModle;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.task.GetCircleIdetailTask;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.UpLoadPicAsyncTask;
import com.changlianxi.task.GetCircleIdetailTask.GetCircleIdetail;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.AsyncImageLoader;
import com.changlianxi.util.AsyncImageLoader.ImageCallback;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.util.WigdtContorl;

/**
 * 编辑圈子界面
 * 
 * @author teeker_bin
 * 
 */
public class EditCircleActivity extends Activity implements OnClickListener,
		GetCircleIdetail, PostCallBack {
	private LinearLayout addRoles;
	private LinearLayout layRoles;
	private int count;// 添加职务数量
	private JSONArray jsonAry = new JSONArray();
	private JSONObject jsonObj;
	private Button btnSave;
	private EditText circleName;// 圈子名称
	private TextView titleName;
	private EditText circleDescription;// 圈子描述
	private ImageView circleLogo;
	private AsyncImageLoader imageLoader;
	private String cid;
	private ProgressDialog pd;
	private String logoPath = "";
	private ImageView back;
	private SelectPicPopwindow pop;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_edit_circle);
		imageLoader = new AsyncImageLoader(this);
		cid = getIntent().getStringExtra("cid");
		findViewByID();
		setListener();
		pd = new ProgressDialog(this);
		GetCircleIdetailTask task = new GetCircleIdetailTask(cid);
		task.setTaskCallBack(this);
		task.execute();
		pd.show();
	}

	private void findViewByID() {
		addRoles = (LinearLayout) findViewById(R.id.addroles);
		layRoles = (LinearLayout) findViewById(R.id.layRoles);
		btnSave = (Button) findViewById(R.id.btnsave);
		titleName = (TextView) findViewById(R.id.titleName);
		circleName = (EditText) findViewById(R.id.circleName);
		circleDescription = (EditText) findViewById(R.id.circleDis);
		circleLogo = (ImageView) findViewById(R.id.circleLogo);
		WigdtContorl.setImageWidth(this, circleLogo);
		back = (ImageView) findViewById(R.id.back);

	}

	private void setListener() {
		addRoles.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		circleLogo.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	/**
	 * 添加职务
	 */
	private void addView() {
		count++;
		View view = LayoutInflater.from(this).inflate(
				R.layout.circle_roles_layout, null);
		layRoles.addView(view);
		TextView txt = (TextView) view.findViewById(R.id.textView1);
		if (count == 1) {
			txt.setVisibility(View.VISIBLE);
		} else {
			txt.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 新增职务json串
	 * 
	 * @param name
	 *            职务名称
	 */
	private void BuildAddJson(String name) {
		try {
			jsonObj = new JSONObject();
			jsonObj.put("name", name);
			jsonObj.put("op", "new");
			jsonAry.put(jsonObj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取职务名称
	 */
	private String getValue() {
		for (int i = 0; i < layRoles.getChildCount(); i++) {
			EditText t = (EditText) layRoles.getChildAt(i).findViewById(
					R.id.roleName);
			BuildAddJson(t.getText().toString());
		}
		return jsonAry.toString();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addroles:
			addView();
			break;
		case R.id.btnsave:
			saveInfo();
			break;
		case R.id.circleLogo:
			pop = new SelectPicPopwindow(this, v);
			pop.show();
			break;
		case R.id.back:
			finish();
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
		System.out.println(logo);
		Bitmap cachedImage = imageLoader.loaDrawable(logo, new ImageCallback() {
			@Override
			public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
			}
		});
		if (cachedImage != null) {
			circleLogo.setImageBitmap(BitmapUtils.toRoundBitmap(cachedImage));

		} else {
			circleLogo.setImageResource(R.drawable.pic);
		}
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
		map.put("roles", getValue());
		PostAsyncTask task = new PostAsyncTask(this, map, "/circles/iedit");
		task.setTaskCallBack(this);
		task.execute();
		pd = new ProgressDialog(this);
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
				"/circles/iuploadLogo", logoPath, "logo");
		picTask.setCallBack(new UpLoadPic() {
			@Override
			public void upLoadFinish(boolean flag) {
				pd.dismiss();
				if (flag) {
					Utils.showToast("修改成功");
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
			BitmapUtils.startPhotoZoom(this, data.getData());
		}// 拍摄图片
		else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
			if (resultCode != RESULT_OK) {
				return;
			}
			if (resultCode != RESULT_OK) {
				return;
			}
			String fileName = pop.getTakePhotoPath();
			logoPath = fileName;
			BitmapUtils.startPhotoZoom(this, Uri.fromFile(new File(fileName)));
		} else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_DROP) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				circleLogo.setImageBitmap(BitmapUtils.toRoundBitmap(photo));
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
			if (logoPath.equals("")) {
				pd.dismiss();
				Utils.showToast("修改成功");
				return;
			}
			upLoadCircleLogo();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
