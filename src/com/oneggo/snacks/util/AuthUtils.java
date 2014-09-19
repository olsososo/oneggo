package com.oneggo.snacks.util;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.oneggo.snacks.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oneggo.snacks.AppData;
import com.oneggo.snacks.ui.MainActivity;
import com.oneggo.snacks.ui.UsernameSettingActivity;
import com.oneggo.snacks.vendor.Api;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class AuthUtils {
	
	public final static UMSocialService mController = UMServiceFactory.
			getUMSocialService("com.umeng.login",RequestType.SOCIAL);
	
    public static boolean validateEmail(String email) {
    	return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    public static boolean validateUserName(String name) {
    	return Pattern.matches("^[\u4e00-\u9fa5_a-zA-Z0-9]{4,30}$", name);
    }
    
    public static boolean validateKeyword(String keyword) {
    	return Pattern.matches("^[\u4e00-\u9fa5_a-zA-Z0-9 ]{1,30}$", keyword);
    }
    
    public static boolean validatePassword(String password) {
    	return (password.length() >= 6 && password.length() <= 16);
    }
    
	public static void doSinaLogin(final Context context) {
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		mController.doOauthVerify(context, SHARE_MEDIA.SINA, new UMAuthListener() {
			
			@Override
			public void onStart(SHARE_MEDIA arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(SocializeException arg0, SHARE_MEDIA arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onComplete(Bundle value, SHARE_MEDIA platform) {
				// TODO Auto-generated method stub
				if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
                    Toast.makeText(context, "授权成功.",Toast.LENGTH_SHORT).show();
                    getPlatformInfo(context, 1);
				} else {
                    Toast.makeText(context, "授权失败",Toast.LENGTH_SHORT).show();
                }	
			}
			
			@Override
			public void onCancel(SHARE_MEDIA arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public static void doQQLogin(final Context context) {
		 mController.getConfig().supportQQPlatform((Activity) context, 
				 AppData.getQQAppId(), AppData.getQQAppKey(),"http://www.umeng.com/social"); 
		mController.doOauthVerify(context, SHARE_MEDIA.QQ, new UMAuthListener() {
			
			@Override
			public void onStart(SHARE_MEDIA arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(SocializeException arg0, SHARE_MEDIA arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onComplete(Bundle value, SHARE_MEDIA platform) {
				// TODO Auto-generated method stub
				if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
                    Toast.makeText(context, "授权成功.",Toast.LENGTH_SHORT).show();
                    getPlatformInfo(context, 2);
                } else {
                    Toast.makeText(context, "授权失败",Toast.LENGTH_SHORT).show();
                }
			}
			
			@Override
			public void onCancel(SHARE_MEDIA arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private static void getPlatformInfo(final Context context, final int platformId) {
		SHARE_MEDIA platform = null;
		
		switch (platformId) {
		case 1:
			platform = SHARE_MEDIA.SINA;
			break;
		case 2:
			platform = SHARE_MEDIA.QQ;
			break;
		}
		
		mController.getPlatformInfo(context, platform, new UMDataListener() {
		    @Override
		    public void onStart() {
		        //Toast.makeText(context, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
		    }
		    
		    @Override
	        public void onComplete(int status, Map<String, Object> info) {
	            if(status == 200 && info != null){
	        		HashMap<String, String> data = new HashMap<String, String>();
	        		data.put("oauthid", info.get("uid").toString());
	        		data.put("nickname", info.get("screen_name").toString());
	        		data.put("photo", info.get("profile_image_url").toString());
	        		data.put("gender", info.get("gender").toString());
	        		data.put("platformid", String.valueOf(platformId));
	        		data.put("site", AppData.Site);
	        		doOauthLogin(context, data);
	            }else{
	               Log.d(AppData.TAG ,"获取平台数据发生错误："+status);
	           }
	        }
		});		
	}
	
	private static void doOauthLogin(final Context context, final HashMap<String, String> data) {
		final Dialog dialog = CommonUtils.creatRequestDialog(context, context.getString(R.string.verifying_account));
		dialog.show();
		
		final CommonUtils.AsyncHttpPost asyncHttpPost = new CommonUtils.AsyncHttpPost(context, data);
		CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Object>(){

			@Override
			protected Object doInBackground(Object... arg0) {
				// TODO Auto-generated method stub
				try {
					HashMap<String, Object> response = asyncHttpPost.execute(Api.OAUTH).get();
					if(response == null || (Integer)response.get("statusCode") != HttpURLConnection.HTTP_OK){
						if(response != null){
							Log.d(AppData.TAG, "oauth登陆时时服务器出错:"+response.toString());
						}
						return null;
					}
					
					String jsonString = (String) response.get("body");
					HashMap<String, String> result = new Gson().fromJson(jsonString, 
							new TypeToken<HashMap<String, String>>() {}.getType());
					String status = result.get("status");			
					
					Log.d(AppData.TAG, "oauth登录返回数据 " + result.toString());
					
					if(status.equals("0")){
						((Activity) context).runOnUiThread(new CommonUtils.uiToast(context, 
								R.string.account_login_success));
						AuthUtils.saveSession(context, result);
						context.startActivity(new Intent(context, MainActivity.class));
					}else{
						((Activity) context).runOnUiThread(new CommonUtils.uiToast(context, 
								R.string.user_name_already_exists));
						AuthUtils.saveTempUser(context, data);
						context.startActivity(new Intent(context, UsernameSettingActivity.class));
					}
				} catch (IllegalArgumentException e) {
					Log.d(AppData.TAG, e.getMessage());
					((Activity) context).runOnUiThread(new CommonUtils.uiToast(context, 
							R.string.save_session_error));	
				} catch (Exception e) {
					Log.d(AppData.TAG, "oauth登录异常信息" + e.getMessage());
					((Activity) context).runOnUiThread(new CommonUtils.uiToast(context, 
							R.string.parsing_data_exception_occurs));
				}
				return null;
			}

			@Override
			protected void onPostExecute(Object result) {
				// TODO Auto-generated method stub
				dialog.cancel();
				super.onPostExecute(result);
			}
		});
	}
	
	public static void saveSession(Context context, HashMap<String, String> data) {
		try{
			SharedPreferences preferences = context.getSharedPreferences(AppData.TAG, 
					context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			
			Set<Map.Entry<String, String>> entrySet = data.entrySet();
			for(Entry entry : entrySet){
				editor.putString(entry.getKey().toString(), entry.getValue().toString());
			}
			editor.commit();
		}catch(Exception e){
			throw new IllegalArgumentException("保存 session数据出错");
		}
	}
	
	public static void saveTempUser(Context context, HashMap<String, String> data) {
		try{
			SharedPreferences preferences = context.getSharedPreferences(AppData.TAG, 
					context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("tempUser", data.toString()).commit();
		}catch(Exception e){
			throw new IllegalArgumentException("保存临时session数据错误");
		}
	}
	
	public static void clearTempUser(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(AppData.TAG, 
				context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove("tempUser").commit();		
	}
	
	public static void logout(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(AppData.TAG, 
				context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		
		String[] keys = new String[]{
				"id", "nickname", "email", "gender", "photo", "sessionid", "platformid"
		};
		
		for(String key : keys){
			editor.remove(key).commit();
		}
		Toast.makeText(context, R.string.account_logout_success, Toast.LENGTH_SHORT).show();
		((Activity) context).finish();
		((Activity) context).startActivity(((Activity) context).getIntent());
	}
	
	public static boolean isLogin(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(AppData.TAG,
				Context.MODE_PRIVATE);
		String sessionid = sharedPreferences.getString("sessionid", null);
		return !TextUtils.isEmpty(sessionid);
	}
	
	public static HashMap<String, String> getLoginUser(Context context) {
		HashMap<String, String> data = new HashMap<String, String>();
		SharedPreferences sharedPreferences = context.getSharedPreferences(AppData.TAG, 
				Context.MODE_PRIVATE);
		String[] keys = new String[]{
				"id", "nickname", "email", "gender", "photo", "sessionid", "platformid"
		};
		
		for(String key : keys){
			data.put(key, sharedPreferences.getString(key, ""));
		}
		
		return data;
	}
	
	public static String getLoginUserId(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(AppData.TAG, 
				Context.MODE_PRIVATE);
		String uid = sharedPreferences.getString("id", "");
		return uid;
	}
}
