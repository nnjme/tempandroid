package com.changlianxi.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.AcceptAmendmentParser;
import com.changlianxi.data.parser.AmendmentsParser;
import com.changlianxi.data.parser.ArrayParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.parser.MyCardDetailParser;
import com.changlianxi.data.parser.SimpleParser;
import com.changlianxi.data.parser.StringParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.ArrayResult;
import com.changlianxi.data.request.MapResult;
import com.changlianxi.data.request.Result;
import com.changlianxi.data.request.SimpleResult;
import com.changlianxi.data.request.StringResult;
import com.changlianxi.db.Const;

/**
 * My card, whose cid=0,
 * 
 * Usage:
 * 
 * get my card detail info:
 *   // new card
 *   card.read();
 *   card.readDetails();
 *   card.getDetails();
 * 
 * refresh my card's detail info:
 *   // new card
 *   card.read()
 *   card.refresh(); // request and merge with local data 
 *   card.writeDetails(); 
 *   card.write();
 * 
 * upload after edit: 
 *   // new card1 
 *   // ...edit... 
 *   // new card2 after edit
 *   card1.uploadAfterEdit(card2); 
 *   card1.write();
 * 
 * upload avatar: 
 *   // new card 
 *   // ...edit avatar... 
 *   // card avatar
 *   card.uploadAvatar(newAvatar);
 *   card.write();
 * 
 * amendments:
 *   // new card 
 *   card.refreshAmendments(); // get all amendments
 *   card.acceptAmendment();
 *   card.refuseAmendment();
 *   
 * 
 * @author jieme
 * 
 */
public class MyCard extends CircleMember {
	public final static String DETAIL_API = "people/imyDetail";
	public final static String EDIT_API = "people/imyEdit";
	public final static String UPLOAD_AVATAR_API = "people/iuploadMyAvatar";
	public final static String GET_AMENDMENT_API = "people/imyAmendments";
	public final static String ACCEPT_AMENDMENT_API = "people/iacceptAmendment";
	public final static String REFUSE_AMENDMENT_API = "people/irefuseAmendment";

	private boolean isChanged = false;
	private List<Amendment> amendments = new ArrayList<Amendment>();

	public MyCard() {
		super(0);
	}

	public MyCard(int pid) {
		super(0, pid);
	}

	public MyCard(int pid, int uid) {
		super(0, pid, uid);
	}

	public MyCard(int pid, int uid, String name) {
		super(0, pid, uid, name);
	}

	@Override
	public void setCid(int cid) {
		super.setCid(0);
	}

	public boolean isChanged() {
		return isChanged || this.amendments.size() > 0;
	}

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	public List<Amendment> getAmendments() {
		return amendments;
	}

	public void setAmendments(List<Amendment> amendments) {
		this.amendments = amendments;
	}

	public void readAmendments(SQLiteDatabase db) {
		if (this.amendments == null) {
			this.amendments = new ArrayList<Amendment>();
		} else {
			this.amendments.clear();
		}

		Cursor cursor = db.query(Const.AMENDMENT_TABLE_NAME, new String[] {
				"amid", "cid", "uid", "content", "time" }, null, null, null,
				null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				int amid = cursor.getInt(cursor.getColumnIndex("amid"));
				int cid = cursor.getInt(cursor.getColumnIndex("cid"));
				int uid = cursor.getInt(cursor.getColumnIndex("uid"));
				String content = cursor.getString(cursor
						.getColumnIndex("content"));
				String time = cursor.getString(cursor.getColumnIndex("time"));

				Amendment amd = new Amendment(amid, cid, uid);
				amd.setContent(content);
				amd.setTime(time);
				amd.setStatus(Status.OLD);
				this.amendments.add(amd);

				cursor.moveToNext();
			}
		}
		cursor.close();
	}

	public void writeAmendments(SQLiteDatabase db) {
		for (Amendment amd : amendments) {
			amd.write(db);
		}
	}

	@SuppressLint("UseSparseArrays")
	protected boolean updateAmendments(List<Amendment> others) {
		if (others.size() == 0) {
			return false;
		}

		boolean isChange = false;
		if (this.amendments.size() == 0) {
			this.setAmendments(others);
			return true;
		}

		Map<Integer, Amendment> olds = new HashMap<Integer, Amendment>();
		Map<Integer, Amendment> news = new HashMap<Integer, Amendment>();
		for (Amendment amd : this.amendments) {
			olds.put(amd.getAmid(), amd);
		}
		for (Amendment amd : others) {
			int amid = amd.getAmid();
			news.put(amid, amd);
			if (olds.containsKey(amid)) {
				// update
				olds.get(amid).update(amd);
				if (olds.get(amid).getStatus() == Status.UPDATE) {
					isChange = true;
				}
			} else {
				// new
				this.amendments.add(amd);
				isChange = true;
			}
		}
		for (Amendment amd : this.amendments) {
			if (!news.containsKey(amd.getAmid())) {
				// del
				amd.setStatus(Status.DEL);
				isChange = true;
			}
		}

		return isChange;
	}

	/**
	 * refresh mycard's detail info
	 * 
	 * @return
	 */
	@Override
	public RetError refresh() {
		IParser parser = new MyCardDetailParser();
		Map<String, Object> params = new HashMap<String, Object>();
		Result ret = ApiRequest.requestWithToken(MyCard.DETAIL_API, params,
				parser);

		if (ret.getStatus() == RetStatus.SUCC) {
			if (this.updateDetails((MyCard) ret.getData())) {
				this.syncBasicAndDetail(false);
				this.status = Status.UPDATE;
			}
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}

	/**
	 * upload edit info to server, and update local data while upload success
	 * 
	 * @param another
	 * @return
	 */
	@Override
	public RetError uploadAfterEdit(CircleMember another) {
		JSONArray changedDetails = getChangedDetails(another);
		if (changedDetails.length() == 0) {
			return RetError.NONE;
		}

		IParser parser = new ArrayParser("details");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("person", changedDetails.toString());

		ArrayResult ret = (ArrayResult) ApiRequest.requestWithToken(
				MyCard.EDIT_API, params, parser);
		if (ret.getStatus() == RetStatus.SUCC) {
			updateForEditInfo(another, changedDetails, ret.getArrs());
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}

	/**
	 * upload new avatar to server, and update local avatar while upload success
	 * 
	 * @param avatar
	 * @return
	 */
	@Override
	public RetError uploadAvatar(String avatar) {
		if (!isAvatarChanged(avatar)) {
			return RetError.NONE;
		}

		IParser parser = new StringParser("avatar");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("avatar", avatar);
		StringResult ret = (StringResult) ApiRequest.requestWithToken(
				MyCard.UPLOAD_AVATAR_API, params, parser);

		if (ret.getStatus() == RetStatus.SUCC) {
			this.setAvatar(ret.getStr());
			this.setStatus(Status.UPDATE); // TODO change local?
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}

	/**
	 * get my amendments from server
	 * 
	 * @return
	 */
	public RetError refreshAmendments() {
		IParser parser = new AmendmentsParser();
		Map<String, Object> params = new HashMap<String, Object>();
		Result ret = ApiRequest.requestWithToken(MyCard.GET_AMENDMENT_API,
				params, parser);

		if (ret.getStatus() == RetStatus.SUCC) {
			MyCard mc = (MyCard) ret.getData();
			this.updateAmendments(mc.getAmendments());
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}

	/**
	 * accept an amendment
	 * 
	 * @param amd
	 * @param oldPd
	 * @param newPd
	 * @return
	 */
	public RetError acceptAmendment(Amendment amd, PersonDetail oldPd,
			PersonDetail newPd) {
		IParser parser = new AcceptAmendmentParser();
		Map<String, Object> params = new HashMap<String, Object>();
		MapResult ret = (MapResult) ApiRequest.requestWithToken(
				MyCard.ACCEPT_AMENDMENT_API, params, parser);

		if (ret.getStatus() == RetStatus.SUCC) {
			int replacedPdId = (Integer) (ret.getMaps().get("replaced"));
			PersonDetail detail = (PersonDetail) ret.getMaps().get("detail");
			if (replacedPdId == 0) {
				// new
				newPd.update(detail);
			} else {
				oldPd.setStatus(Status.DEL);
				if (detail.getId() == 0) {
					// del
				} else {
					// update
					newPd.update(detail);
				}
			}
			amd.setStatus(Status.DEL);
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}

	/**
	 * refuse an amendment
	 * 
	 * @param amd
	 * @return
	 */
	public RetError refuseAmendment(Amendment amd) {
		IParser parser = new SimpleParser();
		Map<String, Object> params = new HashMap<String, Object>();
		SimpleResult ret = (SimpleResult) ApiRequest.requestWithToken(
				MyCard.REFUSE_AMENDMENT_API, params, parser);

		if (ret.getStatus() == RetStatus.SUCC) {
			amd.setStatus(Status.DEL);
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}

}
