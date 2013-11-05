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
	 * 当前显示内容的容器(继承于ViewGroup)
	 */
	private FlipperLayout mRoot;
	/**
	 * 私信列表界面
	 */
	private MessagesList mMessage;
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
	}

	@Override
	protected void onStart() {
		Logger.debug(this, "onStartonStart");
		super.onStart();
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
