package com.changlianxi.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.SelectPicModle;
import com.changlianxi.task.GetMyDetailTask;
import com.changlianxi.task.GetMyDetailTask.GetMyDetail;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.Utils;
import com.changlianxi.view.FlipperLayout;
import com.changlianxi.view.FlipperLayout.OnOpenListener;
import com.changlianxi.view.Home;
import com.changlianxi.view.MessagesList;
import com.changlianxi.view.MyCard;
import com.changlianxi.view.SetMenu;
import com.changlianxi.view.SetMenu.onChangeViewListener;

public class MainActivity extends Activity implements OnOpenListener {
	/**
	 * 当前显示内容的容器(继承于ViewGroup)
	 */
	private FlipperLayout mRoot;
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bitmap bitmap = null;
		String avatarPath = "";
		if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD
				&& resultCode == RESULT_OK && data != null) {
			SelectPicModle modle = BitmapUtils.getPickPic(this, data);
			avatarPath = modle.getPicPath();
			BitmapUtils.startPhotoZoom(this, data.getData());

		}// 拍摄图片
		else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
			if (resultCode != RESULT_OK) {
				return;
			}
			String fileName = mCard.pop.getTakePhotoPath();
			avatarPath = fileName;
			BitmapUtils.startPhotoZoom(this, Uri.fromFile(new File(fileName)));
			// cirImg.setImageBitmap(bitmap);
		} else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_DROP) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				// cirImg.setImageBitmap(photo);
				bitmap = photo;
			}
			mCard.setAvatarPath(avatarPath, bitmap);
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
					if (mHome == null) {
						mHome = new Home(MainActivity.this);
						mHome.setOnOpenListener(MainActivity.this);
					}
					mRoot.close(mHome.getView());
					break;
				case 1:
					if (mCard == null) {
						mCard = new MyCard(MainActivity.this);
						mCard.setOnOpenListener(MainActivity.this);
					}
					mRoot.close(mCard.getView());
					break;
				case 2:
					mMessage = new MessagesList(MainActivity.this);
					mMessage.setOnOpenListener(MainActivity.this);
					mRoot.close(mMessage.getView());
					break;

				default:
					break;
				}

			}
		});

	}

	/**
	 * 连续按两次返回键就退出
	 */
	private long firstTime;

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (System.currentTimeMillis() - firstTime < 3000) {
			finish();
		} else {
			firstTime = System.currentTimeMillis();
			Utils.showToast("再按一次退出程序");
		}
	}

	public void open() {
		if (mRoot.getScreenState() == FlipperLayout.SCREEN_STATE_CLOSE) {
			mRoot.open();
		}
	}
}
