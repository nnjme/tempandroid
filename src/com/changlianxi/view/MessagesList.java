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
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.changlianxi.activity.R;
import com.changlianxi.adapter.MessageListAdapter;
import com.changlianxi.modle.MessagesListModle;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.view.FlipperLayout.OnOpenListener;

/**
 * 私信列表展示界面
 * 
 * @author teeker_bin
 * 
 */
public class MessagesList implements OnClickListener {
	private Context mContext;
	private View mMessages;
	private OnOpenListener mOnOpenListener;
	private LinearLayout mMenu;
	private ListView listview;
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
		listview = (ListView) mMessages.findViewById(R.id.listView);
		listview.setAdapter(adapter);
	}

	private void setListener() {
		mMenu.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mOnOpenListener != null) {
					mOnOpenListener.open();
				}
			}
		});

	}

	/**
	 * 获取成员列表
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
			map.put("timestamp", 0);
			String result = HttpUrlHelper.postData(map, "/messages/ilist");
			try {
				JSONObject jsonobject = new JSONObject(result);
				JSONArray jsonarray = jsonobject.getJSONArray("messages");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject object = (JSONObject) jsonarray.opt(i);
					MessagesListModle modle = new MessagesListModle();
					String uid = object.getString("uid");
					String mid = object.getString("mid");
					String cid = object.getString("cid");
					String type = object.getString("type");
					String msg = object.getString("msg");
					String time = object.getString("time");
					String newCount = object.getString("new");
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

	}

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		mOnOpenListener = onOpenListener;
	}

	public View getView() {
		return mMessages;
	}

}
