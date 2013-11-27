package com.changlianxi.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import com.changlianxi.modle.SelectPicModle;

/**
 * bitmap工作类
 * 
 * @author teeker_bin
 * 
 */
public class BitmapUtils {

	/**
	 * 转换图片成圆�?
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_4444);
		Canvas canvas = new Canvas(output);
		// canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
		// Paint.ANTI_ALIAS_FLAG
		// | Paint.FILTER_BITMAP_FLAG));
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	/**
	 * 将drawable转换为bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap convertDrawable2BitmapSimple(Drawable drawable) {
		BitmapDrawable bd = (BitmapDrawable) drawable;
		if (bd == null) {
			return null;
		}
		return BitmapUtils.toRoundBitmap(bd.getBitmap());
	}

	/**
	 * 将Bitmap文件保存为本地文件
	 * 
	 * @param bmp
	 * @param filename
	 */
	public static void createImgToFile(Bitmap bmp, String filename) {
		FileOutputStream b = null;
		try {
			b = new FileOutputStream(filename);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, b);// 把数据写入文件
		} catch (FileNotFoundException e) {
			Logger.error(BitmapUtils.class, e);
			e.printStackTrace();
		} finally {
			try {
				b.flush();
				b.close();
			} catch (IOException e) {
				Logger.error(BitmapUtils.class, e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * 动态计算图片的inSampleSize
	 * 
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 获取图片缩略�? 只有Android2.1以上版本支持
	 * 
	 * @param imgName
	 * @param kind
	 *            MediaStore.Images.Thumbnails.MICRO_KIND
	 * @return
	 */
	public static Bitmap loadImgThumbnail(String path, int kind,
			Activity activity) {
		String imgName = FileUtils.getFileName(path);
		Bitmap bitmap = null;
		String[] proj = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DISPLAY_NAME };
		Cursor cursor = activity.managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,
				MediaStore.Images.Media.DISPLAY_NAME + "='" + imgName + "'",
				null, null);

		if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
			ContentResolver crThumb = activity.getContentResolver();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 4;
			bitmap = MediaStore.Images.Thumbnails.getThumbnail(crThumb,
					cursor.getInt(0), kind, options);
		}
		return bitmap;
	}

	/**
	 * 按比例缩放图片
	 * 
	 * @param bmp
	 *            要缩放的图片
	 * @param width
	 *            放缩以后的宽
	 * @param height
	 *            缩放以后的高
	 * @return
	 */
	public static Bitmap scaleBitmap(Bitmap bmp) {
		if (bmp == null) {
			return null;
		}
		// 获取这个图片的宽和高
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		// 定义预转换成的图片的宽度和高度
		int newWidth = width / 2;
		int newHeight = height / 2;
		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height,
				matrix, true);
		bmp.recycle();
		return resizedBitmap;
	}

	/**
	 * 获取从图库中选择的图片和地址
	 * 
	 * @param context
	 * @param data
	 * @return
	 */
	public static SelectPicModle getPickPic(Activity context, Intent data) {
		SelectPicModle modle = new SelectPicModle();
		Uri thisUri = data.getData();// 获得图片的uri
		// 这里开始的第二部分，获取图片的路径：
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.managedQuery(thisUri, proj, null, null, null);
		// 按我个人理解 这个是获得用户选择的图片的索引值
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		// 最后根据索引值获取图片路径
		String picPath = cursor.getString(column_index);
		Bitmap bitmap = FitSizeImg(picPath);
		modle.setPicPath(picPath);
		if (bitmap != null) {
			modle.setBmp(bitmap);
		}
		return modle;

	}

	/**
	 * 根据图片地址获取图片
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap getBimapByPath(String path) {
		Bitmap bmp = FitSizeImg(path);
		return bmp;

	}

	/**
	 * / 按图片大小(字节大小)缩放图片
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap FitSizeImg(String path) {
		if (path == null || path.length() < 1)
			return null;
		File file = new File(path);
		Bitmap resizeBmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 数字越大读出的图片占用的heap越小 不然总是溢出
		if (file.length() < 20480) { // 0-20k
			opts.inSampleSize = 1;
		} else if (file.length() < 51200) { // 20-50k
			opts.inSampleSize = 2;
		} else if (file.length() < 307200) { // 50-300k
			opts.inSampleSize = 4;
		} else if (file.length() < 819200) { // 300-800k
			opts.inSampleSize = 6;
		} else if (file.length() < 1048576) { // 800-1024k
			opts.inSampleSize = 8;
		} else {
			opts.inSampleSize = 10;
		}
		resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);
		return resizeBmp;
	}

	/**
	 * 裁剪图片方法实现 &nbsp;
	 * 
	 * @param uri
	 */

	public static void startPhotoZoom(Context context, Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		((Activity) context).startActivityForResult(intent,
				Constants.REQUEST_CODE_GETIMAGE_DROP);
	}
}
