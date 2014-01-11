package com.changlianxi.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.R;
import com.changlianxi.modle.MessagesListModle;
import com.changlianxi.util.DateUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MessageListAdapter extends BaseAdapter {
	private Context mContext;
	private List<MessagesListModle> listModle;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;

	public MessageListAdapter(Context context, List<MessagesListModle> modle) {
		this.mContext = context;
		this.listModle = modle;
		imageLoader = CLXApplication.getImageLoader();
		options = CLXApplication.getUserOptions();
	}

	@Override
	public int getCount() {
		return listModle.size();
	}

	public void setData(List<MessagesListModle> modle) {
		this.listModle = modle;
		notifyDataSetChanged();
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
			holder.layParent = (LinearLayout) convertView
					.findViewById(R.id.parent);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.content.setText(listModle.get(position).getMsg());
		int count = listModle.get(position).getNewCount();
		if (count > 0) {
			holder.count.setVisibility(View.VISIBLE);
			holder.count.setText(count + "");
		} else {
			holder.count.setVisibility(View.GONE);
		}
		holder.cirName.setText(listModle.get(position).getCirName());
		holder.name.setText(listModle.get(position).getUserName());
		holder.time.setText(DateUtils.interceptDateStr(listModle.get(position)
				.getTime(), "MM-dd"));
		String avatarPath = listModle.get(position).getAvatar();

		if (!avatarPath.startsWith("http")) {
			holder.avatar.setImageResource(R.drawable.head_bg);
		} else {
			imageLoader.displayImage(listModle.get(position).getAvatar(),
					holder.avatar, options);
		}
		if (position % 2 == 0) {
			holder.layParent.setBackgroundColor(Color.WHITE);
		} else {
			holder.layParent.setBackgroundColor(mContext.getResources()
					.getColor(R.color.f6));
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
		LinearLayout layParent;
	}
}
