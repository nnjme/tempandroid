package com.changlianxi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.modle.SmsPrevieModle;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;

/**
 * .短信邀请界面
 * 
 * @author teeker_bin
 * 
 */
public class SmsInviteActivity extends Activity implements OnClickListener,
		PostCallBack {
	private List<SmsPrevieModle> contactsList = new ArrayList<SmsPrevieModle>();
	private EditText editpriview;
	private TextView txtShow;
	private TextView sendByServer;
	private Button btnSend;
	private String contactNames = "";
	private ImageView back;
	private String cmids;
	private String cid;
	private ProgressDialog pd;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sms_invite);
		Bundle bundle = getIntent().getExtras();
		contactsList = (List<SmsPrevieModle>) bundle
				.getSerializable("contactsList");
		cmids = getIntent().getStringExtra("cmids");
		cid = getIntent().getStringExtra("cid");
		editpriview = (EditText) findViewById(R.id.editpriview);
		editpriview.setText(getString(R.string.sms_content));
		txtShow = (TextView) findViewById(R.id.showTxt);
		sendByServer = (TextView) findViewById(R.id.sendByServer);
		btnSend = (Button) findViewById(R.id.btnSendBysms);
		getContactNames();
		sendByServer.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		sendByServer.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
		sendByServer.setText("通过服务器自动发送");
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(this);
		sendByServer.setOnClickListener(this);
		btnSend.setOnClickListener(this);
	}

	/**
	 * 得到前三位 联系人的名称
	 */
	private void getContactNames() {
		if (contactsList.size() > 3) {
			for (int i = 0; i < 3; i++) {
				contactNames += contactsList.get(i).getName() + "、";
			}
		} else {
			for (int i = 0; i < contactsList.size(); i++) {
				contactNames += contactsList.get(i).getName() + "、";
			}
		}
		String names = "<font color=\"#000000\">" + contactNames + "</font>";
		String count = "<font color=\"#ff7800\">" + contactsList.size()
				+ "</font>";
		txtShow.setText(Html.fromHtml("向 " + names + "等 " + count + "人发送短信邀请"));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.sendByServer:
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cid", cid);
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("token", SharedUtils.getString("token", ""));
			map.put("cmids", cmids);
			System.out.println("----cid:" + cid + "  uid:"
					+ SharedUtils.getString("uid", "") + "  cmids:" + cmids);
			PostAsyncTask task = new PostAsyncTask(this, map,
					"/people/isendInviteSms");
			task.setTaskCallBack(this);
			task.execute();
			pd = new ProgressDialog(this);
			pd.show();
			break;
		case R.id.btnSendBysms:
			for (int i = 0; i < contactsList.size(); i++) {
				SmsManager.getDefault().sendTextMessage(
						contactsList.get(i).getNum(),
						null,
						"亲爱的" + contactsList.get(i).getName() + ",邀请你加入圈子,"
								+ getString(R.string.sms_content), null, null);
			}
			finish();
			break;
		default:
			break;
		}

	}

	@Override
	public void taskFinish(String result) {
		System.out.println("result::" + result);
		pd.dismiss();
		try {
			JSONObject object = new JSONObject(result);
			String rt = object.getString("rt");
			if (rt.equals("1")) {
				Utils.showToast("发送成功");
				finish();
			} else {
				Utils.showToast("发送失败");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
