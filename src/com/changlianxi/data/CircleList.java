package com.changlianxi.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.parser.CircleListParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.data.request.RetError;
import com.changlianxi.data.request.RetStatus;
import com.changlianxi.db.Const;

/**
 * Circle List of a user
 * 
 * Usage:
 * 
 * get a user's circle list
 *     // new CircleList cl
 *     cl.read();
 *     cl.getCircles();
 * 
 * refresh a user's circle list
 *     // new CircleList cl
 *     cl.read();
 *     ...
 *     cl.refresh(); // get new and mod and del circles
 *     
 *     ...
 *     cl.write();
 *     
 *     
 * @author jieme
 *
 */
public class CircleList extends AbstractData {
	public final static String LIST_API = "circles/ilist";
	private List<Circle> circles = null;

	public CircleList(List<Circle> circles) {
		this.circles = circles;
	}

	public List<Circle> getCircles() {
		return circles;
	}

	public void setCircles(List<Circle> circles) {
		this.circles = circles;
	}

	@Override
	public void read(SQLiteDatabase db) {
		if (this.circles == null) {
			this.circles = new ArrayList<Circle>();
		} else {
			this.circles.clear();
		}

		// read ids
		Cursor cursor = db.query(Const.CIRCLE_TABLE_NAME,
				new String[] { "id" }, null, null, null, null, null);
		List<Integer> cids = new ArrayList<Integer>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				cids.add(id);
				cursor.moveToNext();
			}
		}
		cursor.close();

		// read one by one
		for (int cid : cids) {
			Circle c = new Circle(cid);
			c.read(db);
			this.circles.add(c);
		}

		this.status = Status.OLD;
	}

	@Override
	public void write(SQLiteDatabase db) {
		if (this.status != Status.OLD) {
			// write one by one
			for (Circle c : this.circles) {
				c.write(db);
			}

			this.status = Status.OLD;
		}
	}

	@Override
	public void update(IData data) {
		if (!(data instanceof CircleList)) {
			return;
		}

		CircleList another = (CircleList) data;
		if (another.circles.size() == 0) {
			return;
		}

		// old cids
		Set<Integer> oldCids = new HashSet<Integer>();
		for (Circle c : this.circles) {
			oldCids.add(c.getId());
		}

		// update/del circles
		for (Circle ac : another.circles) {
			int acId = ac.getId();
			if (oldCids.contains(acId)) {
				for (Circle c : this.circles) {
					if (c.getId() == acId) {
						if (ac.getStatus() != Status.DEL) {
							c.updateForListChange(ac);
						} else {
							c.setStatus(Status.DEL);
						}
					}
				}
			}
		}

		// new circles
		for (Circle ac : another.circles) {
			int acId = ac.getId();
			if (!oldCids.contains(acId)) {
				this.circles.add(ac);
			}
		}

		this.status = Status.UPDATE;
	}

	/**
	 * refresh new circles list from server
	 */
	public RetError refresh() {
		return refresh(0);
	}

	/**
	 * refresh new circles list from server with start time
	 */
	public RetError refresh(long startTime) {
		return refresh(startTime, 0);
	}

	/**
	 * refresh new circles list from server with start and end time
	 */
	public RetError refresh(long startTime, long endTime) {
		IParser parser = new CircleListParser();
		Map<String, Object> params = new HashMap<String, Object>();
		if (startTime > 0) {
			params.put("start", startTime);
		}
		if (endTime > 0) {
			params.put("end", endTime);
		}
		Result ret = ApiRequest.requestWithToken(CircleList.LIST_API, params,
				parser);

		if (ret.getStatus() == RetStatus.SUCC) {
			this.update(ret.getData());
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}

}
