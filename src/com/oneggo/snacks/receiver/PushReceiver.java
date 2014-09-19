package com.oneggo.snacks.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.oneggo.snacks.AppData;
import com.oneggo.snacks.dao.ProductsDataHelper;
import com.oneggo.snacks.data.GsonRequest;
import com.oneggo.snacks.data.RequestManager;
import com.oneggo.snacks.datatype.Product;
import com.oneggo.snacks.ui.BrowserActivity;
import com.oneggo.snacks.ui.FeedbackActivity;
import com.oneggo.snacks.util.CommonUtils;
import com.oneggo.snacks.vendor.Api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class PushReceiver extends BroadcastReceiver{
	
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
        Bundle bundle = intent.getExtras();
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA); 
        
        if(JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())){
            if(extras != null){
            	try {
					JSONObject json = new JSONObject(extras);
					String target = json.getString("target");
					
					if(target.equals("product")){
						int id = json.getInt("id");
						openProduct(context, id);
					}else if(target.equals("tucao")){
						openTucao(context);
					}else{
						Log.d(AppData.TAG, "Unhandled notify target");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }	
        }
    }
	
	public void openProduct(final Context context, int id) {
		final ProductsDataHelper dataHelper = new ProductsDataHelper(context);
		
		RequestManager.addRequest(new GsonRequest<>(
				String.format(Api.PRODUCT, id), 
				Product.class, 
				null, 
				new Response.Listener<Product>(){

					@Override
					public void onResponse(Product response) {
						// TODO Auto-generated method stub
						dataHelper.insert(response);
						Bundle bundle = new Bundle();
						bundle.putSerializable(AppData.EXTRA_PRODUCT, response);
						Intent intent = new Intent(context, BrowserActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtras(bundle);
						context.startActivity(intent);
					}					
				}, 
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Toast.makeText(context, CommonUtils.volleyErrorMessage(error), 
								Toast.LENGTH_SHORT).show();
					}
				}), 
		context);
	}
	
	public void openTucao(Context context) {
		Intent intent = new Intent(context, FeedbackActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
