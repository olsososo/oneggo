package com.oneggo.snacks.ui;

import java.util.ArrayList;
import java.util.Arrays;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import com.oneggo.snacks.R;
import com.oneggo.snacks.AppData;
import com.oneggo.snacks.dao.CategoriesDataHelper;
import com.oneggo.snacks.dao.Tables;
import com.oneggo.snacks.datatype.Category;
import com.oneggo.snacks.util.AuthUtils;
import com.oneggo.snacks.util.CommonUtils;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PushContentSettingActivity extends SwipeBackActivity implements OnClickListener{
	
	private Button back;
	
	private ListView listView;
	
	private CategoriesDataHelper mDataHelper;
	
	private Cursor cursor;
	
	private SharedPreferences sharedPreferences;
	
	private SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setupViews();
		
		sharedPreferences = getSharedPreferences(AppData.TAG, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		
		mDataHelper = new CategoriesDataHelper(this);
		cursor = mDataHelper.query();
		
		MyCursorAdapter adapter = new MyCursorAdapter(this, cursor);
		listView.setAdapter(adapter);
	}
	
	private String getPushContent() {
		String pushContent = sharedPreferences.getString(AppData.PUSHCONTENT, null);
		return pushContent;
	}
	
	private void addPushContent(String name) {
		String pushContent = sharedPreferences.getString(AppData.PUSHCONTENT, null);
		if(pushContent != null){
			String[] pushContens = pushContent.split(",");
			if(!Arrays.asList(pushContens).contains(name)){
				pushContent = pushContent + "," + name;
				editor.putString(AppData.PUSHCONTENT, pushContent).commit();
			}
		}else{
			editor.putString(AppData.PUSHCONTENT, name).commit();
		}		
	}
	
	private void removePushContent(String name) {
		String pushContent = sharedPreferences.getString(AppData.PUSHCONTENT, null);
		if(pushContent != null){
			String[] pushContens = pushContent.split(",");
			ArrayList<String> list = new ArrayList<String>();
			
			for(String str : pushContens){
				if(!str.equals(name)){
					list.add(str);
				}
			}
			
			if(list.size() != 0){
				editor.putString(AppData.PUSHCONTENT, TextUtils.join(",", list.toArray())).commit();
			}else{
				editor.putString(AppData.PUSHCONTENT, null).commit();
			}
		}
	}
	
	private class MyCursorAdapter extends CursorAdapter{
		private LayoutInflater mInflater;
		private String json;
		
		public MyCursorAdapter(Context context, Cursor c) {
			super(context, c, true);
			mInflater = LayoutInflater.from(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// TODO Auto-generated method stub
			TextView title = (TextView) view.findViewById(R.id.title);
			CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox); 
			
			json = cursor.getString(cursor.getColumnIndex(Tables.CategoriesDBInfo.JSON));
			Category category = Category.fromJson(json);
			
			title.setText(category.getName());
			checkBox.setTag(category.getName());

			String pushContent = getPushContent();
			if(pushContent != null){
				String[] pushContents = pushContent.split(",");
				if(Arrays.asList(pushContents).contains(String.valueOf(category.getName()))){
					checkBox.setChecked(true);
				}
			}
			
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton view, boolean isChecked) {
					// TODO Auto-generated method stub
					
					if(!CommonUtils.isOnline(PushContentSettingActivity.this)){
						Toast.makeText(PushContentSettingActivity.this, 
							R.string.sync_push_setting_network_error, Toast.LENGTH_SHORT).show();;
						return;
					}
					
					String name = String.valueOf(view.getTag());
					
					if(isChecked){
						addPushContent(name);
					}else{
						removePushContent(name);
					}
				}
			});
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			return mInflater.inflate(R.layout.listitem_contentpush, null);
		}
		
	}
	
	private void setupViews() {
		setContentView(R.layout.activity_pushcontent);

		back = (Button) findViewById(R.id.back);
		listView = (ListView) findViewById(R.id.listView);
		
		back.setOnClickListener(this);
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
