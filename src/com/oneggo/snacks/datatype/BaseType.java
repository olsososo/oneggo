package com.oneggo.snacks.datatype;

import com.google.gson.Gson;

public class BaseType {
	public String toJson() {
		return new Gson().toJson(this);
	}
}
