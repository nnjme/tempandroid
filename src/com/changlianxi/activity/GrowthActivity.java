package com.changlianxi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.adapter.GrowthAdapter;
import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.GrowthImgModle;
import com.changlianxi.modle.GrowthModle;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.view.MyListView;
import com.changlianxi.view.MyListView.OnRefreshListener;

/**
 * �ɳ���¼��ʾ����
 * 
 * @author teeker_bin
 * 
 */
public class GrowthActivity extends Activity implements OnClickListener {
	private String cid = "";
	private List<GrowthModle> listData = new ArrayList<GrowthModle>();
	private MyListView listview;
	private ProgressDialog progressDialog;
	private GrowthAdapter adapter;
	private ImageView btnRelease;// �����ɳ���ť
	private String circleName;
	private TextView txtCirName;
	private ImageView btback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_czjl);
		progressDialog = new ProgressDialog(this);
		cid = getIntent().getStringExtra("cirID");
		circleName = getIntent().getStringExtra("cirName");
		listview = (MyListView) findViewById(R.id.listview);
		adapter = new GrowthAdapter(this, listData);
		listview.setAdapter(adapter);
		listview.setCacheColorHint(0);
		listview.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				listview.onRefreshComplete();
			}
		});
		btnRelease = (ImageView) findViewById(R.id.btnRelease);
		btnRelease.setOnClickListener(this);
		txtCirName = (TextView) findViewById(R.id.circleName);
		txtCirName.setText(circleName);
		btback = (ImageView) findViewById(R.id.back);
		btback.setOnClickListener(this);
	}

	/**
	 * �������¼ ���¼�������
	 */
	@Override
	protected void onStart() {
		listData.clear();
		new GetDataTask().execute();
		super.onStart();
	}

	/**
	 * �Ŵӷ�������ȡ����
	 * 
	 */
	class GetDataTask extends AsyncTask<String, Integer, String> {
		// �ɱ䳤�������������AsyncTask.exucute()��Ӧ
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cid", cid);
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("token", SharedUtils.getString("token", ""));
			map.put("timestamp", 0);
			String result = HttpUrlHelper.postData(map, "/growth/ilist");
			Logger.debug(this, "result:" + result);
			try {
				JSONObject jsonobject = new JSONObject(result);
				String cid = jsonobject.getString("cid");
				String num = jsonobject.getString("num");
				JSONArray jsonarray = jsonobject.getJSONArray("growths");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject object = (JSONObject) jsonarray.opt(i);
					GrowthModle modle = new GrowthModle();
					String id = object.getString("id");
					String uid = object.getString("uid");
					String content = object.getString("content");
					String location = object.getString("location");
					String happen = object.getString("happen");
					int praise = object.getInt("praise");
					int comment = object.getInt("comment");
					String publish = object.getString("publish");
					String isPraise = object.getString("mypraise");
					Logger.debug(this, "content:" + content);
					JSONArray imgrray = object.getJSONArray("images");
					List<GrowthImgModle> imgModle = new ArrayList<GrowthImgModle>();
					for (int j = 0; j < imgrray.length(); j++) {
						GrowthImgModle im = new GrowthImgModle();
						JSONObject imgObj = (JSONObject) imgrray.opt(j);
						String imgId = imgObj.getString("imgid");
						String img = imgObj.getString("img");
						im.setId(imgId);
						im.setImg(img);
						im.setSamllImg(StringUtils.JoinString(img, "_100x100"));
						imgModle.add(im);
					}
					MemberInfoModle md = DBUtils.selectNameAndImgByID("circle"
							+ cid, uid);
					if (md != null) {
						modle.setName(md.getName());
						modle.setPersonImg(md.getAvator());
					}
					if (isPraise.equals("1")) {
						modle.setIspraise(true);
					} else {
						modle.setIspraise(false);
					}
					modle.setImgModle(imgModle);
					modle.setCid(cid);
					modle.setNum(num);
					modle.setId(id);
					modle.setUid(uid);
					modle.setContent(content);
					modle.setLocation(location);
					modle.setHappen(happen);
					modle.setPraise(praise);
					modle.setComment(comment);
					modle.setPublish(publish);
					listData.add(modle);
				}
			} catch (JSONException e) {
				Logger.error(this, e);

				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			adapter.notifyDataSetChanged();

		}

		@Override
		protected void onPreExecute() {
			// ����������������������ʾһ���Ի�������򵥴���
			progressDialog.show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRelease:
			Intent it = new Intent();
			it.setClass(this, ReleaseGrowthActivity.class);
			it.putExtra("cid", cid);
			it.putExtra("type", "add");
			startActivity(it);
			break;
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}

}
