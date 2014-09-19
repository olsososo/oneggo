package com.oneggo.snacks.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.oneggo.snacks.AppData;
import com.oneggo.snacks.datatype.Subject;

public class SubjectsDataHelper {

	public static final Object DBLock = new Object();
	
	public ArrayList<Subject> query() {
		synchronized(DBLock){
			ArrayList<Subject> subjects = new ArrayList<Subject>();
			SQLiteDatabase db = Tables.getDBHelper().getReadableDatabase();
			Cursor cursor = db.query(Tables.SubjectsDBInfo.TABLE_NAME, null, null, null, null, null, null);
			while(cursor.moveToNext()){
				Subject subject = Subject.fromJson(cursor.getString(cursor.getColumnIndex(Tables.SubjectsDBInfo.JSON)));
				subjects.add(subject);
			}
			
			return subjects;
		}
	}
	
	public int deleteAll() {
		synchronized(DBLock){
			int row = 0;
			SQLiteDatabase db = Tables.getDBHelper().getWritableDatabase();
			db.beginTransaction();
			
			try{
				row = db.delete(Tables.SubjectsDBInfo.TABLE_NAME, null, null);
				db.setTransactionSuccessful();
			}catch(Exception e){
				Log.d(AppData.TAG, e.getMessage());
			}finally{
				db.endTransaction();
			}
			
			return row;
		}
	}
	
	public void bulkInsert(ArrayList<Subject> subjects) {
		synchronized(DBLock){
			SQLiteDatabase db = Tables.getDBHelper().getWritableDatabase();
			db.beginTransaction();
			
			try{
				ContentValues values = null;
				for(Subject subject : subjects){
					values = getContentValues(subject);
					db.insert(Tables.SubjectsDBInfo.TABLE_NAME, null, values);
					Log.d(AppData.TAG, "insert:" + subject.getTitle());
				}
				
				db.setTransactionSuccessful();
			}catch(Exception e){
				Log.d(AppData.TAG, e.getMessage());
			}finally{
				db.endTransaction();
			}
		}
	}
	
	private ContentValues getContentValues(Subject subject) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(Tables.SubjectsDBInfo.ID, subject.getId());
		contentValues.put(Tables.SubjectsDBInfo.JSON, subject.toJson());
		return contentValues;
	}
}
