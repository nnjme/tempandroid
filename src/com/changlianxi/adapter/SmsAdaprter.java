package com.changlianxi.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.activity.R;
import com.changlianxi.modle.ContactModle;
import com.changlianxi.popwindow.SmsSetNickNamePopWindow;

/**
 * 短信预览界面
 * 
 * @author teeker_bin
 * 
 */
public class SmsAdaprter extends BaseAdapter {
	private Context mContext;
	private List<ContactModle> contactsList;
	private String cirName;
	private SmsSetNickNamePopWindow pop;

	public SmsAdaprter(Context context, List<ContactModle> listdata,
			String cirName) {
		this.mContext = context;
		this.contactsList = listdata;
		this.cirName = cirName;
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
		holder.content.setText("亲爱的" + contactsList.get(position).getName()
				+ ",邀请您加入" + cirName + "圈子,"
				+ mContext.getResources().getString(R.string.sms_content));
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
			inputNickNameDialog(position);
			pop.dismiss();

		}
	}

	private void inputNickNameDialog(final int position) {
		final EditText inputServer = new EditText(mContext);
		inputServer.setFocusable(true);

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setView(inputServer).setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				String inputName = inputServer.getText().toString();
				contactsList.get(position).setName(inputName);
				notifyDataSetChanged();
			}
		});
		builder.show();
	}

	class ViewHolder {
		TextView name;
		TextView content;
		LinearLayout setNickName;
	}
}
