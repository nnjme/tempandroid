package com.changlianxi.data;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.ArrayParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.parser.MyCardDetailParser;
import com.changlianxi.data.parser.StringParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.ArrayResult;
import com.changlianxi.data.request.Result;
import com.changlianxi.data.request.StringResult;

/**
 * My card, whose cid=0,
 * 
 * Usage:
 * 
 * get my card detail info:
 *     // new card
 *     card.read();
 *     card.readDetails();
 *     card.getDetails();
 * 
 * refresh my card's detail info:
 *     // new card
 *     card.read()
 *     card.refresh(); // request and merge with local data
 *     card.writeDetails();
 *     card.write();
 * 
 * upload after edit:
 *    // new card1
 *    // ...edit...
 *    // new card2 after edit
 *    card1.uploadAfterEdit(card2);
 *    card1.write();    
 * 
 * upload avatar
 *    // new card
 *    // ...edit avatar...
 *    // card avatar
 *    card.uploadAvatar(newAvatar);
 *    card.write();
 * 
 * @author jieme
 * 
 */
public class MyCard extends CircleMember {
	public final static String DETAIL_API = "people/imyDetail";
	public final static String EDIT_API = "people/imyEdit";
	public final static String UPLOAD_AVATAR_API = "people/iuploadMyAvatar";

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

	/**
	 * refresh mycard's detail info
	 * 
	 * @return
	 */
	@Override
	public RetError refresh() {
		IParser parser = new MyCardDetailParser();
		Map<String, Object> params = new HashMap<String, Object>();
		Result ret = ApiRequest.requestWithToken(MyCard.DETAIL_API,
				params, parser);

		if (ret.getStatus() == RetStatus.SUCC) {
			if (this.updateDetails((MyCard)ret.getData())) {
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
		params.put("pid", another.getPid());
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
		params.put("pid", getPid());
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

}
