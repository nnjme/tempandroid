package com.changlianxi.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.activity.R;
import com.changlianxi.util.Utils;
import com.changlianxi.util.WigdtContorl;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 菜单界面
 * 
 */
public class SetMenu implements OnItemClickListener {
	/**
	 * 当前界面的View
	 */
	private View mDesktop;

	private List<MenuModle> menulist = new ArrayList<MenuModle>();
	private Context mcontext;
	private ListView listview;
	private MyAdapter adapter;
	private static CircularImage avatar;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	/**
	 * 接口对象,用来修改显示的View
	 */
	private onChangeViewListener mOnChangeViewListener;

	public SetMenu(Context context, Activity activity) {
		// 绑定布局到当前View
		this.mcontext = context;
		mDesktop = LayoutInflater.from(context).inflate(R.layout.desktop, null);
		getMenu();
		listview = (ListView) mDesktop.findViewById(R.id.menulist);
		adapter = new MyAdapter();
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		avatar = (CircularImage) mDesktop.findViewById(R.id.avatar);
		avatar.setBackgroundResource(R.drawable.menu_pic);
		options = CLXApplication.getOptions();
		imageLoader = CLXApplication.getImageLoader();
	}

	public View getView() {
		return mDesktop;
	}

	/**
	 * 设置头像
	 * 
	 * @param avatarUrl
	 */
	public void setAvatar(String avatarUrl) {
		imageLoader.displayImage(avatarUrl, avatar, options);
	}

	public static void setEidtAvatar(Bitmap bmp) {
		avatar.setImageBitmap(bmp);
	}

	private void getMenu() {
		MenuModle modle = new MenuModle();
		modle.setAngle(true);
		modle.setMenu("我的圈子");
		menulist.add(modle);
		modle = new MenuModle();
		modle.setAngle(false);
		modle.setMenu("我的名片");
		menulist.add(modle);
		modle = new MenuModle();
		modle.setAngle(false);
		modle.setMenu("私信");
		menulist.add(modle);
		modle = new MenuModle();
		modle.setAngle(false);
		modle.setMenu("设置");
		menulist.add(modle);
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return menulist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mcontext).inflate(
						R.layout.menu_item, null);
				holder = new ViewHolder();
				holder.txt = (TextView) convertView.findViewById(R.id.menutxt);
				holder.angle = (ImageView) convertView.findViewById(R.id.angle);
				WigdtContorl.setLayoutX(
						holder.angle,
						Utils.getSecreenWidth(mcontext)
								/ 2
								- (int) mcontext.getResources().getDimension(
										R.dimen.menu));
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();

			}
			if (menulist.get(position).isAngle()) {
				holder.angle.setVisibility(View.VISIBLE);
				holder.txt.setTextColor(Color.WHITE);
			} else {
				holder.angle.setVisibility(View.GONE);
				holder.txt.setTextColor(mcontext.getResources().getColor(
						R.color.default_font_color));

			}
			holder.txt.setText(menulist.get(position).getMenu());
			return convertView;
		}
	}

	class ViewHolder {
		TextView txt;
		ImageView angle;
	}

	class MenuModle {
		String menu;
		boolean angle;

		public String getMenu() {
			return menu;
		}

		public void setMenu(String menu) {
			this.menu = menu;
		}

		public boolean isAngle() {
			return angle;
		}

		public void setAngle(boolean angle) {
			this.angle = angle;
		}

	}

	/**
	 * 界面修改方法
	 * 
	 * @param onChangeViewListener
	 */
	public void setOnChangeViewListener(
			onChangeViewListener onChangeViewListener) {
		mOnChangeViewListener = onChangeViewListener;
	}

	/**
	 * 切换显示界面的接口
	 * 
	 * 
	 */
	public interface onChangeViewListener {
		public abstract void onChangeView(int arg0);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int posititon,
			long arg3) {
		mOnChangeViewListener.onChangeView(posititon);
		for (int i = 0; i < menulist.size(); i++) {
			menulist.get(i).setAngle(false);
		}
		menulist.get(posititon).setAngle(true);
		adapter.notifyDataSetChanged();

	}
}
