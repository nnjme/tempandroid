package com.changlianxi.data.parser;

import java.util.Map;

import org.json.JSONObject;

import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.Chat;
import com.changlianxi.data.request.Result;
import com.changlianxi.util.DateUtils;

public class ChatParser implements IParser {

	@Override
	public Result parse(Map<String, Object> params, JSONObject jsonObj) throws Exception {
		if (jsonObj == null) {
			return Result.defContentErrorResult();
		}

		JSONObject jsonChat = jsonObj.getJSONObject("chat");
		String cid = jsonObj.getString("cid");
		if (jsonChat == null || cid == null) {
			return Result.defContentErrorResult();
		}

		String id = jsonChat.getString("id");
		String type = jsonChat.getString("type");
		String sender = jsonChat.getString("sender");
		String content = jsonChat.getString("content");
		String timeStr = jsonChat.getString("time");
		long time = DateUtils.convertToDate(timeStr);

		Chat c = new Chat(id, cid, Chat.str2Type.get(type), sender, content,
				time);
		c.setStatus(Status.NEW);
		Result ret = new Result();
		ret.setData(c);
		return ret;
	}

}
