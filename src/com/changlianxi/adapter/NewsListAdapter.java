package com.changlianxi.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
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
		if (convertView == null) {
			convertView = LayoutInflater.from(mCotext).inflate(
					R.layout.news_list_item, null);
			holder = new ViewHolder();
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();

		}
		String type = listModle.get(position).getType();
		String content = listModle.get(position).getContent();
		String user1Name = listModle.get(position).getUser1Name();
		String user2Name = listModle.get(position).getUser2Name();
		if (type.equals("TYPE_NEW_CIRCLE")) {
			holder.content.setText(Html.fromHtml(replaceUser(content,
					user1Name, user2Name)));
			holder.time.setText(DateUtils.interceptDateStr(
					listModle.get(position).getCreatedTime(), "yyyy-MM-dd"));
		} else if (type.equals("TYPE_INVINTING")) {
			holder.content.setText(Html.fromHtml(replaceUser(content,
					user1Name, user2Name)));
			holder.time.setText(DateUtils.interceptDateStr(
					listModle.get(position).getCreatedTime(), "yyyy-MM-dd"));
		} else if (type.equals("TYPE_REFUSE_INVITE")) {
			holder.content.setText(Html.fromHtml(replaceUser(content,
					user1Name, user2Name)));
			holder.time.setText(DateUtils.interceptDateStr(
					listModle.get(position).getCreatedTime(), "yyyy-MM-dd"));
		} else if (type.equals("TYPE_ENTERING")) {
			holder.content.setText(Html.fromHtml(replaceUser(content,
					user1Name, user2Name)));
			holder.time.setText(DateUtils.interceptDateStr(
					listModle.get(position).getCreatedTime(), "yyyy-MM-dd"));
		} else if (type.equals("TYPE_PASS_APPROVE")) {
			holder.content.setText(Html.fromHtml(replaceUser(content,
					user1Name, user2Name)));
			holder.time.setText(DateUtils.interceptDateStr(
					listModle.get(position).getCreatedTime(), "yyyy-MM-dd"));
		} else {
			holder.content.setText(Html.fromHtml(replaceUser(content,
					user1Name, user2Name)));
			holder.time.setText(DateUtils.interceptDateStr(
					listModle.get(position).getCreatedTime(), "yyyy-MM-dd"));
		}
		return convertView;
	}

	private String replaceUser(String content, String user1, String user2) {
		String userName1 = "";
		userName1 = "<font color=\"#ff00ff\">" + user1 + "</font>";
		if (user2.equals("")) {
			return content.replace("[X]", userName1);
		}
		String userName2 = "<font color=\"#ff00ff\">" + user2 + "</font>";
		return content.replace("[X]", userName1).replace("[Y]", userName2);
	}

	class ViewHolder {
		TextView content;
		TextView time;
	}

}
