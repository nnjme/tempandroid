package com.changlianxi.popwindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.changlianxi.activity.R;

/**
 * 圈子设置提示框
 * 
 * @author teeker_bin
 * 
 */
public class ListViewPopwindow implements OnItemClickListener {
	private PopupWindow popupWindow;
	private Context mContext;
	private View v;
	private View view;
	private ListView listview;
	private String array[];
	private MyAdapter adapter;
	private OnlistOnclick callback;

	public ListViewPopwindow(Context context, View v, String array[]) {
		this.mContext = context;
		this.v = v;
		this.array = array;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.listview, null);
		initView();
		initPopwindow();
	}

	private void initView() {
		listview = (ListView) view.findViewById(R.id.listView1);
		listview.setOnItemClickListener(this);
		adapter = new MyAdapter();
		listview.setAdapter(adapter);
	}

	/**
	 * 初始化popwindow
	 */
	private void initPopwindow() {
		popupWindow = new PopupWindow(view, v.getWidth() - 20,
				v.getHeight() * 4);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	/**
	 * popwindow的显示
	 */
	public void show() {
		popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, v.getHeight());
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

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return array.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.listview_textview_item, null);
				holder = new ViewHolder();
				holder.laybg = (LinearLayout) convertView
						.findViewById(R.id.laybg);
				holder.text = (TextView) convertView.findViewById(R.id.text);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (position % 2 == 0) {
				holder.laybg.setBackgroundColor(Color.WHITE);
			} else {
				holder.laybg.setBackgroundColor(mContext.getResources()
						.getColor(R.color.f6));
			}
			holder.text.setText(array[position]);
			return convertView;
		}
	}

	class ViewHolder {
		TextView text;
		LinearLayout laybg;
	}

	public void setOnlistOnclick(OnlistOnclick callback) {
		this.callback = callback;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		String str = array[position];
		callback.onclick(str);
		dismiss();
	}

	public interface OnlistOnclick {
		void onclick(String str);
	}
}
