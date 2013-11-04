package com.changlianxi.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.activity.R;
import com.changlianxi.modle.CircleModle;
import com.changlianxi.util.AsyncImageLoader;
import com.changlianxi.util.AsyncImageLoader.ImageCallback;
import com.changlianxi.view.CircularImage;

/**
 * 圈子显示的自定义adapter
 * 
 * @author teeker_bin
 * 
 */
public class CircleAdapter extends BaseAdapter {
	private List<CircleModle> listmodle;
	private Context mcontext;
	private GridView listView;
	private AsyncImageLoader ImageLoader;

	public CircleAdapter(Context context, List<CircleModle> listModle,
			GridView listView, Activity activity) {
		this.listmodle = listModle;
		this.mcontext = context;
		this.listView = listView;
		ImageLoader = new AsyncImageLoader(activity);
	}

	public CircleAdapter(Context context) {
		this.mcontext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listmodle.size();
	}

	public void setData(List<CircleModle> modle) {
		this.listmodle = modle;
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

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
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// int width = Utils.getSecreenWidth(mcontext) - 60;
		// FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
		// width / 4, width / 4);
		// holder.circleImg.setLayoutParams(params);
		String imgUrl = listmodle.get(position).getCirIcon();
		if (imgUrl.equals("addroot")) {
			holder.circleImg.setImageResource(R.drawable.root_add);

		} else {
			ImageView imageView = holder.circleImg;
			imageView.setTag(imgUrl);
			// 异步下载图片
			Bitmap cachedImage = ImageLoader.loaDrawable(imgUrl,
					new ImageCallback() {
						@Override
						public void imageLoaded(Bitmap imageDrawable,
								String imageUrl) {
							ImageView imageViewByTag = (ImageView) listView
									.findViewWithTag(imageUrl);
							if (imageViewByTag != null) {
								imageViewByTag.setImageBitmap(imageDrawable);
							}
						}
					});
			if (cachedImage != null) {
				holder.circleImg.setImageBitmap(cachedImage);

			} else {
				holder.circleImg.setImageResource(R.drawable.hand_pic);
			}
		}
		holder.circleName.setText(listmodle.get(position).getCirName());
		return convertView;
	}

	class ViewHolder {
		CircularImage circleImg;
		TextView circleName;
	}

}
