package com.changlianxi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
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
import com.changlianxi.util.PushMessageReceiver.MessagePrompt;
import com.changlianxi.util.PushMessageReceiver;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.WigdtContorl;
import com.changlianxi.view.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("NewApi")
public class LeftMenuFragMent extends Fragment implements OnItemClickListener,
		MessagePrompt {
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
	private onChangeFragMentListener mOnChangeFragMentListener;

	public LeftMenuFragMent(Context context) {
		this.mcontext = context;
		PushMessageReceiver.setMessagePromptMenu(this);

	}

	public void setmOnChangeFragMentListener(
			onChangeFragMentListener mOnChangeFragMentListener) {
		this.mOnChangeFragMentListener = mOnChangeFragMentListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		getMenu();
		init();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mDesktop = inflater.inflate(R.layout.desktop, null);
		listview = (ListView) mDesktop.findViewById(R.id.menulist);
		avatar = (CircularImage) mDesktop.findViewById(R.id.avatar);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		avatar.setBackgroundResource(R.drawable.head_bg);
		return mDesktop;
	}

	private void init() {
		adapter = new MyAdapter();
		options = CLXApplication.getUserOptions();
		imageLoader = CLXApplication.getImageLoader();

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
	 * 菜单FragMent切换
	 * 
	 * @author teeker_bin
	 * 
	 */
	public interface onChangeFragMentListener {
		void onChangeFragMent(int arg0);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int posititon,
			long arg3) {
		for (int i = 0; i < menulist.size(); i++) {
			menulist.get(i).setAngle(false);
		}
		menulist.get(posititon).setAngle(true);
		adapter.notifyDataSetChanged();
		mOnChangeFragMentListener.onChangeFragMent(posititon);
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
