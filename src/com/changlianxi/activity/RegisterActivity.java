package com.changlianxi.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.Utils;
import com.changlianxi.view.MyViewGroup;

/**
 * ע�����������ע����view
 * 
 * @author teeker_bin
 * 
 */
public class RegisterActivity extends Activity implements OnClickListener,
		OnItemSelectedListener {
	private Spinner mSpinner;// ע������spinner
	private Spinner emailSpinner;// ����ע������spinner
	private LinearLayout layLogin;
	private MyViewGroup rGroup;
	private View reg1, reg2, reg3, emailReg1, emilReg2;// ע��1��2��3���������ע��1��2����
	private LayoutInflater flater;
	private LayoutParams params;
	private Button btnext;// ע��������һ����ť
	private Button btfinish_yz;// ע�����������֤��ť
	private TextView txtabout;// ע�����Ĺ�����ʾ����
	private Button btFinishRegister;// ע���������ע�ᰴť
	private EditText ediNum;// ע�����������ֻ���edittext
	private EditText ediPassword;// ע��������������edittext
	private int rt;// ���ؽ����־��1��ʾ��֤ͨ�����������û���¼�ɹ�������ֵ��ʾδ�ɹ�TODO
	private String uid = "";// �ɹ�����У������û�ID
	private String token = "";
	private Button emailBtNext;// ����ע�����һ����ť
	private String[] mItems;
	private ArrayAdapter<String> adapter;
	private EditText emailNum, emailEdit;// ����ע�������ֻ����������ؼ�
	private TextView emailTxt;// ����ע������email��ʾ�ؼ�
	private EditText code;// ע������������֤��edittext
	private EditText emailCode;// ����ע������������֤��edittext
	private Button emBtFinish;// ����ע�����������֤��ť
	private TextView txtQh;// ע�������ʾ���ŵ�textveiw����ʾ+86��
	private TextView emailTxtQh;// ����ע�������ʾ����
	private Button btGetCode;// ע��������»�ȡ��֤�밴ť
	private int second = 60;// �������»�ȡ��֤��ʱ�䵹��ʱ
	private TextView txtShowNum;// ע�������ʾ����ע����ֻ���
	private ProgressDialog progressDialog;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				btGetCode.setText("���»�ȡ��֤��  " + "(" + second + "s)");
				second--;
				if (second < 0) {
					btGetCode.setText("���»�ȡ��֤��  ");
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		initView();
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initView() {
		layLogin = (LinearLayout) findViewById(R.id.layLogin);
		layLogin.setOnClickListener(this);
		rGroup = (MyViewGroup) findViewById(R.id.regisGroup);
		params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		flater = LayoutInflater.from(this);
		reg1 = flater.inflate(R.layout.register1, null);
		reg2 = flater.inflate(R.layout.register2, null);
		reg3 = flater.inflate(R.layout.register3, null);
		emailReg1 = flater.inflate(R.layout.email_reg1, null);
		emilReg2 = flater.inflate(R.layout.email_reg2, null);
		rGroup.addView(reg1, params);
		// ��������Դ
		mItems = getResources().getStringArray(R.array.countrys);
		// ����Adapter���Ұ�����Դ
		adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, mItems);
		initReg1View();
		initReg2View();
		initReg3View();
		emailTxt = (TextView) emilReg2.findViewById(R.id.emailTxt);
		emailCode = (EditText) emilReg2.findViewById(R.id.emailEdiCode);
		emBtFinish = (Button) emilReg2.findViewById(R.id.email_btfinish);
		emBtFinish.setOnClickListener(this);
		emailTxtQh = (TextView) emailReg1.findViewById(R.id.emailtxtQH);
		emailBtNext = (Button) emailReg1.findViewById(R.id.emailbtnext);
		emailBtNext.setOnClickListener(this);
		emailNum = (EditText) emailReg1.findViewById(R.id.emailnum);
		emailEdit = (EditText) emailReg1.findViewById(R.id.emailEdit);
		emailNum.setInputType(InputType.TYPE_CLASS_NUMBER);
		// emailNum.addTextChangedListener(new EditWather(2));
		emailSpinner = (Spinner) emailReg1.findViewById(R.id.email_spinner);
		emailSpinner.setAdapter(adapter);
	}

	/**
	 * ��ʼ��ע�����1�Ŀؼ�
	 */
	private void initReg1View() {
		txtQh = (TextView) reg1.findViewById(R.id.txtQH);
		txtQh.setText("+86");
		btnext = (Button) reg1.findViewById(R.id.next);
		btnext.setOnClickListener(this);
		ediNum = (EditText) reg1.findViewById(R.id.num);
		ediNum.setInputType(InputType.TYPE_CLASS_NUMBER);
		ediNum.addTextChangedListener(new EditWather(1));
		mSpinner = (Spinner) reg1.findViewById(R.id.spinner1);
		// �� Adapter���ؼ�
		mSpinner.setAdapter(adapter);
		mSpinner.setOnItemSelectedListener(this);
	}

	/**
	 * ��ʼ��ע�����2�Ŀؼ�
	 */
	private void initReg2View() {
		btfinish_yz = (Button) reg2.findViewById(R.id.btfinish_yz);
		btfinish_yz.setOnClickListener(this);
		code = (EditText) reg2.findViewById(R.id.editCode);
		btGetCode = (Button) reg2.findViewById(R.id.bt_get_code);
		btGetCode.setEnabled(false);
		btGetCode.setOnClickListener(this);
		btGetCode.setTextColor(Color.GRAY);
		txtShowNum = (TextView) reg2.findViewById(R.id.txt_show_num);
	}

	/**
	 * ��ʼ��ע�����3�Ŀؼ�
	 */
	private void initReg3View() {
		btFinishRegister = (Button) reg3.findViewById(R.id.btfinish_register);
		btFinishRegister.setOnClickListener(this);
		txtabout = (TextView) reg3.findViewById(R.id.about);
		txtabout.setText(Html.fromHtml("<u>" + "����ע��ʱ������˽��˵��" + "</u>"));// �»���
		ediPassword = (EditText) reg3.findViewById(R.id.editPassword);
	}

	/**
	 * �ֻ����벹�ո�
	 * 
	 * @author teeker_bin
	 * 
	 */
	class EditWather implements TextWatcher {
		EditText text;

		public EditWather(int type) {
			if (type == 1) {
				text = ediNum;
			} else {

				text = emailNum;
			}
		}

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
					text.setText(sb.toString());
					text.setSelection(5);
				}
				if (s.length() == 9) {
					sb.insert(8, "-");
					text.setText(sb.toString());
					text.setSelection(10);
				}

			} else if (count == 0) {
				if (s.length() == 4) {
					text.setText(s.subSequence(0, s.length() - 1));
					text.setSelection(3);
				}
				if (s.length() == 9) {
					text.setText(s.subSequence(0, s.length() - 1));
					text.setSelection(8);
				}
			}
		}

	}

	/**
	 * �ؼ��ĵ���¼�
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layLogin:
			finish();
			overridePendingTransition(R.anim.slide_down_out,
					R.anim.register_down_out);
			break;
		case R.id.next:
			String txtNum = ediNum.getText().toString().replace("-", "");
			Logger.debug(this, "txt:" + txtNum);
			if (!Utils.isPhoneNum(txtNum)) {
				Utils.showToast("��������Ч���ֻ����룡");
				return;
			}
			new RegisterTask().execute(txtNum, "", "1");
			break;
		case R.id.btfinish_yz:
			new CheckCodeTask().execute(code.getText().toString());
			break;
		case R.id.btfinish_register:
			new SetPasswordTask().execute();
			break;
		case R.id.emailbtnext:
			String emtxt = emailEdit.getText().toString();
			if (!Utils.isEmail(emtxt)) {
				Utils.showToast("��������ȷ�������ַ��");
				return;
			}
			new RegisterTask().execute(emailNum.getText().toString(), emtxt,
					"2");

			break;
		case R.id.email_btfinish:
			new CheckCodeTask().execute(emailCode.getText().toString());
			break;
		case R.id.bt_get_code:
			second = 60;
			btGetCode.setTextColor(Color.GRAY);
			btGetCode.setEnabled(false);
			Message msg = new Message();
			msg.what = 0;
			mHandler.sendEmptyMessage(0);
			break;
		default:
			break;
		}
	}

	/**
	 * ע��
	 * 
	 */
	class RegisterTask extends AsyncTask<String, Integer, String> {
		String txtnum = "";
		String type = "1";// 1����ע�� 2 ����ע��
		String emailtxt;

		// �ɱ䳤�������������AsyncTask.exucute()��Ӧ
		@Override
		protected String doInBackground(String... params) {
			txtnum = params[0];
			emailtxt = params[1];

			type = params[2];
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cellphone", params[0]);
			map.put("email", params[1]);
			String result = HttpUrlHelper.postData(map, "/users/iregister");
			Logger.debug(this, "result:" + result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			try {
				JSONObject object = new JSONObject(result);
				rt = object.getInt("rt");
				if (rt == 1) {
					Message msg = new Message();
					msg.what = 0;
					mHandler.sendEmptyMessage(0);
					uid = object.getString("uid");
					if (type.equals("1")) {
						rGroup.setView(reg2);
						txtShowNum.setText(replaceStr(txtnum));
						return;
					}
					rGroup.setView(emilReg2);
					emailTxt.setText(emailtxt);

				} else {
					Utils.showToast("�ֻ������ѱ�ע�ᣬ������ֻ����룡");
				}
			} catch (JSONException e) {
				Logger.error(this, e);

				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// ����������������������ʾһ���Ի�������򵥴���
			progressDialog = new ProgressDialog(RegisterActivity.this);
			progressDialog.show();
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
			mapYz.put("uid", uid);
			mapYz.put("auth_code", params[0]);
			mapYz.put("type", "register");
			Logger.debug(this, "uid:" + uid + "  code:" + params[0]);
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
				rt = object.getInt("rt");
				if (rt == 1) {
					rGroup.setView(reg3);
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
			progressDialog = new ProgressDialog(RegisterActivity.this);
			progressDialog.show();
		}
	}

	/**
	 * ��������
	 * 
	 */
	class SetPasswordTask extends AsyncTask<String, Integer, String> {

		// �ɱ䳤�������������AsyncTask.exucute()��Ӧ
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("uid", uid);
			map1.put("passwd", ediPassword.getText().toString());
			String result = HttpUrlHelper.postData(map1, "/users/isetPasswd");
			Logger.debug(this, "result:" + result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			try {
				JSONObject object = new JSONObject(result);
				rt = object.getInt("rt");
				if (rt == 1) {
					token = object.getString("token");
					Logger.debug(this, "rt:" + rt + "  token:" + token);
					Intent intent = new Intent();
					intent.setClass(RegisterActivity.this,
							RegisterFinishActivity.class);
					startActivity(intent);
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
			progressDialog = new ProgressDialog(RegisterActivity.this);
			progressDialog.show();
		}
	}

	/**
	 * ���ֻ�������м���λ�滻Ϊ****
	 * 
	 * @param str
	 * @return
	 */
	private String replaceStr(String str) {
		StringBuffer sb = new StringBuffer(str);
		for (int i = 3; i < 7; i++) {
			sb.deleteCharAt(i);
			sb.insert(i, "*");
		}
		return sb.toString();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int posititon,
			long arg3) {
		if (posititon == 0) {
			return;
		}
		switch (posititon) {
		case 1:
			emailSpinner.setSelection(posititon);
			emailTxtQh.setText("+95");
			break;
		case 2:
			emailSpinner.setSelection(posititon);
			emailTxtQh.setText("+37");
			break;
		default:
			break;
		}
		rGroup.setView(emailReg1);

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}
