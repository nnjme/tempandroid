package com.changlianxi.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.activity.R;
import com.changlianxi.modle.MessagesListModle;

public class MessageListAdapter extends BaseAdapter {
	private Context mContext;
	private List<MessagesListModle> listModle;

	public MessageListAdapter(Context context, List<MessagesListModle> modle) {
		this.mContext = context;
		this.listModle = modle;
	}

	@Override
	public int getCount() {
		return listModle.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.messages_list_item, null);
			holder = new ViewHolder();
			holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.cirName = (TextView) convertView.findViewById(R.id.cirName);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.count = (TextView) convertView.findViewById(R.id.count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		return convertView;
	}

	class ViewHolder {
		ImageView avatar;
		TextView name;
		TextView cirName;
		TextView time;
		TextView content;
		TextView count;
	}
}
