package com.changlianxi.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.activity.AddCircleMemberActivity;
import com.changlianxi.activity.CircleActivity;
import com.changlianxi.activity.R;
import com.changlianxi.activity.UserInfoActivity;
import com.changlianxi.adapter.CircleAdapter;
import com.changlianxi.adapter.CircleSearchAdapter;
import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.CircleModle;
import com.changlianxi.modle.MemberModle;
import com.changlianxi.task.GetCircleListTask;
import com.changlianxi.task.GetCircleListTask.GetCircleList;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.Utils;
import com.changlianxi.view.BounceScrollView.OnRefreshComplete;
import com.changlianxi.view.FlipperLayout.OnOpenListener;

/**
 * 圈子首页界面的view
 * 
 * @author teeker_bin
 * 
 */
public class Home implements OnClickListener, OnRefreshComplete,
		OnItemClickListener {
	private Context mcontext;
	private View mHome;
	private LinearLayout mMenu;
	private OnOpenListener mOnOpenListener;
	private static List<CircleModle> listModle = new ArrayList<CircleModle>();
	private GrowthImgGridView gView;
	private static CircleAdapter adapter;
	private BounceScrollView scrollView;
	private EditText search;
	private static Dialog progressDialog;
	public static ImageView imgPromte;
	private ListView searchListView;
	private List<MemberModle> searchListModle = new ArrayList<MemberModle>();// 搜索时使用
	private CircleSearchAdapter searchAdapter;

	public Home(Context context) {
		mcontext = context;
		mHome = LayoutInflater.from(context).inflate(R.layout.home, null);
		findViewById();
		setListener();
		listModle = DBUtils.getCircleList();
		showAdapter();
	}

	private void showAdapter() {
		searchAdapter = new CircleSearchAdapter(mcontext, searchListModle);
		searchListView.setAdapter(searchAdapter);
		listModle.add(newCircle());
		adapter = new CircleAdapter(mcontext, listModle);
		gView.setAdapter(adapter);
		if (Utils.isNetworkAvailable()) {
			getServerCircleLists();
			progressDialog = DialogUtil.getWaitDialog(mcontext, "请稍后");
			if (listModle.size() > 1) {
				return;
			}
			progressDialog.show();
		} else {
			Utils.showToast("请检查网络");
		}
	}

	public static void getServerCircleLists() {
		GetCircleListTask task = new GetCircleListTask();
		task.setTaskCallBack(new GetCircleList() {

			@Override
			public void getCircleList(List<CircleModle> modles) {
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
				if (modles.size() == 0) {
					return;
				}
				modles.add(newCircle());
				listModle.clear();
				listModle.addAll(modles);
				adapter.setData(listModle);
			}
		});
		task.execute();
	}

	/**
	 * 更新圈子列表
	 * 
	 * @param modle
	 */
	public static void refreshCircleList(CircleModle modle) {
		getServerCircleLists();
	}

	/**
	 * 修改圈子状态 接受还是拒绝
	 * 
	 * @param cirID
	 * @param flag
	 */
	public static void acceptOrRefuseInvite(String cirID, boolean flag) {
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("isNew", String.valueOf(flag));
		DBUtils.editCircleInfo(values, cirID);
		int position = findCirPosition(cirID);
		listModle.get(position).setNew(false);
		adapter.setData(listModle);

	}

	/***
	 * 退出圈子
	 * 
	 * @param cirID
	 */
	public static void exitCircle(String cirID) {
		int position = findCirPosition(cirID);
		listModle.remove(position);
		adapter.setData(listModle);
		DBUtils.delCircle(cirID);
	}

	/**
	 * 查找圈子索引值
	 * 
	 * @param cirID
	 * @return
	 */
	private static int findCirPosition(String cirID) {
		for (int i = 0; i < listModle.size(); i++) {
			if (cirID.equals(listModle.get(i).getCirID())) {
				return i;

			}
		}
		return -1;
	}

	/**
	 * 初始化控件
	 */
	private void findViewById() {
		mMenu = (LinearLayout) mHome.findViewById(R.id.home_menu);
		gView = (GrowthImgGridView) mHome.findViewById(R.id.gridView1);
		gView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		scrollView = (BounceScrollView) mHome
				.findViewById(R.id.bounceScrollView);
		search = (EditText) mHome.findViewById(R.id.search);
		search.addTextChangedListener(new EditWather());
		imgPromte = (ImageView) mHome.findViewById(R.id.imgNews);
		searchListView = (ListView) mHome.findViewById(R.id.searchListView);
	}

	private void setListener() {
		scrollView.setOnRefreshComplete(this);
		searchListView.setOnItemClickListener(this);
		mMenu.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mOnOpenListener != null) {
					mOnOpenListener.open();
				}
			}
		});
		gView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				if (position == listModle.size() - 1) {
					Intent intent = new Intent();
					intent.setClass(mcontext, AddCircleMemberActivity.class);
					intent.putExtra("type", "create");
					mcontext.startActivity(intent);
					Utils.leftOutRightIn(mcontext);
					return;
				}
				TextView txt = (TextView) v.findViewById(R.id.circleName);
				String name = txt.getText().toString();
				Intent it = new Intent();
				it.setClass(mcontext, CircleActivity.class);
				it.putExtra("name", name);
				it.putExtra("type", "home");// 从圈子列表界面跳转
				it.putExtra("is_New", listModle.get(position).isNew());
				it.putExtra("inviterID", listModle.get(position).getInviterID());
				it.putExtra("cirID", listModle.get(position).getCirID());
				mcontext.startActivity(it);
				Utils.leftOutRightIn(mcontext);

			}
		});

		gView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return MotionEvent.ACTION_MOVE == event.getAction() ? true
						: false;
			}
		});
	}

	class EditWather implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {
			String key = s.toString().toLowerCase();
			searchListModle.clear();
			if (key.length() == 0) {
				Utils.hideSoftInput(mcontext);
				gView.setVisibility(View.VISIBLE);
				searchListView.setVisibility(View.GONE);
				search.setCompoundDrawables(null, null, null, null);
				return;
			}
			Drawable del = mcontext.getResources().getDrawable(R.drawable.del);
			del.setBounds(0, 0, del.getMinimumWidth(), del.getMinimumHeight());
			search.setCompoundDrawables(null, null, del, null);
			searchListModle = DBUtils.fuzzyQuery(key);
			gView.setVisibility(View.GONE);
			searchListView.setVisibility(View.VISIBLE);
			searchAdapter.setData(searchListModle);
			Utils.setListViewHeightBasedOnChildren(searchListView);

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

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		mOnOpenListener = onOpenListener;
	}

	public View getView() {
		return mHome;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.createCircle:
			break;
		default:
			break;
		}

	}

	@Override
	public void onComplete() {
		search.setVisibility(View.VISIBLE);
	}

	/**
	 * 新建圈子
	 * 
	 * @return
	 */
	private static CircleModle newCircle() {
		CircleModle modle = new CircleModle();
		modle.setCirIcon("addroot");
		modle.setNew(false);
		modle.setCirName("新建圈子");
		return modle;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent it = new Intent();
		it.setClass(mcontext, UserInfoActivity.class);
		it.putExtra("cid", searchListModle.get(arg2).getCid());
		it.putExtra("pid", searchListModle.get(arg2).getId());
		it.putExtra("uid", searchListModle.get(arg2).getUid());
		it.putExtra("username", searchListModle.get(arg2).getName());
		it.putExtra("userlistname", "circle"
				+ searchListModle.get(arg2).getCid() + "userlist");
		it.putExtra("iconImg", searchListModle.get(arg2).getImg());
		mcontext.startActivity(it);
		Utils.leftOutRightIn(mcontext);

	}

}