package com.oneggo.snacks.ui;

import java.util.ArrayList;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.waps.AppConnect;

import com.oneggo.snacks.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.oneggo.snacks.AppData;
import com.oneggo.snacks.adapter.SonCategoryAdapter;
import com.oneggo.snacks.dao.CategoriesDataHelper;
import com.oneggo.snacks.dao.Tables;
import com.oneggo.snacks.data.GsonRequest;
import com.oneggo.snacks.data.RequestManager;
import com.oneggo.snacks.datatype.Category;
import com.oneggo.snacks.datatype.Check;
import com.oneggo.snacks.fragment.CategoryFragment;
import com.oneggo.snacks.fragment.PersonFragment;
import com.oneggo.snacks.fragment.ProductFragment;
import com.oneggo.snacks.util.CommonUtils;
import com.oneggo.snacks.vendor.Api;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends SlidingFragmentActivity implements OnClickListener{
	
	private ImageView listIcon;
	private ImageView personIcon;
	private Category mCategory;
	private Spinner selector;
	private ProductFragment mContentFragment;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private ArrayList<Category> categories;
	private SonCategoryAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		init();
		
		setupViews();
		
		AppConnect.getInstance("e6226e579d95ec0df34e245eb17158bf", "waps", this);
		
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
		
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(MainActivity.this);
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为自动消失
        builder.notificationDefaults = Notification.DEFAULT_SOUND;  // 设置为铃声
        JPushInterface.setPushNotificationBuilder(1, builder);
  
        BasicPushNotificationBuilder builder2 = new BasicPushNotificationBuilder(MainActivity.this);
        builder2.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为自动消失
        builder2.notificationDefaults = Notification.DEFAULT_VIBRATE;  // 设置为震动
        JPushInterface.setPushNotificationBuilder(2, builder2);
      
        BasicPushNotificationBuilder builder3 = new BasicPushNotificationBuilder(MainActivity.this);
        builder3.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为自动消失
        builder3.notificationDefaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;  // 设置为铃声与震动都要
        JPushInterface.setPushNotificationBuilder(3, builder3);
        
        sharedPreferences = getSharedPreferences(AppData.TAG, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        
        boolean shortcut = sharedPreferences.getBoolean("shortcut", true);
        if(shortcut) shortcut();
	}
	
	private void shortcut() {
		editor.putBoolean("shortcut", false).commit();

		Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        String title = getResources().getString(R.string.app_name);
        Parcelable icon = Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher);
        
        Intent myIntent = new Intent(this, MainActivity.class);
        
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);
        
        sendBroadcast(addIntent);
	}
	
	private void setupViews() {
		setContentView(R.layout.activity_main);

		listIcon = (ImageView) findViewById(R.id.list_icon);
        personIcon = (ImageView) findViewById(R.id.person_icon);
        selector = (Spinner) findViewById(R.id.selector);
		
		categories = new ArrayList<Category>();
		adapter = new SonCategoryAdapter(categories, this);          
        selector.setAdapter(adapter); 
        selector.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Category category = (Category) arg1.getTag();
				setCategory(category);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
        listIcon.setOnClickListener(this);
        personIcon.setOnClickListener(this);

        initSlideMenu();
		
		custom();
	}
	
	private void custom() {
		SharedPreferences sharedPreferences = getSharedPreferences(AppData.TAG, MODE_PRIVATE);
		RelativeLayout menu = (RelativeLayout) findViewById(R.id.menu);
		
		String myColor = sharedPreferences.getString("color", null);
		
		if(myColor != null){
			int indentify = getResources().getIdentifier(myColor, 
					"color", getPackageName());
					
			menu.setBackgroundColor(getResources().getColor(indentify));
		}
	}
	
	private void initSlideMenu() {
		SlidingMenu sm = getSlidingMenu();
		sm.setMode(SlidingMenu.LEFT_RIGHT);
		
		setBehindContentView(R.layout.menu_category_frame);
		sm.setSlidingEnabled(true);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		sm.setShadowWidthRes(R.dimen.shadow_width);	
		sm.setShadowDrawable(R.drawable.shadow);
		
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_category_frame, new CategoryFragment())
		.commit();
		
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setBehindScrollScale(0);
		sm.setFadeDegree(0.25f);
		
		sm.setSecondaryMenu(R.layout.menu_person_frame);
		sm.setSecondaryShadowDrawable(R.drawable.shadow);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_person_frame, new PersonFragment(this))
		.commit();
	}
	
    protected void executeRequest(Request request) {
        RequestManager.addRequest(request, this);
    }
    
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		JPushInterface.onPause(this);
		MobclickAgent.onPause(this);
		super.onPause();
	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		AppConnect.getInstance(this).close();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		JPushInterface.onResume(this);
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
	
	public void setCategory(Category category) {
		if(mCategory == category) return;
		
		mCategory = category;
		mContentFragment = ProductFragment.newInstance(category);
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, 
				mContentFragment).commit();
		
		
		if(category.getPid() == 0){
			CategoriesDataHelper categoriesDataHelper = new CategoriesDataHelper(this);
			Cursor cursor = categoriesDataHelper.sonCategories(category.getId());
			categories.clear();
			
			categories.add(category);
			while(cursor.moveToNext()){
				Category c = Category.fromJson(cursor.getString(cursor.getColumnIndex(Tables.CategoriesDBInfo.JSON)));
				categories.add(c);
			}
			
			adapter.notifyDataSetChanged();	
		}
		
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.list_icon:
			toggle();
			break;
		case R.id.person_icon:
			showSecondaryMenu();
			break;
		}
	}
	
	private void init() {
		if(!CommonUtils.isOnline(this)) return;
		
		executeRequest(new GsonRequest<>(
			String.format(Api.CHECK, "", "", ""), 
			Check.class, 
			null, 
			new Response.Listener<Check>() {

				@Override
				public void onResponse(Check response) {
					// TODO Auto-generated method stub
					long categoryLastUpdated = response.getCategory_last_updated();
					long subjectLastUpdated = response.getSubject_last_updated();
					
					editor.putLong("serverCategoryLastUpdated", categoryLastUpdated).commit();
					editor.putLong("serverSubjectLastUpdated", subjectLastUpdated).commit();
				}
			}, 
			new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
				}
			}
		));
	}
}
