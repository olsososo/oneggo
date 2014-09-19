
package com.oneggo.snacks.util;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.oneggo.snacks.R;
import com.oneggo.snacks.AppData;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Issac on 7/18/13.
 */
public class CommonUtils {
	private static final int CONNECTION_TIMEOUT = 10000; /* 5 seconds */
	private static final int SO_TIMEOUT = 10000; /* 5 seconds */
	
    public static <Params, Progress,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        Result> void executeAsyncTask(
            AsyncTask<Params, Progress, Result> task, Params... params) {
        if (Build.VERSION.SDK_INT >= 11) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }
    
	public static Dialog creatRequestDialog(final Context context, String tip) {
		
		final Dialog dialog = new Dialog(context, R.style.dialog);
		dialog.setContentView(R.layout.dialog_layout);	
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();	
		int width = getScreenWidth(context);	
		lp.width = (int)(0.6 * width);	
		
		TextView titleTxtv = (TextView) dialog.findViewById(R.id.tvLoad);
		if (tip == null || tip.length() == 0)
		{
			titleTxtv.setText(R.string.sending_request);	
		}else{
			titleTxtv.setText(tip);	
		}
		
		return dialog;
	}
	
	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.x;
	}
	
	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.y;
	}
	
	public static float getScreenDensity(Context context) {
    	try {
    		DisplayMetrics dm = new DisplayMetrics();
	    	WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
	    	manager.getDefaultDisplay().getMetrics(dm);
	    	return dm.density;
    	} catch(Exception ex) {
    	
    	}
    	return 1.0f;
    }
	
	public static boolean isOnline(Context context) {
	    ConnectivityManager cm =
	        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	public static int volleyErrorMessage(VolleyError error) {
		NetworkResponse response = error.networkResponse;
		int resId = R.string.volley_timeout_error;
		
        if( error instanceof NetworkError) {
        	resId = R.string.volley_network_error;
        } else if( error instanceof ServerError) {
        	resId = R.string.volley_server_error;
        } else if( error instanceof AuthFailureError) {
        	resId = R.string.volley_auth_failure_error;
        } else if( error instanceof ParseError) {
        	resId = R.string.volley_parse_error;
        } else if( error instanceof NoConnectionError) {
        	resId = R.string.volley_no_connection_error;
        } else if( error instanceof TimeoutError) {
        	resId = R.string.volley_timeout_error;
        };
        
        return resId;
	}
	
	public static class uiToast implements Runnable{
		
		private int resId;
		private Context context;
		private String message = null;
		
		public uiToast(Context context, int resId){
			this.resId = resId;
			this.context = context;
		}

		public uiToast(Context context, String message){
			this.message = message;
			this.context = context;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(message == null){
				Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	public static class AsyncHttpPost extends AsyncTask<String, String, HashMap<String, Object>> {
		
		private HashMap<String, String> data = null;
		
		private Context context;
		
		private Dialog dialog;
		
		private boolean isBinary;
		
		public AsyncHttpPost(Context context, HashMap<String, String> data) {
			this(context, data, false);
		}
		
		public AsyncHttpPost(Context context, HashMap<String, String> data, boolean isBinary) {
			this.data = data;
			this.context = context;
			this.isBinary = isBinary;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected HashMap<String, Object> doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpParams httpParameters = httpclient.getParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
				HttpConnectionParams.setSoTimeout(httpParameters, SO_TIMEOUT);
				HttpConnectionParams.setTcpNoDelay(httpParameters, true);
				
				HttpPost httppost = new HttpPost(arg0[0]);
				
				if(!isBinary){
					httppost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
				}
				
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				
				Set<Map.Entry<String, String>> entrySet = data.entrySet();
				for(Entry entry : entrySet){
					nameValuePairs.add(new BasicNameValuePair(entry.getKey().toString(), 
							entry.getValue().toString()));
				}
				
				if(!isBinary){
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
				}else{
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				}
				
				HttpResponse response = httpclient.execute(httppost);
	            StatusLine statusLine = response.getStatusLine();
	            
	            byte[] result = null;
	            String str = "";
                result = EntityUtils.toByteArray(response.getEntity());
                str = new String(result, "UTF-8");
                
                HashMap<String, Object> data = new HashMap<String, Object>();
                data.put("body", str);
                data.put("statusCode", statusLine.getStatusCode());
                
                return data;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d(AppData.TAG, "post数据出现异常:" + e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}
	
	public static int convertDipToPx(Context context, int size) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, 
				context.getResources().getDisplayMetrics());
	}
}
