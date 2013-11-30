package com.changlianxi.adapter;

import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.activity.R;
import com.changlianxi.adapter.MyAdapter.ViewHolder;
import com.changlianxi.modle.MemberModle;
import com.changlianxi.util.ImageManager;

public class CircleSearchAdapter extends BaseAdapter {
	Context mContext;
	List<MemberModle> listModle;

	public CircleSearchAdapter(Context context, List<MemberModle> listModle) {
		this.mContext = context;
		this.listModle = listModle;
	}

	public void setData(List<MemberModle> listModle) {
		this.listModle = listModle;
		notifyDataSetChanged();
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
					R.layout.circle_search_layout_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.username);
			holder.circleName = (TextView) convertView
					.findViewById(R.id.circleName);
			holder.mobileNum = (TextView) convertView
					.findViewById(R.id.mobileNum);
			holder.img = (ImageView) convertView.findViewById(R.id.userimg);
			holder.changeBg = (LinearLayout) convertView
					.findViewById(R.id.changebg);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mobileNum.setText(listModle.get(position).getMobileNum());
		holder.name.setText(listModle.get(position).getName());
		holder.circleName.setText(listModle.get(position).getCircleName());
		String path = listModle.get(position).getImg();
		if (path.equals("") || path == null) {
			holder.img.setBackgroundResource(R.drawable.hand_pic);
		} else {
			ImageManager.from(mContext).displayImage(holder.img, path,
					R.drawable.root_default, 100, 100);
		}
		if (position % 2 == 0) {
			holder.changeBg.setBackgroundColor(Color.WHITE);
		} else {
			holder.changeBg.setBackgroundColor(mContext.getResources()
					.getColor(R.color.f6));
		}
		return convertView;
	}

	class ViewHolder {
		TextView mobileNum;
		ImageView img;
		TextView name;
		TextView circleName;
		LinearLayout changeBg;
	}
}
