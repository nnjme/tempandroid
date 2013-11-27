package com.changlianxi.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;

public class RegisterFinishActivity extends Activity implements
		OnClickListener, PostCallBack {
	private Button btStartUse;
	private EditText editNC;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register_finish);
		btStartUse = (Button) findViewById(R.id.startUse);
		btStartUse.setOnClickListener(this);
		editNC = (EditText) findViewById(R.id.editNC);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.startUse:
			// new SetNickNameTask().execute();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("f", "name");
			map.put("token", SharedUtils.getString("token", ""));
			map.put("v", editNC.getText().toString());
			PostAsyncTask task = new PostAsyncTask(this, map,
					"/users/isetUserInfo");
			task.setTaskCallBack(this);
			task.execute();
			progressDialog = new ProgressDialog(this);
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
				Utils.showToast("Í«≥∆…Ë÷√ ß∞‹");
			}
		} catch (JSONException e) {
			Logger.error(this, e);

			e.printStackTrace();
		}
	}
}
//