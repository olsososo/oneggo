package com.oneggo.snacks.ui;

import java.net.HttpURLConnection;
import java.util.HashMap;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import org.json.JSONObject;

import com.oneggo.snacks.R;
import com.oneggo.snacks.AppData;
import com.oneggo.snacks.util.AuthUtils;
import com.oneggo.snacks.util.CommonUtils;
import com.oneggo.snacks.vendor.Api;
import com.umeng.analytics.MobclickAgent;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SettingActivity extends SwipeBackActivity implements OnClickListener{
	
	private Button back;
	
	private EditText username;
	
	private EditText email;
	
	private RadioGroup gender;
	
	private Button saveSetting;
	
	private EditText password;
	
	private EditText newPassword;
	
	private Button setPassword;
	
	private HashMap<String, String> user;
	
	private SharedPreferences sharedPreferences;
	
	private Editor editor;
	
	private Dialog settingDialog = null;
	
	private Dialog passwordDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setupViews();
		
		sharedPreferences = getSharedPreferences(AppData.TAG, MODE_PRIVATE);
		editor = sharedPreferences.edit();
		user = AuthUtils.getLoginUser(this);
		username.setText(user.get("nickname"));
		
		String emailString = user.get("email");
		if(emailString != null){
			email.setText(emailString);
		}
		
		String genderString = user.get("gender");
		if(genderString.equals("1")){
			gender.check(R.id.male);
		}else if(genderString.equals("2")){
			gender.check(R.id.female);
		}else{
			gender.check(R.id.other);
		}
	}
	
	private void setupViews() {
		setContentView(R.layout.activity_setting);
		
		back = (Button) findViewById(R.id.back);
		username = (EditText) findViewById(R.id.username);
		email = (EditText) findViewById(R.id.email);
		gender = (RadioGroup) findViewById(R.id.gender);
		saveSetting = (Button) findViewById(R.id.save_setting);
		password = (EditText) findViewById(R.id.password);
		newPassword = (EditText) findViewById(R.id.new_password);
		setPassword = (Button) findViewById(R.id.set_password);
		
		back.setOnClickListener(this);
		saveSetting.setOnClickListener(this);
		setPassword.setOnClickListener(this);
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

	private String getGender() {
		int id = gender.getCheckedRadioButtonId();
		switch (id){
		case R.id.male:
			return "1";
		case R.id.female:
			return "2";
		case R.id.other:
			return "3";
		default:
			return "1";
		}
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.back:
			onBackPressed();
			break;
		case R.id.save_setting:
			saveSetting();
			break;
		case R.id.set_password:
			setPassword();
			break;
		}
	}
	
	private boolean checkNetWork() {
		boolean status = true;
		if(!CommonUtils.isOnline(this)){
			Toast.makeText(this, R.string.check_your_network_environment, 
					Toast.LENGTH_SHORT).show();;
			status = false;
		}
		
		return status;
	}
	
	private void saveSetting() {
		if(!checkNetWork()) return;
		
		final String usernameString = username.getText().toString().trim();
		final String emailString = email.getText().toString().trim();
		final String genderString = getGender();
		
		if(TextUtils.isEmpty(usernameString)){
			Toast.makeText(this, R.string.username_can_not_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(!AuthUtils.validateUserName(usernameString)){
			Toast.makeText(this, R.string.username_can_not_matched_the_rule, 
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(TextUtils.isEmpty(emailString)){
			Toast.makeText(this, R.string.email_can_not_be_empty, 
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(!AuthUtils.validateEmail(emailString)){
			Toast.makeText(this, R.string.email_can_not_matched_the_rule, 
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(settingDialog == null){
			settingDialog = CommonUtils.creatRequestDialog(this, getString(R.string.saving_setting));
		}
		
		settingDialog.show();
		
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("uid", user.get("id"));
		data.put("sessionid", user.get("sessionid"));
		data.put("nickname", usernameString);
		data.put("email", emailString);
		data.put("gender", genderString);
		data.put("site", AppData.Site);
		
		final CommonUtils.AsyncHttpPost asyncHttpPost = new CommonUtils.AsyncHttpPost(this, data);
		CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Integer>(){

			@Override
			protected Integer doInBackground(Object... arg0) {
				// TODO Auto-generated method stub
				try {
					HashMap<String, Object> response = asyncHttpPost.execute(Api.SETTING).get();
					if(response == null || (Integer)response.get("statusCode") != HttpURLConnection.HTTP_OK){
						if(response != null){
							Log.d(AppData.TAG, "设置用户个人信息时服务器出错:"+response.toString());
						}
						return -1;
					}
					
					String jsonString = (String) response.get("body");
					JSONObject json = new JSONObject(jsonString);
					int status = json.getInt("status");
					
					return status;
				} catch(Exception e){
					Log.d(AppData.TAG, "获取服务器返回数据出现异常:" + e.getMessage());
				}
				return -2;
			}

			@Override
			protected void onPostExecute(Integer result) {
				// TODO Auto-generated method stub
				settingDialog.cancel();
				switch(result){
				case -1:
					Toast.makeText(SettingActivity.this, R.string.server_exception_occurs, 
							Toast.LENGTH_SHORT).show();
					break;
				case -2:
					Toast.makeText(SettingActivity.this, R.string.get_server_data_error, 
							Toast.LENGTH_SHORT).show();
					break;		
				case 1:
					Toast.makeText(SettingActivity.this, R.string.token_error, 
							Toast.LENGTH_SHORT).show();
					break;
				case 2:
					Toast.makeText(SettingActivity.this, R.string.username_can_not_matched_the_rule, 
							Toast.LENGTH_SHORT).show();
					break;	
				case 3:
					Toast.makeText(SettingActivity.this, R.string.email_can_not_matched_the_rule, 
							Toast.LENGTH_SHORT).show();
					break;
				case 4:
					Toast.makeText(SettingActivity.this, R.string.user_name_already_exists, 
							Toast.LENGTH_SHORT).show();
					break;	
				case 5:
					Toast.makeText(SettingActivity.this, R.string.email_name_already_exists, 
							Toast.LENGTH_SHORT).show();
					break;
				case 0:
					editor.putString("nickname", usernameString).commit();
					editor.putString("email", emailString).commit();
					editor.putString("gender", genderString).commit();
					Toast.makeText(SettingActivity.this, R.string.edit_setting_success, 
							Toast.LENGTH_SHORT).show();
					startActivity(new Intent(SettingActivity.this, MainActivity.class));
					break;	
				}
				super.onPostExecute(result);
			}
		});
	}
	
	private void setPassword() {
		if(!checkNetWork()) return;
		
		String passwordString = password.getText().toString().trim();
		String newPasswordString = newPassword.getText().toString().trim();
	
		if(TextUtils.isEmpty(passwordString)){
			Toast.makeText(this, R.string.password_can_not_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(TextUtils.isEmpty(newPasswordString)){
			Toast.makeText(this, R.string.new_password_can_not_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(!AuthUtils.validatePassword(passwordString)){
			Toast.makeText(this, R.string.password_can_not_matched_the_rule, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(!AuthUtils.validatePassword(newPasswordString)){
			Toast.makeText(this, R.string.new_password_can_not_matched_the_rule, 
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(passwordDialog == null){
			passwordDialog = CommonUtils.creatRequestDialog(this, getString(R.string.saving_password));
		}
		
		passwordDialog.show();
		
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("uid", user.get("id"));
		data.put("sessionid", user.get("sessionid"));
		data.put("password", passwordString);
		data.put("newpassword", newPasswordString);
		data.put("site", AppData.Site);
		
		final CommonUtils.AsyncHttpPost asyncHttpPost = new CommonUtils.AsyncHttpPost(this, data);
		CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Integer>(){

			@Override
			protected Integer doInBackground(Object... arg0) {
				// TODO Auto-generated method stub
				try {
					HashMap<String, Object> response = asyncHttpPost.execute(Api.SETPASSWORD).get();
					if(response == null || (Integer)response.get("statusCode") != HttpURLConnection.HTTP_OK){
						if(response != null){
							Log.d(AppData.TAG, "修改用户密码时服务器出错:"+response.toString());
						}
						return -1;
					}
					
					String jsonString = (String) response.get("body");
					JSONObject json = new JSONObject(jsonString);
					int status = json.getInt("status");
					
					return status;
				} catch(Exception e){
					Log.d(AppData.TAG, "获取服务器返回数据出现异常:" + e.getMessage());
				}
				return -2;
			}

			@Override
			protected void onPostExecute(Integer result) {
				// TODO Auto-generated method stub
				passwordDialog.cancel();
				switch(result){
				case -1:
					Toast.makeText(SettingActivity.this, R.string.server_exception_occurs, 
							Toast.LENGTH_SHORT).show();
					break;
				case -2:
					Toast.makeText(SettingActivity.this, R.string.get_server_data_error, 
							Toast.LENGTH_SHORT).show();
					break;	
				case 1:
					Toast.makeText(SettingActivity.this, R.string.new_password_can_not_matched_the_rule, 
							Toast.LENGTH_SHORT).show();
					break;
				case 2:
					Toast.makeText(SettingActivity.this, R.string.token_error, 
							Toast.LENGTH_SHORT).show();
					break;	
				case 3:
					Toast.makeText(SettingActivity.this, R.string.password_error, 
							Toast.LENGTH_SHORT).show();
					break;
				case 0:
					Toast.makeText(SettingActivity.this, R.string.edit_setting_success, 
							Toast.LENGTH_SHORT).show();
					startActivity(new Intent(SettingActivity.this, MainActivity.class));
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
