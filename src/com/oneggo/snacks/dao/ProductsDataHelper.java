package com.oneggo.snacks.dao;

import java.util.ArrayList;
import java.util.List;

import com.oneggo.snacks.AppData;
import com.oneggo.snacks.dao.Tables.DBHelper;
import com.oneggo.snacks.dao.Tables.ProductsDBInfo;
import com.oneggo.snacks.datatype.Category;
import com.oneggo.snacks.datatype.Product;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.util.Log;

public class ProductsDataHelper extends BaseDataHelper{
	
	private Category mCategory;
	
	public ProductsDataHelper(Context context) {
		super(context);
	}
	
	public ProductsDataHelper(Context context, Category category) {
		super(context);
		mCategory = category;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Uri getContentUri() {
		// TODO Auto-generated method stub
		return ProductsProvider.PRODUCTS_CONTENT_URI;
	}
	
	public ContentValues getContentValues(Product product) {
		ContentValues values = new ContentValues();
		values.put(Tables.ProductsDBInfo.ID, product.getId());
		values.put(Tables.ProductsDBInfo.PID, product.getPid());
		values.put(Tables.ProductsDBInfo.CATEGORY, product.getCategory());
		values.put(Tables.ProductsDBInfo.JSON, product.toJson());
		return values;
	}
	
	public Product query(long id) {
		Product product = null;
		SQLiteDatabase db = Tables.getDBHelper().getReadableDatabase();
		Cursor cursor = db.query(ProductsDBInfo.TABLE_NAME, null, ProductsDBInfo.ID + "=?", new String[]{
				String.valueOf(id)
		}, null, null, null);
		
		if(cursor.moveToFirst()){
			product = Product.fromCursor(cursor);
		}
		
		cursor.close();
		return product;
	}
    
	public void insert(Product product) {
		if(query(product.getId()) == null){
			ContentValues values = getContentValues(product);
			insert(values);	
		}
	}
	
	public void bulkInsert(List<Product> products) {
		ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
		for(Product product : products){
			if(query(product.getId()) == null){
				ContentValues values = getContentValues(product);
				contentValues.add(values);	
			}
		}
		
		if(contentValues.size() == 0){
			return;
		}
		
		ContentValues[] valueArray = new ContentValues[contentValues.size()];
		bulkInsert(contentValues.toArray(valueArray));
	}
	
	public int deleteAll() {
		synchronized (ProductsProvider.DBLock) {
			DBHelper mDBHelper = Tables.getDBHelper();
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			int row = db.delete(Tables.ProductsDBInfo.TABLE_NAME, Tables.ProductsDBInfo.CATEGORY + "=? OR " + 
					Tables.ProductsDBInfo.PID + "=?", new String[]{
						String.valueOf(mCategory.getId()), String.valueOf(mCategory.getId())
			});
			Log.d(AppData.TAG, "deleteAll:"+row);
			return row;
		}
	}
	
	public CursorLoader getCursorLoader() {
		return new CursorLoader(getContext(), getContentUri(), null, Tables.ProductsDBInfo.CATEGORY + "=? OR " + 
				Tables.ProductsDBInfo.PID + "=?", new String[]{
					String.valueOf(mCategory.getId()), String.valueOf(mCategory.getId())
		}, Tables.ProductsDBInfo.ID + " DESC ");
	}
}
