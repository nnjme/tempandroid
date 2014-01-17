package com.changlianxi.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.db.Const;

/**
 * Comment in growth.
 * 
 * 
 * @author nnjme
 * 
 */
public class GrowthComment extends AbstractData {
	private int gid = 0;
	private int gcid = 0;
	private int uid = 0;
	private int replyid = 0;
	private String content = "";
	private String time = "";
	private int total = 0; // just use to record total comments while pushlish a
							// new comment

	public GrowthComment(int gid, int gcid, int uid, String content) {
		this.gid = gid;
		this.gcid = gcid;
		this.uid = uid;
		this.content = content;
	}

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public int getGcid() {
		return gcid;
	}

	public void setGcid(int gcid) {
		this.gcid = gcid;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getReplyid() {
		return replyid;
	}

	public void setReplyid(int replyid) {
		this.replyid = replyid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "GrowthComment [gid=" + gid + ", gcid=" + gcid + ", uid=" + uid
				+ ", replyid=" + replyid + ", content=" + content + ", time="
				+ time + "]";
	}

	@Override
	public void read(SQLiteDatabase db) {
		Cursor cursor = db.query(Const.GROWTH_COMMENT_TABLE_NAME, new String[] {
				"gid", "gcid", "uid", "replyid", "content", "time" }, "gcid=?",
				new String[] { this.gcid + "" }, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			int gid = cursor.getInt(cursor.getColumnIndex("gid"));
			int gcid = cursor.getInt(cursor.getColumnIndex("gcid"));
			int uid = cursor.getInt(cursor.getColumnIndex("uid"));
			int replyid = cursor.getInt(cursor.getColumnIndex("replyid"));
			String content = cursor.getString(cursor.getColumnIndex("content"));
			String time = cursor.getString(cursor.getColumnIndex("time"));

			this.gid = gid;
			this.gcid = gcid;
			this.uid = uid;
			this.replyid = replyid;
			this.content = content;
			this.time = time;
		}
		cursor.close();

		this.status = Status.OLD;
	}

	@Override
	public void write(SQLiteDatabase db) {
		String dbName = Const.GROWTH_COMMENT_TABLE_NAME;
		if (this.status == Status.OLD) {
			return;
		}
		if (this.status == Status.DEL) {
			db.delete(dbName, "gcid=?", new String[] { gcid + "" });
			return;
		}

		ContentValues cv = new ContentValues();
		cv.put("gid", gid);
		cv.put("gcid", gcid);
		cv.put("uid", uid);
		cv.put("replyid", replyid);
		cv.put("content", content);
		cv.put("time", time);

		if (this.status == Status.NEW) {
			db.insert(dbName, null, cv);
		} else if (this.status == Status.UPDATE) {
			db.update(dbName, cv, "gcid=?", new String[] { gcid + "" });
		}
		this.status = Status.OLD;
	}

	@Override
	public void update(IData data) {
		if (!(data instanceof GrowthComment)) {
			return;
		}
		GrowthComment another = (GrowthComment) data;
		boolean isChange = false;
		if (this.gid != another.gid) {
			this.gid = another.gid;
			isChange = true;
		}
		if (this.gcid != another.gcid) {
			this.gcid = another.gcid;
			isChange = true;
		}
		if (this.uid != another.uid) {
			this.uid = another.uid;
			isChange = true;
		}
		if (this.replyid != another.replyid) {
			this.replyid = another.replyid;
			isChange = true;
		}
		if (!this.content.equals(another.content)) {
			this.content = another.content;
			isChange = true;
		}
		if (!this.time.equals(another.time)) {
			this.time = another.time;
			isChange = true;
		}
		if (isChange && this.status == Status.OLD) {
			this.status = Status.UPDATE;
		}
	}

}
