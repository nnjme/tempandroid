package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.CircleMemberList;
import com.changlianxi.data.enums.CircleMemberState;
import com.changlianxi.data.request.Result;
import com.changlianxi.util.DateUtils;

public class CircleMemberListParser implements IParser {

	@Override
	public Result parse(Map<String, Object> params, JSONObject jsonObj)
			throws Exception {
		if (jsonObj == null) {
			return Result.defContentErrorResult();
		}

		int cid = (Integer) params.get("cid");
		String type = (String) params.get("type");
		int total = jsonObj.getInt("total");
		// int num = jsonObj.getInt("num");
		int requestTime = jsonObj.getInt("current");
		JSONArray jsonArr = jsonObj.getJSONArray("members");
		if (jsonArr == null) {
			return Result.defContentErrorResult();
		}

		List<CircleMember> members = new ArrayList<CircleMember>();
		long start = 0L, end = 0L;
		for (int i = 0; i < jsonArr.length(); i++) {
			JSONObject obj = (JSONObject) jsonArr.opt(i);
			int pid = obj.getInt("id");
			int uid = obj.getInt("uid");
			String name = obj.getString("name");
			String cellphone = obj.getString("cellphone");
			String avatar = obj.getString("avatar");
			String employer = obj.getString("employer");
			String jobtitle = obj.getString("jobtitle");
			String time = obj.getString("time");
			String location = obj.getString("location");
			int roleId = obj.getInt("role_id");
			String state = obj.getString("state");

			CircleMember m = new CircleMember(cid, pid, uid);
			m.setName(name);
			m.setCellphone(cellphone);
			m.setAvatar(avatar);
			m.setEmployer(employer);
			m.setJobtitle(jobtitle);
			m.setLocation(location);
			m.setRoleId(roleId);
			m.setState(CircleMemberState.convert(state));

			if ("mod".equals(type)) {
				m.setStatus(Status.UPDATE);
				m.setLastModTime(time);
			} else if ("del".equals(type)) {
				m.setStatus(Status.DEL);
				m.setLeaveTime(time);
			} else {
				m.setStatus(Status.NEW);
				m.setJoinTime(time);
			}
			members.add(m);
			
			long tmp = DateUtils.convertToDate(time);
			if (end == 0 || tmp > end) {
				end = tmp;
			}
			if (start == 0 || tmp < start) {
				tmp = start;
			}
		}

		CircleMemberList cml = new CircleMemberList(cid);
		cml.setMembers(members);
		if ("mod".equals(type)) {
			cml.setLastModReqTime(requestTime);
		} else if ("del".equals(type)) {
			cml.setLastDelReqTime(requestTime);
		} else {
			cml.setLastNewReqTime(requestTime);
		}
		cml.setTotal(total);
		cml.setStartTime(start);
		cml.setEndTime(end);
		Result ret = new Result();
		ret.setData(cml);
		return ret;
	}

}
