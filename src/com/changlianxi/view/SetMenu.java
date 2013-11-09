package com.changlianxi.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.activity.R;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.util.WigdtContorl;

/**
 * 菜单界面
 * 
 */
public class SetMenu implements OnClickListener, OnItemClickListener {
	/**
	 * 当前界面的View
	 */
	private View mDesktop;

	private List<MenuModle> menulist = new ArrayList<MenuModle>();
	private Context mcontext;
	private ListView listview;
	private MyAdapter adapter;
	private Button btn;
	private Activity mactivity;
	private CircularImage avatar;
	private ImageView avatarBg;
	/**
	 * 接口对象,用来修改显示的View
	 */
	private onChangeViewListener mOnChangeViewListener;

	public SetMenu(Context context, Activity activity) {
		// 绑定布局到当前View
		this.mcontext = context;
		this.mactivity = activity;
		mDesktop = LayoutInflater.from(context).inflate(R.layout.desktop, null);
		getMenu();
		listview = (ListView) mDesktop.findViewById(R.id.menulist);
		adapter = new MyAdapter();
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		btn = (Button) mDesktop.findViewById(R.id.menuback);
		btn.setOnClickListener(this);
		avatar = (CircularImage) mDesktop.findViewById(R.id.acatarImg);
		avatar.setImageResource(R.drawable.menu_pic);
		avatarBg = (ImageView) mDesktop.findViewById(R.id.acatarBg);
		// WigdtContorl.setAvatarWidth(mcontext, avatar, avatarBg);
	}

	public View getView() {
		return mDesktop;
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
				int mWidth = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 136, mcontext
								.getResources().getDisplayMetrics());
				WigdtContorl.setLayoutX(holder.angle,
						Utils.getSecreenWidth(mcontext) - mWidth);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();

			}
			if (menulist.get(position).isAngle()) {
				holder.angle.setVisibility(View.VISIBLE);
			} else {
				holder.angle.setVisibility(View.GONE);

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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menuback:
			mactivity.finish();
			SharedUtils.setString("uid", "");
			SharedUtils.setString("token", "");
			Logger.debug(this, "退出");
			break;
		default:
			break;
		}
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
