package com.changlianxi.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageSize;

public class LoadHttpImage {
	private MemoryCache cache;
	private Bitmap bmp;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;

	public LoadHttpImage() {
		cache = new MemoryCache();
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.empty_photo)
				.showImageForEmptyUri(R.drawable.empty_photo)
				.showImageOnFail(R.drawable.empty_photo).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.ARGB_8888)
				.build();
		imageLoader = CLXApplication.getImageLoader();
	}

	@SuppressLint("HandlerLeak")
	public void loadImag(final String imgUrl, final ImageView img, int maxX,
			int maxY) {
		img.setImageResource(R.drawable.empty_photo);
		if (imgUrl == null) {
			return;
		}
		if (imgUrl == null || imgUrl.equals("")) {
			img.setImageResource(R.drawable.empty_photo);
			return;
		}
		/**
		 * 先从缓存中查找
		 */
		bmp = cache.get(imgUrl);
		if (bmp != null) {
			img.setImageBitmap(bmp);
			return;
		}
		ImageSize size = new ImageSize(maxX, maxY);
		imageLoader.loadImage(imgUrl, size, options, new ImageListener(img,
				imgUrl));

	}

	class ImageListener implements ImageLoadingListener {

		ImageView img;
		String ImgUrl;

		public ImageListener(ImageView imgvew, String path) {
			this.img = imgvew;
			this.ImgUrl = path;
		}

		public void onLoadingCancelled(String arg0, View arg1) {

		}

		@Override
		public void onLoadingComplete(String arg0, View arg1, Bitmap bmp) {
			cache.put(ImgUrl, bmp);
			img.setImageBitmap(bmp);

		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLoadingStarted(String arg0, View arg1) {
			// TODO Auto-generated method stub

		}
	}
}