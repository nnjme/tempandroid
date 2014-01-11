package com.changlianxi.data.parser;

import org.json.JSONObject;

import com.changlianxi.data.request.Result;
import com.changlianxi.data.request.SimpleResult;

public class SimpleParser implements IParser {

	@Override
	public Result parse(JSONObject jsonObj) throws Exception {
		if (jsonObj == null) {
			return Result.defContentErrorResult();
		}

		return new SimpleResult();
	}

}
