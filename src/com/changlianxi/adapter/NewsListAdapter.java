package com.changlianxi.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.changlianxi.activity.R;
import com.changlianxi.modle.NewsModle;
import com.changlianxi.util.DateUtils;

public class NewsListAdapter extends BaseAdapter {
	private Context mCotext;
	private List<NewsModle> listModle;

	public NewsListAdapter(Context context, List<NewsModle> listModle) {
		this.mCotext = context;
		this.listModle = listModle;
	}

	@Override
	public int getCount() {
		return listModle.size();
	}

	public void setData(List<NewsModle> listModle) {
		this.listModle = listModle;
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
		ViewHolder1 holder1 = null;
		if (convertView == null) {
			if (listModle.get(position).getType().equals("TYPE_EDIT_PERSON")) {
				convertView = LayoutInflater.from(mCotext).inflate(
						R.layout.news_edit, null);
				holder1 = new ViewHolder1();
				holder1.content = (TextView) convertView
						.findViewById(R.id.content);
				holder1.time = (TextView) convertView.findViewById(R.id.time);

				convertView.setTag(holder1);
			} else {
				convertView = LayoutInflater.from(mCotext).inflate(
						R.layout.news_list_item, null);
				holder = new ViewHolder();
				holder.content = (TextView) convertView
						.findViewById(R.id.content);
				holder.time = (TextView) convertView.findViewById(R.id.time);

				convertView.setTag(holder);
			}

		} else {
			if (listModle.get(position).getType().equals("TYPE_EDIT_PERSON")) {
				holder1 = (ViewHolder1) convertView.getTag();
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
		}
		if (listModle.get(position).getType().equals("TYPE_EDIT_PERSON")) {
			holder1.content.setText(listModle.get(position).getContent() + " "
					+ listModle.get(position).getDetail());
			holder1.time.setText(DateUtils.interceptDateStr(
					listModle.get(position).getCreatedTime(), "yyyy-MM-dd"));
		} else {
			holder.content.setText(listModle.get(position).getContent() + " "
					+ listModle.get(position).getDetail());
			holder.time.setText(DateUtils.interceptDateStr(
					listModle.get(position).getCreatedTime(), "yyyy-MM-dd"));
		}

		return convertView;
	}

	class ViewHolder {
		TextView content;
		TextView time;
	}

	class ViewHolder1 {
		TextView content;
		TextView time;
	}
}
