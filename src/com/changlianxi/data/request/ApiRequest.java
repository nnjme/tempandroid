package com.changlianxi.data.request;

import java.util.Map;

import org.json.JSONObject;

import com.changlianxi.data.Global;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.util.HttpUrlHelper;

public class ApiRequest {

	public static Result request(String url, Map<String, Object> params,
			IParser parser) {
		String httpResult = HttpUrlHelper.postData(params, url); // TODO network
																	// problem

		try {
			JSONObject jsonObj = new JSONObject(httpResult);
			String rt = jsonObj.getString("rt");
			if (!rt.equals("1")) {
				String err = jsonObj.getString("err");
				Result ret = new Result();
				ret.setStatus(RetStatus.FAIL);
				ret.setErr(err);

				return ret;
			}

			Result ret = parser.parse(params, jsonObj);
			return ret;
		} catch (Exception e) {
			e.printStackTrace(); // TODO log
			return Result.defContentErrorResult();
		}
	}

	public static Result requestWithToken(String url, String uid, String token,
			Map<String, Object> params, IParser parser) {
		params.put("uid", uid);
		params.put("token", token);

		return request(url, params, parser);
	}

	public static Result requestWithToken(String url,
			Map<String, Object> params, IParser parser) {
		return requestWithToken(url, Global.getUid(), Global.getUserToken(),
				params, parser);
	}

}
