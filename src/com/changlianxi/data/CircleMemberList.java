package com.changlianxi.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.CircleMemberListParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.db.Const;
import com.changlianxi.util.DateUtils;

/**
 * List of members in a circle
 * 
 * Usage:
 * 
 * get a circle's member list
 *     // new CircleMemberList cml
 *     cml.read();
 *     cml.getMembers();
 * 
 * refresh a circle's member
 *     // new CircleMemberList cml
 *     ...
 *     cml.refreshNewMembers(); // new members
 *     // cml.update()
 *     // cml.write()
 *     ...
 *     cml.refreshModMembers(); // mod members
 *     // cml.update()
 *     // cml.write()
 *     ...
 *     cml.refreshDelMembers(); // del members
 *     // cml.update()
 *     // cml.write()
 *     ...
 *     cml.refreshAllNewMembers(); // all new members
 *     ...
 *     cml.refreshAllModMembers(); // all mod members
 *     ...
 *     cml.refreshAllDelMembers(); // all del members
 *     ...
 *     cml.refresh(); // all new and mod and del members
 *     
 *     ...
 *     cml.write();
 *      
 * 
 * @author jieme
 *
 */
@SuppressLint("UseSparseArrays")
public class CircleMemberList extends AbstractData {
	public final static String LIST_API = "/circles/imembers";
	private int cid = 0;
	private long startTime = 0L; // data start time, in milliseconds
	private long endTime = 0L; // data end time
	private long lastNewReqTime = 0L; // last request time of new data
	private long lastModReqTime = 0L; // last request time of mod data
	private long lastDelReqTime = 0L; // last request time of del data
	private int total = 0;
	private List<CircleMember> members = new ArrayList<CircleMember>();
	private Type type;
	
	enum Type{
		NEW,MOD,DEL
	}

	public CircleMemberList(int cid) {
		this.cid = cid;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
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

	public long getLastNewReqTime() {
		return lastNewReqTime;
	}

	public void setLastNewReqTime(long lastNewReqTime) {
		this.lastNewReqTime = lastNewReqTime;
	}

	public long getLastModReqTime() {
		return lastModReqTime;
	}

	public void setLastModReqTime(long lastModReqTime) {
		this.lastModReqTime = lastModReqTime;
	}

	public long getLastDelReqTime() {
		return lastDelReqTime;
	}

	public void setLastDelReqTime(long lastDelReqTime) {
		this.lastDelReqTime = lastDelReqTime;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<CircleMember> getMembers() {
		return members;
	}

	public void setMembers(List<CircleMember> members) {
		if (members != null) {
			this.members = members;
		}
	}

	@Override
	public void read(SQLiteDatabase db) {
		if (members == null) {
			members = new ArrayList<CircleMember>();
		} else {
			members.clear();
		}

		// read ids
		Cursor cursor = db.query(Const.CIRCLE_MEMBER_TABLE_NAME, new String[] {
				"pid" }, "cid=?", new String[] { this.cid + "" }, null,
				null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				int pid = cursor.getInt(cursor.getColumnIndex("pid"));
				CircleMember member = new CircleMember(cid, pid);
				members.add(member);

				cursor.moveToNext();
			}
		}
		cursor.close();

		// read one by one
		for (CircleMember m : members) {
			m.read(db);
			long joinTime = DateUtils.convertToDate(m.getJoinTime());
			if (startTime == 0 || joinTime < startTime) {
				startTime = joinTime;
			}
			if (endTime == 0 || joinTime > endTime) {
				endTime = joinTime;
			}
		}

		// read last request times
		Cursor cursor2 = db.query(Const.TIME_RECORD_TABLE_NAME, new String[] {
				"subkey", "time" }, "key=?", new String[] { "c" + this.cid },
				null, null, null);
		if (cursor2.getCount() > 0) {
			cursor2.moveToFirst();
			for (int i = 0; i < cursor2.getCount(); i++) {
				String subkey = cursor2.getString(cursor
						.getColumnIndex("subkey"));
				long time = cursor2.getLong(cursor.getColumnIndex("time"));
				if ("last_new_req_time".equals(subkey)) {
					this.lastNewReqTime = time;
				} else if ("last_mod_req_time".equals(subkey)) {
					this.lastModReqTime = time;
				} else if ("last_del_req_time".equals(subkey)) {
					this.lastDelReqTime = time;
				}

				cursor2.moveToNext();
			}
		}
		cursor2.close();

		this.status = Status.OLD;
	}

	@Override
	public void write(SQLiteDatabase db) {
		if (this.status != Status.OLD) {
			// write one by one
			for (CircleMember m : members) {
				m.write(db);
			}

			// write last request time
			ContentValues cv = new ContentValues();
			String subkey = "";
			if (type == Type.NEW) {
				subkey = "last_new_req_time";
				cv.put("time", lastNewReqTime);
			} else if (type == Type.MOD) {
				subkey = "last_mod_req_time";
				cv.put("time", lastModReqTime);
			} else if (type == Type.DEL) {
				subkey = "last_del_req_time";
				cv.put("time", lastDelReqTime);
			}
			cv.put("subkey", subkey);
//			cv.put("last_new_req_time", lastNewReqTime);
//			cv.put("last_mod_req_time", lastModReqTime);
//			cv.put("last_del_req_time", lastDelReqTime);
			db.update(Const.TIME_RECORD_TABLE_NAME, cv, "key=?",
					new String[] { "c" + this.cid });

			this.status = Status.OLD;
		}
	}

	@Override
	public void update(IData data) {
		if (!(data instanceof CircleMemberList)) {
			return;
		}

		CircleMemberList another = (CircleMemberList) data;
		if (another.members.size() == 0) {
			return;
		}

		Map<Integer, CircleMember> olds = new HashMap<Integer, CircleMember>();
		for (CircleMember m : this.members) {
			olds.put(m.getPid(), m);
		}

		for (CircleMember am : another.members) {
			if (olds.containsKey(am.getPid())) {
				if (am.getStatus() == Status.UPDATE
						&& olds.get(am.getPid()).getStatus() != Status.DEL) {
					// update
					olds.get(am.getPid()).updateListSummary(am);
					type = Type.MOD;
				} else if (am.getStatus() == Status.DEL) {
					// del
					olds.get(am.getPid()).setStatus(Status.DEL);
					type = Type.DEL;
					this.total--;
				}
			} else {
				// new
				if (am.getStatus() == Status.NEW) {
					this.members.add(am);
					type = Type.NEW;
					this.total++;
				}
			}
		}
		
		// update request time, start and end time
		if (type == Type.NEW) {
			this.setLastNewReqTime(another.getLastNewReqTime());
			this.startTime = Math.min(this.startTime, another.getStartTime());
			this.endTime = Math.max(this.endTime, another.getEndTime());
		} else if (type == Type.MOD) {
			this.setLastModReqTime(another.getLastModReqTime());
		} else if (type == Type.DEL) {
			this.setLastDelReqTime(another.getLastDelReqTime());
		}

		this.status = Status.UPDATE;
		
//		Collections.sort(members, new Comparator<CircleMember>() {
//
//			@Override
//			public int compare(CircleMember lhs, CircleMember rhs) {
//				// TODO Auto-generated method stub
//				return lhs.getSort_key().compareTo(rhs.getSort_key());
//			}
//		});
	}

	/**
	 * refresh circle members list from server, for the first time
	 */
	public void refresh() {
		refresh(0);
	}

	/**
	 * refresh for synchronize local data with server data
	 * 
	 * @param startTime
	 */
	public void refresh(long startTime) {
		refreshAllNewMembers(startTime, 0L);
		refreshAllModMembers(startTime, 0L);
		refreshAllDelMembers(startTime, 0L);
	}

	/**
	 * refresh all new circle members list from server, it will do repeat
	 * refresh request until all data is fetched.
	 * 
	 * @param startTime
	 * @param endTime
	 */
	public void refreshAllNewMembers(long startTime, long endTime) {
		while (true) {
			CircleMemberList cml = refreshNewMembers(startTime, endTime);
			if (cml == null) {
				break;
			}
			// update for data merge
			update(cml);
			if (cml.getTotal() <= cml.getMembers().size()) {
				break;
			}
			startTime = cml.getEndTime() + 1;
		}
	}

	/**
	 * refresh new circle members list from server, just request one time
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public CircleMemberList refreshNewMembers(long startTime, long endTime) {
		Result ret = requestMembers(startTime, endTime, "new");
		if (ret != null && ret.getStatus() == RetStatus.SUCC) {
			return (CircleMemberList) ret.getData();
		} else {
			return null;
		}
	}

	/**
	 * refresh all modified circle members list from server, it will do repeat
	 * refresh request until all data is fetched.
	 * 
	 * @param startTime
	 * @param endTime
	 */
	public void refreshAllModMembers(long startTime, long endTime) {
		while (true) {
			CircleMemberList cml = refreshModMembers(startTime, endTime);
			if (cml == null) {
				break;
			}
			// update for data merge
			update(cml);

			if (cml.getTotal() <= cml.getMembers().size()) {
				break;
			}

			startTime = cml.getEndTime() + 1;
		}
	}

	/**
	 * refresh modified circle members list from server, just request one time
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public CircleMemberList refreshModMembers(long startTime, long endTime) {
		Result ret = requestMembers(startTime, endTime, "mod");
		if (ret != null && ret.getStatus() == RetStatus.SUCC) {
			return (CircleMemberList) ret.getData();
		} else {
			return null;
		}
	}

	/**
	 * refresh all deleted circle members list from server, it will do repeat
	 * refresh request until all data is fetched.
	 * 
	 * @param startTime
	 * @param endTime
	 */
	public void refreshAllDelMembers(long startTime, long endTime) {
		while (true) {
			CircleMemberList cml = refreshDelMembers(startTime, endTime);
			if (cml == null) {
				break;
			}
			// update for data merge
			update(cml);
			if (cml.getTotal() <= cml.getMembers().size()) {
				break;
			}

			startTime = cml.getEndTime() + 1;
		}
	}

	/**
	 * refresh deleted circle members list from server, just request one time
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public CircleMemberList refreshDelMembers(long startTime, long endTime) {
		Result ret = requestMembers(startTime, endTime, "del");
		if (ret != null && ret.getStatus() == RetStatus.SUCC) {
			return (CircleMemberList) ret.getData();
		} else {
			return null;
		}
	}

	private Result requestMembers(long startTime, long endTime, String type) {
		IParser parser = new CircleMemberListParser();
		Map<String, Object> params = new HashMap<String, Object>();
		if ("new".equals(type)) {
			params.put("type", type);
		} else if ("mod".equals(type)) {
			params.put("type", type);
			if (startTime <= 0) {
				return null;
			}
		} else if ("del".equals(type)) {
			params.put("type", type);
			if (startTime <= 0) {
				return null;
			}
		}
		if (startTime > 0) {
			params.put("start", startTime);
		}
		if (endTime > 0) {
			params.put("end", endTime);
		}
		params.put("cid", cid);
		Result ret = ApiRequest.requestWithToken(CircleMemberList.LIST_API,
				params, parser);
		return ret;
	}

}
