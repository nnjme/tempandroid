package com.changlianxi.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.Circle;
import com.changlianxi.modle.CircleIdetailModle;
import com.changlianxi.modle.GrowthImgModle;
import com.changlianxi.modle.GrowthModle;
import com.changlianxi.modle.MemberInfoModle;
import com.changlianxi.modle.MemberModle;
import com.changlianxi.modle.MessageModle;
import com.changlianxi.modle.NewsModle;
import com.changlianxi.util.ChatTimeComparator;
import com.changlianxi.util.Constants;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.MyComparator;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;

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
	public static void insertCircleUser(String cid, String pid, String uid,
			String name, String img, String employer, String mobileNum,
			String auth, String location, String sortkey, String pinyinFir) {
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("cid", cid);
		values.put("personID", pid);
		values.put("userID", uid);
		values.put("userName", name);
		values.put("userImg", img);
		values.put("employer", employer);
		values.put("mobileNum", mobileNum);
		values.put("auth", auth);
		values.put("location", location);
		values.put("sortkey", sortkey);
		values.put("pinyinFir", pinyinFir);
		insertData(Constants.USERLIST_TABLE, values);
	}

	/**
	 * 根据cid删除用户
	 * 
	 * @param cid
	 */
	public static void delUserListByCid(String cid) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		db.delete(Constants.USERLIST_TABLE, "cid=?", new String[] { cid });
	}

	/**
	 * 从数据库获取存储用户列表
	 * 
	 * @return
	 */
	public static List<MemberModle> getUserListByCid(String cid) {
		if (!db.isOpen()) {
			db = dbase.getReadableDatabase();
		}
		List<MemberModle> data = new ArrayList<MemberModle>();
		Cursor cursor = db.query(Constants.USERLIST_TABLE, null, "cid='" + cid
				+ "'", null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String pid = cursor
						.getString(cursor.getColumnIndex("personID"));
				String uid = cursor.getString(cursor.getColumnIndex("userID"));
				String name = cursor.getString(cursor
						.getColumnIndex("userName"));
				String imgAdd = cursor.getString(cursor
						.getColumnIndex("userImg"));
				String employer = cursor.getString(cursor
						.getColumnIndex("employer"));
				String sortkey = cursor.getString(cursor
						.getColumnIndex("sortkey"));
				String mobile = cursor.getString(cursor
						.getColumnIndex("mobileNum"));
				String auth = cursor.getString(cursor.getColumnIndex("auth"));
				String location = cursor.getString(cursor
						.getColumnIndex("location"));
				MemberModle modle = new MemberModle();
				modle.setUid(uid);
				modle.setId(pid);
				modle.setEmployer(employer.equals("null") ? "" : employer);
				modle.setName(name);
				modle.setSort_key(sortkey.toUpperCase());
				modle.setImg(imgAdd);
				modle.setMobileNum(mobile);
				modle.setAuth(auth.equals("1") ? true : false);
				modle.setLocation(location);
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
	 * 将用户资料信息保存到本地数据库
	 * 
	 * @param cid
	 * @param tid
	 * @param personId
	 * @param key
	 * @param value
	 * @param start
	 * @param end
	 */
	public static void insertUserDetails(String cid, String tid,
			String personId, String key, String value, String start, String end) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("cid", cid);
		values.put("tID", tid);
		values.put("personID", personId);
		values.put("key", key);
		values.put("value", value);
		values.put("startDate", start);
		values.put("endDate", end);
		db.insert(Constants.USERDETAIL, null, values);
	}

	/**
	 * 根据pid删除用户的资料信息
	 * 
	 * @param cirID
	 */
	public static void delUserDetails(String pid) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		db.delete(Constants.USERDETAIL, "personID=?", new String[] { pid });
	}

	/**
	 * 从数据库获取存储的本地圈
	 * 
	 * @return
	 */
	public static List<Circle> getCircleList() {
		if (!db.isOpen()) {
			db = dbase.getReadableDatabase();
		}
		List<Circle> data = new ArrayList<Circle>();
		Cursor cursor = db.query(Constants.CIRCLELIST_TABLE, null, null, null,
				null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String id = cursor.getString(cursor.getColumnIndex("cirID"));
				String name = cursor
						.getString(cursor.getColumnIndex("cirName"));
				String imgAdd = cursor.getString(cursor
						.getColumnIndex("cirImg"));
				String isNew = cursor.getString(cursor.getColumnIndex("isNew"));
				Circle modle = new Circle(Integer.parseInt(id));
				modle.setCirID(id);
				modle.setCirName(name);
				modle.setCirIcon(imgAdd);
				modle.setNew("1".equals(isNew) ? true : false);
				data.add(modle);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return data;
	}

	/**
	 * 获取圈子提醒数
	 */
	public static List<Circle> getCirclePtompt() {
		if (!db.isOpen()) {
			db = dbase.getReadableDatabase();
		}
		List<Circle> data = new ArrayList<Circle>();
		Cursor cursor = db.query("circlelist", null, null, null, null, null,
				null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String id = cursor.getString(cursor.getColumnIndex("cirID"));
				int newMemberCount = cursor.getInt(cursor
						.getColumnIndex("newMemberCount"));
				int newGrowthCount = cursor.getInt(cursor
						.getColumnIndex("newGrowthCount"));
				int newChatCount = cursor.getInt(cursor
						.getColumnIndex("newChatCount"));
				int newDynamicCount = cursor.getInt(cursor
						.getColumnIndex("newDynamicCount"));
				int newCommentCount = cursor.getInt(cursor
						.getColumnIndex("newCommentCount"));
				int promptCount = cursor.getInt(cursor
						.getColumnIndex("promptCount"));
				Circle modle = new Circle(Integer.parseInt(id));
				modle.setCirID(id);
				modle.setNewChatCount(newChatCount);
				modle.setNewCommentCount(newCommentCount);
				modle.setNewDynamicCount(newDynamicCount);
				modle.setNewGrowthCount(newGrowthCount);
				modle.setNewMemberCount(newMemberCount);
				modle.setPromptCount(promptCount);
				data.add(modle);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return data;
	}

	/**
	 * 保存圈子信息
	 * 
	 * @param cid
	 * @param cirName
	 * @param cirIcon
	 * @param cirDescribe
	 * @param cirMmembersTotal
	 * @param cirMembersVerified
	 */
	public static void saveCircleDetail(String cid, String cirName,
			String cirIcon, String cirDescribe, String cirMmembersTotal,
			String cirMembersVerified, String creator) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("cid", cid);
		values.put("cirName", cirName);
		values.put("cirIcon", cirIcon);
		values.put("cirDescribe", cirDescribe);
		values.put("cirMmembersTotal", cirMmembersTotal);
		values.put("cirMembersVerified", cirMembersVerified);
		values.put("creator", creator);
		db.insert(Constants.CIRCLEDETAIL, null, values);
	}

	/**
	 * 根据cid删除圈子的资料信息
	 * 
	 * @param cirID
	 */
	public static void delCiecleDetails(String cid) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		db.delete(Constants.CIRCLEDETAIL, "cid=?", new String[] { cid });
	}

	/**
	 * 获取圈子信息
	 * 
	 * @param cid
	 * @return
	 */
	public static CircleIdetailModle getCircleDetail(String cid) {
		Cursor cursor = db.query(Constants.CIRCLEDETAIL, null, "cid='" + cid
				+ "'", null, null, null, null);
		CircleIdetailModle modle = new CircleIdetailModle();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex("cirName"));
			String cirIcon = cursor.getString(cursor.getColumnIndex("cirIcon"));
			String cirDescribe = cursor.getString(cursor
					.getColumnIndex("cirDescribe"));
			String cirMmembersTotal = cursor.getString(cursor
					.getColumnIndex("cirMmembersTotal"));
			String cirMembersVerified = cursor.getString(cursor
					.getColumnIndex("cirMembersVerified"));
			String creator = cursor.getString(cursor.getColumnIndex("creator"));
			modle.setName(name);
			modle.setDescription(cirDescribe);
			modle.setLogo(cirIcon);
			modle.setCreator(creator);
			modle.setMembersTotal(Integer.valueOf(cirMmembersTotal));
			modle.setMembersVerified(Integer.valueOf(cirMembersVerified));
		} else {
			cursor.close();
			return null;
		}
		cursor.close();
		return modle;
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
	public static MemberInfoModle selectNameAndImgByID(String id) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		//
		MemberInfoModle modle = new MemberInfoModle();
		// if (!tabbleIsExist(tabName)) {
		// return modle;
		// }
		Cursor cursor = db.query(Constants.USERLIST_TABLE, null, "userID='"
				+ id + "'", null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex("userName"));
			String img = cursor.getString(cursor.getColumnIndex("userImg"));
			modle.setName(name);
			modle.setAvator(img);
		} else {
			cursor.close();
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
	public static MemberInfoModle findMemberInfo(String uid, String pid2,
			String uid2, String cid) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		MemberInfoModle modle = new MemberInfoModle();
		if (uid.equals("0")) {
			modle = getUserInfoServer(cid, uid2, pid2);
			return modle;
		}
		Cursor cursor = db.query(Constants.USERLIST_TABLE, null, "userID='"
				+ uid + "'", null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex("userName"));
			String img = cursor.getString(cursor.getColumnIndex("userImg"));
			modle.setName(name.equals("") ? "  " : name);
			modle.setAvator(img);
		}

		cursor.close();
		return modle;

	}

	/**
	 * 根据成员uid得到成员pid
	 * 
	 * @param uid
	 */
	public static String getPidByUid(String uid) {
		if (!db.isOpen()) {
			db = dbase.getReadableDatabase();
		}
		String pid = "0";
		Cursor cursor = db.query(Constants.USERLIST_TABLE, null, "userID='"
				+ uid + "'", null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			pid = cursor.getString(cursor.getColumnIndex("personID"));
		}
		return pid;
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
		map.put("pid2", pid2);
		map.put("uid2", uid2);
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
	 * 查看用户是否已认证
	 * 
	 * @param tableName
	 * @param uid
	 * @return
	 */
	public static boolean isAuth(String uid) {
		if (!db.isOpen()) {
			db = dbase.getReadableDatabase();
		}
		Cursor cursor = db.query(Constants.USERLIST_TABLE, null, "userID='"
				+ uid + "'", null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String auth = cursor.getString(cursor.getColumnIndex("auth"));
			if (auth.equals("1")) {
				cursor.close();
				return true;
			}
		}
		cursor.close();
		return false;

	}

	/**
	 * 根根据圈子ID查找圈子信息
	 * 
	 * @param cirID
	 */
	public static Circle findCircleInfoById(String cirID) {
		if (!db.isOpen()) {
			db = dbase.getReadableDatabase();
		}
		Circle modle = new Circle(Integer.parseInt(cirID));
		Cursor cursor = db.query(Constants.CIRCLELIST_TABLE, null, "cirID='"
				+ cirID + "'", null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex("cirName"));
			String img = cursor.getString(cursor.getColumnIndex("cirImg"));
			modle.setCirIcon(img);
			modle.setCirName(name);
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
			db = dbase.getReadableDatabase();
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
	public static String getUserNameByUid(String uid) {
		if (!db.isOpen()) {
			db = dbase.getReadableDatabase();
		}
		String name = "";
		Cursor cursor = db.query(Constants.USERLIST_TABLE, null, "userID='"
				+ uid + "'", null, null, null, null);
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
	 * 
	 * 获取个人姓名
	 * 
	 * @param uid
	 * @return
	 */
	public static String getMyName(String uid) {
		if (!db.isOpen()) {
			db = dbase.getReadableDatabase();
		}
		String name = "";
		Cursor cursor = db.query(Constants.MYDETAIL, null, "uid='" + uid + "'",
				null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			name = cursor.getString(cursor.getColumnIndex("name"));
		}
		cursor.close();
		return name;
	}

	/**
	 * 对表的修改
	 * 
	 * @param tableName
	 *            要修改表名
	 * @param values
	 *            修改以后的值
	 * @param whereClause
	 *            修改条件
	 * @param whereArgs
	 */
	public static void updateInfo(String tableName, ContentValues values,
			String whereClause, String[] whereArgs) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		db.update(tableName, values, whereClause, whereArgs);

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
		db.update(Constants.CIRCLELIST_TABLE, cv, "cirID=?",
				new String[] { cirID });

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
	 * 模糊查询
	 * 
	 * @param tableName
	 */
	public static List<MemberModle> fuzzyQuery(String str) {
		List<MemberModle> listModle = new ArrayList<MemberModle>();
		if (!db.isOpen()) {
			db = dbase.getReadableDatabase();
		}
		// Cursor curCircle = db.query("circlelist", null, null, null, null,
		// null,
		// null);
		Cursor cur = null;
		// if (curCircle.getCount() > 0) {
		// curCircle.moveToFirst();
		// for (int i = 0; i < curCircle.getCount(); i++) {
		// String cirID = curCircle.getString(curCircle
		// .getColumnIndex("cirID"));
		cur = db.query(
				Constants.USERLIST_TABLE,
				new String[] { "userName", "userImg", "cid", "personID",
						"sortkey", "pinyinFir", "mobileNum" },
				"userName like ?  or sortkey like ? or pinyinFir like ? or mobileNum like ?",
				new String[] { "%" + str + "%", "%" + str + "%",
						"%" + str + "%", "%" + str + "%" }, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			for (int j = 0; j < cur.getCount(); j++) {
				MemberModle modle = new MemberModle();
				String name = cur.getString(cur.getColumnIndex("userName"));
				String img = cur.getString(cur.getColumnIndex("userImg"));
				String cid = cur.getString(cur.getColumnIndex("cid"));
				String pid = cur.getString(cur.getColumnIndex("personID"));
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
		// curCircle.moveToNext();
		// }
		// }
		cur.close();
		// curCircle.close();
		return listModle;

	}

	/************************* 成长操作******************************* **/
	/**
	 * 保存成长记录
	 * 
	 * @param cid
	 * @param name
	 * @param avatar
	 * @param growthID
	 * @param uid
	 * @param content
	 * @param location
	 * @param happen
	 * @param praise
	 * @param comment
	 * @param img
	 */
	public static void saveGrowth(String cid, String name, String avatar,
			String growthID, String uid, String content, String location,
			String happen, String publish, int praise, int comment,
			int isparise, String img) {
		ContentValues values = new ContentValues();
		values.put("cid", cid);
		values.put("name", name);
		values.put("avatar", avatar);
		values.put("growthID", growthID);
		values.put("uid", uid);
		values.put("content", content);
		values.put("location", location);
		values.put("happen", happen);
		values.put("publish", publish);
		values.put("praise", praise);
		values.put("comment", comment);
		values.put("isparise", isparise);
		values.put("img", img);
		insertData(Constants.GROWTH_TABLE, values);

	}

	public static void delGrowthById(String cid) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		db.delete(Constants.GROWTH_TABLE, "cid=?", new String[] { cid });
	}

	/**
	 * 获取成长记录
	 * 
	 * @param cid
	 * @return
	 */
	public static List<GrowthModle> getGrowthList(String cid) {
		List<GrowthModle> listMode = new ArrayList<GrowthModle>();
		Cursor cursor = db.query(Constants.GROWTH_TABLE, null, "cid='" + cid
				+ "'", null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				GrowthModle modle = new GrowthModle();
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String avatar = cursor.getString(cursor
						.getColumnIndex("avatar"));
				String growthID = cursor.getString(cursor
						.getColumnIndex("growthID"));
				String uid = cursor.getString(cursor.getColumnIndex("uid"));
				String content = cursor.getString(cursor
						.getColumnIndex("content"));
				String location = cursor.getString(cursor
						.getColumnIndex("location"));
				String happen = cursor.getString(cursor
						.getColumnIndex("happen"));
				String publish = cursor.getString(cursor
						.getColumnIndex("publish"));
				int praise = cursor.getInt(cursor.getColumnIndex("praise"));
				int comment = cursor.getInt(cursor.getColumnIndex("comment"));
				int isparise = cursor.getInt(cursor.getColumnIndex("isparise"));
				String img = cursor.getString(cursor.getColumnIndex("img"));
				try {
					JSONArray json = new JSONArray(img);
					List<GrowthImgModle> imgModle = new ArrayList<GrowthImgModle>();
					for (int j = 0; j < json.length(); j++) {
						GrowthImgModle m = new GrowthImgModle();
						JSONObject imgObj = (JSONObject) json.opt(j);
						String imgPath = imgObj.getString("img");
						m.setImg(imgPath);
						m.setImg_200(StringUtils
								.JoinString(imgPath, "_200x200"));
						m.setImg_100(StringUtils
								.JoinString(imgPath, "_100x100"));
						m.setImg_60(StringUtils.JoinString(imgPath, "_60x60"));
						m.setImg_500(StringUtils
								.JoinString(imgPath, "_500x500"));
						imgModle.add(m);
					}
					modle.setImgModle(imgModle);

				} catch (JSONException e) {
					e.printStackTrace();
				}
				modle.setCid(cid);
				modle.setComment(comment);
				modle.setContent(content);
				modle.setHappen(happen);
				modle.setIspraise(isparise == 1 ? true : false);
				modle.setUid(uid);
				modle.setPublish(publish);
				modle.setPraise(praise);
				modle.setPersonImg(avatar);
				modle.setName(name);
				modle.setLocation(location);
				modle.setId(growthID);
				listMode.add(modle);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return listMode;

	}

	/************************* 动态操作******************************* **/
	/**
	 * 将动态列表保存到数据库
	 */
	public static void insertNews(String cid, String newsID, String user1,
			String user2, String person2, String time, String content,
			String detail, String user1Name, String user2Name,
			String avatarURL, String need_approve) {
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("cid", cid);
		values.put("newsID", newsID);
		values.put("user1", user1);
		values.put("user2", user2);
		values.put("person2", person2);
		values.put("time", time);
		values.put("content", content);
		values.put("detail", detail);
		values.put("user1Name", user1Name);
		values.put("user2Name", user2Name);
		values.put("avatarURL", avatarURL);
		values.put("need_approve", need_approve);
		insertData(Constants.NEWS_TABLE, values);
	}

	/**
	 * 获取动态信息
	 * 
	 * @param cid
	 * @return
	 */
	public static List<NewsModle> getNewsList(String cid) {
		if (!db.isOpen()) {
			db = dbase.getReadableDatabase();
		}
		List<NewsModle> listMode = new ArrayList<NewsModle>();
		Cursor cursor = db.query(Constants.NEWS_TABLE, null, "cid='" + cid
				+ "'", null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			for (int i = 0; i < cursor.getCount(); i++) {

				NewsModle modle = new NewsModle();
				String newsID = cursor.getString(cursor
						.getColumnIndex("newsID"));
				String user1 = cursor.getString(cursor.getColumnIndex("user1"));
				String user2 = cursor.getString(cursor.getColumnIndex("user2"));
				String person2 = cursor.getString(cursor
						.getColumnIndex("person2"));
				String time = cursor.getString(cursor.getColumnIndex("time"));
				String detail = cursor.getString(cursor
						.getColumnIndex("detail"));
				String content = cursor.getString(cursor
						.getColumnIndex("content"));
				String user1Name = cursor.getString(cursor
						.getColumnIndex("user1Name"));
				String user2Name = cursor.getString(cursor
						.getColumnIndex("user2Name"));
				String avatarURL = cursor.getString(cursor
						.getColumnIndex("avatarURL"));
				String need_approve = cursor.getString(cursor
						.getColumnIndex("need_approve"));
				modle.setCreatedTime(time);
				modle.setAvatarUrl(avatarURL);
				modle.setContent(content);
				modle.setDetail(detail);
				modle.setId(newsID);
				modle.setNeed_approve(need_approve);
				modle.setPerson2(person2);
				modle.setUser2Name(user2Name);
				modle.setUser1Name(user1Name);
				modle.setUser1(user1);
				modle.setUser2(user2);
				listMode.add(modle);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return listMode;
		// TODO Auto-generated method stub

	}

	/**
	 * 根据cid删除动态列表信息
	 * 
	 * @param cirID
	 */
	public static void delNewsList(String cid) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		db.delete(Constants.NEWS_TABLE, "cid=?", new String[] { cid });
	}

	/****************************************** 聊天操作****************************** ****/
	/**
	 * 将聊天记录存入数据库
	 * 
	 * @param cid
	 * @param name
	 * @param avatarURL
	 * @param time
	 * @param content
	 * @param self
	 */
	public static void saveChatMessage(MessageModle modle) {
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("cid", modle.getCid());
		values.put("name", modle.getName());
		values.put("avatarURL", modle.getAvatar());
		values.put("time", modle.getTime());
		values.put("content", modle.getContent());
		values.put("self", modle.isSelf() ? 1 : 0);
		values.put("type", modle.getType());
		insertData(Constants.CHATLIST_TABLE_NAME, values);
	}

	/**
	 * 获取聊天记录
	 * 
	 * @param cid
	 * @return
	 */
	public static List<MessageModle> getChatMessage(String cid) {
		List<MessageModle> listModle = new ArrayList<MessageModle>();
		if (!db.isOpen()) {
			db = dbase.getReadableDatabase();
		}
		// String sql = "select * from " + Constants.CHATLIST_TABLE_NAME
		// + "  where cid='" + cid + "'" + " Limit " + 20;
		Cursor cursor = db.query(Constants.CHATLIST_TABLE_NAME, null, "cid='"
				+ cid + "'", null, null, null, null);
		// Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				MessageModle modle = new MessageModle();
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String avatarURL = cursor.getString(cursor
						.getColumnIndex("avatarURL"));
				String time = cursor.getString(cursor.getColumnIndex("time"));
				String content = cursor.getString(cursor
						.getColumnIndex("content"));
				String self = cursor.getString(cursor.getColumnIndex("self"));
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				modle.setAvatar(avatarURL);
				modle.setCid(cid);
				modle.setContent(content);
				modle.setSelf(self.equals("1") ? true : false);
				modle.setTime(time);
				modle.setName(name);
				modle.setType(type);
				listModle.add(modle);
				cursor.moveToNext();
			}
		} else {
			cursor.close();
		}
		ChatTimeComparator compartor = new ChatTimeComparator();
		Collections.sort(listModle, compartor);// 按时间排序
		return listModle;
	}

	/**
	 * 根据圈子ID删除圈子内的聊天记录
	 * 
	 * @param cirID
	 */
	public static void delCircleChatMessage(String cid) {
		if (!db.isOpen()) {
			db = dbase.getReadableDatabase();
		}
		db.delete(Constants.CHATLIST_TABLE_NAME, "cid=?", new String[] { cid });
	}

	/**
	 * 保存私信
	 * 
	 * @param modle
	 */
	public static void saveMessage(MessageModle modle, String keyID) {
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("cid", modle.getCid());
		values.put("ruid", modle.getUid());
		values.put("keyID", keyID);
		values.put("name", modle.getName());
		values.put("avatarURL", modle.getAvatar());
		values.put("time", modle.getTime());
		values.put("content", modle.getContent());
		values.put("self", modle.isSelf() ? 1 : 0);
		values.put("type", modle.getType());
		insertData(Constants.MESSAGELIST_TABLE_NAME, values);
	}

	/**
	 * 获取私信
	 * 
	 * @param cid
	 * @return
	 */
	public static List<MessageModle> getMessage(String keyID) {
		List<MessageModle> listModle = new ArrayList<MessageModle>();
		if (!db.isOpen()) {
			db = dbase.getReadableDatabase();
		}
		Cursor cursor = db.query(Constants.MESSAGELIST_TABLE_NAME, null,
				"keyID='" + keyID + "'", null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				MessageModle modle = new MessageModle();
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String avatarURL = cursor.getString(cursor
						.getColumnIndex("avatarURL"));
				String time = cursor.getString(cursor.getColumnIndex("time"));
				String content = cursor.getString(cursor
						.getColumnIndex("content"));
				String ruid = cursor.getString(cursor.getColumnIndex("ruid"));
				String self = cursor.getString(cursor.getColumnIndex("self"));
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				modle.setAvatar(avatarURL);
				modle.setContent(content);
				modle.setSelf(self.equals("1") ? true : false);
				modle.setTime(time);
				modle.setName(name);
				modle.setUid(ruid);
				modle.setType(type);
				listModle.add(modle);
				cursor.moveToNext();
			}
		} else {
			cursor.close();
		}
		ChatTimeComparator compartor = new ChatTimeComparator();
		Collections.sort(listModle, compartor);// 按时间排序
		return listModle;

	}

	/**
	 * 根据uid删除私信记录
	 * 
	 * @param cirID
	 */
	public static void delMessage(String keyID) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		db.delete(Constants.MESSAGELIST_TABLE_NAME, "keyID=?",
				new String[] { keyID });
	}

	/**
	 * 判断某个表是否存在
	 * 
	 * @param tableName
	 * @return
	 */
	public static boolean tabbleIsExist(String tableName) {
		if (!db.isOpen()) {
			db = dbase.getReadableDatabase();
		}
		boolean result = false;
		if (tableName == null) {
			return false;
		}
		Cursor cursor = null;
		try {
			String sql = "select count(*) as c from sqlite_master   where type ='table' and name ='"
					+ tableName.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			cursor.close();
		}
		cursor.close();
		return result;
	}

	public static void close() {
		if (db != null) {
			db.close();
		}
	}

	/**
	 * 获取个人名片提醒
	 * 
	 * @return
	 */
	public static boolean getMyCardPrompt() {
		if (!db.isOpen()) {
			db = dbase.getReadableDatabase();
		}
		String changed = "";
		Cursor cursor = db.query(Constants.MYDETAIL, null, "uid='"
				+ SharedUtils.getString("uid", "") + "'", null, null, null,
				null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			changed = cursor.getString(cursor.getColumnIndex("changed"));
		}
		cursor.close();
		return changed.equals("1") ? true : false;
	}
}
