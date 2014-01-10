package com.changlianxi.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.R;
import com.changlianxi.modle.MemberModle;
import com.changlianxi.view.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CircleSearchAdapter extends BaseAdapter {
	Context mContext;
	List<MemberModle> listModle;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;

	public CircleSearchAdapter(Context context, List<MemberModle> listModle) {
		this.mContext = context;
		this.listModle = listModle;
		options = CLXApplication.getOptions();
		imageLoader = CLXApplication.getImageLoader();
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
			holder.img = (CircularImage) convertView.findViewById(R.id.userimg);
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
			holder.img.setBackgroundResource(R.drawable.head_bg);
		} else {
			imageLoader.displayImage(path, holder.img, options);
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
		CircularImage img;
		TextView name;
		TextView circleName;
		LinearLayout changeBg;
	}
}
