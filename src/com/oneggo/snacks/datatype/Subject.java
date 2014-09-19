package com.oneggo.snacks.datatype;

import java.util.ArrayList;

import com.google.gson.Gson;

public class Subject extends BaseType{
	private long id;
	
	private String title;
	
	private String description;
	
	private String photo;

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getPhoto() {
		return photo;
	}
	
	public static Subject fromJson(String json) {
		return new Gson().fromJson(json, Subject.class);
	}
	
	public static class RequestData{
		private ArrayList<Subject> subjects;

		public ArrayList<Subject> getSubjects() {
			return subjects;
		}
	}
}
