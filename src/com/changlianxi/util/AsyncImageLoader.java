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
 * �첽����ͼƬ��
 * 
 * @author teeker_bin
 * 
 */
public class AsyncImageLoader {
	private MemoryCache cache;
	private static final String SUFFIX = ".cach";// ��׺��
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
		 * �ȴӻ����в���
		 */
		bmp = cache.get(imgUrl);
		if (bmp != null) {
			Logger.debug(this, "����");
			return bmp;
		}
		/**
		 * ���ļ������л�ȡ
		 */
		bmp = getImage(imgUrl);
		if (bmp != null) {
			Logger.debug(this, "����");
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
	 * ����urlת�������ļ���
	 * 
	 * @param url
	 * @return
	 */
	private String convertUrlToFileName(String url) {
		String[] strs = url.split("/");
		return strs[strs.length - 1] + SUFFIX;
	}

	/** ��ͼƬ�����ļ����� **/
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

	/** �ӻ����л�ȡͼƬ **/
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
	 * �������ȡͼƬ
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap loadImageFromUrl(String url) {
		Logger.debug(this, "����");
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
