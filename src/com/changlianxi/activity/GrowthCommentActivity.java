package com.changlianxi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.changlianxi.activity.showBigPic.ImagePagerActivity;
import com.changlianxi.adapter.GrowthImgAdapter;
import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.CommentsModle;
import com.changlianxi.modle.GrowthModle;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.task.GetGrowthCommentsTask;
import com.changlianxi.task.GetGrowthCommentsTask.GetGrowthComments;
import com.changlianxi.task.PraiseAndCanclePraiseTask;
import com.changlianxi.task.PraiseAndCanclePraiseTask.PraiseAndCancle;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.changlianxi.R;

/**
 * 成长详情及评论界面
 * 
 * @author teeker_bin
 * 
 */
public class GrowthCommentActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener, GetGrowthComments {
	private GrowthModle modle;
	private LayoutInflater flater;
	private ListView listview;
	private String cid;// 圈子id
	private String uid;// 用户id
	private String gid = "";// 成长记录id
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
	private int pisition = 0;
	private static RecordOperation callBack;
	private TextView titleTxt;
	private ScrollView scorll;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private LinearLayout layParise;
	private ImageView oneImg;
	private LinearLayout layEdit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_growth_comment);
		Bundle bundle = getIntent().getExtras();
		modle = (GrowthModle) bundle.getSerializable("modle");
		pisition = getIntent().getIntExtra("position", 0);
		imageLoader = CLXApplication.getImageLoader();
		options = CLXApplication.getOptions();
		cid = modle.getCid();
		gid = modle.getId();
		uid = modle.getUid();
		initView();
		MemberInfoModle md = DBUtils.selectNameAndImgByID(uid);
		if (md != null) {
			name.setText(md.getName());
			String path = md.getAvator();
			imageLoader.displayImage(path, img, options);
		}
		adapter = new MyAdapter();
		listview.setAdapter(adapter);
		GetGrowthCommentsTask task = new GetGrowthCommentsTask(cid, gid);
		task.setTaskCallBack(this);
		task.execute();

	}

	/**
	 * 设置页面统计
	 * 
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getClass().getName() + "");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getName() + "");
	}

	/**
	 * . 初始化控件
	 */
	private void initView() {
		flater = LayoutInflater.from(this);
		edit = (Button) findViewById(R.id.edit);
		edit.setOnClickListener(this);
		del = (Button) findViewById(R.id.del);
		del.setOnClickListener(this);
		findViewById(R.id.button_share).setOnClickListener(this);
		btnPublis = (Button) findViewById(R.id.btPublish);
		btnPublis.setOnClickListener(this);
		edtContent = (EditText) findViewById(R.id.editContent);
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(this);
		oneImg = (ImageView) findViewById(R.id.oneImg);
		oneImg.setOnClickListener(this);
		gridView = (GridView) findViewById(R.id.gridView1);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridView.setOnItemClickListener(this);
		content = (TextView) findViewById(R.id.content);
		int size = modle.getImgModle().size();
		int average = 0;
		if (size == 1) {
			average = 1;
		} else if (size <= 4) {
			average = 2;
			gridView.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
		} else if (size >= 5) {
			average = 4;
			gridView.setLayoutParams(new LinearLayout.LayoutParams(400, 400));
		}
		if (modle.getContent().equals("")) {
			content.setVisibility(View.GONE);
		}
		if (average == 1) {
			String imgPath = modle.getImgModle().get(0).getImg_200();
			oneImg.setVisibility(View.VISIBLE);
			gridView.setVisibility(View.GONE);
			imageLoader.displayImage(imgPath, oneImg, options);
		} else {
			oneImg.setVisibility(View.GONE);
			gridView.setVisibility(View.VISIBLE);
			gridView.setNumColumns(average);
			gridView.setAdapter(new GrowthImgAdapter(this, modle.getImgModle(),
					average));
		}
		praise = (TextView) findViewById(R.id.praise);
		comment = (TextView) findViewById(R.id.comments);
		name = (TextView) findViewById(R.id.name);
		time = (TextView) findViewById(R.id.time);
		img = (CircularImage) findViewById(R.id.img);
		time.setText(DateUtils.publishedTime(modle.getPublish()));
		content.setText(StringUtils.ToDBC(modle.getContent()));
		comment.setText("评论（" + modle.getComment() + "）");
		praise.setText("赞（" + modle.getPraise() + "）");
		listview = (ListView) findViewById(R.id.listView);
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText("成长详情");
		scorll = (ScrollView) findViewById(R.id.scroll);
		layParise = (LinearLayout) findViewById(R.id.layParise);
		layParise.setOnClickListener(this);
		layEdit = (LinearLayout) findViewById(R.id.layedit);
		if (!isPermission(uid)) {
			layEdit.setVisibility(View.GONE);
		}
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
				modle.setCid(cid);
				modle.setUid(SharedUtils.getString("uid", ""));
				modle.setContent(edtContent.getText().toString());
				modle.setTime(DateUtils.getCurrDateStr());
				cModle.add(0, modle);
				adapter.notifyDataSetChanged();
				Utils.setListViewHeightBasedOnChildren(listview);
				scorll.scrollTo(0, 0);
				edtContent.setText("");

			} else {
				Utils.showToast("评论失败!");
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			pd = DialogUtil.getWaitDialog(GrowthCommentActivity.this, "请稍后");
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
				finish();
				Utils.rightOut(GrowthCommentActivity.this);
				callBack.delRecord(pisition);
			} else {
				Utils.showToast(ErrorCodeUtil.convertToChines(errCode));
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			pd = DialogUtil.getWaitDialog(GrowthCommentActivity.this, "请稍后");
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
			holder.img.setImageResource(R.drawable.head_bg);
		} else {
			imageLoader.displayImage(path, holder.img, options);
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
		Intent intent = new Intent(this, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, imageUrls);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		startActivity(intent);
	}

	/**
	 * 点击事件的处理
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			Utils.rightOut(this);
			break;
		case R.id.button_share:
			Intent intent1 = new Intent(GrowthCommentActivity.this,
					ShareActivity.class);
			intent1.putExtra("content", content.getText());
			intent1.putExtra("imgUrl", modle.getImgModle().get(0).getImg());
			startActivity(intent1);
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
			if (isPermission(modle.getUid())) {
				for (int i = 0; i < modle.getImgModle().size(); i++) {
					urlPath.add(modle.getImgModle().get(i).getImg_100());
					imgID.add(modle.getImgModle().get(i).getId());
				}
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("path", urlPath);
				bundle.putStringArrayList("imgID", imgID);
				intent.putExtras(bundle);
				intent.setClass(this, ReleaseGrowthActivity.class);
				intent.putExtra("type", "edit");
				intent.putExtra("cid", cid);
				intent.putExtra("gid", gid);
				intent.putExtra("content", content.getText());
				startActivity(intent);
				// dismiss();
				return;
			}
			Utils.showToast("您没有编辑权限！");
			break;
		case R.id.del:
			if (isPermission(modle.getUid())) {
				new DelCommentsTask().execute();
				return;
			}
			Utils.showToast("您没有删除权限！");
			break;
		case R.id.layParise:
			if (!modle.isIspraise()) {
				PraiseAndCancle(modle.getCid(), modle.getId(), "praise",
						"/growth/imyPraise");

				return;
			}
			PraiseAndCancle(modle.getCid(), modle.getId(), "cancle",
					"/growth/icancelPraise");
			break;
		default:
			break;
		}
	}

	/**
	 * 点赞
	 */
	private void PraiseAndCancle(String cid, String gid, String type, String url) {
		final Dialog dialog = DialogUtil.getWaitDialog(
				GrowthCommentActivity.this, "请稍后");
		dialog.show();
		PraiseAndCanclePraiseTask task = new PraiseAndCanclePraiseTask(cid,
				gid, type, url);
		task.setPraiseCallBack(new PraiseAndCancle() {
			@Override
			public void praiseAndCancle(String type, int count) {
				dialog.dismiss();
				modle.setPraise(count);
				praise.setText("赞(" + modle.getPraise() + ")");
				if (type.equals("praise")) {
					modle.setIspraise(true);
				} else {
					modle.setIspraise(false);

				}
			}

		});
		task.execute();

	}

	public static void setRecordOperation(RecordOperation callBa) {
		callBack = callBa;
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
	public void exit() {
		super.exit();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		List<String> imgUrl = new ArrayList<String>();
		for (int i = 0; i < modle.getImgModle().size(); i++) {
			imgUrl.add(modle.getImgModle().get(i).getImg());
		}
		imageBrower(arg2, imgUrl.toArray(new String[imgUrl.size()]));
	}

	@Override
	public void getGrowthComments(List<CommentsModle> listModle) {
		cModle.addAll(listModle);
		adapter.notifyDataSetChanged();
		Utils.setListViewHeightBasedOnChildren(listview);
	}
}
