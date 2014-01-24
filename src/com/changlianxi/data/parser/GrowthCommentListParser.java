package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.GrowthComment;
import com.changlianxi.data.GrowthCommentList;
import com.changlianxi.data.request.Result;
import com.changlianxi.util.DateUtils;

public class GrowthCommentListParser implements IParser {

	@Override
	public Result parse(Map<String, Object> params, JSONObject jsonObj)
			throws Exception {
		if (jsonObj == null) {
			return Result.defContentErrorResult();
		}

		int cid = (Integer) params.get("cid");
		int gid = jsonObj.getInt("gid");
		int total = jsonObj.getInt("total");
		int requestTime = jsonObj.getInt("current");
		JSONArray jsonArr = jsonObj.getJSONArray("comments");
		if (jsonArr == null) {
			return Result.defContentErrorResult();
		}

		List<GrowthComment> comments = new ArrayList<GrowthComment>();
		long start = 0L, end = 0L;
		for (int i = 0; i < jsonArr.length(); i++) {
			JSONObject obj = (JSONObject) jsonArr.opt(i);
			// comment info
			int gcid = obj.getInt("id");
			int uid = obj.getInt("uid");
			String content = obj.getString("content");
			int replyid = obj.getInt("replyid");
			String time = obj.getString("time");

			GrowthComment comment = new GrowthComment(gid, gcid, uid, content);
			comment.setTime(time);
			comment.setReplyid(replyid);
			comments.add(comment);

			long tmp = DateUtils.convertToDate(time);
			if (end == 0 || tmp > end) {
				end = tmp;
			}
			if (start == 0 || tmp < start) {
				tmp = start;
			}
		}

		GrowthCommentList gml = new GrowthCommentList(cid, gid);
		gml.setComments(comments);
		gml.setLastReqTime(requestTime);
		gml.setTotal(total);
		gml.setStartTime(start);
		gml.setEndTime(end);
		Result ret = new Result();
		ret.setData(gml);
		return ret;
	}

}
