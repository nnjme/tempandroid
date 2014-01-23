package com.changlianxi.fragment;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Window;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.changlianxi.R;
import com.changlianxi.activity.CLXApplication;
import com.changlianxi.db.DBUtils;
import com.changlianxi.fragment.LeftMenuFragMent.onChangeFragMentListener;
import com.changlianxi.inteface.PushOnBind;
import com.changlianxi.modle.Info;
import com.changlianxi.slidingmenu.lib.SlidingMenu;
import com.changlianxi.slidingmenu.lib.app.SlidingActivity;
import com.changlianxi.task.GetMyDetailTask;
import com.changlianxi.task.GetMyDetailTask.GetMyDetail;
import com.changlianxi.task.GetMyNotifyTask;
import com.changlianxi.task.GetMyNotifyTask.GetMyNotify;
import com.changlianxi.task.SetClientInfo;
import com.changlianxi.task.SetClientInfo.ClientCallBack;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.PushMessageReceiver;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;

@SuppressLint("NewApi")
public class MainActivity1 extends SlidingActivity implements
		onChangeFragMentListener, PushOnBind {
	private LeftMenuFragMent menuFragMent = null;
	private HomeFragMent homeFragMent = null;
	public FragmentTransaction fraTra = null;
	private int menuWidth;
	private MyCardFragMent myCardFragMent = null;
	private MessageListFragMent messageListFragMent = null;
	private SettingFragMent setFragMent = null;
	private int currentFramentIndex;
	private long firstTime;
	private boolean prompt;
	private GetMyDetailTask task;
	private GetMyNotifyTask task1;
	private boolean isFirstLogin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_activity1);
		setBehindContentView(R.layout.left_menu); // 设置菜单页
		CLXApplication.addActivity(this);
		menuWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 178, getResources()
						.getDisplayMetrics());
		fraTra = this.getFragmentManager().beginTransaction();
		menuFragMent = new LeftMenuFragMent(this);
		homeFragMent = new HomeFragMent(this);
		menuFragMent.setmOnChangeFragMentListener(this);
		fraTra.replace(R.id.left_menu, menuFragMent);
		fraTra.replace(R.id.main_rl, homeFragMent);
		fraTra.commit();
		SlidingMenu sm = getSlidingMenu(); // 滑动菜单
		sm.setBehindOffset(menuWidth); // 菜单与边框的距离
		sm.setShadowDrawable(R.drawable.shadow); // 滑动菜单渐变
		sm.setFadeDegree(0.35f); // 色度
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN); // 边缘滑动菜单
		prompt = DBUtils.getMyCardPrompt();
		getMyDetail();
		registerBoradcastReceiver();
		isFirstLogin();
	}

	private void isFirstLogin() {
		isFirstLogin = SharedUtils.getBoolean("isFristLogin", true);
		if (isFirstLogin) {
			// 以apikey的方式登录，一般放在主Activity的onCreate中
			PushManager.startWork(this, PushConstants.LOGIN_TYPE_API_KEY,
					Utils.getMetaValue(this, "api_key"));
			PushMessageReceiver.setPushOnBind(this);
			SharedUtils.setBoolean("isFristLogin", false);
		}
	}

	/**
	 * 属性是否设置成功
	 */
	private void isSetSuccess(String result) {
		try {
			JSONObject json = new JSONObject(result);
			int rt = json.getInt("rt");
			if (rt != 1) {
				String err = json.getString("err");
				String errorString = ErrorCodeUtil.convertToChines(err);
				Utils.showToast(errorString);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onChangeFragMent(int position) {
		fraTra = getFragmentManager().beginTransaction();
		getCurrentFragMent();
		switch (position) {
		case 0:
			if (homeFragMent == null) {
				homeFragMent = new HomeFragMent(this);
				fraTra.add(R.id.main_rl, homeFragMent);
			} else {
				homeFragMent.onResume(); // 启动目标FragMent的onResume()
			}
			break;
		case 1:
			if (myCardFragMent == null) {
				myCardFragMent = new MyCardFragMent(this);
				fraTra.add(R.id.main_rl, myCardFragMent);
			} else {
				myCardFragMent.onResume();
			}
			break;
		case 2:
			if (messageListFragMent == null) {
				messageListFragMent = new MessageListFragMent(this);
				fraTra.add(R.id.main_rl, messageListFragMent);
			} else {
				menuFragMent.onResume();
			}
			break;
		case 3:
			if (setFragMent == null) {
				setFragMent = new SettingFragMent(this);
				fraTra.add(R.id.main_rl, setFragMent);
			} else {
				setFragMent.onResume();
			}
			break;
		default:
			break;
		}
		setCurrentFragMent(position);
		toggle();
		fraTra.commit();
		currentFramentIndex = position;

	}

	private void setCurrentFragMent(int position) {
		switch (position) {
		case 0:
			fraTra.show(homeFragMent);
			if (myCardFragMent != null) {
				fraTra.hide(myCardFragMent);
			}
			if (messageListFragMent != null) {
				fraTra.hide(messageListFragMent);
			}
			if (setFragMent != null) {
				fraTra.hide(setFragMent);
			}
			break;
		case 1:
			fraTra.show(myCardFragMent);
			if (homeFragMent != null) {
				fraTra.hide(homeFragMent);
			}
			if (messageListFragMent != null) {
				fraTra.hide(messageListFragMent);
			}
			if (setFragMent != null) {
				fraTra.hide(setFragMent);
			}
			break;
		case 2:
			fraTra.show(messageListFragMent);
			if (homeFragMent != null) {
				fraTra.hide(homeFragMent);
			}
			if (myCardFragMent != null) {
				fraTra.hide(myCardFragMent);
			}
			if (setFragMent != null) {
				fraTra.hide(setFragMent);
			}
			break;
		case 3:
			fraTra.show(setFragMent);
			if (homeFragMent != null) {
				fraTra.hide(homeFragMent);
			}
			if (messageListFragMent != null) {
				fraTra.hide(messageListFragMent);
			}
			if (myCardFragMent != null) {
				fraTra.hide(myCardFragMent);
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 把当前fragment设置哼onPause状态
	 * 
	 * @param currentFramentIndex
	 */
	private void getCurrentFragMent() {
		switch (currentFramentIndex) {
		case 0:
			homeFragMent.onPause();
			break;
		case 1:
			myCardFragMent.onPause();
			break;
		case 2:
			messageListFragMent.onPause();
			break;
		case 3:
			setFragMent.onPause();
			break;

		default:
			break;
		}
	}

	/**
	 * 连续按两次返回键就退出
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {// 如果两次按键时间间隔大于2秒，则不退出
				Utils.showToast("再按一次退出程序");
				firstTime = secondTime;// 更新firstTime
				return true;
			} else {
				SharedUtils.setString("exitTime",
						DateUtils.phpTime(System.currentTimeMillis()));
				homeFragMent.savePromptCount();
				CLXApplication.exit(false);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getMyDetail() {
		task = new GetMyDetailTask();
		task.setTaskCallBack(new GetMyDetail() {
			@Override
			public void getMydetail(String avatarUrl) {
				menuFragMent.setAvatar(avatarUrl);
			}
		});
		task.execute();
		task1 = new GetMyNotifyTask(SharedUtils.getString("exitTime",
				DateUtils.phpTime(System.currentTimeMillis())));
		task1.setTaskCallBack(new GetMyNotify() {

			@Override
			public void getMyNotify(boolean newCircle, boolean newMessge,
					boolean cardPrompt) {
				menuFragMent.setMessagePrompt(newMessge);
				menuFragMent.setMyCardPrompt(cardPrompt);
				if (newMessge || cardPrompt) {
					homeFragMent.setVisibleImgPrompt();
				}
			}
		});
		task1.execute();
	}

	@Override
	protected void onDestroy() {
		if (task != null && task.getStatus() == Status.RUNNING) {
			task.cancel(true); // 如果Task还在运行，则先取消它
		}
		if (task1 != null && task1.getStatus() == Status.RUNNING) {
			task1.cancel(true); // 如果Task还在运行，则先取消它
		}
		homeFragMent.cancleTask();
		unregisterReceiver(mBroadcastReceiver);
		SharedUtils.setBoolean("isBackHome", true);// 后台运行
		DBUtils.close();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		SharedUtils.setBoolean("isBackHome", false);// 后台运行
		super.onResume();
	}

	/**
	 * 注册该广播
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
		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	/**
	 * 定义广播
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Constants.REFRESH_CIRCLE_LIST)) {// 更新圈子列表
				homeFragMent.refreshCircleList();
			} else if (action.equals(Constants.ACCEPT_OR_REFUSE_INVITE)) {// 接受或者拒绝圈子邀请
				String cid = intent.getStringExtra("cid");
				boolean flag = intent.getBooleanExtra("flag", false);
				homeFragMent.acceptOrRefuseInvite(cid, flag);
			} else if (action.equals(Constants.EXIT_CIRCLE)) {// 退出圈子
				String cid = intent.getStringExtra("cid");
				homeFragMent.exitCircle(cid);
			} else if (action.equals(Constants.REMOVE_PROMPT_COUNT)) {// 减少圈子提示数量
				String cid = intent.getStringExtra("cid");
				int position = intent.getIntExtra("position", 0);
				int promptCount = intent.getIntExtra("promptCount", 0);
				homeFragMent.remorePromptCount(cid, promptCount, position);
			} else if (action.equals(Constants.PUSH_TYPE)) {// 推送消息
				String cid = intent.getStringExtra("cid");
				String type = intent.getStringExtra("type");
				int promptCount = intent.getIntExtra("promptCount", 0);
				homeFragMent.pushPormpt(cid, promptCount, type);
			} else if (action.equals(Constants.UPDECIRNAME)) {// 更改圈子名称
				String cid = intent.getStringExtra("cid");
				String cirName = intent.getStringExtra("cirName");
				homeFragMent.upDateCirName(cid, cirName);
			} else if (action.equals(Constants.MYCARD_PROMPT)) {// 个人名片提醒
				boolean prot = intent.getBooleanExtra("prompt", false);
				menuFragMent.setMyCardPrompt(prot);
				prompt = menuFragMent.messagePrompt;
			} else if (action.equals(Constants.MESSAGE_PROMPT)) {// 私信提醒
				boolean prot = intent.getBooleanExtra("prompt", false);
				menuFragMent.setMessagePrompt(prot);
				prompt = menuFragMent.myCardPrompt;

			}
		}
	};

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
			myCardFragMent.cardShow.setName(name);
			Bitmap bmp = bundle.getParcelable("avatar");
			if (bmp != null) {
				myCardFragMent.cardShow.setAvatar(bmp, avatatPath);
				menuFragMent.setEidtAvatar(bmp);
			}
			if (basicList != null && contactList != null && socialList != null
					&& addressList != null && eduList != null
					&& workList != null)
				myCardFragMent.cardShow.notifyData(basicList, contactList,
						socialList, addressList, eduList, workList);

		}

	}

	/**
	 * 百度推送绑定回调
	 */
	@Override
	public void onBind(int errorCode, String content) {
		if (errorCode == 0) {// 0 标示成功 非0失败
			String channelid = "";
			String userid = "";
			try {
				JSONObject jsonContent = new JSONObject(content);
				JSONObject params = jsonContent
						.getJSONObject("response_params");
				channelid = params.getString("channel_id");
				userid = params.getString("user_id");
				SharedUtils.setChannelID(channelid);
				SharedUtils.setUserID(userid);
				Utils.showToast("推送服务绑定成功" + errorCode);
				SetClientInfo task = new SetClientInfo(new ClientCallBack() {// 设置属性接口回调
							public void afterLogin(String result) {
								isSetSuccess(result);
							}
						});
				task.execute();
			} catch (JSONException e) {
			}
		} else {
			Utils.showToast("推送服务绑定错误" + errorCode);
		}
	}

}
