package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.MyCard;
import com.changlianxi.data.PersonDetail;
import com.changlianxi.data.PersonDetailType;
import com.changlianxi.data.request.Result;

public class MyCardDetailParser implements IParser {

	@Override
	public Result parse(Map<String, Object> params, JSONObject jsonObj) throws Exception {
		if (jsonObj == null) {
			return Result.defContentErrorResult();
		}

		if (!jsonObj.has("person") || !jsonObj.has("uid") || !jsonObj.has("pid")) {
			return Result.defContentErrorResult();
		}
		JSONArray jsonPersons = jsonObj.getJSONArray("person");
		int pid = jsonObj.getInt("pid");
		int uid = jsonObj.getInt("uid");
		if (jsonPersons == null || pid == 0) {
			return Result.defContentErrorResult();
		}

		// member properties
		List<PersonDetail> properties = new ArrayList<PersonDetail>();
		for (int i = 0; i < jsonPersons.length(); i++) {
			JSONObject obj = (JSONObject) jsonPersons.opt(i);
			int id = obj.getInt("id");
			String type = obj.getString("type");
			String value = obj.getString("value");
			String start = obj.getString("start");
			String end = obj.getString("end");
			String remark = obj.getString("remark");
			PersonDetailType pType = PersonDetailType.convertToType(type);
			if (pType == PersonDetailType.UNKNOWN) {
				continue;
			}
			PersonDetail p = new PersonDetail(id, 0, pType, value);
			p.setStart(start);
			p.setEnd(end);
			p.setRemark(remark);
			properties.add(p);
		}

		MyCard member = new MyCard(pid, uid);
		member.setDetails(properties);
		Result ret = new Result();
		ret.setData(member);
		return ret;
	}
}
