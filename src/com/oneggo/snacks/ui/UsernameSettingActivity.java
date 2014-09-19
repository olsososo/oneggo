package com.oneggo.snacks.ui;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import com.oneggo.snacks.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oneggo.snacks.AppData;
import com.oneggo.snacks.util.AuthUtils;
import com.oneggo.snacks.util.CommonUtils;
import com.oneggo.snacks.vendor.Api;
import com.umeng.analytics.MobclickAgent;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class UsernameSettingActivity extends SwipeBackActivity implements OnClickListener{
	
	private Button back;
	
	private Button submit;
	
	private EditText username;
	
	private Dialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setupViews();
	}
	
	private void setupViews() {
		setContentView(R.layout.activity_updateusername);
		
		back = (Button) findViewById(R.id.back);
		username = (EditText) findViewById(R.id.username);
		submit = (Button) findViewById(R.id.submit);
		
		back.setOnClickListener(this);
		submit.setOnClickListener(this);
		custom();
	}
	
	private void custom() {
		SharedPreferences sharedPreferences = getSharedPreferences(AppData.TAG, MODE_PRIVATE);
		SwipeBackLayout swipeBackLayout= getSwipeBackLayout();
		RelativeLayout menu = (RelativeLayout) findViewById(R.id.menu);
		int edgeModel = sharedPreferences.getInt("edgemodel", 1);
		int edgeFlag;
		
		switch(edgeModel){
		case 1:
			edgeFlag = SwipeBackLayout.EDGE_LEFT;
			break;
		case 2:
			edgeFlag = SwipeBackLayout.EDGE_RIGHT;
			break;
		case 3:
			edgeFlag = SwipeBackLayout.EDGE_BOTTOM;
			break;
		default:
			edgeFlag = SwipeBackLayout.EDGE_ALL;
			break;
		}	
		
		swipeBackLayout.setEdgeTrackingEnabled(edgeFlag);
		
		String myColor = sharedPreferences.getString("color", null);
		
		if(myColor != null){
			int indentify = getResources().getIdentifier(myColor, 
					"color", getPackageName());
					
			menu.setBackgroundColor(getResources().getColor(indentify));
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.back:
			onBackPressed();
			break;
		case R.id.submit:
			setUsername();
			break;
		}
	}
	
	private void setUsername() {
		String usernameText = username.getText().toString().trim();
		if(TextUtils.isEmpty(usernameText)){
			Toast.makeText(this, R.string.username_can_not_be_empty, 
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(!AuthUtils.validateUserName(usernameText)){
			Toast.makeText(this, R.string.username_can_not_matched_the_rule, 
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(!CommonUtils.isOnline(this)){
			Toast.makeText(this, R.string.check_your_network_environment, 
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(dialog == null){
			dialog = CommonUtils.creatRequestDialog(this, getString(R.string.verifying_username));
		}
		
		dialog.show();
		
		SharedPreferences sharedPreferences = this.getSharedPreferences(AppData.TAG, Context.MODE_PRIVATE);
		String tempUser = sharedPreferences.getString("tempUser", null);
		HashMap<String, String> data = new HashMap<String, String>();
		
		Properties props = new Properties();
		try {
			props.load(new StringReader(tempUser.substring(1, tempUser.length() - 1).replace(", ", "\n")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(AppData.TAG, e.getMessage());
		}       
		
		for (Map.Entry<Object, Object> e : props.entrySet()) {
			data.put((String)e.getKey(), (String)e.getValue());
		}
		
		data.put("nickname", usernameText);
		data.put("site", AppData.Site);
		
		final CommonUtils.AsyncHttpPost asyncHttpPost = new CommonUtils.AsyncHttpPost(this, data);
		CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Integer>(){

			@Override
			protected Integer doInBackground(Object... arg0) {
				// TODO Auto-generated method stub
				try {
					HashMap<String, Object> response = asyncHttpPost.execute(Api.OAUTH).get();
					if(response == null || (Integer)response.get("statusCode") != HttpURLConnection.HTTP_OK){
						if(response != null){
							Log.d(AppData.TAG, "设置用户名时服务器出错:"+response.toString());
						}
						return -1;
					}

					String jsonString = (String) response.get("body");
					HashMap<String, String> result = new Gson().fromJson(jsonString, 
							new TypeToken<HashMap<String, String>>() {}.getType());
					String statusString = result.get("status");
					int status = Integer.parseInt(statusString);
					
					if(status == 0){
						AuthUtils.saveSession(UsernameSettingActivity.this, result);
					}
					
					return status;

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d(AppData.TAG, e.getMessage());
				}
				
				return -2;
			}

			@Override
			protected void onPostExecute(Integer result) {
				// TODO Auto-generated method stub
				dialog.cancel();
				
				switch(result){
				case -1:
					Toast.makeText(UsernameSettingActivity.this, R.string.server_exception_occurs, 
							Toast.LENGTH_SHORT).show();
					break;
				case -2:
					Toast.makeText(UsernameSettingActivity.this, R.string.get_server_data_error, 
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(UsernameSettingActivity.this, R.string.user_name_already_exists, 
							Toast.LENGTH_SHORT).show();
					break;
				case 0:
					Toast.makeText(UsernameSettingActivity.this, R.string.account_login_success, 
							Toast.LENGTH_SHORT).show();
					startActivity(new Intent(UsernameSettingActivity.this, MainActivity.class));
					break;
				}
				
				super.onPostExecute(result);
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		custom();
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
