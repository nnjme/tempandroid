package com.changlianxi.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.activity.CLXApplication;
import com.changlianxi.modle.CircleModle;
import com.changlianxi.util.StringUtils;
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
			holder.prompt = (TextView) convertView.findViewById(R.id.txtPrompt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String imgUrl = listmodle.get(position).getCirIcon();
		if (imgUrl.equals("")) {
			holder.circleImg.setImageResource(R.drawable.pic);
		} else if (!imgUrl.startsWith("http")) {
			holder.circleBg.setImageResource(R.drawable.pic_add);
			holder.circleImg.setImageBitmap(null);
		} else {
			imageLoader.displayImage(imgUrl, holder.circleImg, options);
		}
		int promptCount = listmodle.get(position).getPromptCount();
		boolean isnew = listmodle.get(position).isNew();
		if (!isnew && promptCount == 0) {
			holder.prompt.setVisibility(View.GONE);
		} else {
			holder.prompt.setVisibility(View.VISIBLE);
			if (isnew) {
				holder.prompt.setText("new");
			} else {
				holder.prompt.setText(promptCount + "");
				if (promptCount <= 0) {
					holder.prompt.setVisibility(View.GONE);
				}
			}
		}
		holder.circleName.setText(StringUtils.ToDBC(listmodle.get(position)
				.getCirName()));
		return convertView;
	}

	class ViewHolder {
		CircularImage circleImg;
		ImageView circleBg;
		TextView circleName;
		TextView prompt;
	}

}
