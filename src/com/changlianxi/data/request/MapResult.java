package com.changlianxi.data.request;

import java.util.HashMap;
import java.util.Map;

public class MapResult extends Result {
	private Map<String, Object> maps = new HashMap<String, Object>();

	public MapResult() {
	}

	public MapResult(Map<String, Object> maps) {
		this.setArrs(maps);
	}

	public Map<String, Object> getArrs() {
		return maps;
	}

	public void setArrs(Map<String, Object> maps) {
		if (maps != null) {
			this.maps = maps;
		}
	}

	@Override
	public String toString() {
		return "ArrayResult [data=" + maps + ", status=" + status + ", err="
				+ err + "]";
	}
}
