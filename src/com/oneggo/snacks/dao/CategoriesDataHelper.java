package com.oneggo.snacks.dao;

import java.util.ArrayList;
import java.util.List;

import com.oneggo.snacks.dao.Tables.DBHelper;
import com.oneggo.snacks.datatype.Category;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

public class CategoriesDataHelper extends BaseDataHelper{

	public CategoriesDataHelper(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Uri getContentUri() {
		// TODO Auto-generated method stub
		return CategoriesProvider.CATEGORIES_CONTENT_URI;
	}
	
	private ContentValues getContentValues(Category category) {
		ContentValues values = new ContentValues();
		values.put(Tables.CategoriesDBInfo.ID, category.getId());
		values.put(Tables.CategoriesDBInfo.PID, category.getPid());
		values.put(Tables.CategoriesDBInfo.JSON, category.toJson());
		return values;
	}
	
	public Cursor query() {
		Cursor cursor = query(null, Tables.CategoriesDBInfo.PID + "=0", null, null);
		return cursor;
	}
	
	public Category query(long id) {
		Category category = null;
		Cursor cursor = query(null, null, null, null);
		if(cursor.moveToFirst()){
			category = Category.fromCursor(cursor);
		}
		cursor.close();
		return category;
	}
	
	public Cursor sonCategories(long pid) {
		Cursor cursor = null;
		cursor = query(null, Tables.CategoriesDBInfo.PID + "=" + String.valueOf(pid), null, null);
		return cursor;
	}
	
	public void bulkInsert(List<Category> categories) {
		ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
		for(Category category : categories){
			ContentValues values = getContentValues(category);
			contentValues.add(values);
		}
		
		ContentValues[] valueArray = new ContentValues[contentValues.size()];
		bulkInsert(contentValues.toArray(valueArray));
	}
	
	public int deleteAll() {
		synchronized (CategoriesProvider.DBLock) {
			DBHelper mDBHelper = Tables.getDBHelper();
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			int row = db.delete(Tables.CategoriesDBInfo.TABLE_NAME, null, null);
			return row;
		}
	}
	
	public CursorLoader getCursorLoader() {
		return new CursorLoader(getContext(), getContentUri(), null, Tables.CategoriesDBInfo.PID + "=0", null, null);
	}
}
