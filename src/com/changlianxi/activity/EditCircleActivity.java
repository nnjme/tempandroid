package com.changlianxi.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
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

import com.changlianxi.inteface.UpLoadPic;
import com.changlianxi.modle.CircleIdetailModle;
import com.changlianxi.modle.SelectPicModle;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.task.GetCircleIdetailTask;
import com.changlianxi.task.GetCircleIdetailTask.GetCircleIdetail;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.task.UpLoadPicAsyncTask;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.Home;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

/**
 * �༭Ȧ�ӽ���
 * 
 * @author teeker_bin
 * 
 */
public class EditCircleActivity extends BaseActivity implements
		OnClickListener, GetCircleIdetail, PostCallBack {
	// private LinearLayout addRoles;
	// private LinearLayout layRoles;
	// private int count;// ���ְ������
	// private JSONArray jsonAry = new JSONArray();
	// private JSONObject jsonObj;
	private Button btnSave;
	private EditText circleName;// Ȧ������
	private TextView titleName;
	private EditText circleDescription;// Ȧ������
	private ImageView circleLogo;
	private String cid;
	private Dialog pd;
	private String logoPath = "";
	private ImageView back;
	private SelectPicPopwindow pop;
	// private List<CircleRoles> rolesModle = null;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private Bitmap cirBmp = null;

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
	/**����ҳ��ͳ��
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
		if (!Utils.isNetworkAvailable()) {
			Utils.showToast("��������");
			return;
		}
		pd = DialogUtil.getWaitDialog(this, "���Ժ�");
		GetCircleIdetailTask task = new GetCircleIdetailTask(cid);
		task.setTaskCallBack(this);
		task.execute();
		pd.show();
	}

	private void findViewByID() {
		// addRoles = (LinearLayout) findViewById(R.id.addroles);
		// layRoles = (LinearLayout) findViewById(R.id.layRoles);
		btnSave = (Button) findViewById(R.id.btnsave);
		titleName = (TextView) findViewById(R.id.titleName);
		circleName = (EditText) findViewById(R.id.circleName);
		circleDescription = (EditText) findViewById(R.id.circleDis);
		circleLogo = (ImageView) findViewById(R.id.circleLogo);
		back = (ImageView) findViewById(R.id.back);

	}

	private void setListener() {
		// addRoles.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		circleLogo.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	// /**
	// * ���ְ��
	// */
	// private void addView(String roleName, String tag) {
	// count++;
	// View view = LayoutInflater.from(this).inflate(
	// R.layout.circle_roles_layout, null);
	// // layRoles.addView(view);
	// TextView txt = (TextView) view.findViewById(R.id.textView1);
	// EditText edit = (EditText) view.findViewById(R.id.roleName);
	// edit.setTag(tag);
	// edit.setText(roleName);
	// if (count == 1) {
	// txt.setVisibility(View.VISIBLE);
	// } else {
	// txt.setVisibility(View.INVISIBLE);
	// }
	// }

	// /**
	// * ����ְ��json��
	// *
	// * @param name
	// * ְ������
	// */
	// private void BuildAddJson(String name) {
	// try {
	// jsonObj = new JSONObject();
	// jsonObj.put("name", name);
	// jsonObj.put("op", "new");
	// jsonAry.put(jsonObj);
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// /**
	// * �༭ְ��json��
	// *
	// * @param name
	// * ְ������
	// */
	// private void BuildEditJson(String name, String id) {
	// try {
	// jsonObj = new JSONObject();
	// jsonObj.put("name", name);
	// jsonObj.put("id", id);
	// jsonObj.put("op", "edit");
	// jsonAry.put(jsonObj);
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }

	// /**
	// * ��ȡְ������
	// */
	// private String getValue() {
	// int newposition = 0;
	// if (rolesModle != null && rolesModle.size() > 0) {// �ж��Ƿ�༭
	// newposition = rolesModle.size();
	// for (int i = 0; i < rolesModle.size(); i++) {
	// EditText t = (EditText) layRoles.getChildAt(i).findViewById(
	// R.id.roleName);
	// String txt = t.getText().toString();
	// if (!txt.equals(rolesModle.get(i).getRoleName())) {
	// BuildEditJson(txt, rolesModle.get(i).getRoleId());
	// }
	// }
	// }
	// // ����
	// for (int i = newposition; i < layRoles.getChildCount(); i++) {
	// EditText t = (EditText) layRoles.getChildAt(i).findViewById(
	// R.id.roleName);
	// BuildAddJson(t.getText().toString());
	// }
	// return jsonAry.toString();
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// case R.id.addroles:
		// addView("", "new");
		// break;
		case R.id.btnsave:
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
		// rolesModle = modle.getRolesModle();
		// for (int i = 0; i < rolesModle.size(); i++) {
		// addView(rolesModle.get(i).getRoleName(), "");
		// }
	}

	/**
	 * �����޸���Ϣ
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
		// map.put("roles", getValue());
		PostAsyncTask task = new PostAsyncTask(this, map, "/circles/iedit");
		task.setTaskCallBack(this);
		task.execute();
		pd = DialogUtil.getWaitDialog(this, "���Ժ�");
		pd.show();

	}

	/**
	 * �ϴ�Ȧ��logo
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
					Utils.showToast("�޸ĳɹ�");
					Home.refreshCircleList(null);
					exitSuccess();

				} else {
					Utils.showToast("�޸�ʧ��");
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
		}// ����ͼƬ
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
			if (logoPath.equals("")) {
				pd.dismiss();
				Utils.showToast("�޸ĳɹ�");
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
