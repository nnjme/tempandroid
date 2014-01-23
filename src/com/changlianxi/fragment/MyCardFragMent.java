package com.changlianxi.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.changlianxi.R;
import com.changlianxi.db.DBUtils;
import com.changlianxi.util.BroadCast;
import com.changlianxi.util.Constants;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.view.MyCardShow;
import com.changlianxi.view.MyCardShow.OnButtonClickListener;
import com.changlianxi.view.MyDetailChange;
import com.changlianxi.view.MyDetailChange.OnBackClick;
import com.changlianxi.view.MyViewGroup;

@SuppressLint("NewApi")
public class MyCardFragMent extends Fragment implements OnButtonClickListener,
		OnBackClick {
	private Context mContext;
	private View mCard;
	private MyViewGroup rGroup;
	public MyCardShow cardShow;
	private MyDetailChange cardChange;

	public MyCardFragMent(Context context) {
		this.mContext = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mCard = inflater.inflate(R.layout.mycard, null);
		return mCard;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		findViewById();
		init();
	}

	private void init() {
		cardShow = new MyCardShow(mContext);
		cardShow.setOnBack(this);
		rGroup.addView(cardShow.getView());
	}

	private void findViewById() {
		rGroup = (MyViewGroup) mCard.findViewById(R.id.infoGroup);

	}

	@Override
	public void onBackClick() {
		((MainActivity1) mContext).getSlidingMenu().toggle();
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
			Intent intent = new Intent();
			intent.setAction(Constants.MYCARD_PROMPT);
			intent.putExtra("prompt", false);
			BroadCast.sendBroadCast(mContext, intent);
		}
	}

}
