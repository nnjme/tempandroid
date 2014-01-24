package com.changlianxi.activity;

import java.util.HashMap;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import com.changlianxi.R;
import com.changlianxi.util.FileUtils;

public class ShareActivity extends Activity implements OnClickListener,
		PlatformActionListener {
	String content = "";
	String imgUrl = "";
	String imgLocalPath = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.button2).setOnClickListener(this);
		findViewById(R.id.button3).setOnClickListener(this);
		findViewById(R.id.button4).setOnClickListener(this);
		findViewById(R.id.button5).setOnClickListener(this);
		content = getIntent().getExtras().getString("content");
		imgUrl = getIntent().getExtras().getString("imgUrl");
		imgLocalPath = FileUtils.getCachePath(imgUrl);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			showShare(true, "TencentWeibo", imgUrl, content);
			break;
		case R.id.button2:
			showShare(true, "SinaWeibo", imgUrl, content);
			break;
		case R.id.button3:
			showShare(true, "QZone", imgUrl, content);
			break;
		case R.id.button4:
			showShare(true, "WechatMoments", imgUrl, content);
			break;
		case R.id.button5:
			showShare(true, "Wechat", imgUrl, content);
			break;
		}
	}

	private void showShare(boolean silent, String platform, String imgUrl,
			String content) {
		final OnekeyShare oks = new OnekeyShare();
		oks.setNotification(R.drawable.ic_launcher, "12345678901");
		// oks.setAddress("12345678901");
		oks.setTitle("来自常联系的分享");
		oks.setTitleUrl("http://www.teeker.com/");
		oks.setText(content);
		oks.setImagePath(imgLocalPath);
		oks.setUrl("http://www.teeker.com/");
		oks.setComment("55555555");
		oks.setSite("常联系");
		oks.setSilent(silent);
		if (platform != null) {
			oks.setPlatform(platform);
		}
		oks.disableSSOWhenAuthorize();
		oks.show(this);
	}

	@Override
	public void onCancel(Platform arg0, int arg1) {

	}

	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {

	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		arg2.printStackTrace();
	}

}
