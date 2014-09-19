package com.oneggo.snacks.ui;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import com.etsy.android.grid.StaggeredGridView;
import com.oneggo.snacks.R;
import com.oneggo.snacks.AppData;
import com.oneggo.snacks.adapter.ColorAdapter;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

public class SetColorActivity extends SwipeBackActivity implements OnScrollListener, 
	OnItemClickListener, OnClickListener{
	
	private Button back;
	
	private StaggeredGridView staggeredGridView;
	
	private ColorAdapter colorAdapter;
	
	private RelativeLayout menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setupViews();
		
		String[] colors = getResources().getStringArray(R.array.colors);
		
		colorAdapter = new ColorAdapter(this, R.id.txt_line, colors, menu);
		staggeredGridView.setAdapter(colorAdapter);
	}
	
	private void setMenuColor() {
		SharedPreferences sharedPreferences = getSharedPreferences(AppData.TAG, Context.MODE_PRIVATE);
		String myColor = sharedPreferences.getString("color", null);
		if(myColor != null){
			int indentify = getResources().getIdentifier(myColor, 
					"color", getPackageName());
			menu.setBackgroundColor(getResources().getColor(indentify));
		}
	}
	
	private void setupViews() {
		setContentView(R.layout.activity_setcolor);
		
		back = (Button) findViewById(R.id.back);
		menu = (RelativeLayout) findViewById(R.id.menu);
		staggeredGridView = (StaggeredGridView) findViewById(R.id.grid_view);
		back.setOnClickListener(this);
		staggeredGridView.setOnItemClickListener(this);
		staggeredGridView.setOnScrollListener(this);
		
		setMenuColor();
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.back:
			onBackPressed();
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
