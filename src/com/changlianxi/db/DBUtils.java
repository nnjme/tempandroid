package com.changlianxi.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.modle.CircleModle;
import com.changlianxi.modle.Info;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.modle.MemberModle;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.MyComparator;
import com.changlianxi.util.SharedUtils;

/**
 * 数据库操作类
 * 
 * @author teeker_bin
 * 
 */
public class DBUtils {
	public static DataBase dbase = DataBase.getInstance();
	public static SQLiteDatabase db = dbase.getWritableDatabase();

	/**
	 * 将圈子成员插入本地数据库
	 * 
	 * @param circleName
	 * @param pid
	 * @param uid
	 * @param name
	 * @param img
	 * @param employer
	 * @param sortkey
	 */
	public static void insertCircleUser(String cid, String circleName,
			String pid, String uid, String name, String img, String employer,
			String mobileNum, String sortkey, String pinyinFir) {
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("cid", cid);
		values.put("personID", pid);
		values.put("userID", uid);
		values.put("userName", name);
		values.put("userImg", img);
		values.put("employer", employer);
		values.put("mobileNum", mobileNum);
		values.put("sortkey", sortkey);
		values.put("pinyinFir", pinyinFir);
		insertData(circleName, values);
	}

	/**
	 * 从数据库获取存储的本地圈
	 * 
	 * @return
	 */
	public static List<CircleModle> getCircleList() {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		List<CircleModle> data = new ArrayList<CircleModle>();
		Cursor cursor = db.query("circlelist", null, null, null, null, null,
				null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String id = cursor.getString(cursor.getColumnIndex("cirID"));
				String name = cursor
						.getString(cursor.getColumnIndex("cirName"));
				String imgAdd = cursor.getString(cursor
						.getColumnIndex("cirImg"));
				CircleModle modle = new CircleModle();
				modle.setCirID(id);
				modle.setCirName(name);
				modle.setCirIcon(imgAdd);
				data.add(modle);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return data;
	}

	/**
	 * 从数据库获取存储用户列表
	 * 
	 * @return
	 */
	public static List<MemberModle> getUserList(String cirname) {
		if (!db.isOpen()) {
			db = dbase.getReadableDatabase();
		}
		List<MemberModle> data = new ArrayList<MemberModle>();
		Cursor cursor = db.query(cirname, null, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String id = cursor.getString(cursor.getColumnIndex("userID"));
				String name = cursor.getString(cursor
						.getColumnIndex("userName"));
				String imgAdd = cursor.getString(cursor
						.getColumnIndex("userImg"));
				String employer = cursor.getString(cursor
						.getColumnIndex("employer"));
				String sortkey = cursor.getString(cursor
						.getColumnIndex("sortkey"));
				MemberModle modle = new MemberModle();
				modle.setId(id);
				modle.setEmployer(employer);
				modle.setName(name);
				modle.setSort_key(sortkey.toUpperCase());
				modle.setImg(imgAdd);
				data.add(modle);
				cursor.moveToNext();
			}
		}
		cursor.close();
		MyComparator compartor = new MyComparator();
		Collections.sort(data, compartor);
		return data;
	}

	/**
	 * 查找圈子成员信息
	 * 
	 * @param userlistNmae圈子成员表名
	 * @param pid成员ID
	 * @return
	 */
	public static List<Info> getUserInfo(String userlistNmae, String pid) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		List<Info> listInfo = new ArrayList<Info>();
		Cursor cursor = db.query(userlistNmae, null, "personID='" + pid + "'",
				null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String key = cursor.getString(cursor.getColumnIndex("key"));
				String value = cursor.getString(cursor.getColumnIndex("value"));
				Info info = new Info();
				info.setKey(key);
				info.setValue(value);
				listInfo.add(info);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return listInfo;
	}

	/**
	 * 在用户资料表中查找指定用户是否存在
	 * 
	 * @param userlistNmae
	 * @param pid
	 * @return
	 */
	public static boolean isExistOfPersonId(String userlistNmae, String pid) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		Cursor cursor = db.query(userlistNmae, null, "personID='" + pid + "'",
				null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.close();
			return true;
		}
		cursor.close();
		return false;

	}

	/**
	 * 根据id查找姓名和头像地址
	 * 
	 * @param tabName
	 * @param id
	 * @return
	 */
	public static MemberInfoModle selectNameAndImgByID(String tabName, String id) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		MemberInfoModle modle = new MemberInfoModle();
		Cursor cursor = db.query(tabName, null, "userID='" + id + "'", null,
				null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex("userName"));
			String img = cursor.getString(cursor.getColumnIndex("userImg"));
			modle.setName(name);
			modle.setAvator(img);
		} else {
			cursor.close();
			return null;
		}
		cursor.close();
		return modle;
	}

	/**
	 * 查找成员信息
	 * 
	 * @param tabName
	 * @param uid
	 * @param pid
	 * @return
	 */
	public static MemberInfoModle findMemberInfo(String tabName, String uid,
			String pid2, String uid2, String cid) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		MemberInfoModle modle = new MemberInfoModle();
		Cursor cursor = db.query(tabName, null, "userID='" + uid + "'", null,
				null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex("userName"));
			String img = cursor.getString(cursor.getColumnIndex("userImg"));
			modle.setName(name);
			modle.setAvator(img);
		} else {
			modle = getUserInfoServer(cid, pid2, uid2);
		}
		cursor.close();
		return modle;

	}

	/**
	 * 从网络获取信息
	 * 
	 * @param cid
	 * @param pid
	 * @param content
	 * @return
	 */
	private static MemberInfoModle getUserInfoServer(String cid, String uid2,
			String pid2) {
		MemberInfoModle modle = new MemberInfoModle();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cid", cid);
		map.put("uid", SharedUtils.getString("uid", ""));
		map.put("pid2", 2);
		map.put("uid2", 2);
		map.put("token", SharedUtils.getString("token", ""));
		String result = HttpUrlHelper.postData(map, "/people/ibasic");
		try {
			JSONObject jsonobject = new JSONObject(result);
			JSONObject object = jsonobject.getJSONObject("person");
			String name = object.getString("name");
			modle.setName(name);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return modle;

	}

	/**
	 * 插入数据岛数据库
	 * 
	 * @param tableName
	 *            表名
	 * @param values
	 */
	public static void insertData(String tableName, ContentValues values) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		db.insert(tableName, null, values);
	}

	public static void clearTableData(String tableName) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		db.delete(tableName, null, null);
	}

	/**
	 * 根根据圈子ID查找圈子信息
	 * 
	 * @param cirID
	 */
	public static CircleModle findCircleInfoById(String cirID) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		CircleModle modle = new CircleModle();
		Cursor cursor = db.query("circlelist", null, "cirID='" + cirID + "'",
				null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex("cirName"));
			String img = cursor.getString(cursor.getColumnIndex("cirImg"));
			modle.setCirIcon(img);
			modle.setCirName(name);
			System.out.println("cirName:" + name);
		} else {
			cursor.close();
			return null;
		}
		cursor.close();
		return modle;
	}

	/**
	 * 根根据圈子ID查找圈子名称
	 * 
	 * @param cirID
	 */
	public static String getCircleNameById(String cirID) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		String cirName = "";
		Cursor cursor = db.query("circlelist", null, "cirID='" + cirID + "'",
				null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			cirName = cursor.getString(cursor.getColumnIndex("cirName"));
		} else {
			cursor.close();
			return null;
		}
		cursor.close();
		return cirName;
	}

	/**
	 * 根据userid获取姓名
	 * 
	 * @param circleTable
	 * @param uid
	 * @return
	 */
	public static String getUserNameByUid(String circleTable, String uid) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		String name = "";
		Cursor cursor = db.query(circleTable, null, "userID='" + uid + "'",
				null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			name = cursor.getString(cursor.getColumnIndex("userName"));
		} else {
			cursor.close();
			return null;
		}
		cursor.close();
		return name;
	}

	/**
	 * 根据圈子id编辑圈子信息
	 * 
	 * @param cv
	 * @param cirID
	 */
	public static void editCircleInfo(ContentValues cv, String cirID) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		db.update("circlelist", cv, "cirID=?", new String[] { cirID });

	}

	/**
	 * 根据圈子ID删除圈子
	 * 
	 * @param cirID
	 */
	public static void delCircle(String cirID) {
		db.delete("circlelist", "cirID=?", new String[] { cirID });
	}

	/**
	 * 创建表圈子所对应的表
	 */
	public static void creatTable(String circleName) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		// 创建圈子所对应的表
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ circleName
				+ " ( _id integer PRIMARY KEY AUTOINCREMENT ,cid varchar,personID varchar,userID varchar,userName varchar, userImg varchar,employer varchar,mobileNum varchar,sortkey varchar,pinyinFir varchar)");
		db.execSQL("create table IF NOT EXISTS "
				+ circleName
				+ "userlist"
				+ "( _id integer PRIMARY KEY AUTOINCREMENT ,tID varchar,personID varchar,key varchar, value varchar,startDate varchar,endDate)");
	}

	/**
	 * 模糊查询
	 * 
	 * @param tableName
	 */
	public static List<MemberModle> fuzzyQuery(String str) {
		List<MemberModle> listModle = new ArrayList<MemberModle>();
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		Cursor curCircle = db.query("circlelist", null, null, null, null, null,
				null);
		Cursor cur = null;
		if (curCircle.getCount() > 0) {
			curCircle.moveToFirst();
			for (int i = 0; i < curCircle.getCount(); i++) {
				String cirID = curCircle.getString(curCircle
						.getColumnIndex("cirID"));
				cur = db.query(
						"circle" + cirID,
						new String[] { "userName", "userImg", "cid",
								"personID", "sortkey", "pinyinFir", "mobileNum" },
						"userName like ?  or sortkey like ? or pinyinFir like ? or mobileNum like ?",
						new String[] { "%" + str + "%", "%" + str + "%",
								"%" + str + "%", "%" + str + "%" }, null, null,
						null);
				if (cur.getCount() > 0) {
					cur.moveToFirst();
					for (int j = 0; j < cur.getCount(); j++) {
						MemberModle modle = new MemberModle();
						String name = cur.getString(cur
								.getColumnIndex("userName"));
						String img = cur.getString(cur
								.getColumnIndex("userImg"));
						String cid = cur.getString(cur.getColumnIndex("cid"));
						String pid = cur.getString(cur
								.getColumnIndex("personID"));
						String mobileNum = cur.getString(cur
								.getColumnIndex("mobileNum"));
						String circleName = getCircleNameById(cid);
						modle.setCircleName(circleName);
						modle.setImg(img);
						modle.setName(name);
						modle.setId(pid);
						modle.setCid(cid);
						modle.setMobileNum(mobileNum);
						listModle.add(modle);
						cur.moveToNext();
					}
				}
				curCircle.moveToNext();
			}
		}
		cur.close();
		curCircle.close();
		return listModle;

	}

	public static void close() {
		if (db != null) {
			db.close();
		}
	}
}
