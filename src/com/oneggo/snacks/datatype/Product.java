package com.oneggo.snacks.datatype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;

import com.google.gson.Gson;
import com.oneggo.snacks.dao.Tables;

public class Product extends BaseType implements Serializable{
	
	private static final HashMap<Long, Product> CACHE = new HashMap<Long, Product>();
	
	public long id;
	
	public long pid;
	
	public long category;
	
	public String title;
	
	public String description;
	
	public String asin;
	
	public String url;
	
	public String short_url;
	
	public String photo;
	
	public String price;
	
	public String market_price;
	
	public long view_count;
	
	public long like_count;
	
	public String created_at;
	
	private static void addToCache(Product product) {
		CACHE.put(product.getId(), product);
	}
	
	private static Product getFromCache(long id) {
		return CACHE.get(id);
	}
	
	public static Product fromJson(String json) {
		return new Gson().fromJson(json, Product.class);
	}
	
	public static Product fromCursor(Cursor cursor) {		
		Product product = new Gson().fromJson(
				cursor.getString(cursor.getColumnIndex(Tables.ProductsDBInfo.JSON)),
				Product.class);
		return product;
	}
	
	public long getId() {
		return id;
	}
	
	public long getPid() {
		return pid;
	}
	
	public long getCategory() {
		return category;
	}
	
	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getAsin() {
		return asin;
	}

	public String getUrl() {
		return url;
	}
	
	public String getShort_url() {
		return short_url;
	}
	
	public String getPhoto() {
		return photo;
	}

	public String getPrice() {
		return price;
	}
	
	public String getMarket_price() {
		return market_price;
	}
	
	public long getView_count() {
		return view_count;
	}

	public long getLike_count() {
		return like_count;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setView_count(long view_count) {
		this.view_count = view_count;
	}

	public void setLike_count(long like_count) {
		this.like_count = like_count;
	}


	public static class ProductsRequestData {
		public int page;
		
		public int per_page;
		
		public int pages;
		
		public int total;
		
		private ArrayList<Product> products;

		public int getPage() {
			return page;
		}

		public int getPer_page() {
			return per_page;
		}

		public int getPages() {
			return pages;
		}

		public int getTotal() {
			return total;
		}

		public ArrayList<Product> getProducts() {
			return products;
		}
	}
}
