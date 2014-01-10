package com.changlianxi.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;

/**
 * 修改密码
 * 
 * @author teeker_bin
 * 
 */
public class ChangePassswordActivity extends BaseActivity implements
		OnClickListener, PostCallBack {
	private Button ok;
	private EditText nowPasswrod;
	private EditText newPassword;
	private TextView titleTxt;
	private ImageView back;
	private Dialog pd;

	// startActivity
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_passsword);
		findViewByID();
		setListener();
	}

	private void findViewByID() {
		ok = (Button) findViewById(R.id.ok);
		nowPasswrod = (EditText) findViewById(R.id.nowPassword);
		newPassword = (EditText) findViewById(R.id.newPassword);
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText("修改密码");
		back = (ImageView) findViewById(R.id.back);
	}

	private void setListener() {
		ok.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	private void changePassword() {
		String oldpswd = nowPasswrod.getText().toString();
		String newpswd = newPassword.getText().toString();
		if (oldpswd.length() == 0) {
			Utils.showToast("输入当前密码");
			return;
		}
		if (newpswd.length() == 0) {
			Utils.showToast("输入新密码");
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("old", oldpswd);
		map.put("new", newpswd);
		PostAsyncTask task = new PostAsyncTask(this, map,
				"/users/ichangePasswd");
		task.setTaskCallBack(this);
		task.execute();
		pd = DialogUtil.getWaitDialog(this, "请稍后");
		pd.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			Utils.rightOut(this);

			break;
		case R.id.ok:
			changePassword();
			break;
		default:
			break;
		}

	}

	@Override
	public void taskFinish(String result) {
		pd.dismiss();
		try {
			JSONObject object = new JSONObject(result);
			int rt = object.getInt("rt");
			if (rt == 1) {
				Utils.showToast("密码修改成功");
				finish();
				Utils.rightOut(this);

			} else {
				String err = object.getString("err");
				Utils.showToast(ErrorCodeUtil.convertToChines(err));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
