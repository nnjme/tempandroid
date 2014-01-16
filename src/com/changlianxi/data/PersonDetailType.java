package com.changlianxi.data;

import java.util.HashMap;
import java.util.Map;


public enum PersonDetailType {

	UNKNOWN,
	D_NAME,
	D_GENDAR,
	D_BIRTHDAY,
	D_AVATAR,
	D_EMPLOYER,
	D_JOBTITLE,
	D_CELLPHONE,
	D_MOBILE,
	D_WORK_PHONE,
	D_BIRTH_PLACE,
	D_BLOG,
	D_COLLEGE,
	D_CURRENT_ADDRESS,
	D_FACEBOOK,
	D_GRADE_SCHOOL,
	D_HOME_ADDRESS,
	D_HOME_FAX,
	D_HOME_PAGE,
	D_HOME_PHONE,
	D_JOB,
	D_JUNIOR_COLLEGE,
	D_JUNIOR_SCHOOL,
	D_KINDER_GARTEN,
	D_MARRIAGE,
	D_MASTER_COLLEGE,
	D_OTHER_ADDRESS,
	D_EMAIL,
	D_PERSONAL_EMAIL,
	D_PHD_COLLEGE,
	D_QQ,
	D_QQ_WEIBO,
	D_RENREN,
	D_SENIOR_SCHOOL,
	D_SINA_WEIBO,
	D_SKYPE,
	D_TECHNICAL_SCHOOL,
	D_TWITTER,
	D_WEIXIN,
	D_WORK_ADDRESS,
	D_WORK_EMAIL,
	D_WORK_FAX,
	D_NICKNAME,
	D_REMARK,
	D_ROLE;
	
	public static Map<String, PersonDetailType> s2t = new HashMap<String, PersonDetailType>();
	static {
		for (PersonDetailType err : PersonDetailType.values()) {
			s2t.put(err.name(), err);
		}
	}

	public static PersonDetailType convertToType(String s) {
		if (s2t.containsKey(s)) {
			return s2t.get(s);
		}
		return PersonDetailType.UNKNOWN;
	}

	public static boolean hasTimeRange(PersonDetailType type) {
		return type == D_COLLEGE || type == D_GRADE_SCHOOL
				|| type == D_JUNIOR_COLLEGE || type == D_JUNIOR_SCHOOL
				|| type == D_KINDER_GARTEN || type == D_MASTER_COLLEGE
				|| type == D_PHD_COLLEGE || type == D_SENIOR_SCHOOL
				|| type == D_TECHNICAL_SCHOOL || type == D_JOB;
	}

}
