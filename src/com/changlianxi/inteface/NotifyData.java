package com.changlianxi.inteface;

import java.util.List;

import com.changlianxi.modle.Info;

public interface NotifyData {
	/**
	 * 用来更新资料显示界面
	 * 
	 * @param data
	 * @param infoType
	 */
	public void refush(List<Info> data, int infoType);
}
