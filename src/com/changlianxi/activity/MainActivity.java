package com.changlianxi.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.Info;
import com.changlianxi.task.GetMyDetailTask;
import com.changlianxi.task.GetMyDetailTask.GetMyDetail;
import com.changlianxi.util.Utils;
import com.changlianxi.view.FlipperLayout;
import com.changlianxi.view.FlipperLayout.OnOpenListener;
import com.changlianxi.view.Home;
import com.changlianxi.view.MessagesList;
import com.changlianxi.view.MyCard;
import com.changlianxi.view.SetMenu;
import com.changlianxi.view.SetMenu.onChangeViewListener;
import com.changlianxi.view.Setting;

public class MainActivity extends Activity implements OnOpenListener {
	/**
	 * ��ǰ��ʾ���ݵ�����(�̳���ViewGroup)
	 */
	private FlipperLayout mRoot;
	/**
	 * ���ý���
	 */
	private Setting mSetting;
	/**
	 * ˽���б����
	 */
	private MessagesList mMessage;
	/**
	 * �ҵ���Ƭ����
	 */
	private MyCard mCard;
	/**
	 * �˵�����
	 */
	private SetMenu mDesktop;
	/**
	 * ������ҳ����
	 */
	private Home mHome;
	public static Activity mInstance;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CLXApplication.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/**
		 * ��������,������ȫ����С
		 */
		mRoot = new FlipperLayout(this);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mRoot.setLayoutParams(params);
		/**
		 * �����˵������������ҳ����,����ӵ�������,���ڳ�ʼ��ʾ
		 */
		mDesktop = new SetMenu(this, this);
		mHome = new Home(this);
		mRoot.addView(mDesktop.getView(), params);
		mRoot.addView(mHome.getView(), params);
		setContentView(mRoot);
		setListener();
		GetMyDetailTask task = new GetMyDetailTask();
		task.setTaskCallBack(new GetMyDetail() {
			@Override
			public void getMydetail(String avatarUrl) {
				mDesktop.setAvatar(avatarUrl);
			}
		});
		task.execute();
	}

	@Override
	protected void onDestroy() {
		DBUtils.close();
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2 && data != null) {
			Bundle bundle = data.getExtras();
			List<Info> basicList = (List<Info>) bundle
					.getSerializable("basicList");
			List<Info> contactList = (List<Info>) bundle
					.getSerializable("contactList");
			List<Info> socialList = (List<Info>) bundle
					.getSerializable("socialList");
			List<Info> addressList = (List<Info>) bundle
					.getSerializable("addressList");
			List<Info> eduList = (List<Info>) bundle.getSerializable("eduList");
			List<Info> workList = (List<Info>) bundle
					.getSerializable("workList");
			String name = data.getStringExtra("name");
			mCard.cardShow.setName(name);
			Bitmap bmp = bundle.getParcelable("avatar");
			if (bmp != null) {
				mCard.cardShow.setAvatar(bmp);
				SetMenu.setEidtAvatar(bmp);
			}
			if (basicList != null && contactList != null && socialList != null
					&& addressList != null && eduList != null
					&& workList != null)
				mCard.cardShow.notifyData(basicList, contactList, socialList,
						addressList, eduList, workList);

		}

	}

	/**
	 * UI�¼�����
	 */
	private void setListener() {
		mHome.setOnOpenListener(this);
		mDesktop.setOnChangeViewListener(new onChangeViewListener() {
			@Override
			public void onChangeView(int arg0) {
				switch (arg0) {
				case 0:
					// if (mHome == null) {
					mHome = new Home(MainActivity.this);
					mHome.setOnOpenListener(MainActivity.this);
					// }
					mRoot.close(mHome.getView());
					break;
				case 1:
					// if (mCard == null) {
					mCard = new MyCard(MainActivity.this);
					mCard.setOnOpenListener(MainActivity.this);
					// }
					mRoot.close(mCard.getView());
					break;
				case 2:
					mMessage = new MessagesList(MainActivity.this);
					mMessage.setOnOpenListener(MainActivity.this);
					mRoot.close(mMessage.getView());
					break;
				case 3:
					if (mSetting == null) {
						mSetting = new Setting(MainActivity.this);
						mSetting.setOnOpenListener(MainActivity.this);
					}
					mRoot.close(mSetting.getView());

					break;
				default:
					break;
				}

			}
		});

	}

	private long firstTime;

	/**
	 * ���������η��ؼ����˳�
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// TODO Auto-generated method stub
			if (System.currentTimeMillis() - firstTime < 3000) {
				CLXApplication.exit();
			} else {
				firstTime = System.currentTimeMillis();
				Utils.showToast("�ٰ�һ���˳�����");
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {

	}

	public void open() {
		if (mRoot.getScreenState() == FlipperLayout.SCREEN_STATE_CLOSE) {
			mRoot.open();
		}
	}
}
