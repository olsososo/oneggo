package com.oneggo.snacks.fragment;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.oneggo.snacks.R;
import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.oneggo.snacks.AppData;
import com.oneggo.snacks.adapter.CardsAnimationAdapter;
import com.oneggo.snacks.adapter.ProductsAdapter;
import com.oneggo.snacks.adapter.ViewPaperAdapter;
import com.oneggo.snacks.dao.ProductsDataHelper;
import com.oneggo.snacks.dao.SubjectsDataHelper;
import com.oneggo.snacks.data.GsonRequest;
import com.oneggo.snacks.data.RequestManager;
import com.oneggo.snacks.datatype.Category;
import com.oneggo.snacks.datatype.Product;
import com.oneggo.snacks.datatype.Subject;
import com.oneggo.snacks.datatype.Product.ProductsRequestData;
import com.oneggo.snacks.ui.BrowserActivity;
import com.oneggo.snacks.ui.MainActivity;
import com.oneggo.snacks.ui.SubjectActivity;
import com.oneggo.snacks.util.CommonUtils;
import com.oneggo.snacks.vendor.Api;
import com.oneggo.snacks.view.LoadingFooter;
import com.oneggo.snacks.view.LoadingFooter.State;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class ProductFragment extends BaseFragment implements LoaderCallbacks<Cursor>,
	OnRefreshListener, OnPageChangeListener, OnClickListener{

	public SwipeRefreshLayout swipeLayout;
	
	/** product **/
	
	private Category mCategory;
	
	private ProductsDataHelper mDataHelper;
	
	private ProductsAdapter mAdapter;
	
	private ListView mListView;
	
	private MainActivity mActivity;
	
	private int mPage = 1;
	
	/** subject **/
	private View head;
	
	private Handler handler;
	
	private int currentIndex;
	
	private ViewPager viewPager;
	
	private LoadingFooter mLoadingFooter;
	
	private ArrayList<ImageView> views;
	
	private ArrayList<ImageView> points;
	
	private ViewPaperAdapter viewPaperAdapter;
	
	private LinearLayout pointLayout;
	
	private SubjectsDataHelper subjectsDataHelper;
	
	private SharedPreferences sharedPreferences;
	
	private Editor editor;
	
	private long serverSubjectLastUpdated;
	
	private long clientSubjectLastUpdated;
	
	private Drawable mDefaultImageDrawable = new ColorDrawable(Color.argb(255, 201, 201, 201));
	
	public static ProductFragment newInstance(Category category) {
		ProductFragment fragment = new ProductFragment();
		Bundle bundle = new Bundle();
		bundle.putString(AppData.EXTRA_CATEGORY, category.getName());
		bundle.putSerializable(AppData.EXTRA_CATEGORY, category);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View contentView = inflater.inflate(R.layout.fragment_product, null);
		swipeLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.swipe_container);
	    swipeLayout.setOnRefreshListener(this);
	    swipeLayout.setColorScheme(R.color.holo_blue_bright, 
	            R.color.holo_green_light, 
	            R.color.holo_orange_light, 
	            R.color.holo_red_light);
	    parseArgument();
	    
	    mActivity = (MainActivity) getActivity();
	    mDataHelper = new ProductsDataHelper(AppData.getContext(), mCategory);
	    mLoadingFooter = new LoadingFooter(getActivity());
	    mListView = (ListView) contentView.findViewById(R.id.listView);
	    head = inflater.inflate(R.layout.view_main_head, null, false);
	    viewPager = (ViewPager) head.findViewById(R.id.paper);
	    pointLayout = (LinearLayout) head.findViewById(R.id.point_layout);
	    subjectsDataHelper = new SubjectsDataHelper();
	    sharedPreferences = AppData.getContext().getSharedPreferences(AppData.TAG, Context.MODE_PRIVATE);
	    editor = sharedPreferences.edit();
	    
	    views = new ArrayList<ImageView>();
	    points = new ArrayList<ImageView>();
	    viewPaperAdapter = new ViewPaperAdapter(views);
	    viewPager.setAdapter(viewPaperAdapter);
	    viewPager.setOnPageChangeListener(this);
	    
	    mListView.addHeaderView(head);
	    mListView.addFooterView(mLoadingFooter.getView());
	    mAdapter = new ProductsAdapter(getActivity(), mListView);
	    AnimationAdapter animationAdapter = new CardsAnimationAdapter(mAdapter);
	    animationAdapter.setListView(mListView);
	    mListView.setAdapter(animationAdapter);
	    
	    serverSubjectLastUpdated = sharedPreferences.getLong("serverSubjectLastUpdated", 0);
	    clientSubjectLastUpdated = sharedPreferences.getLong("clientSubjectLastUpdated", 0);
	    
	    if(clientSubjectLastUpdated <= serverSubjectLastUpdated){
	    	loadSubject();
	    }else{
	    	setSubjectFromDb();
	    }
	    
	    getLoaderManager().initLoader(0, null, this);
	    
	    mListView.setOnScrollListener(new OnScrollListener() {
			boolean flag = false;
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
					if(flag && mLoadingFooter.getState() == State.Idle){
						loadNextPage();
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
				// TODO Auto-generated method stub
				if(firstVisibleItem + visibleItemCount == totalItemCount 
						&& totalItemCount !=0 
						&& totalItemCount != mListView.getHeaderViewsCount() +
							mListView.getFooterViewsCount() && mAdapter.getCount() > 0){
					flag = true;
				}else{
					flag = false;
				}
			}
		});
	    
	    mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Product product = mAdapter.getItem(position - mListView.getHeaderViewsCount());
				Bundle bundle = new Bundle();
				bundle.putSerializable(AppData.EXTRA_PRODUCT, product);
				Intent intent = new Intent(mActivity, BrowserActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	    
	    handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what == 0x123 && views.size() > 0){
					currentIndex++;					
					int position = currentIndex % views.size();
					viewPager.setCurrentItem(position);
				}
			}
	    };
	    
	    new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(0x123);
			}
		}, 2000, 3000);
	    
		return contentView;
	}
	
	public void parseArgument() {
		Bundle bundle = getArguments();
		mCategory = (Category) bundle.getSerializable(AppData.EXTRA_CATEGORY);
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		loadFirstPage();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		return mDataHelper.getCursorLoader();
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		mAdapter.changeCursor(data);
        if (data != null && data.getCount() == 0) {
            loadFirstPage();
        }
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		mAdapter.changeCursor(null);
	}
	
	private void loadNextPage() {
		mLoadingFooter.setState(LoadingFooter.State.Loading);
		loadData(mPage+1); 
	}
	
	private void loadFirstPage() {
		loadData(1);
	}
	
	private void loadData(final int page) {
		final boolean isRefreshFromTop = page == 1;
		if(!swipeLayout.isRefreshing() && isRefreshFromTop){
			swipeLayout.setRefreshing(true);
		}
		
		executeRequest(new GsonRequest<>(String.format(Api.PRODUCT_LIST,
				mCategory.getId(), page), Product.ProductsRequestData.class,
				null,
				new Response.Listener<Product.ProductsRequestData>() {
				
					@Override
					public void onResponse(final ProductsRequestData response) {
						// TODO Auto-generated method stub
						CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Integer>(){

							@Override
							protected Integer doInBackground(Object... arg0) {
								// TODO Auto-generated method stub								
								ArrayList<Product> products = response.getProducts();
								int status = 0;
								
								if(products != null && products.size() == 0){
									status = 1;
								}else{
									mPage = response.getPage();
									if(mPage == 1){
										mDataHelper.deleteAll();
									}
									mDataHelper.bulkInsert(products);
								}
								return status;	
							}

							@Override
							protected void onPostExecute(Integer result) {
								// TODO Auto-generated method stub
								if(result != 0){
									Toast.makeText(AppData.getContext(), R.string.no_more_data, Toast.LENGTH_SHORT).show();
								}
								
								if(isRefreshFromTop){
									swipeLayout.setRefreshing(false);
								}else{
									mLoadingFooter.setState(LoadingFooter.State.Idle, 1000);
								}
								super.onPostExecute(result);
							}
						});
					}
				},
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Toast.makeText(AppData.getContext(), CommonUtils.volleyErrorMessage(error), 
								Toast.LENGTH_LONG).show();
						if(isRefreshFromTop){
							swipeLayout.setRefreshing(false);
						}else{
							mLoadingFooter.setState(LoadingFooter.State.Idle, 1000);
						}
					}
				}
		));
	}
	
	private void loadSubject() {
		executeRequest(new GsonRequest<>(Api.SUBJECT, Subject.RequestData.class, null,
			new Response.Listener<Subject.RequestData>(){

				@Override
				public void onResponse(Subject.RequestData response) {
					// TODO Auto-generated method stub
					ArrayList<Subject> subjects = response.getSubjects();
					setSubject(subjects);
					
					subjectsDataHelper.deleteAll();
					subjectsDataHelper.bulkInsert(subjects);
					
					clientSubjectLastUpdated = System.currentTimeMillis() / 1000;
					editor.putLong("clientSubjectLastUpdated", clientSubjectLastUpdated).commit();
				}
			}, 
			
			new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					Toast.makeText(AppData.getContext(), CommonUtils.volleyErrorMessage(error), Toast.LENGTH_SHORT).show();
				}
			
			}
		));
	}
	
	private void setSubjectFromDb() {
		ArrayList<Subject> subjects = subjectsDataHelper.query();
		setSubject(subjects);
	}
	
	private void setSubject(ArrayList<Subject> subjects) {
		
		LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		
		LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		p2.gravity = Gravity.CENTER_VERTICAL;
		int padding = CommonUtils.convertDipToPx(AppData.getContext(), 5);
		
		for(Subject subject : subjects){
			ImageView imageView = new ImageView(AppData.getContext());
			imageView.setLayoutParams(p1);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setTag(subject.getId());
			imageView.setOnClickListener(this);
			
			RequestManager.loadImage(subject.getPhoto(), RequestManager.getImageListener(
					imageView, mDefaultImageDrawable, mDefaultImageDrawable));
			views.add(imageView);
			
			ImageView point = new ImageView(AppData.getContext());
			point.setClickable(true);
			point.setPadding(padding, padding, padding, padding);
			point.setLayoutParams(p2);
			point.setImageResource(R.drawable.point);
			point.setEnabled(true);
			pointLayout.addView(point);
			points.add(point);
		}
		
		currentIndex = 0;
		points.get(currentIndex).setEnabled(false);
		viewPaperAdapter.notifyDataSetChanged();	
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void onPageScrolled(int position, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub	
		setCurrentPoint(arg0);
	}
	
	private void setCurrentPoint(int position) {
		for(int i=0; i < views.size(); i++){
			if(position == i){
				points.get(i).setEnabled(false);
			}else{
				points.get(i).setEnabled(true);
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0 instanceof ImageView){
			long subjectId = (long) arg0.getTag();
			Bundle bundle = new Bundle();
			bundle.putLong("subjectId", subjectId);
			
			Intent intent = new Intent(mActivity, SubjectActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}
}
