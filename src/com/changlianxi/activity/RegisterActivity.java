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
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.popwindow.ListViewPopwindow;
import com.changlianxi.popwindow.ListViewPopwindow.OnlistOnclick;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.EditWather;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.MyViewGroup;

/**
 * 注册界面包含多个注册子view
 * 
 * @author teeker_bin
 * 
 */
public class RegisterActivity extends Activity implements OnClickListener,
		PostCallBack {
	private EditText spinner;
	private LinearLayout layLogin;
	private MyViewGroup rGroup;
	private View reg1, reg2, reg3, emailReg1, emilReg2;// 注册1、2、3界面和邮箱注意1、2界面
	private LayoutInflater flater;
	private LayoutParams params;
	private Button btnext;// 注册界面的下一步按钮
	private Button btfinish_yz;// 注册界面的完成验证按钮
	private TextView txtabout;// 注册界面的关于提示文字
	private Button btFinishRegister;// 注册界面的完成注册按钮
	private EditText ediNum;// 注册界面的输入手机号edittext
	private EditText ediPassword;// 注册界面的输入密码edittext
	private int rt;// 返回结果标志，1表示验证通过且生成新用户记录成功，其他值表示未成功TODO
	private String uid = "";// 成功后才有，代表用户ID
	private String token = "";
	private Button emailBtNext;// 邮箱注册的下一步按钮
	private EditText emailNum, emailEdit;// 邮箱注册界面的手机号码和邮箱控件
	private TextView emailTxt;// 邮箱注册界面的email显示控件
	private EditText code;// 注册界面的输入验证码edittext
	// private EditText emailCode;// 邮箱注册界面的输入验证码edittext
	private Button emBtFinish;// 邮箱注册界面的完成验证按钮
	private TextView txtQh;// 注册界面显示区号的textveiw如显示+86等
	private Button btGetCode;// 注册界面重新获取验证码按钮
	private int second = 60;// 用于重新获取验证码时间倒计时
	private TextView txtShowNum;// 注册界面显示用来注册的手机号
	private ProgressDialog progressDialog;
	private String type = "";// 1 验证码处理 2 设置密码处理
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		initView();
		getWindow()
				.setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
								| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
	}

	/**
	 * 初始化控件
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
		// emailCode = (EditText) emilReg2.findViewById(R.id.emailEdiCode);
		emBtFinish = (Button) emilReg2.findViewById(R.id.email_btfinish);
		emBtFinish.setOnClickListener(this);
		emailBtNext = (Button) emailReg1.findViewById(R.id.emailbtnext);
		emailBtNext.setOnClickListener(this);
		emailNum = (EditText) emailReg1.findViewById(R.id.emailnum);
		emailEdit = (EditText) emailReg1.findViewById(R.id.emailEdit);
		emailNum.setInputType(InputType.TYPE_CLASS_NUMBER);
		// emailNum.addTextChangedListener(new EditWather(2));
	}

	/**
	 * 初始化注册界面1的控件
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
	 * 初始化注册界面2的控件
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
	 * 初始化注册界面3的控件
	 */
	private void initReg3View() {
		btFinishRegister = (Button) reg3.findViewById(R.id.btfinish_register);
		btFinishRegister.setOnClickListener(this);
		txtabout = (TextView) reg3.findViewById(R.id.about);
		txtabout.setText(Html.fromHtml("<u>" + "关于注册时保护隐私的说明" + "</u>"));// 下划线
		ediPassword = (EditText) reg3.findViewById(R.id.editPassword);
	}

	/**
	 * 控件的点击事件
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
			Logger.debug(this, "txt:" + txtNum);
			if (!Utils.isPhoneNum(txtNum)) {
				Utils.showToast("请输入有效的手机号码！");
				return;
			}
			new RegisterTask().execute(txtNum, "", "1");
			break;
		case R.id.btfinish_yz:
			// new CheckCodeTask().execute(code.getText().toString());
			map = new HashMap<String, Object>();
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("auth_code", code.getText().toString());
			map.put("type", "register");
			task = new PostAsyncTask(this, map, "/users/iverifyAuthCode");
			task.setTaskCallBack(this);
			task.execute();
			progressDialog = new ProgressDialog(this);
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
			progressDialog = new ProgressDialog(this);
			progressDialog.show();
			type = "2";
			break;
		case R.id.emailbtnext:
			String emtxt = emailEdit.getText().toString();
			if (!Utils.isEmail(emtxt)) {
				Utils.showToast("请输入正确的邮箱地址！");
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
			Message msg = new Message();
			msg.what = 0;
			mHandler.sendEmptyMessage(0);
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
	 * 注册
	 * 
	 */
	class RegisterTask extends AsyncTask<String, Integer, String> {
		String txtnum = "";
		String type = "1";// 1短信注册 2 邮箱注册
		String emailtxt;

		// 可变长的输入参数，与AsyncTask.exucute()对应
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
					SharedUtils.setString("uid", uid);
					if (type.equals("1")) {
						rGroup.setView(reg2);
						txtShowNum.setText(replaceStr(txtnum));
						return;
					}
					rGroup.setView(emilReg2);
					emailTxt.setText(emailtxt);

				} else {
					// Utils.showToast("手机号码已被注册，请更换手机号码！");
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
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			progressDialog = new ProgressDialog(RegisterActivity.this);
			progressDialog.show();
		}
	}

	/**
	 * 将手机号码的中间四位替换为****
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

	/**
	 * 判断验证码是否正确
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
	 * 判断密码是否设置成功
	 * 
	 * @param result
	 */
	private void SetPassword(String result) {
		try {
			JSONObject object = new JSONObject(result);
			rt = object.getInt("rt");
			if (rt == 1) {
				token = object.getString("token");
				SharedUtils.setString("token", token);
				Logger.debug(this, "rt:" + rt + "  token:" + token);
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
			Logger.error(this, e);

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
		}
	}
}
