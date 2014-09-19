package com.oneggo.snacks.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.oneggo.snacks.R;
import com.oneggo.snacks.datatype.Category;

public class SonCategoryAdapter extends BaseAdapter implements SpinnerAdapter{
	
	private Context context;
	
	private ArrayList<Category> categories;
	
	public SonCategoryAdapter(ArrayList<Category> categories, Context context) {
		super();
		// TODO Auto-generated constructor stub
		this.categories = categories;
		this.context = context;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		TextView tv = new TextView(context);
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 
				AbsListView.LayoutParams.WRAP_CONTENT);
		
		tv.setGravity(Gravity.CENTER);
		tv.setLayoutParams(lp);
		tv.setTextSize(18);
		tv.setTextColor(context.getResources().getColor(R.color.white));
		Category category = getItem(arg0);
		tv.setText(category.getName());
		tv.setTag(category);
		return tv;
	}
	
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	
	@Override
	public Category getItem(int arg0) {
		// TODO Auto-generated method stub
		return categories.get(arg0);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return categories.size();
	}

	@Override
	public View getDropDownView(int position, View convertView,
			ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, null);
		}
		
		TextView title = (TextView) convertView.findViewById(R.id.title);
		Category category = getItem(position);
		title.setText(category.getName());
		return convertView;
	}
};