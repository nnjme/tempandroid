package com.changlianxi.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.R;
import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.NewsModle;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 动态展示
 * 
 * @author teeker_bin
 * 
 */
public class NewsListAdapter extends BaseAdapter {
	private Context mCotext;
	private List<NewsModle> listModle;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private final int TYPE_1 = 1;
	private final int TYPE_2 = 2;

	public NewsListAdapter(Context context, List<NewsModle> listModle) {
		this.mCotext = context;
		this.listModle = listModle;
		imageLoader = CLXApplication.getImageLoader();
		options = CLXApplication.getUserOptions();
	}

	@Override
	public int getCount() {
		return listModle.size();
	}

	private void notifyData() {
		notifyDataSetChanged();
	}

	public void setData(List<NewsModle> listModle) {
		this.listModle = listModle;
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		String need = listModle.get(position).getNeed_approve();
		if (need.equals("1")) {
			return TYPE_1;
		}
		return TYPE_2;
	}

	// @Override
	// public int getViewTypeCount() {
	// return 2;
	// }

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 获取到当前位置所对应的Type
		String content = listModle.get(position).getContent();
		String user1Name = listModle.get(position).getUser1Name();
		String user2Name = listModle.get(position).getUser2Name();
		String avatarUrl = listModle.get(position).getAvatarUrl();
		String detail = listModle.get(position).getDetail();
		ViewHolderInvite holderInvite = null;
		ViewHolderOther holderOther = null;
		int type = getItemViewType(position);
		// if (convertView == null) {
		switch (type) {
		case TYPE_1:
			convertView = LayoutInflater.from(mCotext).inflate(
					R.layout.news_invitate1, null);
			holderInvite = new ViewHolderInvite();
			holderInvite.avatarInvite = (ImageView) convertView
					.findViewById(R.id.avatarInvite);
			holderInvite.contentInvite = (TextView) convertView
					.findViewById(R.id.contentInvite);
			holderInvite.timeInvite = (TextView) convertView
					.findViewById(R.id.timeInvite);
			holderInvite.btnAgreeInvite = (Button) convertView
					.findViewById(R.id.btnAgree);
			holderInvite.btnNotAgreeInvite = (Button) convertView
					.findViewById(R.id.btnNotAgree);
			convertView.setTag(holderInvite);
			break;
		case TYPE_2:
			convertView = LayoutInflater.from(mCotext).inflate(
					R.layout.news_list_item, null);
			holderOther = new ViewHolderOther();
			holderOther.avatar = (ImageView) convertView
					.findViewById(R.id.avatar);
			holderOther.content = (TextView) convertView
					.findViewById(R.id.content);
			holderOther.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holderOther);
			break;
		default:
			break;
		}
		// } else {
		// switch (type) {
		// case TYPE_1:
		// holderInvite = (ViewHolderInvite) convertView.getTag();
		// break;
		// case TYPE_2:
		// holderOther = (ViewHolderOther) convertView.getTag();
		// break;
		// default:
		// break;
		// }
		// }
		switch (type) {
		case TYPE_1:
			holderInvite.contentInvite.setText(Html.fromHtml(replaceUser(
					content, user1Name, user2Name) + "。" + getDetail(detail)));
			holderInvite.timeInvite.setText(DateUtils.interceptDateStr(
					listModle.get(position).getCreatedTime(), "yyyy-MM-dd"));
			if (avatarUrl.equals("") || avatarUrl == null) {
				holderInvite.avatarInvite.setImageResource(R.drawable.head_bg);
			} else {
				imageLoader.displayImage(avatarUrl, holderInvite.avatarInvite,
						options);

			}
			String pid = listModle.get(position).getPerson2();
			holderInvite.btnAgreeInvite.setOnClickListener(new BtnClick(pid
					.equals("0") ? listModle.get(position).getUser1() : pid,
					listModle.get(position).getCid(), position));
			holderInvite.btnNotAgreeInvite.setOnClickListener(new BtnClick(pid
					.equals("0") ? listModle.get(position).getUser1() : pid,
					listModle.get(position).getCid(), position));
			break;
		case TYPE_2:
			if (listModle.get(position).getType().equals("TYPE_ENTERING")) {
				detail = getDetail(detail);
				holderOther.content.setText(Html.fromHtml(replaceUser(content,
						user1Name, user2Name) + "。" + detail));
			} else {
				holderOther.content.setText(Html.fromHtml(replaceUser(content,
						user1Name, user2Name) + "。" + detail));

			}
			holderOther.time.setText(DateUtils.interceptDateStr(
					listModle.get(position).getCreatedTime(), "yyyy-MM-dd"));
			if (avatarUrl.equals("") || avatarUrl == null) {
				holderOther.avatar.setImageResource(R.drawable.head_bg);
			} else {
				imageLoader
						.displayImage(avatarUrl, holderOther.avatar, options);
			}
			break;
		default:
			break;
		}

		return convertView;

	}

	private String getDetail(String detail) {
		if (detail.equals("")) {
			return detail;
		}
		return "<font color=\"#fd7a00\">(" + detail + ")</font>";

	}

	private String replaceUser(String content, String user1, String user2) {
		String userName1 = "";
		userName1 = "<font color=\"#000000\">" + user1 + "</font>";
		if (user2 == null || user2.equals("")) {
			return content.replace("[X]", userName1);
		}
		String userName2 = "<font color=\"#000000\">" + user2 + "</font>";
		return content.replace("[X]", userName1).replace("[Y]", userName2);
	}

	class ViewHolderOther {
		TextView content;
		TextView time;
		ImageView avatar;
	}

	class ViewHolderInvite {
		TextView contentInvite;
		TextView timeInvite;
		ImageView avatarInvite;
		Button btnAgreeInvite;
		Button btnNotAgreeInvite;

	}

	class BtnClick implements OnClickListener {
		String pid = "";
		String cid = "";
		String type = "";
		int position;
		Map<String, Object> map = null;
		Dialog pd;
		String url = "";

		public BtnClick(String pid, String cid, int position) {
			this.pid = pid;
			this.cid = cid;
			this.position = position;
			if (listModle.get(position).getType().equals("TYPE_ENTERING")) {
				url = "/news/ienterApprove";
			} else if (listModle.get(position).getType().equals("TYPE_KICKOUT")) {
				url = "/news/ikickoutApprove";
			}
			map = new HashMap<String, Object>();
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("token", SharedUtils.getString("token", ""));
			map.put("cid", cid);
			map.put("pid", DBUtils.getPidByUid(pid));
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnAgree:
				if (listModle.get(position).getType().equals("TYPE_ENTERING")) {
					map.put("attitude", "1");
				}
				this.type = "agree";
				break;
			case R.id.btnNotAgree:
				if (listModle.get(position).getType().equals("TYPE_ENTERING")) {
					map.put("attitude", "0");
				}
				break;
			default:
				break;
			}
			pd = DialogUtil.getWaitDialog(mCotext, "请稍后");
			pd.show();
			PostAsyncTask task = new PostAsyncTask(mCotext, map, url);
			task.setTaskCallBack(new PostCallBack() {
				@Override
				public void taskFinish(String result) {
					System.out.println("result:::::" + result);
					pd.dismiss();
					try {
						JSONObject object = new JSONObject(result);
						int rt = object.getInt("rt");
						if (rt == 1) {
							String detail = "";
							if (type.equals("agree")) {
								detail = object.getString("detail");

								Utils.showToast("已同意");
							} else {
								Utils.showToast("已忽略");
							}
							listModle.get(position).setNeed_approve("0");
							listModle.get(position).setDetail(detail);
							notifyData();
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
}
