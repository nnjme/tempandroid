package com.changlianxi.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changlianxi.inteface.ChangeView;
import com.changlianxi.inteface.UpLoadPic;
import com.changlianxi.modle.Info;
import com.changlianxi.modle.SelectPicModle;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.task.UpLoadPicAsyncTask;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.FileUtils;
import com.changlianxi.util.ImageManager;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.util.WigdtContorl;
import com.changlianxi.util.WigdtContorl.Visible;
import com.changlianxi.view.MyViewGroup;
import com.changlianxi.view.UserInfoEdit;
import com.changlianxi.view.UserInfoShow;

/**
 * �û�������ʾ����
 * 
 * @author teeker_bin
 * 
 */
public class UserInfoActivity extends Activity implements Visible, ChangeView,
		OnClickListener, UpLoadPic {
	private RelativeLayout drag;
	private Button scrollDrag;
	private TextView txtname;
	private ImageView imgback;
	private String iconPath;
	private String userlistName;// ���ϴ洢��
	private MyViewGroup rGroup;
	private UserInfoShow infoShow;
	private UserInfoEdit vEdit;
	private LinearLayout layCall;
	private String pid;// �û�id
	private String cid;// Ȧ��id
	private String username;
	private LinearLayout sendMessage;
	private RelativeLayout layAvatar;
	private ImageView avatar;
	private ImageView avatarBg;
	private ProgressDialog pd;
	private int flag = 0;// 0��ʾ��ʾ���� 1 �༭����

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
		layAvatar = (RelativeLayout) findViewById(R.id.LayAvatar);
		layAvatar.setOnClickListener(this);
		avatar = (ImageView) findViewById(R.id.avatar);
		avatarBg = (ImageView) findViewById(R.id.avatarBg);
		WigdtContorl.setAvatarWidth(this, avatar, avatarBg);
		sendMessage = (LinearLayout) findViewById(R.id.sendMessage);
		sendMessage.setOnClickListener(this);
		layCall = (LinearLayout) findViewById(R.id.call);
		layCall.setOnClickListener(this);
		infoShow = new UserInfoShow(this, userlistName, pid, cid);
		rGroup = (MyViewGroup) findViewById(R.id.infoGroup);
		rGroup.addView(infoShow.getView());
		scrollDrag = (Button) infoShow.getView().findViewById(R.id.scrolldrag);
		scrollDrag.setOnTouchListener(MyTouchListener);
		drag = (RelativeLayout) findViewById(R.id.drag);
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
		Logger.debug(this, "iconPath:" + iconPath);
		avatar.post(new Runnable() {
			@Override
			public void run() {
				int[] location = new int[2];
				// iconImg.getLocationInWindow(location); // ��ȡ�ڵ�ǰ�����ڵľ�������
				avatar.getLocationOnScreen(location);// ��ȡ��������Ļ�ڵľ�������
				WigdtContorl.moveY = location[1] - avatar.getHeight();
				Logger.debug(this, "moveY:" + WigdtContorl.moveY);
				ImageManager.from(UserInfoActivity.this).displayImage(avatar,
						iconPath, -1, avatar.getWidth(), avatar.getWidth());
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
				// ��ס�¼�������ִ�д��������
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				// �ƶ��¼�������ִ�д��������
				y2 = (int) (y1 - event.getY());
				if (y2 > 0) {
					WigdtContorl.setLayoutY_UP(drag, y2, UserInfoActivity.this,
							layAvatar);
				} else {
					WigdtContorl.setLayoutY_Down(drag, y2,
							UserInfoActivity.this, layAvatar);
				}
				break;
			}
			case MotionEvent.ACTION_UP: {
				// �ɿ��¼�������ִ�д��������
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
		layAvatar.setAnimation(ani1);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			if (flag == 1) {
				rGroup.delView();
				flag = 0;
				return;
			}
			finish();
			break;
		case R.id.LayAvatar:
			SelectPicPopwindow pop = new SelectPicPopwindow(this, v);
			pop.show();
			break;
		case R.id.call:
			infoShow.moveToCall();
			break;
		case R.id.sendMessage:
			Intent intent = new Intent();
			intent.putExtra("uid", pid);
			intent.putExtra("cid", cid);
			intent.putExtra("type", "write");
			intent.setClass(this, MessageActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bitmap bitmap = null;
		String avatarPath = "";
		if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD
				&& resultCode == RESULT_OK && data != null) {
			SelectPicModle modle = BitmapUtils.getPickPic(this, data);
			avatarPath = modle.getPicPath();
			bitmap = modle.getBmp();
		}// ����ͼƬ
		else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
			if (resultCode != RESULT_OK) {
				return;
			}
			super.onActivityResult(requestCode, resultCode, data);
			Bundle bundle = data.getExtras();
			bitmap = (Bitmap) bundle.get("data");// ��ȡ������ص����ݣ���ת��ΪBitmapͼƬ��ʽ

			if (bitmap != null) {
				String dir = "/clx/camera/";
				Utils.createDir(dir);
				String name = FileUtils.getFileName() + ".jpg";
				String fileName = Utils.getgetAbsoluteDir(dir) + name;
				BitmapUtils.createImgToFile(bitmap, fileName);
				avatarPath = fileName;
			}
		}
		avatar.setImageBitmap(bitmap);
		upLoadPic(avatarPath);
	}

	/**
	 * �ϴ�ͷ��
	 */
	private void upLoadPic(String avatarPath) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("pid", pid);
		map.put("cid", cid);
		UpLoadPicAsyncTask picTask = new UpLoadPicAsyncTask(map,
				"/people/iuploadAvatar", avatarPath);
		picTask.setCallBack(this);
		picTask.execute();
		pd = new ProgressDialog(this);
		pd.show();
	}

	@Override
	public void setViewData(List<Info> data, int type, String cid, String pid,
			String tableName) {
		vEdit = new UserInfoEdit(this, data, type, cid, pid, tableName);
		vEdit.setChangeView(this);
		rGroup.setInfoEditView(vEdit.getView());
		flag = 1;

	}

	@Override
	public void delView() {
		rGroup.delView();
		flag = 0;

	}

	@Override
	public void NotifyData(List<Info> data, int infoType) {
		infoShow.refushData(data, infoType);
	}

	@Override
	public void upLoadFinish(boolean flag) {
		pd.dismiss();
		if (flag) {
			Utils.showToast("�ϴ��ɹ�");
			return;
		}
		Utils.showToast("�ϴ�ʧ��");

	}
}
