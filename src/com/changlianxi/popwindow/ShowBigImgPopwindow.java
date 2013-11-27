package com.changlianxi.popwindow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.FloatMath;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.changlianxi.activity.R;
import com.changlianxi.modle.GrowthImgModle;
import com.changlianxi.util.Logger;
import com.changlianxi.util.Utils;
import com.changlianxi.view.RoundProgressBar;

/**
 * 大图预览界面 用popwindow实现
 * 
 * @author teeker_bin
 * 
 */
public class ShowBigImgPopwindow implements OnClickListener, OnTouchListener {
	private View parent = null;
	private ViewPager vp;
	private List<ImageView> views = new ArrayList<ImageView>();// 定义每页要显示imagveiw
	private List<GrowthImgModle> data;
	private Context mContext;
	private static final String SUFFIX = ".cach";// 后缀名
	private View view;
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, Boolean> isSelected = new HashMap<Integer, Boolean>();// 表示是否划过
	private PopupWindow popupWindow;
	private ImageView[] imageViews;// 圆点
	public static int showing;

	private float minscale;
	private float maxscale = 3;
	private int oimgw;
	private int oimgh;
	private int imgw;
	private int imgh;
	private int scw;
	private int sch;
	private HashMap<Integer, Matrix> matrixmap = new HashMap();
	private Matrix savedMatrix = new Matrix();
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist;
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;

	/**
	 * 构造函数 做一些初始化操作
	 * 
	 * @param context
	 * @param parent
	 * @param data
	 */
	public ShowBigImgPopwindow(Context context, View parent,
			List<GrowthImgModle> data) {
		this.parent = parent;
		this.data = data;
		this.mContext = context;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.show_img, null);
		vp = (ViewPager) view.findViewById(R.id.imgPages);
		initViews();
		for (int i = 0; i < data.size(); i++) {
			isSelected.put(i, false);
		}
		vp.setOnPageChangeListener(new PageListener());
		isSelected.put(0, true);
		initPopwindow();
		show();
		new GetImage().execute(data.get(0).getImg(), 0 + "");
	}

	/**
	 * 初始化popwindow
	 */
	private void initPopwindow() {

		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	/**
	 * 初始化界面显示
	 */
	private void initViews() {
		ViewGroup group = (ViewGroup) view.findViewById(R.id.viewGroup);// 包裹小圆点的LinearLayout
		imageViews = new ImageView[data.size()];
		for (int i = 0; i < data.size(); i++) {
			ImageView img = new ImageView(mContext);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			img.setLayoutParams(lp);
			img.setOnClickListener(this);
			img.setScaleType(ImageView.ScaleType.MATRIX);
			img.setOnTouchListener(this);
			views.add(img);
			// 设置 每张图片的句点
			ImageView imageView = new ImageView(mContext);
			imageView.setLayoutParams(new LayoutParams(20, 20));
			imageView.setPadding(20, 0, 20, 0);
			imageViews[i] = imageView;
			if (i == 0) {
				// 默认选中第一张图片
				imageViews[i].setBackgroundResource(R.drawable.point01);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.point02);
			}
			group.addView(imageViews[i]);
		}
		vp.setAdapter(new ViewAdapter(views));
	}

	/**
	 * ViewPager的变化事件
	 * 
	 * @author teeker_bin
	 * 
	 */
	class PageListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			for (int i = 0; i < imageViews.length; i++) {// 设置当前圆点
				if (arg0 == i) {
					imageViews[arg0].setBackgroundResource(R.drawable.point01);
					continue;
				}
				imageViews[i].setBackgroundResource(R.drawable.point02);
			}
			if (isSelected.get(arg0)) {
				return;
			}
			/**
			 * 第一次滑动到当前界面的时候进行加载网络大图
			 */
			new GetImage().execute(data.get(arg0).getImg(), arg0 + "");
			isSelected.put(arg0, true);

		}

	}

	/**
	 * popwindow的显示
	 */
	public void show() {
		popupWindow.showAtLocation(view, Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL, 0, 0);
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 刷新状态
		popupWindow.update();
	}

	// 隐藏
	public void dismiss() {
		popupWindow.dismiss();
	}

	/**
	 * 获取网络大图
	 * 
	 * @author teeker_bin
	 * 
	 */
	private class GetImage extends AsyncTask<String, Integer, String> {
		ImageView img;
		Bitmap bm;
		public final probarpop probar = new probarpop();

		@Override
		protected void onPostExecute(String result) {
			/** 此方法在主线程执行，任务执行的结果作为此方法的参数返回 */
			int position = Integer.valueOf(result);
			img = views.get(position);
			img.setImageBitmap(bm);
			oimgw = bm.getWidth();
			oimgh = bm.getHeight();
			initmatrix(img);
			Logger.debug(this, "大图加载完成！");
			probar.close();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			/** 执行预处理，它运行于UI线程，可以为后台任务做一些准备工作，比如绘制一个进度条控件 */
			probar.show();
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			/** 此方法在主线程执行，用于显示任务执行的进度。 */
			// 由publishProgress传递的值
			int p = Integer.parseInt(values[0].toString());
			probar.porgress(p);
			super.onProgressUpdate(values);
		}

		@Override
		protected String doInBackground(String... params) {
			/**
			 * 此方法在后台线程执行，完成任务的主要工作，通常需要较长的时间。
			 * 在执行过程中可以调用publicProgress(Progress…)来更新任务的进度。
			 **/

			bm = getImage(params[0]);
			if (bm != null) {
				Logger.debug(this, "本地的");
				return params[1];
			}
			try {
				HttpClient client = new DefaultHttpClient();
				String imgurl = params[0];
				URI uri = URI.create(imgurl);
				HttpGet get = new HttpGet(uri);
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				long length = entity.getContentLength();
				InputStream in = entity.getContent();
				if (in != null) {
					String filename = convertUrlToFileName(imgurl);
					String dir = Environment.getExternalStorageDirectory()
							+ File.separator + "clx" + File.separator
							+ "growthImg";
					createPath(dir, filename);
					FileOutputStream os = new FileOutputStream(dir
							+ File.separator + filename);
					byte[] buf = new byte[128];
					int ch = -1;
					int count = 0;
					while ((ch = in.read(buf)) != -1) {
						os.write(buf, 0, ch);
						count += ch;
						if (length > 0) {
							publishProgress((int) ((count / (float) length) * 100));
						}
					}
					bm = BitmapFactory.decodeFile(dir + File.separator
							+ filename);
					in.close();
					os.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Logger.error(this, e);
			}
			return params[1];
		}
	}

	/** 从文件缓存中获取图片 **/
	private Bitmap getImage(String url) {
		final String path = Environment.getExternalStorageDirectory()
				+ File.separator + "clx" + File.separator + "growthImg"
				+ File.separator + convertUrlToFileName(url);
		File file = new File(path);
		if (file.exists()) {
			Bitmap bmp = BitmapFactory.decodeFile(path);
			if (bmp == null) {
				file.delete();
			} else {
				Logger.debug(this, "bmpmbmp");
				return bmp;
			}
		}
		return null;
	}

	/**
	 * 进度条的显示
	 * 
	 * @author teeker_bin
	 * 
	 */
	class probarpop {
		private RoundProgressBar probar1;
		private PopupWindow popupWindow;

		public void show() {
			if (popupWindow == null) {
				LayoutInflater layoutInflater = LayoutInflater.from(mContext);
				View view = layoutInflater.inflate(R.layout.probarpop, null);
				popupWindow = new PopupWindow(view,
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				probar1 = (RoundProgressBar) view.findViewById(R.id.probar1);
			}
			popupWindow.showAtLocation(parent, Gravity.CENTER_VERTICAL, 20, 20);
			popupWindow.update();
		}

		public void porgress(int i) {
			probar1.setProgress(i);
			Logger.debug(this, "progress::" + i);
		}

		public void close() {
			if (popupWindow != null) {
				popupWindow.dismiss();
			}
		}
	}

	/**
	 * 创建文件
	 * 
	 * @param path
	 */
	private void createPath(String sDir, String filename) {
		File destDir = new File(sDir);
		if (!destDir.exists()) {// 创建文件夹
			destDir.mkdirs();
		}
		Logger.debug(this, "createPath");
		File file = new File(sDir + File.separator + filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Logger.error(this, e);
				e.printStackTrace();
			}
		}
	}

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

	public class ViewAdapter extends PagerAdapter {

		// 界面列表
		private List<ImageView> views;

		public ViewAdapter(List<ImageView> views) {
			this.views = views;
		}

		// 销毁arg1位置的界面
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		// 获得当前界面数
		@Override
		public int getCount() {
			if (views != null) {
				return views.size();
			}
			return 0;
		}

		// 初始化arg1位置的界面
		@Override
		public Object instantiateItem(View arg0, int arg1) {

			((ViewPager) arg0).addView(views.get(arg1), 0);

			return views.get(arg1);
		}

		// 判断是否由对象生成界面
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub
		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub
		}
	}

	@Override
	public void onClick(View v) {
		dismiss();

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		ImageView myImageView = (ImageView) v;
		float[] mv = new float[9];
		Matrix matrix = new Matrix();
		if (matrixmap.containsKey(showing)) {
			matrix = matrixmap.get(showing);
		}

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:// 设置拖拉模式
			matrix.set(myImageView.getImageMatrix());
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:// 设置多点触摸模式
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;

		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {// 若为DRAG模式，则点击移动图片
				float tx = (event.getX() - start.x);
				float ty = (event.getY() - start.y);
				matrix.set(savedMatrix);
				matrix.getValues(mv);
				if (sch >= imgh) {
					if (mv[5] + ty >= (sch - imgh) / 2) {
						ty = (sch - imgh) / 2 - mv[5];
					} else if (mv[5] + ty <= (sch - imgh) / 2) {
						ty = (sch - imgh) / 2 - mv[5];
					}
				} else {
					if (mv[5] + ty >= 0) {
						ty = -mv[5];
					} else if (mv[5] + ty <= sch - imgh) {
						ty = sch - imgh - mv[5];
					}
				}
				matrix.postTranslate(tx, ty);
			} else if (mode == ZOOM) {// 若为ZOOM模式，则点击触摸缩放
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = (newDist / oldDist);
					matrix.getValues(mv);
					if (mv[0] * scale < minscale) {
						scale = minscale / mv[0];
					} else if (mv[0] * scale > maxscale) {
						scale = maxscale / mv[0];
					}
					matrix.postScale(scale, scale, mid.x, mid.y);
					matrix.getValues(mv);
					imgw = (int) (oimgw * mv[0]);
					imgh = (int) (oimgh * mv[0]);
					float tx = 0;
					float ty = 0;
					if (sch >= imgh) {
						if (mv[5] > (sch - imgh) / 2) {
							ty = (sch - imgh) / 2 - mv[5];
						} else if (mv[5] < (sch - imgh) / 2) {
							ty = (sch - imgh) / 2 - mv[5];
						}
					} else {
						if (mv[5] > 0) {
							ty = -mv[5];
						} else if (mv[5] < sch - imgh) {
							ty = sch - imgh - mv[5];
						}
					}
					matrix.postTranslate(tx, ty);
				}
			}
			break;
		}
		myImageView.setImageMatrix(matrix);
		matrixmap.put(showing, matrix);
		return true;
	}

	// 计算移动距离
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	// 计算中点位置
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	private void initmatrix(ImageView img) {
		Matrix matrix = new Matrix();
		if (matrixmap.containsKey(showing)) {
			matrix = matrixmap.get(showing);
		}
		ImageView imgi = img;
		matrix.set(imgi.getImageMatrix());

		scw = Utils.getSecreenWidth(mContext); // 当前分辨率 宽度
		sch = Utils.getSecreenHeight(mContext); // 当前分辨率高度
		float wscale = scw / (float) oimgw;
		float hscale = sch / (float) oimgh;
		float scale;
		if (wscale < hscale) {
			scale = wscale;
		} else {
			scale = hscale;
		}
		matrix.setScale(scale, scale);
		minscale = scale;
		imgw = (int) ((int) oimgw * scale);
		imgh = (int) ((int) oimgh * scale);
		int dx = (scw - imgw) / 2;
		int dy = (sch - imgh) / 2;
		matrix.postTranslate(dx, dy);
		imgi.setImageMatrix(matrix);
		matrixmap.put(showing, matrix);
	}
}
