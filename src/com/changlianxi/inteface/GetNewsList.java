package com.changlianxi.inteface;

import java.util.List;

import com.changlianxi.modle.NewsModle;

/**
 * 获取动态列表接口回调
 * 
 * @author teeker_bin
 * 
 */
public interface GetNewsList {
	void getNewsList(List<NewsModle> listModle);
}
