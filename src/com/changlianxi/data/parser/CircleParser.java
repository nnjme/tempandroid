package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleRole;
import com.changlianxi.data.request.Result;

public class CircleParser implements IParser {

	@Override
	public Result parse(JSONObject jsonObj) throws Exception {
		if (jsonObj == null) {
			return Result.defContentErrorResult();
		}

		JSONObject jsonCircle = jsonObj.getJSONObject("circle");
		JSONArray jsonRoles = jsonObj.getJSONArray("roles");
		JSONObject jsonMembersCount = jsonObj.getJSONObject("membersCount");
		String cid = jsonObj.getString("cid");
		if (jsonCircle == null || jsonRoles == null
				|| jsonMembersCount == null || cid == null) {
			return Result.defContentErrorResult();
		}

		// circle basic info
		String name = jsonCircle.getString("name");
		String logo = jsonCircle.getString("logo");
		String description = jsonCircle.getString("description");
		String creator = jsonCircle.getString("creator");
		String created = jsonCircle.getString("created");
		Circle c = new Circle(cid, name, description, logo);
		c.setCreator(creator);
		c.setCreated(created);
		
		// circle roles
		List<CircleRole> roles = new ArrayList<CircleRole>();
		for (int i = 0; i < jsonRoles.length(); i++) {
			JSONObject obj = (JSONObject) jsonRoles.opt(i);
			String roleId = obj.getString("id");
			String roleName = obj.getString("name");
			int roleCount = obj.getInt("count");
			CircleRole role = new CircleRole(cid, roleId, roleName, roleCount);
			roles.add(role);
		}
		c.setRoles(roles);
		
		// circle member counts
		c.setTotalCnt(jsonMembersCount.getInt("total"));
		c.setInvitingCnt(jsonMembersCount.getInt("inviting"));
		c.setVerifiedCnt(jsonMembersCount.getInt("verified"));
		c.setUnverifiedCnt(jsonMembersCount.getInt("unverified"));
		
		Result ret = new Result();
		ret.setData(c);
		return ret;
	}

}
