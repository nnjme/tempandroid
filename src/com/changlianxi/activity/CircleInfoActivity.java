package com.changlianxi.activity;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.CircleIdetailModle;
import com.changlianxi.task.GetCircleIdetailTask;
import com.changlianxi.task.GetCircleIdetailTask.GetCircleIdetail;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.Home;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class CircleInfoActivity extends BaseActivity implements
		OnClickListener, GetCircleIdetail, PostCallBack {
	private TextView circleName;// 圈子名称
	private TextView titleName;
	private TextView circleDescription;// 圈子描述
	// private TextView circleRoles;// 圈子职务
	private TextView circleMemberCount;
	private ImageView circleLogo;
	private ImageView back;
	private ImageView edit;
	private Button btnDissolve;// 解散圈子
	private Button btnExit;
	private String cid;
	private Dialog pd;
	private int type;// 1退出圈子 2 解散圈子
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private CircleIdetailModle modle = new CircleIdetailModle();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_circle_info);
		cid = getIntent().getStringExtra("cid");
		findViewById();
		setListener();
		modle = DBUtils.getCircleDetail(cid);
		imageLoader = CLXApplication.getImageLoader();
		options = CLXApplication.getOptions();
		if (!Utils.isNetworkAvailable()) {
			Utils.showToast("请检查网络");
			return;
		}
		if (modle != null) {
			isSelf(modle.getCreator());
			setvalue(modle.getName(), modle.getLogo(), modle.getDescription(),
					modle.getMembersTotal() + "", modle.getMembersVerified()
							+ "");
		} else {
			pd = DialogUtil.getWaitDialog(this, "请稍后");
			pd.show();
		}
		getServerData();
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
	

	private void setvalue(String name, String cirIcon, String des,
			String total, String ver) {
		circleDescription.setText(des);
		circleName.setText(name);
		titleName.setText(name);
		String logo = StringUtils.JoinString(cirIcon, "_200x200");
		imageLoader.displayImage(logo, circleLogo, options);
		setCount(total, ver);
	}

	private void getServerData() {
		GetCircleIdetailTask task = new GetCircleIdetailTask(cid);
		task.setTaskCallBack(this);
		task.execute();

	}

	private void findViewById() {
		titleName = (TextView) findViewById(R.id.titleName);
		circleName = (TextView) findViewById(R.id.circleName);
		circleDescription = (TextView) findViewById(R.id.circleDis);
		// circleRoles = (TextView) findViewById(R.id.circleZhiWu);
		circleLogo = (ImageView) findViewById(R.id.circleLogo);
		btnExit = (Button) findViewById(R.id.btnExit);
		btnDissolve = (Button) findViewById(R.id.btnDissolve);
		back = (ImageView) findViewById(R.id.back);
		edit = (ImageView) findViewById(R.id.edit);
		circleMemberCount = (TextView) findViewById(R.id.circleMemberCount);
	}

	private void setListener() {
		circleLogo.setOnClickListener(this);
		btnExit.setOnClickListener(this);
		btnDissolve.setOnClickListener(this);
		back.setOnClickListener(this);
		edit.setOnClickListener(this);

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
			Intent it = new Intent();
			it.putExtra("flag", false);
			setResult(1, it);
			finish();
			Utils.rightOut(this);
			break;
		case R.id.edit:
			Intent intent = new Intent();
			intent.setClass(this, EditCircleActivity.class);
			intent.putExtra("cid", cid);
			// startActivity(intent);
			startActivityForResult(intent, 2);
			Utils.leftOutRightIn(this);
			// finish();
			break;
		default:
			break;
		}
	}

	private void isSelf(String creatorID) {
		if (creatorID.equals(SharedUtils.getString("uid", ""))) {
			btnDissolve.setVisibility(View.VISIBLE);
			edit.setVisibility(View.VISIBLE);
		} else {
			btnExit.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2 && data != null) {
			boolean flag = data.getBooleanExtra("flag", false);
			if (flag) {
				Intent intent = new Intent();
				intent.putExtra("flag", flag);
				setResult(1, intent);
				finish();
				Utils.rightOut(this);
			} else {
				String cirName = data.getStringExtra("circleName");
				String circleDescriptionStr = data
						.getStringExtra("circleDescription");
				Bitmap bmp = data.getParcelableExtra("cirBmp");
				if (bmp != null) {
					circleLogo.setImageBitmap(bmp);
				}
				circleName.setText(cirName);
				circleDescription.setText(circleDescriptionStr);
			}
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
		pd = DialogUtil.getWaitDialog(CircleInfoActivity.this, "请稍后");
		pd.show();

	}

	/**
	 * 接口回调
	 */
	@Override
	public void getIdetail(CircleIdetailModle modle) {
		if (pd != null) {
			pd.dismiss();
		}
		if (modle == null) {
			return;
		}
		isSelf(modle.getCreator());
		setvalue(modle.getName(), modle.getLogo(), modle.getDescription(),
				modle.getMembersTotal() + "", modle.getMembersVerified() + "");
		// circleDescription.setText(modle.getDescription());
		// circleName.setText(modle.getName());
		// titleName.setText(modle.getName());
		// String logo = StringUtils.JoinString(modle.getLogo(), "_200x200");
		// imageLoader.displayImage(logo, circleLogo, options);
		// int father = 0;// 会长数量
		// int mather = 0;// 副会长数量
		// for (int i = 0; i < modle.getRolesModle().size(); i++) {
		// if (modle.getRolesModle().get(i).getRoleName().equals("Father")) {
		// father++;
		// }
		// }
		// mather = modle.getRolesModle().size() - father;
		// String numfather = "会长<font color=\"#fd7a00\">" + father
		// + "</font>人<br>";
		// String nummather = "副会长<font color=\"#fd7a00\">" + mather +
		// "</font>人";
		// circleRoles.setText(Html.fromHtml(numfather + nummather));
		// String total = "<font color=\"#fd7a00\">" + modle.getMembersTotal()
		// + "</font>人";
		// String unverified = " （<font color=\"#fd7a00\">"
		// + modle.getMembersVerified() + "</font>认证成员）";
		// circleMemberCount.setText(Html.fromHtml(total + unverified));
	}

	private void setCount(String total, String veriofied) {
		String strtotal = "<font color=\"#fd7a00\">" + total + "</font>人";
		String strunverified = " （<font color=\"#fd7a00\">" + veriofied
				+ "</font>认证成员）";
		circleMemberCount.setText(Html.fromHtml(strtotal + strunverified));
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
				exitSuccess();
			} else if (type == 2) {
				Utils.showToast("解散圈子成功");
				exitSuccess();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void exitSuccess() {
		Home.exitCircle(cid);
		Intent intent = new Intent();
		intent.putExtra("flag", true);
		setResult(1, intent);
		finish();
		Utils.rightOut(this);

	}
}
