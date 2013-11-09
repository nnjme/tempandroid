package com.changlianxi.adapter;

import java.util.List;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.activity.R;
import com.changlianxi.modle.MessageModle;
import com.changlianxi.util.ExpressionUtil;
import com.changlianxi.util.ImageManager;
import com.changlianxi.util.Logger;

public class MessageAdapter extends BaseAdapter {
	private Context mContext;
	private List<MessageModle> listModle;

	public MessageAdapter(Context context, List<MessageModle> listModle) {
		this.mContext = context;
		this.listModle = listModle;
	}

	@Override
	public int getCount() {
		return listModle.size();
	}

	public void setData(List<MessageModle> listModle) {
		this.listModle = listModle;
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
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.message_list_item, null);
			holder = new ViewHolder();
			holder.otherLayout = (LinearLayout) convertView
					.findViewById(R.id.otherLayout);
			holder.otterContent = (TextView) convertView
					.findViewById(R.id.otherContent);
			holder.otherTime = (TextView) convertView
					.findViewById(R.id.otherTime);
			holder.otherAvatar = (ImageView) convertView
					.findViewById(R.id.otherAvatar);
			holder.otherName = (TextView) convertView
					.findViewById(R.id.otherName);
			holder.selfLayout = (LinearLayout) convertView
					.findViewById(R.id.selfLayout);
			holder.selfContent = (TextView) convertView
					.findViewById(R.id.selfContent);
			holder.selfTime = (TextView) convertView
					.findViewById(R.id.selfTime);
			holder.selfAvatar = (ImageView) convertView
					.findViewById(R.id.selfAvatar);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (listModle.get(position).isSelf()) {
			holder.otherLayout.setVisibility(View.GONE);
			holder.selfLayout.setVisibility(View.VISIBLE);
			// holder.selfContent.setText(listModle.get(position).getContent());
			replaceExpression(holder.selfContent, listModle.get(position)
					.getContent());

			holder.selfTime.setText(listModle.get(position).getTime());
			String path = listModle.get(position).getAvatar();
			if (path.equals("") || path == null) {
				holder.selfAvatar.setImageResource(R.drawable.hand_pic);
			} else {
				ImageManager.from(mContext).displayImage(holder.selfAvatar,
						path, R.drawable.hand_pic, 50, 50);
			}
		} else {
			holder.selfLayout.setVisibility(View.GONE);
			holder.otherLayout.setVisibility(View.VISIBLE);
			// holder.otterContent.setText(listModle.get(position).getContent());
			holder.otherTime.setText(listModle.get(position).getTime());
			replaceExpression(holder.otterContent, listModle.get(position)
					.getContent());
			holder.otherName.setText(listModle.get(position).getName());
			Logger.debug(this, "avatar:::"
					+ listModle.get(position).getAvatar());
			String path = listModle.get(position).getAvatar();
			if (path.equals("") || path == null) {
				holder.otherAvatar.setImageResource(R.drawable.hand_pic);
			} else {
				ImageManager.from(mContext).displayImage(holder.otherAvatar,
						listModle.get(position).getAvatar(),
						R.drawable.hand_pic, 50, 50);
			}

		}
		return convertView;
	}

	private void replaceExpression(TextView txt, String str) {
		String zhengze = "f0[0-9]{2}|f10[0-7]"; // 正则表达式，用来判断消息内是否有表情
		try {
			SpannableString spannableString = ExpressionUtil
					.getExpressionString(mContext, str, zhengze);
			txt.setText(spannableString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class ViewHolder {
		LinearLayout otherLayout;
		TextView otherTime;
		TextView otterContent;
		TextView otherName;
		ImageView otherAvatar;
		LinearLayout selfLayout;
		TextView selfContent;
		TextView selfTime;
		ImageView selfAvatar;
	}
}
