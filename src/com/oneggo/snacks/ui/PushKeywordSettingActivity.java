package com.oneggo.snacks.ui;

import java.util.ArrayList;
import java.util.Arrays;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import com.oneggo.snacks.R;
import com.oneggo.snacks.AppData;
import com.oneggo.snacks.util.AuthUtils;
import com.oneggo.snacks.util.CommonUtils;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class PushKeywordSettingActivity extends SwipeBackActivity implements OnClickListener{
	
	private Button back;
	
	private EditText keyword;
	
	private ImageView addKeyword;
	
	private LinearLayout keywordList;
	
	private SharedPreferences sharedPreferences;
	
	private SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setupViews();
		
		sharedPreferences = this.getSharedPreferences(AppData.TAG, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		
		String string = getPushKeyword();
		if(string != null){
			String[] keywords = string.split(",");
			RelativeLayout view = null;
			for(String str : keywords){
				view = getKeywordView(str);
				keywordList.addView(view);
			}
		}
	}
	
	public View getLine(boolean top) {
		View line = new View(this);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
		if(top){
			lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		}else{
			lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		}
		line.setLayoutParams(lp);
		line.setBackgroundColor(getResources().getColor(R.color.v1_push_border));
		return line;
	}
	
	
	public RelativeLayout getKeywordView(final String keyword) {
		final RelativeLayout view = new RelativeLayout(this);
		LayoutParams lp = new LayoutParams(
				LayoutParams.MATCH_PARENT, CommonUtils.convertDipToPx(this, 50));
		lp.setMargins(CommonUtils.convertDipToPx(this, 20), CommonUtils.convertDipToPx(this, 15),
				CommonUtils.convertDipToPx(this, 20), 0);
		view.setLayoutParams(lp);
		
		LinearLayout content = new LinearLayout(this);
		content.setOrientation(LinearLayout.HORIZONTAL);
		content.setGravity(Gravity.CENTER_VERTICAL);
		
		LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, CommonUtils.convertDipToPx(this, 48));
		clp.gravity = RelativeLayout.CENTER_VERTICAL;
		content.setLayoutParams(clp);
		
		TextView tv = new TextView(this);
		LayoutParams tlp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
		
		tv.setLayoutParams(tlp);
		tv.setText(keyword);
		tv.setPadding(CommonUtils.convertDipToPx(this, 10), 0, 0, 0);
		tv.setGravity(Gravity.CENTER_VERTICAL);
		
		ImageView iv = new ImageView(this);
		LayoutParams ilp = new LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT);
		iv.setLayoutParams(ilp);
		iv.setImageResource(R.drawable.remove);
		iv.setPadding(0, 0, CommonUtils.convertDipToPx(this, 20), 0);
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!CommonUtils.isOnline(PushKeywordSettingActivity.this)){
					Toast.makeText(PushKeywordSettingActivity.this, 
						R.string.sync_push_setting_network_error, Toast.LENGTH_SHORT).show();
					return;
				}
				
				removePushKeyword(keyword);
				view.setVisibility(View.GONE);
			}
		});
		
		content.addView(tv);
		content.addView(iv);
		
		view.addView(getLine(true));
		view.addView(content);
		view.addView(getLine(false));
		return view;
	}
	
	public String getPushKeyword() {
		String pushKeyword = sharedPreferences.getString(AppData.PUSHKEYWORD, null);
		return pushKeyword;
	}
	
	public boolean addPushKeyword(String keyword) {
		boolean status = true;
		
		String pushKeyword = getPushKeyword();
		if(pushKeyword != null){
			String[] pushKeywords = pushKeyword.split(",");
			if(!Arrays.asList(pushKeywords).contains(keyword)){
				pushKeyword = pushKeyword + "," + keyword;
				editor.putString(AppData.PUSHKEYWORD, pushKeyword).commit();
			}else{
				status = false;
			}			
		}else{
			editor.putString(AppData.PUSHKEYWORD, keyword).commit();
		}
		
		return status;
	}
	
	public void removePushKeyword(String keyword) {
		String pushKeyword = sharedPreferences.getString(AppData.PUSHKEYWORD, null);
		if(pushKeyword != null){
			String[] pushKeywords = pushKeyword.split(",");
			ArrayList<String> list = new ArrayList<String>();
			
			for(String str : pushKeywords){
				if(!str.equals(keyword)){
					list.add(str);
				}
			}
			
			if(list.size() != 0){
				editor.putString(AppData.PUSHKEYWORD, TextUtils.join(",", list.toArray())).commit();
			}else{
				editor.putString(AppData.PUSHKEYWORD, null).commit();
			}
		}		
	}
	
	private void addKeyword() {
		String str = keyword.getText().toString().trim();
		
		if(TextUtils.isEmpty(str)){
			Toast.makeText(this, R.string.keyword_can_not_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(!AuthUtils.validateKeyword(str)){
			Toast.makeText(this, R.string.keyword_format_error, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(addPushKeyword(str)){
			keywordList.addView(getKeywordView(str));
			keyword.setText("");
		}else{
			Toast.makeText(this, R.string.keyword_exists, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void setupViews() {
		setContentView(R.layout.activity_pushkeyword);
		
		back = (Button) findViewById(R.id.back);
		keyword = (EditText) findViewById(R.id.keyword);
		addKeyword = (ImageView) findViewById(R.id.addKeyword);
		keywordList = (LinearLayout) findViewById(R.id.keywordList);
		
		back.setOnClickListener(this);
		addKeyword.setOnClickListener(this);
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
		case R.id.addKeyword:
			if(!CommonUtils.isOnline(this)){
				Toast.makeText(this, R.string.sync_push_setting_network_error, 
						Toast.LENGTH_SHORT).show();
			}else{
				addKeyword();
			}
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
}
