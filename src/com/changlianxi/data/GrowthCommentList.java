package com.changlianxi.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.GrowthCommentListParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.db.Const;
import com.changlianxi.util.DateUtils;

/**
 * Comment List of a growth
 * 
 * Usage:
 * 
 * get a growth's comment list
 *     // new GrowthCommentList gcl
 *     // or gcl = grow.getCommentList();
 *     gcl.read();
 *     gcl.getComments();
 * 
 * refresh a growth's comments list
 *     // new GrowthCommentList gcl
 *     gcl.read();
 *     ...
 *     gcl.refresh(); // get new comments
 *     
 *     ...
 *     gcl.write();
 *     
 *     
 * @author jieme
 *
 */
public class GrowthCommentList extends AbstractData {
	public final static String LIST_API = "growth/icomments";

	private int cid = 0;
	private int gid = 0;
	private long startTime = 0L; // data start time, in milliseconds
	private long endTime = 0L; // data end time
	private long lastReqTime = 0L; // last request time of comment data
	private int total = 0;
	private List<GrowthComment> comments = new ArrayList<GrowthComment>();

	public GrowthCommentList(int cid, int gid) {
		this.cid = cid;
		this.gid = gid;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public List<GrowthComment> getComments() {
		return comments;
	}

	public void setComments(List<GrowthComment> comments) {
		this.comments = comments;
	}
	
	public void addComment(GrowthComment comment) {
		this.comments.add(comment);
		this.status = Status.UPDATE;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getLastReqTime() {
		return lastReqTime;
	}

	public void setLastReqTime(long lastReqTime) {
		this.lastReqTime = lastReqTime;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	@Override
	public void read(SQLiteDatabase db) { // TODO how to sort?
		if (comments == null) {
			comments = new ArrayList<GrowthComment>();
		} else {
			comments.clear();
		}

		// read comments
		Cursor cursor = db.query(Const.GROWTH_COMMENT_TABLE_NAME, new String[] {
				"gid", "gcid", "uid", "replyid", "content", "time" }, "gid=?",
				new String[] { this.gid + "" }, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				int gid = cursor.getInt(cursor.getColumnIndex("gid"));
				int gcid = cursor.getInt(cursor.getColumnIndex("gcid"));
				int uid = cursor.getInt(cursor.getColumnIndex("uid"));
				int replyid = cursor.getInt(cursor.getColumnIndex("replyid"));
				String content = cursor.getString(cursor
						.getColumnIndex("content"));
				String time = cursor.getString(cursor.getColumnIndex("time"));

				GrowthComment comment = new GrowthComment(gid, gcid, uid,
						content);
				comment.setReplyid(replyid);
				comment.setTime(time);
				comments.add(comment);

				long joinTime = DateUtils.convertToDate(time);
				if (startTime == 0 || joinTime < startTime) {
					startTime = joinTime;
				}
				if (endTime == 0 || joinTime > endTime) {
					endTime = joinTime;
				}

				cursor.moveToNext();
			}
		}
		cursor.close();

		// read last request times
		Cursor cursor2 = db.query(Const.GROWTH_TABLE_NAME, new String[] {
				"cid", "lastCommentsReqTime" }, "id=?", new String[] { this.gid
				+ "" }, null, null, null);
		if (cursor2.getCount() > 0) {
			cursor2.moveToFirst();
			long lastCommentsReqTime = cursor2.getLong(cursor
					.getColumnIndex("lastCommentsReqTime"));
			this.lastReqTime = lastCommentsReqTime;
		}
		cursor.close();

		this.status = Status.OLD;
	}

	@Override
	public void write(SQLiteDatabase db) {
		if (this.status != Status.OLD) {
			// write one by one
			for (GrowthComment comment : comments) {
				comment.write(db);
			}

			// write last request time
			ContentValues cv = new ContentValues();
			cv.put("lastCommentsReqTime", lastReqTime);
			db.update(Const.GROWTH_TABLE_NAME, cv, "id=?", new String[] { gid
					+ "" });

			this.status = Status.OLD;
		}
	}

	@SuppressLint("UseSparseArrays")
	@Override
	public void update(IData data) {
		if (!(data instanceof GrowthCommentList)) {
			return;
		}

		GrowthCommentList another = (GrowthCommentList) data;
		if (another.comments.size() == 0) {
			return;
		}

		boolean isNewer = true; // another list is newer than current list
		if (another.endTime <= this.startTime) {
			isNewer = false;
		}

		// old ones
		Map<Integer, GrowthComment> olds = new HashMap<Integer, GrowthComment>();
		for (GrowthComment comment : this.comments) {
			olds.put(comment.getGcid(), comment);
		}

		// join new ones
		boolean canJoin = false;
		Map<Integer, GrowthComment> news = new HashMap<Integer, GrowthComment>();
		for (GrowthComment comment : another.comments) {
			int gid = comment.getGcid();
			news.put(gid, comment);

			if (olds.containsKey(gid)) {
				olds.get(gid).update(comment);
				canJoin = true;
			} else {
				this.comments.add(comment);
			}
		}

		if (isNewer) {
			if (another.total == another.getComments().size()) {
				canJoin = true;
			}

			if (!canJoin) {
				for (int gid : olds.keySet()) {
					if (news.containsKey(gid)) {
						olds.get(gid).setStatus(Status.DEL);
					}
				}
			}
		}

		this.status = Status.UPDATE;
	}

	/**
	 * refresh new growth comments list from server for the first time
	 */
	public void refresh() {
		refresh(0);
	}

	/**
	 * refresh new growth comments list from server, with start time
	 * 
	 * @param startTime
	 */
	public void refresh(long startTime) {
		refresh(startTime, 0);
	}

	/**
	 * refresh new growth comments list from server, with start and end time
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public RetError refresh(long startTime, long endTime) {
		IParser parser = new GrowthCommentListParser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cid", cid);
		params.put("gid", gid);
		if (startTime > 0) {
			params.put("start", startTime);
		}
		if (endTime > 0) {
			params.put("end", endTime);
		}

		Result ret = ApiRequest.requestWithToken(GrowthCommentList.LIST_API,
				params, parser);
		if (ret.getStatus() == RetStatus.SUCC) {
			update((GrowthCommentList) ret.getData());
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}

}
