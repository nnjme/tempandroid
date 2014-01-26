package com.changlianxi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.util.Constants;

/**
 * Êý¾Ý¿âÀà
 * 
 * @author teeker_bin
 * 
 */
public class DataBase extends SQLiteOpenHelper {
	private static DataBase instance;

	public DataBase(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// onCreate(getWritableDatabase());
	}

	private DataBase(Context context, String dbName, int version) {
		super(context, dbName, null, version);
		// onCreate(getWritableDatabase());
	}

	public static DataBase getInstance() {
		if (instance == null) {
			instance = new DataBase(CLXApplication.getInstance(), "clx", 1);
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table IF NOT EXISTS "
				+ Constants.CIRCLELIST_TABLE
				+ "( _id integer PRIMARY KEY AUTOINCREMENT ,cirID varchar,cirName varchar, cirImg varchar,isNew varchar,newMemberCount integer,newGrowthCount integer,newChatCount integer,newDynamicCount integer,newCommentCount integer, promptCount integer)");
		db.execSQL("create table IF NOT EXISTS "
				+ Constants.MYDETAIL
				+ "( _id integer PRIMARY KEY AUTOINCREMENT ,tid varchar,pid varchar,uid varchar,name varchar,key varchar, value varchar,startDate varchar,endDate varchar,changed varchar)");
		db.execSQL("create table IF NOT EXISTS "
				+ Constants.CHATLIST_TABLE_NAME
				+ "( _id integer PRIMARY KEY AUTOINCREMENT ,cid varchar,name varchar,avatarURL varchar,time varchar, content varchar ,self varchar,type integer)");
		db.execSQL("create table IF NOT EXISTS "
				+ Constants.MESSAGELIST_TABLE_NAME
				+ "( _id integer PRIMARY KEY AUTOINCREMENT ,keyID varchar,ruid varchar,cid varchar,name varchar,avatarURL varchar,time varchar, content varchar ,self varchar,type integer)");
		db.execSQL("create table IF NOT EXISTS "
				+ Constants.USERDETAIL
				+ "( _id integer PRIMARY KEY AUTOINCREMENT ,cid varchar,tID varchar,personID varchar,key varchar, value varchar,startDate varchar,endDate varchar)");
		db.execSQL("create table IF NOT EXISTS "
				+ Constants.CIRCLEDETAIL
				+ "( _id integer PRIMARY KEY AUTOINCREMENT ,cid varchar,cirName varchar,cirIcon varchar,cirDescribe varchar, cirMmembersTotal varchar,cirMembersVerified varchar,creator varchar)");

		db.execSQL("create table IF NOT EXISTS "
				+ Constants.NEWS_TABLE
				+ "( _id integer PRIMARY KEY AUTOINCREMENT ,cid varchar ,newsID varchar, type varchar,user1 varchar,user2 varchar,person2 varchar,time varchar,content varchar,detail varchar,user1Name varchar, user2Name varchar,avatarURL varchar,need_approve varchar)");
		db.execSQL("create table IF NOT EXISTS "
				+ Constants.GROWTH_TABLE
				+ "( _id integer PRIMARY KEY AUTOINCREMENT ,cid varchar ,name varchar, avatar varchar,growthID varchar,uid varchar,content varchar,location varchar,happen varchar, publish varchar,praise integer,comment integer ,isparise ingeger,img varchar)");

		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ Constants.USERLIST_TABLE
				+ " ( _id integer PRIMARY KEY AUTOINCREMENT ,cid varchar,personID varchar,userID varchar,userName varchar, userImg varchar,employer varchar,mobileNum varchar,sortkey varchar,pinyinFir varchar,auth varchar,location varchar)");
		initDB(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	 public void initDB(SQLiteDatabase db) {
	 db.execSQL("create table IF NOT EXISTS " + Const.CIRCLE_TABLE_NAME
	 + "( " + Const.CIRCLE_TABLE_STRUCTURE + " )");
//	 db.execSQL("create table IF NOT EXISTS " + Const.CHAT_TABLE_NAME + "( "
//	 + Const.CHAT_TABLE_STRUCTURE + " )");
	 db.execSQL("create table IF NOT EXISTS " + Const.CIRCLE_ROLE_TABLE_NAME + "(" + Const.CIRCLE_ROLE_TABLE_STRUCTURE +")");
	 db.execSQL("create table IF NOT EXISTS " + Const.CIRCLE_MEMBER_TABLE_NAME + "(" + Const.CIRCLE_MEMBER_TABLE_STRUCTURE +")");
	 db.execSQL("create table IF NOT EXISTS " + Const.TIME_RECORD_TABLE_NAME + "(" + Const.TIME_RECORD_TABLE_STRUCTURE +")");
	 db.execSQL("create table IF NOT EXISTS " + Const.PERSON_DETAIL_TABLE_NAME + "(" + Const.PERSON_DETAIL_TABLE_STRUCTURE +")");
	 }
}

