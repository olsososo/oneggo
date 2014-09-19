package com.oneggo.snacks.datatype;

import java.util.ArrayList;

public class Like extends BaseType{
	private long statusCode;
	
	private String message;
	
	private ArrayList<Product> products;

	public long getStatusCode() {
		return statusCode;
	}

	public String getMessage() {
		return message;
	}

	public ArrayList<Product> getProducts() {
		return products;
	}
}
