package com.changlianxi.data.parser;

import org.json.JSONObject;

import com.changlianxi.data.request.Result;

public interface IParser {

	public Result parse(JSONObject jsonObj) throws Exception;
}
