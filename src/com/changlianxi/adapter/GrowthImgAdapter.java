package com.changlianxi.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.data.Growth;
import com.changlianxi.data.GrowthImage;
import com.changlianxi.R;
import com.changlianxi.modle.GrowthImgModle;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GrowthImgAdapter extends BaseAdapter {
	private Context mContext;
	private List<GrowthImage> listData;
	private int average;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;

	public GrowthImgAdapter(Context context, List<GrowthImage> data,
			int average) {
		this.mContext = context;
		this.listData = data;
		this.average = average;
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.empty_photo)
				.showImageForEmptyUri(R.drawable.empty_photo)
				.showImageOnFail(R.drawable.empty_photo).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.ARGB_8888)
				.build();
		imageLoader = CLXApplication.getImageLoader();
	}

	@Override
	public int getCount() {
		return listData.size();
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
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.grow_img_gridview_item, null);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String path = "";
		if (average == 2) {
			holder.img.setScaleType(ScaleType.CENTER_CROP);
			path = listData.get(position).getImg();
			holder.img.setLayoutParams(new FrameLayout.LayoutParams(150, 150));
		} else {
			holder.img.setScaleType(ScaleType.FIT_XY);
			path = listData.get(position).getImg();
		}
		imageLoader.displayImage(path, holder.img, options);

		return convertView;
	}

	class ViewHolder {
		ImageView img;
	}
}
