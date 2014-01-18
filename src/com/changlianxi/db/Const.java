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
			+ " detailIds varchar";

	public static final String PERSON_DETAIL_TABLE_NAME = "person_details";
	public static final String PERSON_DETAIL_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
			+ " id integer, cid integer, type varchar, value varchar, start varchar, end integer, remark varchar";

	public static final String TIME_RECORD_TABLE_NAME = "time_records";
	public static final String TIME_RECORD_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
			+ " key varchar, subkey varchar, time long";
	public static final String TIME_RECORD_KEY_PREFIX_GROWTH = "growth";
	public static final String TIME_RECORD_KEY_PREFIX_CIRCLEMEMBER = "cm";
	public static final String TIME_RECORD_KEY_PREFIX_CIRCLECHAT = "chats";
	public static final String TIME_RECORD_KEY_PREFIX_PARTNERS = "partners";
	
	
	public static final String GROWTH_TABLE_NAME = "growths";
	public static final String GROWTH_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
			+ " id integer, cid integer, publisher integer, content varchar, location varchar, happened integer,"
			+ " published varchar, praiseCnt integer, commentCnt integer, isPraised integer,"
			+ " lastCommentsReqTime long";

	public static final String GROWTH_IMAGE_TABLE_NAME = "growth_images";
	public static final String GROWTH_IMAGE_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
			+ " cid integer, gid integer, imgId integer, img varchar";	

	public static final String GROWTH_COMMENT_TABLE_NAME = "growth_comments";
	public static final String GROWTH_COMMENT_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
			+ " gid integer, gcid integer, uid integer, replyid integer, content varchar, time varchar";	
	
	public static final String CIRCLE_CHAT_TABLE_NAME = "circle_chats";
	public static final String CIRCLE_CHAT_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
			+ " chatId integer, cid integer, sender integer, type varchar, content varchar, time varchar";

	public static final String PERSON_CHAT_TABLE_NAME = "person_chats";
	public static final String PERSON_CHAT_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
			+ " chatId integer, cid integer, partner integer, sender integer, type varchar, content varchar,"
			+ " time varchar, isRead integer";

	public static final String CHAT_PARTNER_TABLE_NAME = "person_chat_partners";
	public static final String CHAT_PARTNER_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
			+ " chatId integer, cid integer, partner integer, type varchar, content varchar, time varchar,"
			+ " unReadCnt integer, lastChatsReqTime long";

}
