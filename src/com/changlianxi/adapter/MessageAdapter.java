package com.changlianxi.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.activity.CLXApplication;
import com.changlianxi.activity.showBigPic.ImagePagerActivity;
import com.changlianxi.modle.MessageModle;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.EmojiParser;
import com.changlianxi.util.LoadHttpImage;
import com.changlianxi.util.LoadImageTask;
import com.changlianxi.view.CircularImage;
import com.changlianxi.view.EmojiEditText;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MessageAdapter extends BaseAdapter {
	private Context mContext;
	private List<MessageModle> listModle;
	private DisplayImageOptions optionsAvatar;
	private ImageLoader imageLoader;
	private LoadImageTask myLoad;
	private LoadHttpImage loadImg;

	public MessageAdapter(Context context, List<MessageModle> listModle) {
		this.mContext = context;
		this.listModle = listModle;
		optionsAvatar = CLXApplication.getUserOptions();
		imageLoader = CLXApplication.getImageLoader();
		myLoad = new LoadImageTask();
		loadImg = new LoadHttpImage();
	}

	@Override
	public int getCount() {
		return listModle.size();
	}

	public void setData(List<MessageModle> listModle) {
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
		int type = listModle.get(position).getType();
		String content = listModle.get(position).getContent();
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.message_chat_list_item1, null);
			holder = new ViewHolder();
			holder.otherLayout = (RelativeLayout) convertView
					.findViewById(R.id.otherLayout);
			holder.otherContent = (EmojiEditText) convertView
					.findViewById(R.id.otherContent);
			holder.otherAvatar = (CircularImage) convertView
					.findViewById(R.id.otherAvatar);
			holder.otherName = (TextView) convertView
					.findViewById(R.id.otherName);
			holder.selfLayout = (RelativeLayout) convertView
					.findViewById(R.id.selfLayout);
			holder.selfContent = (EmojiEditText) convertView
					.findViewById(R.id.selfContent);
			holder.selfAvatar = (CircularImage) convertView
					.findViewById(R.id.selfAvatar);
			holder.selfImg = (ImageView) convertView.findViewById(R.id.selfImg);
			holder.otherImg = (ImageView) convertView
					.findViewById(R.id.otherImg);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (listModle.get(position).isSelf()) {
			if (type == 0) {
				holder.selfImg.setVisibility(View.GONE);
				holder.selfContent.setVisibility(View.VISIBLE);
			} else if (type == 1) {
				holder.selfImg.setVisibility(View.VISIBLE);
				holder.selfContent.setVisibility(View.GONE);
				if (content.startsWith("http")) {
					loadImg.loadImag(content, holder.selfImg, 50, 50);
					// imageLoader.displayImage(content, holder.selfImg,
					// options);

				} else {
					myLoad.loadBitmap(content, holder.selfImg, true);
				}
			}
			holder.otherLayout.setVisibility(View.GONE);
			holder.selfLayout.setVisibility(View.VISIBLE);
			holder.selfContent
					.setText(EmojiParser.demojizedText(content + " "));
			String path = listModle.get(position).getAvatar();
			if (path == null || path.equals("")) {
				holder.selfAvatar.setImageResource(R.drawable.head_bg);
			} else {
				imageLoader
						.displayImage(path, holder.selfAvatar, optionsAvatar);
			}
		} else {
			if (type == 0) {
				holder.otherImg.setVisibility(View.GONE);
				holder.otherContent.setVisibility(View.VISIBLE);
			} else if (type == 1) {
				holder.otherImg.setVisibility(View.VISIBLE);
				holder.otherContent.setVisibility(View.GONE);
				loadImg.loadImag(content, holder.otherImg, 50, 50);
				// imageLoader.displayImage(content, holder.otherImg, options);

			}
			holder.selfLayout.setVisibility(View.GONE);
			holder.otherLayout.setVisibility(View.VISIBLE);
			holder.otherContent.setText(EmojiParser
					.demojizedText(content + " "));
			holder.otherName.setText(listModle.get(position).getName());
			String path = listModle.get(position).getAvatar();
			if (path.equals("") || path == null) {
				holder.otherAvatar.setImageResource(R.drawable.head_bg);
			} else {
				imageLoader.displayImage(path, holder.otherAvatar,
						optionsAvatar);
			}
		}
		holder.selfImg.setOnClickListener(new ImageOnClick(
				new String[] { content }));
		holder.otherImg.setOnClickListener(new ImageOnClick(
				new String[] { content }));
		showTime(position, holder);
		return convertView;
	}

	class ViewHolder {
		RelativeLayout otherLayout;
		EmojiEditText otherContent;
		TextView otherName;
		CircularImage otherAvatar;
		RelativeLayout selfLayout;
		EmojiEditText selfContent;
		CircularImage selfAvatar;
		ImageView selfImg;
		ImageView otherImg;
		TextView time;
	}

	class ImageOnClick implements OnClickListener {
		String url[];

		public ImageOnClick(String url[]) {
			this.url = url;
		}

		@Override
		public void onClick(View v) {
			imageBrower(1, url);
		}

	}

	private void showTime(int position, ViewHolder holder) {
		if (position == 0) {
			holder.time.setVisibility(View.VISIBLE);
			holder.time.setText(listModle.get(position).getTime());
			return;
		}
		// 当前联系人的时间
		String endTime = listModle.get(position).getTime();
		// 上一个联系人的时间
		String startTime = listModle.get(position - 1).getTime();
		/**
		 * 判断时间间隔
		 */
		if (DateUtils.compareDate(startTime, endTime, 1)) {
			holder.time.setVisibility(View.VISIBLE);
			holder.time.setText(endTime);
		} else {
			holder.time.setVisibility(View.GONE);
		}
	}

	/**
	 * 浏览大图
	 * 
	 * @param position
	 * @param imageUrls
	 */
	private void imageBrower(int position, String imageUrls[]) {
		Intent intent = new Intent(mContext, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, imageUrls);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		mContext.startActivity(intent);
	}
}
