package com.changlianxi.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.changlianxi.R;
import com.changlianxi.fragment.MainActivity1;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.EditWather;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

/**
 * 登录界面
 *             
 * @author teeker_bin
 * 
 */
public class LoginActivity extends Activity implements OnClickListener,
		PostCallBack {
	private Button btReg;// 去往注册界面按钮
	private Button btLogin;// 登录按钮
	private EditText ediNum;// 手机号码输入框
	private EditText ediPassword;// 密码输入框
	private String uid = "";// 成功后才有，代表用户ID
	private String token = "";
	private Button btFindWd;// 找回密码按钮
	private Dialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
<<<<<<< HEAD
		CLXApplication.addActivity(this);
=======
		MobclickAgent.openActivityDurationTrack(false);
>>>>>>> 729aaf0cdd6612eadceb0fb2558c3d358c778b85
		uid = SharedUtils.getString("uid", "");
		token = SharedUtils.getString("token", "");
		if (!uid.equals("") && !token.equals("")) {
			Intent intent = new Intent();
			intent.setClass(this, MainActivity1.class);
			startActivity(intent);
			finish();
		}
		initView();
<<<<<<< HEAD
		SharedUtils.setString("imei", Utils.getImei(this));
=======
		PushMessageReceiver.setPushOnBind(this);
		SharedUtils.setInt("imei", 1);
	}

	/**
	 * 数据统计
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getClass().getName() + "");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getName() + "");
		MobclickAgent.onPause(this);
>>>>>>> 729aaf0cdd6612eadceb0fb2558c3d358c778b85
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		btFindWd = (Button) findViewById(R.id.findpd);
		btFindWd.setOnClickListener(this);
		btReg = (Button) findViewById(R.id.btregister);
		btReg.setOnClickListener(this);
		btLogin = (Button) findViewById(R.id.btlogin);
		btLogin.setOnClickListener(this);
		ediNum = (EditText) findViewById(R.id.edtNum);
		ediNum.setInputType(InputType.TYPE_CLASS_NUMBER);
		ediNum.addTextChangedListener(new EditWather(ediNum));
		ediPassword = (EditText) findViewById(R.id.edtPassword);// 取得InputMethodRelativeLayout组件
		// 设置监听事件
		// layout.setOnSizeChangedListenner(this);
	}

	/**
	 * 登录时判断用户名是否存在
	 * 
	 * @param str
	 * @return
	 */
	private boolean isUserExist(String str) {
		try {
			JSONObject object = new JSONObject(str);
			int rt = object.getInt("rt");
			if (rt == 1) {
				token = object.getString("token");
				uid = object.getString("uid");
				SharedUtils.setString("uid", uid);
				SharedUtils.setString("token", token);
				Intent it = new Intent();
				it.setClass(LoginActivity.this, MainActivity1.class);
				startActivity(it);
				finish();
				return true;
			} else {
				dialog.dismiss();
				String errorCoce = object.getString("err");
				Utils.showToast(ErrorCodeUtil.convertToChines(errorCoce));
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * 控件的点击事件
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btregister:
			Intent intent = new Intent();
			intent.setClass(this, RegisterActivity.class);
			startActivity(intent);
			Utils.leftOutRightIn(this);
			break;
		case R.id.btlogin:
			if (!Utils.isNetworkAvailable()) {
				Utils.showToast("请检查网络");
				return;
			}
			String num = ediNum.getText().toString().replace("-", "");
			if (!Utils.isPhoneNum(num)) {
				Utils.showToast("请输入有效的手机号码！");
				return;
			}
			if (ediPassword.getText().toString().length() == 0) {
				Utils.showToast("请输入密码！");
				return;
			}
			login(num);
			dialog = DialogUtil.getWaitDialog(this, "登陆中");
			dialog.show();
			break;
		case R.id.findpd:
			Intent find = new Intent();
			find.setClass(this, FindPasswordActivity.class);
			startActivity(find);
			Utils.leftOutRightIn(this);
			break;
		default:
			break;
		}

	}

	/**
	 * 登录
	 */
	private void login(String num) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cellphone", num);
		map.put("passwd", ediPassword.getText().toString());
		PostAsyncTask task = new PostAsyncTask(this, map, "/users/ilogin");
		task.setTaskCallBack(this);
		task.execute();
	}

	/**
	 * 登录接口处理回调
	 */
	@Override
	public void taskFinish(String result) {
		isUserExist(result);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);

	}

	@Override
	protected void onDestroy() {
		if (dialog != null) {
			dialog.dismiss();
		}
		super.onDestroy();
	}
}
