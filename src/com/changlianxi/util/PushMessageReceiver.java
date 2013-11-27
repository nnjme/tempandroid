package com.changlianxi.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.baidu.android.pushservice.PushConstants;
import com.changlianxi.activity.CLXApplication;
import com.changlianxi.activity.CircleActivity;
import com.changlianxi.activity.R;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.PushChat;
import com.changlianxi.inteface.PushMessages;
import com.changlianxi.inteface.PushOnBind;
import com.changlianxi.modle.MessageModle;
import com.changlianxi.view.Home;

/**
 * Push消息处理receiver
 */
public class PushMessageReceiver extends BroadcastReceiver {
	public static PushMessages pushMessage;
	public static PushChat pushChat;
	private static PushOnBind pushBind;
	public static int mNewNum = 0;// 通知栏新消息条目，我只是用了一个全局变量，
	public static final int NOTIFY_ID = 0x000;

	/**
	 * @param context
	 *            Context
	 * @param intent
	 *            接收的intent
	 */
	@Override
	public void onReceive(final Context context, Intent intent) {

		if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
			// 获取消息内容
			String message = intent.getExtras().getString(
					PushConstants.EXTRA_PUSH_MESSAGE_STRING);
			// 消息的用户自定义内容读取方式
			resolutionJson(message);
			// 自定义内容的json串

		} else if (intent.getAction().equals(PushConstants.ACTION_RECEIVE)) {
			// 处理绑定等方法的返回数据
			// PushManager.startWork()的返回值通过PushConstants.METHOD_BIND得到
			// 获取方法
			final String method = intent
					.getStringExtra(PushConstants.EXTRA_METHOD);
			// 方法返回错误码。若绑定返回错误（非0），则应用将不能正常接收消息。
			// 绑定失败的原因有多种，如网络原因，或access token过期。
			// 请不要在出错时进行简单的startWork调用，这有可能导致死循环。
			// 可以通过限制重试次数，或者在其他时机重新调用来解决。
			int errorCode = intent.getIntExtra(PushConstants.EXTRA_ERROR_CODE,
					PushConstants.ERROR_SUCCESS);
			String content = "";
			if (intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT) != null) {
				// 返回内容
				content = new String(
						intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));
			}
			System.out.println("content:" + content);
			pushBind.onBind(errorCode, content);
			// 可选。通知用户点击事件处理
		} else if (intent.getAction().equals(// 通知
				PushConstants.ACTION_RECEIVER_NOTIFICATION_CLICK)) {
			System.out.println("通知");
			// 自定义内容的json串

		}
	}

	/**
	 * 解析接受的json字符 确定是聊天信息还是私信信息
	 * 
	 * @param strJson
	 */
	private void resolutionJson(String strJson) {
		try {
			JSONObject json = new JSONObject(strJson);
			String type = json.getString("t");
			if (type.equals("CHAT")) {
				if (SharedUtils.getBoolean("isBackHome", false)) {
					showNotify(strJson);// 后台运行时提醒
				}
				if (pushChat == null) {
					CLXApplication.saveChatModle(strJson);
					return;
				}
				pushChat.getPushChat(strJson);
			} else if (type.equals("MESSAGE")) {
				if (pushMessage != null) {
					pushMessage.getPushMessages(strJson);
					Home.imgPromte.setVisibility(View.VISIBLE);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showNotify(String message) {
		MessageModle modle = Utils.getChatModle(message);
		if (modle == null) {// 自己发送的消息不谈
			return;
		}
		CLXApplication.getInstance().getMediaPlayer().start();
		mNewNum++;
		// 更新通知栏
		CLXApplication application = CLXApplication.getInstance();
		int icon = R.drawable.app_icon_1;
		long when = System.currentTimeMillis();
		String circleName = DBUtils.getCircleNameById(modle.getCid());
		Notification notification = new Notification(icon, circleName, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		// 设置默认声音
		// notification.defaults |= Notification.DEFAULT_SOUND;
		// 设定震动(需加VIBRATE权限)
		// notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.contentView = null;
		Intent intent = new Intent(application, CircleActivity.class);
		intent.putExtra("cirID", modle.getCid());
		intent.putExtra("name", circleName);
		intent.putExtra("type", "push");// 从推送跳转
		PendingIntent contentIntent = PendingIntent.getActivity(application, 0,
				intent, 0);
		notification.setLatestEventInfo(CLXApplication.getInstance(),
				(CharSequence) modle.getName() + " (" + mNewNum + "条新消息)",
				(CharSequence) modle.getContent(), contentIntent);

		application.getNotificationManager().notify(NOTIFY_ID, notification);// 通知一下才会生效哦
	}

	public static void setPushMessageCallBack(PushMessages push) {
		pushMessage = push;
	}

	public static void setPushChatCallBack(PushChat push) {
		pushChat = push;
	}

	public static void setPushOnBind(PushOnBind bind) {
		pushBind = bind;
	}

}
