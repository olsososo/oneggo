package com.oneggo.snacks.ui;

import java.net.HttpURLConnection;
import java.util.HashMap;

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
import com.umeng.socialize.sso.UMSsoHandler;

import android.app.Dialog;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends SwipeBackActivity implements OnClickListener{
	
	private Button back;
	
	private Button login;
	
	private TextView forget;
	
	private TextView register;
	
	private EditText username;
	
	private EditText password;
	
	private LinearLayout sinaLogin;
	
	private LinearLayout qqLogin;
	
	private Dialog dialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setupViews();
	}

	private void setupViews() {
		setContentView(R.layout.activity_login);

		back = (Button) findViewById(R.id.back);
		login = (Button) findViewById(R.id.login);
		forget = (TextView) findViewById(R.id.forget);
		register = (TextView) findViewById(R.id.register);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		sinaLogin = (LinearLayout) findViewById(R.id.sina_login);
		qqLogin = (LinearLayout) findViewById(R.id.qq_login);
	
		back.setOnClickListener(this);
		login.setOnClickListener(this);
		forget.setOnClickListener(this);
		register.setOnClickListener(this);
		sinaLogin.setOnClickListener(this);
		qqLogin.setOnClickListener(this);
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
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.back:
			onBackPressed();
			break;
		case R.id.login:
			checkLogin();
			break;
		case R.id.forget:
			startActivity(new Intent(this, ForgetPasswordActivity.class));
			break;
		case R.id.register:
			startActivity(new Intent(this, RegisterActivity.class));
			break;
		case R.id.sina_login:
			AuthUtils.doSinaLogin(this);
			break;
		case R.id.qq_login:
			Toast.makeText(this, R.string.sorry_qq, Toast.LENGTH_SHORT).show();
			break;
		}
	}
	
	private void checkLogin() {
		if(TextUtils.isEmpty(username.getText().toString().trim())){
			Toast.makeText(this, R.string.username_can_not_be_empty, 
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(!AuthUtils.validateUserName(username.getText().toString().trim())){
			Toast.makeText(this, R.string.username_can_not_matched_the_rule, 
					Toast.LENGTH_SHORT).show();
			return;			
		}
		
		if(TextUtils.isEmpty(password.getText().toString().trim())){
			Toast.makeText(this, R.string.password_can_not_be_empty, 
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(!AuthUtils.validatePassword(password.getText().toString().trim())){
			Toast.makeText(this, R.string.password_can_not_matched_the_rule, 
					Toast.LENGTH_SHORT).show();
			return;			
		}	
		
		if(!CommonUtils.isOnline(this)) {
			Toast.makeText(this, R.string.check_your_network_environment, 
					Toast.LENGTH_SHORT).show();
			return;					
		}
		
		doLogin();
	}
	
	private void doLogin() {
		if(dialog == null){
			dialog = CommonUtils.creatRequestDialog(this, getString(R.string.verifying_account));
		}
		
		dialog.show();
		
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("nickname", username.getText().toString().trim());
		data.put("password", password.getText().toString().trim());
		data.put("site", AppData.Site);
		
		final CommonUtils.AsyncHttpPost asyncHttpPost = new CommonUtils.AsyncHttpPost(this, data);
		CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Integer>(){
			@Override
			protected Integer doInBackground(Object... arg0) {
				try {
					HashMap<String, Object> response = asyncHttpPost.execute(Api.LOGIN).get();
					if(response == null || (Integer)response.get("statusCode") != HttpURLConnection.HTTP_OK){
						if(response != null){
							Log.d(AppData.TAG, "登录帐号时服务器出错:"+response.toString());
						}
						return -1;
					}
					
					String jsonString = (String) response.get("body");
					HashMap<String, String> result = new Gson().fromJson(jsonString, 
							new TypeToken<HashMap<String, String>>() {}.getType());
					String statusString = result.get("status");
					int status = Integer.parseInt(statusString);
					
					if(status == 0){
						AuthUtils.saveSession(LoginActivity.this, result);
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
					Toast.makeText(LoginActivity.this, R.string.server_exception_occurs, 
							Toast.LENGTH_SHORT).show();
					break;
				case -2:
					Toast.makeText(LoginActivity.this, R.string.get_server_data_error, 
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(LoginActivity.this, R.string.user_name_not_exists, 
							Toast.LENGTH_SHORT).show();
					break;
				case 2:
					Toast.makeText(LoginActivity.this, R.string.user_name_can_not_match_password, 
							Toast.LENGTH_SHORT).show();
					break;
				case 0:
					Toast.makeText(LoginActivity.this, R.string.account_login_success, 
							Toast.LENGTH_SHORT).show();
					startActivity(new Intent(LoginActivity.this, MainActivity.class));
					break;
				}
				super.onPostExecute(result);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    /**使用SSO授权必须添加如下代码 */  
	    UMSsoHandler ssoHandler = AuthUtils.mController.getConfig().getSsoHandler(requestCode);
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
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
