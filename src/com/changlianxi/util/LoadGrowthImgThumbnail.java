package com.changlianxi.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.activity.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class LoadGrowthImgThumbnail {
	private MemoryCache cache;
	private Bitmap scaleBmp;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;

	public LoadGrowthImgThumbnail() {
		cache = new MemoryCache();
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.empty_photo)
				.showImageForEmptyUri(R.drawable.empty_photo)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.showImageOnFail(R.drawable.empty_photo).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.ARGB_8888)
				.build();
		imageLoader = CLXApplication.getImageLoader();
	}

	@SuppressLint("HandlerLeak")
	public void loadBitmap(final String imgUrl, final ImageView img,
			final int width, final int height) {
		if (imgUrl == null || imgUrl.equals("")) {
			img.setImageResource(R.drawable.empty_photo);
			return;
		}
		/**
		 * 先从缓存中查找
		 */
		scaleBmp = cache.get(imgUrl);
		if (scaleBmp != null) {
			img.setImageBitmap(scaleBmp);
			return;
		}

		imageLoader.loadImage(imgUrl, options, new ImgListener(imgUrl, img,
				width, height));
	};

	class ImgListener implements ImageLoadingListener {
		String path;
		ImageView img;
		int width;
		int height;

		public ImgListener(String path, ImageView img, int width, int height) {
			this.path = path;
			this.img = img;
			this.width = width;
			this.height = height;
		}

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {

		}

		@Override
		public void onLoadingComplete(String arg0, View arg1, Bitmap bmp) {
			System.out.println("aaaaaaaaa::");
			scaleBmp = BitmapUtils.getImageThumbnail(bmp, width, height);
			cache.put(path, scaleBmp);
			img.setImageBitmap(scaleBmp);
		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {

		}

		@Override
		public void onLoadingStarted(String arg0, View arg1) {

		}

	}
}
