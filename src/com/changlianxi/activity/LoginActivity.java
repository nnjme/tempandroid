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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.changlianxi.inteface.PushOnBind;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.task.SetClientInfo;
import com.changlianxi.task.SetClientInfo.ClientCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.EditWather;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.Logger;
import com.changlianxi.util.PushMessageReceiver;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;

/**
 * ��¼����
 * 
 * @author teeker_bin
 * 
 */
public class LoginActivity extends Activity implements OnClickListener,
		PushOnBind, PostCallBack {
	private RelativeLayout btReg;// ȥ��ע����水ť
	private Button btLogin;// ��¼��ť
	private EditText ediNum;// �ֻ����������
	private EditText ediPassword;// ���������
	private String uid = "";// �ɹ�����У������û�ID
	private String token = "";
	private TextView btFindWd;// �һ����밴ť
	private Dialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		uid = SharedUtils.getString("uid", "");
		token = SharedUtils.getString("token", "");
		if (!uid.equals("") && !token.equals("")) {
			Intent intent = new Intent();
			intent.setClass(this, MainActivity.class);
			startActivity(intent);
			finish();
		}
		initView();
		PushMessageReceiver.setPushOnBind(this);
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initView() {
		btFindWd = (TextView) findViewById(R.id.findpd);
		btFindWd.setOnClickListener(this);
		btReg = (RelativeLayout) findViewById(R.id.btregister);
		btReg.setOnClickListener(this);
		btLogin = (Button) findViewById(R.id.btlogin);
		btLogin.setOnClickListener(this);
		ediNum = (EditText) findViewById(R.id.edtNum);
		ediNum.setInputType(InputType.TYPE_CLASS_NUMBER);
		ediNum.addTextChangedListener(new EditWather(ediNum));
		ediPassword = (EditText) findViewById(R.id.edtPassword);
	}

	/**
	 * ��¼ʱ�ж��û����Ƿ����
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
				return true;
			} else {
				String errorCoce = object.getString("err");
				Utils.showToast(ErrorCodeUtil.convertToChines(errorCoce));
				return false;
			}
		} catch (JSONException e) {
			Logger.error(this, e);
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * �ؼ��ĵ���¼�
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btregister:
			Intent intent = new Intent();
			intent.setClass(this, RegisterActivity.class);
			startActivity(intent);
			// activity�л�������ʾ һ��Ҫ�ŵ�startactivity ��finish()֮��
			overridePendingTransition(R.anim.register_up_in,
					R.anim.slide_down_out);
			break;
		case R.id.btlogin:
			if (!Utils.isNetworkAvailable()) {
				Utils.showToast("��������");
				return;
			}
			String num = ediNum.getText().toString().replace("-", "");
			if (!Utils.isPhoneNum(num)) {
				Utils.showToast("��������Ч���ֻ����룡");
				return;
			}
			if (ediPassword.getText().toString().length() == 0) {
				Utils.showToast("���������룡");
				return;
			}
			// ��apikey�ķ�ʽ��¼��һ�������Activity��onCreate��
			PushManager.startWork(this, PushConstants.LOGIN_TYPE_API_KEY,
					Utils.getMetaValue(this, "api_key"));
			dialog = DialogUtil.getWaitDialog(this, "��½��");
			dialog.show();
			break;
		case R.id.findpd:
			Intent find = new Intent();
			find.setClass(this, FindPasswordActivity.class);
			startActivity(find);
			break;
		default:
			break;
		}

	}

	/**
	 * �ٶ����Ͱ󶨻ص�
	 */
	@Override
	public void onBind(int errorCode, String content) {
		if (errorCode == 0) {// 0 ��ʾ�ɹ� ��0ʧ��
			String channelid = "";
			String userid = "";
			try {
				JSONObject jsonContent = new JSONObject(content);
				JSONObject params = jsonContent
						.getJSONObject("response_params");
				channelid = params.getString("channel_id");
				userid = params.getString("user_id");
				SharedUtils.setChannelID(channelid);
				SharedUtils.setUserID(userid);
				login();
			} catch (JSONException e) {
			}
		} else {
			dialog.dismiss();
			Utils.showToast("���ͷ���󶨴���" + errorCode);
		}
	}

	/**
	 * �����Ƿ����óɹ�
	 */
	private void isSetSuccess(String result) {
		try {
			JSONObject json = new JSONObject(result);
			int rt = json.getInt("rt");
			if (rt == 1) {
				Intent it = new Intent();
				it.setClass(LoginActivity.this, MainActivity.class);
				startActivity(it);
				finish();
			} else {
				String err = json.getString("err");
				String errorString = ErrorCodeUtil.convertToChines(err);
				Utils.showToast(errorString);
				dialog.dismiss();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * ��¼
	 */
	private void login() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cellphone", ediNum.getText().toString().replace("-", ""));
		map.put("passwd", ediPassword.getText().toString());
		PostAsyncTask task = new PostAsyncTask(this, map, "/users/ilogin");
		task.setTaskCallBack(this);
		task.execute();
	}

	/**
	 * ��¼�ӿڴ���ص�
	 */
	@Override
	public void taskFinish(String result) {
		if (!isUserExist(result)) {
			if (dialog != null) {
				dialog.dismiss();
			}
			return;
		}
		SetClientInfo task = new SetClientInfo(new ClientCallBack() {// �������Խӿڻص�
					public void afterLogin(String result) {
						isSetSuccess(result);
					}
				});
		task.execute();
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
