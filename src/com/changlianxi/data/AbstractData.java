package com.changlianxi.data;

import com.changlianxi.db.DataBase;

import android.database.sqlite.SQLiteDatabase;

public abstract class AbstractData implements IData {

	public static enum Status {
		NEW, OLD, UPDATE, DEL
	};

	protected Status status = Status.NEW;

	@Override
	public void read(SQLiteDatabase db) {
		if (!db.isOpen()) {
			db = DataBase.getInstance().getReadableDatabase();
		}
	}

	@Override
	public void write(SQLiteDatabase db) {
		if (!db.isOpen()) {
			db = DataBase.getInstance().getWritableDatabase();
		}
	}

	@Override
	public void update(IData data) {
		return;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
