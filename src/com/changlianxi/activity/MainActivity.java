package com.changlianxi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import com.changlianxi.util.Logger;
import com.changlianxi.view.FlipperLayout;
import com.changlianxi.view.FlipperLayout.OnOpenListener;
import com.changlianxi.view.Home;
import com.changlianxi.view.MessagesList;
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
	}

	@Override
	protected void onStart() {
		Logger.debug(this, "onStartonStart");
		super.onStart();
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
				case 2:
					if (mMessage == null) {
						mMessage = new MessagesList(MainActivity.this);
						mMessage.setOnOpenListener(MainActivity.this);
					}
					mRoot.close(mMessage.getView());
					break;

				default:
					break;
				}

			}
		});

	}

	public void open() {
		if (mRoot.getScreenState() == FlipperLayout.SCREEN_STATE_CLOSE) {
			mRoot.open();
		}
	}
}
