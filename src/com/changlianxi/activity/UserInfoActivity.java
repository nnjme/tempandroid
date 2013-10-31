package com.changlianxi.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changlianxi.inteface.ChangeView;
import com.changlianxi.modle.Info;
import com.changlianxi.util.ImageManager;
import com.changlianxi.util.Logger;
import com.changlianxi.util.WigdtContorl;
import com.changlianxi.util.WigdtContorl.Visible;
import com.changlianxi.view.MyViewGroup;
import com.changlianxi.view.UserInfoEdit;
import com.changlianxi.view.UserInfoShow;

/**
 * 用户资料显示界面
 * 
 * @author teeker_bin
 * 
 */
public class UserInfoActivity extends Activity implements Visible, ChangeView,
		OnClickListener {
	private RelativeLayout drag;
	private FrameLayout imgFrame;
	private Button scrollDrag;
	private TextView txtname;
	private ImageView imgback;
	private ImageView iconImg;
	private String iconPath;
	private String userlistName;// 资料存储表
	private MyViewGroup rGroup;
	private UserInfoShow infoShow;
	private UserInfoEdit vEdit;
	private LinearLayout layCall;
	private String pid;// 用户id
	private String cid;// 圈子id
	private String username;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.user_info);
		iconPath = getIntent().getStringExtra("iconImg");
		userlistName = getIntent().getStringExtra("userlistname");
		username = getIntent().getStringExtra("username");
		pid = getIntent().getStringExtra("pid");
		cid = getIntent().getStringExtra("cid");
		layCall = (LinearLayout) findViewById(R.id.call);
		layCall.setOnClickListener(this);
		infoShow = new UserInfoShow(this, userlistName, pid, cid);
		rGroup = (MyViewGroup) findViewById(R.id.infoGroup);
		rGroup.addView(infoShow.getView());
		scrollDrag = (Button) infoShow.getView().findViewById(R.id.scrolldrag);
		scrollDrag.setOnTouchListener(MyTouchListener);
		drag = (RelativeLayout) findViewById(R.id.drag);
		imgFrame = (FrameLayout) findViewById(R.id.imgframe);
		drag.setOnTouchListener(MyTouchListener);
		drag.post(new Runnable() {
			@Override
			public void run() {
				WigdtContorl.delaultY = drag.getTop();
				Logger.debug(this, "delaultY:" + WigdtContorl.delaultY);
			}
		});
		txtname = (TextView) findViewById(R.id.name);
		txtname.setText(username);
		imgback = (ImageView) findViewById(R.id.back);
		imgback.setOnClickListener(this);
		iconImg = (ImageView) findViewById(R.id.img);
		ImageManager.from(this).displayImage(iconImg, iconPath, -1, 60, 60);
		iconImg.post(new Runnable() {

			@Override
			public void run() {
				int[] location = new int[2];
				// iconImg.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
				iconImg.getLocationOnScreen(location);// 获取在整个屏幕内的绝对坐标
				WigdtContorl.moveY = location[1] - iconImg.getHeight();
				Logger.debug(this, "moveY:" + WigdtContorl.moveY);

			}
		});
		WigdtContorl.setVisible(this);
		infoShow.setChangeView(this);
	}

	private OnTouchListener MyTouchListener = new OnTouchListener() {
		int y1 = 0, y2;

		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				y1 = (int) event.getY();
				// 按住事件发生后执行代码的区域
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				// 移动事件发生后执行代码的区域
				y2 = (int) (y1 - event.getY());
				if (y2 > 0) {
					WigdtContorl.setLayoutY_UP(drag, y2, UserInfoActivity.this,
							imgFrame);
				} else {
					WigdtContorl.setLayoutY_Down(drag, y2,
							UserInfoActivity.this, imgFrame);
				}
				Logger.debug(this, "move:" + y2 + "  y1:" + y1);
				break;
			}
			case MotionEvent.ACTION_UP: {
				// 松开事件发生后执行代码的区域
				break;
			}
			default:
				break;
			}
			return true;
		}
	};

	public void setVisible(boolean visible) {
		Animation ani1 = null;
		if (visible) {
			ani1 = AnimationUtils.loadAnimation(this,
					R.anim.alpha_animation_show);
			scrollDrag.setVisibility(View.GONE);

		} else {
			ani1 = AnimationUtils.loadAnimation(this,
					R.anim.alpha_animation_hidden);
			scrollDrag.setVisibility(View.VISIBLE);

		}
		imgFrame.setAnimation(ani1);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		// case R.id.edt_basic_info:
		// rGroup.setView(vEditor);
		// break;
		case R.id.call:
			infoShow.moveToCall();
			break;
		default:
			break;
		}

	}

	@Override
	public void setViewData(List<Info> data, int type, String cid, String pid,
			String tableName) {
		vEdit = new UserInfoEdit(this, data, type, cid, pid, tableName);
		vEdit.setChangeView(this);
		rGroup.setInfoEditView(vEdit.getView());
	}

	@Override
	public void delView() {
		rGroup.delView();
	}

	@Override
	public void NotifyData(List<Info> data, int infoType) {
		infoShow.refushData(data, infoType);
	}
}
