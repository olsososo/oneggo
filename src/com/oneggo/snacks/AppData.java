package com.oneggo.snacks;

import android.app.Application;
import android.content.Context;

public class AppData extends Application{
	private static Context sContext;
	
	public static String Site = "1";
	
	private static String QQAppId = "1101821694";
	
	private static String QQAppKey = "gYwovLNZSK0Afslg";
	
	public static final String TAG = "oneggo";
	
	public static final String DBNAME = "oneggo.snacks.db";
	
	public static final String PACKAGENAME = "oneggo.snacks.apk";
	
	public static final String REGISTRATION = "registration";
	
	public static final String ISPUSH = "isPush";
	
	public static final String PUSHSOUND = "pushSound";
	
	public static final String PUSHVIBRATE = "pushVibrate";
	
	public static final String PUSHKEYWORD = "pushKeyword";
	
	public static final String PUSHCONTENT = "pushContent";
	
	public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";
	
	public static final String EXTRA_PRODUCT = "EXTRA_PRODUCT";
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		sContext = getApplicationContext();
	}
	
	public static Context getContext() {
		return sContext;
	}

	public static String getQQAppId() {
		return QQAppId;
	}

	public static String getQQAppKey() {
		return QQAppKey;
	}
	
}
