package com.changlianxi.view;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.activity.CLXApplication;
import com.changlianxi.db.DBUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.PushMessageReceiver;
import com.changlianxi.util.PushMessageReceiver.MessagePrompt;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.WigdtContorl;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 菜单界面
 * 
 */
public class SetMenu implements OnItemClickListener, MessagePrompt {
	/**
	 * 当前界面的View
	 */
	private View mDesktop;
	private List<MenuModle> menulist = new ArrayList<MenuModle>();
	private Context mcontext;
	private ListView listview;
	private MyAdapter adapter;
	private CircularImage avatar;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	public boolean myCardPrompt;
	public boolean messagePrompt;

	/**
	 * 接口对象,用来修改显示的View
	 */
	private onChangeViewListener mOnChangeViewListener;

	public SetMenu(Context context, boolean myCardPrompt) {
		// 绑定布局到当前View
		this.mcontext = context;
		this.myCardPrompt = myCardPrompt;
		mDesktop = LayoutInflater.from(context).inflate(R.layout.desktop, null);
		PushMessageReceiver.setMessagePromptMenu(this);
		getMenu();
		listview = (ListView) mDesktop.findViewById(R.id.menulist);
		adapter = new MyAdapter();
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		avatar = (CircularImage) mDesktop.findViewById(R.id.avatar);
		avatar.setBackgroundResource(R.drawable.head_bg);
		options = CLXApplication.getUserOptions();
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

	public void setEidtAvatar(Bitmap bmp) {
		avatar.setImageBitmap(bmp);
	}

	/**
	 * 个人名片提醒
	 * 
	 * @param prompt
	 */
	public void setMyCardPrompt(boolean prompt) {
		if (prompt) {// 改变数据库个人修改提醒状态
			ContentValues cv = new ContentValues();
			cv.put("changed", "1");
			DBUtils.updateInfo(Constants.MYDETAIL, cv, "uid=?",
					new String[] { SharedUtils.getString("uid", "") });
		}
		menulist.get(1).setNofiyPrompt(prompt);
		adapter.notifyDataSetChanged();
		this.myCardPrompt = prompt;

	}

	/**
	 * 私信提醒
	 * 
	 * @param prompt
	 */
	public void setMessagePrompt(boolean prompt) {
		menulist.get(2).setNofiyPrompt(prompt);
		adapter.notifyDataSetChanged();
		this.messagePrompt = prompt;
	}

	private void getMenu() {
		MenuModle modle = new MenuModle();
		modle.setAngle(true);
		modle.setMenu("我的圈子");
		menulist.add(modle);
		modle = new MenuModle();
		modle.setAngle(false);
		modle.setMenu("我的名片");
		modle.setNofiyPrompt(myCardPrompt);
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
				holder.notifyPrompt = (ImageView) convertView
						.findViewById(R.id.notifyPrompt);
				WigdtContorl.setLayoutX(holder.angle, (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 170,
								mcontext.getResources().getDisplayMetrics()));
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
			if (menulist.get(position).isNofiyPrompt()) {
				holder.notifyPrompt.setVisibility(View.VISIBLE);
			} else {
				holder.notifyPrompt.setVisibility(View.GONE);
			}
			return convertView;
		}
	}

	class ViewHolder {
		TextView txt;
		ImageView angle;
		ImageView notifyPrompt;
	}

	class MenuModle {
		String menu;
		boolean angle;
		boolean nofiyPrompt;

		public boolean isNofiyPrompt() {
			return nofiyPrompt;
		}

		public void setNofiyPrompt(boolean nofiyPrompt) {
			this.nofiyPrompt = nofiyPrompt;
		}

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

	@Override
	public void messagePrompt(boolean messagePrompt) {
		setMessagePrompt(messagePrompt);
		this.messagePrompt = messagePrompt;

	}

	@Override
	public void myCardPrompt(boolean myCardPrompt) {
		setMyCardPrompt(myCardPrompt);
		this.myCardPrompt = myCardPrompt;
	}

	@Override
	public void homePrompt(boolean rompt) {
		// TODO Auto-generated method stub

	}
}
