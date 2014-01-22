package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.Chat;
import com.changlianxi.data.ChatList;
import com.changlianxi.data.request.Result;
import com.changlianxi.util.DateUtils;

public class ChatListParser implements IParser {

	@Override
	public Result parse(JSONObject jsonObj) throws Exception {
		if (jsonObj == null) {
			return Result.defContentErrorResult();
		}

		JSONArray jsonArr = jsonObj.getJSONArray("chats");
		String cid = jsonObj.getString("cid");
		String start = jsonObj.getString("start");
		String end = jsonObj.getString("ent");
		String current = jsonObj.getString("current");
		if (jsonArr == null || cid == null || start == null || end == null
				|| current == null) {
			return Result.defContentErrorResult();
		}

		List<Chat> chats = new ArrayList<Chat>();
		for (int i = jsonArr.length() - 1; i > 0; i--) {
			JSONObject obj = (JSONObject) jsonArr.opt(i);
			String id = obj.getString("id");
			String type = obj.getString("type");
			String sender = obj.getString("sender");
			String content = obj.getString("content");
			String timeStr = obj.getString("time");
			long time = DateUtils.convertToDate(timeStr);
			Chat c = new Chat(id, cid, Chat.str2Type.get(type), sender,
					content, time);
			c.setStatus(Status.NEW);
			chats.add(c);
		}

		long startTime = 0L, endTime = 0L, requestTime = 0L;
		if (!start.equals("0")) {
			startTime = Long.parseLong(start);
		} else {
			startTime = chats.get(chats.size() - 1).getTime();
		}
		if (!end.equals("0")) {
			endTime = Long.parseLong(end);
		} else {
			endTime = chats.get(0).getTime();
		}
		requestTime = Long.parseLong(current);

		Result ret = new Result();
		ret.setData(new ChatList(cid, startTime, endTime, requestTime, chats));
		return ret;
	}

}
