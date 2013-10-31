package com.changlianxi.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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

import com.changlianxi.modle.ContactModle;

public class SmsInviteActivity extends Activity implements OnClickListener {
	private List<ContactModle> contactsList = new ArrayList<ContactModle>();
	private EditText editpriview;
	private TextView txtShow;
	private TextView sendByServer;
	private Button btnSend;
	private String contactNames = "";
	private ImageView back;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sms_invite);
		Bundle bundle = getIntent().getExtras();
		contactsList = (List<ContactModle>) bundle
				.getSerializable("contactsList");
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
			finish();
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
}
