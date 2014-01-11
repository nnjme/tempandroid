package com.changlianxi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.modle.SmsPrevieModle;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.umeng.analytics.MobclickAgent;

/**
 * .短信邀请界面
 * 
 * @author teeker_bin
 * 
 */
public class SmsInviteActivity extends BaseActivity implements OnClickListener,
		PostCallBack {
	private List<SmsPrevieModle> contactsList = new ArrayList<SmsPrevieModle>();
	private TextView txtShow;
	private TextView sendByServer;
	private Button btnSend;
	private String contactNames = "";
	private ImageView back;
	private String cmids;
	private String cid;
	private Dialog pd;
	private TextView titleTxt;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms_invite);
		Bundle bundle = getIntent().getExtras();
		contactsList = (List<SmsPrevieModle>) bundle
				.getSerializable("contactsList");
		cmids = getIntent().getStringExtra("cmids");
		cid = getIntent().getStringExtra("cid");
		txtShow = (TextView) findViewById(R.id.showTxt);
		sendByServer = (TextView) findViewById(R.id.sendByServer);
		btnSend = (Button) findViewById(R.id.btnSendBysms);
		getContactNames();
		sendByServer.setText(Html.fromHtml("<u>通过服务器自动发送</u>"));
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(this);
		sendByServer.setOnClickListener(this);
		btnSend.setOnClickListener(this);
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText("邀请短信发送");
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
	 * 得到前三位 联系人的名称
	 */
	private void getContactNames() {
		if (contactsList.size() > 5) {
			for (int i = 0; i < 5; i++) {
				contactNames += contactsList.get(i).getName() + " ";
			}
		} else {
			for (int i = 0; i < contactsList.size(); i++) {
				contactNames += contactsList.get(i).getName() + " ";
			}
		}
		String names = "<font color=\"#000000\">" + contactNames + "</font>";
		String count = "<font color=\"#fd7a01\">" + contactsList.size()
				+ "</font>";
		String txt;
		if (contactsList.size() > 5) {
			txt = "向 " + names + "等 " + count + "人发送短信邀请";
		} else {
			txt = "向 " + names + count + "人发送短信邀请";
		}
		txtShow.setText(Html.fromHtml(txt));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			Utils.rightOut(this);

			break;
		case R.id.sendByServer:
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cid", cid);
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("token", SharedUtils.getString("token", ""));
			map.put("cmids", cmids);
			PostAsyncTask task = new PostAsyncTask(this, map,
					"/people/isendInviteSms");
			task.setTaskCallBack(this);
			task.execute();
			pd = DialogUtil.getWaitDialog(this, "请稍后");
			pd.show();
			break;
		case R.id.btnSendBysms:
			for (int i = 0; i < contactsList.size(); i++) {
				SmsManager.getDefault().sendTextMessage(
						contactsList.get(i).getNum(), null,
						contactsList.get(i).getContent(), null, null);
			}
			CLXApplication.exitSmsInvite();
			finish();
			Utils.rightOut(this);

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
			String rt = object.getString("rt");
			if (rt.equals("1")) {
				Utils.showToast("发送成功");
				CLXApplication.exitSmsInvite();
				finish();
				Utils.rightOut(this);

			} else {
				String err = object.getString("err");
				Utils.showToast(ErrorCodeUtil.convertToChines(err));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
