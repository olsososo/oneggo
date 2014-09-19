package com.oneggo.snacks.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.oneggo.snacks.AppData;
import com.oneggo.snacks.database.Column;
import com.oneggo.snacks.database.SQLiteTable;
import com.oneggo.snacks.database.Column.Constraint;

public class Tables {
	
	public static DBHelper mDBHelper;
	
	public static DBHelper getDBHelper() {
		if(mDBHelper == null) {
			mDBHelper = new DBHelper(AppData.getContext());
		}
		
		return mDBHelper;
	}
	
	public static final class CategoriesDBInfo implements BaseColumns {
		private CategoriesDBInfo() {	
		}
		
		public static final String TABLE_NAME = "categories";
		
		public static final String ID = "id";
		
		public static final String PID = "pid";
		
		public static final String JSON = "json";
		
		public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME)
			.addColumn(ID, Constraint.UNIQUE, Column.DataType.INTEGER)
			.addColumn(PID, Column.DataType.INTEGER)
			.addColumn(JSON, Column.DataType.TEXT);
	}
	
	public static final class SubjectsDBInfo implements BaseColumns {

		public SubjectsDBInfo() {
		}
		
		public static final String TABLE_NAME = "subjects";
		
		public static final String ID = "id";
		
		public static final String JSON = "json";
		
		public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME)
			.addColumn(ID, Constraint.UNIQUE, Column.DataType.INTEGER)
			.addColumn(JSON, Column.DataType.TEXT);
	}
	
	public static final class ProductsDBInfo implements BaseColumns {
		private ProductsDBInfo() {
			
		}
		
		public static final String TABLE_NAME = "products";
		
		public static final String ID = "id";
		
		public static final String PID = "pid";
		
		public static final String CATEGORY = "category";
		
		public static final String JSON = "json";
		
		public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME)
				.addColumn(ID, Constraint.UNIQUE, Column.DataType.INTEGER)
				.addColumn(PID, Column.DataType.INTEGER)
				.addColumn(CATEGORY, Column.DataType.INTEGER)
				.addColumn(JSON, Column.DataType.TEXT);
	} 
	
	public static class DBHelper extends SQLiteOpenHelper {
		
		private static final String DB_NAME = AppData.DBNAME;
		
		private static final int VERSION = 1;
		
		public DBHelper(Context context) {
			super(context, DB_NAME, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			CategoriesDBInfo.TABLE.create(db);
			ProductsDBInfo.TABLE.create(db);
			SubjectsDBInfo.TABLE.create(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
	}
}
