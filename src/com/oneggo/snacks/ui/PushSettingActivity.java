package com.oneggo.snacks.ui;

import java.net.HttpURLConnection;
import java.util.HashMap;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import cn.jpush.android.api.JPushInterface;

import com.oneggo.snacks.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oneggo.snacks.AppData;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PushSettingActivity extends SwipeBackActivity implements OnClickListener, 
	OnCheckedChangeListener{
	
	private Button back;
	
	private Button saveSetting;
	
	private LinearLayout content;
	
	private CheckBox turnOn;
	
	private CheckBox sound;
	
	private CheckBox vibrate;
	
	private Dialog dialog = null;
	
	private SharedPreferences sharedPreferences;
	
	private SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setupViews();
        
		sharedPreferences = this.getSharedPreferences(AppData.TAG, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		
		if(getPushSetting(AppData.ISPUSH)){
			content.setVisibility(View.VISIBLE);
		}else{
			content.setVisibility(View.GONE);
		}
		
		turnOn.setChecked(getPushSetting(AppData.ISPUSH));
		sound.setChecked(getPushSetting(AppData.PUSHSOUND));
		vibrate.setChecked(getPushSetting(AppData.PUSHVIBRATE));
	}
	
	private boolean getPushSetting(String key) {
		boolean status = sharedPreferences.getBoolean(key, false);
		return status;
	}
	
	private void setupViews() {
		setContentView(R.layout.activity_pushsetting);

		back = (Button) findViewById(R.id.back);
		saveSetting = (Button) findViewById(R.id.save_setting);
		turnOn = (CheckBox) findViewById(R.id.turn_on);
		sound = (CheckBox) findViewById(R.id.sound);
		vibrate = (CheckBox) findViewById(R.id.vibrate);
		content = (LinearLayout) findViewById(R.id.content);
		
		sound.setOnCheckedChangeListener(this);
		vibrate.setOnCheckedChangeListener(this);
		turnOn.setOnCheckedChangeListener(this);
		back.setOnClickListener(this);
		saveSetting.setOnClickListener(this);
		
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
			savePushSetting();
			onBackPressed();
			break;
		case R.id.save_setting:
			savePushSetting();
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean arg1) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.sound:
			editor.putBoolean(AppData.PUSHSOUND, arg1).commit();
			break;
		case R.id.vibrate:
			editor.putBoolean(AppData.PUSHVIBRATE, arg1).commit();
			break;
		case R.id.turn_on:
			editor.putBoolean(AppData.ISPUSH, arg1).commit();
			
			if(arg1){
				content.setVisibility(View.VISIBLE);
			}else{
				content.setVisibility(View.GONE);
			}
		break;
		}
	}
	
	public void pushContentSetting(View view) {
		startActivity(new Intent(this, PushContentSettingActivity.class));
	}
	
	public void pushKeywordSetting(View view) {
		startActivity(new Intent(this, PushKeywordSettingActivity.class));
	}
	
	private void savePushSetting() {
		if(!CommonUtils.isOnline(this)){
			Toast.makeText(this, R.string.sync_push_setting_network_error, 
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(dialog == null){
			dialog = CommonUtils.creatRequestDialog(this, getString(R.string.sync_push_setting));
		}
		
		dialog.show();
		
		HashMap<String, String> data = new HashMap<String, String>();
		String registration = JPushInterface.getRegistrationID(this);
		String categories = sharedPreferences.getString(AppData.PUSHCONTENT, "");
		String keyword = sharedPreferences.getString(AppData.PUSHKEYWORD, "");
		String nickname = sharedPreferences.getString("nickname", "");
		
		if(TextUtils.isEmpty(registration)){
			Toast.makeText(this, R.string.get_registration_error, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(!TextUtils.isEmpty(keyword)){
			keyword.replace(" ", "+");
		}
		
		data.put("registration", registration);
		data.put("status", getPushSetting(AppData.ISPUSH) ? "1" : "0");
		data.put("sound", getPushSetting(AppData.PUSHSOUND) ? "1" : "0");
		data.put("vibrate", getPushSetting(AppData.PUSHVIBRATE) ? "1" : "0");
		data.put("categories", categories);
		data.put("keyword", keyword);
		data.put("nickname", nickname);
		data.put("site", AppData.Site);
		
		final CommonUtils.AsyncHttpPost asyncHttpPost = new CommonUtils.AsyncHttpPost(this, data);
		CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Integer>(){

			@Override
			protected Integer doInBackground(Object... arg0) {
				// TODO Auto-generated method stub
				try {
					HashMap<String, Object> response = asyncHttpPost.execute(Api.PUSH).get();
					if(response == null || (Integer)response.get("statusCode") != HttpURLConnection.HTTP_OK){
						if(response != null){
							Log.d(AppData.TAG, "发送反馈信息时服务器出错:"+response.toString());
						}
						return -1;
					}
					
					String jsonString = (String) response.get("body");
					HashMap<String, String> result = new Gson().fromJson(jsonString, 
							new TypeToken<HashMap<String, String>>() {}.getType());
					String status = result.get("status");
					
					return Integer.parseInt(status);
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
					Toast.makeText(PushSettingActivity.this, R.string.server_exception_occurs, 
							Toast.LENGTH_SHORT).show();
					break;
				case -2:
					Toast.makeText(PushSettingActivity.this, R.string.get_server_data_error, 
							Toast.LENGTH_SHORT).show();
					break;
				case 0:
					Toast.makeText(PushSettingActivity.this, R.string.sync_push_setting_success, 
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(PushSettingActivity.this, R.string.sync_push_setting_server_error, 
							Toast.LENGTH_SHORT).show();
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
