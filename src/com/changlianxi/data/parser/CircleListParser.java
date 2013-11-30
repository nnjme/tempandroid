package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleList;
import com.changlianxi.data.request.Result;

public class CircleListParser implements IParser {

	@Override
	public Result parse(JSONObject jsonObj) throws Exception {
		if (jsonObj == null) {
			return Result.defContentErrorResult();
		}
		JSONArray jsonArr = jsonObj.getJSONArray("circles");
		if (jsonArr == null) {
			return Result.defContentErrorResult();
		}

		List<Circle> circles = new ArrayList<Circle>();
		for (int i = 0; i < jsonArr.length(); i++) {
			JSONObject obj = (JSONObject) jsonArr.opt(i);
			String id = obj.getString("id");
			String logo = obj.getString("logo");
			String name = obj.getString("name");
			String isNew = obj.getString("is_new");
			// TODO inviter uid

			Circle c = new Circle(id, name, "", logo); // TODO description //
														// TODO logo size
			c.setNew(isNew.equals("1"));
			c.setStatus(Status.OLD);
			circles.add(c);
		}

		Result ret = new Result();
		ret.setData(new CircleList(circles)); // TODO other info of CircleList
		return ret;
	}

}
