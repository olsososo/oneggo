package com.oneggo.snacks.datatype;

import java.util.ArrayList;

public class Msg extends BaseType{
	private long id;
	
	private String msg;
	
	private String time;
	
	private int read;
	
	private int msgtype;

	public Msg(String msg, String time, int msgtype) {
		this.msg = msg;
		this.time = time;
		this.msgtype = msgtype;
	}

	public long getId() {
		return id;
	}

	public String getMsg() {
		return msg;
	}

	public String getTime() {
		return time;
	}

	public int getRead() {
		return read;
	}

	public int getMsgtype() {
		return msgtype;
	}
	
	public static class RequestData {
		private ArrayList<Msg> msgs;

		public ArrayList<Msg> getMsgs() {
			return msgs;
		}
	}
}
