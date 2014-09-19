package com.oneggo.snacks.dao;

import com.oneggo.snacks.dao.Tables.ProductsDBInfo;
import com.oneggo.snacks.database.DatabaseUtils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class ProductsProvider extends ContentProvider{
	
	private static final String TAG = "ProductsProvider";
	
	public static final Object DBLock = new Object();
	
	public static final String AUTHORITY = "com.oneggo.snacks.products_provider";
	
	public static final String SCHEME = "content://";
	
	public static final String PATH_PRODUCTS = "/products";
	
	public static final Uri PRODUCTS_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_PRODUCTS);
	
	private static final int PRODUCTS = 0;
	
	public static final String PRODUCTS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.shop.product";
	
	private static final UriMatcher sUriMatcher;
	
	static{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, "products", PRODUCTS);
	}
	
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch(sUriMatcher.match(uri)){
		case PRODUCTS:
			return PRODUCTS_CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	
	private String matchTable(Uri uri) {
		String table = null;
		switch(sUriMatcher.match(uri)){
		case PRODUCTS:
			table = Tables.ProductsDBInfo.TABLE_NAME;
		break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return table;
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
		synchronized(DBLock){
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			String table = matchTable(uri);
			queryBuilder.setTables(table);
			
			SQLiteDatabase db = Tables.getDBHelper().getReadableDatabase();
			Cursor cursor = queryBuilder.query(db, 
					projection, 
					selection, 
					selectionArgs, 
					null, 
					null,
					sortOrder
					);
			
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
		}
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) throws SQLException {
		// TODO Auto-generated method stub
		synchronized(DBLock){
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
			
			throw new SQLException("Failed to insert row into " + uri);
		}
	}
	
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) throws SQLException {
		// TODO Auto-generated method stub
		synchronized(DBLock){
			String table = matchTable(uri);
			SQLiteDatabase db = Tables.getDBHelper().getWritableDatabase();
			int count = 0;
			db.beginTransaction();
			
			try{
				for(ContentValues contentValues : values){
					count = DatabaseUtils.queryCount(db, table, ProductsDBInfo.ID + "=?", new String[]{
							String.valueOf(contentValues.getAsLong(Tables.ProductsDBInfo.ID))
					});
					
					if(count == 0){
						db.insert(table, null, contentValues);
					}else{
						db.update(table, contentValues, ProductsDBInfo.ID + "=?", new String[]{
							String.valueOf(contentValues.getAsLong(Tables.ProductsDBInfo.ID))
						});
					}
				}
				db.setTransactionSuccessful();
				getContext().getContentResolver().notifyChange(uri, null);
				return values.length;
			}catch(Exception e){
				Log.e(TAG, e.getMessage());
			}finally{
				db.endTransaction();
			}
			throw new SQLException("Failed to insert row into " + uri);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		synchronized(DBLock){
			int count = 0;
			String table = matchTable(uri);
			SQLiteDatabase db = Tables.getDBHelper().getWritableDatabase();
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
		synchronized(DBLock){
			int count;
			String table = matchTable(uri);
			SQLiteDatabase db = Tables.getDBHelper().getWritableDatabase();
			db.beginTransaction();
			
			try{
				count = db.update(table, values, selection, selectionArgs);
				db.setTransactionSuccessful();
			}finally{
				db.endTransaction();
			}
			
			getContext().getContentResolver().notifyChange(uri, null);
			return count;
		}
	}
}
