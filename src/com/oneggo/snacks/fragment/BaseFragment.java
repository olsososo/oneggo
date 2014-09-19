package com.oneggo.snacks.fragment;

import com.android.volley.Request;
import com.oneggo.snacks.data.RequestManager;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment{

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		RequestManager.cancelAll(this);
	}
	
    protected void executeRequest(Request request) {
        RequestManager.addRequest(request, this);
    }	
}
