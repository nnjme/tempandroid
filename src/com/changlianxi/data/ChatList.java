package com.changlianxi.data;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.db.Const;

public class ChatList extends AbstractData {
	private long startTime = 0L; // in milliseconds
	private long endTime = 0L; // in milliseconds
	private long requestTime = 0L; // in milliseconds // TODO need it ?
	private String circleId = "0";
	private List<Chat> chats = null;
	private List<Chat> toBeDel = null;

	public ChatList(String circleId) {
		this.circleId = circleId;
	}

	public ChatList(String circleId, long startTime, long endTime,
			long requestTime, List<Chat> chats) {
		super();
		this.circleId = circleId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.requestTime = requestTime;
		this.chats = chats;
	}

	public String getCircleId() {
		return circleId;
	}

	public void setCircleId(String circleId) {
		this.circleId = circleId;
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

	public long getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(long requestTime) {
		this.requestTime = requestTime;
	}

	public List<Chat> getChats() {
		return chats;
	}

	public void setChats(List<Chat> chats) {
		this.chats = chats;
	}

	@Override
	public void read(SQLiteDatabase db) {
		super.read(db);
		if (this.chats == null) {
			this.chats = new ArrayList<Chat>();
		} else {
			this.chats.clear();
		}

		Cursor cursor = db.query(Const.CHAT_TABLE_NAME, new String[] {
				"chatId", "circleId", "senderUid", "type", "content", "time" },
				"circleId=?", new String[] { this.circleId }, null, null,
				"time asc");
		if (cursor.getCount() > 0) {
			long start = 0, end = 0;
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String id = cursor.getString(cursor.getColumnIndex("chatId"));
				String cid = cursor
						.getString(cursor.getColumnIndex("circleId"));
				String sender = cursor.getString(cursor
						.getColumnIndex("senderUid"));
				String type = cursor.getString(cursor.getColumnIndex("type"));
				String content = cursor.getString(cursor
						.getColumnIndex("content"));
				long time = cursor.getLong(cursor.getColumnIndex("time"));

				Chat c = new Chat(id, cid, Chat.str2Type.get(type), sender,
						content, time);
				c.setStatus(Status.OLD);
				this.chats.add(c);

				if (start == 0 || time < start) {
					start = time;
				}
				if (end == 0 || time > end) {
					end = time;
				}

				cursor.moveToNext();
			}
			this.startTime = start;
			this.endTime = end;
		}
		cursor.close();
	}

	@Override
	public void write(SQLiteDatabase db) {
		if (this.toBeDel != null) {
			for (Chat c : this.toBeDel) {
				c.setStatus(Status.OLD);
				c.write(db);
			}
			this.toBeDel = null;
		}
		for (Chat c : this.chats) {
			c.write(db);
		}
	}

	@Override
	public void update(IData data) {
		if (!(data instanceof ChatList)) {
			return;
		}

		ChatList another = (ChatList) data;
		if (another.chats.size() == 0 || this.circleId != another.circleId) {
			return;
		}

		if (another.startTime >= this.endTime) {
			// another is newer than this, replace this
			this.startTime = another.startTime;
			this.endTime = another.endTime;
			this.requestTime = another.requestTime;
			this.toBeDel = this.chats;
			this.chats = another.chats;
			return;
		}

		if (another.endTime <= this.startTime) {
			// another is older than this, append to the end
			// TODO some duplicate chats??
			this.startTime = another.startTime;
			this.chats.addAll(another.chats);
			return;
		}
	}

	public void insertNew(Chat chat) {
		if (chat.getTime() >= this.endTime) {
			this.endTime = chat.getTime();
			this.chats.add(0, chat);
		}
	}

}
