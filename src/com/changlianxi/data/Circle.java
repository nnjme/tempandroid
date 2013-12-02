package com.changlianxi.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.db.Const;
import com.changlianxi.util.StringUtils;

/**
 * Circle data
 * 
 * @author nnjme
 * 
 */
public class Circle extends AbstractData {
	private String id = "0";
	private String name = "";
	private String description = "";
	private String logo = "";
	private boolean isNew = false;

	// TODO creator, join_time
	// TODO members;
	// TODO growths
	// TODO chats
	// TODO news

	public Circle(String id, String name) {
		this(id, name, "");
	}

	public Circle(String id, String name, String description) {
		this(id, name, description, "");
	}

	public Circle(String id, String name, String description, String logo) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.logo = logo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLogo() {
		return logo;
	}

	public String getLogo(String size) {
		return StringUtils.JoinString(logo, size);
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	@Override
	public String toString() {
		return "Circle [id=" + id + ", name=" + name + ", isNew=" + isNew + "]";
	}

	@Override
	public void read(SQLiteDatabase db) {
		super.read(db);
		Cursor cursor = db.query(Const.CIRCLE_TABLE_NAME, new String[] { "cid",
				"name", "logo", "description", "is_new" }, "cid=?",
				new String[] { this.id }, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String id = cursor.getString(cursor.getColumnIndex("cid"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String logo = cursor.getString(cursor.getColumnIndex("logo"));
			String description = cursor.getString(cursor
					.getColumnIndex("description"));
			int isNew = cursor.getInt(cursor.getColumnIndex("is_new"));

			this.id = id;
			this.name = name;
			this.logo = logo;
			this.description = description;
			this.isNew = (isNew > 0);
		}
		cursor.close();
	}

	@Override
	public void write(SQLiteDatabase db) {
		super.write(db);
		String dbName = Const.CIRCLE_TABLE_NAME;
		if (this.status == Status.OLD) {
			return;
		}
		if (this.status == Status.DEL) {
			db.delete(dbName, "cid=?", new String[] { id });
			return;
		}

		ContentValues cv = new ContentValues();
		cv.put("cid", id);
		cv.put("name", name);
		cv.put("logo", logo);
		cv.put("description", description);
		cv.put("is_new", isNew ? 1 : 0);

		if (this.status == Status.NEW) {
			db.insert(dbName, null, cv);
		} else if (this.status == Status.UPDATE) {
			db.update(dbName, cv, "cid=?", new String[] { id });
		}
		this.status = Status.OLD;
	}

	@Override
	public void update(IData data) {
		if (!(data instanceof Circle)) {
			return;
		}
		Circle another = (Circle) data;
		boolean isChange = false;
		if (this.id != another.id) {
			this.id = another.id;
			isChange = true;
		}
		if (this.name != another.name) {
			this.name = another.name;
			isChange = true;
		}
		if (this.logo != another.logo) {
			this.logo = another.logo;
			isChange = true;
		}
		if (this.description != another.description) {
			this.description = another.description;
			isChange = true;
		}
		if (this.isNew != another.isNew) {
			this.isNew = another.isNew;
			isChange = true;
		}

		if (isChange && this.status == Status.OLD) {
			this.status = Status.UPDATE;
		}
	}

}
