package com.changlianxi.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.activity.GrowthCommentActivity;
import com.changlianxi.activity.GrowthCommentActivity.RecordOperation;
import com.changlianxi.R;
import com.changlianxi.activity.showBigPic.ImagePagerActivity;
import com.changlianxi.data.Growth;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.modle.GrowthModle;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.PraiseAndCanclePraiseTask;
import com.changlianxi.task.PraiseAndCanclePraiseTask.PraiseAndCancle;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.StringUtils;
import com.changlianxi.view.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GrowthAdapter extends BaseAdapter {
	//private List<GrowthModle> listData;
	private List<Growth> listData;
	private Context mContext;
	private DisplayImageOptions options;
	private DisplayImageOptions options1;
	private ImageLoader imageLoader;

	public GrowthAdapter(Context context, List<Growth> modle) {
		this.mContext = context;
		this.listData = modle;
		options = CLXApplication.getUserOptions();
		imageLoader = CLXApplication.getImageLoader();
		options1 = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.empty_photo)
				.showImageForEmptyUri(R.drawable.empty_photo)
				.showImageOnFail(R.drawable.empty_photo).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.ARGB_8888)
				.build();
	}

	public void setData(List<Growth> listData) {
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
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.praise = (TextView) convertView.findViewById(R.id.praise);
			holder.comment = (TextView) convertView.findViewById(R.id.comment);
			holder.location = (TextView) convertView
					.findViewById(R.id.location);
			holder.layComment = (LinearLayout) convertView
					.findViewById(R.id.layComment);
			holder.layParise = (LinearLayout) convertView
					.findViewById(R.id.layParise);
			holder.gridView = (GridView) convertView
					.findViewById(R.id.imgGridview);
			holder.gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
			holder.avatar = (CircularImage) convertView
					.findViewById(R.id.avatar);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		int size = listData.get(position).getImages().size();
		int average = 0;
		if (size == 1) {
			average = 1;
		} else if (size <= 4) {
			average = 2;
			holder.gridView.setLayoutParams(new LinearLayout.LayoutParams(300,
					300));
		} else if (size >= 5) {
			average = 4;
			holder.gridView.setLayoutParams(new LinearLayout.LayoutParams(400,
					400));
		}
		if (average == 1) {
			String imgPath = listData.get(position).getImages().get(0)
					.getImg();
			holder.img.setVisibility(View.VISIBLE);
			holder.gridView.setVisibility(View.GONE);
			imageLoader.displayImage(imgPath, holder.img, options1);
			holder.img.setOnClickListener(new ImgOnClick(listData.get(position)
					.getImages().get(0).getImg()));
		} else {
			holder.img.setVisibility(View.GONE);
			holder.gridView.setVisibility(View.VISIBLE);
			holder.gridView.setNumColumns(average);
			holder.gridView.setAdapter(new GrowthImgAdapter(mContext, listData
					.get(position).getImages(), average));
			holder.gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					List<String> imgUrl = new ArrayList<String>();
					for (int i = 0; i < listData.get(position).getImages()
							.size(); i++) {
						imgUrl.add(listData.get(position).getImages().get(i)
								.getImg());
					}
					imageBrower(arg2, imgUrl.toArray(new String[imgUrl.size()]));
				}
			});
		}
		holder.location.setText(listData.get(position).getLocation());
		holder.layParise.setOnClickListener(new BtnClick(holder, position));
		holder.layComment.setOnClickListener(new BtnClick(position));
		String path = listData.get(position).getAvatar();
		if (path == null || path.equals("")) {
			holder.avatar.setImageResource(R.drawable.head_bg);
		} else {
			imageLoader.displayImage(path, holder.avatar, options);
		}
		holder.name.setText(listData.get(position).getName());
		holder.time.setText(DateUtils.publishedTime(listData.get(position)
				.getPublished()));
		String content = StringUtils.ToDBC(listData.get(position).getContent());
		if (content.length() == 0) {
			holder.content.setVisibility(View.GONE);
		}
		holder.content.setText(content);
		holder.praise.setText("赞（" + listData.get(position).getPraiseCnt() + "）");
		holder.comment.setText("评论（" + listData.get(position).getCommentCnt()
				+ "）");
		return convertView;
	}

	class ViewHolder {
		LinearLayout layParise;
		LinearLayout layComment;
		TextView name;
		TextView time;
		TextView content;
		TextView praise;
		TextView comment;
		GridView gridView;
		CircularImage avatar;
		TextView location;
		ImageView img;
	}

	class ImgOnClick implements OnClickListener {
		String path;

		public ImgOnClick(String path) {
			this.path = path;
		}

		@Override
		public void onClick(View v) {
			imageBrower(1, new String[] { path });
		}

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
				Dialog pd = DialogUtil.getWaitDialog(mContext, "请稍后");
				pd.show();
				
				final Growth growth = listData.get(position);
				
				new BaseAsyncTask<Void, Void, RetError>() {

					@Override
					protected RetError doInBackground(Void... params) {
						// TODO Auto-generated method stub
						growth.uploadMyPraise(!growth.isPraised());
						return null;
					}
				}.executeWithCheckNet();
				
//				if (!listData.get(position).isPraised()) {
//					PraiseAndCancle(listData.get(position).getCid(), listData
//							.get(position).getId(), "praise",
//							"/growth/imyPraise", position, pd);
//
//					return;
//				}
//				PraiseAndCancle(listData.get(position).getCid(),
//						listData.get(position).getId(), "cancle",
//						"/growth/icancelPraise", position, pd);
				break;
			case R.id.layComment:
				Growth modle = listData.get(position);
				Intent intent = new Intent(mContext,
						GrowthCommentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("modle", (Serializable) modle);
				intent.putExtras(bundle);
				intent.putExtra("position", position);
				mContext.startActivity(intent);
				((Activity) mContext).getParent().overridePendingTransition(
						R.anim.in_from_right, R.anim.out_to_left);
				GrowthCommentActivity.setRecordOperation(new RecordOperation() {
					@Override
					public void delRecord(int pisition) {
						listData.remove(pisition);
						notifyDataSetChanged();
					}

					@Override
					public void setComment(int position, String count) {
						listData.get(position).setCommentCnt(
								Integer.valueOf(count));
						notifyDataSetChanged();

					}
				});
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
			String url, final int postition, final Dialog pd) {
		PraiseAndCanclePraiseTask task = new PraiseAndCanclePraiseTask(cid,
				gid, type, url);
		task.setPraiseCallBack(new PraiseAndCancle() {
			@Override
			public void praiseAndCancle(String type, int count) {
				pd.dismiss();
				listData.get(postition).setPraiseCnt(count);
				if (type.equals("praise")) {
					listData.get(postition).setPraised(true);
				} else {
					listData.get(postition).setPraised(false);

				}
				notifyDataSetChanged();
			}

		});
		task.execute();

	}

	/**
	 * 浏览大图
	 * 
	 * @param position
	 * @param imageUrls
	 */
	private void imageBrower(int position, String imageUrls[]) {
		Intent intent = new Intent(mContext, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, imageUrls);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		mContext.startActivity(intent);
	}
}