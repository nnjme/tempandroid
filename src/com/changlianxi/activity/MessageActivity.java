package com.changlianxi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.Window;
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
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.modle.MessageModle;
import com.changlianxi.task.GetMessagesTask;
import com.changlianxi.task.SendMessageThread;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.Expressions;
import com.changlianxi.util.Logger;
import com.changlianxi.util.PushMessageReceiver;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.MyListView;
import com.changlianxi.view.MyListView.OnRefreshListener;

/**
 * 私信聊天界面
 * 
 * @author teeker_bin
 * 
 */
public class MessageActivity extends Activity implements OnClickListener,
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
	private int page = 0;// 标记表情当前页
	private String type = "";// read 读私信 write 写私信
	private ProgressDialog pd;
	private SendMessageThread messageThread;
	private String avatarPath;
	private String startTime = "2008-08-08 12:10:12";
	private boolean isRefresh = false;// 是否是下拉刷新
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_message);
		ruid = getIntent().getStringExtra("uid");
		cid = getIntent().getStringExtra("cid");
		type = getIntent().getStringExtra("type");
		findViewById();
		setListener();
		initExpression();
		initViewPager();
		PushMessageReceiver.setPushMessageCallBack(this);
		getSeverMessage();
		getNameById();
		messageThread = new SendMessageThread("/messages/isend");
		messageThread.setRun(true);
		messageThread.setQueueMap(queueMap);
		messageThread.setMessageAndChatCallBack(this);
		messageThread.start();
	}

	private void getNameById() {
		MemberInfoModle modle = DBUtils.selectNameAndImgByID("circle" + cid,
				ruid);
		if (modle != null) {
			name.setText(modle.getName());
		}
		MemberInfoModle info = DBUtils.selectNameAndImgByID("circle" + cid,
				SharedUtils.getString("uid", ""));
		if (info == null) {
			Utils.showToast("未知错误 tableName:" + "circle" + cid + "  uid:"
					+ SharedUtils.getString("uid", ""));
		} else {
			avatarPath = info.getAvator();
		}
	}

	/**
	 * 获取私信内容
	 */
	private void getSeverMessage() {
		if (!type.equals("read")) {
			return;
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("ruid", ruid);
		Logger.debug(this, "startTIme:" + startTime);
		map.put("start", DateUtils.phpTime(DateUtils.convertToDate(startTime)));
		map.put("end", DateUtils.phpTime(System.currentTimeMillis()));
		pd = new ProgressDialog(this);
		pd.show();
		GetMessagesTask task = new GetMessagesTask(this, map,
				"/messages/imessages");
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
	}

	/**
	 * 初始化控件
	 */
	private void findViewById() {
		initImageViews();
		expression = (RelativeLayout) findViewById(R.id.expression);
		layoutExpression = (LinearLayout) findViewById(R.id.layoutExpression);
		layoutExpression.setOnClickListener(this);
		layAdd = (LinearLayout) findViewById(R.id.layoutAdd);
		imgAdd = (ImageView) findViewById(R.id.imgAdd);
		btnSend = (Button) findViewById(R.id.btnSend);
		editContent = (EditText) findViewById(R.id.editContent);
		listview = (MyListView) findViewById(R.id.listView);
		back = (ImageView) findViewById(R.id.back);
		name = (TextView) findViewById(R.id.name);
		adapter = new MessageAdapter(this, listModle);
		listview.setAdapter(adapter);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		listview.setCacheColorHint(0);
		listview.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				isRefresh = true;
				getSeverMessage();
			}
		});
	}

	/**
	 * 初始化圆点
	 */
	private void initImageViews() {
		ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);// 包裹小圆点的LinearLayout
		imageViews = new ImageView[3];
		for (int i = 0; i < imageViews.length; i++) {
			// 设置 每张图片的句点
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new LayoutParams(20, 20));
			imageView.setPadding(20, 0, 20, 0);
			imageViews[i] = imageView;
			if (i == 0) {
				// 默认选中第一张图片
				imageViews[i].setBackgroundResource(R.drawable.point01);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.point02);
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
		for (int i = 0; i < 3; i++) {
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
			default:
				break;
			}
			GridView gView = (GridView) inflater.inflate(R.layout.grid1, null);
			List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
			// 生成24个表情 每页显示个数
			for (int j = 0; j < 24; j++) {
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
	private void sendToServer() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("cid", cid);
		map.put("token", SharedUtils.getString("token", ""));
		map.put("ruid", ruid);
		map.put("content", editContent.getText().toString());
		Logger.debug(
				this,
				"uid:" + SharedUtils.getString("uid", "") + "  cid:" + cid
						+ "  ruid:" + ruid + "   token:"
						+ SharedUtils.getString("token", "") + " content:"
						+ editContent.getText().toString());
		queueMap.offer(map);
		messageThread.setQueueMap(queueMap);
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
					imageViews[arg0].setBackgroundResource(R.drawable.point01);
					continue;
				}
				imageViews[i].setBackgroundResource(R.drawable.point02);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.btnSend:
			if (editContent.getText().toString().length() == 0) {
				Utils.showToast("发送内容不能为空");
				return;
			}

			MessageModle modle = new MessageModle();
			modle.setContent(editContent.getText().toString());
			modle.setSelf(true);
			modle.setAvatar(avatarPath);
			modle.setTime(DateUtils.getCurrDateStr());
			listModle.add(modle);
			adapter.notifyDataSetChanged();
			listview.setSelection(listModle.size());// 每次发送之后将listview滑动到最低端
			// 从而显示最新消息
			sendToServer();
			editContent.setText("");
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
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		messageThread.setRun(false);
		messageThread = null;
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
		default:
			break;
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
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MemberInfoModle info = DBUtils
				.selectNameAndImgByID("circle" + cid, uid);
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
		if (list == null) {
			Utils.showToast("获取失败");
			return;
		}
		if (list.size() == 0) {
			return;
		}
		startTime = list.get(list.size() - 1).getTime();
		if (isRefresh) {
			list.remove(0);
		}
		listModle.addAll(list);
		adapter.setData(listModle);
		listview.setSelection(listModle.size());// 每次发送之后将listview滑动到最低端

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
				// mHandler.sendEmptyMessage(0);
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
