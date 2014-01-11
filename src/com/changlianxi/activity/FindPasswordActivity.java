package com.changlianxi.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.EditWather;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.Logger;
import com.changlianxi.util.MD5;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.MyViewGroup;
import com.umeng.analytics.MobclickAgent;

/**
 * 找回密码界面包含多个子view
 * 
 * @author teeker_bin
 * 
 */
public class FindPasswordActivity extends BaseActivity implements
		OnClickListener, PostCallBack {
	private MyViewGroup group;
	private LayoutInflater flater;
	private View find1, find2, find3;// 找回密码的三个界面
	private Button btnext;// 界面1的下一步按钮
	private Button btfinishYz;// 界面2的完成验证按钮
	private Button btfinish;// 界面3的完成按钮
	private LayoutParams params;
	private ImageView btback;
	private EditText ediNum;// 手机号输入框
	private EditText ediCode;// 验证码输入框
	private EditText ediPasswd;// 密码输入框
	private EditText editAgainPassword;// 再次输入密码
	private String uid;
	private String type = "";// 1 找回密码回调接口处理 2 验证码接口回调处理 3 设置密码接口回调处理4
								// 重新发送验证码回调处理
	private Dialog pd;
	private Button btGetCode;// 注册界面重新获取验证码按钮
	private int second = 60;// 用于重新获取验证码时间倒计时
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				btGetCode.setText("重新获取验证码  " + "(" + second + "s)");
				second--;
				if (second < 0) {
					btGetCode.setText("重新获取验证码  ");
					btGetCode.setEnabled(true);
					removeMessages(0);
					btGetCode.setTextColor(Color.WHITE);
					return;
				}
				this.sendEmptyMessageDelayed(0, 1000);
				break;
			default:
				break;
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_password);
		group = (MyViewGroup) findViewById(R.id.myGroup);
		flater = LayoutInflater.from(this);
		params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		find1 = flater.inflate(R.layout.find_word1, null);
		find2 = flater.inflate(R.layout.find_word2, null);
		find3 = flater.inflate(R.layout.find_word3, null);
		group.addView(find1, params);
		btback = (ImageView) findViewById(R.id.btback);
		btback.setOnClickListener(this);
		initFind1View();
		initFind2View();
		initFind3View();
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
	 * 初始化找回密码界面1的控件
	 */
	private void initFind1View() {
		btnext = (Button) find1.findViewById(R.id.btnext);
		btnext.setOnClickListener(this);
		ediNum = (EditText) find1.findViewById(R.id.editnum);
		ediNum.addTextChangedListener(new EditWather(ediNum));
		ediNum.setInputType(InputType.TYPE_CLASS_NUMBER);
	}

	/**
	 * 初始化找回密码界面2的控件
	 */
	private void initFind2View() {
		btfinishYz = (Button) find2.findViewById(R.id.btfinish_yz);
		btfinishYz.setOnClickListener(this);
		ediCode = (EditText) find2.findViewById(R.id.editCode);
		btGetCode = (Button) find2.findViewById(R.id.bt_get_code);
		btGetCode.setOnClickListener(this);
	}

	/**
	 * 初始化找回密码界面3的控件
	 */
	private void initFind3View() {
		btfinish = (Button) find3.findViewById(R.id.btfinish);
		btfinish.setOnClickListener(this);
		ediPasswd = (EditText) find3.findViewById(R.id.editPassword);
		editAgainPassword = (EditText) find3
				.findViewById(R.id.editAgainPassword);
	}

	@Override
	public void onClick(View v) {
		PostAsyncTask task = null;
		Map<String, Object> map = null;
		switch (v.getId()) {
		case R.id.btnext:
			// 找回密码之验证手机号是否存在
			map = new HashMap<String, Object>();
			String phoneNum = ediNum.getText().toString().replace("-", "");
			map.put("cellphone", phoneNum);
			map.put("version", "v1.0");
			map.put("tag",
					MD5.MD5_32(StringUtils.reverseSort(phoneNum) + "v1.0"
							+ phoneNum + "v1.0"));

			task = new PostAsyncTask(this, map, "/users/iretrievePassword");
			task.setTaskCallBack(this);
			task.execute();
			pd = DialogUtil.getWaitDialog(this, "请稍后");
			pd.show();
			type = "1";
			break;
		case R.id.btfinish_yz:
			// 找回密码之获取验证码
			map = new HashMap<String, Object>();
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("auth_code", ediCode.getText().toString());
			map.put("type", "retrievePasswd");
			task = new PostAsyncTask(this, map, "/users/iverifyAuthCode");
			task.setTaskCallBack(this);
			task.execute();
			pd = DialogUtil.getWaitDialog(this, "请稍后");
			pd.show();
			type = "2";
			break;
		case R.id.btback:
			finish();
			Utils.rightOut(this);

			break;
		case R.id.btfinish:
			// 找回密码之设置密码
			if (!ediPasswd.getText().toString()
					.equals(editAgainPassword.getText().toString())) {
				Utils.showToast("两次密码输入不一致");
				return;
			}
			map = new HashMap<String, Object>();
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("type", "retrievePasswd");
			map.put("cellphone", ediNum.getText().toString().replace("-", ""));
			map.put("passwd", ediPasswd.getText().toString());
			task = new PostAsyncTask(this, map, "/users/isetPasswd");
			task.setTaskCallBack(this);
			task.execute();
			pd = DialogUtil.getWaitDialog(this, "请稍后");
			pd.show();
			type = "3";
			break;
		case R.id.bt_get_code:
			second = 60;
			btGetCode.setTextColor(Color.GRAY);
			btGetCode.setEnabled(false);
			mHandler.sendEmptyMessage(0);
			map = new HashMap<String, Object>();
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("type", "retrievePasswd");
			map.put("cellphone", ediNum.getText().toString().replace("-", ""));
			task = new PostAsyncTask(this, map, "/users/isendAuthCode");
			task.setTaskCallBack(this);
			task.execute();
			pd = DialogUtil.getWaitDialog(this, "请稍后");
			pd.show();
			type = "4";
			break;
		default:
			break;
		}
	}

	/***
	 * 找回密码处理
	 * 
	 * @param result
	 */
	private void FindPassword(String result) {
		try {
			JSONObject object = new JSONObject(result);
			int rt = object.getInt("rt");
			if (rt == 1) {
				uid = object.getString("uid");
				SharedUtils.setString("uid", uid);
				group.setView(find2);
				btGetCode.setTextColor(Color.GRAY);
				btGetCode.setEnabled(false);
				mHandler.sendEmptyMessage(0);
//				Utils.hideSoftInput(this);

			} else {
				Utils.showToast("手机号码不存在");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断验证码是否正确
	 * 
	 * @param result
	 */
	private void CheckCode(String result) {
		try {
			JSONObject object = new JSONObject(result);
			int rt = object.getInt("rt");
			if (rt == 1) {
				group.setView(find3);
			} else {
				Utils.showToast("验证失败");
			}
		} catch (JSONException e) {
			Logger.error(this, e);

			e.printStackTrace();
		}
	}

	/**
	 * 判断密码是否设置成功
	 * 
	 * @param result
	 */
	private void SetPassword(String result) {
		try {
			JSONObject object = new JSONObject(result);
			int rt = object.getInt("rt");
			if (rt == 1) {
				finish();
				Utils.rightOut(this);

			} else {
				Utils.showToast("密码设置失败");
			}
		} catch (JSONException e) {
			Logger.error(this, e);

			e.printStackTrace();
		}
	}

	/**
	 * 重新获取验证码
	 * 
	 * @param result
	 */
	private void getAgainCode(String result) {
		try {
			JSONObject object = new JSONObject(result);
			int rt = object.getInt("rt");
			if (rt != 1) {
				String errCode = object.getString("err");
				Utils.showToast(ErrorCodeUtil.convertToChines(errCode));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 接口回调函数
	 */
	@Override
	public void taskFinish(String result) {
		pd.dismiss();
		if (type.equals("1")) {
			FindPassword(result);
		} else if (type.equals("2")) {
			CheckCode(result);
		} else if (type.equals("3")) {
			SetPassword(result);
		} else if (type.equals("4")) {
			getAgainCode(result);
		}

	}
}
