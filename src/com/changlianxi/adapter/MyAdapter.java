package com.changlianxi.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.R;
import com.changlianxi.modle.MemberModle;
import com.changlianxi.view.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 用来显示圈子成员的自定义adapter
 * 
 * @author teeker_bin
 * 
 */
public class MyAdapter extends BaseAdapter {
	private List<MemberModle> list;
	private Context context;
	private HashMap<String, Integer> alphaIndexer;// 保存每个索引在list中的位置�?-0，A-4，B-10�?
	private String[] sections;// 每个分组的索引表【A,B,C,F...�?
	private DisplayImageOptions options;
	private ImageLoader imageLoader;

	public MyAdapter(Context context, List<MemberModle> list) {
		this.context = context;
		this.list = list;
		this.alphaIndexer = new HashMap<String, Integer>();
		this.sections = new String[list.size()];
		for (int i = 1; i < list.size(); i++) {
			String name = getAlpha(list.get(i).getSort_key());
			if (!alphaIndexer.containsKey(name)) {// 只记录在list中首次出现的位置
				alphaIndexer.put(name, i);
			}
		}
		Set<String> sectionLetters = alphaIndexer.keySet();
		ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
		Collections.sort(sectionList);
		sections = new String[sectionList.size()];
		sectionList.toArray(sections);
		options = CLXApplication.getUserOptions();
		imageLoader = CLXApplication.getImageLoader();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	public void setData(List<MemberModle> list) {
		this.list = list;
		notifyDataSetChanged();
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
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.user_list_item, null);
			holder.imgAuth = (ImageView) convertView.findViewById(R.id.imgAuth);
			holder.img = (CircularImage) convertView.findViewById(R.id.userimg);
			holder.info = (TextView) convertView.findViewById(R.id.userinfo);
			holder.name = (TextView) convertView.findViewById(R.id.username);
			holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
			holder.news = (TextView) convertView.findViewById(R.id.userdt);
			holder.changeBg = (LinearLayout) convertView
					.findViewById(R.id.changebg);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (list.get(position).isAuth()) {
			holder.imgAuth.setVisibility(View.VISIBLE);
		} else {
			holder.imgAuth.setVisibility(View.GONE);
		}
		String employer = list.get(position).getEmployer();
		holder.info.setText(employer.equals("") ? list.get(position)
				.getLocation() : employer);
		holder.name.setText(list.get(position).getName());
		holder.news.setText(list.get(position).getMobileNum());
		showAlpha(position, holder);
		if (position % 2 == 0) {
			holder.changeBg.setBackgroundColor(Color.WHITE);
		} else {
			holder.changeBg.setBackgroundColor(context.getResources().getColor(
					R.color.f6));
		}
		String path = list.get(position).getImg();
		if (path.equals("") || path == null) {
			holder.img.setImageResource(R.drawable.head_bg);
		} else {
			imageLoader.displayImage(path, holder.img, options);
		}
		return convertView;
	}

	private void showAlpha(int position, ViewHolder holder) {
		// 当前联系人的sortKey
		String currentStr = getAlpha(list.get(position).getSort_key());
		// 上一个联系人的sortKey
		String previewStr = (position - 1) >= 0 ? getAlpha(list.get(
				position - 1).getSort_key()) : " ";
		/**
		 * 判断显示#、A-Z的TextView隐藏与可�?
		 */
		if (!previewStr.equals(currentStr)) { // 当前联系人的sortKey�?上一个联系人的sortKey，说明当前联系人是新组�?
			holder.alpha.setVisibility(View.VISIBLE);
			holder.alpha.setText(currentStr);
		} else {
			holder.alpha.setVisibility(View.GONE);
		}
	}

	/**
	 * 提取英文的首字母，非英文字母�?代替�?
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}

		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase(); // 大写输出
		} else {
			return "#";
		}

	}

	class ViewHolder {
		ImageView imgAuth;
		CircularImage img;
		TextView info;
		TextView news;
		TextView name;
		TextView alpha;
		LinearLayout changeBg;
	}
}
