package com.changlianxi.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.R;

public class RegisterFinishActivity extends BaseActivity implements
		OnClickListener, PostCallBack {
	private Button btStartUse;
	private EditText editNC;
	private Dialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_finish);
		btStartUse = (Button) findViewById(R.id.startUse);
		btStartUse.setOnClickListener(this);
		editNC = (EditText) findViewById(R.id.editNC);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.startUse:
			String name = editNC.getText().toString();
			if (name.replace(" ", "").length() == 0) {
				Utils.showToast("姓名不能为空");
				return;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("f", "name");
			map.put("token", SharedUtils.getString("token", ""));
			map.put("v", name);
			PostAsyncTask task = new PostAsyncTask(this, map,
					"/users/isetUserInfo");
			task.setTaskCallBack(this);
			task.execute();
			progressDialog = DialogUtil.getWaitDialog(this, "请稍后");
			progressDialog.show();
			break;

		default:
			break;
		}
	}

	@Override
	public void taskFinish(String result) {
		progressDialog.dismiss();
		try {
			JSONObject object = new JSONObject(result);
			int rt = object.getInt("rt");
			if (rt == 1) {
				Intent intent = new Intent();
				intent.setClass(RegisterFinishActivity.this,
						LoginActivity.class);
				startActivity(intent);
				finish();
			} else {
				String err = object.getString("err");
				Utils.showToast(ErrorCodeUtil.convertToChines(err));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
