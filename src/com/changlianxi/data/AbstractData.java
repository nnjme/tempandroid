package com.changlianxi.data;

import android.database.sqlite.SQLiteDatabase;


public abstract class AbstractData implements IData {

	public static enum Status {
		NEW, OLD, UPDATE, DEL
	};

	protected Status status = Status.NEW;

	@Override
	public void read(SQLiteDatabase db) {
		return;
	}
	
	@Override
	public void write(SQLiteDatabase db) {
		return;
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
