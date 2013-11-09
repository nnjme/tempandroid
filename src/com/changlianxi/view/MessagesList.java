package com.changlianxi.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.changlianxi.activity.MessageActivity;
import com.changlianxi.activity.R;
import com.changlianxi.adapter.MessageListAdapter;
import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.modle.MessagesListModle;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.FlipperLayout.OnOpenListener;
import com.changlianxi.view.MyListView.OnRefreshListener;

/**
 * 私信列表展示界面
 * 
 * @author teeker_bin
 * 
 */
public class MessagesList implements OnClickListener, OnItemClickListener {
	private Context mContext;
	private View mMessages;
	private OnOpenListener mOnOpenListener;
	private LinearLayout mMenu;
	private MyListView listview;
	private List<MessagesListModle> listModle = new ArrayList<MessagesListModle>();
	private MessageListAdapter adapter;

	public MessagesList(Context context) {
		this.mContext = context;
		mMessages = LayoutInflater.from(context).inflate(
				R.layout.activity_messages_list, null);
		adapter = new MessageListAdapter(context, listModle);
		initView();
		setListener();
		new GetMessagetTask().execute();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mMenu = (LinearLayout) mMessages.findViewById(R.id.home_menu);
		listview = (MyListView) mMessages.findViewById(R.id.listView);
		listview.setAdapter(adapter);
	}

	private void setListener() {
		mMenu.setOnClickListener(this);
		listview.setOnItemClickListener(this);
		listview.setCacheColorHint(0);
		listview.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				listModle.clear();
				new GetMessagetTask().execute();
			}
		});
	}

	/**
	 * 获取私信列表
	 * 
	 */
	class GetMessagetTask extends AsyncTask<String, Integer, String> {
		ProgressDialog progressDialog;

		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("token", SharedUtils.getString("token", ""));
			String result = HttpUrlHelper.postData(map, "/messages/ilist");
			try {
				JSONObject jsonobject = new JSONObject(result);
				JSONArray jsonarray = jsonobject.getJSONArray("messages");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject object = (JSONObject) jsonarray.opt(i);
					MessagesListModle modle = new MessagesListModle();
					String avatarPath = "";
					String name = "";
					String uid = object.getString("uid");
					String mid = object.getString("mid");
					String cid = object.getString("cid");
					String type = object.getString("type");
					String msg = object.getString("msg");
					Logger.debug(this, "msg:" + msg);
					String time = object.getString("time");
					String newCount = object.getString("new");
					String cirName = DBUtils.getCircleNameById(cid);
					MemberInfoModle info = DBUtils.selectNameAndImgByID(
							"circle" + cid, uid);
					if (info != null) {
						avatarPath = info.getAvator();
						name = info.getName();
					}
					modle.setAvatar(avatarPath);
					modle.setCirName(cirName);
					modle.setUserName(name);
					modle.setUid(uid);
					modle.setMid(mid);
					modle.setCid(cid);
					modle.setTime(time);
					modle.setType(type);
					modle.setMsg(msg);
					modle.setNewCount(newCount);
					listModle.add(modle);

				}
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			adapter.notifyDataSetChanged();
			listview.onRefreshComplete();

		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			progressDialog = new ProgressDialog(mContext);
			progressDialog.show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_menu:
			if (mOnOpenListener != null) {
				mOnOpenListener.open();
			}
			break;

		default:
			break;
		}
	}

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		mOnOpenListener = onOpenListener;
	}

	public View getView() {
		return mMessages;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int position = arg2 - 1;
		Intent intent = new Intent();
		intent.putExtra("type", "read");// 阅读私信
		intent.putExtra("uid", listModle.get(position).getUid());// 要读私信者的id
		intent.putExtra("cid", listModle.get(position).getCid());// 私信所属圈子ID
		intent.setClass(mContext, MessageActivity.class);
		mContext.startActivity(intent);
	}

}
