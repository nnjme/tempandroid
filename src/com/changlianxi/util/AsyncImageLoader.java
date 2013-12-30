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

	public AsyncImageLoader(Activity activity) {
		cache = new MemoryCache();
	}

	public Bitmap loaDrawable(final String imgUrl,
			final ImageCallback imageCallback) {
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
			return bmp;
		}
		/**
		 * ���ļ������л�ȡ
		 */
		bmp = getImage(imgUrl);
		if (bmp != null) {
			return bmp;
		}
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				imageCallback.imageLoaded((Bitmap) msg.obj, imgUrl);
				return;
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
			Bitmap bitmap = BitmapFactory.decodeFile(url, options);
			if (bitmap == null) {
				return null;
			}
			return bitmap;
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
		Bitmap bmp = HttpUtil.GetBitmapFromUrl(url);
		return bmp;
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap imageDrawable, String imageUrl);
	}
}
