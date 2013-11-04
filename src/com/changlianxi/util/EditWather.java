package com.changlianxi.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * 手机号码分段显示
 * 
 * @author teeker_bin
 * 
 */
public class EditWather implements TextWatcher {
	private EditText edit;

	public EditWather(EditText edit) {
		this.edit = edit;
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		StringBuffer sb = new StringBuffer(s);
		if (count == 1) {
			if (s.length() == 4) {
				sb.insert(3, "-");
				edit.setText(sb.toString());
				edit.setSelection(5);
			}
			if (s.length() == 9) {
				sb.insert(8, "-");
				edit.setText(sb.toString());
				edit.setSelection(10);
			}

		} else if (count == 0) {
			if (s.length() == 4) {
				edit.setText(s.subSequence(0, s.length() - 1));
				edit.setSelection(3);
			}
			if (s.length() == 9) {
				edit.setText(s.subSequence(0, s.length() - 1));
				edit.setSelection(8);
			}

		}
	}

}
