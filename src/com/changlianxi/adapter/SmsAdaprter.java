package com.changlianxi.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.activity.R;
import com.changlianxi.modle.SmsPrevieModle;
import com.changlianxi.popwindow.DialogPopWindow;
import com.changlianxi.popwindow.DialogPopWindow.OnButtonOnclick;
import com.changlianxi.popwindow.SmsSetNickNamePopWindow;

/**
 * 短信预览界面
 * 
 * @author teeker_bin
 * 
 */
public class SmsAdaprter extends BaseAdapter {
	private Context mContext;
	private List<SmsPrevieModle> contactsList;
	private SmsSetNickNamePopWindow pop;
	private LinearLayout vtitle;

	public SmsAdaprter(Context context, List<SmsPrevieModle> listdata,
			LinearLayout view) {
		this.mContext = context;
		this.contactsList = listdata;
		this.vtitle = view;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return contactsList.size();
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
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.sms_list_item, null);
			holder.content = (TextView) convertView
					.findViewById(R.id.smsContent);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.setNickName = (LinearLayout) convertView
					.findViewById(R.id.setNiceName);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.setNickName.setOnClickListener(new OnClick(position));
		holder.name.setText(contactsList.get(position).getName());
		holder.content.setText(contactsList.get(position).getContent());
		return convertView;
	}

	class OnClick implements OnClickListener {
		int position;

		public OnClick(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			pop = new SmsSetNickNamePopWindow(mContext, v, new PopClick(
					position));
			pop.show();
		}
	}

	/**
	 * SmsSetNickNamePopWindow 中设置昵称按钮 的事件
	 * 
	 * @author teeker_bin
	 * 
	 */
	class PopClick implements OnClickListener {
		int position;

		public PopClick(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			DialogPopWindow diaPop = new DialogPopWindow(mContext, vtitle);
			diaPop.setOnlistOnclick(new OnButtonOnclick() {

				@Override
				public void onclick(String str) {
					contactsList.get(position).setName(str);
					notifyDataSetChanged();
				}
			});
			diaPop.show();
			pop.dismiss();

		}
	}

	class ViewHolder {
		TextView name;
		TextView content;
		LinearLayout setNickName;
	}
}
