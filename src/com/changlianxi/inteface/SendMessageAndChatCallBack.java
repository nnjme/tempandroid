package com.changlianxi.inteface;

/**
 * 发送私信和聊天回调接口
 * 
 * @author teeker_bin
 * 
 */
public interface SendMessageAndChatCallBack {
	/**
	 * 获取返回码和消息id
	 */
	void getReturnStrAndMid(String result);
}
