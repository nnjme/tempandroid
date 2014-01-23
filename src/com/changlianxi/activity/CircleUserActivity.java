package com.changlianxi.activity;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.adapter.MyAdapter;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.CircleMemberList;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.MemberModle;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.CircleMemberListTask;
import com.changlianxi.task.GetCircleUserTask;
import com.changlianxi.task.GetCircleUserTask.GetCircleUserList;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.MyComparator;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.QuickAlphabeticBar;
import com.changlianxi.view.QuickAlphabeticBar.OnTouchingLetterChangedListener;
import com.changlianxi.view.QuickAlphabeticBar.touchUp;
import com.changlianxi.view.SearchEditText;
import com.umeng.analytics.MobclickAgent;

/**
 * 显示圈子成员列表的界面
 * 
 * @author teeker_bin
 * 
 */
public class CircleUserActivity extends BaseActivity implements
		OnTouchingLetterChangedListener, touchUp, OnItemClickListener,
		OnClickListener, PostCallBack, GetCircleUserList {
	private QuickAlphabeticBar indexBar;// 右侧字母拦
	private MyAdapter adapter;
	private ListView listView;
	private TextView selectedChar;// 显示选择字母
	//private List<MemberModle> listModles = new ArrayList<MemberModle>();// 存储成员列表
	private List<CircleMember> searchListModles = new ArrayList<CircleMember>();// 存储搜索列表
	private int position;// 当前字母子listview中所对应的位置
	private int cid;// 圈子ID
	private ImageView btadd;
	private ImageView btback;
	private TextView txtciecleName;
	private Dialog progressDialog;
	private boolean isnew;// 是否是新邀请的圈子
	private LinearLayout layInvitate;
	private Button btnAccecpt;// 接受邀请按钮
	private Button btnFefuse;// 拒绝邀请按钮
	private int status;// 0 拒绝 1 接受
	private SearchEditText editSearch;
	private String inviterID = "";
	private CircleMemberListTask task;
	private CircleMemberList circleMemberList;
//	private GetCircleUserTask task;

	private void filldata() {
		if(circleMemberList == null)
			circleMemberList = new CircleMemberList(cid);
		if (circleMemberList.getMembers().size() == 0) {
			progressDialog = DialogUtil.getWaitDialog(this, "请稍后");
			progressDialog.show();
		}
		task = new CircleMemberListTask();
		task.setTaskCallBack(new BaseAsyncTask.PostCallBack<RetError>() {

			@Override
			public void taskFinish(RetError result) {
				layInvitate.setVisibility(View.GONE);
				progressDialog.dismiss();
				adapter = new MyAdapter(CircleUserActivity.this, circleMemberList);
				listView.setAdapter(adapter);
			}
		});
		task.executeWithCheckNet(circleMemberList);
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		cid = Integer.parseInt(getIntent().getStringExtra("cirID"));
		isnew = getIntent().getBooleanExtra("is_New", false);
		inviterID = getIntent().getStringExtra("inviterID");
		initView();
		filldata();
	}

//	 private void getServerList() {
//	 if (Utils.isNetworkAvailable()) {
//	 if (listModles.size() == 0) {
//	 progressDialog = DialogUtil.getWaitDialog(this, "请稍后");
//	 progressDialog.show();
//	 }
//	 task = new GetCircleUserTask(cid);
//	 task.setTaskCallBack(this);
//	 task.execute();
//	 } else {
//	 Utils.showToast("请检查网络");
//	 }
//	 }

//	private void setMyAdapter() {
//		adapter = new MyAdapter(CircleUserActivity.this, listModles);
//		adapter = new MyAdapter(CircleUserActivity.this, circleMemberList);
//		listView.setAdapter(adapter);
//	}

	@Override
	protected void onRestart() {
		super.onRestart();
//		 getServerList();
		filldata();
	}

	@Override
	protected void onResume() {
		super.onResume();
		CircleActivity
				.setInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		MobclickAgent.onPageStart(getClass().getName() + "");
		// MobclickAgent.onResume(this);
	}

	/**
	 * 数据统计
	 */

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getName() + "");
		// MobclickAgent.onPause(this);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {

		btnAccecpt = (Button) findViewById(R.id.btnAccetpt);
		btnFefuse = (Button) findViewById(R.id.btnrefuse);
		btnAccecpt.setOnClickListener(this);
		btnFefuse.setOnClickListener(this);
		layInvitate = (LinearLayout) findViewById(R.id.layInvitate);
		if (isnew) {
			TextView inviteName = (TextView) findViewById(R.id.inviterName);
			String iName = DBUtils.getUserNameByUid(inviterID);
			inviteName.setText(iName + "邀请你加入圈子");
			layInvitate.setVisibility(View.VISIBLE);
		}
		btback = (ImageView) findViewById(R.id.back);
		btadd = (ImageView) findViewById(R.id.btadd);
		btadd.setOnClickListener(this);
		btback.setOnClickListener(this);
		txtciecleName = (TextView) findViewById(R.id.circleName);
		String name = getIntent().getStringExtra("cirName");
		txtciecleName.setText(name);
		listView = (ListView) findViewById(R.id.cy_list);
		listView.setCacheColorHint(0);
		listView.setOnItemClickListener(this);
//		MyComparator compartor = new MyComparator();
//		Collections.sort(listModles, compartor);
		View view = LayoutInflater.from(this).inflate(R.layout.header, null);
		editSearch = (SearchEditText) view.findViewById(R.id.search);
		editSearch.addTextChangedListener(new EditWather());
		listView.addHeaderView(view);
		indexBar = (QuickAlphabeticBar) findViewById(R.id.indexBar);
		indexBar.setOnTouchingLetterChangedListener(this);
		indexBar.getBackground().setAlpha(0);
		indexBar.setOnTouchUp(this);
		selectedChar = (TextView) findViewById(R.id.selected_tv);
		selectedChar.setVisibility(View.INVISIBLE);

	}

	class EditWather implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {
			String key = s.toString().toLowerCase();
			if (key.length() == 0) {
				Utils.hideSoftInput(CircleUserActivity.this);
				adapter.setData(circleMemberList.getMembers());
				indexBar.setVisibility(View.VISIBLE);
				editSearch.setCompoundDrawables(null, null, null, null);
				return;
			}
			Drawable del = getResources().getDrawable(R.drawable.del);
			del.setBounds(0, 0, del.getMinimumWidth(), del.getMinimumHeight());
			editSearch.setCompoundDrawables(null, null, del, null);
			indexBar.setVisibility(View.GONE);
			searchListModles.clear();
			for (int i = 0; i < circleMemberList.getMembers().size(); i++) {
				String name = circleMemberList.getMembers().get(i).getName();
				String pinyin = circleMemberList.getMembers().get(i).getSortkey().toLowerCase();
				String pinyinFir = circleMemberList.getMembers().get(i).getPinyinFir();
				String mobileNum = circleMemberList.getMembers().get(i).getCellphone();
				if (name.contains(key) || pinyin.contains(key)
						|| pinyinFir.contains(key) || mobileNum.contains(key)) {
					CircleMember modle = circleMemberList.getMembers().get(i);
					searchListModles.add(modle);

				}
			}
			adapter.setData(searchListModles);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

	}

	/**
	 * 设置listview的当前选中值
	 * 
	 * @param s
	 * @return
	 */
	public int findIndexer(String s) {
		int position = 0;
		for (int i = 0; i < circleMemberList.getMembers().size(); i++) {
			String sortkey = circleMemberList.getMembers().get(i).getSortkey();
			if (sortkey.startsWith(s)) {
				position = i;
				break;
			}
		}
		return position;
	}

	@Override
	public void onTouchingLetterChanged(String s) {
		indexBar.getBackground().setAlpha(200);
		selectedChar.setText(s);
		selectedChar.setVisibility(View.VISIBLE);
		position = (findIndexer(s)) + 2;
		listView.setSelection(position);
	}

	@Override
	public void onTouchUp() {
		indexBar.getBackground().setAlpha(0);
		selectedChar.setVisibility(View.GONE);
		listView.setSelection(position);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int position = arg2 - 2;
		int pid = 0;
		int uid = 0;
		String iconImg = "";
		String username = "";
		if (searchListModles.size() > 0) {
			uid = searchListModles.get(position).getUid();
			iconImg = searchListModles.get(position).getAvatar();
			username = searchListModles.get(position).getName();
			pid = searchListModles.get(position).getPid();

		} else {
			uid = circleMemberList.getMembers().get(position).getUid();
			iconImg = circleMemberList.getMembers().get(position).getAvatar();
			username = circleMemberList.getMembers().get(position).getName();
			pid = circleMemberList.getMembers().get(position).getPid();
		}
		//TODO 跳转到圈子成员详情页面
		Intent it = new Intent();
		it.setClass(this, UserInfoActivity.class);
		it.putExtra("cid", cid);
		it.putExtra("pid", pid);
		it.putExtra("uid", uid);
		it.putExtra("username", username);
		it.putExtra("iconImg", iconImg);
		startActivity(it);
		this.getParent().overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			getParent().overridePendingTransition(R.anim.right_in,
					R.anim.right_out);
		}
		return super.onKeyDown(keyCode, event);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			this.getParent().overridePendingTransition(R.anim.right_in,
					R.anim.right_out);
			break;
		case R.id.btadd:
			Intent it = new Intent();
			it.setClass(this, AddCircleMemberActivity.class);
			it.putExtra("cid", cid);
			it.putExtra("type", "add");// 添加成员
			it.putExtra("cirName", txtciecleName.getText().toString());
			startActivity(it);
			this.getParent().overridePendingTransition(R.anim.in_from_right,
					R.anim.out_to_left);

			break;
		case R.id.btnAccetpt:
			acceptOrRefuse("/circles/iacceptInvitation");
			status = 1;
			break;
		case R.id.btnrefuse:
			acceptOrRefuse("/circles/irefuseInvitation");
			status = 0;
			break;

		default:
			break;
		}
	}

	/**
	 * 同意或者拒绝邀请加入圈子
	 * 
	 * @param url
	 */
	private void acceptOrRefuse(String url) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("cid", cid);
		PostAsyncTask task = new PostAsyncTask(this, map, url);
		task.setTaskCallBack(this);
		task.execute();
		progressDialog = DialogUtil.getWaitDialog(this, "请稍后");
		progressDialog.show();
	}

	@Override
	protected void onDestroy() {
		if (task != null && task.getStatus() == Status.RUNNING) {
			task.cancel(true); // 如果Task还在运行，则先取消它
		}
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		super.onDestroy();
	}

//	@Override
//	public void taskFinish(String result) {
//		layInvitate.setVisibility(View.GONE);
//		progressDialog.dismiss();
//		try {
//			JSONObject json = new JSONObject(result);
//			Intent intent = new Intent();
//			int rt = json.getInt("rt");
//			if (rt != 1) {
//				String errorCode = json.getString("err");
//				String err = ErrorCodeUtil.convertToChines(errorCode);
//				Utils.showToast(err);
//				return;
//			}
//			if (status == 1) {
//				// Home.acceptOrRefuseInvite(cid, false);
//				intent.setAction(Constants.ACCEPT_OR_REFUSE_INVITE);
//				intent.putExtra("cid", cid);
//				intent.putExtra("flag", false);
//				sendBroad(intent);
//				getServerList();// 更新列表
//			} else {
//				// Home.exitCircle(cid);
//				intent.setAction(Constants.EXIT_CIRCLE);
//				intent.putExtra("cid", cid);
//				sendBroad(intent);
//				finish();
//				this.getParent().overridePendingTransition(R.anim.right_in,
//						R.anim.right_out);
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * 发送广播
	 * 
	 * @param intent
	 */
	private void sendBroad(Intent intent) {
		BroadCast.sendBroadCast(this, intent);

	}

	@Override
	public void getCircleUserList(List<MemberModle> listModle) {
//		if (progressDialog != null) {
//			progressDialog.dismiss();
//		}
//		if (listModle.size() == 0) {
//			return;
//		}
//		circleMemberList.getMembers().clear();
//		listModles = listModle;
//		adapter.setData(listModles);
	}

	@Override
	public void taskFinish(String result) {
		
	}

}