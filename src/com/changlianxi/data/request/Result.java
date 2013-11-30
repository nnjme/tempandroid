package com.changlianxi.data.request;

import java.util.HashMap;
import java.util.Map;

import com.changlianxi.data.IData;

public class Result {
	public static enum Status {
		SUCC, FAIL
	};

	public static enum Error {
		NONE, UNKOWN, NETWORK_ERROR, NOT_POST_REQUEST, NEED_MORE_PARAMS, NOT_EXIST_USER, USER_ALREADY_EXIST, WRONG_PASSWORD, FAIL_SEND_AUTH_CODE, DB_SAVE_ERROR, FAIL_VERIFY_AUTH_CODE, FIELD_NOT_EXIST, NOT_EXIST_PERSON, PERSON_ALREADY_IN_CIRCLE, NOT_EXIST_CIRCLE, PERMISSION_DENIED, IMAGE_SAVE_ERROR, CREATOR_CANNOT_QUIT_CIRCLE, CIRCLE_CANNOT_DISSOLVE, REPEAT_OPERATION, TARGET_NOT_EXIST, MSG_DIST_ERROR, CONDITION_NOT_SATISFY, TOKEN_INVALID, OLD_PASSWD_WRONG,
	};

	public static Map<String, Error> str2Error = new HashMap<String, Error>();
	static {
		for (Error err : Error.values()) {
			str2Error.put(err.name(), err);
		}
	}

	private IData data = null;
	
	private String tmp = null;

	private Status status = Status.SUCC;

	private Error err = Error.NONE;

	public Result() {
	}

	public Result(IData data, Status status, Error err) {
		this.data = data;
		this.status = status;
		this.err = err;
	}

	public Result(IData data, String tmp, Status status, Error err) { // TODO
		this.tmp = tmp;
		this.status = status;
		this.err = err;
	}

	public IData getData() {
		return data;
	}

	public void setData(IData data) {
		this.data = data;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Error getErr() {
		return err;
	}

	public void setErr(Error err) {
		this.err = err;
	}

	public void setErr(String err) {
		if (!Result.str2Error.containsKey(err)) {
			this.err = Error.UNKOWN;
		} else {
			this.err = Result.str2Error.get(err);
		}
	}

	@Override
	public String toString() { // TODO
		return "Result [data=" + data + ", tmp=" + tmp + ", status=" + status + ", err=" + err
				+ "]";
	}

	public static Result defContentErrorResult() {
		return new Result(null, Status.FAIL, Error.NONE); // TODO data // TODO
															// error
	}

}
