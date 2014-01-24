package com.changlianxi.activity;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

import com.changlianxi.R;

public class ShareActivity extends Activity implements OnClickListener,
		PlatformActionListener {
	String content = "";
	String imgUrl = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.button2).setOnClickListener(this);
		findViewById(R.id.button3).setOnClickListener(this);
		findViewById(R.id.button4).setOnClickListener(this);
		findViewById(R.id.button5).setOnClickListener(this);
		content = getIntent().getExtras().getString("content");
		imgUrl = getIntent().getExtras().getString("imgUrl");
		System.out.println("~~~~~~~~~~~~~~~~~~~~"+imgUrl);
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
			// Platform platform = ShareSDK.getPlatform(ShareActivity.this,
			// WechatMoments.NAME);
			// WechatMoments.ShareParams sParams = new
			// WechatMoments.ShareParams();
			// sParams.shareType = Platform.SHARE_TEXT;
			// sParams.title = "�������";
			// sParams.text = "��������";
			// platform.setPlatformActionListener(ShareActivity.this);
			// platform.share(sParams);
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
		 oks.setAddress("12345678901");
		oks.setTitle("���Գ���ϵ�ķ���");
		oks.setTitleUrl("http://www.baidu.com/");
		// ����
		oks.setText(content);
		// ͼƬ
		oks.setImageUrl(imgUrl);
//		oks.setImagePath(Environment.getExternalStorageDirectory() + "/DCIM/Camera/1389748596762.jpg");
		//oks.setImageUrl("http://t1.baidu.com/it/u=2015605517,3496666907&fm=21&gp=0.jpg");
		oks.setUrl("http://www.teeker.com/");
		// oks.setFilePath(MainActivity.TEST_IMAGE);
		oks.setComment("55555555");
		 oks.setSite("����ϵ");
		// ��ַ����
//		 oks.setSiteUrl("http://sharesdk.cn");
//		 oks.setVenueName("ShareSDK");
//		 oks.setVenueDescription("This is a beautiful place!");
		 //����γ��
//		 oks.setLatitude(23.056081f);
//		 oks.setLongitude(113.385708f);
//		oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
//			
//			@Override
//			public void onShare(Platform platform, ShareParams paramsToShare) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		oks.setSilent(silent);
		if (platform != null) {
			oks.setPlatform(platform);
		}

		// ȥ��ע�ͣ�����༭ҳ����ʾΪDialogģʽ
		// oks.setDialogMode();

		// ȥ��ע�ͣ����Զ���Ȩʱ���Խ���SSO��ʽ
		oks.disableSSOWhenAuthorize();

		oks.show(this);
	}

	@Override
	public void onCancel(Platform arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		arg2.printStackTrace();
	}

}
