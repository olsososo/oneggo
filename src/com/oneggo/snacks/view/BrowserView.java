package com.oneggo.snacks.view;

import com.oneggo.snacks.ui.MainActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebView;

public class BrowserView extends WebView implements OnGestureListener{
	
	public BrowserView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		detector = new GestureDetector(this.getContext(), this);
	}

	public GestureDetector detector;
	
	private final int FLIP_DISTANCE = 100;

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		
//		// TODO Auto-generated method stub
//		if(arg0.getX() - arg1.getX() > FLIP_DISTANCE){
//			super.goForward();
//		}
//		
//		if(arg1.getX() - arg0.getX() > FLIP_DISTANCE){
//			super.goBack();
//		}
		
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return detector.onTouchEvent(event) || super.onTouchEvent(event);
	}
}
