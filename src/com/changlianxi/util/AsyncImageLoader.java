package com.changlianxi.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

/**
 * 异步下载图片类
 * 
 * @author teeker_bin
 * 
 */
public class AsyncImageLoader {
	private MemoryCache cache;
	private static final String SUFFIX = ".cach";// 后缀名
	private Bitmap bmp;
	private Activity activity;

	public AsyncImageLoader(Activity activity) {
		cache = new MemoryCache();
		this.activity = activity;
	}

	public Bitmap loaDrawable(final String imgUrl,
			final ImageCallback imageCallback) {
		Logger.debug(this, imgUrl);
		if (imgUrl == null) {
			return null;
		}
		if (imgUrl.equals("") || imgUrl == null) {
			return null;
		}

		/**
		 * 先从缓存中查找
		 */
		bmp = cache.get(imgUrl);
		if (bmp != null) {
			Logger.debug(this, "缓存");
			return bmp;
		}
		/**
		 * 从文件缓存中获取
		 */
		bmp = getImage(imgUrl);
		if (bmp != null) {
			Logger.debug(this, "本地");
			return bmp;
		}
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				imageCallback.imageLoaded((Bitmap) msg.obj, imgUrl);
			}
		};
		new Thread() {
			@Override
			public void run() {
				super.run();
				Bitmap bmp = loadImageFromUrl(imgUrl);
				cache.put(imgUrl, bmp);
				saveBitmap(bmp, imgUrl);
				Message message = handler.obtainMessage(0, bmp);
				handler.sendMessage(message);
			}
		}.start();

		return null;
	};

	/**
	 * 根据url转换本地文件名
	 * 
	 * @param url
	 * @return
	 */
	private String convertUrlToFileName(String url) {
		String[] strs = url.split("/");
		return strs[strs.length - 1] + SUFFIX;
	}

	/** 将图片存入文件缓存 **/
	private void saveBitmap(Bitmap bm, String url) {
		if (bm == null) {
			return;
		}
		String filename = convertUrlToFileName(url);
		String dir = Environment.getExternalStorageDirectory() + File.separator
				+ "clxcache";
		File file = new File(dir + "/" + filename);
		try {
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			Logger.error(this, e);
		} catch (IOException e) {
			Logger.error(this, e);
		}
	}

	/** 从缓存中获取图片 **/
	private Bitmap getImage(String url) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		if (url.contains(FileUtils.getRootDir())) {
			// String imgName = FileUtils.getFileName(url);
			Bitmap bitmap = BitmapFactory.decodeFile(url, options);
			// Bitmap bitmap = BitmapUtils.loadImgThumbnail(imgName,
			// MediaStore.Images.Thumbnails.MICRO_KIND, activity);
			if (bitmap == null) {
				return null;
			}
			return bitmap;
			// return BitmapUtils.toRoundBitmap(bitmap);
		}
		final String path = Environment.getExternalStorageDirectory()
				+ File.separator + "clxcache" + "/" + convertUrlToFileName(url);
		File file = new File(path);
		if (file.exists()) {
			Bitmap bmp = BitmapFactory.decodeFile(path, options);
			if (bmp == null) {
				file.delete();
			} else {
				cache.put(url, bmp);
				return bmp;
			}
		}
		return null;
	}

	/**
	 * 从网络获取图片
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap loadImageFromUrl(String url) {
		Logger.debug(this, "网络");
		Bitmap bmp = HttpUtil.GetBitmapFromUrl(url);
		// Bitmap rbnmp = null;
		// if (bmp != null) {
		// rbnmp = BitmapUtils.toRoundBitmap(bmp);
		// bmp.recycle();
		// }
		// smallBmp.recycle();
		Bitmap bitmap = BitmapUtils.scaleBitmap(bmp);
		if (bmp != null && !bmp.isRecycled()) {
			bmp.recycle();
		}
		return bitmap;
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap imageDrawable, String imageUrl);
	}
}
