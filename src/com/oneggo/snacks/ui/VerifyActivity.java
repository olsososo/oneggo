package com.oneggo.snacks.ui;

import java.net.HttpURLConnection;
import java.util.HashMap;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import org.json.JSONObject;

import com.oneggo.snacks.R;
import com.oneggo.snacks.AppData;
import com.oneggo.snacks.util.CommonUtils;
import com.oneggo.snacks.vendor.Api;
import com.umeng.analytics.MobclickAgent;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

public class VerifyActivity extends SwipeBackActivity implements OnClickListener{
	
	private Button back;
	
	private EditText verifyCode;
	
	private Button submit;
	
	private Dialog dialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setupViews();
	}
	
	private void setupViews() {
		setContentView(R.layout.activity_verify);
		
		back = (Button) findViewById(R.id.back);
		verifyCode = (EditText) findViewById(R.id.verify_code);
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
			verify();
			break;
		}
	}
	
	private void verify() {
		String verifyCodeText = verifyCode.getText().toString().trim();
		if(TextUtils.isEmpty(verifyCodeText)){
			Toast.makeText(this, R.string.verify_code_can_not_empty, Toast.LENGTH_SHORT).show();;
			return;
		}
		
		if(!CommonUtils.isOnline(this)){
			Toast.makeText(this, R.string.check_your_network_environment, 
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(dialog == null){
			dialog = CommonUtils.creatRequestDialog(this, getString(R.string.verifying));
		}
		
		dialog.show();
		
		SharedPreferences sharedPreferences = this.getSharedPreferences(AppData.TAG, Context.MODE_PRIVATE);
		HashMap<String, String> data = new HashMap<String, String>();
		final String nickname = sharedPreferences.getString("nickname", null);
		data.put("nickname", nickname);
		data.put("verify", verifyCodeText);
		data.put("site", AppData.Site);		
		
		final CommonUtils.AsyncHttpPost asyncHttpPost = new CommonUtils.AsyncHttpPost(this, data);
		CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Integer>(){

			@Override
			protected Integer doInBackground(Object... arg0) {
				// TODO Auto-generated method stub
				try {
					HashMap<String, Object> response = asyncHttpPost.execute(Api.VERIFY).get();
					if(response == null || (Integer)response.get("statusCode") != HttpURLConnection.HTTP_OK){
						if(response != null){
							Log.d(AppData.TAG, "验证重设密码时服务器出错:"+response.toString());
						}
						return -1;
					}
					
					String jsonString = (String) response.get("body");
					JSONObject json = new JSONObject(jsonString);
					int status = json.getInt("status");
					
					if(status == 0){
						String token = json.getString("token");
						
						SharedPreferences sharedPreferences = VerifyActivity.this.getSharedPreferences(
								AppData.TAG, Context.MODE_PRIVATE);
						Editor editor = sharedPreferences.edit();
						editor.putString("nickname", nickname);
						editor.putString("token", token);
						editor.commit();	
					}
					
					return status;
					
				} catch(Exception e){
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
					Toast.makeText(VerifyActivity.this, R.string.server_exception_occurs, 
							Toast.LENGTH_SHORT).show();
					break;
				case -2:
					Toast.makeText(VerifyActivity.this, R.string.get_server_data_error, 
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(VerifyActivity.this, R.string.verify_code_error, 
							Toast.LENGTH_SHORT).show();
					break;	
				case 0:
					startActivity(new Intent(VerifyActivity.this, ResetPasswordActivity.class));
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
