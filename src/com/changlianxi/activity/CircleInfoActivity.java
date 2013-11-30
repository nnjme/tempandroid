package com.changlianxi.activity;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.modle.CircleIdetailModle;
import com.changlianxi.task.GetCircleIdetailTask;
import com.changlianxi.task.GetCircleIdetailTask.GetCircleIdetail;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.AsyncImageLoader;
import com.changlianxi.util.AsyncImageLoader.ImageCallback;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.util.WigdtContorl;
import com.changlianxi.view.Home;

public class CircleInfoActivity extends Activity implements OnClickListener,
		GetCircleIdetail, PostCallBack {
	private TextView circleName;// 圈子名称
	private TextView titleName;
	private TextView circleDescription;// 圈子描述
	private TextView circleRoles;// 圈子职务
	private ImageView circleLogo;
	private ImageView back;
	private ImageView edit;
	private Button btnDissolve;// 解散圈子
	private Button btnExit;
	private String cid;
	private ProgressDialog pd;
	private AsyncImageLoader ImageLoader;
	private int type;// 1退出圈子 2 解散圈子

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_circle_info);
		cid = getIntent().getStringExtra("cid");
		ImageLoader = new AsyncImageLoader(this);
		findViewById();
		setListener();

	}

	private void findViewById() {
		titleName = (TextView) findViewById(R.id.titleName);
		circleName = (TextView) findViewById(R.id.circleName);
		circleDescription = (TextView) findViewById(R.id.circleDis);
		circleRoles = (TextView) findViewById(R.id.circleZhiWu);
		circleLogo = (ImageView) findViewById(R.id.circleLogo);
		btnExit = (Button) findViewById(R.id.btnExit);
		btnDissolve = (Button) findViewById(R.id.btnDissolve);
		back = (ImageView) findViewById(R.id.back);
		edit = (ImageView) findViewById(R.id.edit);
	}

	private void setListener() {
		circleLogo.setOnClickListener(this);
		WigdtContorl.setImageWidth(this, circleLogo);
		btnExit.setOnClickListener(this);
		btnDissolve.setOnClickListener(this);
		back.setOnClickListener(this);
		edit.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		pd = new ProgressDialog(this);
		GetCircleIdetailTask task = new GetCircleIdetailTask(cid);
		task.setTaskCallBack(this);
		task.execute();
		pd.show();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.circleLogo:
			break;
		case R.id.btnDissolve:
			PostTask("/circles/idissolve");
			type = 2;
			break;
		case R.id.btnExit:
			PostTask("/circles/iquit");
			type = 1;
			break;
		case R.id.back:
			finish();
			break;
		case R.id.edit:
			Intent intent = new Intent();
			intent.setClass(this, EditCircleActivity.class);
			intent.putExtra("cid", cid);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}

	private void PostTask(String url) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("cid", cid);
		PostAsyncTask task = new PostAsyncTask(this, map, url);
		task.setTaskCallBack(this);
		task.execute();
		pd = new ProgressDialog(this);
		pd.show();

	}

	/**
	 * 接口回调
	 */
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
		Bitmap cachedImage = ImageLoader.loaDrawable(logo, new ImageCallback() {
			@Override
			public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
			}
		});
		if (cachedImage != null) {
			circleLogo.setImageBitmap(BitmapUtils.toRoundBitmap(cachedImage));

		} else {
			circleLogo.setImageResource(R.drawable.pic);
		}
		int father = 0;// 会长数量
		int mather = 0;// 副会长数量
		for (int i = 0; i < modle.getRolesModle().size(); i++) {
			if (modle.getRolesModle().get(i).getRoleName().equals("Father")) {
				father++;
			}
		}
		mather = modle.getRolesModle().size() - father;
		String numfather = "会长<font color=\"#fd7a00\">" + father
				+ "</font>人<br>";
		String nummather = "副会长<font color=\"#fd7a00\">" + mather + "</font>人";
		circleRoles.setText(Html.fromHtml(numfather + nummather));
	}

	@Override
	public void taskFinish(String result) {
		pd.dismiss();
		try {
			JSONObject object = new JSONObject(result);
			int rt = object.getInt("rt");
			if (rt != 1) {
				String errCode = object.getString("err");
				Utils.showToast(ErrorCodeUtil.convertToChines(errCode));
				return;
			}
			if (type == 1) {
				Utils.showToast("退出圈子成功");
				Home.exitCircle(cid);
				setResult(1);
			} else if (type == 2) {
				Utils.showToast("解散圈子成功");
				setResult(1);
			}
			finish();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
