package com.oneggo.snacks.data;

import com.android.volley.toolbox.ImageLoader;
import com.oneggo.snacks.util.ImageUtils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache{

	public BitmapLruCache(int maxSize) {
		super(maxSize);
		// TODO Auto-generated constructor stub
	}

    @Override
    protected int sizeOf(String key, Bitmap bitmap) {
        return ImageUtils.getBitmapSize(bitmap);
    }
    
	@Override
	public Bitmap getBitmap(String url) {
		// TODO Auto-generated method stub
		return get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		// TODO Auto-generated method stub
		put(url, bitmap);
	}

}
