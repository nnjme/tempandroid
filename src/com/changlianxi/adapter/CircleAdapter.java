package com.changlianxi.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.activity.R;
import com.changlianxi.modle.CircleModle;
import com.changlianxi.util.FileUtils;
import com.changlianxi.view.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 圈子显示的自定义adapter
 * 
 * @author teeker_bin
 * 
 */
public class CircleAdapter extends BaseAdapter {
	private List<CircleModle> listmodle;
	private Context mcontext;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;

	public CircleAdapter(Context context, List<CircleModle> listModle) {
		this.listmodle = listModle;
		this.mcontext = context;
		options = CLXApplication.getOptions();
		imageLoader = CLXApplication.getImageLoader();
	}

	public CircleAdapter(Context context) {
		this.mcontext = context;
	}

	@Override
	public int getCount() {
		return listmodle.size();
	}

	public void setData(List<CircleModle> modle) {
		this.listmodle = modle;
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

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mcontext).inflate(
					R.layout.circle_item, null);
			holder.circleImg = (CircularImage) convertView
					.findViewById(R.id.circleImg);
			holder.circleName = (TextView) convertView
					.findViewById(R.id.circleName);
			holder.circleBg = (ImageView) convertView
					.findViewById(R.id.circleBg);
			holder.isnew = (TextView) convertView.findViewById(R.id.isnew);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// WigdtContorl.setAvatarWidth(mcontext, holder.circleImg,
		// holder.circleBg, 5, 6);
		String imgUrl = listmodle.get(position).getCirIcon();
		if (imgUrl.equals("addroot") || imgUrl.contains(FileUtils.getRootDir())) {
			holder.circleBg.setImageResource(R.drawable.pic_add);
			holder.circleImg.setImageBitmap(null);
		} else {
			imageLoader.displayImage(imgUrl, holder.circleImg, options);
		}
		if (listmodle.get(position).isNew()) {
			holder.isnew.setVisibility(View.VISIBLE);
		} else {
			holder.isnew.setVisibility(View.GONE);

		}
		holder.circleName.setTextColor(Color.argb(150, 0, 0, 0));
		holder.circleName.setText(listmodle.get(position).getCirName());
		return convertView;
	}

	class ViewHolder {
		CircularImage circleImg;
		ImageView circleBg;
		TextView circleName;
		TextView isnew;
	}

}
