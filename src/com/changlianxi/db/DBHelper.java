package com.changlianxi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static DBHelper instance;
	private SQLiteDatabase db = null; // TODO

	public DBHelper(Context context, String dbName, int version) {
		super(context, dbName, null, version);
		try {
			db = getWritableDatabase();
		} catch (Exception e) {
			db = getReadableDatabase();
		}
		// mContext = context;
	}

	public DBHelper(Context context) throws SQLiteException {
		super(context, "clx", null, 1); // TODO

	}

	public static synchronized DBHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DBHelper(context);
		}
		return instance;

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table IF NOT EXISTS circlelist( _id integer PRIMARY KEY AUTOINCREMENT ,cirID varchar,cirName varchar, cirImg varchar,isNew varchar)");
		// TODO
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() { // TODO
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

}
