package com.oneggo.snacks.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.oneggo.snacks.R;
import com.oneggo.snacks.data.GsonRequest;
import com.oneggo.snacks.data.RequestManager;
import com.oneggo.snacks.datatype.Action;
import com.oneggo.snacks.datatype.Product;
import com.oneggo.snacks.datatype.Record;
import com.oneggo.snacks.util.AuthUtils;
import com.oneggo.snacks.util.CommonUtils;
import com.oneggo.snacks.vendor.Api;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class LikeAdapter extends BaseAdapter{
	
	private Context context;
	
	private ArrayList<Product> products;
	
	private LayoutInflater layoutInflater;
	
	private SwipeListView listView;
	
	private ScrollView showEmptyMessage;
	
	private Drawable mDefaultImageDrawable = new ColorDrawable(Color.argb(255, 201, 201, 201));
	
	public LikeAdapter(Context context, ArrayList<Product> products, SwipeListView listView, ScrollView showEmptyMessage) {
		super();
		// TODO Auto-generated constructor stub
		this.context = context;
		this.products = products;
		this.listView = listView;
		this.showEmptyMessage = showEmptyMessage;
		layoutInflater = LayoutInflater.from(context);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.listitem_like, null);
					
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.price = (TextView) convertView.findViewById(R.id.price);
			viewHolder.text_like_count = (TextView) convertView.findViewById(R.id.text_like_count);
			viewHolder.text_view_count = (TextView) convertView.findViewById(R.id.text_view_count);	
			viewHolder.cancel = (Button) convertView.findViewById(R.id.cancel);
			viewHolder.delete = (Button) convertView.findViewById(R.id.delete);
			viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
			
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
		
		viewHolder.cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				listView.closeAnimate(position);
			}
		});
		
		viewHolder.delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				HashMap<String, String> user = AuthUtils.getLoginUser(context);
				
				RequestManager.addRequest(new GsonRequest<>(
					String.format(
						Api.RECORD, 
						product.getId(), 
						Action.removelike.getString(), 
						user.get("id"), 
						user.get("sessionid")
					),
					Record.class, null,
					new Response.Listener<Record>() {

						@Override
						public void onResponse(final Record response) {
							// TODO Auto-generated method stub
							long status_code = response.getStatus_code();
							
							if(status_code == 0){
								products.remove(position);
								listView.closeAnimate(position);
								notifyDataSetChanged();
								if(products.size() == 0){
									showEmptyMessage.setVisibility(View.VISIBLE);
								}
								
								Toast.makeText(context, R.string.remove_like_success, 
										Toast.LENGTH_SHORT).show();
							}else{
								String message = response.getMessage();
								Toast.makeText(context, message, 
										Toast.LENGTH_SHORT).show();
							}
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							Toast.makeText(context, CommonUtils.volleyErrorMessage(error), 
									Toast.LENGTH_SHORT).show();
						}
					}), context);
			}
		});
		
		return convertView;
	}
	
	static class ViewHolder {
		public ImageView image;
		
		public Button cancel;
		
		public Button delete;
		
		public TextView title;
		
		public TextView price;
		
		public TextView text_view_count;
		
		public TextView text_like_count;
		
		public ProgressBar progressBar;
		
		public ImageLoader.ImageContainer imageRequest;		
	}

}
