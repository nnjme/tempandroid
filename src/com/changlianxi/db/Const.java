package com.changlianxi.db;



public class Const {
	public static final String CIRCLE_TABLE_NAME = "circles";
	public static final String CIRCLE_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
			+ " id integer, name varchar, description varchar, logo varchar, isNew varchar,"
			+ " creator integer, myInvitor integer, created varchar, joinTime varchar, total integer,"
			+ " inviting integer, verified integer, unverified integer";

	public static final String CIRCLE_ROLE_TABLE_NAME = "circle_roles";
	public static final String CIRCLE_ROLE_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
			+ " cid integer, id integer, name varchar, count integer";

	public static final String CIRCLE_MEMBER_TABLE_NAME = "circle_members";
	public static final String CIRCLE_MEMBER_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
			+ " cid integer, uid integer, pid integer, name varchar, cellphone varchar, location varchar,"
			+ " gendar integer, avatar varchar, birthday varchar, employer varchar, jobtitle varchar,"
			+ " joinTime varchar, lastModTime varchar, leaveTime varchar, roleId integer, state varchar,"
			+ " detailIds varchar,sortkey varchar,pinyinFir varchar,auth varchar";

	public static final String PERSON_DETAIL_TABLE_NAME = "person_details";
	public static final String PERSON_DETAIL_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
			+ " id integer, cid integer, type varchar, value varchar, start varchar, end integer, remark varchar";

	public static final String TIME_RECORD_TABLE_NAME = "time_records";
	public static final String TIME_RECORD_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
			+ " key varchar, subkey varchar, time long";

	public static final String CHAT_TABLE_NAME = "chats";
	public static final String CHAT_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT," // TODO
			+ " chatId varchar, circleId varchar, senderUid varchar, type varchar, content varchar, time integer";

}
