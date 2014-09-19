package com.oneggo.snacks.datatype;

public class Check extends BaseType{
	
	private long category_last_updated;
	
	private long product_last_updated;
	
	private long subject_last_updated;
	
	private App app;
	
	public long getCategory_last_updated() {
		return category_last_updated;
	}

	public long getProduct_last_updated() {
		return product_last_updated;
	}

	
	public long getSubject_last_updated() {
		return subject_last_updated;
	}

	public App getApp() {
		return app;
	}
}
