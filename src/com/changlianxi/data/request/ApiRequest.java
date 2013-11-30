package com.changlianxi.data.request;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.changlianxi.data.ChatList;
import com.changlianxi.data.parser.ChatListParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.request.Result.Status;
import com.changlianxi.db.DataBase;
import com.changlianxi.util.HttpUrlHelper;

public class ApiRequest {

	public static Result request(String url, Map<String, Object> params, IParser parser) { // TODO name, other request
		String httpResult = HttpUrlHelper.postData(params, url); // TODO network problem
		
		try {
			JSONObject jsonObj = new JSONObject(httpResult);
			String rt = jsonObj.getString("rt");
			if (!rt.equals("1")) {
				String err = jsonObj.getString("err");
				Result ret = new Result();
				ret.setStatus(Status.FAIL);
				ret.setErr(err);
				
				return ret;
			}
			
			Result ret = parser.parse(jsonObj);
			return ret;
		} catch (Exception e) {
			e.printStackTrace(); // TODO
			return Result.defContentErrorResult();
		}
	}
	
	public static void main(String [] args) {
		IParser parser = new ChatListParser();
		String url = "/chats/ilist";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", "");
		params.put("cid", "");
		params.put("token", ""); // TODO start, end
		Result ret = ApiRequest.request(url, params, parser);
		
		if (ret.getStatus() == Status.SUCC) {
			ChatList chatList = (ChatList) ret.getData();
			chatList.write(DataBase.getInstance().getWritableDatabase());
		}
	}
	
}
