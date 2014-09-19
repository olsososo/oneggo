package com.oneggo.snacks.ui;

import java.io.File;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.oneggo.snacks.R;
import com.oneggo.snacks.AppData;
import com.oneggo.snacks.data.GsonRequest;
import com.oneggo.snacks.data.RequestManager;
import com.oneggo.snacks.datatype.Check;
import com.oneggo.snacks.receiver.DownLoadReceive;
import com.oneggo.snacks.util.CommonUtils;
import com.oneggo.snacks.util.SystemDownLoad;
import com.oneggo.snacks.vendor.Api;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MoreActivity extends SwipeBackActivity implements OnClickListener, OnCheckedChangeListener{
	
	private Button back;
	
	private TextView versonName;
	
	private RelativeLayout setColor;
	
	private RelativeLayout versonUpdate;
	
	private RelativeLayout setSwipe;
	
	private RelativeLayout aboutUs;
	
	private LinearLayout dialogSetSwipe;
	
	private RadioGroup edgeFlagGroup;
	
	private Handler handler;
	
	private Dialog dialog = null;
	
	private String versonname;
	
	private String description;
	
	private String path;
	
	private DownLoadReceive receiver;
	
	private Dialog setSwipDialog = null;
	
	private SharedPreferences sharedPreferences;
	
	private Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		sharedPreferences = getSharedPreferences(AppData.TAG, MODE_PRIVATE);
		editor = sharedPreferences.edit();
		
		setupViews();
		
		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String versionName = pinfo.versionName;
			
			versonName.setText(versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			versonName.setText(R.string.unknown);
			e.printStackTrace();
		}
		
		receiver = new DownLoadReceive();
		IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		registerReceiver(receiver, intentFilter);
		
		handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what != 0x123) return;
				
				String savePath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/download";
				File file = new File(savePath);
				
				if(!file.exists()){
					file.mkdirs();
				}
				
				SystemDownLoad systemDownLoad = new SystemDownLoad(MoreActivity.this, 
						path, AppData.PACKAGENAME, "/download");
				systemDownLoad.downLoad();
				super.handleMessage(msg);
			}
		};
	}

	private void setupViews() {
		setContentView(R.layout.activity_more);
		
		back = (Button) findViewById(R.id.back);
		versonName = (TextView) findViewById(R.id.verson_name);
		setColor = (RelativeLayout) findViewById(R.id.set_color);
		versonUpdate = (RelativeLayout) findViewById(R.id.verson_update);
		setSwipe = (RelativeLayout) findViewById(R.id.set_swipe);
		aboutUs = (RelativeLayout) findViewById(R.id.about_us);
		
		dialogSetSwipe = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.dialog_set_swipe, null);
		edgeFlagGroup = (RadioGroup) dialogSetSwipe.findViewById(R.id.edge_flag);
		
		setSwipDialog = new Dialog(this, R.style.dialog);
		setSwipDialog.setContentView(dialogSetSwipe);
		Window window = setSwipDialog.getWindow();
		WindowManager.LayoutParams slp = window.getAttributes();	
		int width = CommonUtils.getScreenWidth(this);	
		slp.width = (int)(0.6 * width);	
		
		int currentEdgeModel = sharedPreferences.getInt("edgemodel", 1);
		switch(currentEdgeModel){
		case 0:
			edgeFlagGroup.check(R.id.all);
			break;
		case 1:
			edgeFlagGroup.check(R.id.left);
			break;
		case 2:
			edgeFlagGroup.check(R.id.right);
			break;
		case 3:
			edgeFlagGroup.check(R.id.bottom);
			break;
		}
		
		back.setOnClickListener(this);
		setSwipe.setOnClickListener(this);
		setColor.setOnClickListener(this);
		versonUpdate.setOnClickListener(this);
		aboutUs.setOnClickListener(this);
		edgeFlagGroup.setOnCheckedChangeListener(this);
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
	
	private void versonUpdate() {
		if(!CommonUtils.isOnline(this)){
			Toast.makeText(this, R.string.check_your_network_environment, 
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(dialog == null){
			dialog = CommonUtils.creatRequestDialog(this, getString(R.string.checking_update));
		}
		
		dialog.show();
		
		String versionName = "";
		String packageName = getPackageName();
		
		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionName = pinfo.versionName;
			
			versonName.setText(versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			versonName.setText(R.string.unknown);
			e.printStackTrace();
		}
		
		Log.d(AppData.TAG, String.format(Api.CHECK, "", packageName, versionName));
		
		executeRequest(new GsonRequest<>(
				String.format(Api.CHECK, "0", packageName, versionName), 
				Check.class,
				null,
				new Response.Listener<Check>() {

					@Override
					public void onResponse(Check response) {
						// TODO Auto-generated method stub
						dialog.cancel();
						
						if(response.getApp() != null){
							
							versonname = response.getApp().getVersonname();
							description = response.getApp().getDescription();
							path = response.getApp().getPath();
							
							new AlertDialog.Builder(MoreActivity.this)
							.setTitle(getString(R.string.found_new_verson) + versonname + "," + getString(R.string.whether_to_update))
							.setMessage(Html.fromHtml(description))
							.setPositiveButton(R.string.y, new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									handler.sendEmptyMessage(0x123);
								}
							})
							.setNegativeButton(R.string.n, new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									
								}
							}).create().show();
						    
						}else{
							Toast.makeText(MoreActivity.this, R.string.lastest_verson, 
									Toast.LENGTH_SHORT).show();
						}
					}
				},
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub						
						Toast.makeText(MoreActivity.this, CommonUtils.volleyErrorMessage(error), 
								Toast.LENGTH_SHORT).show();
					}
				}
		));
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.back:
			onBackPressed();
			break;
		case R.id.set_color:
			startActivity(new Intent(this, SetColorActivity.class));
			break;
		case R.id.verson_update:
			versonUpdate();
			break;
		case R.id.set_swipe:
			setSwipDialog.show();
			break;
		case R.id.about_us:
			startActivity(new Intent(this, AboutUsActivity.class));
			break;
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

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		RequestManager.cancelAll(this);
	}
	
    protected void executeRequest(Request request) {
        RequestManager.addRequest(request, this);
    }

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		int val = 0;
		switch(arg1){
		case R.id.left:
			val = 1;
			break;
		case R.id.right:
			val = 2;
			break;
		case R.id.bottom:
			val = 3;
			break;
		case R.id.all:
			val = 0;
			break;
		}	
		
		editor.putInt("edgemodel", val).commit();
		Toast.makeText(this, R.string.setting_success, Toast.LENGTH_SHORT).show();
	}	
}
