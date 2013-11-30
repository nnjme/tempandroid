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
	 * ��ǰ��ʾ���ݵ�����(�̳���ViewGroup)
	 */
	private FlipperLayout mRoot;
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bitmap bitmap = null;
		String avatarPath = "";
		if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD
				&& resultCode == RESULT_OK && data != null) {
			SelectPicModle modle = BitmapUtils.getPickPic(this, data);
			avatarPath = modle.getPicPath();
			BitmapUtils.startPhotoZoom(this, data.getData());

		}// ����ͼƬ
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
	 * UI�¼�����
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
	 * ���������η��ؼ����˳�
	 */
	private long firstTime;

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (System.currentTimeMillis() - firstTime < 3000) {
			finish();
		} else {
			firstTime = System.currentTimeMillis();
			Utils.showToast("�ٰ�һ���˳�����");
		}
	}

	public void open() {
		if (mRoot.getScreenState() == FlipperLayout.SCREEN_STATE_CLOSE) {
			mRoot.open();
		}
	}
}
