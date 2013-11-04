package com.changlianxi.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.MyViewGroup;

/**
 * �һ����������������view
 * 
 * @author teeker_bin
 * 
 */
public class FindPasswordActivity extends Activity implements OnClickListener {
	private MyViewGroup group;
	private LayoutInflater flater;
	private View find1, find2, find3;// �һ��������������
	private Button btnext;// ����1����һ����ť
	private Button btfinishYz;// ����2�������֤��ť
	private Button btfinish;// ����3����ɰ�ť
	private LayoutParams params;
	private ImageView btback;
	private EditText ediNum;// �ֻ��������
	private EditText ediCode;// ��֤�������
	private EditText ediPasswd;// ���������
	private ProgressDialog progressDialog;
	private String uid;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
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

	/**
	 * ��ʼ���һ��������1�Ŀؼ�
	 */
	private void initFind1View() {
		btnext = (Button) find1.findViewById(R.id.btnext);
		btnext.setOnClickListener(this);
		ediNum = (EditText) find1.findViewById(R.id.editnum);
		ediNum.addTextChangedListener(new EditWather());
		ediNum.setInputType(InputType.TYPE_CLASS_NUMBER);
	}

	/**
	 * ��ʼ���һ��������2�Ŀؼ�
	 */
	private void initFind2View() {
		btfinishYz = (Button) find2.findViewById(R.id.btfinish_yz);
		btfinishYz.setOnClickListener(this);
		ediCode = (EditText) find2.findViewById(R.id.editCode);
	}

	/**
	 * ��ʼ���һ��������3�Ŀؼ�
	 */
	private void initFind3View() {
		btfinish = (Button) find3.findViewById(R.id.btfinish);
		btfinish.setOnClickListener(this);
		ediPasswd = (EditText) find3.findViewById(R.id.editPassword);
	}

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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnext:
			new FindPasswordTask().execute();
			break;
		case R.id.btfinish_yz:
			new CheckCodeTask().execute();
			break;
		case R.id.btback:
			finish();
			break;
		case R.id.btfinish:
			new SetPasswordTask().execute();
			break;
		default:
			break;
		}
	}

	/**
	 * У����֤���Ƿ���ȷ
	 * 
	 */
	class CheckCodeTask extends AsyncTask<String, Integer, String> {

		// �ɱ䳤�������������AsyncTask.exucute()��Ӧ
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> mapYz = new HashMap<String, Object>();
			mapYz.put("uid", SharedUtils.getString("uid", ""));
			mapYz.put("auth_code", ediCode.getText().toString());
			mapYz.put("type", "retrievePasswd");
			String result = HttpUrlHelper.postData(mapYz,
					"/users/iverifyAuthCode");
			Logger.debug(this, "result:" + result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			try {
				JSONObject object = new JSONObject(result);
				int rt = object.getInt("rt");
				if (rt == 1) {
					group.setView(find3);
				} else {
					Utils.showToast("��֤ʧ��");
				}
			} catch (JSONException e) {
				Logger.error(this, e);

				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// ����������������������ʾһ���Ի�������򵥴���
			progressDialog = new ProgressDialog(FindPasswordActivity.this);
			progressDialog.show();
		}
	}

	/**
	 * �һ�����ӿ�
	 * 
	 */
	class FindPasswordTask extends AsyncTask<String, Integer, String> {

		// �ɱ䳤�������������AsyncTask.exucute()��Ӧ
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cellphone", ediNum.getText().toString().replace("-", ""));
			Logger.debug(this, "cellphone:"
					+ ediNum.getText().toString().replace("-", ""));
			String result = HttpUrlHelper.postData(map,
					"/users/iretrievePassword");
			Logger.debug(this, "result:" + result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			try {
				JSONObject object = new JSONObject(result);
				int rt = object.getInt("rt");
				if (rt == 1) {
					uid = object.getString("uid");
					SharedUtils.setString("uid", uid);
					group.setView(find2);

				} else {
					Utils.showToast("�ֻ����벻����");
				}
			} catch (JSONException e) {
				Logger.error(this, e);

				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// ����������������������ʾһ���Ի�������򵥴���
			progressDialog = new ProgressDialog(FindPasswordActivity.this);
			progressDialog.show();
		}
	}

	/**
	 * ��������ӿ�
	 * 
	 */
	class SetPasswordTask extends AsyncTask<String, Integer, String> {

		// �ɱ䳤�������������AsyncTask.exucute()��Ӧ
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> mapPwd = new HashMap<String, Object>();
			mapPwd.put("uid", SharedUtils.getString("uid", ""));
			mapPwd.put("passwd", ediPasswd.getText().toString());
			String result = HttpUrlHelper.postData(mapPwd, "/users/isetPasswd");
			Logger.debug(this, "result:" + result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			try {
				JSONObject object = new JSONObject(result);
				int rt = object.getInt("rt");
				if (rt == 1) {
					finish();
				} else {
					Utils.showToast("��������ʧ��");
				}
			} catch (JSONException e) {
				Logger.error(this, e);

				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// ����������������������ʾһ���Ի�������򵥴���
			progressDialog = new ProgressDialog(FindPasswordActivity.this);
			progressDialog.show();
		}
	}
}
