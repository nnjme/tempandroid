package com.changlianxi.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.GrowthCommentParser;
import com.changlianxi.data.parser.GrowthImageParser;
import com.changlianxi.data.parser.GrowthParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.parser.SimpleParser;
import com.changlianxi.data.parser.StringParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.data.request.StringResult;
import com.changlianxi.db.Const;

/**
 * circle growth
 *
 * Usage:
 * 
 * get a growth info:
 *     // new growth
 *     growth.read();
 *     // growth.get***()
 * 
 * refresh a growth's detail info:
 *     // new growth
 *     growth.read()
 *     growth.refresh(); // request and merge with local data
 *     growth.write();
 *     
 * 
 * upload after edit:
 *    // new growth1
 *    // ...edit...
 *    // new growth2 after edit
 *    growth1.uploadAfterEdit(growth2);
 *    growth1.write();    
 * 
 * upload images:
 *    // new growth
 *    // ...edit images...
 *    // new images list
 *    growth.uploadImages(imageList);
 *    growth.write();
 *    
 * add new growth
 *    // new growth
 *    // ...set growth info...
 *    growth.uploadForAdd();
 *    // new images list
 *    growth.uploadImages(imageList);
 *    growth.write();
 *    
 * delete growth:
 *    // new growth
 *    // ...set growth info...
 *    growth.uploadForDel();
 *    growth.write();
 *    
 * praise growth:
 *    // new growth
 *    // ...
 *    growth.uploadMyPraise(false); // cancel: growth.uploadMyPraise(true);
 *    growth.write();
 *    
 *    
 * @author nnjme
 * 
 */
public class Growth extends AbstractData {
	public final static String DETAIL_API = "growth/idetail";
	public final static String EDIT_API = "growth/igrowth";
	public final static String UPLOAD_IMAGE_API = "growth/iuploadImage";
	public final static String REMOVE_IMAGE_API = "growth/iremoveImage";
	public final static String ADD_API = "growth/igrowth";
	public final static String REMOVE_API = "growth/iremoveGrowth";	
	public final static String PRAISE_API = "growth/imyPraise";	
	public final static String CANCEL_PRAISE_API = "growth/icancelPraise";
	public final static String COMMENT_API = "growth/iMyComment";
	
	private int id = 0;
	private int cid = 0;
	private int publisher = 0;
	private String content = "";
	private String location = "";
	private String happened = ""; // happen time
	private String published = ""; // publish time
	private int praiseCnt = 0;
	private int commentCnt = 0;
	private boolean isPraised = false; // do I praised this growth
	private List<GrowthImage> images = new ArrayList<GrowthImage>();
	private GrowthCommentList commentList = null;

	
	public Growth(int cid) {
		this(cid, 0);
	}
	
	public Growth(int cid, int id) {
		this.cid = cid;
		this.id = id;
		this.commentList = new GrowthCommentList(cid, id);
	}

	public Growth(int cid, int id, int publisher, String content,
			String location, String happened, String published) {
		this.cid = cid;
		this.id = id;
		this.publisher = publisher;
		this.content = content;
		this.location = location;
		this.happened = happened;
		this.published = published;
		this.commentList = new GrowthCommentList(cid, id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPublisher() {
		return publisher;
	}

	public void setPublisher(int publisher) {
		this.publisher = publisher;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getHappened() {
		return happened;
	}

	public void setHappened(String happened) {
		this.happened = happened;
	}

	public String getPublished() {
		return published;
	}

	public void setPublished(String published) {
		this.published = published;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public int getPraiseCnt() {
		return praiseCnt;
	}

	public void setPraiseCnt(int praiseCnt) {
		this.praiseCnt = praiseCnt;
	}

	public int getCommentCnt() {
		return commentCnt;
	}

	public void setCommentCnt(int commentCnt) {
		this.commentCnt = commentCnt;
	}

	public boolean isPraised() {
		return isPraised;
	}

	public void setPraised(boolean isPraised) {
		this.isPraised = isPraised;
	}

	private void addImage(GrowthImage image) {
		if (this.images == null) {
			this.images = new ArrayList<GrowthImage>();
		}
		images.add(image);
	}

	public List<GrowthImage> getImages() {
		return images;
	}

	public void setImages(List<GrowthImage> images) {
		this.images = images;
	}

	public GrowthCommentList getCommentList() {
		return commentList;
	}

	public void setCommentList(GrowthCommentList commentList) {
		this.commentList = commentList;
	}

	@Override
	public String toString() {
		return "Growth [id=" + id + ", publisher=" + publisher + ", content="
				+ content + ", location=" + location + ", happened=" + happened
				+ ", published=" + published + ", praised=" + praiseCnt
				+ ", commented=" + commentCnt + ", images="
				+ ((images != null) ? 0 : images.size()) + "]";
	}

	@Override
	public void read(SQLiteDatabase db) {
		// read growth basic info
		Cursor cursor = db.query(Const.GROWTH_TABLE_NAME, new String[] { "cid",
				"publisher", "content", "location", "happened", "published",
				"praiseCnt", "commentCnt", "ispraised" }, "id=?",
				new String[] { this.id + "" }, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			int cid = cursor.getInt(cursor.getColumnIndex("cid"));
			int publisher = cursor.getInt(cursor.getColumnIndex("publisher"));
			String content = cursor.getString(cursor.getColumnIndex("content"));
			String location = cursor.getString(cursor
					.getColumnIndex("location"));
			String happened = cursor.getString(cursor
					.getColumnIndex("happened"));
			String published = cursor.getString(cursor
					.getColumnIndex("published"));
			int praiseCnt = cursor.getInt(cursor.getColumnIndex("praiseCnt"));
			int commentCnt = cursor.getInt(cursor.getColumnIndex("commentCnt"));
			int isPraised = cursor.getInt(cursor.getColumnIndex("isPraised"));

			this.cid = cid;
			this.publisher = publisher;
			this.content = content;
			this.location = location;
			this.happened = happened;
			this.published = published;
			this.praiseCnt = praiseCnt;
			this.commentCnt = commentCnt;
			this.isPraised = (isPraised > 0);
		}
		cursor.close();

		// read growth images
		List<GrowthImage> images = new ArrayList<GrowthImage>();
		Cursor cursor2 = db.query(Const.GROWTH_IMAGE_TABLE_NAME, new String[] {
				"imgId", "img" }, "gid=?", new String[] { this.id + "" }, null,
				null, null);
		if (cursor2.getCount() > 0) {
			cursor2.moveToFirst();
			for (int i = 0; i < cursor2.getCount(); i++) {
				int imgId = cursor2.getInt(cursor.getColumnIndex("imgId"));
				String img = cursor2.getString(cursor.getColumnIndex("img"));

				GrowthImage imgage = new GrowthImage(cid, id, imgId, img);
				images.add(imgage);
			}
		}
		cursor2.close();
		this.setImages(images);

		// set status
		this.status = Status.OLD;
	}
	
	@Override
	public void write(SQLiteDatabase db) {
		String dbName = Const.GROWTH_TABLE_NAME;
		if (this.status == Status.OLD) {
			return;
		}
		if (this.status == Status.DEL) {
			db.delete(dbName, "id=?", new String[] { id + "" });

			// delete images
			for (GrowthImage gImage : this.images) {
				gImage.setStatus(Status.DEL);
				gImage.write(db);
			}

			return;
		}

		ContentValues cv = new ContentValues();
		cv.put("cid", cid);
		cv.put("id", id);
		cv.put("publisher", publisher);
		cv.put("content", content);
		cv.put("location", location);
		cv.put("happened", happened);
		cv.put("published", published);
		cv.put("praiseCnt", praiseCnt);
		cv.put("commentCnt", commentCnt);
		cv.put("isPraised", isPraised ? 1 : 0);

		if (this.status == Status.NEW) {
			db.insert(dbName, null, cv);
		} else if (this.status == Status.UPDATE) {
			db.update(dbName, cv, "id=?", new String[] { id + "" });
		}

		// write images
		for (GrowthImage gImage : this.images) {
			gImage.write(db);
		}

		this.status = Status.OLD;
	}
	
	@Override
	public void update(IData data) {
		if (!(data instanceof Growth)) {
			return;
		}
		Growth another = (Growth) data;
		boolean isChange = false;
		if (this.cid != another.cid) {
			this.cid = another.cid;
			isChange = true;
		}
		if (this.id != another.id) {
			this.id = another.id;
			isChange = true;
		}
		if (this.publisher != another.publisher) {
			this.publisher = another.publisher;
			isChange = true;
		}
		if (!this.content.equals(another.content)) {
			this.content = another.content;
			isChange = true;
		}
		if (!this.location.equals(another.location)) {
			this.location = another.location;
			isChange = true;
		}
		if (!this.happened.equals(another.happened)) {
			this.happened = another.happened;
			isChange = true;
		}
		if (!this.published.equals(another.published)) {
			this.published = another.published;
			isChange = true;
		}
		if (this.praiseCnt != another.praiseCnt) {
			this.praiseCnt = another.praiseCnt;
			isChange = true;
		}
		if (this.commentCnt != another.commentCnt) {
			this.commentCnt = another.commentCnt;
			isChange = true;
		}
		if (this.isPraised != another.isPraised) {
			this.isPraised = another.isPraised;
			isChange = true;
		}
		if (updateImages(another)) {
			isChange = true;
		}
		
		if (isChange && this.status == Status.OLD) {
			this.status = Status.UPDATE;
		}
	}

	@SuppressLint("UseSparseArrays")
	private boolean updateImages(Growth another) {
		boolean isChange = false;
		Map<Integer, GrowthImage> olds = new HashMap<Integer, GrowthImage>();
		Map<Integer, GrowthImage> news = new HashMap<Integer, GrowthImage>();
		for (GrowthImage gImage : this.images) {
			olds.put(gImage.getImgId(), gImage);
		}
		for (GrowthImage gImage : another.images) {
			int imgId = gImage.getImgId();
			news.put(imgId, gImage);
			if (olds.containsKey(imgId)) {
				olds.get(imgId).update(gImage);
				if (olds.get(imgId).getStatus() == Status.UPDATE) {
					isChange = true;
				}
			} else {
				// new
				this.images.add(gImage);
				isChange = true;
			}
		}
		for (GrowthImage gImage : this.images) {
			if (!news.containsKey(gImage.getImgId())) {
				gImage.setStatus(Status.DEL);
				isChange = true;
			}
		}
		return isChange;
	}
	
	private void updateForEditInfo(Growth another) {
		boolean isChange = false;
		if (!this.content.equals(another.content)) {
			this.content = another.content;
			isChange = true;
		}
		if (!this.location.equals(another.location)) {
			this.location = another.location;
			isChange = true;
		}
		if (!this.happened.equals(another.happened)) {
			this.happened = another.happened;
			isChange = true;
		}		

		if (isChange && this.status == Status.OLD) {
			this.status = Status.UPDATE;
		}
	}

	/**
	 * refresh this growth info from server
	 */
	public RetError refresh() {
		return this.refresh(id);
	}

	/**
	 * refresh growth info with id from server
	 * 
	 * @param id
	 */
	public RetError refresh(int id) {
		IParser parser = new GrowthParser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cid", cid);
		params.put("gid", id);
		Result ret = ApiRequest.requestWithToken(Growth.DETAIL_API, params,
				parser);

		if (ret.getStatus() == RetStatus.SUCC) {
			this.update(ret.getData());
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}

	private boolean isReallyChangedForUpload(Growth another) {
		return (another.id != this.id)
				|| (!another.content.equals(this.content))
				|| (!another.location.equals(this.location))
				|| (!another.happened.equals(this.happened));
	}

	/**
	 * upload edit info to server, and update local data while upload success
	 * 
	 * @param another
	 * @return
	 */
	public RetError uploadAfterEdit(Growth another) {
		if (!isReallyChangedForUpload(another)) {
			return RetError.NONE;
		}
		
		IParser parser = new StringParser("gid");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cid", another.getCid());
		params.put("gid", another.getId());
		params.put("content", another.getContent());
		params.put("location", another.getLocation());
		params.put("time", another.getPublished());

		StringResult ret = (StringResult) ApiRequest.requestWithToken(
				Growth.EDIT_API, params, parser);
		if (ret.getStatus() == RetStatus.SUCC) {
			updateForEditInfo(another);
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}
	
	@SuppressLint("UseSparseArrays")
	public void uploadImages(List<GrowthImage> gImages) {
		Map<Integer, GrowthImage> olds = new HashMap<Integer, GrowthImage>();
		for (GrowthImage gImage : this.images) {
			olds.put(gImage.getImgId(), gImage);
		}

		boolean isChange = false;
		Map<Integer, GrowthImage> news = new HashMap<Integer, GrowthImage>();
		for (GrowthImage gImage : gImages) {
			int imgId = gImage.getImgId();
			if (imgId == 0) {
				// new
			    this.uploadNewImage(gImage);
			    this.addImage(gImage);
			    isChange = true;
				continue;
			}

			news.put(imgId, gImage);
			if (olds.containsKey(imgId)) {
				// already exist
			} else {
				// new
				gImage.setStatus(Status.NEW);
				this.addImage(gImage);
				isChange = true;
			}
		}

		for (int imgId: olds.keySet()) {
			if (!news.containsKey(imgId)) {
				// del
				this.uploadDelImage(olds.get(imgId));
				isChange = true;
			}
		}
		
		if (isChange && this.status == Status.OLD) {
			this.status = Status.UPDATE;
		}
	}

	/**
	 * upload new growth image to server, and reset image info while upload success
	 * 
	 * @param gImage
	 * @return
	 */
	public RetError uploadNewImage(GrowthImage gImage) {
		IParser parser = new GrowthImageParser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cid", cid);
		params.put("gid", id);
		params.put("img", gImage.getImg());
		StringResult ret = (StringResult) ApiRequest.requestWithToken(
				Growth.UPLOAD_IMAGE_API, params, parser);

		if (ret.getStatus() == RetStatus.SUCC) {
			GrowthImage retGImage = (GrowthImage) ret.getData();
			gImage.setCid(cid);
			gImage.setGid(id);
			gImage.setImgId(retGImage.getImgId());
			gImage.setImg(retGImage.getImg());
			gImage.setStatus(Status.NEW);
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}

	/**
	 * upload growth image delete info to server
	 * 
	 * @param gImage
	 * @return
	 */
	public RetError uploadDelImage(GrowthImage gImage) {
		IParser parser = new SimpleParser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cid", cid);
		params.put("gid", id);
		params.put("img", gImage.getImg());
		StringResult ret = (StringResult) ApiRequest.requestWithToken(
				Growth.REMOVE_IMAGE_API, params, parser);

		if (ret.getStatus() == RetStatus.SUCC) {
			gImage.setStatus(Status.DEL);
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}

	/**
	 * upload new growth to server, and reset id while upload success
	 * 
	 * @return
	 */
	public RetError uploadForAdd() {
		IParser parser = new StringParser("gid");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cid", cid);
		params.put("gid", 0);
		params.put("content", content);
		params.put("location", location);
		params.put("time", happened);

		StringResult ret = (StringResult) ApiRequest.requestWithToken(
				Growth.ADD_API, params, parser);
		if (ret.getStatus() == RetStatus.SUCC) {
			this.id = Integer.parseInt(ret.getStr());
			this.status = Status.UPDATE;
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}

	/**
	 * upload growth delete info to server
	 * 
	 * @return
	 */
	public RetError uploadForDel() {
		IParser parser = new SimpleParser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cid", cid);
		params.put("gid", id);

		StringResult ret = (StringResult) ApiRequest.requestWithToken(
				Growth.REMOVE_API, params, parser);
		if (ret.getStatus() == RetStatus.SUCC) {
			this.status = Status.DEL;
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}

	/**
	 * upload my praise or cancel praise info to server
	 * 
	 * @return
	 */
	public RetError uploadMyPraise(boolean isCancel) {
		IParser parser = new StringParser("count");
		if ((!isCancel && this.isPraised) || (isCancel && !this.isPraised)) {
			return RetError.NONE;
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cid", cid);
		params.put("gid", id);

		String api = !isCancel ? Growth.PRAISE_API : Growth.CANCEL_PRAISE_API;
		StringResult ret = (StringResult) ApiRequest.requestWithToken(api,
				params, parser);
		if (ret.getStatus() == RetStatus.SUCC) {
			String count = ret.getStr();
			this.praiseCnt = Integer.parseInt(count);
			this.status = Status.UPDATE;
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}

	/**
	 * upload my praise or cancel praise info to server
	 * 
	 * @return
	 */
	public RetError uploadMyComment(GrowthComment comment) {
		IParser parser = new GrowthCommentParser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cid", cid);
		params.put("gid", id);
		params.put("content", comment.getContent());
		params.put("replyid", comment.getReplyid());

		StringResult ret = (StringResult) ApiRequest.requestWithToken(
				Growth.COMMENT_API, params, parser);
		if (ret.getStatus() == RetStatus.SUCC) {
			GrowthComment newComent = (GrowthComment) ret.getData();
			this.commentList.addComment(newComent);
			this.commentCnt = newComent.getTotal();

			this.status = Status.UPDATE;
			return RetError.NONE;
		} else {
			return ret.getErr();
		}
	}
	
	// TODO comments for me
}
