package com.oneggo.snacks.datatype;

public class Record extends BaseType{
	
	public long status_code;
	
	public String message;
	
	public long current_count;

	public String getMessage() {
		return message;
	}

	public long getCurrent_count() {
		return current_count;
	}

	public long getStatus_code() {
		return status_code;
	}
}
