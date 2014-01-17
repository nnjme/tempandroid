package com.changlianxi.task;

import java.util.ArrayList;
import java.util.List;

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleList;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.Utils;

import android.content.ContentValues;
import android.os.AsyncTask;

public class CircleListTask extends AsyncTask<List<Circle>, Void, RetError>{

	private GetCircleList callBack;
	private List<Circle> circles;
	private CircleList circleList = null; // TODO
	

	public void setTaskCallBack(GetCircleList callBack) {
		this.callBack = callBack;
	}

	// 可变长的输入参数，与AsyncTask.exucute()对应
	@Override
	protected RetError doInBackground(List<Circle>... params) {
		if (isCancelled() || params == null) {
			return null;
		}
		circles = params[0];
		circleList = new CircleList(circles);
		circleList.read(DBUtils.db);
		RetError retError = circleList.refresh(CLXApplication.circleListLastRefreshTime);
		circleList.write(DBUtils.db);
		
		
//		// 你要执行的方法
//		try {
//			JSONObject jsonobject = new JSONObject(result);
//			rt = jsonobject.getString("rt");
//			if (!rt.equals("1")) {
//				errorCode = jsonobject.getString("err");
//				return null;
//			}
//			JSONArray jsonarray = jsonobject.getJSONArray("circles");
//			if (jsonarray != null) {
//				DBUtils.clearTableData("circlelist");// 清空本地表 保存最新数据
//			}
//			for (int i = 0; i < jsonarray.length(); i++) {
//				JSONObject object = (JSONObject) jsonarray.opt(i);
//				CircleModle modle = new CircleModle();
//				String id = object.getString("id");
//				String logo = object.getString("logo");
//				String name = object.getString("name");
//				String inviter = object.getString("inviter");
//				String isNew = object.getString("is_new");
//				if (isNew.equals("1")) {
//					modle.setNew(true);
//				} else {
//					modle.setNew(false);
//				}
//				modle.setInviterID(inviter);
//				modle.setCirID(id);
//				modle.setCirIcon(StringUtils.JoinString(logo, "_200x200"));
//				modle.setCirName(name);
//				serverListModle.add(modle);
//				
//				insertData(id, name, StringUtils.JoinString(logo, "_200x200"),
//						String.valueOf(isNew));
//				// DBUtils.creatTable("circle" + id);
//				//获取圈子成员列表
//				GetCircleUserTask task = new GetCircleUserTask(id);
//				task.execute();
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//			return null;
//		}
		
		return retError;
	}

	@Override
	protected void onPostExecute(RetError result) {
		
		if(result == null)
			return;
		// 任务结束
		if (result != RetError.NONE) {
			Utils.showToast(ErrorCodeUtil.convertToChines(result.name()));
			return;
		}
		CLXApplication.circleListLastRefreshTime = System.currentTimeMillis();
//		callBack.getCircleList(serverListModle);
		callBack.getCircleList(circles);
		return;
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理
		
	}

	/**
	 * 插入数据库
	 * 
	 * @param name
	 * @param num
	 */
	private void insertData(String id, String name, String img, String status) {
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("cirID", id);
		values.put("cirName", name);
		values.put("cirImg", img);
		values.put("isNew", status);
		DBUtils.insertData("circlelist", values);
	}

	public interface GetCircleList {
		//void getCircleList(List<CircleModle> listModle);
		void getCircleList(List<Circle> circles);
	}

}
