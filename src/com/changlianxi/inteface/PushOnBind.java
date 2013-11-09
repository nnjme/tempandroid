package com.changlianxi.inteface;

/**
 * 百度推送绑定
 * 
 * @author teeker_bin
 * 
 */
public interface PushOnBind {
	void onBind(int errorCode, String content);
}
