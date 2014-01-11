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

import com.changlianxi.R;
import com.changlianxi.activity.CLXApplication;
import com.changlianxi.modle.NewsComments;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NewsCommentsListAdapter extends BaseAdapter {
	private Context mContext;
	private List<NewsComments> listModle;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;

	public NewsCommentsListAdapter(Context context, List<NewsComments> modle) {
		this.mContext = context;
		this.listModle = modle;
		imageLoader = CLXApplication.getImageLoader();
		options = CLXApplication.getUserOptions();
	}

	@Override
	public int getCount() {
		return listModle.size();
	}

	public void setDate(List<NewsComments> modle) {
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
					R.layout.news_comments_list, null);
			holder = new ViewHolder();
			holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.layParent = (LinearLayout) convertView
					.findViewById(R.id.parent);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.content.setText(listModle.get(position).getContent());
		holder.name.setText(listModle.get(position).getName());
		holder.time.setText(listModle.get(position).getTime());
		imageLoader.displayImage(listModle.get(position).getAvatar(),
				holder.avatar, options);
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
		TextView time;
		TextView content;
		LinearLayout layParent;
	}
}
