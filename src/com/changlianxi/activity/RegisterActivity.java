package com.changlianxi.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.popwindow.ListViewPopwindow;
import com.changlianxi.popwindow.ListViewPopwindow.OnlistOnclick;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.EditWather;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.MD5;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.MyViewGroup;

/**
 * ע�����������ע����view
 * 
 * @author teeker_bin
 * 
 */
public class RegisterActivity extends BaseActivity implements OnClickListener,
		PostCallBack {
	private EditText spinner;
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
	private EditText emailNum, emailEdit;// ����ע�������ֻ����������ؼ�
	private TextView emailTxt;// ����ע������email��ʾ�ؼ�
	private EditText code;// ע������������֤��edittext
	private Button emBtFinish;// ����ע�����������֤��ť
	private TextView txtQh;// ע�������ʾ���ŵ�textveiw����ʾ+86��
	private Button btGetCode;// ע��������»�ȡ��֤�밴ť
	private int second = 60;// �������»�ȡ��֤��ʱ�䵹��ʱ
	private TextView txtShowNum;// ע�������ʾ����ע����ֻ���
	private Dialog progressDialog;
	private String type = "";// 1 ��֤�봦�� 2 �������봦��3 ���»�ȡ��֤�봦��
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
		setContentView(R.layout.activity_register);
		initView();
		getWindow()
				.setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
								| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
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
		initReg1View();
		initReg2View();
		initReg3View();
		emailTxt = (TextView) emilReg2.findViewById(R.id.emailTxt);
		emBtFinish = (Button) emilReg2.findViewById(R.id.email_btfinish);
		emBtFinish.setOnClickListener(this);
		emailBtNext = (Button) emailReg1.findViewById(R.id.emailbtnext);
		emailBtNext.setOnClickListener(this);
		emailNum = (EditText) emailReg1.findViewById(R.id.emailnum);
		emailEdit = (EditText) emailReg1.findViewById(R.id.emailEdit);
		emailNum.setInputType(InputType.TYPE_CLASS_NUMBER);
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
		ediNum.addTextChangedListener(new EditWather(ediNum));
		spinner = (EditText) reg1.findViewById(R.id.spinner);
		spinner.setOnClickListener(this);

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
	 * �ؼ��ĵ���¼�
	 */
	@Override
	public void onClick(View v) {
		PostAsyncTask task = null;
		Map<String, Object> map = null;
		switch (v.getId()) {
		case R.id.layLogin:
			finish();
			overridePendingTransition(R.anim.slide_down_out,
					R.anim.register_down_out);
			break;
		case R.id.next:
			String txtNum = ediNum.getText().toString().replace("-", "");
			if (!Utils.isPhoneNum(txtNum)) {
				Utils.showToast("��������Ч���ֻ����룡");
				return;
			}
			new RegisterTask().execute(txtNum, "", "1");
			break;
		case R.id.btfinish_yz:
			map = new HashMap<String, Object>();
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("auth_code", code.getText().toString());
			map.put("type", "register");
			task = new PostAsyncTask(this, map, "/users/iverifyAuthCode");
			task.setTaskCallBack(this);
			task.execute();
			progressDialog = DialogUtil.getWaitDialog(this, "���Ժ�");
			progressDialog.show();
			type = "1";
			break;
		case R.id.btfinish_register:
			map = new HashMap<String, Object>();
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("uid", uid);
			map.put("type", "register");
			map.put("cellphone", ediNum.getText().toString().replace("-", ""));
			map.put("passwd", ediPassword.getText().toString());
			task = new PostAsyncTask(this, map, "/users/isetPasswd");
			task.setTaskCallBack(this);
			task.execute();
			progressDialog = DialogUtil.getWaitDialog(this, "���Ժ�");
			progressDialog.show();
			type = "2";
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
			break;
		case R.id.bt_get_code:
			second = 60;
			btGetCode.setTextColor(Color.GRAY);
			btGetCode.setEnabled(false);
			mHandler.sendEmptyMessage(0);
			map = new HashMap<String, Object>();
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("type", "register");
			map.put("cellphone", ediNum.getText().toString().replace("-", ""));
			task = new PostAsyncTask(this, map, "/users/isendAuthCode");
			task.setTaskCallBack(this);
			task.execute();
			progressDialog = DialogUtil.getWaitDialog(this, "���Ժ�");
			progressDialog.show();
			type = "3";
			break;
		case R.id.spinner:
			ListViewPopwindow pop = new ListViewPopwindow(this, spinner,
					getResources().getStringArray(R.array.countrys));
			pop.show();
			pop.setOnlistOnclick(new OnlistOnclick() {
				@Override
				public void onclick(String str) {
					spinner.setText(str);
				}
			});
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
			map.put("cellphone", txtnum);
			map.put("email", emailtxt);
			map.put("version", "v1.0");
			map.put("tag",
					MD5.MD5_32(StringUtils.reverseSort(txtnum) + "v1.0"
							+ txtnum + "v1.0"));
			String result = HttpUrlHelper.postData(map, "/users/iregister");
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
					SharedUtils.setString("uid", uid);
					if (type.equals("1")) {
						rGroup.setView(reg2);
						txtShowNum.setText(txtnum);
						return;
					}
					rGroup.setView(emilReg2);
					emailTxt.setText(emailtxt);

				} else {
					String errorCoce = object.getString("err");
					Utils.showToast(ErrorCodeUtil.convertToChines(errorCoce));
				}
			} catch (JSONException e) {
				Logger.error(this, e);

				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// ����������������������ʾһ���Ի�������򵥴���
			progressDialog = DialogUtil.getWaitDialog(RegisterActivity.this,
					"���Ժ�");
			progressDialog.show();
		}
	}

	/**
	 * �ж���֤���Ƿ���ȷ
	 * 
	 * @param result
	 */
	private void CheckCode(String result) {
		try {
			JSONObject object = new JSONObject(result);
			rt = object.getInt("rt");
			if (rt == 1) {
				rGroup.setView(reg3);
			} else {
				String errorCoce = object.getString("err");
				Utils.showToast(ErrorCodeUtil.convertToChines(errorCoce));
			}
		} catch (JSONException e) {
			Logger.error(this, e);

			e.printStackTrace();
		}
	}

	/**
	 * �ж������Ƿ����óɹ�
	 * 
	 * @param result
	 */
	private void SetPassword(String result) {
		try {
			JSONObject object = new JSONObject(result);
			rt = object.getInt("rt");
			if (rt == 1) {
				token = object.getString("token");
				uid = object.getString("uid");
				SharedUtils.setString("token", token);
				SharedUtils.setString("uid", uid);
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this,
						RegisterFinishActivity.class);
				startActivity(intent);
				finish();
			} else {
				String errorCoce = object.getString("err");
				Utils.showToast(ErrorCodeUtil.convertToChines(errorCoce));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���»�ȡ��֤��
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

	@Override
	public void taskFinish(String result) {
		progressDialog.dismiss();
		if (type.equals("1")) {
			CheckCode(result);
		} else if (type.equals("2")) {
			SetPassword(result);
		} else if (type.equals("3")) {
			getAgainCode(result);
		}
	}
}
