package com.oneggo.snacks.adapter;

import com.android.volley.toolbox.ImageLoader;
import com.oneggo.snacks.R;
import com.oneggo.snacks.data.RequestManager;
import com.oneggo.snacks.datatype.Category;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CategoryAdapter extends CursorAdapter{
	private LayoutInflater mLayoutInflater;
	private ListView mListView;
	private Drawable mDefaultImageDrawable = new ColorDrawable(Color.argb(255, 201, 201, 201));
	
	public CategoryAdapter(Context context, ListView listView) {
		super(context, null, false);
		mLayoutInflater = ((Activity) context).getLayoutInflater();
		mListView = listView;
	}


	@Override
	public Category getItem(int position) {
		mCursor.moveToPosition(position);
		return Category.fromCursor(mCursor);
	}


	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		return mLayoutInflater.inflate(R.layout.listitem_category, null);
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		Holder holder = getHolder(view);
		
		if(holder.imageRequest != null){
			holder.imageRequest.cancelRequest();
		}
		
		Category category = Category.fromCursor(cursor);
		
		holder.imageRequest = RequestManager.loadImage(category.getPhoto(), RequestManager
				.getImageListener(holder.image, mDefaultImageDrawable, mDefaultImageDrawable));
		
		holder.title.setText(category.getName());
		holder.title.setSelected(mListView.isItemChecked(cursor.getPosition()));
	}
	
	public Holder getHolder(final View view) {
		Holder holder = (Holder) view.getTag();
		if(holder == null){
			holder = new Holder(view);
			view.setTag(holder);
		}
		return holder;
	}
	
	private class Holder {
		public TextView title;
		public ImageView image;
		
		public ImageLoader.ImageContainer imageRequest;
		
		public Holder(View view) {
			title = (TextView) view.findViewById(R.id.title);
			image = (ImageView) view.findViewById(R.id.image);
		}
	}
}
