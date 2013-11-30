package com.changlianxi.db;

public class Const {
	public static final String CIRCLE_TABLE_NAME = "circles";
	public static final String CIRCLE_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT," +
			" id varchar, name varchar, description varchar, icon varchar, is_new varchar";

	public static final String CHAT_TABLE_NAME = "chats";
	public static final String CHAT_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT," +
			" chatId varchar, circleId varchar, senderUid varchar, type varchar, content varchar, time integer";

}
