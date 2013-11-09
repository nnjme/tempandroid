package com.changlianxi.inteface;

import java.util.List;

import com.changlianxi.modle.MessageModle;

/**
 * 获取私信内容回调接口
 * 
 * @author teeker_bin
 * 
 */
public interface GetMessagesCallBack {
	void getMessages(List<MessageModle> listModle);
}
