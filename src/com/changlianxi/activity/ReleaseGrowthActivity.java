package com.changlianxi.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.inteface.UpLoadPic;
import com.changlianxi.modle.SelectPicModle;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.task.UpLoadGrowthPicTask;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.GrowthImgGridView;

public class ReleaseGrowthActivity extends Activity implements OnClickListener,
		UpLoadPic {
	private ImageView addPic;
	private TextView time;
	private EditText location;// 地点输入框
	private EditText content;// 内容输入框
	private String cid;
	private ProgressDialog progressDialog;
	private Button btnUpload;
	private ImageView btnback;
	private String gid = "";// 成长记录id
	private GrowthImgGridView gridView;
	private List<PicModle> listBmp = new ArrayList<PicModle>();
	private List<String> newAdd = new ArrayList<String>();// 保存新增图片地址 编辑成長记录时使用
	private List<String> delID = new ArrayList<String>();// 保存新增刪除的图片记录id
															// 编辑成長记录时使用
	private MyAdapter adapter;
	private String type;// 标记是增加记录还是编辑记录
	private ArrayList<String> urlPath = new ArrayList<String>();// 存放图片的地址的本地图片路径
	private String editcontent;
	private List<String> imgID = new ArrayList<String>();
	private SelectPicPopwindow pop;
	private TextView titleTxt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_release_growth_main);
		addPic = (ImageView) findViewById(R.id.addPicture);
		addPic.setOnClickListener(this);
		time = (TextView) findViewById(R.id.time);
		location = (EditText) findViewById(R.id.location);
		content = (EditText) findViewById(R.id.content);
		time.setText(getDate());
		type = getIntent().getStringExtra("type");
		if (type.equals("edit")) {
			edit();
		}
		cid = getIntent().getStringExtra("cid");
		btnUpload = (Button) findViewById(R.id.btnUpload);
		btnUpload.setOnClickListener(this);
		btnback = (ImageView) findViewById(R.id.back);
		btnback.setOnClickListener(this);
		gridView = (GrowthImgGridView) findViewById(R.id.imgGridview);
		adapter = new MyAdapter();
		gridView.setAdapter(adapter);
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText("发布成长记录");
	}

	/**
	 * 编辑时初始化
	 */
	private void edit() {
		Bundle bundle = this.getIntent().getExtras();
		gid = getIntent().getStringExtra("gid");
		urlPath = bundle.getStringArrayList("path");
		imgID = bundle.getStringArrayList("imgID");
		editcontent = getIntent().getStringExtra("content");
		content.setText(editcontent);
		content.setSelection(editcontent.length());
		for (int i = 0; i < urlPath.size(); i++) {
			PicModle modle = new PicModle();
			modle.setPath(urlPath.get(i));
			modle.setFilePath(Utils.createFilePath(urlPath.get(i)));
			Bitmap bm = BitmapFactory.decodeFile(Utils.createFilePath(urlPath
					.get(i)));
			modle.setBmp(bm);
			modle.setImgID(imgID.get(i));
			listBmp.add(modle);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addPicture:
			pop = new SelectPicPopwindow(this, v);
			pop.show();
			break;
		case R.id.btn_cancel:
			break;
		case R.id.btnUpload:
			new UpDataTask().execute(content.getText().toString(), time
					.getText().toString(), location.getText().toString());
			break;
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}

	private String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		return df.format(new Date());
	}

	/**
	 * 上传数据到服务器
	 * 
	 */
	class UpDataTask extends AsyncTask<String, Integer, String> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		UpLoadGrowthPicTask picTask;

		@Override
		protected String doInBackground(String... params) {
			String rt = "";
			String time = String.valueOf(new Date().getTime());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cid", cid);
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("gid", gid);
			map.put("content", params[0]);
			map.put("location", params[2]);
			map.put("time", time.substring(0, time.length() - 3));
			map.put("token", SharedUtils.getString("token", ""));
			String result = HttpUrlHelper.postData(map, "/growth/igrowth");
			Logger.debug(this,
					"cid" + cid + "  uid:" + SharedUtils.getString("uid", "")
							+ "    gid:" + gid + " content:" + params[0]
							+ "  location:" + params[2] + "  time:" + params[1]);
			Logger.debug(this, "Growthresult:" + result);
			try {
				JSONObject jsonobject = new JSONObject(result);
				rt = jsonobject.getString("rt");
				if (rt.equals("1")) {
					gid = jsonobject.getString("gid");
				}
			} catch (JSONException e) {
				Logger.error(this, e);

				e.printStackTrace();
			}
			return rt;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("1")) {
				Logger.debug(this, "lsitBmap.size:" + listBmp.size());
				if (listBmp.size() == 0 && delID.size() == 0) {// 当没有图片要上传并且也没有要删除的图片则返回
					Utils.showToast("成长记录已发布!");
					progressDialog.dismiss();
					finish();
					return;
				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("cid", cid);
				map.put("uid", SharedUtils.getString("uid", ""));
				map.put("token", SharedUtils.getString("token", ""));
				map.put("gid", gid);
				if (type.equals("add")) {
					List<String> picPath = new ArrayList<String>();
					for (PicModle modle : listBmp) {
						picPath.add(modle.getPath());
					}
					picTask = new UpLoadGrowthPicTask(picPath, map);
				} else {
					picTask = new UpLoadGrowthPicTask(newAdd, map);
				}
				picTask.setGrowthCallBack(ReleaseGrowthActivity.this);
				picTask.execute();
			} else {
				Utils.showToast("发布失败!");
				progressDialog.dismiss();
				finish();

			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			progressDialog = new ProgressDialog(ReleaseGrowthActivity.this);
			progressDialog.show();
		}
	}

	/**
	 * 删除图片 编辑时可能使用
	 * 
	 */
	class DelTask extends AsyncTask<String, Integer, String> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		String rt = "1";
		JSONObject jsonobject;
		String errCode;

		@Override
		protected String doInBackground(String... params) {

			for (int i = 0; i < delID.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("cid", cid);
				map.put("uid", SharedUtils.getString("uid", ""));
				map.put("gid", gid);
				map.put("imgid", delID.get(i));
				map.put("token", SharedUtils.getString("token", ""));
				if (delID.get(i) == null) {
					continue;
				}
				Logger.debug(this, "cid:" + cid + " gid:" + gid + "  imgid:"
						+ delID.get(i));
				String result = HttpUrlHelper.postData(map,
						"/growth/iremoveImage");
				Logger.debug(this, "resultdel:" + result);
				try {
					jsonobject = new JSONObject(result);
					rt = jsonobject.getString("rt");
					if (!rt.equals("1")) {
						errCode = jsonobject.getString("err");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return rt;
		}

		@Override
		protected void onPostExecute(String result) {
			Logger.debug(this, "DelTask完成");
			if (result.equals("1")) {
				Utils.showToast("成长记录已发布!");
			} else {
				Utils.showToast(ErrorCodeUtil.convertToChines(errCode));
			}
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			finish();
		}

		@Override
		protected void onPreExecute() {

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bitmap bitmap;
		PicModle modle = new PicModle();

		if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD) {
			if (resultCode != RESULT_OK) {
				return;
			}
			if (data == null) {
				return;
			}
			SelectPicModle picmodle = BitmapUtils.getPickPic(this, data);
			if (type.equals("edit")) {
				newAdd.add(picmodle.getPicPath());
			}
			modle.setBmp(picmodle.getBmp());
			modle.setPath(picmodle.getPicPath());
		}
		// 拍摄图片
		else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
			if (resultCode != RESULT_OK) {
				return;
			}
			if (resultCode != RESULT_OK) {
				return;
			}
			String fileName = pop.getTakePhotoPath();
			bitmap = BitmapUtils.FitSizeImg(fileName);
			modle.setPath(fileName);
			modle.setBmp(bitmap);
		}
		listBmp.add(modle);
		adapter.notifyDataSetChanged();
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listBmp.size();
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
				convertView = LayoutInflater.from(ReleaseGrowthActivity.this)
						.inflate(R.layout.grow_img_gridview_item, null);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.del = (ImageView) convertView.findViewById(R.id.del);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.del.setVisibility(View.VISIBLE);
			holder.del.setOnClickListener(new BtnDelClick(position));
			int width = Utils.getSecreenWidth(ReleaseGrowthActivity.this) - 30;
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					width / 4, width / 4);
			holder.img.setLayoutParams(params);
			Bitmap bmp = listBmp.get(position).getBmp();
			if (bmp != null) {
				BitmapDrawable bd = new BitmapDrawable(bmp);
				holder.img.setBackgroundDrawable(bd);
			}
			return convertView;
		}
	}

	class BtnDelClick implements OnClickListener {
		int position;

		public BtnDelClick(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			if (type.equals("edit")) {
				delID.add(listBmp.get(position).getImgID());
			}
			for (int i = 0; i < newAdd.size(); i++) {// 删除新增图片
				if (listBmp.get(position).getPath().equals(newAdd.get(i))) {
					newAdd.remove(newAdd.get(i));
					break;
				}
			}
			listBmp.remove(position);
			adapter.notifyDataSetChanged();
		}
	}

	class PicModle {
		String path;
		Bitmap bmp;
		String filePath;// 修改时用
		String imgID;// 编辑删除时使用

		public String getImgID() {
			return imgID;
		}

		public void setImgID(String imgID) {
			this.imgID = imgID;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public Bitmap getBmp() {
			return bmp;
		}

		public void setBmp(Bitmap bmp) {
			this.bmp = bmp;
		}

	}

	class ViewHolder {
		ImageView img;
		ImageView del;
	}

	@Override
	public void upLoadFinish(boolean flag) {
		if (type.equals("add")) {
			if (flag) {
				Utils.showToast("成长记录已发布!");
			}
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			finish();
		} else {
			new DelTask().execute();
		}

	}
}
