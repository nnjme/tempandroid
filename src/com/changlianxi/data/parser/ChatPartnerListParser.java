package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.ChatPartner;
import com.changlianxi.data.ChatPartnerList;
import com.changlianxi.data.enums.ChatType;
import com.changlianxi.data.request.Result;

public class ChatPartnerListParser implements IParser {

	@Override
	public Result parse(Map<String, Object> params, JSONObject jsonObj) throws Exception {
		if (jsonObj == null) {
			return Result.defContentErrorResult();
		}

		int total = jsonObj.getInt("total");
		int requestTime = jsonObj.getInt("current");
		JSONArray jsonArr = jsonObj.getJSONArray("messages");
		if (jsonArr == null) {
			return Result.defContentErrorResult();
		}

		List<ChatPartner> partners = new ArrayList<ChatPartner>();
		for (int i = jsonArr.length() - 1; i > 0; i--) {
			JSONObject obj = (JSONObject) jsonArr.opt(i);
			int partnerId = obj.getInt("uid");
			int chatId = obj.getInt("mid");
			int cid = obj.getInt("cid");
			String type = obj.getString("type");
			String content = obj.getString("msg");
			String time = obj.getString("time");
			int unReadCnt = obj.getInt("new");

			ChatPartner partner = new ChatPartner(cid, partnerId, chatId, content);
			partner.setType(ChatType.convert(type));
			partner.setTime(time);
			partner.setUnReadCnt(unReadCnt);
			partner.setStatus(Status.NEW);
			
			partners.add(partner);
		}
		
		ChatPartnerList pcpl = new ChatPartnerList();
		pcpl.setPartners(partners);
		pcpl.setLastReqTime(requestTime);
		pcpl.setTotal(total);
		Result ret = new Result();
		ret.setData(pcpl);
		return ret;
	}

}
