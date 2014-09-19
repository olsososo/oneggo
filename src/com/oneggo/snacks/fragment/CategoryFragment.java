package com.oneggo.snacks.fragment;

import java.util.ArrayList;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.oneggo.snacks.R;
import com.oneggo.snacks.AppData;
import com.oneggo.snacks.adapter.CategoryAdapter;
import com.oneggo.snacks.dao.CategoriesDataHelper;
import com.oneggo.snacks.data.GsonRequest;
import com.oneggo.snacks.datatype.Category;
import com.oneggo.snacks.ui.MainActivity;
import com.oneggo.snacks.util.CommonUtils;
import com.oneggo.snacks.vendor.Api;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class CategoryFragment extends BaseFragment implements LoaderCallbacks<Cursor>{
	private ListView mListView;
	
	private CategoriesDataHelper mDataHelper;
	
	private CategoryAdapter mAdapter;
	
	private MainActivity mActivity;
	
	private SharedPreferences sharedPreferences;
	
	private Editor editor;
	
	private long serverCategoryLastUpdated;
	
	private long clientCategoryLastUpdated;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mActivity = (MainActivity) getActivity();
		View contentView = inflater.inflate(R.layout.fragment_category, null);
		mListView = (ListView) contentView.findViewById(R.id.listView);
	
		sharedPreferences = AppData.getContext().getSharedPreferences(AppData.TAG, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		serverCategoryLastUpdated = sharedPreferences.getLong("serverCategoryLastUpdated", 0);
		clientCategoryLastUpdated = sharedPreferences.getLong("clientCategoryLastUpdated", 0);
		
		mDataHelper = new CategoriesDataHelper(AppData.getContext());
		mAdapter = new CategoryAdapter(getActivity(), mListView);
		mListView.setAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				mListView.setItemChecked(position, true);
				Category category = mAdapter.getItem(position);
				mActivity.setCategory(category);
			}
		});
		
		return contentView;
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
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		return mDataHelper.getCursorLoader();
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		mAdapter.changeCursor(data);

		if((data != null && data.getCount() == 0) || (clientCategoryLastUpdated <= serverCategoryLastUpdated)){
			loadData();
		}
		
		if(data != null && data.getCount() != 0){
			new Handler().post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mListView.setItemChecked(0, true);
					Category category = mAdapter.getItem(0);
					mActivity.setCategory(category);
				}
			});			
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		mAdapter.changeCursor(null);
	}

	public void loadData() {
		Log.d(AppData.TAG, "loadData");
		executeRequest(new GsonRequest<>(Api.CATEGORY_LIST, Category.RequestData.class, null, 
				new Response.Listener<Category.RequestData>() {

					@Override
					public void onResponse(final Category.RequestData response) {
						// TODO Auto-generated method stub
						CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Object>(){

							@Override
							protected Object doInBackground(Object... params) {
								// TODO Auto-generated method stub
								ArrayList<Category> mCategories = response.getCategories();
								mDataHelper.deleteAll();
								mDataHelper.bulkInsert(mCategories);
								
								clientCategoryLastUpdated = System.currentTimeMillis()/1000;
								editor.putLong("clientCategoryLastUpdated", clientCategoryLastUpdated).commit();
								
								return null;
							}

							@Override
							protected void onPostExecute(Object result) {
								// TODO Auto-generated method stub
								super.onPostExecute(result);
							}
							
						});
					}
				}, 
				
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), CommonUtils.volleyErrorMessage(error), 
								Toast.LENGTH_SHORT).show();
					}
				}
		));
	}
}
