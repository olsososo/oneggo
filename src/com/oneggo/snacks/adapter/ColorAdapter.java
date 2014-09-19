package com.oneggo.snacks.adapter;

import java.util.Random;

import com.etsy.android.grid.util.DynamicHeightTextView;
import com.oneggo.snacks.R;
import com.oneggo.snacks.AppData;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ColorAdapter extends ArrayAdapter<String>{
	
	private Random mRandom;
	
	private Context mContext;
	
	private LayoutInflater layoutInflater;
	
	private String[] colors;
	
	private RelativeLayout menu;
	
	private SharedPreferences.Editor editor;
	
	public ColorAdapter(Context context, int textViewResourceId,
			String[] objects, RelativeLayout layout) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		menu = layout;
		colors = objects;
		mContext = context;
		mRandom = new Random();
		editor = context.getSharedPreferences(AppData.TAG, Context.MODE_PRIVATE).edit();
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		
		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.list_item_color, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.txtLine = (DynamicHeightTextView) 
					convertView.findViewById(R.id.txt_line);
			viewHolder.colorMenu = (LinearLayout) convertView.findViewById(R.id.color_menu);
			viewHolder.preview = (Button) convertView.findViewById(R.id.preview);
			viewHolder.apply = (Button) convertView.findViewById(R.id.apply);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final int indentify = mContext.getResources().getIdentifier(colors[position], 
				"color", mContext.getPackageName());
		
		viewHolder.txtLine.setText(colors[position]);
		viewHolder.txtLine.setHeightRatio(getRandomHeightRatio());
		viewHolder.txtLine.setBackgroundResource(indentify);
		
		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.color_menu_in);
		viewHolder.colorMenu.setAnimation(animation);
		
		viewHolder.preview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				menu.setBackgroundColor(mContext.getResources().getColor(indentify));
			}
		});
		
		viewHolder.apply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				editor.putString("color", colors[position]).commit();
				Toast.makeText(mContext, R.string.setting_success, Toast.LENGTH_SHORT).show();
			}
		});
		return convertView;
	}
	
    static class ViewHolder {
        DynamicHeightTextView txtLine;
        LinearLayout colorMenu;
        Button preview;
        Button apply;
    }
    
    private double getRandomHeightRatio() {
        return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5 the width
    }
}
