package com.oneggo.snacks.datatype;

import java.util.ArrayList;

public class SubjectItem extends BaseType{
	private long id;
	
	private String title;
	
	private String photo;
	
	private String description;

	private ArrayList<Product> products;

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getPhoto() {
		return photo;
	}

	public String getDescription() {
		return description;
	}

	public ArrayList<Product> getProducts() {
		return products;
	}
}
