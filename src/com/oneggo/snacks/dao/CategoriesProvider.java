package com.oneggo.snacks.dao;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class CategoriesProvider extends ContentProvider{
	private static final String TAG = "CategoriesProvider";
	
	public static final Object DBLock = new Object();
	
	public static final String AUTHORITY = "com.oneggo.snacks.categories_provider";
	
	public static final String SCHEME = "content://";
	
	public static final String PATH_CATEGORIES = "/categories";
	
	public static final Uri CATEGORIES_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_CATEGORIES);
	
	private static final int CATEGORIES = 0;
	
	public static final String CATEGORIES_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.shop.category";
	
	private static final UriMatcher sUriMatcher;
	
	static{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, "categories", CATEGORIES);
	}
	
	private String matchTable(Uri uri) {
		String table = null;
		switch(sUriMatcher.match(uri)){
		case CATEGORIES:
			table = Tables.CategoriesDBInfo.TABLE_NAME;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		return table;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch(sUriMatcher.match(uri)){
			case CATEGORIES:
				return CATEGORIES_CONTENT_TYPE;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
		// TODO Auto-generated method stub
		synchronized (DBLock) {
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			String table = matchTable(uri);
			queryBuilder.setTables(table);
			
			SQLiteDatabase db = Tables.getDBHelper().getReadableDatabase();
			Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
			
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
		}
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) throws SQLiteException {
		synchronized (DBLock) {
			String table = matchTable(uri);
			SQLiteDatabase db = Tables.getDBHelper().getWritableDatabase();
			
			long rowId = 0;
			db.beginTransaction();
			try{
				rowId = db.insert(table, null, values);
				db.setTransactionSuccessful();
			}catch(Exception e){
				Log.e(TAG, e.getMessage());
			}finally{
				db.endTransaction();
			}
			
			if(rowId > 0){
				Uri returnUri = ContentUris.withAppendedId(uri, rowId);
				getContext().getContentResolver().notifyChange(uri, null);
				return returnUri;
			}
			
			throw new SQLiteException("Failed to insert row into " + uri);
		}
	}

	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		// TODO Auto-generated method stub
		synchronized (DBLock) {
			String table = matchTable(uri);
			SQLiteDatabase db = Tables.getDBHelper().getWritableDatabase();
			db.beginTransaction();
			try{
				for(ContentValues contentValues : values){
					db.insertWithOnConflict(table, BaseColumns._ID, contentValues, 
							SQLiteDatabase.CONFLICT_IGNORE);
				}
				
				db.setTransactionSuccessful();
				getContext().getContentResolver().notifyChange(uri, null);
				return values.length;
			}catch(Exception e){
				Log.e(TAG, e.getMessage());
			}finally{
				db.endTransaction();
			}
			throw new SQLiteException("Failed to insert row into " + uri);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		synchronized (DBLock) {
			SQLiteDatabase db = Tables.getDBHelper().getWritableDatabase();
			
			int count = 0;
			String table = matchTable(uri);
			
			db.beginTransaction();
			try{
				count = db.delete(table, selection, selectionArgs);
				db.setTransactionSuccessful();
			}finally{
				db.endTransaction();
			}
			
			getContext().getContentResolver().notifyChange(uri, null);
			return count;
		}
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		synchronized (DBLock) {
			SQLiteDatabase db = Tables.getDBHelper().getWritableDatabase();
			String table = matchTable(uri);
			int count = 0;
			
			db.beginTransaction();
			try{
				db.update(table, values, selection, selectionArgs);
				db.setTransactionSuccessful();
			}finally{
				db.endTransaction();
			}
			
			getContext().getContentResolver().notifyChange(uri, null);
			return count;
		}
	}
}
