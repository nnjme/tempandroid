package com.changlianxi.inteface;

import java.util.List;

import com.changlianxi.modle.Info;

public interface ChangeView1 {
	public void toEdidView(String strName, String avatarURL,
			List<String> groupkey, List<Info> basicList, List<Info> socialList,
			List<Info> contactList, List<Info> addressList,
			List<Info> otherList, List<Info> eduList, List<Info> workList);
}
