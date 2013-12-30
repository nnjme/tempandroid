package com.changlianxi.view;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.changlianxi.activity.R;
import com.changlianxi.db.DBUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.view.FlipperLayout.OnOpenListener;
import com.changlianxi.view.MyCardShow1.OnButtonClickListener;
import com.changlianxi.view.MyDetailChange.OnBackClick;

public class MyCard implements OnButtonClickListener, OnBackClick {
	private Context mContext;
	private View mCard;
	private OnOpenListener mOnOpenListener;
	private MyViewGroup rGroup;
	public MyCardShow1 cardShow;
	private MyDetailChange cardChange;

	/**
	 * 构造
	 * 
	 * @param context
	 */
	public MyCard(Context context) {
		this.mContext = context;
		mCard = LayoutInflater.from(context).inflate(R.layout.mycard, null);
		findViewById();
		cardShow = new MyCardShow1(mContext);
		cardShow.setOnBack(this);
		rGroup.addView(cardShow.getView());
	}

	private void findViewById() {
		rGroup = (MyViewGroup) mCard.findViewById(R.id.infoGroup);

	}

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		mOnOpenListener = onOpenListener;
	}

	public View getView() {
		return mCard;
	}

	@Override
	public void onBackClick() {
		if (mOnOpenListener != null) {
			mOnOpenListener.open();
		}
	}

	@Override
	public void onChangeClick(String name, String avatarURL) {
		cardChange = new MyDetailChange(mContext, name, avatarURL);
		cardChange.setCallBack(this);
		rGroup.addView(cardChange.getView());

	}

	@Override
	public void onChangeBackClick(int size) {
		rGroup.delView();
		if (size == 0) {
			cardShow.isDetailChange("0");
			ContentValues cv = new ContentValues();
			cv.put("changed", "0");
			DBUtils.updateInfo(Constants.MYDETAIL, cv, "uid=?",
					new String[] { SharedUtils.getString("uid", "") });
		}
	}
}
