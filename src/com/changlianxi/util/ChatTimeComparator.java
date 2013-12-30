package com.changlianxi.util;

import java.util.Comparator;
import java.util.Date;

import com.changlianxi.modle.MessageModle;

/**
 * 聊天时间排序
 * 
 * @author teeker_bin
 * 
 */
public class ChatTimeComparator implements Comparator<Object> {

	@Override
	public int compare(Object ob1, Object ob2) {
		MessageModle modle1 = (MessageModle) ob1;
		MessageModle modle2 = (MessageModle) ob2;
		long str1 = DateUtils.convertToDate(modle1.getTime());
		long str2 = DateUtils.convertToDate(modle2.getTime());
		Date acceptTime1 = new Date(str1);
		Date acceptTime2 = new Date(str2);
		// 对日期字段进行升序，如果欲降序可采用before方法
		if (acceptTime1.after(acceptTime2))
			return 1;
		return -1;
	}

}
