package com.oneggo.snacks.adapter;

import java.util.ArrayList;

import com.android.volley.toolbox.ImageLoader;
import com.oneggo.snacks.R;
import com.oneggo.snacks.data.RequestManager;
import com.oneggo.snacks.datatype.Product;
import com.oneggo.snacks.ui.BrowserActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SubjectItemAdapter extends BaseAdapter{

	private ArrayList<Product> products;
	
	private Context context;
	
	private LayoutInflater layoutInflater;
	
	private Drawable mDefaultImageDrawable = new ColorDrawable(Color.argb(255, 201, 201, 201));
	
	private static final String EXTRA_PRODUCT = "EXTRA_PRODUCT";
	
	public SubjectItemAdapter(ArrayList<Product> products, Context context) {
		this.products = products;
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return products.size();
	}

	@Override
	public Product getItem(int arg0) {
		// TODO Auto-generated method stub
		return products.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		
		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.listitem_product, null);
			
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.price = (TextView) convertView.findViewById(R.id.price);
			viewHolder.text_like_count = (TextView) convertView.findViewById(R.id.text_like_count);
			viewHolder.text_view_count = (TextView) convertView.findViewById(R.id.text_view_count);	
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final Product product = getItem(position);
		viewHolder.imageRequest = RequestManager.loadImage(product.getPhoto(), RequestManager.
				getImageListener(viewHolder.image, mDefaultImageDrawable, mDefaultImageDrawable));		
		viewHolder.title.setText(product.getTitle());
		viewHolder.price.setText(product.getPrice());
		TextPaint tp = viewHolder.price.getPaint();
		tp.setFakeBoldText(true);
		viewHolder.text_like_count.setText(String.valueOf(product.getLike_count()));
		viewHolder.text_view_count.setText(String.valueOf(product.getView_count()));
		
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putSerializable(EXTRA_PRODUCT, product);
				Intent intent = new Intent(context, BrowserActivity.class);
				intent.putExtras(bundle);
				context.startActivity(intent);					
			}
		});
		
		return convertView;
	}
	
	static class ViewHolder{
		public ImageView image;
		
		public TextView title;
		
		public TextView price;
		
		public TextView text_view_count;
		
		public TextView text_like_count;
		
		public ImageLoader.ImageContainer imageRequest;		
	}
}
