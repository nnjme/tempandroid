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
	 * 当前显示内容的容器(继承于ViewGroup)
	 */
	private FlipperLayout mRoot;
	/**
	 * 设置界面
	 */
	private Setting mSetting;
	/**
	 * 私信列表界面
	 */
	private MessagesList mMessage;
	/**
	 * 我的名片界面
	 */
	private MyCard mCard;
	/**
	 * 菜单界面
	 */
	private SetMenu mDesktop;
	/**
	 * 内容首页界面
	 */
	private Home mHome;
	public static Activity mInstance;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CLXApplication.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/**
		 * 创建容器,并设置全屏大小
		 */
		mRoot = new FlipperLayout(this);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mRoot.setLayoutParams(params);
		/**
		 * 创建菜单界面和内容首页界面,并添加到容器中,用于初始显示
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
	 * UI事件监听
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
	 * 连续按两次返回键就退出
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// TODO Auto-generated method stub
			if (System.currentTimeMillis() - firstTime < 3000) {
				CLXApplication.exit();
			} else {
				firstTime = System.currentTimeMillis();
				Utils.showToast("再按一次退出程序");
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
