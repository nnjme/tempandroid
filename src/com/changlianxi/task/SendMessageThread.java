package com.changlianxi.task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import com.changlianxi.inteface.SendMessageAndChatCallBack;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;

/**
 * 私信和聊天线程
 * 
 * @author teeker_bin
 * 
 */
public class SendMessageThread extends Thread {
	private Queue<HashMap<String, Object>> queueMap = new LinkedList<HashMap<String, Object>>();// 用于发送私信的队列
	private boolean running = true;
	private String url = "";
	private SendMessageAndChatCallBack callBack;

	public SendMessageThread(String url) {
		this.url = url;
	}

	public void setMessageAndChatCallBack(SendMessageAndChatCallBack callBack) {
		this.callBack = callBack;
	}

	public void setRun(boolean runing) {
		this.running = runing;
	}

	public void setQueueMap(Queue<HashMap<String, Object>> queueMap) {
		this.queueMap = queueMap;
	}

	public void run() {
		while (running) {
			if (queueMap.size() > 0) {
				HashMap<String, Object> map = queueMap.poll();
				String result = HttpUrlHelper.postData(map, url);
				Logger.debug(this, result);
				callBack.getReturnStrAndMid(result);
			}
		}
	}
}
