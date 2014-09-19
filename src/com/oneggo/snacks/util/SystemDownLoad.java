package com.oneggo.snacks.util;

import com.oneggo.snacks.AppData;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

public class SystemDownLoad {
	
	Context context;
	String url,path,name;
	public SystemDownLoad(Context context,String url,String name,String path){
		this.context = context;
		this.url = url;
		this.name = name;
		this.path = path;
	}
	
	public void downLoad(){
		if(url!=null && name!=null && path!=null){
			DownloadManager download = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
			Uri uri = Uri.parse(url);
			Request request = new Request(uri);
			request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
			request.setVisibleInDownloadsUi(true);
			request.setTitle(name);
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			request.setDestinationInExternalPublicDir(path, name);
			
			long id = download.enqueue(request);
			
			SharedPreferences spf =  context.getSharedPreferences(AppData.TAG, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = spf.edit();
			editor.putLong("download_id", id);
			editor.commit();
		}	
	}
}
