package com.changlianxi.data.enums;

import java.util.HashMap;
import java.util.Map;

public enum RetError {
	NONE,
	UNVALID,
	UNKOWN,
	NETWORK_ERROR,
	NOT_POST_REQUEST,
	NEED_MORE_PARAMS,
	NOT_EXIST_USER,
	USER_ALREADY_EXIST,
	WRONG_PASSWORD,
	FAIL_SEND_AUTH_CODE,
	DB_SAVE_ERROR,
	FAIL_VERIFY_AUTH_CODE,
	FIELD_NOT_EXIST,
	NOT_EXIST_PERSON,
	PERSON_ALREADY_IN_CIRCLE,
	NOT_EXIST_CIRCLE,
	PERMISSION_DENIED,
	IMAGE_SAVE_ERROR,
	CREATOR_CANNOT_QUIT_CIRCLE,
	CIRCLE_CANNOT_DISSOLVE,
	REPEAT_OPERATION,
	TARGET_NOT_EXIST,
	MSG_DIST_ERROR,
	CONDITION_NOT_SATISFY,
	TOKEN_INVALID,
	OLD_PASSWD_WRONG;
	
	public static Map<String, RetError> str2Error = new HashMap<String, RetError>();
	static {
		for (RetError err : RetError.values()) {
			str2Error.put(err.name(), err);
		}
	}

	public static RetError convert(String err) {
		if (!str2Error.containsKey(err)) {
			return RetError.UNKOWN;
		} else {
			return str2Error.get(err);
		}
	}

}
