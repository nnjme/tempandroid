package com.changlianxi.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.changlianxi.activity.R;

public class LoadImageTask {
	private MemoryCache cache;
	private Bitmap bmp;

	public LoadImageTask() {
		cache = new MemoryCache();
	}

	@SuppressLint("HandlerLeak")
	public void loadBitmap(final String imgUrl, final ImageView img,
			final boolean scale) {
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

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				img.setImageBitmap((Bitmap) msg.obj);
				return;
			}
		};
		new Thread() {
			@Override
			public void run() {
				super.run();
				// Bitmap bmp = BitmapUtils.decodeBitmap(imgUrl, 100, 200);
				// Bitmap bmp = BitmapUtils.getImageThumbnail(imgUrl, 150, 150);
				// if (bmp == null) {
				if (scale) {
					bmp = BitmapUtils.decodeBitmap(imgUrl, 150, 150);
				}
				cache.put(imgUrl, bmp);
				Message message = handler.obtainMessage(0, bmp);
				handler.sendMessage(message);
			}
		}.start();

		return;
	};

}
