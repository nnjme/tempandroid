package com.changlianxi.data;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.db.Const;

/**
 * Circle Chat data
 * 
 * @author nnjme
 * 
 */
public class Chat extends AbstractData {
	public static enum Type {
		TEXT, IAMGE, AUDIO, VIDEO, POSITION
	}

	public static Map<String, Type> str2Type = new HashMap<String, Type>();
	static {
		for (Type type : Type.values()) {
			str2Type.put(type.name(), type);
		}
	}

	private String id = "0";
	private String circleId = "0";
	private Type type = Type.TEXT;
	private String senderUid = "0";
	private String content = "";
	private long time = 0L;

	public Chat(String id) {
		this.id = id;
	}

	public Chat(String id, String circleId, Type type, String senderUid,
			String content, long time) {
		super();
		this.id = id;
		this.circleId = circleId;
		this.type = type;
		this.senderUid = senderUid;
		this.content = content;
		this.time = time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCircleId() {
		return circleId;
	}

	public void setCircleId(String circleId) {
		this.circleId = circleId;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getSenderUid() {
		return senderUid;
	}

	public void setSenderUid(String senderUid) {
		this.senderUid = senderUid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public void read(SQLiteDatabase db) {
		super.read(db);

		Cursor cursor = db.query(Const.CHAT_TABLE_NAME, new String[] {
				"chatId", "circleId", "senderUid", "type", "content", "time" },
				"chatId=?", new String[] { this.id }, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String id = cursor.getString(cursor.getColumnIndex("chatId"));
			String cid = cursor.getString(cursor.getColumnIndex("circleId"));
			String sender = cursor
					.getString(cursor.getColumnIndex("senderUid"));
			String type = cursor.getString(cursor.getColumnIndex("type"));
			String content = cursor.getString(cursor.getColumnIndex("content"));
			long time = cursor.getLong(cursor.getColumnIndex("time"));

			this.id = id;
			this.circleId = cid;
			this.senderUid = sender;
			this.type = Chat.str2Type.get(type);
			this.content = content;
			this.time = time;
		}
		cursor.close();
	}

	@Override
	public void write(SQLiteDatabase db) {
		super.write(db);

		String dbName = Const.CHAT_TABLE_NAME;
		if (this.status == Status.OLD) {
			return;
		}
		if (this.status == Status.DEL) {
			db.delete(dbName, "chatId=?", new String[] { id });
			return;
		}

		ContentValues cv = new ContentValues();
		cv.put("chatId", id);
		cv.put("circleId", circleId);
		cv.put("senderUid", senderUid);
		cv.put("type", type.name());
		cv.put("content", content);
		cv.put("time", time);

		if (this.status == Status.NEW) {
			db.insert(dbName, null, cv);
		} else if (this.status == Status.UPDATE) {
			db.update(dbName, cv, "chatId=?", new String[] { id });
		}
		this.status = Status.OLD;
	}

	@Override
	public void update(IData data) {
		if (!(data instanceof Chat)) {
			return;
		}
		Chat another = (Chat) data;
		boolean isChange = false;
		if (this.id.equals(another.id)) {
			this.id = another.id;
			isChange = true;
		}
		if (this.circleId.equals(another.circleId)) {
			this.circleId = another.circleId;
			isChange = true;
		}
		if (this.type != another.type) {
			this.type = another.type;
			isChange = true;
		}
		if (this.senderUid.equals(another.senderUid)) {
			this.senderUid = another.senderUid;
			isChange = true;
		}
		if (this.content.equals(another.content)) {
			this.content = another.content;
			isChange = true;
		}
		if (this.time == another.time) {
			this.time = another.time;
			isChange = true;
		}

		if (isChange && this.status == Status.OLD) {
			this.status = Status.UPDATE;
		}
	}

}
