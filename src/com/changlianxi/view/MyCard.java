package com.changlianxi.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.changlianxi.activity.R;
import com.changlianxi.inteface.ChangeView;
import com.changlianxi.inteface.UpLoadPic;
import com.changlianxi.modle.Info;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.task.UpLoadPicAsyncTask;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.util.WigdtContorl;
import com.changlianxi.view.FlipperLayout.OnOpenListener;

public class MyCard implements OnClickListener, ChangeView, UpLoadPic {
	private Context mContext;
	private View mCard;
	private OnOpenListener mOnOpenListener;
	private ImageView back;
	private MyViewGroup rGroup;
	private MyCardShow show;
	private MyCardEdit edit;
	private int flag = 0;// 0标示显示界面 1 编辑界面
	private ImageView avatar;
	private ImageView avatarBg;
	private RelativeLayout layAvatar;
	private ProgressDialog pd;

	/**
	 * 构造
	 * 
	 * @param context
	 */
	public MyCard(Context context) {
		this.mContext = context;
		mCard = LayoutInflater.from(context).inflate(R.layout.mycard, null);
		findViewById();
		setListener();
		show = new MyCardShow(context, avatar);
		rGroup.addView(show.getView());
		show.setChangeView(this);
	}

	private void findViewById() {
		back = (ImageView) mCard.findViewById(R.id.back);
		rGroup = (MyViewGroup) mCard.findViewById(R.id.infoGroup);
		avatar = (ImageView) mCard.findViewById(R.id.avatar);
		avatarBg = (ImageView) mCard.findViewById(R.id.avatarBg);
		WigdtContorl.setAvatarWidth(mContext, avatar, avatarBg);
		layAvatar = (RelativeLayout) mCard.findViewById(R.id.LayAvatar);
	}

	private void setListener() {
		back.setOnClickListener(this);
		layAvatar.setOnClickListener(this);
	}

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		mOnOpenListener = onOpenListener;
	}

	public View getView() {
		return mCard;
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
			if (mOnOpenListener != null) {
				mOnOpenListener.open();
			}

			break;
		case R.id.LayAvatar:
			SelectPicPopwindow pop = new SelectPicPopwindow(mContext, v);
			pop.show();
			break;
		default:
			break;
		}
	}

	/**
	 * 设置头像
	 * 
	 * @param avatarPath
	 * @param bmp
	 */
	public void setAvatarPath(String avatarPath, Bitmap bmp) {
		avatar.setImageBitmap(bmp);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("token", SharedUtils.getString("token", ""));
		map.put("pid", show.pid);
		UpLoadPicAsyncTask picTask = new UpLoadPicAsyncTask(map,
				"/people/iuploadMyAvatar", avatarPath);
		picTask.setCallBack(this);
		picTask.execute();
		pd = new ProgressDialog(mContext);
		pd.show();
	}

	@Override
	public void setViewData(List<Info> data, int type, String cid, String pid,
			String tableName) {
		edit = new MyCardEdit(mContext, data, type, pid);
		edit.setChangeView(this);
		rGroup.setInfoEditView(edit.getView());
		flag = 1;
	}

	@Override
	public void delView() {
		rGroup.delView();
		flag = 0;
	}

	@Override
	public void NotifyData(List<Info> data, int infoType) {
		show.refushData(data, infoType);

	}

	/**
	 * 上传图片回调接口
	 */
	@Override
	public void upLoadFinish(boolean flag) {
		pd.dismiss();
		if (flag) {
			Utils.showToast("上传成功");
			return;
		}
		Utils.showToast("上传失败");

	}
}
