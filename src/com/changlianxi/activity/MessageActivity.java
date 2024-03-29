package com.changlianxi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
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

import com.changlianxi.adapter.MessageAdapter;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.GetMessagesCallBack;
import com.changlianxi.inteface.PushMessages;
import com.changlianxi.inteface.SendMessageAndChatCallBack;
import com.changlianxi.inteface.UpLoadPic;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.modle.MessageModle;
import com.changlianxi.modle.SelectPicModle;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.task.GetMessagesTask;
import com.changlianxi.task.SendMessageThread;
import com.changlianxi.task.UpLoadPicAsyncTask;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.Expressions;
import com.changlianxi.util.PushMessageReceiver;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.MyListView;
import com.changlianxi.view.MyListView.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;
import com.changlianxi.R;

/**
 * 私信聊天界面
 * 
 * @author teeker_bin
 * 
 */
public class MessageActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener, PushMessages, GetMessagesCallBack,
		SendMessageAndChatCallBack {
	private List<MessageModle> listModle = new ArrayList<MessageModle>();
	private Button btnSend;// 发送按钮
	private EditText editContent;// 内容输入框
	private MyListView listview;
	private ImageView back;
	private TextView name;// 显示接受私信者的姓名
	private String ruid;// 接受私信者id
	private MessageAdapter adapter;
	private String cid;
	private ImageView imgAdd;
	private LinearLayout layAdd;
	private LinearLayout layoutExpression;
	private RelativeLayout expression;
	private LinearLayout layoutImg;
	private boolean layAddIsShow = false;
	private Queue<HashMap<String, Object>> queueMap = new LinkedList<HashMap<String, Object>>();// 用于发送私信的队列
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
	private Dialog pd;
	private SendMessageThread messageThread;
	private String avatarPath;
	private String startTime = "";
	private String endTime = "";
	private boolean isRefresh = false;// 是否是下拉刷新
	private String receiveName;// 私信对方姓名
	private SelectPicPopwindow pop;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Utils.showToast((String) msg.obj);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		getIntentData();
		findViewById();
		setListener();
		initExpression();
		initViewPager();
		PushMessageReceiver.setPushMessageCallBack(this);
		getNameById();
		messageThread = new SendMessageThread("/messages/isend");
		messageThread.setRun(true);
		messageThread.setQueueMap(queueMap);
		messageThread.setMessageAndChatCallBack(this);
		messageThread.start();
		getDBMessage();
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
	
	private void getIntentData() {
		ruid = getIntent().getStringExtra("ruid");
		cid = getIntent().getStringExtra("cid");
		receiveName = getIntent().getStringExtra("name");
	}

	/**
	 * 先从数据库获取数据
	 */
	private void getDBMessage() {
		adapter.setData(listModle);
		listview.setSelection(listModle.size());
		if (listModle.size() == 0) {
			pd = DialogUtil.getWaitDialog(this, "请稍后");
			pd.show();
			getSeverMessage();
			return;
		}
	}

	private void getNameById() {
		name.setText(receiveName);
		MemberInfoModle info = DBUtils.selectNameAndImgByID(SharedUtils
				.getString("uid", ""));
		if (info != null) {
			avatarPath = info.getAvator();
		}
	}

	/**
	 * 获取私信内容
	 */
	private void getSeverMessage() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("ruid", ruid);
		map.put("start", startTime);
		map.put("end", endTime);
		GetMessagesTask task = new GetMessagesTask(this, map,
				"/messages/imessages", ruid);
		task.setTaskCallBack(this);
		task.execute();

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

	/**
	 * 初始化控件
	 */
	private void findViewById() {
		initImageViews();
		expression = (RelativeLayout) findViewById(R.id.expression);
		layoutExpression = (LinearLayout) findViewById(R.id.layoutExpression);
		layoutImg = (LinearLayout) findViewById(R.id.layoutImg);
		layoutImg.setOnClickListener(this);
		layoutExpression.setOnClickListener(this);
		layAdd = (LinearLayout) findViewById(R.id.layoutAdd);
		imgAdd = (ImageView) findViewById(R.id.imgAdd);
		btnSend = (Button) findViewById(R.id.btnSend);
		editContent = (EditText) findViewById(R.id.editContent);
		listview = (MyListView) findViewById(R.id.listView);
		back = (ImageView) findViewById(R.id.back);
		name = (TextView) findViewById(R.id.titleTxt);
		adapter = new MessageAdapter(this, listModle);
		listview.setAdapter(adapter);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		listview.setCacheColorHint(0);
		listview.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				isRefresh = true;
				isRefresh = true;
				if (listModle.size() == 0) {
					endTime = DateUtils.phpTime(System.currentTimeMillis());
				} else {
					endTime = DateUtils.phpTime(DateUtils
							.convertToDate(listModle.get(0).getTime()));

				}
				getSeverMessage();
			}
		});
	}

	/**
	 * 初始化圆点
	 */
	private void initImageViews() {
		ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);// 包裹小圆点的LinearLayout
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
			SimpleAdapter simpleAdapter = new SimpleAdapter(
					MessageActivity.this, listItems, R.layout.singleexpression,
					new String[] { "image" }, new int[] { R.id.image });
			gView.setAdapter(simpleAdapter);
			gView.setOnItemClickListener(this);
			grids.add(gView);
		}

		viewPager.setAdapter(mPagerAdapter);
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
	}

	/**
	 * 设置点击事件
	 */
	private void setListener() {
		btnSend.setOnClickListener(this);
		back.setOnClickListener(this);
		imgAdd.setOnClickListener(this);
		listview.setSelector(new ColorDrawable(Color.TRANSPARENT));

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

	/**
	 * 将信息发送到服务器
	 */
	private void sendToServer(String content) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("cid", cid);
		map.put("token", SharedUtils.getString("token", ""));
		map.put("ruid", ruid);
		map.put("content", content);
		queueMap.offer(map);
		messageThread.setQueueMap(queueMap);
	}

	private void refushAdapter(String content, int type) {
		MessageModle modle = new MessageModle();
		modle.setContent(content);
		modle.setSelf(true);
		modle.setType(type);
		modle.setAvatar(avatarPath);
		modle.setTime(DateUtils.getCurrDateStr());
		listModle.add(modle);
		adapter.notifyDataSetChanged();
		listview.setSelection(listModle.size());// 每次发送之后将listview滑动到最低端
		// 从而显示最新消息
		editContent.setText("");
		DBUtils.saveMessage(modle, ruid);

	}

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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			Utils.rightOut(this);

			break;
		case R.id.btnSend:
			String content = editContent.getText().toString();
			if (content.length() == 0) {
				Utils.showToast("发送内容不能为空");
				return;
			}
			// 从而显示最新消息
			refushAdapter(content, 0);
			sendToServer(content);
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
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		messageThread.running = false;
		PushMessageReceiver.pushMessage = null;
		DBUtils.delMessage(ruid);
		int size = listModle.size();
		int count = size > 20 ? size - 20 : 0;
		for (int i = 0; i < count; i++) {
			MessageModle modle = listModle.get(i);
			DBUtils.saveMessage(modle, ruid);
		}
		super.onDestroy();
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
		ImageSpan imageSpan = new ImageSpan(MessageActivity.this, bitmap);
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

	/**
	 * 上传聊天图片
	 */
	private void upLoadPic(String path) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cid", cid);
		map.put("ruid", ruid);
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		UpLoadPicAsyncTask picTask = new UpLoadPicAsyncTask(map,
				"/messages/isendImg", path, "image");
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
	public void getPushMessages(String str) {
		String contetn = "";
		String time = "";
		String uid = "";
		String name = "";
		String avatarPath = "";
		try {
			JSONObject json = new JSONObject(str);
			contetn = json.getString("c");
			time = json.getString("m");
			uid = json.getString("uid");
			if (uid.equals(SharedUtils.getString("uid", ""))) {
				return;
			}
			if (!ruid.equals(uid)) {
				return;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		MemberInfoModle info = DBUtils.selectNameAndImgByID(uid);
		if (info != null) {
			avatarPath = info.getAvator();
			name = info.getName();
		}
		MessageModle modle = new MessageModle();
		modle.setAvatar(avatarPath);
		modle.setName(name);
		modle.setContent(contetn);
		modle.setSelf(false);
		modle.setTime(time);
		listModle.add(modle);
		adapter.notifyDataSetChanged();
		listview.setSelection(listModle.size());// 每次发送之后将listview滑动到最低端
	}

	@Override
	public void getMessages(List<MessageModle> list) {
		if (pd != null) {
			pd.dismiss();
		}
		listview.onRefreshComplete();
		if (list.size() == 0) {
			return;
		}
		if (isRefresh) {
			list.remove(0);
			listModle.addAll(0, list);
			adapter.setData(listModle);
		} else {
			listModle.addAll(list);
			adapter.setData(listModle);
			listview.setSelection(listModle.size());// 将listview滑动到最低端
		}
	}

	/**
	 * 发送消息后的回调接口
	 */
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
				return;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
