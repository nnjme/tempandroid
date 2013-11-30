package com.changlianxi.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.db.Const;

public class CircleList extends AbstractData {
	// private long timestamp = 0L; // TODO
	private List<Circle> circles = null;

	public CircleList(List<Circle> circles) {
		super();
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
		super.read(db);

		if (this.circles == null) {
			this.circles = new ArrayList<Circle>();
		} else {
			this.circles.clear();
		}

		Cursor cursor = db.query(Const.CIRCLE_TABLE_NAME, new String[] {
				"cid", "name", "logo", "description", "is_new" },
				null, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String id = cursor.getString(cursor.getColumnIndex("cid"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String logo = cursor.getString(cursor.getColumnIndex("logo"));
				String description = cursor.getString(cursor
						.getColumnIndex("description"));
				int isNew = cursor.getInt(cursor.getColumnIndex("is_new"));

				Circle c = new Circle(id, name, description, logo);
				c.setNew(isNew > 0);
				c.setStatus(Status.OLD);
				this.circles.add(c);

				cursor.moveToNext();
			}
		}
		cursor.close();

		this.status = Status.OLD;
	}

	@Override
	public void write(SQLiteDatabase db) {
		if (this.status != Status.OLD) {
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
		Set<String> thisCids = new HashSet<String>();
		for (Circle c : this.circles) {
			thisCids.add(c.getId());
		}

		// check update circle
		for (Circle ac : another.circles) {
			String acId = ac.getId();
			if (thisCids.contains(acId)) {
				for (Circle c : this.circles) {
					if (c.getId() == acId) {
						c.update(ac);
					}
				}
			}
		}

		// check new circle
		for (Circle ac : another.circles) {
			String acId = ac.getId();
			if (!thisCids.contains(acId)) {
				this.circles.add(ac);
			}
		}

		// TODO time
		this.status = Status.UPDATE;
	}

}
