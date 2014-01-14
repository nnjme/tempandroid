package com.changlianxi.db;

public class Const {
	public static final String CIRCLE_TABLE_NAME = "circles";
	public static final String CIRCLE_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
			+ " id varchar, name varchar, description varchar, logo varchar, is_new varchar,"
			+ " creator varchar, created varchar, joinTime varchar, total integer, inviting integer,"
			+ " verified integer, unverified integer, myinvitor varchar";

	public static final String CIRCLE_ROLE_TABLE_NAME = "circle_roles";
	public static final String CIRCLE_ROLE_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
			+ " cid varchar, id varchar, name varchar, count integer";

	public static final String CHAT_TABLE_NAME = "chats";
	public static final String CHAT_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
			+ " chatId varchar, circleId varchar, senderUid varchar, type varchar, content varchar, time integer";

}
