package com.oneggo.snacks.ui;

import java.util.ArrayList;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.oneggo.snacks.R;
import com.oneggo.snacks.AppData;
import com.oneggo.snacks.adapter.CardsAnimationAdapter;
import com.oneggo.snacks.adapter.SubjectItemAdapter;
import com.oneggo.snacks.data.GsonRequest;
import com.oneggo.snacks.data.RequestManager;
import com.oneggo.snacks.datatype.Product;
import com.oneggo.snacks.datatype.SubjectItem;
import com.oneggo.snacks.util.CommonUtils;
import com.oneggo.snacks.vendor.Api;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SubjectActivity extends SwipeBackActivity implements OnClickListener,
	OnRefreshListener{
	
	private long subjectId;
	
	private Button back;
	
	private ImageView photo;
	
	private TextView title;
	
	private TextView description;
	
	private View head;
	
	private ListView listView;
	
	public SubjectItemAdapter adapter;
	
	public ArrayList<Product> products;
	
	private SwipeRefreshLayout swipeLayout;
	
	private Drawable mDefaultImageDrawable = new ColorDrawable(Color.argb(255, 201, 201, 201));
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		subjectId = bundle.getLong("subjectId");
		
		setupViews();
		
		products = new ArrayList<Product>();
		adapter = new SubjectItemAdapter(products, this);
	    AnimationAdapter animationAdapter = new CardsAnimationAdapter(adapter);
	    animationAdapter.setListView(listView);
	    listView.setAdapter(adapter);
	    
	    listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
			}
	    	
	    });
	    
		loadData();
	}

	private void setupViews() {
		setContentView(R.layout.activity_subject);
		
		back = (Button) findViewById(R.id.back);
		head = LayoutInflater.from(this).inflate(R.layout.view_subject_head, null);
		photo = (ImageView) head.findViewById(R.id.photo);
		description = (TextView) head.findViewById(R.id.description);
		title = (TextView) findViewById(R.id.title);
		listView = (ListView) findViewById(R.id.listView);
		listView.addHeaderView(head, null, false);
		
		back.setOnClickListener(this);
		
		initSwipeLayout();
		
		custom();
	}
	
	private void initSwipeLayout() {
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
	    swipeLayout.setOnRefreshListener(this);
	    swipeLayout.setColorScheme(R.color.holo_blue_bright, 
	            R.color.holo_green_light, 
	            R.color.holo_orange_light, 
	            R.color.holo_red_light);		
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

	private void loadData() {
		if(!CommonUtils.isOnline(this)){
			Toast.makeText(this, R.string.check_your_network_environment, Toast.LENGTH_SHORT).show();
			return;
		}
		
		swipeLayout.setRefreshing(true);
		
		executeRequest(new GsonRequest<>(String.format(Api.SUBJECTITEM, String.valueOf(subjectId)),
			SubjectItem.class,
			null,
			new Response.Listener<SubjectItem>(){

				@Override
				public void onResponse(SubjectItem response) {
					// TODO Auto-generated method stub
					description.setText(Html.fromHtml(response.getDescription()));
					title.setText(Html.fromHtml(response.getTitle()));
					RequestManager.loadImage(response.getPhoto(), RequestManager.getImageListener(
							photo, mDefaultImageDrawable, mDefaultImageDrawable));
					
					products.clear();
					
					ArrayList<Product> arrayList = response.getProducts();
					for(Product product : arrayList){
						products.add(product);
					}
					
					adapter.notifyDataSetChanged();
					swipeLayout.setRefreshing(false);
				}
			
			},
			new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					swipeLayout.setRefreshing(false);
				}
			}
		));
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
