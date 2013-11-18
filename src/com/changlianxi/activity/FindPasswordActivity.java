package com.changlianxi.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.EditWather;
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
public class FindPasswordActivity extends Activity implements OnClickListener,
		PostCallBack {
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
	private String uid;
	private String type = "";// 1 �һ�����ص��ӿڴ��� 2 ��֤��ӿڻص����� 3 ��������ӿڻص�����
	private ProgressDialog pd;

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
		ediNum.addTextChangedListener(new EditWather(ediNum));
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

	@Override
	public void onClick(View v) {
		PostAsyncTask task = null;
		Map<String, Object> map = null;
		switch (v.getId()) {
		case R.id.btnext:
			// �һ�����֮��֤�ֻ����Ƿ����
			map = new HashMap<String, Object>();
			map.put("cellphone", ediNum.getText().toString().replace("-", ""));
			task = new PostAsyncTask(this, map, "/users/iretrievePassword");
			task.setTaskCallBack(this);
			task.execute();
			pd = new ProgressDialog(this);
			pd.show();
			type = "1";
			break;
		case R.id.btfinish_yz:
			// �һ�����֮��ȡ��֤��
			map = new HashMap<String, Object>();
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("auth_code", ediCode.getText().toString());
			map.put("type", "retrievePasswd");
			task = new PostAsyncTask(this, map, "/users/iverifyAuthCode");
			task.setTaskCallBack(this);
			task.execute();
			pd = new ProgressDialog(this);
			pd.show();
			type = "2";
			break;
		case R.id.btback:
			finish();
			break;
		case R.id.btfinish:
			// �һ�����֮��������
			map = new HashMap<String, Object>();
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("uid", uid);
			map.put("type", "retrievePasswd");
			map.put("cellphone", ediNum.getText().toString().replace("-", ""));
			map.put("passwd", ediPasswd.getText().toString());
			task = new PostAsyncTask(this, map, "/users/isetPasswd");
			task.setTaskCallBack(this);
			task.execute();
			pd = new ProgressDialog(this);
			pd.show();
			type = "3";
			break;
		default:
			break;
		}
	}

	/***
	 * �һ����봦��
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

			} else {
				Utils.showToast("�ֻ����벻����");
			}
		} catch (JSONException e) {
			Logger.error(this, e);

			e.printStackTrace();
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

	/**
	 * �ж������Ƿ����óɹ�
	 * 
	 * @param result
	 */
	private void SetPassword(String result) {
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

	/**
	 * �ӿڻص�����
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
		}

	}
}
