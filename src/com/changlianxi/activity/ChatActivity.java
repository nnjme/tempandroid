package com.changlianxi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.adapter.MessageAdapter;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.PushChat;
import com.changlianxi.inteface.SendMessageAndChatCallBack;
import com.changlianxi.inteface.UpLoadPic;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.modle.MessageModle;
import com.changlianxi.modle.SelectPicModle;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.task.GetChatListTask;
import com.changlianxi.task.GetChatListTask.GetChatsList;
import com.changlianxi.task.SendMessageThread;
import com.changlianxi.task.UpLoadPicAsyncTask;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.Expressions;
import com.changlianxi.util.PushMessageReceiver;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.MyListView;
import com.changlianxi.view.MyListView.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 圈子聊天界面
 * 
 * @author teeker_bin
 * 
 */
public class ChatActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener, SendMessageAndChatCallBack, PushChat {
	private ImageView back;
	private TextView cirName;
	private String cid = "";
	private String txtCirName;
	private EditText editContent;// 内容输入框
	private Button btnSend;// 发送按钮
	private ImageView imgAdd;
	private MyListView listview;
	private LinearLayout layAdd;
	private LinearLayout layoutExpression;
	private LinearLayout layoutImg;
	private RelativeLayout expression;
	private boolean layAddIsShow = false;
	private ViewPager viewPager;
	private ArrayList<GridView> grids;
	private ImageView[] imageViews;// 圆点
	private int[] expressionImages;
	private String[] expressionImageNames;
	private int[] expressionImages1;
	private String[] expressionImageNames1;
	private int[] expressionImages2;
	private String[] expressionImageNames2;
	private int[] expressionImages3;
	private String[] expressionImageNames3;
	private int[] expressionImages4;
	private String[] expressionImageNames4;
	private int page = 0;// 标记表情当前页
	private List<MessageModle> listModle = new ArrayList<MessageModle>();
	private MessageAdapter adapter;
	private Queue<HashMap<String, Object>> queueMap = new LinkedList<HashMap<String, Object>>();// 用于发送私信的队列
	private SendMessageThread messageThread;
	private String avatarPath;
	private String startTime = "";
	private String endTime = "";
	private boolean isRefresh = false;// 是否是下拉刷新
	private SelectPicPopwindow pop;
	private GetChatListTask task;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Utils.showToast((String) msg.obj);
				break;
			case 1:
				setListener();
				initExpression();
				initViewPager();
				getChatRecord();
				break;
			default:
				break;
			}
		}
	};

	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		cid = getIntent().getStringExtra("cirID");
		txtCirName = getIntent().getStringExtra("cirName");
		endTime = DateUtils.phpTime(System.currentTimeMillis());
		PushMessageReceiver.setPushChatCallBack(this);
		messageThread = new SendMessageThread("/chats/isend");
		messageThread.setRun(true);
		messageThread.setQueueMap(queueMap);
		messageThread.setMessageAndChatCallBack(this);
		messageThread.start();
		findViewById();
		mHandler.sendEmptyMessageDelayed(1, 100);
		getMyName();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(getClass().getName() + "");
		CircleActivity
				.setInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	/**
	 * 数据统计
	 */
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getName() + "");
		// MobclickAgent.onPause(this);
	}
	
	private void getMyName() {
		MemberInfoModle modle = DBUtils.selectNameAndImgByID(SharedUtils
				.getString("uid", ""));
		if (modle != null) {
			avatarPath = modle.getAvator();
		}
	}

	private void getChatRecord() {
		listModle = DBUtils.getChatMessage(cid);
		adapter.setData(listModle);
		listview.setSelection(listModle.size());// 将listview滑动到最低端
		if (listModle.size() == 0) {
			getChats();
			listview.Refush();
		}
	}

	/**
	 * 初始化控件
	 */
	public void findViewById() {
		initImageViews();
		back = (ImageView) findViewById(R.id.back);
		cirName = (TextView) findViewById(R.id.titleTxt);
		expression = (RelativeLayout) findViewById(R.id.expression);
		layoutExpression = (LinearLayout) findViewById(R.id.layoutExpression);
		layoutImg = (LinearLayout) findViewById(R.id.layoutImg);
		layAdd = (LinearLayout) findViewById(R.id.layoutAdd);
		imgAdd = (ImageView) findViewById(R.id.imgAdd);
		listview = (MyListView) findViewById(R.id.listView);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		editContent = (EditText) findViewById(R.id.editContent);
		btnSend = (Button) findViewById(R.id.btnSend);
		adapter = new MessageAdapter(this, listModle);
		listview.setAdapter(adapter);
	}

	private void setListener() {
		back.setOnClickListener(this);
		cirName.setText(txtCirName);
		imgAdd.setOnClickListener(this);
		layoutExpression.setOnClickListener(this);
		layoutImg.setOnClickListener(this);
		btnSend.setOnClickListener(this);
		listview.setCacheColorHint(0);
		listview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		listview.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				if (!Utils.isNetworkAvailable()) {
					Utils.showToast("当前无网络，请检查网络连接");
					return;
				}
				isRefresh = true;
				if (listModle.size() == 0) {
					endTime = DateUtils.phpTime(System.currentTimeMillis());
				} else {
					endTime = DateUtils.phpTime(DateUtils
							.convertToDate(listModle.get(0).getTime()));

				}
				getChats();
			}
		});
	}

	/**
	 * 初始化圆点
	 */
	private void initImageViews() {
		ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);//
		// 包裹小圆点的LinearLayout
		imageViews = new ImageView[5];
		for (int i = 0; i < imageViews.length; i++) {
			// 设置 每张图片的句点
			ImageView imageView = new ImageView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(5, 5, 5, 5);
			imageView.setLayoutParams(params);
			imageViews[i] = imageView;
			if (i == 0) {
				// 默认选中第一张图片
				imageViews[i].setBackgroundResource(R.drawable.face_current);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.face);
			}
			group.addView(imageViews[i]);
		}
	}

	/**
	 * 初始化viewPager
	 */
	private void initViewPager() {
		LayoutInflater inflater = LayoutInflater.from(this);
		grids = new ArrayList<GridView>();
		int expressionimage[] = null;
		for (int i = 0; i < 4; i++) {
			switch (i) {
			case 0:
				expressionimage = expressionImages;
				break;
			case 1:
				expressionimage = expressionImages1;
				break;
			case 2:
				expressionimage = expressionImages2;
				break;
			case 3:
				expressionimage = expressionImages3;
				break;
			case 4:
				expressionimage = expressionImages4;
				break;
			default:
				break;
			}
			GridView gView = (GridView) inflater.inflate(R.layout.grid1, null);
			gView.setSelector(new ColorDrawable(Color.TRANSPARENT));
			List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
			// 生成24个表情 每页显示个数
			for (int j = 0; j < 21; j++) {
				Map<String, Object> listItem = new HashMap<String, Object>();
				listItem.put("image", expressionimage[j]);
				listItems.add(listItem);
			}
			SimpleAdapter simpleAdapter = new SimpleAdapter(ChatActivity.this,
					listItems, R.layout.singleexpression,
					new String[] { "image" }, new int[] { R.id.image });
			gView.setAdapter(simpleAdapter);
			gView.setOnItemClickListener(this);
			grids.add(gView);
		}

		viewPager.setAdapter(mPagerAdapter);
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
	}

	private void initExpression() {
		// 引入表情
		expressionImages = Expressions.expressionImgs;
		expressionImageNames = Expressions.expressionImgNames;
		expressionImages1 = Expressions.expressionImgs1;
		expressionImageNames1 = Expressions.expressionImgNames1;
		expressionImages2 = Expressions.expressionImgs2;
		expressionImageNames2 = Expressions.expressionImgNames2;
		expressionImages3 = Expressions.expressionImgs3;
		expressionImageNames3 = Expressions.expressionImgNames3;
		expressionImages4 = Expressions.expressionImgs4;
		expressionImageNames4 = Expressions.expressionImgNames4;
	}

	// 填充ViewPager的数据适配器
	private PagerAdapter mPagerAdapter = new PagerAdapter() {
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return grids.size();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(grids.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(grids.get(position));
			return grids.get(position);
		}
	};

	// ** 指引页面改监听器 */
	class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			page = arg0;
			for (int i = 0; i < imageViews.length; i++) {// 设置当前圆点
				if (arg0 == i) {
					imageViews[arg0]
							.setBackgroundResource(R.drawable.face_current);
					continue;
				}
				imageViews[i].setBackgroundResource(R.drawable.face);
			}
		}
	}

	private void getChats() {
		task = new GetChatListTask(cid, startTime, endTime);
		task.setTaskCallBack(new GetChatsList() {
			@Override
			public void getChatsList(List<MessageModle> modles) {
				listview.onRefreshComplete();
				if (modles.size() == 0) {
					return;
				}
				if (isRefresh) {
					modles.remove(0);
					listModle.addAll(0, modles);
					adapter.setData(listModle);
				} else {
					listModle.addAll(modles);
					adapter.setData(listModle);
					listview.setSelection(listModle.size());// 将listview滑动到最低端
				}

			}
		});
		task.execute();
	}

	/**
	 * 将信息发送到服务器
	 */
	private void sendToServer(String content) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("cid", cid);
		map.put("token", SharedUtils.getString("token", ""));
		map.put("content", content);
		queueMap.offer(map);
		messageThread.setQueueMap(queueMap);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			BroadCast.sendBroadCast(this, Constants.CHANGE_TAB);

			// finish();
			// this.getParent().overridePendingTransition(R.anim.right_in,
			// R.anim.right_out);
			break;
		case R.id.imgAdd:
			if (layAddIsShow) {
				layAdd.setVisibility(View.GONE);
				expression.setVisibility(View.GONE);
				layAddIsShow = false;
				return;
			}
			layAddIsShow = true;
			Utils.hideSoftInput(this);
			layAdd.setVisibility(View.VISIBLE);
			break;
		case R.id.layoutExpression:
			expression.setVisibility(View.VISIBLE);
			layAdd.setVisibility(View.GONE);
			break;
		case R.id.layoutImg:
			pop = new SelectPicPopwindow(this, v);
			pop.show();
			layAdd.setVisibility(View.GONE);
			break;
		case R.id.btnSend:
			if (!DBUtils.isAuth(SharedUtils.getString("uid", ""), cid)) {
				Utils.showToast("您不是认证人员，没有权限发送消息");
				return;
			}
			String content = editContent.getText().toString();
			if (content.length() == 0) {
				Utils.showToast("发送内容不能为空");
				return;
			}
			refushAdapter(content, 0);
			sendToServer(content);
			break;

		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int expressionimage[] = null;
		String expressionname[] = null;
		switch (page) {
		case 0:
			expressionimage = expressionImages;
			expressionname = expressionImageNames;
			break;
		case 1:
			expressionimage = expressionImages1;
			expressionname = expressionImageNames1;
			break;
		case 2:
			expressionimage = expressionImages2;
			expressionname = expressionImageNames2;
			break;
		case 3:
			expressionimage = expressionImages3;
			expressionname = expressionImageNames3;
			break;
		case 4:
			expressionimage = expressionImages4;
			expressionname = expressionImageNames4;
			break;
		default:
			break;
		}
		if (arg2 == expressionimage.length - 1) {
			int selection = editContent.getSelectionStart();
			String text = editContent.getText().toString();
			if (selection > 0) {
				String text2 = text.substring(selection - 1);
				if (":".equals(text2)) {
					int start = StringUtils.getPositionEmoj(text);
					int end = selection;
					editContent.getText().delete(start, end);
					return;
				}
				editContent.getText().delete(selection - 1, selection);
			}
			return;
		}
		Bitmap bitmap = null;
		bitmap = BitmapFactory.decodeResource(getResources(),
				expressionimage[arg2]);
		ImageSpan imageSpan = new ImageSpan(ChatActivity.this, bitmap);
		SpannableString spannableString = new SpannableString(
				expressionname[arg2].substring(1,
						expressionname[arg2].length() - 1));
		spannableString.setSpan(imageSpan, 0,
				expressionname[arg2].length() - 2,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 编辑框设置数据
		editContent.append(spannableString);

	}

	@Override
	public void getReturnStrAndMid(String result) {
		try {
			JSONObject object = new JSONObject(result);
			String rt = object.getString("rt");
			if (!rt.equals("1")) {
				String errorCoce = object.getString("err");
				String strError = ErrorCodeUtil.convertToChines(errorCoce);
				Message msg = mHandler.obtainMessage();
				msg.what = 0;
				msg.obj = strError;
				mHandler.sendMessage(msg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取聊天推送回调接口
	 */
	@Override
	public void getPushChat(String str) {
		MessageModle modle = Utils.getChatModle(str);
		if (modle == null) {
			return;
		}
		if (modle.getUid().equals(SharedUtils.getString("uid", ""))) {
			return;
		}
		listModle.add(modle);
		adapter.notifyDataSetChanged();
		listview.setSelection(listModle.size());// 将listview滑动到最低端
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// finish();
			// getParent().overridePendingTransition(R.anim.right_in,
			// R.anim.right_out);
			BroadCast.sendBroadCast(this, Constants.CHANGE_TAB);

		}
		return true;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String path = "";
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD) {
			if (data == null) {
				return;
			}
			SelectPicModle picmodle = BitmapUtils.getPickPic(this, data);
			path = picmodle.getPicPath();
		}
		// 拍摄图片
		else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
			path = pop.getTakePhotoPath();
		}
		refushAdapter(path, 1);
		upLoadPic(path);
	}

	private void refushAdapter(String content, int type) {
		MessageModle modle = new MessageModle();
		modle.setContent(content);
		modle.setSelf(true);
		modle.setAvatar(avatarPath);
		modle.setCid(cid);
		modle.setType(type);
		modle.setTime(DateUtils.getCurrDateStr());
		listModle.add(modle);
		adapter.notifyDataSetChanged();
		listview.setSelection(listModle.size());// 每次发送之后将listview滑动到最低端
		// 从而显示最新消息
		editContent.setText("");
		DBUtils.saveChatMessage(modle);// 保存到本地数据库

	}

	/**
	 * 上传聊天图片
	 */
	private void upLoadPic(String path) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cid", cid);
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		UpLoadPicAsyncTask picTask = new UpLoadPicAsyncTask(map,
				"/chats/isendImg", path, "image");
		picTask.setCallBack(new UpLoadPic() {
			@Override
			public void upLoadFinish(boolean flag) {
				if (!flag) {
					Utils.showToast("图片发送失败");
				}
			}
		});
		picTask.execute();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
			task.cancel(true); // 如果Task还在运行，则先取消它
		}
		PushMessageReceiver.pushChat = null;
		messageThread.running = false;
		DBUtils.delCircleChatMessage(cid);
		int size = listModle.size();
		int count = size > 20 ? size - 20 : 0;
		for (int i = listModle.size() - 1; i >= count; i--) {
			MessageModle modle = listModle.get(i);
			DBUtils.saveChatMessage(modle);
		}
	}
}
