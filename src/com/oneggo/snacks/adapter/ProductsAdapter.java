package com.oneggo.snacks.adapter;

import com.android.volley.toolbox.ImageLoader;
import com.oneggo.snacks.R;
import com.oneggo.snacks.data.RequestManager;
import com.oneggo.snacks.datatype.Product;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ProductsAdapter extends CursorAdapter{
	
	private LayoutInflater mLayoutInflater;
	
	private ListView mListView;
	
	private Drawable mDefaultImageDrawable = new ColorDrawable(Color.argb(255, 201, 201, 201));
	
	public ProductsAdapter(Context context, ListView listView) {
		super(context, null, false);
		mLayoutInflater = ((Activity) context).getLayoutInflater();
		mListView = listView;
	}

	
	@Override
	public Product getItem(int position) {
		// TODO Auto-generated method stub
		mCursor.moveToPosition(position);
		return Product.fromCursor(mCursor);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		return mLayoutInflater.inflate(R.layout.listitem_product, null);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		Holder holder = getHolder(view);
		
		if(holder.imageRequest != null){
			holder.imageRequest.cancelRequest();
		}
		
		view.setEnabled(!mListView.isItemChecked(cursor.getPosition()
                + mListView.getHeaderViewsCount()));
		
		Product product = Product.fromCursor(cursor);
		holder.imageRequest = RequestManager.loadImage(product.getPhoto(), RequestManager.
				getImageListener(holder.image, mDefaultImageDrawable, mDefaultImageDrawable));
		
		holder.title.setText(product.getTitle());
		holder.price.setText(product.getPrice());
		TextPaint tp = holder.price.getPaint();
		tp.setFakeBoldText(true);
		holder.text_view_count.setText(String.valueOf(product.getView_count()));
		holder.text_like_count.setText(String.valueOf(product.getLike_count()));
	}
	
	private Holder getHolder(final View view) {
		Holder holder = (Holder) view.getTag();
		if(holder == null){
			holder = new Holder(view);
			view.setTag(holder);
		}
		return holder;
	}
	
	private class Holder {
		public ImageView image;
		
		public TextView title;
		
		public TextView price;
		
		public TextView text_view_count;
		
		public TextView text_like_count;
		
		public ImageLoader.ImageContainer imageRequest;
		
		public Holder(View view) {
			image = (ImageView) view.findViewById(R.id.image);
			title = (TextView) view.findViewById(R.id.title);
			price = (TextView) view.findViewById(R.id.price);
			text_view_count = (TextView) view.findViewById(R.id.text_view_count);
			text_like_count = (TextView) view.findViewById(R.id.text_like_count);
		}
	}
}
