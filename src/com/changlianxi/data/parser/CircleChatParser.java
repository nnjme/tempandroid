package com.changlianxi.data.parser;

import java.util.Map;

import org.json.JSONObject;

import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.CircleChat;
import com.changlianxi.data.enums.ChatType;
import com.changlianxi.data.request.Result;

public class CircleChatParser implements IParser {

	@Override
	public Result parse(Map<String, Object> params, JSONObject jsonObj)
			throws Exception {
		if (jsonObj == null) {
			return Result.defContentErrorResult();
		}

		int cid = (Integer) params.get("cid");
		int uid = (Integer) params.get("uid");
		String content = (String) params.get("content");
		ChatType type = ChatType.convert((String) params.get("type"));

		if (!jsonObj.has("cmid") || !jsonObj.has("time")) {
			return Result.defContentErrorResult();
		}
		String time = jsonObj.getString("time"); // TODO server support
		int cmid = jsonObj.getInt("cmid");
		if (time == null || cmid == 0) {
			return Result.defContentErrorResult();
		}

		CircleChat chat = new CircleChat(cmid, cid, uid, content);
		chat.setTime(time);
		chat.setType(type);
		if (type == ChatType.TYPE_IMAGE) {
			if (!jsonObj.has("image")) {
				return Result.defContentErrorResult();
			} else {
				String image = jsonObj.getString("image");
				chat.setContent(image);
			}
		}
		chat.setStatus(Status.NEW);
		Result ret = new Result();
		ret.setData(chat);
		return ret;
	}

}
