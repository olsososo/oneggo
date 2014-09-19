package com.oneggo.snacks.adapter;

import java.util.ArrayList;

import com.oneggo.snacks.R;
import com.oneggo.snacks.AppData;
import com.oneggo.snacks.data.RequestManager;
import com.oneggo.snacks.datatype.Msg;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MsgAdapter extends BaseAdapter{
	
	private ArrayList<Msg> msgs;
	
	private Context context;
	
	private LayoutInflater inflater;
	
	private TextView sendTime;
	
	private TextView msgContent;
	
	private ImageView photo;
	
	private Drawable mDefaultImageDrawable = new ColorDrawable(Color.argb(255, 242, 242, 242));
	
	public MsgAdapter(ArrayList<Msg> msgs, Context context) {
		this.msgs = msgs;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return msgs.size();
	}

	@Override
	public Msg getItem(int arg0) {
		// TODO Auto-generated method stub
		return msgs.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Msg msg = getItem(position);
		boolean isComMsg = (msg.getMsgtype() == 0);
			
		if(isComMsg){
			convertView = inflater.inflate(R.layout.listitem_msg_left, null);
		}else{
			convertView = inflater.inflate(R.layout.listitem_msg_right, null);
		}
		
		sendTime = (TextView) convertView.findViewById(R.id.send_time);
		msgContent = (TextView) convertView.findViewById(R.id.msg_content);
		photo = (ImageView) convertView.findViewById(R.id.photo);
		
		SharedPreferences sharedPreferences = context.getSharedPreferences(AppData.TAG, 
				Context.MODE_PRIVATE);
		
		String photoString = sharedPreferences.getString("photo", null);
		
		if(isComMsg && photoString != null){
			RequestManager.loadImage(photoString, RequestManager.getImageListener(
					photo, mDefaultImageDrawable, mDefaultImageDrawable));			
		}else{
			photo.setBackgroundResource(R.drawable.mini_avatar_shadow);
		}
		
		sendTime.setText(msg.getTime());
		msgContent.setText(msg.getMsg());
		return convertView;
	}
}
