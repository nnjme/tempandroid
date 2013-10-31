package com.changlianxi.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/* 
 * ��ȡ�����ÿؼ���Ϣ 
 */
public class WigdtContorl {
	public static int delaultY;
	public static Visible visible;
	public static int moveY;

	/*
	 * ��ȡ�ؼ���
	 */
	public static int getWidth(View view) {
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		return (view.getMeasuredWidth());
	}

	/*
	 * ��ȡ�ؼ���
	 */
	public static int getHeight(View view) {
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		return (view.getMeasuredHeight());
	}

	/*
	 * ���ÿؼ����ڵ�λ��X�����Ҳ��ı��ߣ� XΪ����λ�ã���ʱY���ܹ�0
	 */
	public static void setLayoutX(View view, int x) {
		MarginLayoutParams margin = new MarginLayoutParams(
				view.getLayoutParams());
		margin.setMargins(x, margin.topMargin, x + margin.width,
				margin.bottomMargin);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				margin);
		view.setLayoutParams(layoutParams);
	}

	/*
	 * ���ÿؼ����ڵ�λ��Y�����Ҳ��ı��ߣ� YΪ����λ�ã���ʱX���ܹ�0
	 */
	public static void setLayoutY_UP(View view, int y, Context context, View v) {
		MarginLayoutParams margin = new MarginLayoutParams(
				view.getLayoutParams());
		if (view.getTop() - y <= moveY) {// marginС��moveYʱ��ֵΪmoveY
			margin.setMargins(margin.leftMargin, moveY, margin.rightMargin,
					margin.bottomMargin);

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					margin);
			view.setLayoutParams(layoutParams);
			visible.setVisible(true);
			return;
		}
		margin.setMargins(margin.leftMargin, view.getTop() - y,
				margin.rightMargin, margin.bottomMargin);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				margin);
		view.setLayoutParams(layoutParams);
		visible.setVisible(true);

	}

	public static void setLayoutY_Down(View view, int y, Context context, View v) {
		MarginLayoutParams margin = new MarginLayoutParams(
				view.getLayoutParams());
		if (view.getTop() - y > delaultY) {
			// margin����delaultyʱ��ֵΪdelaulty
			margin.setMargins(margin.leftMargin, delaultY, margin.rightMargin,
					margin.bottomMargin);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					margin);
			view.setLayoutParams(layoutParams);
			visible.setVisible(false);
			return;
		}
		margin.setMargins(margin.leftMargin, view.getTop() - y,
				margin.rightMargin, margin.bottomMargin);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				margin);
		view.setLayoutParams(layoutParams);
		visible.setVisible(false);

	}

	/*
	 * ���ÿؼ����ڵ�λ��YY�����Ҳ��ı��ߣ� XYΪ����λ��
	 */
	public static void setLayout(View view, int x, int y) {
		MarginLayoutParams margin = new MarginLayoutParams(
				view.getLayoutParams());
		margin.setMargins(x, y, x + margin.width, y + margin.height);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				margin);
		view.setLayoutParams(layoutParams);
	}

	public static void setVisible(Visible vi) {
		visible = vi;
		visible.setVisible(false);
	}

	public interface Visible {
		public void setVisible(boolean flag);
	}

	/**
	 * �趨�ؼ��Ŀ��ֵ
	 * 
	 * @param img
	 * @param context
	 * @param width
	 * @param marginTop
	 * @param marginLeft
	 * @param marginRight
	 * @param marginButtom
	 */
	public static void setViewWidth(ImageView img, Context context, int width,
			int marginTop, int marginLeft, int marginRight, int marginButtom) {
		int Screenwidth = Utils.getSecreenWidth(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				Screenwidth / width, Screenwidth / width);
		lp.setMargins(marginLeft, marginTop, marginRight, marginButtom);
		img.setLayoutParams(lp);
	}
}