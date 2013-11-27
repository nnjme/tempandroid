package com.changlianxi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.changlianxi.activity.CLXApplication;

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
		onCreate(getWritableDatabase());
	}

	private DataBase(Context context) {
		super(context, "clx", null, 1);
		onCreate(getWritableDatabase());
	}

	public static DataBase getInstance() {
		if (instance == null) {
			instance = new DataBase(CLXApplication.getInstance());
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table IF NOT EXISTS circlelist( _id integer PRIMARY KEY AUTOINCREMENT ,cirID varchar,cirName varchar, cirImg varchar,isNew varchar)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
