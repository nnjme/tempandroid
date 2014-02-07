package com.changlianxi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.changlianxi.data.Growth;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.CommentsModle;
import com.changlianxi.modle.GrowthModle;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.GetGrowthCommentsTask;
import com.changlianxi.task.GetGrowthCommentsTask.GetGrowthComments;
import com.changlianxi.task.GetGrowthIdetailTask;
import com.changlianxi.task.GetGrowthIdetailTask.GetGrowthIdetail;
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
import com.changlianxi.R;

/**
 * �ɳ����鼰���۽���
 * 
 * @author teeker_bin
 * 
 */
public class CommentsListItemActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener, GetGrowthComments,
		GetGrowthIdetail {
	private Growth modle;
	private LayoutInflater flater;
	private ListView listview;
	private String cid;// Ȧ��id
	private String gid = "";// �ɳ���¼id
	private List<CommentsModle> cModle = new ArrayList<CommentsModle>();
	private MyAdapter adapter;// �Զ���adapter
	private TextView name;// ��ʾ�l��������
	private TextView content;// ��ʾ��¼����
	private TextView time;// ��ʾ��¼����ʱ��
	private CircularImage img;// ������ͷ��
	private TextView praise;// ���޵�����
	private TextView comment;// ��������
	private GridView gridView;// ����չʾ��¼�е�ͼƬ
	private ImageView back;
	private Button btnPublis;// �ظ���ť
	private EditText edtContent;// ��������
	private Button edit;// �༭��ť
	private Button del;// ɾ����ť
	private ArrayList<String> urlPath = new ArrayList<String>();// ���ͼƬ�ĵ�ַ�ı���ͼƬ·���༭ʱʹ��
	private ArrayList<String> imgID = new ArrayList<String>();// ���ͼƬ�ĵ�ַ��ID�༭ʱʹ��
	private int pisition = 0;
	private TextView titleTxt;
	private ScrollView scorll;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private LinearLayout layParise;
	private ImageView oneImg;
	private Dialog dialog;
	private String selfName = "";
	private String avatarPath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comments_list_item);
		cid = getIntent().getStringExtra("cid");
		gid = getIntent().getStringExtra("gid");
		imageLoader = CLXApplication.getImageLoader();
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.head_bg)
				.showImageForEmptyUri(R.drawable.head_bg)
				.showImageOnFail(R.drawable.head_bg).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.ARGB_8888)
				.build();
		initView();
		getMyName();
		name.setText(selfName);
		imageLoader.displayImage(avatarPath, img, options);
		// }
		adapter = new MyAdapter();
		listview.setAdapter(adapter);
		getGrowthIdetail();
	}

	/**
	 * . ��ʼ���ؼ�
	 */
	private void initView() {
		flater = LayoutInflater.from(this);
		edit = (Button) findViewById(R.id.edit);
		edit.setOnClickListener(this);
		del = (Button) findViewById(R.id.del);
		del.setOnClickListener(this);
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

		praise = (TextView) findViewById(R.id.praise);
		comment = (TextView) findViewById(R.id.comments);
		name = (TextView) findViewById(R.id.name);
		time = (TextView) findViewById(R.id.time);
		img = (CircularImage) findViewById(R.id.img);
		listview = (ListView) findViewById(R.id.listView);
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText("�ɳ�����");
		scorll = (ScrollView) findViewById(R.id.scroll);
		layParise = (LinearLayout) findViewById(R.id.layParise);
		layParise.setOnClickListener(this);

	}

	private void getMyName() {
		MemberInfoModle modle = DBUtils.selectNameAndImgByID(SharedUtils
				.getString("uid", ""));
		if (modle != null) {
			avatarPath = modle.getAvator();
			selfName = modle.getName();
		}
	}

	private void setContent(Growth modle) {
		int size = modle.getImages().size();
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
			String imgPath = modle.getImages().get(pisition).getImg();
			oneImg.setVisibility(View.VISIBLE);
			gridView.setVisibility(View.GONE);
			imageLoader.displayImage(imgPath, oneImg, options);
		} else {
			oneImg.setVisibility(View.GONE);
			gridView.setVisibility(View.VISIBLE);
			gridView.setNumColumns(average);
			gridView.setAdapter(new GrowthImgAdapter(this, modle.getImages(),
					average));
		}
		time.setText(DateUtils.publishedTime(modle.getPublished()));
		content.setText(StringUtils.ToDBC(modle.getContent()));
		comment.setText("���ۣ�" + modle.getCommentCnt() + "��");
		praise.setText("�ޣ�" + modle.getPraiseCnt() + "��");
	}

	private void getGrowthIdetail() {
		GetGrowthCommentsTask task = new GetGrowthCommentsTask(cid, gid);
		task.setTaskCallBack(this);
		task.execute();
		GetGrowthIdetailTask task1 = new GetGrowthIdetailTask(cid, gid);
		task1.setTaskCallBack(this);
		task1.execute();
		dialog = DialogUtil.getWaitDialog(this, "���Ժ�");
		dialog.show();
	}

	/**
	 * �ж��Ƿ���Ȩ�޽��б༭
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
	 * �ύ�ظ�����
	 * 
	 */
	class PublishCommentsTask extends AsyncTask<String, Integer, String> {
		String count;
		String rt = "";
		Dialog pd;

		// �ɱ䳤�������������AsyncTask.exucute()��Ӧ
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
				comment.setText("����(" + count + ")");
				Utils.showToast("���۳ɹ�!");
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
				Utils.showToast("����ʧ��!");
			}
		}

		@Override
		protected void onPreExecute() {
			// ����������������������ʾһ���Ի�������򵥴���
			pd = DialogUtil.getWaitDialog(CommentsListItemActivity.this, "���Ժ�");
			pd.show();
		}
	}

	/**
	 * ɾ����¼
	 * 
	 */
	class DelCommentsTask extends AsyncTask<String, Integer, String> {
		String rt = "";
		Dialog pd;
		String errCode;

		// �ɱ䳤�������������AsyncTask.exucute()��Ӧ
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
				Utils.showToast("ɾ���ɹ���");
				// dismiss();
				finish();
				Utils.rightOut(CommentsListItemActivity.this);
			} else {
				Utils.showToast(ErrorCodeUtil.convertToChines(errCode));
			}
		}

		@Override
		protected void onPreExecute() {
			// ����������������������ʾһ���Ի�������򵥴���
			pd = DialogUtil.getWaitDialog(CommentsListItemActivity.this, "���Ժ�");
			pd.show();
		}
	}

	/**
	 * �Զ���adapter������ʾ��������
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
	 * ����id����������ͷ��
	 * 
	 * @param tableName
	 *            //������
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
	 * �����ͼ
	 * 
	 * @param position
	 * @param imageUrls
	 */
	private void imageBrower(int position, String imageUrls[]) {
		Intent intent = new Intent(this, ImagePagerActivity.class);
		// ͼƬurl,Ϊ����ʾ����ʹ�ó�����һ������ݿ��л������л�ȡ
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, imageUrls);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		startActivity(intent);
	}

	/**
	 * ����¼��Ĵ���
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			Utils.rightOut(this);
			break;
		case R.id.btPublish:
			String str = edtContent.getText().toString();
			if (str.length() == 0) {
				Utils.showToast("���������ݣ�");
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
				intent.setClass(this, ReleaseGrowthActivity.class);
				intent.putExtra("type", "edit");
				intent.putExtra("cid", cid);
				intent.putExtra("gid", gid);
				intent.putExtra("content", content.getText());
				startActivity(intent);
				// dismiss();
				return;
			}
			Utils.showToast("��û�б༭Ȩ�ޣ�");
			break;
		case R.id.del:
			if (isPermission(modle.getPublisher()+"")) {
				new DelCommentsTask().execute();
				return;
			}
			Utils.showToast("��û��ɾ��Ȩ�ޣ�");
			break;
		case R.id.layParise:
			
			new BaseAsyncTask<Void, Void, RetError>() {

				@Override
				protected RetError doInBackground(Void... params) {
					// TODO Auto-generated method stub
					modle.uploadMyPraise(!modle.isPraised());
					return null;
				}
			}.executeWithCheckNet();
			
//			if (!modle.isPraised()) {
//				PraiseAndCancle(modle.getCid(), modle.getId(), "praise",
//						"/growth/imyPraise");
//
//				return;
//			}
//			PraiseAndCancle(modle.getCid(), modle.getId(), "cancle",
//					"/growth/icancelPraise");
			break;
		default:
			break;
		}
	}

	/**
	 * ����
	 */
	private void PraiseAndCancle(String cid, String gid, String type, String url) {
		final Dialog dialog = DialogUtil.getWaitDialog(
				CommentsListItemActivity.this, "���Ժ�");
		dialog.show();
		PraiseAndCanclePraiseTask task = new PraiseAndCanclePraiseTask(cid,
				gid, type, url);
		task.setPraiseCallBack(new PraiseAndCancle() {
			@Override
			public void praiseAndCancle(String type, int count) {
				dialog.dismiss();
				modle.setPraiseCnt(count);
				praise.setText("��(" + modle.getPraiseCnt() + ")");
				if (type.equals("praise")) {
					modle.setPraised(true);
				} else {
					modle.setPraised(false);

				}
			}

		});
		task.execute();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		List<String> imgUrl = new ArrayList<String>();
		for (int i = 0; i < modle.getImages().size(); i++) {
			imgUrl.add(modle.getImages().get(i).getImg());
		}
		imageBrower(arg2, imgUrl.toArray(new String[imgUrl.size()]));
	}

	@Override
	public void getGrowthComments(List<CommentsModle> listModle) {
		cModle.addAll(listModle);
		adapter.notifyDataSetChanged();
		Utils.setListViewHeightBasedOnChildren(listview);
	}

	@Override
	public void getGrowthIdetail(Growth models) {
		if (dialog != null) {
			dialog.dismiss();
		}
		if (models == null) {
			return;
		}
		modle = models;
		setContent(models);
	}
}
