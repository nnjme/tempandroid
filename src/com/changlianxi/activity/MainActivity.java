package com.changlianxi.activity;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.Info;
import com.changlianxi.task.GetMyDetailTask;
import com.changlianxi.task.GetMyDetailTask.GetMyDetail;
import com.changlianxi.task.GetMyNotifyTask;
import com.changlianxi.task.GetMyNotifyTask.GetMyNotify;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.SharedUtils;
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
	private GetMyDetailTask task;
	private GetMyNotifyTask task1;
	private boolean prompt;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CLXApplication.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mRoot = new FlipperLayout(this);
		prompt = DBUtils.getMyCardPrompt();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mRoot.setLayoutParams(params);
		mDesktop = new SetMenu(this, prompt);
		mDesktop.setMyCardPrompt(prompt);
		mHome = new Home(this, prompt);
		mRoot.addView(mDesktop.getView(), params);
		mRoot.addView(mHome.getView(), params);
		setContentView(mRoot);
		setListener();
		getMyDetail();
		registerBoradcastReceiver();
	}

	private void getMyDetail() {
		task = new GetMyDetailTask();
		task.setTaskCallBack(new GetMyDetail() {
			@Override
			public void getMydetail(String avatarUrl) {
				mDesktop.setAvatar(avatarUrl);
			}
		});
		task.execute();
		task1 = new GetMyNotifyTask(SharedUtils.getString("exitTime",
				DateUtils.phpTime(System.currentTimeMillis())));
		task1.setTaskCallBack(new GetMyNotify() {

			@Override
			public void getMyNotify(boolean newCircle, boolean newMessge,
					boolean cardPrompt) {
				mDesktop.setMessagePrompt(newMessge);
				mDesktop.setMyCardPrompt(cardPrompt);
				if (newMessge || cardPrompt) {
					mHome.setVisibleImgPrompt();
				}
			}
		});
		task1.execute();
	}

	@Override
	protected void onDestroy() {
		if (task != null && task.getStatus() == Status.RUNNING) {
			task.cancel(true); // ���Task�������У�����ȡ����
		}
		if (task1 != null && task1.getStatus() == Status.RUNNING) {
			task1.cancel(true); // ���Task�������У�����ȡ����
		}
		mHome.cancleTask();
		unregisterReceiver(mBroadcastReceiver);
		SharedUtils.setBoolean("isBackHome", true);// ��̨����
		DBUtils.close();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		SharedUtils.setBoolean("isBackHome", false);// ��̨����
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
			String avatatPath = data.getStringExtra("avatarPath");
			mCard.cardShow.setName(name);
			Bitmap bmp = bundle.getParcelable("avatar");
			if (bmp != null) {
				mCard.cardShow.setAvatar(bmp, avatatPath);
				mDesktop.setEidtAvatar(bmp);
			}
			if (basicList != null && contactList != null && socialList != null
					&& addressList != null && eduList != null
					&& workList != null)
				mCard.cardShow.notifyData(basicList, contactList, socialList,
						addressList, eduList, workList);

		}

	}

	/**
	 * ע��ù㲥
	 */
	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(Constants.REFRESH_CIRCLE_LIST);
		myIntentFilter.addAction(Constants.ACCEPT_OR_REFUSE_INVITE);
		myIntentFilter.addAction(Constants.EXIT_CIRCLE);
		myIntentFilter.addAction(Constants.REMOVE_PROMPT_COUNT);
		myIntentFilter.addAction(Constants.PUSH_TYPE);
		myIntentFilter.addAction(Constants.UPDECIRNAME);
		myIntentFilter.addAction(Constants.MYCARD_PROMPT);
		myIntentFilter.addAction(Constants.MESSAGE_PROMPT);

		// ע��㲥
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	/**
	 * ����㲥
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Constants.REFRESH_CIRCLE_LIST)) {// ����Ȧ���б�
				mHome.refreshCircleList();
			} else if (action.equals(Constants.ACCEPT_OR_REFUSE_INVITE)) {// ���ܻ��߾ܾ�Ȧ������
				String cid = intent.getStringExtra("cid");
				boolean flag = intent.getBooleanExtra("flag", false);
				mHome.acceptOrRefuseInvite(cid, flag);
			} else if (action.equals(Constants.EXIT_CIRCLE)) {// �˳�Ȧ��
				String cid = intent.getStringExtra("cid");
				mHome.exitCircle(cid);
			} else if (action.equals(Constants.REMOVE_PROMPT_COUNT)) {// ����Ȧ����ʾ����
				String cid = intent.getStringExtra("cid");
				int position = intent.getIntExtra("position", 0);
				int promptCount = intent.getIntExtra("promptCount", 0);
				mHome.remorePromptCount(cid, promptCount, position);
			} else if (action.equals(Constants.PUSH_TYPE)) {// ������Ϣ
				String cid = intent.getStringExtra("cid");
				String type = intent.getStringExtra("type");
				int promptCount = intent.getIntExtra("promptCount", 0);
				mHome.pushPormpt(cid, promptCount, type);
			} else if (action.equals(Constants.UPDECIRNAME)) {// ����Ȧ������
				String cid = intent.getStringExtra("cid");
				String cirName = intent.getStringExtra("cirName");
				mHome.upDateCirName(cid, cirName);
			} else if (action.equals(Constants.MYCARD_PROMPT)) {// ������Ƭ����
				boolean prot = intent.getBooleanExtra("prompt", false);
				mDesktop.setMyCardPrompt(prot);
				prompt = mDesktop.messagePrompt;
			} else if (action.equals(Constants.MESSAGE_PROMPT)) {// ˽������
				boolean prot = intent.getBooleanExtra("prompt", false);
				mDesktop.setMessagePrompt(prot);
				prompt = mDesktop.myCardPrompt;

			}
		}
	};

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
					mHome = new Home(MainActivity.this, prompt);
					mHome.setOnOpenListener(MainActivity.this);
					// }
					mRoot.close(mHome.getView());
					// mHome.getServerCircleLists();
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
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {// ������ΰ���ʱ��������2�룬���˳�
				Utils.showToast("�ٰ�һ���˳�����");
				firstTime = secondTime;// ����firstTime
				return true;
			} else {
				SharedUtils.setString("exitTime",
						DateUtils.phpTime(System.currentTimeMillis()));
				mHome.savePromptCount();
				CLXApplication.exit(false);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void open() {
		if (mRoot.getScreenState() == FlipperLayout.SCREEN_STATE_CLOSE) {
			mRoot.open();
		}
	}
}
