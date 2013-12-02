package com.changlianxi.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 意见反馈界面
 * 
 * @author teeker_bin
 * 
 */
public class AdviceFeedBackActivity extends Activity implements
		OnClickListener, PostCallBack {
	private ImageView back;
	private TextView titleTxt;
	private EditText editAdvice;
	private Button submit;
	private ProgressDialog pd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_advice_feed_back);
		findViewByID();
		setListener();
	}

	private void findViewByID() {
		back = (ImageView) findViewById(R.id.back);
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText("意见反馈");
		editAdvice = (EditText) findViewById(R.id.editAdvice);
		submit = (Button) findViewById(R.id.submit);

	}

	private void setListener() {
		submit.setOnClickListener(this);
		back.setOnClickListener(this);

	}

	/**
	 * 提交建议
	 */
	private void PostSubmit() {
		String strAdvice = editAdvice.getText().toString();
		if (strAdvice.length() == 0) {
			Utils.showToast("输入意见不能为空");
			return;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("content", strAdvice);
		map.put("device", Utils.getOS());
		PostAsyncTask task = new PostAsyncTask(this, map, "/feedbacks/icommit");
		task.setTaskCallBack(this);
		task.execute();
		pd = new ProgressDialog(this);
		pd.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.submit:
			PostSubmit();
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
				Utils.showToast("提交成功");
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
