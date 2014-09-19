package com.oneggo.snacks.vendor;

import com.oneggo.snacks.AppData;

public class Api {
	private static final String BASE_URL = "http://oneggo.com/api";
	
	public static final String CATEGORY_LIST = BASE_URL + "/categories?site=" + AppData.Site; 
	
	public static final String PRODUCT_LIST = BASE_URL + "/products?site="+AppData.Site+"&category=%1$d&page=%2$d";
	
	public static final String RECORD = BASE_URL + "/record?site="+AppData.Site+"&id=%1$d&action=%2$s&uid=%3$s&sessionid=%4$s";
	
	public static final String LOGIN = BASE_URL + "/login";
	
	public static final String REGISTER = BASE_URL + "/register";
	
	public static final String OAUTH = BASE_URL + "/oauth";
	
	public static final String LIKE = BASE_URL + "/likes?site="+AppData.Site+"&uid=%1$s&sessionid=%2$s";
	
	public static final String PUSH = BASE_URL + "/push";
	
	public static final String PRODUCT = BASE_URL + "/product?site="+AppData.Site+"&id=%1$d";
	
	public static final String SEND = BASE_URL + "/send";
	
	public static final String MSG = BASE_URL + "/msg?site="+AppData.Site+"&registration=%1$s";
	
	public static final String MAIL = BASE_URL + "/mail";
	
	public static final String VERIFY = BASE_URL + "/verify";
	
	public static final String FORGETPASSWORD = BASE_URL + "/forgetpassword";
	
	public static final String PHOTO = BASE_URL + "/photo";
	
	public static final String CHECK = BASE_URL + "/check?site="+AppData.Site+"&category=%1$s&package=%2$s&versonname=%3$s";

	public static final String SETTING = BASE_URL + "/setting";
	
	public static final String SETPASSWORD = BASE_URL + "/setpassword";
	
	public static final String SUBJECT = BASE_URL + "/subject?site="+AppData.Site;
	
	public static final String SUBJECTITEM = BASE_URL + "/subjectitem?id=%1$s&site="+AppData.Site;
	
	public static final String ISLIKE = BASE_URL + "/islike";
}
