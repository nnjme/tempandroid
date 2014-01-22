package com.changlianxi.data;


public abstract class AbstractData implements IData {

	public static enum Status {
		NEW, OLD, UPDATE, DEL
	};

	protected Status status = Status.NEW;

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
