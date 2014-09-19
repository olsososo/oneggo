package com.oneggo.snacks.ui;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.oneggo.snacks.R;
import com.oneggo.snacks.AppData;
import com.oneggo.snacks.adapter.MsgAdapter;
import com.oneggo.snacks.data.GsonRequest;
import com.oneggo.snacks.data.RequestManager;
import com.oneggo.snacks.datatype.Msg;
import com.oneggo.snacks.datatype.Msg.RequestData;
import com.oneggo.snacks.util.CommonUtils;
import com.oneggo.snacks.vendor.Api;
import com.umeng.analytics.MobclickAgent;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FeedbackActivity extends SwipeBackActivity implements OnClickListener, 
	OnRefreshListener{
	
	private EditText sendMessage;
	private Button send;
	private Button back;
	private ListView listview;
	private ArrayList<Msg> msgs;
	private MsgAdapter adapter;
	private LinearLayout loading;
	private SwipeRefreshLayout swipeLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setupViews();
		
		loadData();
	}
	
	private void setupViews() {
		setContentView(R.layout.activity_feedback);
		
		send = (Button) findViewById(R.id.send);
		back = (Button) findViewById(R.id.back);
		listview = (ListView) findViewById(R.id.listView);
		sendMessage = (EditText) findViewById(R.id.send_message);
		loading = (LinearLayout) findViewById(R.id.loading);
		
		send.setOnClickListener(this);
		back.setOnClickListener(this);
		
		initSwipeLayout();
		
		custom();
	}

	private void custom() {
		SharedPreferences sharedPreferences = getSharedPreferences(AppData.TAG, MODE_PRIVATE);
		SwipeBackLayout swipeBackLayout= getSwipeBackLayout();
		RelativeLayout menu = (RelativeLayout) findViewById(R.id.menu);
		int edgeModel = sharedPreferences.getInt("edgemodel", 1);
		int edgeFlag;
		
		switch(edgeModel){
		case 1:
			edgeFlag = SwipeBackLayout.EDGE_LEFT;
			break;
		case 2:
			edgeFlag = SwipeBackLayout.EDGE_RIGHT;
			break;
		case 3:
			edgeFlag = SwipeBackLayout.EDGE_BOTTOM;
			break;
		default:
			edgeFlag = SwipeBackLayout.EDGE_ALL;
			break;
		}	
		
		swipeBackLayout.setEdgeTrackingEnabled(edgeFlag);
		
		String myColor = sharedPreferences.getString("color", null);
		
		if(myColor != null){
			int indentify = getResources().getIdentifier(myColor, 
					"color", getPackageName());
					
			menu.setBackgroundColor(getResources().getColor(indentify));
		}
	}
	
	private void initSwipeLayout() {
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
	    swipeLayout.setOnRefreshListener(this);
	    swipeLayout.setColorScheme(R.color.holo_blue_bright, 
	            R.color.holo_green_light, 
	            R.color.holo_orange_light, 
	            R.color.holo_red_light);		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.back:
			finish();
			break;
		case R.id.send:
			send();
			break;
		}
	}
	
	private void loadData() {
		if(!CommonUtils.isOnline(this)){
			Toast.makeText(this, R.string.check_your_network_environment, Toast.LENGTH_SHORT).show();
			loading.setVisibility(View.GONE);
			return;
		}
		
		executeRequest(new GsonRequest<>(
				String.format(Api.MSG, JPushInterface.getRegistrationID(this)), 
				Msg.RequestData.class, null, 
				new Response.Listener<Msg.RequestData>() {

					@Override
					public void onResponse(RequestData response) {
						// TODO Auto-generated method stub
						loading.setVisibility(View.GONE);
						
						msgs = response.getMsgs();
						adapter = new MsgAdapter(msgs, FeedbackActivity.this);
						listview.setAdapter(adapter);
						swipeLayout.setRefreshing(false);
					}
				}, 
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						loading.setVisibility(View.GONE);
						swipeLayout.setRefreshing(false);
						Toast.makeText(FeedbackActivity.this, CommonUtils.volleyErrorMessage(error), 
								Toast.LENGTH_SHORT).show();
					}
				})
		);
	}
	
	private void send() {
		final String message = sendMessage.getText().toString().trim();
		if(TextUtils.isEmpty(message)){
			Toast.makeText(this, R.string.enter_feekback_msg, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(!CommonUtils.isOnline(this)){
			Toast.makeText(this, R.string.check_your_network_environment, Toast.LENGTH_SHORT).show();
			return;
		}
		
		sendMessage.setText(R.string.sending);
		send.setEnabled(false);
		
		SharedPreferences sharedPreferences = getSharedPreferences(AppData.TAG, MODE_PRIVATE);
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("id", sharedPreferences.getString("id", ""));
		data.put("registration", JPushInterface.getRegistrationID(this));
		data.put("msg", message);
		data.put("site", AppData.Site);
		
		final CommonUtils.AsyncHttpPost asyncHttpPost = new CommonUtils.AsyncHttpPost(this, data);
		CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Integer>(){

			@Override
			protected Integer doInBackground(Object... arg0) {
				// TODO Auto-generated method stub
				try {
					HashMap<String, Object> response = asyncHttpPost.execute(Api.SEND).get();
					if(response == null || (Integer)response.get("statusCode") != HttpURLConnection.HTTP_OK){
						if(response != null){
							Log.d(AppData.TAG, "发送反馈信息时服务器出错:"+response.toString());
						}
						return -1;
					}
					
					String jsonString = (String) response.get("body");
					JSONObject json = new JSONObject(jsonString);
					int status = json.getInt("status");
					
					return status;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d(AppData.TAG, "获取服务器返回数据出现异常:" + e.getMessage());
				} 	
				return -2;
			}

			@Override
			protected void onPostExecute(Integer result) {
				// TODO Auto-generated method stub
				switch(result) {
				case -1:
					Toast.makeText(FeedbackActivity.this, R.string.server_exception_occurs, 
							Toast.LENGTH_SHORT).show();
					break;
				case -2:
					Toast.makeText(FeedbackActivity.this, R.string.get_server_data_error, 
							Toast.LENGTH_SHORT).show();
					break;
				case 0:
					Msg msg = new Msg(message, getDateTime(), 0);
					msgs.add(msg);
					adapter.notifyDataSetChanged();
					sendMessage.setText("");
					listview.setSelection(adapter.getCount() - 1);
					break;
				}
				
				sendMessage.setText("");
				send.setEnabled(true);
				super.onPostExecute(result);
			}
		});	
	}
	
    private String getDateTime() {
        Calendar c = Calendar.getInstance();

        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH) + 1);
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mins = String.valueOf(c.get(Calendar.MINUTE));
        
       
        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins); 
        						
        return sbBuffer.toString();
    }
    
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		RequestManager.cancelAll(this);
	}
	
    protected void executeRequest(Request request) {
        RequestManager.addRequest(request, this);
    }

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		loadData();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		custom();
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	

}
