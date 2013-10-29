package com.changlianxi.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
 

/**
 * ��¼����
 * 
 * @author teeker_bin
 * 
 */
public class LoginActivity extends Activity implements OnClickListener {
	private LinearLayout btReg;// ȥ��ע����水ť
	private Button btLogin;// ��¼��ť
	private EditText ediNum;// �ֻ����������
	private EditText ediPassword;// ���������
	private String uid = "";// �ɹ�����У������û�ID
	private String token = "";
	private TextView btFindWd;// �һ����밴ť
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		initView();
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initView() {
		btFindWd = (TextView) findViewById(R.id.findpd);
		btFindWd.setOnClickListener(this);
		btReg = (LinearLayout) findViewById(R.id.btregister);
		btReg.setOnClickListener(this);
		btLogin = (Button) findViewById(R.id.btlogin);
		btLogin.setOnClickListener(this);
		ediNum = (EditText) findViewById(R.id.edtNum);
		ediNum.setInputType(InputType.TYPE_CLASS_NUMBER);
		ediNum.addTextChangedListener(new EditWather());
		ediPassword = (EditText) findViewById(R.id.edtPassword);
	}

	/**
	 * �ֻ�����ֶ���ʾ����
	 * 
	 * @author teeker_bin
	 * 
	 */
	class EditWather implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			StringBuffer sb = new StringBuffer(s);
			if (count == 1) {
				if (s.length() == 4) {
					sb.insert(3, "-");
					ediNum.setText(sb.toString());
					ediNum.setSelection(5);
				}
				if (s.length() == 9) {
					sb.insert(8, "-");
					ediNum.setText(sb.toString());
					ediNum.setSelection(10);
				}
				// if (s.length() == 11) {
				// ediNum.setText(s + " ");
				// ediNum.setSelection(11);
				// }

			} else if (count == 0) {
				if (s.length() == 4) {
					ediNum.setText(s.subSequence(0, s.length() - 1));
					ediNum.setSelection(3);
				}
				if (s.length() == 9) {
					ediNum.setText(s.subSequence(0, s.length() - 1));
					ediNum.setSelection(8);
				}
				// if (s.length() == 11) {
				// ediNum.setText(s.subSequence(0, s.length() - 1));
				// ediNum.setSelection(10);
				// }
			}
		}
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
				Logger.debug(this, "rt:" + rt + "  uid:" + uid + "  token:"
						+ token);
				return true;
			} else {
				Utils.showToast("�û���������");
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
			String num = ediNum.getText().toString().replace("-", "");
			if (!Utils.isPhoneNum(num)) {
				Utils.showToast("��������Ч���ֻ����룡");
				return;
			}
			if (ediPassword.getText().toString().length() == 0) {
				Utils.showToast("���������룡");
				return;
			}
			new LoginTask().execute(num);
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
	 * �첽�ύ�޸����ݵ�������
	 * 
	 */
	class LoginTask extends AsyncTask<String, Integer, String> {
		// �ɱ䳤�������������AsyncTask.exucute()��Ӧ
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cellphone", params[0]);
			map.put("passwd", ediPassword.getText().toString());
			String result = HttpUrlHelper.postData(map, "/users/ilogin");
			Logger.debug(this, "LoginResult:" + result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			if (!isUserExist(result)) {
				return;
			}
			SharedUtils.setString("uid", uid);
			SharedUtils.setString("token", token);
			Utils.uid = uid;
			Intent it = new Intent();
			it.setClass(LoginActivity.this, MainActivity.class);
			startActivity(it);
			finish();
		}

		@Override
		protected void onPreExecute() {
			// ����������������������ʾһ���Ի�������򵥴���
			progressDialog = new ProgressDialog(LoginActivity.this);
			progressDialog.show();
		}
	}
}
