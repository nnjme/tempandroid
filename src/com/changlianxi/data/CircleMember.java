package com.changlianxi.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.CircleMemberState;
import com.changlianxi.data.enums.Gendar;
import com.changlianxi.data.enums.PersonDetailType;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.ArrayParser;
import com.changlianxi.data.parser.CircleMemberBasicParser;
import com.changlianxi.data.parser.CircleMemberDetailParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.parser.SimpleParser;
import com.changlianxi.data.parser.StringParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.ArrayResult;
import com.changlianxi.data.request.Result;
import com.changlianxi.data.request.SimpleResult;
import com.changlianxi.data.request.StringResult;
import com.changlianxi.db.Const;

/**
 * Circle Member
 * 
 * Usage:
 * 
 * get a member's detail info:
 *     // new member
 *     member.read();
 *     member.readDetails();
 *     member.getDetails();
 * 
 * refresh a member's detail info:
 *     // new member
 *     member.read()
 *     member.refresh(); // request and merge with local data
 *     member.writeDetails();
 *     member.write();
 *     
 * get or refresh a member's basic info:
 *     // new member
 *     member.read();
 *     member.refreshBasic();
 * 
 * upload after edit:
 *    // new member1
 *    // ...edit...
 *    // new member2 after edit
 *    member1.uploadAfterEdit(member2);
 *    member1.write();    
 * 
 * upload avatar
 *    // new member
 *    // ...edit avatar...
 *    // new avatar
 *    member.uploadAvatar(newAvatar);
 *    member.write();
 *    
 * other operations:
 *   // new member
 *   member.quit(); // quit the circle
 *   member.acceptInvitation(); // accept invitation
 *   member.refuseInvitation(); // refuse invitation
 *   member.kickout(); // be kick out
 *    
 * @author nnjme
 * 
 */
public class CircleMember extends AbstractData {
	public final static String DETAIL_API = "people/idetail";
	public final static String BASIC_API = "people/ibasic";
	public final static String EDIT_API = "people/iedit";
	public final static String UPLOAD_AVATAR_API = "people/iuploadAvatar";
	public final static String QUIT_API = "circles/iquit";
	public final static String ACCETP_INVITATION_API = "circles/iacceptInvitation";
	public final static String REFUSE_INVITATION_API = "circles/irefuseInvitation";
	public final static String KICKOUT_API = "circles/ikickOut";

	private int cid = 0;
	private int uid = 0;
	private int pid = 0;
	private String name = "";
	private String cellphone = "";
	private String location = "";
	private Gendar gendar = Gendar.UNKNOWN;
	private String avatar = "";
	private String birthday = "";
	private String employer = "";
	private String jobtitle = "";
	private String joinTime = "";
	private String lastModTime = "";
	private String leaveTime = "";
	private int roleId = 0;
	private String detailIds = "";
	private List<PersonDetail> details = new ArrayList<PersonDetail>();

	private CircleMemberState state = CircleMemberState.STATUS_INVALID;

	public CircleMember(int cid) {
		this(cid, 0);
	}

	public CircleMember(int cid, int pid) {
		this(cid, pid, 0);
	}

	public CircleMember(int cid, int pid, int uid) {
		this(cid, pid, uid, "");
	}

	public CircleMember(int cid, int pid, int uid, String name) {
		this.cid = cid;
		this.pid = pid;
		this.uid = uid;
		this.name = name;
	}
	
	public boolean isEmpty() {
		return pid == 0 && uid == 0;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public String getJobtitle() {
		return jobtitle;
	}

	public void setJobtitle(String jobtitle) {
		this.jobtitle = jobtitle;
	}

	public CircleMemberState getState() {
		return state;
	}

	public void setState(CircleMemberState state) {
		this.state = state;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Gendar getGendar() {
		return gendar;
	}

	public void setGendar(Gendar gendar) {
		this.gendar = gendar;
	}

	public void setGendar(int gendar) {
		this.gendar = Gendar.parseInt2Gendar(gendar);
	}
	
	public void setGendar(String gendar) {
		this.gendar = Gendar.parseString2Gendar(gendar);
	}
	
	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getJoinTime() {
		return joinTime;
	}

	public void setJoinTime(String joinTime) {
		this.joinTime = joinTime;
	}

	public String getLastModTime() {
		return lastModTime;
	}

	public void setLastModTime(String lastModTime) {
		this.lastModTime = lastModTime;
	}

	public String getLeaveTime() {
		return leaveTime;
	}

	public void setLeaveTime(String leaveTime) {
		this.leaveTime = leaveTime;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public List<PersonDetail> getDetails() {
		return details;
	}

	public void setDetails(List<PersonDetail> properties) {
		this.details = properties;
	}

	@Override
	public String toString() {
		return "CircleMember [cid=" + cid + ", uid=" + uid + ", pid=" + pid
				+ ", name=" + name + ", cellphone=" + cellphone + ", location=" + location + "]";
	}

	@Override
	public void read(SQLiteDatabase db) {
		String conditionsKey = "cid=? and pid=?";
		String[] conditionsValue = { this.cid + "", this.pid + "" };
		if (pid == 0) {
			conditionsKey = "cid=? and uid=?";
			conditionsValue = new String[] { this.cid + "", this.uid + "" };
		}
		Cursor cursor = db.query(Const.CIRCLE_MEMBER_TABLE_NAME, new String[] {
				"uid", "name", "cellphone", "location", "gendar", "avatar",
				"birthday", "employer", "jobtitle", "joinTime", "lastModTime",
				"leaveTime", "roleId", "state", "detailIds" }, conditionsKey,
				conditionsValue, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			int uid = cursor.getInt(cursor.getColumnIndex("uid"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String cellphone = cursor.getString(cursor
					.getColumnIndex("cellphone"));
			String location = cursor.getString(cursor
					.getColumnIndex("location"));
			int gendar = cursor.getInt(cursor.getColumnIndex("gendar"));
			String avatar = cursor.getString(cursor.getColumnIndex("avatar"));
			String birthday = cursor.getString(cursor
					.getColumnIndex("birthday"));
			String employer = cursor.getString(cursor
					.getColumnIndex("employer"));
			String jobtitle = cursor.getString(cursor
					.getColumnIndex("jobtitle"));
			String joinTime = cursor.getString(cursor
					.getColumnIndex("joinTime"));
			String lastModTime = cursor.getString(cursor
					.getColumnIndex("lastModTime"));
			String leaveTime = cursor.getString(cursor
					.getColumnIndex("leaveTime"));
			int roleId = cursor.getInt(cursor
					.getColumnIndex("roleId"));
			String state = cursor.getString(cursor.getColumnIndex("state"));
			String detailIds = cursor.getString(cursor
					.getColumnIndex("detailIds"));
			
			this.uid = uid;
			this.name = name;
			this.cellphone = cellphone;
			this.location = location;
			this.gendar = Gendar.parseInt2Gendar(gendar);
			this.avatar = avatar;
			this.birthday = birthday;
			this.employer = employer;
			this.jobtitle = jobtitle;
			this.joinTime = joinTime;
			this.lastModTime = lastModTime;
			this.leaveTime = leaveTime;
			this.roleId = roleId;
			this.state = CircleMemberState.convert(state);
			this.detailIds = detailIds;
		}
		cursor.close();

		// set status
		this.status = Status.OLD;
	}

	public void readDetails(SQLiteDatabase db) {
		List<PersonDetail> detials = new ArrayList<PersonDetail>();
		String[] ids = this.detailIds.split(",");
		if (ids.length > 0) {
			for (String pdid : ids) {
				if (!"".equals(pdid) && Integer.parseInt(pdid) > 0) {
					PersonDetail detail = new PersonDetail(Integer.parseInt(pdid),
							cid);
					detail.read(db);
					detials.add(detail);
				}
			}
		}
		this.setDetails(details);
		this.syncBasicAndDetail(true);
	}

	@Override
	public void write(SQLiteDatabase db) {
		String dbName = Const.CIRCLE_MEMBER_TABLE_NAME;
		if (this.status == Status.OLD) {
			return;
		}
		if (this.status == Status.DEL) {
			db.delete(dbName, "cid=? and pid=?", new String[] { cid + "", pid + "" });
			return;
		}

		ContentValues cv = new ContentValues();
		cv.put("cid", cid);
		cv.put("uid", uid);
		cv.put("pid", pid);
		cv.put("name", name);
		cv.put("cellphone", cellphone);
		cv.put("location", location);
		cv.put("gendar", Gendar.parseGendar2Int(gendar));
		cv.put("avatar", avatar);
		cv.put("birthday", birthday);
		cv.put("employer", employer);
		cv.put("jobtitle", jobtitle);
		cv.put("joinTime", joinTime);
		cv.put("lastModTime", lastModTime);
		cv.put("leaveTime", leaveTime);
		cv.put("roleId", roleId);
		cv.put("state", state.name());

		if (this.status == Status.NEW) {
			db.insert(dbName, null, cv);
		} else if (this.status == Status.UPDATE) {
			db.update(dbName, cv, "cid=? and pid=?", new String[] { cid + "", pid + "" });
		}
		this.status = Status.OLD;
	}

	public void writeDetails(SQLiteDatabase db) {
		StringBuffer detailIds = new StringBuffer();
		for (PersonDetail pd : details) {
			if (pd.getStatus() != Status.DEL) {
				detailIds.append(pd.getId());
				detailIds.append(",");
			}
			pd.write(db);
		}

		this.detailIds = detailIds.toString();
		ContentValues cv = new ContentValues();
		cv.put("detailIds", this.detailIds);
		db.update(Const.CIRCLE_MEMBER_TABLE_NAME, cv, "cid=? and pid=?",
				new String[] { cid + "", pid + "" });
	}

	@Override
	public void update(IData data) {
		if (!(data instanceof CircleMember)) {
			return;
		}
		
		CircleMember another = (CircleMember) data;
		boolean isChange = false;
		if (this.cid != another.cid) {
			this.cid = another.cid;
			isChange = true;
		}
		if (this.uid != another.uid) {
			this.uid = another.uid;
			isChange = true;
		}
		if (this.pid != another.pid) {
			this.pid = another.pid;
			isChange = true;
		}
		if (!this.name.equals(another.name)) {
			this.name = another.name;
			isChange = true;
		}
		if (!this.cellphone.equals(another.cellphone)) {
			this.cellphone = another.cellphone;
			isChange = true;
		}
		if (!this.location.equals(another.location)) {
			this.location = another.location;
			isChange = true;
		}
		if (this.gendar != another.gendar) {
			this.gendar = another.gendar;
			isChange = true;
		}
		if (!this.avatar.equals(another.avatar)) {
			this.avatar = another.avatar;
			isChange = true;
		}
		if (!this.birthday.equals(another.birthday)) {
			this.birthday = another.birthday;
			isChange = true;
		}
		if (!this.employer.equals(another.employer)) {
			this.employer = another.employer;
			isChange = true;
		}
		if (!this.jobtitle.equals(another.jobtitle)) {
			this.jobtitle = another.jobtitle;
			isChange = true;
		}

		if (isChange && this.status == Status.OLD) {
			this.status = Status.UPDATE;
		}
	}

	/**
	 * update for member list synchronize
	 * 
	 * @param another
	 */
	public void updateListSummary(CircleMember another) {
		boolean isChange = false;
		if (this.cid != another.cid) {
			this.cid = another.cid;
			isChange = true;
		}
		if (this.uid != another.uid) {
			this.uid = another.uid;
			isChange = true;
		}
		if (this.pid != another.pid) {
			this.pid = another.pid;
			isChange = true;
		}
		if (!this.name.equals(another.name)) {
			this.name = another.name;
			isChange = true;
		}
		if (!this.cellphone.equals(another.cellphone)) {
			this.cellphone = another.cellphone;
			isChange = true;
		}
		if (!this.location.equals(another.location)) {
			this.location = another.location;
			isChange = true;
		}
		if (!this.avatar.equals(another.avatar)) {
			this.avatar = another.avatar;
			isChange = true;
		}
		if (!this.employer.equals(another.employer)) {
			this.employer = another.employer;
			isChange = true;
		}
		if (!this.jobtitle.equals(another.jobtitle)) {
			this.jobtitle = another.jobtitle;
			isChange = true;
		}
		if (!this.joinTime.equals(another.joinTime)) {
			this.joinTime = another.joinTime;
			isChange = true;
		}
		if (!this.lastModTime.equals(another.lastModTime)) {
			this.lastModTime = another.lastModTime;
			isChange = true;
		}
		if (!this.leaveTime.equals(another.leaveTime)) {
			this.leaveTime = another.leaveTime;
			isChange = true;
		}
		if (this.roleId != another.roleId) {
			this.roleId = another.roleId;
			isChange = true;
		}
		if (this.state != another.state) {
			this.state = another.state;
			isChange = true;
		}

		if (isChange && this.status == Status.OLD) {
			this.status = Status.UPDATE;
		}
	}

	@SuppressLint("UseSparseArrays")
	protected boolean updateDetails(CircleMember another) {
		if (another.getDetails().size() == 0) {
			return false;
		}
		
		boolean isChange = false;
		if ("".equals(this.detailIds)) {
			this.setDetails(another.getDetails());
			return true;
		}
		
		Map<Integer, PersonDetail> olds = new HashMap<Integer, PersonDetail>();
		Map<Integer, PersonDetail> news = new HashMap<Integer, PersonDetail>();
		for (PersonDetail cr : this.details) {
			olds.put(cr.getId(), cr);
		}
		for (PersonDetail p : another.details) {
			int propid = p.getId();
			news.put(propid, p);
			if (olds.containsKey(propid)) {
				// update
				olds.get(propid).update(p);
				isChange = true;
			} else {
				// new
				this.details.add(p);
				isChange = true;
			}
		}
		for (PersonDetail p : this.details) {
			if (!news.containsKey(p.getId())) {
				// del
				p.setStatus(Status.DEL);
				isChange = true;
			}
		}
		
		return isChange;
	}

	protected void syncBasicAndDetail(boolean forward) {
		Map<PersonDetailType, PersonDetail> type2Details =
				new HashMap<PersonDetailType, PersonDetail>();
		for (PersonDetail pd : this.details) {
			type2Details.put(pd.getType(), pd);
		}
		
		if (forward) {
			// basic => detail
			PersonDetailType type = PersonDetailType.D_NAME;
			if (!"".equals(this.name)) {
				if (type2Details.containsKey(type)) {
					type2Details.get(type).setValue(this.name);
				} else {
					PersonDetail pd = new PersonDetail(0, cid);
					pd.setType(type);
					pd.setValue(this.name);
					this.details.add(pd);
				}
			}
			type = PersonDetailType.D_CELLPHONE;
			if (!"".equals(this.cellphone)) {
				if (type2Details.containsKey(type)) {
					type2Details.get(type).setValue(this.cellphone);
				} else {
					PersonDetail pd = new PersonDetail(0, cid);
					pd.setType(type);
					pd.setValue(this.cellphone);
					this.details.add(pd);
				}
			}
			if (Gendar.UNKNOWN != this.gendar) {
				if (type2Details.containsKey(type)) {
					type2Details.get(type).setValue(Gendar.parseGendar2String(this.gendar));
				} else {
					PersonDetail pd = new PersonDetail(0, cid);
					pd.setType(type);
					pd.setValue(Gendar.parseGendar2String(this.gendar));
					this.details.add(pd);
				}
			}
			type = PersonDetailType.D_AVATAR;
			if (!"".equals(this.avatar)) {
				if (type2Details.containsKey(type)) {
					type2Details.get(type).setValue(this.avatar);
				} else {
					PersonDetail pd = new PersonDetail(0, cid);
					pd.setType(type);
					pd.setValue(this.avatar);
					this.details.add(pd);
				}
			}
			type = PersonDetailType.D_BIRTHDAY;
			if (!"".equals(this.birthday)) {
				if (type2Details.containsKey(type)) {
					type2Details.get(type).setValue(this.birthday);
				} else {
					PersonDetail pd = new PersonDetail(0, cid);
					pd.setType(type);
					pd.setValue(this.birthday);
					this.details.add(pd);
				}
			}
			type = PersonDetailType.D_EMPLOYER;
			if (!"".equals(this.employer)) {
				if (type2Details.containsKey(type)) {
					type2Details.get(type).setValue(this.employer);
				} else {
					PersonDetail pd = new PersonDetail(0, cid);
					pd.setType(type);
					pd.setValue(this.employer);
					this.details.add(pd);
				}
			}
			type = PersonDetailType.D_JOBTITLE;
			if (!"".equals(this.jobtitle)) {
				if (type2Details.containsKey(type)) {
					type2Details.get(type).setValue(this.jobtitle);
				} else {
					PersonDetail pd = new PersonDetail(0, cid);
					pd.setType(type);
					pd.setValue(this.jobtitle);
					this.details.add(pd);
				}
			}
		} else {
			// detail => basic
			PersonDetailType type = PersonDetailType.D_NAME;
			if (type2Details.containsKey(type)) {
				this.name = type2Details.get(type).getValue();
			}
			type = PersonDetailType.D_CELLPHONE;
			if (type2Details.containsKey(type)) {
				this.cellphone = type2Details.get(type).getValue();
			}
			type = PersonDetailType.D_GENDAR;
			if (type2Details.containsKey(type)) {
				this.gendar = Gendar.parseString2Gendar(type2Details.get(type).getValue());
			}
			type = PersonDetailType.D_AVATAR;
			if (type2Details.containsKey(type)) {
				this.avatar = type2Details.get(type).getValue();
			}
			type = PersonDetailType.D_BIRTHDAY;
			if (type2Details.containsKey(type)) {
				this.birthday = type2Details.get(type).getValue();
			}
			type = PersonDetailType.D_EMPLOYER;
			if (type2Details.containsKey(type)) {
				this.employer = type2Details.get(type).getValue();
			}
			type = PersonDetailType.D_JOBTITLE;
			if (type2Details.containsKey(type)) {
				this.jobtitle = type2Details.get(type).getValue();
			}
		}
	}

	/**
	 * refresh circle member's detail info
	 * 
	 * @return
	 */
	public RetError refresh() {
		return refresh(0L);
	}

	/**
	 * refresh circle member's detail info from start time
	 * 
	 * @param start
	 * @return
	 */
	public RetError refresh(long start) {
		IParser parser = new CircleMemberDetailParser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cid", cid);
		params.put("pid", pid);
		params.put("start", start);
		Result ret = ApiRequest.requestWithToken(CircleMember.DETAIL_API,
				params, parser);

		if (ret.getStatus() == RetStatus.SUCC) {
			if (this.updateDetails((CircleMember)ret.getData())) {
				this.syncBasicAndDetail(false);
				this.status = Status.UPDATE;
			}
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}

	/**
	 * get and update basic info of a member
	 * 
	 * @return
	 */
	public RetError refreshBasic() {
		IParser parser = new CircleMemberBasicParser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cid", cid);
		params.put("pid2", pid);
		if (uid != 0) {
			params.put("uid2", uid);
		}
		Result ret = ApiRequest.requestWithToken(CircleMember.BASIC_API,
				params, parser);

		if (ret.getStatus() == RetStatus.SUCC) {
			this.update(ret.getData());
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}

	@SuppressLint("UseSparseArrays")
	protected JSONArray getChangedDetails(CircleMember another) {
		Map<Integer, PersonDetail> olds = new HashMap<Integer, PersonDetail>();
		for (PersonDetail pp : this.details) {
			olds.put(pp.getId(), pp);
		}

		JSONArray jsonArr = new JSONArray();
		Map<Integer, PersonDetail> news = new HashMap<Integer, PersonDetail>();
		for (PersonDetail pd : another.details) {
			int propid = pd.getId();
			news.put(propid, pd);

			if (olds.containsKey(propid)) {
				if (!pd.equals(olds.get(propid))) {
					// edit
					try {
						JSONObject jsonObj = pd.toJson();
						jsonObj.put("op", "edit");
						jsonArr.put(jsonObj);
					} catch (Exception e) {
					}
				}
			} else {
				// new
				try {
					JSONObject jsonObj = pd.toJson();
					jsonObj.put("op", "new");
					jsonArr.put(jsonObj);
				} catch (Exception e) {
				}
			}
		}

		for (PersonDetail pd : this.details) {
			if (!news.containsKey(pd.getId())) {
				// del
				try {
					JSONObject jsonObj = pd.toJson();
					jsonObj.put("op", "del");
					jsonArr.put(jsonObj);
				} catch (Exception e) {
				}
			}
		}

		return jsonArr;
	}

	protected void updateForEditInfo(CircleMember another,
			JSONArray changedDetails, List<Object> ret) {
		if (changedDetails.length() > 0
				&& changedDetails.length() == ret.size()) {

			List<PersonDetail> details = new ArrayList<PersonDetail>();
			for (int i = 0; i < changedDetails.length(); i++) {
				int propid = (Integer) ret.get(i);
				if (propid > 0) {
					try {
						PersonDetail pd = new PersonDetail(propid, cid);
						JSONObject jobj = (JSONObject) changedDetails.opt(i);
						pd.setType(PersonDetailType.convertToType(jobj
								.getString("t")));
						pd.setValue(jobj.getString("v"));
						if (jobj.has("start")) {
							pd.setStart(jobj.getString("start"));
						}
						if (jobj.has("end")) {
							pd.setEnd(jobj.getString("end"));
						}
						if (jobj.has("remark")) {
							pd.setRemark(jobj.getString("remark"));
						}
						String op = jobj.getString("op");
						if ("new".equals(op)) {
							pd.setStatus(Status.NEW);
						} else if ("mod".equals(op)) {
							pd.setStatus(Status.UPDATE);
						} else if ("del".equals(op)) {
							pd.setStatus(Status.DEL);
						}
						details.add(pd);
					} catch (JSONException e) {
					}
				}
			}
			another.setDetails(details);

			if (this.updateDetails(another)) {
				this.syncBasicAndDetail(false);
				this.status = Status.UPDATE;
			}
		} else {
			// size not equal???
		}
	}
	
	/**
	 * upload edit info to server, and update local data while upload success
	 * 
	 * @param another
	 * @return
	 */
	public RetError uploadAfterEdit(CircleMember another) {
		JSONArray changedDetails = getChangedDetails(another);
		if (changedDetails.length() == 0) {
			return RetError.NONE;
		}

		IParser parser = new ArrayParser("details");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cid", another.cid);
		params.put("pid", another.pid);
		params.put("person", changedDetails.toString());

		ArrayResult ret = (ArrayResult) ApiRequest.requestWithToken(
				CircleMember.EDIT_API, params, parser);
		if (ret.getStatus() == RetStatus.SUCC) {
			updateForEditInfo(another, changedDetails, ret.getArrs());
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}

	protected boolean isAvatarChanged(String avatar) {
		return avatar != null && avatar.length() > 0
				&& !this.avatar.equals(avatar);
	}

	/**
	 * upload new avatar to server, and update local avatar while upload success
	 * 
	 * @param avatar
	 * @return
	 */
	public RetError uploadAvatar(String avatar) {
		if (!isAvatarChanged(avatar)) {
			return RetError.NONE;
		}

		IParser parser = new StringParser("avatar");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cid", cid);
		params.put("pid", pid);
		params.put("avatar", avatar);
		StringResult ret = (StringResult) ApiRequest.requestWithToken(
				CircleMember.UPLOAD_AVATAR_API, params, parser);

		if (ret.getStatus() == RetStatus.SUCC) {
			this.avatar = ret.getStr();
			this.status = Status.UPDATE; // TODO change local?
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}
	
	
	/**
	 * get circle member, first read member info from db, 
	 * if not in db, refresh from server.
	 * 
	 * @param cid
	 * @param pid
	 * @param uid
	 * @param db
	 * @return
	 */
	public static CircleMember getUser(int cid, int pid, int uid, SQLiteDatabase db) {
		CircleMember cm = new CircleMember(cid, pid, uid);
		cm.read(db);
		if (cm.getUid() == 0 && cm.getPid() == 0) {
			cm.refreshBasic();
			cm.write(db);
		}
		return cm;
	}
	
	/**
	 * quit from the circle
	 * 
	 * @return
	 */
	public RetError quit() {
		int uid = Integer.parseInt(Global.getUid());
		if (uid != this.uid
				|| !CircleMemberState.isInCircle(this.state)) {
			return RetError.UNVALID;
		}

		IParser parser = new SimpleParser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cid", cid);
		SimpleResult ret = (SimpleResult) ApiRequest.requestWithToken(
				CircleMember.QUIT_API, params, parser);

		if (ret.getStatus() == RetStatus.SUCC) {
			this.state = CircleMemberState.STATUS_QUIT;
			this.status = Status.UPDATE;

			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}
	
	/**
	 * accept the invitation for the circle
	 * 
	 * @return
	 */
	public RetError acceptInvitation() {
		int uid = Integer.parseInt(Global.getUid());
		if (uid != this.uid
				|| (CircleMemberState.STATUS_INVITING != this.state)) {
			return RetError.UNVALID;
		}

		IParser parser = new StringParser("auth");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cid", cid);
		StringResult ret = (StringResult) ApiRequest.requestWithToken(
				CircleMember.ACCETP_INVITATION_API, params, parser);

		if (ret.getStatus() == RetStatus.SUCC) {
			int auth = Integer.parseInt(ret.getStr());
			this.state = auth > 0 ? CircleMemberState.STATUS_VERIFIED
					: CircleMemberState.STATUS_ENTER_AND_VERIFYING;
			this.status = Status.UPDATE;

			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}
	
	/**
	 * refuse the invitation for the circle
	 * 
	 * @return
	 */
	public RetError refuseInvitation() {
		int uid = Integer.parseInt(Global.getUid());
		if (uid != this.uid
				|| (CircleMemberState.STATUS_INVITING != this.state)) {
			return RetError.UNVALID;
		}

		IParser parser = new SimpleParser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cid", cid);
		SimpleResult ret = (SimpleResult) ApiRequest.requestWithToken(
				CircleMember.REFUSE_INVITATION_API, params, parser);

		if (ret.getStatus() == RetStatus.SUCC) {
			this.state = CircleMemberState.STATUS_REFUSED;
			this.status = Status.UPDATE;

			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}
	
	/**
	 * kickout a circle member
	 * 
	 * @return
	 */
	public RetError kickout() {
		if (!CircleMemberState.isInCircle(this.state)
				|| (CircleMemberState.STATUS_KICKOFFING == this.state)) {
			return RetError.UNVALID;
		}

		IParser parser = new StringParser("auth");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cid", cid);
		params.put("pid", pid);
		StringResult ret = (StringResult) ApiRequest.requestWithToken(
				CircleMember.KICKOUT_API, params, parser);

		if (ret.getStatus() == RetStatus.SUCC) {
			int auth = Integer.parseInt(ret.getStr());
			this.state = auth > 0 ? CircleMemberState.STATUS_KICKOUT
					: CircleMemberState.STATUS_KICKOFFING;
			this.status = Status.UPDATE;

			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}

}
