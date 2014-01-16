package com.changlianxi.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.inteface.UpLoadPic;
import com.changlianxi.modle.SelectPicModle;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.task.UpLoadGrowthPicTask;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.GrowthImgGridView;
import com.changlianxi.view.RoundAngleImageView;
import com.umeng.analytics.MobclickAgent;
import com.changlianxi.R;

public class ReleaseGrowthActivity extends BaseActivity implements
		OnClickListener, UpLoadPic, OnItemClickListener {
	private EditText time;
	private EditText location;// 地点输入框
	private EditText content;// 内容输入框
	private String cid;
	private Dialog progressDialog;
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
		setContentView(R.layout.activity_release_growth_main);
		time = (EditText) findViewById(R.id.time);
		location = (EditText) findViewById(R.id.location);
		content = (EditText) findViewById(R.id.content);
		time.setText(DateUtils.getCurrDateStr("yyyy-MM-dd HH:mm"));
		type = getIntent().getStringExtra("type");
		listBmp.add(null);
		if (type.equals("edit")) {
			edit();
		}
		cid = getIntent().getStringExtra("cid");
		btnUpload = (Button) findViewById(R.id.btnUpload);
		btnUpload.setOnClickListener(this);
		btnback = (ImageView) findViewById(R.id.back);
		btnback.setOnClickListener(this);
		gridView = (GrowthImgGridView) findViewById(R.id.imgGridview);
		gridView.setOnItemClickListener(this);
		adapter = new MyAdapter();
		gridView.setAdapter(adapter);
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText("发布成长记录");
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));

	}
	/**设置页面统计
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
			listBmp.add(listBmp.size() - 1, modle);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnUpload:
			if (content.getText().toString().replace(" ", "").length() == 0) {
				Utils.showToast("请输入内容");
				break;
			}
			new UpDataTask().execute(content.getText().toString(), time
					.getText().toString(), location.getText().toString());
			break;
		case R.id.back:
			Intent intent = new Intent();
			intent.putExtra("flag", false);
			setResult(2, intent);
			finish();
			Utils.rightOut(this);

			break;
		default:
			break;
		}
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
				if (listBmp.size() == 0 && delID.size() == 0) {// 当没有图片要上传并且也没有要删除的图片则返回
					Utils.showToast("成长记录已发布!");
					progressDialog.dismiss();
					exitSuccess();
					return;
				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("cid", cid);
				map.put("uid", SharedUtils.getString("uid", ""));
				map.put("token", SharedUtils.getString("token", ""));
				map.put("gid", gid);
				if (type.equals("add")) {
					if (listBmp.size() == 1) {
						Utils.showToast("成长记录已发布!");
						if (progressDialog != null
								&& progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
						setResult(2);
						exitSuccess();
						return;
					}
					List<String> picPath = new ArrayList<String>();
					for (int i = 0; i < listBmp.size() - 1; i++) {
						PicModle modle = listBmp.get(i);
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
				Utils.rightOut(ReleaseGrowthActivity.this);

			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			progressDialog = DialogUtil.getWaitDialog(
					ReleaseGrowthActivity.this, "请稍后");
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
				String result = HttpUrlHelper.postData(map,
						"/growth/iremoveImage");
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
			if (result.equals("1")) {
				Utils.showToast("成长记录已发布!");
			} else {
				Utils.showToast(ErrorCodeUtil.convertToChines(errCode));
			}
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			exitSuccess();
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
			String fileName = picmodle.getPicPath();
			int degree = BitmapUtils.readPictureDegree(fileName);
			bitmap = BitmapUtils.FitSizeImg(fileName);
			Bitmap degreeBitmap = BitmapUtils.rotaingImageView(degree, bitmap);
			modle.setBmp(degreeBitmap);
			modle.setPath(fileName);
		}
		// 拍摄图片
		else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
			if (resultCode != RESULT_OK) {
				return;
			}
			String fileName = pop.getTakePhotoPath();
			int degree = BitmapUtils.readPictureDegree(fileName);
			bitmap = BitmapUtils.FitSizeImg(fileName);
			Bitmap degreeBitmap = BitmapUtils.rotaingImageView(degree, bitmap);
			modle.setPath(fileName);
			modle.setBmp(degreeBitmap);
		}
		listBmp.add(listBmp.size() - 1, modle);
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
						.inflate(R.layout.growth_publich_grid_item, null);
				holder.img = (RoundAngleImageView) convertView
						.findViewById(R.id.img);
				holder.del = (ImageView) convertView.findViewById(R.id.del);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (position == listBmp.size() - 1) {
				holder.del.setVisibility(View.GONE);
				holder.img.setImageResource(R.drawable.add_pic);
			} else {
				Bitmap bmp = listBmp.get(position).getBmp();
				holder.img.setImageBitmap(bmp);
				holder.del.setVisibility(View.VISIBLE);
				// holder.del.setOnClickListener(new BtnDelClick(position));
			}
			return convertView;
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
		RoundAngleImageView img;
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
			exitSuccess();
		} else {
			new DelTask().execute();
		}

	}

	private void exitSuccess() {
		// Intent intent = new Intent();
		// intent.putExtra("flag", true);
		// setResult(2, intent);
		finish();
		Utils.rightOut(this);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg2 == listBmp.size() - 1) {
			Utils.hideSoftInput(this);
			pop = new SelectPicPopwindow(this, arg1);
			pop.show();
			return;
		}
		if (type.equals("edit")) {
			delID.add(listBmp.get(arg2).getImgID());
		}
		for (int i = 0; i < newAdd.size(); i++) {// 删除新增图片
			if (listBmp.get(arg2).getPath().equals(newAdd.get(i))) {
				newAdd.remove(newAdd.get(i));
				break;
			}
		}
		listBmp.remove(arg2);
		adapter.notifyDataSetChanged();
	}
}
