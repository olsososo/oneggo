package com.oneggo.snacks.ui;

import java.net.HttpURLConnection;
import java.util.HashMap;

import org.json.JSONObject;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oneggo.snacks.R;
import com.oneggo.snacks.AppData;
import com.oneggo.snacks.dao.CategoriesDataHelper;
import com.oneggo.snacks.dao.ProductsDataHelper;
import com.oneggo.snacks.dao.Tables.ProductsDBInfo;
import com.oneggo.snacks.data.GsonRequest;
import com.oneggo.snacks.data.RequestManager;
import com.oneggo.snacks.datatype.Action;
import com.oneggo.snacks.datatype.Product;
import com.oneggo.snacks.datatype.Record;
import com.oneggo.snacks.util.AuthUtils;
import com.oneggo.snacks.util.CommonUtils;
import com.oneggo.snacks.vendor.Api;
import com.oneggo.snacks.view.BrowserView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class BrowserActivity extends SwipeBackActivity implements OnRefreshListener,
	OnClickListener{
	
	private BrowserView webView;
	
	private Button back;
	
	private ImageView like;
	
	private ImageView share;
	
	private ImageView previous;
	
	private ImageView next;
	
	private ImageView refresh;
	
	private ImageView browser;
	
	private Product product;
	
	private ProductsDataHelper productsDataHelper;
	
	private RelativeLayout browserControlLayout;
	
	private SwipeRefreshLayout swipeLayout;
	
	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share",
            RequestType.SOCIAL);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setupViews();
		
		Intent intent = getIntent();
		product = (Product) intent.getSerializableExtra(AppData.EXTRA_PRODUCT);
		
		updateRecord(this, Action.addview.getString(), product.getId());
		
		like.setSelected(false);
		
		if(AuthUtils.isLogin(this)){
			isLike();
		}
		
		productsDataHelper = new ProductsDataHelper(this,
				new CategoriesDataHelper(this).query(product.getPid()));
		
		webView.loadUrl(product.getUrl());
		webView.setWebViewClient(webViewClient);
		WebSettings settings = webView.getSettings();
		settings.setDomStorageEnabled(true);
		settings.setJavaScriptEnabled(true);
	}
	
	private WebViewClient webViewClient = new WebViewClient(){

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			swipeLayout.setRefreshing(true);
			super.onPageStarted(view, url, favicon);
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			swipeLayout.setRefreshing(false);
			super.onPageFinished(view, url);
		}	
	};
	
	private void setupViews() {
		setContentView(R.layout.activity_browser);

		back = (Button) findViewById(R.id.back);
		like = (ImageView) findViewById(R.id.like);
		share = (ImageView) findViewById(R.id.share);
		
		previous = (ImageView) findViewById(R.id.previous);
		next = (ImageView) findViewById(R.id.next);
		refresh = (ImageView) findViewById(R.id.refresh);
		browser = (ImageView) findViewById(R.id.browser);
		webView = (BrowserView) findViewById(R.id.webView);
		browserControlLayout = (RelativeLayout) findViewById(R.id.browserControl);

		back.setOnClickListener(this);
		share.setOnClickListener(this);
		like.setOnClickListener(this);
		previous.setOnClickListener(this);
		next.setOnClickListener(this);
		refresh.setOnClickListener(this);
		browserControlLayout.setOnClickListener(this);
		browser.setOnClickListener(this);
		
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
	
	private void onClickBack() {
		onBackPressed();
	}
	
	private void onClickShare() {
		mController.setShareContent(product.getTitle()+","+product.getShort_url());
//		mController.setShareMedia(new UMImage(this, 
//                product.getPhoto()));
		mController.openShare(this, false);		
	}

	private void onClickLike() {
		if(!AuthUtils.isLogin(this)){
			Toast.makeText(this, R.string.please_login, Toast.LENGTH_SHORT).show();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		}
		
		if(like.isSelected()){
			updateRecord(this, Action.removelike.getString(), product.getId());
		}else{
			updateRecord(this, Action.addlike.getString() ,product.getId());
		}		
	}
	
	private void onClickPrevious() {
		if(webView.canGoBack()){
			webView.goBack();
		}else{
			Toast.makeText(this, R.string.already_first_page, Toast.LENGTH_SHORT).show();
		}		
	}
	
	private void onClickNext() {
		if(webView.canGoForward()){
			webView.goForward();
		}else{
			Toast.makeText(this, R.string.already_last_page, Toast.LENGTH_SHORT).show();
		}		
	}
	
	private void onClickRefresh() {
		webView.reload();
	}
	
	private void onClickBrowserControlLayout() {
		
	}
	
	private void onClickBrowser() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri uri = Uri.parse(product.getUrl());
		intent.setData(uri);
		startActivity(intent);	
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.back:
			onClickBack();
			break;
		case R.id.share:
			onClickShare();
			break;
		case R.id.like:
			onClickLike();
			break;
		case R.id.previous:
			onClickPrevious();
			break;
		case R.id.next:
			onClickNext();
			break;
		case R.id.refresh:
			onClickRefresh();
			break;
		case R.id.browserControl:
			onClickBrowserControlLayout();
			break;
		case R.id.browser:
			onClickBrowser();
			break;
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
	public void onRefresh() {
		// TODO Auto-generated method stub
		webView.reload();
	}
	
    private void updateRecord(final Context context, final String action, long id) {
    	HashMap<String, String> user = AuthUtils.getLoginUser(this);
    	
    	executeRequest(new GsonRequest<>(String.format(Api.RECORD, id, 
    			action, user.get("id"), user.get("sessionid")), 
    			Record.class, null, 
    			new Response.Listener<Record>() {

					@Override
					public void onResponse(final Record response) {
						// TODO Auto-generated method stub
						CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Integer>(){

							@Override
							protected Integer doInBackground(Object... arg0) {
								// TODO Auto-generated method stub
								int result = 0;
								long status_code = response.getStatus_code();
								long current_count = response.getCurrent_count();
								
								if(status_code != 0){
									return 1;
								}
								
								String json = product.toJson();
								Product newProduct = new Gson().fromJson(json, Product.class);

								if(action.equals(Action.addview.getString())){
									newProduct.setView_count(current_count);
								}else if(action.equals(Action.addlike.getString())){
									newProduct.setLike_count(current_count);
									result = 2;
								}else{
									newProduct.setLike_count(current_count);
									result = 3;
								}
								
								ContentValues values = productsDataHelper.getContentValues(newProduct);
								productsDataHelper.update(values, ProductsDBInfo.ID + "=?", new String[]{
										String.valueOf(product.getId())
								});
								
								return result;
							}

							@Override
							protected void onPostExecute(Integer result) {
								// TODO Auto-generated method stub
								int ResId = 0;
								switch(result) {
								case 1:
									ResId = R.string.error_data;
									break;
								case 2:
									like.setSelected(!like.isSelected());
									ResId = R.string.add_like_success;
									break;
								case 3:
									like.setSelected(!like.isSelected());
									ResId = R.string.remove_like_success;
									break;
								}
								
								if(ResId != 0){
									Toast.makeText(BrowserActivity.this, ResId, Toast.LENGTH_SHORT).show();
								}
								
								super.onPostExecute(result);
							}
						});
					}
				}, 
				
    			new Response.ErrorListener(){

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Toast.makeText(BrowserActivity.this, CommonUtils.volleyErrorMessage(error), 
								Toast.LENGTH_SHORT).show();
					}
					
				})
    	);
    }
    
    private void isLike() {
    	if(!CommonUtils.isOnline(this)) return;
    	
    	HashMap<String, String> user = AuthUtils.getLoginUser(this);
    	HashMap<String, String> data = new HashMap<String, String>();
    	data.put("uid", user.get("id"));
    	data.put("sessionid", user.get("sessionid"));
    	data.put("product", String.valueOf(product.getId()));
    	data.put("site", AppData.Site);
    	final CommonUtils.AsyncHttpPost asyncHttpPost = new CommonUtils.AsyncHttpPost(this, data);
    	CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Integer>(){

			@Override
			protected Integer doInBackground(Object... arg0) {
				// TODO Auto-generated method stub
				
				try{
					HashMap<String, Object> response = asyncHttpPost.execute(Api.ISLIKE).get();
					if(response == null || (Integer)response.get("statusCode") != HttpURLConnection.HTTP_OK){
						if(response != null){
							Log.d(AppData.TAG, "检查是否收藏息时服务器出错:"+response.toString());
						}
						return -1;
					}
					
					String jsonString = (String) response.get("body");
					JSONObject json = new JSONObject(jsonString);
					int status = json.getInt("status");
					
					return status;
				}catch(Exception e){
					Log.d(AppData.TAG, "获取服务器返回数据出现异常:" + e.getMessage());
				}
				
				return -2;
			}

			@Override
			protected void onPostExecute(Integer result) {
				// TODO Auto-generated method stub
				if(result == 0) like.setSelected(true);
				super.onPostExecute(result);
			}
    	});
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		custom();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onResume(this);
		MobclickAgent.onPause(this);
	}
}
