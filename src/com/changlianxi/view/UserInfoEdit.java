package com.changlianxi.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.changlianxi.activity.R;
import com.changlianxi.db.DataBase;
import com.changlianxi.inteface.ChangeView;
import com.changlianxi.modle.Info;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.UserInfoUtils;
import com.changlianxi.util.Utils;

/**
 * �û����ϱ༭����
 * 
 * @author teeker_bin
 * 
 */
public class UserInfoEdit implements OnClickListener {
	private Context mContext;
	private View vEdit;
	private ListView listview;
	private List<Info> listData;
	private List<Info> listOldData;// �������������Ƚ�
	private MyAdapter adapter;
	private TextView titleKey;
	private Button btnFinish;
	private String title;
	private ChangeView cv;
	private int type;
	private LayoutInflater flater;
	private LinearLayout add;
	private JSONArray jsonAry;
	private JSONObject jsonObj;
	private String pid;// �û�id
	private String cid;// Ȧ��id
	private DataBase dbase = DataBase.getInstance();
	private SQLiteDatabase db;
	private String tableName;
	private Calendar cal = Calendar.getInstance();
	private ProgressDialog progressDialog;

	public UserInfoEdit(Context context) {
		this.mContext = context;
	}

	public UserInfoEdit(Context context, List<Info> listData, int type,
			String cid, String pid, String tableName) {
		db = dbase.getWritableDatabase();
		this.tableName = tableName;
		this.pid = pid;
		this.cid = cid;
		this.mContext = context;
		this.listData = listData;
		this.listOldData = listData;
		this.title = UserInfoUtils.infoTitleKey[type];
		this.type = type;
		flater = LayoutInflater.from(context);
		vEdit = flater.inflate(R.layout.user_info_editor, null);
		titleKey = (TextView) vEdit.findViewById(R.id.titlekey);
		titleKey.setText(title);
		btnFinish = (Button) vEdit.findViewById(R.id.btnfinish);
		btnFinish.setOnClickListener(this);
		listview = (ListView) vEdit.findViewById(R.id.listedit);
		View view = (View) flater.inflate(R.layout.info_edit_item_footer, null);
		add = (LinearLayout) view.findViewById(R.id.add);
		add.setOnClickListener(this);
		listview.addFooterView(view);
		adapter = new MyAdapter();
		listview.setAdapter(adapter);
		jsonAry = new JSONArray();
	}

	public View getView() {
		return vEdit;

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listData.size();
		}

		private Integer index = -1;

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			/* ����������ѧϰ�������شβ��� */
			if (Arrays.toString(UserInfoUtils.eduStr).contains(
					listData.get(position).getType())) {
				if (convertView == null) {
					holder = new ViewHolder();
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.info_edu_word__edit_item, null);
					holder.del = (ImageView) convertView
							.findViewById(R.id.imgdel);
					holder.startDate = (TextView) convertView
							.findViewById(R.id.startTime);
					holder.endDate = (TextView) convertView
							.findViewById(R.id.endTime);
					holder.contentValue = (EditText) convertView
							.findViewById(R.id.contentValue);
					holder.eduKey = (TextView) convertView
							.findViewById(R.id.edukey);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				holder.contentValue.setText(listData.get(position).getValue());
				holder.contentValue.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
						listData.get(position).setValue(s.toString());
						if (listData.get(position).getEditType() != 2) {
							listData.get(position).setEditType(3);
						}
					}
				});
				holder.startDate.setOnClickListener(new BtnClick(position));
				holder.endDate.setOnClickListener(new BtnClick(position));
				holder.del.setOnClickListener(new BtnClick(position));
				/* �������Ϊnull����ʾ��ǰ���� */
				holder.startDate.setText(listData.get(position).getStartDate()
						.equals("") ? getDate() : listData.get(position)
						.getStartDate());
				holder.endDate.setText(listData.get(position).getEndDate()
						.equals("") ? getDate() : listData.get(position)
						.getEndDate());
				return convertView;
			}
			/* ������������ѧϰ����֮��ļ��شβ��� */
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.info_edit_item, null);
				holder.del = (ImageView) convertView.findViewById(R.id.imgdel);
				holder.key = (TextView) convertView.findViewById(R.id.key);
				holder.value = (EditText) convertView.findViewById(R.id.value);
				holder.value.setTag(position);
				holder.value.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_UP) {
							index = (Integer) v.getTag();
						}
						return false;
					}
				});
				class MyTextWatcher implements TextWatcher {
					public MyTextWatcher(ViewHolder holder) {
						mHolder = holder;
					}

					private ViewHolder mHolder;

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
						if (s != null && !"".equals(s.toString())) {
							int position = (Integer) mHolder.value.getTag();
							// mData.get(position).put("list_item_inputvalue",
							// s.toString());// ��EditText���ݷ����ı��ʱ��浽data������
							Logger.debug(this, "value:" + s.toString());
							listData.get(position).setValue(s.toString());
							if (listData.get(position).getEditType() != 2) {
								listData.get(position).setEditType(3);
							}
						}
					}
				}
				holder.value.addTextChangedListener(new MyTextWatcher(holder));
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
				holder.value.setTag(position);
			}
			holder.key.setText(listData.get(position).getKey());
			String value = listData.get(position).getValue();
			if (value != null && !"".equals(value)) {
				holder.value.setText(value.toString());
			} else {
				holder.value.setText("");
			}
			holder.value.clearFocus();
			if (index != -1 && index == position) {
				holder.value.requestFocus();
			}
			holder.del.setOnClickListener(new BtnClick(position));
			return convertView;
		}
	}

	class BtnClick implements OnClickListener {
		int position;

		public BtnClick(int position) {
			this.position = position;
			Logger.debug(this, "position:" + position);
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.imgdel:
				BuildDelJson(listData.get(position).getId(),
						listData.get(position).getType(), listData
								.get(position).getValue(),
						listData.get(position).getStartDate(),
						listData.get(position).getEndDate());
				listData.remove(position);
				adapter.notifyDataSetChanged();
				break;
			case R.id.startTime:
				showDateDialog((TextView) v, position);
				break;
			case R.id.endTime:
				showDateDialog((TextView) v, position);
				break;
			default:
				break;
			}

		}

	}

	/**
	 * ����ʱ��json��
	 * 
	 */
	private void BuildDateJson(String start, String end, String type,
			String value) {
		try {
			jsonObj = new JSONObject();
			jsonObj.put("op", "new");
			jsonObj.put("id", "0");
			jsonObj.put("start", start);
			jsonObj.put("end", end);
			jsonObj.put("v", value);
			jsonObj.put("t", type);
			jsonAry.put(jsonObj);
			Logger.debug(this, jsonAry.toString());

		} catch (JSONException e) {
			Logger.error(this, e);

			e.printStackTrace();
		}
	}

	/**
	 * ��������json��
	 * 
	 * @param type
	 *            ��������
	 */
	private void BuildAddJson(String type, String value) {
		try {
			jsonObj = new JSONObject();
			jsonObj.put("op", "new");
			jsonObj.put("v", value);
			jsonObj.put("id", "0");
			jsonObj.put("t", type);
			jsonObj.put("start", "");
			jsonObj.put("end", "");
			jsonAry.put(jsonObj);
			Logger.debug(this, jsonAry.toString());
		} catch (JSONException e) {
			Logger.error(this, e);

			e.printStackTrace();
		}

	}

	/**
	 * �����޸�json��
	 * 
	 * @param value�޸�ֵ
	 * @param id
	 *            �޸����� id
	 * @param type
	 *            �޸�����
	 */
	private void BuildEditJson(String value, String id, String type) {
		try {
			jsonObj = new JSONObject();
			jsonObj.put("op", "edit");
			jsonObj.put("v", value);
			jsonObj.put("id", id == null ? "0" : id);
			jsonObj.put("t", type);
			jsonObj.put("start", "");
			jsonObj.put("end", "");
			jsonAry.put(jsonObj);
			Logger.debug(this, jsonAry.toString());
		} catch (JSONException e) {
			Logger.error(this, e);

			e.printStackTrace();
		}

	}

	/**
	 * ����ɾ��json��
	 * 
	 * @param id
	 *            ����id
	 * @param type��������
	 */
	private void BuildDelJson(String id, String type, String value,
			String start, String end) {
		try {
			jsonObj = new JSONObject();
			jsonObj.put("op", "del");
			jsonObj.put("id", id == null ? "0" : id);
			jsonObj.put("v", value);
			jsonObj.put("start", start);
			jsonObj.put("end", end);
			jsonObj.put("t", type);
			jsonAry.put(jsonObj);
			Logger.debug(this, jsonAry.toString());
		} catch (JSONException e) {
			Logger.error(this, e);

			e.printStackTrace();
		}

	}

	class ViewHolder {
		ImageView del;
		TextView key;
		EditText value;
		TextView startDate;
		TextView endDate;
		EditText contentValue;
		TextView eduKey;
	}

	public void setChangeView(ChangeView cv) {
		this.cv = cv;
	}

	private void selectAddType() {
		List<String> list = null;
		Logger.debug(this, "type:" + type);
		switch (type) {
		case 0:
			list = Arrays.asList(UserInfoUtils.basicChineseStr);
			break;
		case 1:
			list = Arrays.asList(UserInfoUtils.contacChinesetStr);
			break;
		case 2:
			list = Arrays.asList(UserInfoUtils.socialChineseStr);
			break;
		case 3:
			list = Arrays.asList(UserInfoUtils.addressChineseStr);
			break;
		case 4:
			list = Arrays.asList(UserInfoUtils.otherChineseStr);
			break;
		case 5:
			list = Arrays.asList(UserInfoUtils.eduChinesStr);
			break;
		case 6:
			list = Arrays.asList(UserInfoUtils.workChineseStr);
			break;
		default:
			break;
		}
		List<String> arrayList = new ArrayList<String>(list);
		for (int i = 0; i < listData.size(); i++) {
			if (arrayList.contains(listData.get(i).getKey())) {
				arrayList.remove(listData.get(i).getKey());
			}
		}
		showTypeDialog(arrayList.toArray(new String[arrayList.size()]));
	}

	/**
	 * ��ʾѡ�������б�
	 * 
	 * @param str
	 */
	private void showTypeDialog(final String str[]) {
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int which) {
				Info info = new Info();
				info.setKey(str[which]);
				info.setType(UserInfoUtils.convertToEnglish(str[which]));
				info.setEditType(2);
				listData.add(info);
				adapter.notifyDataSetChanged();
				if (type != 5) {
					// BuildJson(2, listData.size() - 1);
					// BuildAddJson(info.getType(), "");
				}

			}
		};
		new AlertDialog.Builder(mContext).setTitle("ѡ���������")
				.setItems(str, listener).show();

	}

	/**
	 * �޸ĺ������
	 * 
	 * @author teeker_bin
	 * 
	 */
	class EditWather implements TextWatcher {
		int position;

		public EditWather(int position) {
			this.position = position;
			Logger.debug(this, "position:" + position);
		}

		@Override
		public void afterTextChanged(Editable s) {
			Logger.debug(this, "ss:" + s.toString());
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}
	}

	// ����ѡ��Ի���� DateSet �¼�������
	class DateListener implements OnDateSetListener {
		TextView tview;
		int position;

		public DateListener(TextView view, int position) {
			this.tview = view;
			this.position = position;
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, monthOfYear);
			cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateDate(tview, position);

		}

	}

	private void showDateDialog(TextView view, int position) {
		new DatePickerDialog(mContext, new DateListener(view, position),
				cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH)).show();
	}

	// �� DatePickerDialog �رգ�����������ʾ
	private void updateDate(TextView v, int position) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
		v.setText(df.format(cal.getTime()));
		if (v.getId() == R.id.startTime) {
			listData.get(position).setStartDate(df.format(cal.getTime()));
		} else {
			listData.get(position).setEndDate(df.format(cal.getTime()));
		}
	}

	/**
	 * �첽�ύ�޸����ݵ�������
	 * 
	 */
	class SubmitTask extends AsyncTask<String, Integer, String> {
		// �ɱ䳤�������������AsyncTask.exucute()��Ӧ
		String rt = "";
		String errorCoce;

		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cid", cid);
			map.put("uid", SharedUtils.getString("uid", ""));
			map.put("pid", pid);
			map.put("token", SharedUtils.getString("token", ""));
			map.put("person", jsonAry.toString());
			Logger.debug(this, "person:" + jsonAry.toString());
			String json = HttpUrlHelper.postData(map, "/people/iedit");
			try {
				JSONObject object = new JSONObject(json);
				rt = object.getString("rt");
				if (rt.equals("1")) {
					JSONArray json1 = new JSONArray(jsonAry.toString());
					Logger.debug(this, "jsonAry:" + jsonAry.toString());
					for (int i = 0; i < json1.length(); i++) {
						if (!db.isOpen()) {
							db = dbase.getWritableDatabase();
						}
						JSONObject jsonObj = json1.optJSONObject(i);
						String op = jsonObj.getString("op");
						String t = jsonObj.getString("t");
						String v = jsonObj.getString("v");
						String id = jsonObj.getString("id");
						String start = "";
						String end = "";
						start = jsonObj.getString("start");
						end = jsonObj.getString("end");
						if (op.equals("del")) {
							db.delete(tableName, "key=?", new String[] { t });
							Logger.debug(this, tableName + "  del :" + t);
						} else if (op.equals("new")) {
							ContentValues cv = new ContentValues();
							cv.put("personID", pid);
							cv.put("key", t);
							cv.put("value", v);
							cv.put("startDate", start);
							cv.put("endDate", end);
							db.insert(tableName, null, cv);
							Logger.debug(this, "insert");
						} else if (op.equals("edit")) {
							ContentValues cv = new ContentValues();
							cv.put("value", v);
							db.update(tableName, cv, "personID=? and tID=?",
									new String[] { pid, id });
							Logger.debug(this, "update");
						}
					}
					db.close();
				} else {
					errorCoce = object.getString("err");
					return "error";
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return rt;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			if (result.equals("error")) {
				Utils.showToast(ErrorCodeUtil.convertToChines(errorCoce));
				return;
			}
			if (result.equals("1")) {
				Utils.showToast("�޸ĳɹ���");
				cv.delView();
				cv.NotifyData(listData, type);
			} else {
				Utils.showToast("�޸�ʧ�ܣ�");
			}

		}

		@Override
		protected void onPreExecute() {
			// ����������������������ʾһ���Ի�������򵥴���
			progressDialog = new ProgressDialog(mContext);
			progressDialog.show();
		}
	}

	private String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
		return df.format(new Date());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnfinish:
			for (int i = 0; i < listData.size(); i++) {
				String id = listData.get(i).getId();
				String start = listData.get(i).getStartDate();
				String end = listData.get(i).getEndDate();
				String type = listData.get(i).getType();
				String value = listData.get(i).getValue();
				int editType = listData.get(i).getEditType();
				if (editType == 2) {
					if (this.type == 5) {
						BuildDateJson(start, end, type, value);
					} else {
						BuildAddJson(type, value);
					}
				} else if (editType == 3) {
					if (listOldData.contains(value)) {
						continue;
					}
					BuildEditJson(value, id, type);

				}
			}
			new SubmitTask().execute();
			break;
		case R.id.add:
			selectAddType();
			break;
		default:
			break;
		}
	}
}
