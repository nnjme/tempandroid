package com.changlianxi.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.activity.R;
import com.changlianxi.util.Logger;

/**
 * 菜单界面
 * 
 */
public class SetMenu implements OnClickListener, OnItemClickListener {
	/**
	 * 当前界面的View
	 */
	private View mDesktop;

	private List<String> menulist = new ArrayList<String>();
	private Context mcontext;
	private ListView listview;
	private MyAdapter adapter;
	private Button btn;
	private Activity mactivity;
	/**
	 * 接口对象,用来修改显示的View
	 */
	private onChangeViewListener mOnChangeViewListener;

	public SetMenu(Context context, Activity activity) {
		// 绑定布局到当前View
		this.mcontext = context;
		this.mactivity = activity;
		mDesktop = LayoutInflater.from(context).inflate(R.layout.desktop, null);
		menulist.add("我的圈子");
		menulist.add("我的名片");
		menulist.add("私信");
		menulist.add("设置");
		listview = (ListView) mDesktop.findViewById(R.id.menulist);
		adapter = new MyAdapter();
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		btn = (Button) mDesktop.findViewById(R.id.menuback);
		btn.setOnClickListener(this);
	}

	public View getView() {
		return mDesktop;
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
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();

			}
			holder.txt.setText(menulist.get(position).toString());
			return convertView;
		}
	}

	class ViewHolder {
		TextView txt;
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
			Logger.debug(this, "退出");
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int posititon,
			long arg3) {
		mOnChangeViewListener.onChangeView(posititon);
	}
}
