package com.changlianxi.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.activity.R;
import com.changlianxi.modle.MydetailChangeModle;
import com.changlianxi.task.MyDetailChangeTask;
import com.changlianxi.task.MyDetailChangeTask.GetChangedDetail;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 显示名片更改信息
 * 
 * @author teeker_bin
 * 
 */
public class MyDetailChange implements OnClickListener, GetChangedDetail {
	private Context mContext;
	private View myCardChange;
	private ImageView back;
	private OnBackClick callBack;
	private String name;
	private String avatarURL;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private CircularImage avatar;
	private TextView txtName;
	private ListView listView;
	private List<MydetailChangeModle> listModles = new ArrayList<MydetailChangeModle>();
	private MyAdapter adapter;
	private Dialog dialog;

	public MyDetailChange(Context context, String name, String avatarURL) {
		this.mContext = context;
		this.name = name;
		this.avatarURL = avatarURL;
		imageLoader = CLXApplication.getImageLoader();
		options = CLXApplication.getOptions();
		initView();
		setOnClickListener();
		getChangeDeatil();
	}

	private void initView() {
		myCardChange = LayoutInflater.from(mContext).inflate(
				R.layout.my_card_detail_change, null);
		back = (ImageView) myCardChange.findViewById(R.id.back);
		txtName = (TextView) myCardChange.findViewById(R.id.name);
		avatar = (CircularImage) myCardChange.findViewById(R.id.avatar);
		txtName.setText(name);
		imageLoader.displayImage(avatarURL, avatar, options);
		listView = (ListView) myCardChange.findViewById(R.id.listView1);
		adapter = new MyAdapter();
		listView.setAdapter(adapter);

	}

	private void setOnClickListener() {
		back.setOnClickListener(this);
	}

	private void getChangeDeatil() {
		dialog = DialogUtil.getWaitDialog(mContext, "请稍后");
		MyDetailChangeTask task = new MyDetailChangeTask();
		task.setTaskCallBack(this);
		task.execute();
		dialog.show();

	}

	public View getView() {
		return myCardChange;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			callBack.onChangeBackClick(listModles.size());
			break;
		default:
			break;
		}
	}

	public class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listModles.size();
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
			String content = listModles.get(position).getContent();
			String avatarUrl = listModles.get(position).getAvatarURL();
			String time = listModles.get(position).getTime();
			ViewHolder holder = null;
			if (convertView == null) {

				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.news_invitate1, parent, false);
				holder = new ViewHolder();
				holder.avatar = (ImageView) convertView
						.findViewById(R.id.avatarInvite);
				holder.content = (TextView) convertView
						.findViewById(R.id.contentInvite);
				holder.time = (TextView) convertView
						.findViewById(R.id.timeInvite);
				holder.btnAgree = (Button) convertView
						.findViewById(R.id.btnAgree);
				holder.btnNotAgree = (Button) convertView
						.findViewById(R.id.btnNotAgree);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.content.setText(StringUtils.ToDBC(content));
			holder.time.setText(DateUtils.interceptDateStr(time, "yyyy-MM-dd"));
			if (avatarUrl.equals("") || avatarUrl == null) {
				holder.avatar.setImageResource(R.drawable.head_bg);
			} else {
				imageLoader.displayImage(avatarUrl, holder.avatar, options);
			}
			holder.btnAgree.setOnClickListener(new BtnClick(listModles.get(
					position).getId(), listModles.get(position).getCid(),
					position));
			holder.btnNotAgree.setOnClickListener(new BtnClick(listModles.get(
					position).getId(), listModles.get(position).getCid(),
					position));
			return convertView;

		}

		class ViewHolder {
			TextView content;
			TextView time;
			ImageView avatar;
			Button btnAgree;
			Button btnNotAgree;

		}

	}

	class BtnClick implements OnClickListener {
		String amid = "";// 修改属性id
		String cid = "";
		int position;
		Map<String, Object> map = null;
		String url = "";
		String type = "";

		public BtnClick(String amid, String cid, int position) {
			this.amid = amid;
			this.cid = cid;
			this.position = position;
			map = new HashMap<String, Object>();
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("token", SharedUtils.getString("token", ""));
			map.put("cid", cid);
			map.put("amid", amid);
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnAgree:
				url = "/people/iacceptAmendment";
				this.type = "agree";
				break;
			case R.id.btnNotAgree:
				url = "/people/irefuseAmendment";
				break;
			default:
				break;
			}
			dialog = DialogUtil.getWaitDialog(mContext, "请稍后");
			dialog.show();
			PostAsyncTask task = new PostAsyncTask(mContext, map, url);
			task.setTaskCallBack(new PostCallBack() {
				@Override
				public void taskFinish(String result) {
					if (dialog != null) {
						dialog.dismiss();
					}
					try {
						JSONObject object = new JSONObject(result);
						int rt = object.getInt("rt");
						if (rt == 1) {
							listModles.remove(position);
							adapter.notifyDataSetChanged();
							if (type.equals("agree")) {
								Utils.showToast("已同意");
							} else {
								Utils.showToast("已忽略");
							}
						} else {
							String err = object.getString("err");
							Utils.showToast(ErrorCodeUtil.convertToChines(err));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			});
			task.execute();

		}
	}

	public void setCallBack(OnBackClick callBack) {
		this.callBack = callBack;
	}

	interface OnBackClick {
		void onChangeBackClick(int size);
	}

	@Override
	public void getChangeDeatils(List<MydetailChangeModle> listModle) {
		if (dialog != null) {
			dialog.dismiss();
		}
		listModles.addAll(listModle);
		adapter.notifyDataSetChanged();
	}
}
