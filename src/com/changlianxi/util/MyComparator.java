package com.changlianxi.util;

import java.util.Comparator;

import com.changlianxi.modle.MemberModle;

 /**
 * �Զ������Ƚ���
 * @author teeker_bin
 *
 */
public class MyComparator implements Comparator<Object> {

	@Override
	public int compare(Object ob1, Object ob2) {
		MemberModle modle1 = (MemberModle) ob1;
		MemberModle modle2 = (MemberModle) ob2;
		String str1 = modle1.getSort_key();
		String str2 = modle2.getSort_key();
		return str1.compareTo(str2);
	}

}
