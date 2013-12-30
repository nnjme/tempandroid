package com.changlianxi.popwindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.changlianxi.activity.R;
import com.changlianxi.util.WigdtContorl;

/**
 * 用户资料编辑界面增加属性选择框
 * 
 * @author teeker_bin
 * 
 */
public class AddKeyAndValuePopwindow implements OnClickListener,
		OnItemClickListener {
	private PopupWindow popupWindow;
	private Context mContext;
	private View v;
	private View view;
	private OnSelectKey callback;
	private LinearLayout bg;
	private String array[];
	private ListView listview;
	private MyAdapter adapter;
	private Button title;
	private String titleStr;

	public AddKeyAndValuePopwindow(Context context, View v, String array[],
			String titleStr) {
		this.mContext = context;
		this.v = v;
		this.array = array;
		this.titleStr = titleStr;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.user_info_edit_add_dialog, null);
		adapter = new MyAdapter();
		initView();
		initPopwindow();
	}

	private void initView() {
		bg = (LinearLayout) view.findViewById(R.id.layoutBg);
		bg.getBackground().setAlpha(200);
		bg.setOnClickListener(this);
		title = (Button) view.findViewById(R.id.title);
		title.setText(titleStr);
		listview = (ListView) view.findViewById(R.id.keyListView);
		listview.setOnItemClickListener(this);
		listview.setAdapter(adapter);
		WigdtContorl.setListViewHeight(listview);
	}

	/**
	 * 初始化popwindow
	 */
	@SuppressWarnings("deprecation")
	private void initPopwindow() {
		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		// 这个是为了点击�?返回Back”也能使其消失，并且并不会影响你的背景（很神奇的�?
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	/**
	 * popwindow的显�?
	 */
	public void show() {
		popupWindow.showAtLocation(v, 0, 0, 0);
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 刷新状�?
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
				holder.laybg.setBackgroundResource(R.drawable.enter_td_bg);
			}
			holder.text.setText(array[position]);
			return convertView;
		}
	}

	class ViewHolder {
		TextView text;
		LinearLayout laybg;
	}

	public void setCallBack(OnSelectKey callback) {
		this.callback = callback;
	}

	public interface OnSelectKey {
		void getSelectKey(String str);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layoutBg:
			dismiss();
			break;

		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		callback.getSelectKey(array[arg2]);
		dismiss();
	}

}
