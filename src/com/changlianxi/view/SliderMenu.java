package com.changlianxi.view;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * 侧边栏
 */
public class SliderMenu {

	private SliderMenu() {
	}

	/**
	 * 获取侧边栏
	 * 
	 * @param activity
	 * @return
	 */
	public static SliderView getSliderView(Activity activity) {
		return new SliderView(activity);
	}

	public static class SliderView extends ViewGroup {

		private LinearLayout mMenuLy;

		private LinearLayout mContentLy;

		private Scroller mScroller;

		private static int MENU_WIDTH = 270;

		private static int MENU_HEIGHT = 270;

		private static int mMovePosition = 0;

		// 布局时偏离的位置
		private static int M_LAYOUT_POSITION = 0;

		// 打开时位置
		private static int M_SLIDER_MAX_OPEN = 0;
		// 关闭时位置
		private static int M_SLIDER_MAX_CLOSE = 0;

		private static float MOTIONEVENT_X, MOTIONEVENT_Y;

		private static float TOUCH_OPEN_MENU_DISTANCE = 50;

		private boolean ISMENU_OPEN = false;
		private static Position MENU_POSITION = Position.LEFT;

		private Context mContext;

		private SliderView(Context context) {
			super(context);
			mScroller = new Scroller(context);
			mContext = context;
			init();
		}

		/**
		 * 初始化
		 * 
		 * @param activity
		 */
		protected void init() {
			initLayout();
			initProperties();
		}

		/**
		 * 初始化容器
		 */
		protected void initLayout() {
			removeAllViews();
			// 初始化Menu菜单
			mMenuLy = new LinearLayout(mContext);

			// 初始化内容容器
			mContentLy = new LinearLayout(mContext);
			LayoutParams menuParams = null;
			LayoutParams contentParams = null;
			switch (MENU_POSITION) {
			case LEFT:
			case RIGHT:
				menuParams = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
				contentParams = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
				break;
			case TOP:
			case BOTTOM:
				menuParams = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
				contentParams = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
				break;
			}
			addView(mMenuLy, menuParams);
			addView(mContentLy, contentParams);
		}

		/**
		 * 重置调用时用到的参数
		 */
		protected void initProperties() {
			switch (MENU_POSITION) {
			case LEFT:
				menuLeftProperties();
				break;
			case RIGHT:
				menuRightProperties();
				break;
			case TOP:
				menuTopProperties();
				break;
			case BOTTOM:
				menuBottomProperties();
				break;
			}
		}

		/**
		 * 左边栏的属性初始化
		 */
		protected void menuLeftProperties() {
			M_LAYOUT_POSITION = ISMENU_OPEN ? 0 : -MENU_WIDTH;
			// 当打开时，右边的移动。理解：保持画布不动，视图区移动
			M_SLIDER_MAX_OPEN = ISMENU_OPEN ? 0 : -MENU_WIDTH;
			// 当移动时左边的移动
			M_SLIDER_MAX_CLOSE = ISMENU_OPEN ? MENU_WIDTH : 0;
			// 偏移位置
			mMovePosition = ISMENU_OPEN ? 0 : -MENU_WIDTH;
		}

		/**
		 * 右边栏的属性初始化
		 */
		protected void menuRightProperties() {
			M_LAYOUT_POSITION = ISMENU_OPEN ? -MENU_WIDTH : 0;
			// 打开时
			M_SLIDER_MAX_OPEN = ISMENU_OPEN ? 0 : MENU_WIDTH;
			// 关闭时
			M_SLIDER_MAX_CLOSE = ISMENU_OPEN ? -MENU_WIDTH : 0;
			// 偏移位置
			mMovePosition = ISMENU_OPEN ? 0 : MENU_WIDTH;
		}

		/**
		 * 上边栏的属性初始化
		 */
		protected void menuTopProperties() {
			M_LAYOUT_POSITION = ISMENU_OPEN ? 0 : -MENU_HEIGHT;
			// 打开时
			M_SLIDER_MAX_OPEN = ISMENU_OPEN ? 0 : -MENU_HEIGHT;
			// 关闭时
			M_SLIDER_MAX_CLOSE = ISMENU_OPEN ? MENU_HEIGHT : 0;
			// 偏移位置
			mMovePosition = ISMENU_OPEN ? 0 : -MENU_HEIGHT;
		}

		/**
		 * 下边栏的属性初始化
		 */
		protected void menuBottomProperties() {
			M_LAYOUT_POSITION = ISMENU_OPEN ? -MENU_HEIGHT : 0;
			M_SLIDER_MAX_OPEN = ISMENU_OPEN ? 0 : MENU_HEIGHT;
			M_SLIDER_MAX_CLOSE = ISMENU_OPEN ? -MENU_HEIGHT : 0;
			mMovePosition = ISMENU_OPEN ? 0 : MENU_HEIGHT;
		}

		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				MOTIONEVENT_X = ev.getX();
				MOTIONEVENT_Y = ev.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				return isInterceptTouchEvent(ev);
			}
			return super.onInterceptTouchEvent(ev);
		}

		/**
		 * 判断是否需要处理事件
		 * 
		 * @param ev
		 * @return
		 */
		protected boolean isInterceptTouchEvent(MotionEvent ev) {
			boolean isIntercept = false;
			switch (MENU_POSITION) {
			case LEFT:
				isIntercept = isInterceptMenuLeftTouchEvent(ev);
				break;
			case RIGHT:
				isIntercept = isInterceptMenuRightTouchEvent(ev);
				break;
			case TOP:
				isIntercept = isInterceptMenuTopTouchEvent(ev);
				break;
			case BOTTOM:
				isIntercept = isInterceptMenuBottomTouchEvent(ev);
				break;
			}
			return isIntercept;
		}

		/**
		 * 边栏左边是否要处理
		 * 
		 * @param ev
		 * @return
		 */
		protected boolean isInterceptMenuLeftTouchEvent(MotionEvent ev) {
			// 是左右滑动
			boolean isIntercept = Math.abs(ev.getY() - MOTIONEVENT_Y) <= Math
					.abs(ev.getX() - MOTIONEVENT_X);
			return isIntercept
					&& ((MOTIONEVENT_X >= 0
							&& MOTIONEVENT_X <= TOUCH_OPEN_MENU_DISTANCE
							&& !ISMENU_OPEN && (ev.getX() - MOTIONEVENT_X) > 0) || (ev
							.getX() - MOTIONEVENT_X < 0 && ISMENU_OPEN));
		}

		/**
		 * 边栏右边是否要处理
		 * 
		 * @param ev
		 * @return
		 */
		protected boolean isInterceptMenuRightTouchEvent(MotionEvent ev) {
			// 是左右滑动
			boolean isIntercept = Math.abs(ev.getY() - MOTIONEVENT_Y) < Math
					.abs(ev.getX() - MOTIONEVENT_X);
			return isIntercept
					&& ((MOTIONEVENT_X >= (getRight() - TOUCH_OPEN_MENU_DISTANCE)
							&& MOTIONEVENT_X <= getRight() && !ISMENU_OPEN && (ev
							.getX() - MOTIONEVENT_X) < 0) || (ev.getX()
							- MOTIONEVENT_X > 0 && ISMENU_OPEN));
		}

		/**
		 * 边栏上边是否要处理
		 * 
		 * @param ev
		 * @return
		 */
		protected boolean isInterceptMenuTopTouchEvent(MotionEvent ev) {
			// 是上下滑动
			boolean isIntercept = Math.abs(ev.getY() - MOTIONEVENT_Y) > Math
					.abs(ev.getX() - MOTIONEVENT_X);
			return isIntercept
					&& ((MOTIONEVENT_Y >= 0
							&& MOTIONEVENT_Y <= TOUCH_OPEN_MENU_DISTANCE
							&& !ISMENU_OPEN && (ev.getY() - MOTIONEVENT_Y) > 0) || (ev
							.getY() - MOTIONEVENT_Y < 0 && ISMENU_OPEN));
		}

		/**
		 * 边栏下面是否要处理
		 * 
		 * @param ev
		 * @return
		 */
		protected boolean isInterceptMenuBottomTouchEvent(MotionEvent ev) {
			// 是上下滑动
			boolean isIntercept = Math.abs(ev.getY() - MOTIONEVENT_Y) > Math
					.abs(ev.getX() - MOTIONEVENT_X);
			return isIntercept
					&& ((MOTIONEVENT_Y >= (getBottom() - TOUCH_OPEN_MENU_DISTANCE)
							&& MOTIONEVENT_Y <= getBottom() && !ISMENU_OPEN && (ev
							.getY() - MOTIONEVENT_Y) < 0) || (ev.getY()
							- MOTIONEVENT_Y > 0 && ISMENU_OPEN));
		}

		/**
		 * 触摸事件
		 */
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				MOTIONEVENT_X = event.getX();
				MOTIONEVENT_Y = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				moveEvent(event);
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				checkIsOpenOrClose();
				break;
			}
			return true;
		}

		/**
		 * 移动事件
		 * 
		 * @param event
		 */
		protected void moveEvent(MotionEvent event) {
			switch (MENU_POSITION) {
			case LEFT:
				menuLeftMoveEvent(event);
				break;
			case RIGHT:
				menuRightMoveEvent(event);
				break;
			case TOP:
				menuTopMoveEvent(event);
				break;
			case BOTTOM:
				menuBottomMoveEvent(event);
				break;
			}
		}

		/**
		 * 当Menu菜单在左边时
		 * 
		 * @param event
		 */
		protected void menuLeftMoveEvent(MotionEvent event) {
			float distance = event.getX() - MOTIONEVENT_X;
			// 向右滑动，打开菜单栏
			if (distance > 0) {
				if (distance + mMovePosition >= 0) {
					ISMENU_OPEN = true;
					mMovePosition = 0;
					smoothScrollTo(M_SLIDER_MAX_OPEN, 0);
					return;
				} else {
					mMovePosition += distance;
				}
			} else if (distance < 0) {
				if (distance + mMovePosition <= -MENU_WIDTH) {
					ISMENU_OPEN = false;
					mMovePosition = -MENU_WIDTH;
					smoothScrollTo(M_SLIDER_MAX_CLOSE, 0);
					return;
				} else {
					mMovePosition += distance;
				}
			}
			smoothScrollBy((int) -distance, 0);
			MOTIONEVENT_X = event.getX();
		}

		public void open() {
			if (ISMENU_OPEN) {
				smoothScrollTo(M_SLIDER_MAX_CLOSE, 0);
				ISMENU_OPEN = false;

			} else {
				smoothScrollBy(M_SLIDER_MAX_OPEN, 0);
				ISMENU_OPEN = true;
			}

		}

		/**
		 * 边栏在右边时
		 * 
		 * @param event
		 */
		protected void menuRightMoveEvent(MotionEvent event) {
			float distance = event.getX() - MOTIONEVENT_X;
			// 向右滑动，关闭菜单栏
			if (distance > 0) {
				if (mMovePosition + distance >= MENU_WIDTH) {
					ISMENU_OPEN = false;
					mMovePosition = MENU_WIDTH;
					smoothScrollTo(M_SLIDER_MAX_CLOSE, 0);
					return;
				} else {
					mMovePosition += distance;
				}
			}
			// open
			else if (distance < 0) {
				if (mMovePosition + distance <= 0) {
					ISMENU_OPEN = true;
					mMovePosition = 0;
					smoothScrollTo(M_SLIDER_MAX_OPEN, 0);
					return;
				} else {
					mMovePosition += distance;
				}
			}
			smoothScrollBy((int) -distance, 0);
			MOTIONEVENT_X = event.getX();
		}

		/**
		 * 边栏在上面时
		 * 
		 * @param event
		 */
		protected void menuTopMoveEvent(MotionEvent event) {
			float distance = event.getY() - MOTIONEVENT_Y;
			// 向下滑动，打开菜单栏
			if (distance > 0) {
				if (mMovePosition + distance >= 0) {
					ISMENU_OPEN = true;
					mMovePosition = 0;
					smoothScrollTo(0, M_SLIDER_MAX_OPEN);
					return;
				} else {
					mMovePosition += distance;
				}
			}
			// close
			else if (distance < 0) {
				if (mMovePosition + distance <= -MENU_HEIGHT) {
					ISMENU_OPEN = false;
					mMovePosition = -MENU_HEIGHT;
					smoothScrollTo(0, M_SLIDER_MAX_CLOSE);
					return;
				} else {
					mMovePosition += distance;
				}
			}
			smoothScrollBy(0, (int) -distance);
			MOTIONEVENT_Y = event.getY();
		}

		/**
		 * 边栏在下面时
		 * 
		 * @param event
		 */
		protected void menuBottomMoveEvent(MotionEvent event) {
			float distance = event.getY() - MOTIONEVENT_Y;
			// 向下滑动，关闭菜单栏
			if (distance > 0) {
				if (mMovePosition + distance >= MENU_HEIGHT) {
					ISMENU_OPEN = false;
					mMovePosition = MENU_HEIGHT;
					smoothScrollTo(0, M_SLIDER_MAX_CLOSE);
					return;
				} else {
					mMovePosition += distance;
				}
			}
			//
			else if (distance < 0) {
				if (mMovePosition + distance <= 0) {
					ISMENU_OPEN = true;
					mMovePosition = 0;
					smoothScrollTo(0, M_SLIDER_MAX_OPEN);
					return;
				} else {
					mMovePosition += distance;
				}
			}
			smoothScrollBy(0, (int) -distance);
			MOTIONEVENT_Y = event.getY();
		}

		protected void checkIsOpenOrClose() {
			switch (MENU_POSITION) {
			case LEFT:
				checkInLeft();
				break;
			case RIGHT:
				checkInRight();
				break;
			case TOP:
				checkInTop();
				break;
			case BOTTOM:
				checkInBottom();
				break;
			}
		}

		protected void checkInLeft() {
			// Close
			if (Math.abs(mMovePosition) > (MENU_WIDTH / 2)) {
				ISMENU_OPEN = false;
				mMovePosition = -MENU_WIDTH;
				smoothScrollTo(M_SLIDER_MAX_CLOSE, 0);
			}
			// Open
			else {
				ISMENU_OPEN = true;
				mMovePosition = 0;
				smoothScrollTo(M_SLIDER_MAX_OPEN, 0);
			}
		}

		protected void checkInRight() {
			// close
			if (Math.abs(mMovePosition) > (MENU_WIDTH) / 2) {
				ISMENU_OPEN = false;
				mMovePosition = MENU_WIDTH;
				smoothScrollTo(M_SLIDER_MAX_CLOSE, 0);
			} else {
				ISMENU_OPEN = true;
				mMovePosition = 0;
				smoothScrollTo(M_SLIDER_MAX_OPEN, 0);
			}
		}

		protected void checkInTop() {
			// close
			if (Math.abs(mMovePosition) > (MENU_HEIGHT) / 2) {
				ISMENU_OPEN = false;
				mMovePosition = -MENU_HEIGHT;
				smoothScrollTo(0, M_SLIDER_MAX_CLOSE);
			} else {
				ISMENU_OPEN = true;
				mMovePosition = 0;
				smoothScrollTo(0, M_SLIDER_MAX_OPEN);
			}
		}

		protected void checkInBottom() {
			// close
			if (Math.abs(mMovePosition) > (MENU_HEIGHT) / 2) {
				ISMENU_OPEN = false;
				mMovePosition = MENU_HEIGHT;
				smoothScrollTo(0, M_SLIDER_MAX_CLOSE);
			} else {
				ISMENU_OPEN = true;
				mMovePosition = 0;
				smoothScrollTo(0, M_SLIDER_MAX_OPEN);
			}
		}

		@Override
		public void computeScroll() {
			if (mScroller.computeScrollOffset()) {
				scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
				postInvalidate();
			}
			super.computeScroll();
		}

		public LinearLayout getMenuLayout() {
			return mMenuLy;
		}

		public LinearLayout getContainerLayout() {
			return mContentLy;
		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			if (changed) {
				switch (MENU_POSITION) {
				case LEFT:
					leftLayout(l, t, r, b);
					break;
				case TOP:
					topLayout(l, t, r, b);
					break;
				case RIGHT:
					rightLayout(l, t, r, b);
					break;
				case BOTTOM:
					bottomLayout(l, t, r, b);
					break;
				}
			}
		}

		/**
		 * 菜单栏在左边时的布局
		 * 
		 * @param l
		 * @param t
		 * @param r
		 * @param b
		 */
		protected void leftLayout(int l, int t, int r, int b) {
			int screenWidth = r - l;
			int mL = l + M_LAYOUT_POSITION;
			int mR = mL + MENU_WIDTH;
			int cR = mR + screenWidth;
			mMenuLy.layout(mL, t, mR, b);
			mContentLy.layout(mR, t, cR, b);
		}

		/**
		 * 菜单栏在右边时的布局
		 * 
		 * @param l
		 * @param t
		 * @param r
		 * @param b
		 */
		protected void rightLayout(int l, int t, int r, int b) {
			int screenWidth = r - l;
			int cL = l + M_LAYOUT_POSITION;
			int cR = cL + screenWidth;
			int mR = cR + MENU_WIDTH;
			mContentLy.layout(cL, t, cR, b);
			mMenuLy.layout(cR, t, mR, b);
		}

		/**
		 * 菜单栏在上面时布局
		 * 
		 * @param l
		 * @param t
		 * @param r
		 * @param b
		 */
		protected void topLayout(int l, int t, int r, int b) {
			int screenHeight = b - t;
			int mT = t + M_LAYOUT_POSITION;
			int mB = mT + MENU_HEIGHT;
			int cB = mB + screenHeight;
			mMenuLy.layout(l, mT, r, mB);
			mContentLy.layout(l, mB, r, cB);
		}

		/**
		 * 菜单栏在下面时的布局
		 * 
		 * @param l
		 * @param t
		 * @param r
		 * @param b
		 */
		protected void bottomLayout(int l, int t, int r, int b) {
			int screenHeight = b - t;
			int cT = t + M_LAYOUT_POSITION;
			int cB = cT + screenHeight;
			int mB = cB + MENU_HEIGHT;
			mContentLy.layout(l, cT, r, cB);
			mMenuLy.layout(l, cB, r, mB);
		}

		protected void smoothScrollTo(int fx, int fy) {
			int dx = fx - mScroller.getFinalX();
			int dy = fy - mScroller.getFinalY();
			smoothScrollBy(dx, dy);
		}

		protected void smoothScrollBy(int dx, int dy) {
			mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(),
					dx, dy);
			invalidate();
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int childCount = getChildCount();
			for (int i = 0; i < childCount; ++i) {
				getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
			}
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

		/**
		 * 设置边栏菜单的宽或高 Set Position.Left Set IsOpen = false
		 * 
		 * @param menuWH
		 */
		public void setMenuWH(int menuWH) {
			if (menuWH <= 0)
				throw new IllegalArgumentException(
						"'MenuWH' Must gatter then Zero!");
			MENU_WIDTH = menuWH;
			setMenuProperty(Position.LEFT, MENU_WIDTH, false);
		}

		/**
		 * 设置位置 Set isOpen = false
		 * 
		 * @param position
		 */
		public void setPosition(Position position) {
			MENU_POSITION = position;
			setMenuProperty(MENU_POSITION, position == Position.TOP
					|| position == Position.BOTTOM ? MENU_HEIGHT : MENU_WIDTH,
					false);
		}

		/**
		 * @param position
		 *            value:[TOP, RIGHT, BOTTOM, LEFT]
		 * @param menuWH
		 *            :Menu width OR Menu height
		 * @param isOpen
		 *            : is Hide Menu in start
		 */
		public void setMenuProperty(Position position, int menuWH,
				boolean isOpen) {
			if (menuWH <= 0)
				throw new IllegalArgumentException(
						"'MenuWidth' Must gatter then Zero!");
			if (Position.LEFT == position || Position.RIGHT == position) {
				MENU_WIDTH = menuWH;
			} else {
				MENU_HEIGHT = menuWH;
			}
			MENU_POSITION = position;
			ISMENU_OPEN = isOpen;
			initProperties();
			requestLayout();
		}

		public enum Position {
			TOP, RIGHT, BOTTOM, LEFT;
			public static Position getPosition(int value) {
				Position p = null;
				switch (value) {
				case 0:
					p = Position.TOP;
					break;
				case 1:
					p = Position.RIGHT;
					break;
				case 2:
					p = Position.BOTTOM;
					break;
				case 3:
					p = Position.LEFT;
					break;
				}
				return p;
			}
		}
	}
}
