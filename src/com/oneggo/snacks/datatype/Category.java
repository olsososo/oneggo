package com.oneggo.snacks.datatype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;

import com.google.gson.Gson;
import com.oneggo.snacks.dao.Tables;

public class Category extends BaseType implements Serializable{
	private static final HashMap<Long, Category> CACHE = new HashMap<Long, Category>();
	
	private long id;
	
	private long pid;
	
	private String name;
	
	private String photo;
	
	private String description;
	
	private static void addToCache(Category category) {
		CACHE.put(category.getId(), category);
	}
	
	private static Category getFromCache(long id) {
		return CACHE.get(id);
	}
	
	public static Category fromJson(String json) {
		return new Gson().fromJson(json, Category.class);
	}
	
	public static Category fromCursor(Cursor cursor) {
		long id = cursor.getLong(cursor.getColumnIndex(Tables.CategoriesDBInfo.ID));
		Category category = getFromCache(id);
		if(category != null) {
			return category;
		} 
		
		category = new Gson().fromJson(
				cursor.getString(cursor.getColumnIndex(Tables.CategoriesDBInfo.JSON)), 
				Category.class);
		addToCache(category);
		return category;
	}
	
	public long getId() {
		return id;
	}
	
	public long getPid() {
		return pid;
	}
	
	public String getName() {
		return name;
	}

	public String getPhoto() {
		return photo;
	}

	public String getDescription() {
		return description;
	}
	
	public static class RequestData {
		private ArrayList<Category> categories;
		
		public ArrayList<Category> getCategories() {
			return categories;
		}
		
	}
}
