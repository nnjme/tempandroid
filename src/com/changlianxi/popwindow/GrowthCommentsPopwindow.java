package com.changlianxi.popwindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.changlianxi.activity.CLXApplication;
import com.changlianxi.R;
import com.changlianxi.activity.ReleaseGrowthActivity;
import com.changlianxi.activity.showBigPic.ImagePagerActivity;
import com.changlianxi.adapter.GrowthImgAdapter;
import com.changlianxi.data.Growth;
import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.CommentsModle;
import com.changlianxi.modle.GrowthModle;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.ImageManager;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 成长记录详情界面
 */
public class GrowthCommentsPopwindow implements OnClickListener,
		OnItemClickListener {
	private Context mContext;// 当前对象
	private View parent;
	private Growth modle;
	private LayoutInflater flater;
	private View view;// 要加载的布局文件
	private PopupWindow popupWindow;// popWindow弹出框
	private ListView listview;
	private int cid;// 圈子id
	private int uid;// 用户id
	private int gid;// 成长记录id
	private List<CommentsModle> cModle = new ArrayList<CommentsModle>();
	private MyAdapter adapter;// 自定义adapter
	private TextView name;// 显示發佈人姓名
	private TextView content;// 显示记录内容
	private TextView time;// 显示记录发布时间
	private CircularImage img;// 发布人头像
	private TextView praise;// 点赞的数量
	private TextView comment;// 评论数量
	private GridView gridView;// 用来展示记录中的图片
	private ImageView back;
	private Button btnPublis;// 回复按钮
	private EditText edtContent;// 评论内容
	private Button edit;// 编辑按钮
	private Button del;// 删除按钮
	private ArrayList<String> urlPath = new ArrayList<String>();// 存放图片的地址的本地图片路径编辑时使用
	private ArrayList<String> imgID = new ArrayList<String>();// 存放图片的地址的ID编辑时使用
	private int pisition;
	private RecordOperation callBack;
	private TextView titleTxt;
	private ScrollView scorll;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;

	/**
	 * 构造函数
	 * 
	 * @param contxt
	 * @param parent
	 * @param modle
	 */
	public GrowthCommentsPopwindow(Context contxt, View parent,
			Growth modle, int pisition) {
		this.mContext = contxt;
		this.parent = parent;
		this.modle = modle;
		this.pisition = pisition;
		flater = LayoutInflater.from(contxt);
		view = flater.inflate(R.layout.growth_comments, null);
		imageLoader = CLXApplication.getImageLoader();
		options = CLXApplication.getOptions();
		initView();
		initPopwindow();
		cid = modle.getCid();
		gid = modle.getId();
		uid = modle.getPublisher();
//		MemberInfoModle md = DBUtils.selectNameAndImgByID(uid);
//		if (md != null) {
//			name.setText(md.getName());
//			String path = md.getAvator();
//			ImageManager.from(mContext).displayImage(img, path,
//					R.drawable.hand_pic, 60, 60);
//		}
		adapter = new MyAdapter();
		listview.setAdapter(adapter);
		new GetCommentsTask().execute();
	}

	public void setRecordOperation(RecordOperation callBack) {
		this.callBack = callBack;
	}

	/**
	 * . 初始化控件
	 */
	private void initView() {
		edit = (Button) view.findViewById(R.id.edit);
		edit.setOnClickListener(this);
		del = (Button) view.findViewById(R.id.del);
		del.setOnClickListener(this);
		btnPublis = (Button) view.findViewById(R.id.btPublish);
		btnPublis.setOnClickListener(this);
		edtContent = (EditText) view.findViewById(R.id.editContent);
		back = (ImageView) view.findViewById(R.id.back);
		back.setOnClickListener(this);
		gridView = (GridView) view.findViewById(R.id.gridView1);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridView.setOnItemClickListener(this);
		int size = modle.getImages().size();
		int average = 0;
		if (size == 1) {
			average = 1;
		} else if (size <= 4) {
			average = 2;
		} else if (size >= 5) {
			average = 4;
		}
		gridView.setNumColumns(average);
		gridView.setAdapter(new GrowthImgAdapter(mContext, modle.getImages(),
				average));
		praise = (TextView) view.findViewById(R.id.praise);
		comment = (TextView) view.findViewById(R.id.comments);
		name = (TextView) view.findViewById(R.id.name);
		time = (TextView) view.findViewById(R.id.time);
		content = (TextView) view.findViewById(R.id.content);
		img = (CircularImage) view.findViewById(R.id.img);
		time.setText(DateUtils.publishedTime(modle.getPublished()));
		content.setText(modle.getContent());
		comment.setText("评论(" + modle.getCommentCnt() + ")");
		praise.setText("赞(" + modle.getPraiseCnt() + ")");
		listview = (ListView) view.findViewById(R.id.listView);
		titleTxt = (TextView) view.findViewById(R.id.titleTxt);
		titleTxt.setText("成长");
		scorll = (ScrollView) view.findViewById(R.id.scroll);
	}

	/**
	 * 初始化popwindow
	 */
	@SuppressWarnings("deprecation")
	private void initPopwindow() {

		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setAnimationStyle(R.style.LeftAndRight);
	}

	/**
	 * popwindow的显示
	 */
	public void show() {
		popupWindow.showAtLocation(parent, Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL, 0, 0);
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 刷新状态
		popupWindow.update();
	}

	/**
	 * popwindow的隐藏
	 */
	public void dismiss() {
		popupWindow.dismiss();
	}

	/**
	 * 判断是否有权限进行编辑
	 * 
	 * @param uid
	 * @return
	 */
	private boolean isPermission(String uid) {
		if (uid.equals(SharedUtils.getString("uid", ""))) {
			return true;
		}
		return false;
	}

	/**
	 * 从服务器获取评论内容
	 * 
	 */
	class GetCommentsTask extends AsyncTask<String, Integer, String> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cid", cid);
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("token", SharedUtils.getString("token", ""));
			map.put("gid", gid);
			String result = HttpUrlHelper.postData(map, "/growth/icomments");
			try {
				JSONObject jsonobject = new JSONObject(result);
				String cid = jsonobject.getString("cid");
				String gid = jsonobject.getString("gid");
				String num = jsonobject.getString("num");
				JSONArray jsonarray = jsonobject.getJSONArray("comments");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject object = (JSONObject) jsonarray.opt(i);
					CommentsModle modle = new CommentsModle();
					String id = object.getString("id");
					String uid = object.getString("uid");
					String content = object.getString("content");
					String replyid = object.getString("replyid");
					String time = object.getString("time");
					modle.setCid(cid);
					modle.setContent(content);
					modle.setGid(gid);
					modle.setId(id);
					modle.setNum(num);
					modle.setReplyid(replyid);
					modle.setUid(uid);
					modle.setTime(time);
					cModle.add(modle);
				}
			} catch (JSONException e) {
				Logger.error(this, e);
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			adapter.notifyDataSetChanged();
			Utils.setListViewHeightBasedOnChildren(listview);
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理

		}
	}

	/**
	 * 提交回复评论
	 * 
	 */
	class PublishCommentsTask extends AsyncTask<String, Integer, String> {
		String count;
		String rt = "";
		Dialog pd;

		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cid", cid);
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("gid", gid);
			map.put("token", SharedUtils.getString("token", ""));
			map.put("content", params[0]);
			String result = HttpUrlHelper.postData(map, "/growth/imyComment");
			try {
				JSONObject jsonobject = new JSONObject(result);
				rt = jsonobject.getString("rt");
				if (rt.equals("1")) {
					count = jsonobject.getString("count");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Logger.error(this, e);
				e.printStackTrace();
			}
			return rt;
		}

		@Override
		protected void onPostExecute(String result) {
			pd.dismiss();
			if (result.equals("1")) {
				comment.setText("评论(" + count + ")");
				Utils.showToast("评论成功!");
				callBack.setComment(pisition, count);
				CommentsModle modle = new CommentsModle();
				modle.setCid(cid+"");
				modle.setUid(SharedUtils.getString("uid", ""));
				modle.setContent(edtContent.getText().toString());
				modle.setTime(DateUtils.getCurrDateStr());
				cModle.add(0, modle);
				adapter.notifyDataSetChanged();
				Utils.setListViewHeightBasedOnChildren(listview);
				scorll.scrollTo(0, 0);
				edtContent.setText("");
				edtContent.clearFocus();
				Utils.hideSoftInput(mContext);

			} else {
				Utils.showToast("评论失败!");
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			pd = DialogUtil.getWaitDialog(mContext, "请稍后");
			pd.show();
		}
	}

	/**
	 * 删除记录
	 * 
	 */
	class DelCommentsTask extends AsyncTask<String, Integer, String> {
		String rt = "";
		Dialog pd;
		String errCode;

		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cid", cid);
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("gid", gid);
			map.put("token", SharedUtils.getString("token", ""));
			String result = HttpUrlHelper
					.postData(map, "/growth/iremoveGrowth");
			try {
				JSONObject jsonobject = new JSONObject(result);
				rt = jsonobject.getString("rt");
				if (!rt.equals("1")) {
					errCode = jsonobject.getString("err");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Logger.error(this, e);
				e.printStackTrace();
			}
			return rt;
		}

		@Override
		protected void onPostExecute(String result) {
			pd.dismiss();
			if (result.equals("1")) {
				Utils.showToast("删除成功！");
				dismiss();
				callBack.delRecord(pisition);
			} else {
				Utils.showToast(ErrorCodeUtil.convertToChines(errCode));
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			pd = DialogUtil.getWaitDialog(mContext, "请稍后");
			pd.show();
		}
	}

	/**
	 * 自定义adapter用来显示评论内容
	 * 
	 * @author teeker_bin
	 * 
	 */
	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return cModle.size();
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
				holder = new ViewHolder();
				convertView = flater.inflate(R.layout.growth_comments_item,
						null);
				holder.content = (TextView) convertView
						.findViewById(R.id.content);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				holder.img = (CircularImage) convertView.findViewById(R.id.img);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			setNameAndImg("circle" + cModle.get(position).getCid(),
					cModle.get(position).getUid(), holder);
			holder.content.setText(cModle.get(position).getContent());
			holder.time.setText(DateUtils.publishedTime(cModle.get(position)
					.getTime()));
			return convertView;
		}
	}

	/**
	 * 根据id设置姓名和头像
	 * 
	 * @param tableName
	 *            //表名称
	 * @param id
	 * @param holder
	 */
	private void setNameAndImg(String tableName, String id, ViewHolder holder) {
		MemberInfoModle modle = new MemberInfoModle();
		modle = DBUtils.selectNameAndImgByID(id);
		String name = modle.getName();
		String path = modle.getAvator();
		holder.name.setText(name);
		if (path == null || path.equals("")) {
			holder.img.setImageResource(R.drawable.hand_pic);
		} else {
			imageLoader.displayImage(path, holder.img, options);
			// ImageManager.from(mContext).displayImage(holder.img, path,
			// R.drawable.hand_pic, 60, 60);
		}
	}

	class ViewHolder {
		CircularImage img;
		TextView name;
		TextView time;
		TextView content;
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

	/**
	 * 点击事件的处理
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			dismiss();
			break;
		case R.id.btPublish:
			String str = edtContent.getText().toString();
			if (str.length() == 0) {
				Utils.showToast("请输入内容！");
				return;
			}
			new PublishCommentsTask().execute(str);

			break;
		case R.id.edit:
			if (isPermission(modle.getPublisher()+"")) {
				for (int i = 0; i < modle.getImages().size(); i++) {
					urlPath.add(modle.getImages().get(i).getImg());
					imgID.add(modle.getImages().get(i).getImgId()+"");
				}
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("path", urlPath);
				bundle.putStringArrayList("imgID", imgID);
				intent.putExtras(bundle);
				intent.setClass(mContext, ReleaseGrowthActivity.class);
				intent.putExtra("type", "edit");
				intent.putExtra("cid", cid);
				intent.putExtra("gid", gid);
				intent.putExtra("content", content.getText());
				mContext.startActivity(intent);
				dismiss();
				return;
			}
			Utils.showToast("您没有编辑权限！");
			break;
		case R.id.del:
			if (isPermission(modle.getPublisher()+"")) {
				new DelCommentsTask().execute();
				return;
			}
			Utils.showToast("您没有删除权限！");
			break;
		default:
			break;
		}
	}

	/**
	 * 对成长记录的操作接口
	 * 
	 * @author teeker_bin
	 * 
	 */
	public interface RecordOperation {
		/**
		 * 删除记录
		 * 
		 * @param pisition
		 */
		public void delRecord(int pisition);

		/**
		 * 评论
		 * 
		 * @param position
		 * @return
		 */
		public void setComment(int position, String count);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		List<String> imgUrl = new ArrayList<String>();
		for (int i = 0; i < modle.getImages().size(); i++) {
			imgUrl.add(modle.getImages().get(i).getImg());
		}
		imageBrower(arg2, imgUrl.toArray(new String[imgUrl.size()]));
	}
}
