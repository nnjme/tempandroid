package com.changlianxi.adapter;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.activity.R;
import com.changlianxi.modle.GrowthModle;
import com.changlianxi.popwindow.GrowthCommentsPopwindow;
import com.changlianxi.popwindow.GrowthCommentsPopwindow.RecordOperation;
import com.changlianxi.popwindow.ShowBigImgPopwindow;
import com.changlianxi.task.PraiseAndCanclePraiseTask;
import com.changlianxi.task.PraiseAndCanclePraiseTask.PraiseAndCancle;
import com.changlianxi.util.ImageManager;

public class GrowthAdapter extends BaseAdapter {
	private List<GrowthModle> listData;
	private Context mContext;

	public GrowthAdapter(Context context, List<GrowthModle> modle) {
		this.mContext = context;
		this.listData = modle;
	}

	public void setData(List<GrowthModle> listData) {
		this.listData = listData;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return listData.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.growth_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			// holder.location = (TextView) convertView
			// .findViewById(R.id.txtLocate);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.praise = (TextView) convertView.findViewById(R.id.praise);
			holder.comment = (TextView) convertView.findViewById(R.id.comment);
			holder.layComment = (LinearLayout) convertView
					.findViewById(R.id.layComment);
			holder.layParise = (LinearLayout) convertView
					.findViewById(R.id.layParise);
			holder.gridView = (GridView) convertView
					.findViewById(R.id.imgGridview);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		int size = listData.get(position).getImgModle().size();
		int average = 0;
		if (size == 1) {
			average = 1;
		} else if (size <= 4) {
			average = 2;
		} else if (size >= 5) {
			average = 4;
		}
		holder.gridView.setNumColumns(average);
		holder.gridView.setAdapter(new GrowthImgAdapter(mContext, listData.get(
				position).getImgModle(), average));
		holder.gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				new ShowBigImgPopwindow(mContext, arg0, listData.get(position)
						.getImgModle(), arg2);
			}
		});
		holder.layParise.setOnClickListener(new BtnClick(holder, position));
		holder.layComment.setOnClickListener(new BtnClick(position));
		String path = listData.get(position).getPersonImg();
		if (path == null || path.equals("")) {
			holder.img.setImageResource(R.drawable.pic);
		} else {
			ImageManager.from(mContext).displayImage(holder.img, path,
					R.drawable.root_default, 60, 60);
		}
		holder.name.setText(listData.get(position).getName());
		holder.time.setText(listData.get(position).getPublish());
		// holder.location.setText(listData.get(position).getLocation());
		holder.content.setText(listData.get(position).getContent());
		holder.praise.setText(listData.get(position).getPraise() + "");
		holder.comment.setText(listData.get(position).getComment() + "");
		return convertView;
	}

	class ViewHolder {
		LinearLayout layParise;
		LinearLayout layComment;
		TextView name;
		TextView time;
		// TextView location;
		TextView content;
		TextView praise;
		TextView comment;
		// LinearLayout layImg;
		GridView gridView;
		ImageView img;
	}

	class BtnClick implements OnClickListener {
		int position;
		ViewHolder holder;
		String url;

		public BtnClick(ViewHolder holder, int position) {
			this.position = position;
			this.holder = holder;
		}

		public BtnClick(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layParise:
				ProgressDialog pd = new ProgressDialog(mContext);
				pd.show();
				if (!listData.get(position).isIspraise()) {
					PraiseAndCancle(listData.get(position).getCid(), listData
							.get(position).getId(), "praise",
							"/growth/imyPraise", position, pd);

					return;
				}
				PraiseAndCancle(listData.get(position).getCid(),
						listData.get(position).getId(), "cancle",
						"/growth/icancelPraise", position, pd);
				break;
			case R.id.layComment:
				GrowthCommentsPopwindow pop = new GrowthCommentsPopwindow(
						mContext, v, listData.get(position), position);
				pop.setRecordOperation(new RecordOperation() {
					@Override
					public void delRecord(int pisition) {
						listData.remove(pisition);
						notifyDataSetChanged();
					}

					@Override
					public void setComment(int position, String count) {
						listData.get(position).setComment(
								Integer.valueOf(count));
						notifyDataSetChanged();

					}
				});
				pop.show();
				break;
			default:
				break;
			}

		}
	}

	/**
	 * 点赞
	 */
	private void PraiseAndCancle(String cid, String gid, String type,
			String url, final int postition, final ProgressDialog pd) {
		PraiseAndCanclePraiseTask task = new PraiseAndCanclePraiseTask(cid,
				gid, type, url);
		task.setPraiseCallBack(new PraiseAndCancle() {
			@Override
			public void praiseAndCancle(String type, int count) {
				pd.dismiss();
				listData.get(postition).setPraise(count);
				if (type.equals("praise")) {
					listData.get(postition).setIspraise(true);
				} else {
					listData.get(postition).setIspraise(false);

				}
				notifyDataSetChanged();
			}

		});
		task.execute();

	}

}