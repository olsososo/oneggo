package com.oneggo.snacks.ui;

import java.util.ArrayList;
import java.util.HashMap;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.oneggo.snacks.R;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.oneggo.snacks.AppData;
import com.oneggo.snacks.adapter.CardsAnimationAdapter;
import com.oneggo.snacks.adapter.LikeAdapter;
import com.oneggo.snacks.data.GsonRequest;
import com.oneggo.snacks.data.RequestManager;
import com.oneggo.snacks.datatype.Like;
import com.oneggo.snacks.datatype.Product;
import com.oneggo.snacks.util.AuthUtils;
import com.oneggo.snacks.util.CommonUtils;
import com.oneggo.snacks.vendor.Api;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class LikeActivity extends SwipeBackActivity implements OnClickListener,
	OnRefreshListener{
	
	private SwipeListView mlistView;
	
	private ScrollView showEmptyMessage;
	
	private Button back;
	
	private ArrayList<Product> products;
	
	private LikeAdapter mAdapter;
	
	private SwipeRefreshLayout swipeLayout;
	
	public static final String EXTRA_PRODUCT = "EXTRA_PRODUCT";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		setupViews();
		
		products = new ArrayList<Product>();
		mAdapter = new LikeAdapter(this, products, mlistView, showEmptyMessage);
		AnimationAdapter animationAdapter = new CardsAnimationAdapter(mAdapter);
		animationAdapter.setListView(mlistView);
		mlistView.setAdapter(animationAdapter);
		
		loadData();
		
        mlistView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onClickFrontView(int position) {
				Product product = mAdapter.getItem(position);
				if(product != null){
					Bundle bundle = new Bundle();
					bundle.putSerializable(EXTRA_PRODUCT, product);
					Intent intent = new Intent(LikeActivity.this, BrowserActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);					
				}	
            }
            
            @Override
            public void onClosed(int position, boolean fromRight) {
        		if(products.size() == 0){
        			showEmptyMessage.setVisibility(View.VISIBLE);
        		}else{
        			showEmptyMessage.setVisibility(View.GONE);
        		}     	
            }	
        });
	}
	
	private void setupViews() {
		setContentView(R.layout.activity_like);
		
		back = (Button) findViewById(R.id.back);
		mlistView = (SwipeListView) findViewById(R.id.listView);
		showEmptyMessage = (ScrollView) findViewById(R.id.showEmptyMessage);
		
		back.setOnClickListener(this);
		
		initSwipeLayout();
		
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
	
	private void initSwipeLayout() {
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
	    swipeLayout.setOnRefreshListener(this);
	    swipeLayout.setColorScheme(R.color.holo_blue_bright, 
	            R.color.holo_green_light, 
	            R.color.holo_orange_light, 
	            R.color.holo_red_light);		
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
	public void onRefresh() {
		// TODO Auto-generated method stub
		loadData();
	}

	private void loadData() {
		if(!CommonUtils.isOnline(this)){
			Toast.makeText(this, R.string.check_your_network_environment, Toast.LENGTH_SHORT).show();
			return;
		}
		
		swipeLayout.setRefreshing(true);
		
		HashMap<String, String> user = AuthUtils.getLoginUser(this);
		executeRequest(new GsonRequest<>(
				String.format(Api.LIKE, user.get("id"), user.get("sessionid")), 
				Like.class, null,
				new Response.Listener<Like>() {

					@Override
					public void onResponse(final Like response) {
						// TODO Auto-generated method stub
						long statusCode = response.getStatusCode();
						
						if(statusCode == 0){
							ArrayList<Product> arrayList = response.getProducts();
							products.clear();
							
							for(Product product : arrayList){
								products.add(product);
							}
							
							if(products.size() == 0){
								showEmptyMessage.setVisibility(View.VISIBLE);
							}else{
								showEmptyMessage.setVisibility(View.GONE);
							}
							
							mAdapter.notifyDataSetChanged();
						}else{
							String message = response.getMessage();
							Toast.makeText(LikeActivity.this, message, Toast.LENGTH_SHORT).show();
						}
						swipeLayout.setRefreshing(false);						
					}
				}, 
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Toast.makeText(LikeActivity.this, CommonUtils.volleyErrorMessage(error),
								Toast.LENGTH_SHORT).show();
						swipeLayout.setRefreshing(false);
					}
				}
			)
		);
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
