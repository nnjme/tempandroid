package com.changlianxi.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.activity.AddCircleMemberActivity;
import com.changlianxi.activity.CircleActivity;
import com.changlianxi.activity.UserInfoActivity;
import com.changlianxi.adapter.CircleAdapter;
import com.changlianxi.adapter.CircleSearchAdapter;
import com.changlianxi.db.DBUtils;
import com.changlianxi.modle.CircleModle;
import com.changlianxi.modle.MemberModle;
import com.changlianxi.task.GetCieclesNotifyTask;
import com.changlianxi.task.GetCieclesNotifyTask.GetCirclesNotify;
import com.changlianxi.task.GetCircleListTask;
import com.changlianxi.task.GetCircleListTask.GetCircleList;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.PushMessageReceiver;
import com.changlianxi.util.PushMessageReceiver.MessagePrompt;
import com.changlianxi.util.ResolutionPushJson;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.BounceScrollView;
import com.changlianxi.view.BounceScrollView.OnRefreshComplete;
import com.changlianxi.view.GrowthImgGridView;

@SuppressLint("NewApi")
public class HomeFragMent extends Fragment implements OnClickListener,
		OnRefreshComplete, OnItemClickListener, MessagePrompt {
	private Context mcontext;
	private LinearLayout mMenu;
	private List<CircleModle> listModle = new ArrayList<CircleModle>();
	private List<CircleModle> listPrompt = new ArrayList<CircleModle>();// 圈子提醒数
	private GrowthImgGridView gView;
	private CircleAdapter adapter;
	private BounceScrollView scrollView;
	private EditText search;
	private Dialog progressDialog;
	private ImageView imgPromte;
	private ListView searchListView;
	private List<MemberModle> searchListModle = new ArrayList<MemberModle>();// 搜索时使用
	private CircleSearchAdapter searchAdapter;
	private GetCieclesNotifyTask notifyTask;
	private GetCircleListTask task;
	private boolean prompt;
	private View viewHome;

	public HomeFragMent(Context context) {
		this.mcontext = context;
		PushMessageReceiver.setMessagePrompt(this);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		listModle = DBUtils.getCircleList();
		listPrompt = DBUtils.getCirclePtompt();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewHome = inflater.inflate(R.layout.home, null);
		return viewHome;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		findViewById();
		setListener();
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

	private void getServerCircleLists() {
		task = new GetCircleListTask();
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
				String cids = "";
				for (int i = 0; i < modles.size() - 1; i++) {
					cids += modles.get(i).getCirID() + ",";
				}
				notifyTask = new GetCieclesNotifyTask(SharedUtils.getString(
						"exitTime",
						DateUtils.phpTime(System.currentTimeMillis())), cids
						.substring(0, cids.length() - 1));
				notifyTask.setTaskCallBack(new GetCirclesNotify() {
					@Override
					public void getCirclesNotify(String result) {
						if (result == null) {
							return;
						}
						getPromptCount(result);
					}
				});
				notifyTask.execute();
			}
		});
		task.execute();
	}

	private void getPromptCount(String result) {
		try {
			JSONArray array = new JSONArray(result);
			for (int i = 0; i < array.length(); i++) {
				if (listModle.get(i).isNew()) {
					continue;
				}
				String str = array.getString(i);
				getCount(i, str);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		adapter.setData(listModle);
		listPrompt = null;

	}

	/**
	 * 推送消息提示
	 */
	public void pushPormpt(String cid, int count, String type) {
		for (int i = 0; i < listModle.size() - 1; i++) {
			if (cid.equals(listModle.get(i).getCirID())) {
				listModle.get(i).setPromptCount(
						listModle.get(i).getPromptCount() + count);
				if (type.equals(ResolutionPushJson.COMMENT_TYPE)) {
					listModle.get(i).setNewCommentCount(
							listModle.get(i).getNewCommentCount() + count);
				} else if (type.equals(ResolutionPushJson.NEW_TYPE)) {
					listModle.get(i).setNewDynamicCount(
							listModle.get(i).getNewDynamicCount() + count);
				} else if (type.equals(ResolutionPushJson.GROWTH_TYPE)) {
					listModle.get(i).setNewGrowthCount(
							listModle.get(i).getNewGrowthCount() + count);
				} else if (type.equals(ResolutionPushJson.CHAT_TYPE)) {
					listModle.get(i).setNewChatCount(
							listModle.get(i).getNewChatCount() + count);
				}
				break;
			}
		}
		adapter.setData(listModle);
	}

	/**
	 * 更新圈子名称
	 * 
	 * @param cid
	 * @param cirName
	 */
	public void upDateCirName(String cid, String cirName) {
		for (int i = 0; i < listModle.size(); i++) {
			if (cid.equals(listModle.get(i).getCirID())) {
				listModle.get(i).setCirName(cirName);
				adapter.setData(listModle);
				break;
			}
		}

	}

	public void cancleTask() {
		if (notifyTask != null && notifyTask.getStatus() == Status.RUNNING) {
			notifyTask.cancel(true); // 如果Task还在运行，则先取消它
		}
		if (task != null && task.getStatus() == Status.RUNNING) {
			task.cancel(true); // 如果Task还在运行，则先取消它
		}
	}

	/**
	 * 获取各个界面的提示数量
	 * 
	 * @param position
	 * @param str
	 * @return
	 */
	private void getCount(int position, String str) {

		String countArray[] = str.split(",");
		int count = 0;
		int newMemberCount = 0;// 新成员数
		int newGrowthCount = 0;// 新成长数、
		int newChatCount = 0;// 新聊天数、
		int newDynamicCount = 0;// 新动态数、
		int newCommentCount = 0;// 新评论数。
		newMemberCount = Integer.valueOf(countArray[0]);
		newGrowthCount = Integer.valueOf(countArray[1]);
		newChatCount = Integer.valueOf(countArray[2]);
		newDynamicCount = Integer.valueOf(countArray[3]);
		newCommentCount = Integer.valueOf(countArray[4]);
		count = newMemberCount + newGrowthCount + newChatCount
				+ newDynamicCount + newCommentCount;
		listModle.get(position).setPromptCount(count);
		listModle.get(position).setNewChatCount(newChatCount);
		listModle.get(position).setNewCommentCount(newCommentCount);
		listModle.get(position).setNewDynamicCount(newDynamicCount);
		listModle.get(position).setNewGrowthCount(newGrowthCount);
		listModle.get(position).setNewMemberCount(newMemberCount);
		getLoaclPrompt(listModle.get(position).getCirID(), position);

	}

	private void getLoaclPrompt(String cid, int position) {
		if (listPrompt == null) {
			return;
		}
		for (int i = 0; i < listPrompt.size(); i++) {
			if (cid.equals(listPrompt.get(i).getCirID())) {
				listModle.get(position).setNewChatCount(
						listModle.get(position).getNewChatCount()
								+ listPrompt.get(i).getNewChatCount());
				listModle.get(position).setNewCommentCount(
						listModle.get(position).getNewCommentCount()
								+ listPrompt.get(i).getNewCommentCount());
				listModle.get(position).setNewDynamicCount(
						listModle.get(position).getNewDynamicCount()
								+ listPrompt.get(i).getNewDynamicCount());
				listModle.get(position).setNewGrowthCount(
						listModle.get(position).getNewGrowthCount()
								+ listPrompt.get(i).getNewGrowthCount());
				listModle.get(position).setNewMemberCount(
						listModle.get(position).getNewMemberCount()
								+ listPrompt.get(i).getNewMemberCount());
				listModle.get(position).setPromptCount(
						listModle.get(position).getPromptCount()
								+ listPrompt.get(i).getPromptCount());

				continue;
			}
		}

	}

	/**
	 * 减少提示数量
	 * 
	 * @param cid
	 * @param count
	 * @param position
	 *            0新成员数1 新成长数2新聊天数3新动态树4 新评论数
	 */
	public void remorePromptCount(String cid, int count, int position) {
		for (int i = 0; i < listModle.size() - 1; i++) {
			if (cid.equals(listModle.get(i).getCirID())) {
				switch (position) {
				case 0:
					listModle.get(i).setNewMemberCount(0);
					break;
				case 1:
					listModle.get(i).setNewGrowthCount(0);
					break;
				case 2:
					listModle.get(i).setNewChatCount(0);
					break;
				case 3:
					listModle.get(i).setNewDynamicCount(0);
					break;
				case 4:
					listModle.get(i).setNewCommentCount(0);
					break;
				default:
					break;
				}
				int countPrompet = listModle.get(i).getPromptCount();
				listModle.get(i).setPromptCount(countPrompet - count);
				break;
			}
		}
		adapter.setData(listModle);
	}

	/**
	 * 保存提示数量
	 */
	public void savePromptCount() {

		int newMemberCount = 0;// 新成员数
		int newGrowthCount = 0;// 新成长数、
		int newChatCount = 0;// 新聊天数、
		int newDynamicCount = 0;// 新动态数、
		int newCommentCount = 0;// 新评论数。
		int promptCount = 0;
		for (int i = 0; i < listModle.size() - 1; i++) {
			String cid = listModle.get(i).getCirID();
			promptCount = listModle.get(i).getPromptCount();
			if (promptCount <= 0) {
				continue;
			}
			newChatCount = listModle.get(i).getNewChatCount();
			newCommentCount = listModle.get(i).getNewCommentCount();
			newDynamicCount = listModle.get(i).getNewDynamicCount();
			newGrowthCount = listModle.get(i).getNewGrowthCount();
			newMemberCount = listModle.get(i).getNewMemberCount();
			updateDB(cid, newMemberCount, newGrowthCount, newChatCount,
					newDynamicCount, newCommentCount, promptCount);
		}
	}

	private void updateDB(String cid, int newMemberCount, int newGrowthCount,
			int newChatCount, int newDynamicCount, int newCommentCount,
			int promptCount) {
		ContentValues cv = new ContentValues();
		cv.put("newMemberCount", newMemberCount);
		cv.put("newGrowthCount", newGrowthCount);
		cv.put("newChatCount", newChatCount);
		cv.put("newDynamicCount", newDynamicCount);
		cv.put("newCommentCount", newCommentCount);
		cv.put("promptCount", promptCount);
		DBUtils.updateInfo("circlelist", cv, "cirID=?", new String[] { cid });
	}

	/**
	 * 更新圈子列表
	 * 
	 * @param modle
	 */
	public void refreshCircleList() {
		getServerCircleLists();
	}

	/**
	 * 修改圈子状态 接受还是拒绝
	 * 
	 * @param cirID
	 * @param flag
	 */
	public void acceptOrRefuseInvite(String cirID, boolean flag) {
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
	public void exitCircle(String cirID) {
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
	private int findCirPosition(String cirID) {
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
		mMenu = (LinearLayout) viewHome.findViewById(R.id.home_menu);
		gView = (GrowthImgGridView) viewHome.findViewById(R.id.gridView1);
		gView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		scrollView = (BounceScrollView) viewHome
				.findViewById(R.id.bounceScrollView);
		search = (EditText) viewHome.findViewById(R.id.search);
		search.addTextChangedListener(new EditWather());
		imgPromte = (ImageView) viewHome.findViewById(R.id.imgNews);
		searchListView = (ListView) viewHome.findViewById(R.id.searchListView);
		if (prompt) {
			imgPromte.setVisibility(View.VISIBLE);
		}
	}

	public void setVisibleImgPrompt() {
		imgPromte.setVisibility(View.VISIBLE);
	}

	private void setListener() {
		scrollView.setOnRefreshComplete(this);
		searchListView.setOnItemClickListener(this);
		mMenu.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				((MainActivity1) mcontext).getSlidingMenu().toggle();

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
				it.putExtra("newGrowthCount", listModle.get(position)
						.getNewGrowthCount());
				it.putExtra("newChatCount", listModle.get(position)
						.getNewChatCount());
				it.putExtra("newDynamicCount", listModle.get(position)
						.getNewDynamicCount());
				it.putExtra("newCommentCount", listModle.get(position)
						.getNewCommentCount());
				mcontext.startActivity(it);
				Utils.leftOutRightIn(mcontext);
				remorePromptCount(listModle.get(position).getCirID(), listModle
						.get(position).getNewMemberCount(), 0);

			}
		});

		// gView.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// return MotionEvent.ACTION_MOVE == event.getAction() ? true
		// : false;
		// }
		// });
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
	private CircleModle newCircle() {
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
		it.putExtra("iconImg", searchListModle.get(arg2).getImg());
		mcontext.startActivity(it);
		Utils.leftOutRightIn(mcontext);
	}

	@Override
	public void messagePrompt(boolean messagePrompt) {
	}

	@Override
	public void myCardPrompt(boolean myCardPrompt) {

	}

	@Override
	public void homePrompt(boolean rompt) {
		setVisibleImgPrompt();

	}
}
