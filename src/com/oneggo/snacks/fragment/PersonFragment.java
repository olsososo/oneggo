package com.oneggo.snacks.fragment;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;

import org.json.JSONObject;

import cn.waps.AppConnect;

import com.oneggo.snacks.R;
import com.oneggo.snacks.AppData;
import com.oneggo.snacks.data.RequestManager;
import com.oneggo.snacks.extend.AppWall;
import com.oneggo.snacks.ui.FeedbackActivity;
import com.oneggo.snacks.ui.LikeActivity;
import com.oneggo.snacks.ui.LoginActivity;
import com.oneggo.snacks.ui.MoreActivity;
import com.oneggo.snacks.ui.PushSettingActivity;
import com.oneggo.snacks.ui.SettingActivity;
import com.oneggo.snacks.util.AuthUtils;
import com.oneggo.snacks.util.CommonUtils;
import com.oneggo.snacks.vendor.Api;
import com.oneggo.snacks.view.CustomImageView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class PersonFragment extends Fragment implements OnClickListener{
	
	private Context context;
	
	private ImageView more;
	
	private TextView gender;
	
	private TextView username;
	
	private CustomImageView person;
	
	private RelativeLayout userLayout;
	
	private RelativeLayout loginLayout;
	
	private LinearLayout favorites;
	
	private LinearLayout logout;
	
	private LinearLayout pushSetting;
	
	private LinearLayout iWantTucao;
	
	private LinearLayout hotApp;
	
	private LinearLayout shareApp;
	
	private Button takePhoto;
	
	private Button pickPhoto;
	
	private Button camearCancel;
	
	private LinearLayout dialogUpdatePhoto;
	
	private Dialog photoDialog;
	
	private Dialog shareDialog;
	
	private Dialog tipsDialog = null;
	
	private Uri photoUri;
	
	private boolean isLogin;
	
	private HashMap<String, String> user;
	
    private BitmapDrawable mDefaultImageDrawable = (BitmapDrawable) AppData.getContext()
            .getResources().getDrawable(R.drawable.defaultperson);
    
    private SharedPreferences sharedPreferences;
    
    private Editor editor;
    
	private static final int SELECT_PIC_BY_TACK_PHOTO = 1;

	private static final int SELECT_PIC_BY_PICK_PHOTO = 2;
	
	private static final int PHOTO_RESULT = 4;
	
	private static final String IMAGE_UNSPECIFIED = "image/*";
	
	public PersonFragment(Context context){
		this.context = context;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		AppConnect.getInstance(context).initAdInfo();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_person, null);
		more = (ImageView) view.findViewById(R.id.more);
		gender = (TextView) view.findViewById(R.id.gender);
		username = (TextView) view.findViewById(R.id.username);
		person = (CustomImageView) view.findViewById(R.id.person);
		favorites = (LinearLayout) view.findViewById(R.id.favorites);
		logout = (LinearLayout) view.findViewById(R.id.logout);
		pushSetting = (LinearLayout) view.findViewById(R.id.push_setting);
		iWantTucao = (LinearLayout) view.findViewById(R.id.i_want_tucao);
		hotApp = (LinearLayout) view.findViewById(R.id.hot_app);
		shareApp = (LinearLayout) view.findViewById(R.id.share_app);
		userLayout = (RelativeLayout) view.findViewById(R.id.user_layout);
		loginLayout = (RelativeLayout) view.findViewById(R.id.login_layout);
		
		dialogUpdatePhoto = (LinearLayout) inflater.inflate(R.layout.dialog_update_photo, null);
		takePhoto = (Button) dialogUpdatePhoto.findViewById(R.id.take_photo);
		pickPhoto = (Button) dialogUpdatePhoto.findViewById(R.id.pick_photo);
		camearCancel = (Button) dialogUpdatePhoto.findViewById(R.id.cancel);
		
		initPhotoDialog();
		initShareDialog();
		
		isLogin = AuthUtils.isLogin(context);
		user = AuthUtils.getLoginUser(context);
		sharedPreferences = context.getSharedPreferences(AppData.TAG, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		
		if(isLogin){
			logout.setVisibility(View.VISIBLE);
			loginLayout.setVisibility(View.GONE);
			userLayout.setVisibility(View.VISIBLE);
			
			String genderString = user.get("gender");
			
			RequestManager.loadImage(user.get("photo"), RequestManager.getImageListener(
					person, mDefaultImageDrawable, mDefaultImageDrawable));
			username.setText(user.get("nickname"));
			
			if(genderString.equals("1")){
				gender.setText(R.string.boy);
				gender.setTextColor(getResources().getColor(R.color.v1_boy));
			}else if(genderString.equals("2")){
				gender.setText(R.string.girl);
				gender.setTextColor(getResources().getColor(R.color.v1_girl));
			}
		}else{
			logout.setVisibility(View.GONE);
			loginLayout.setVisibility(View.VISIBLE);
			userLayout.setVisibility(View.GONE);
		}
		
		username.setOnClickListener(this);
		person.setOnClickListener(this);
		favorites.setOnClickListener(this);
		pushSetting.setOnClickListener(this);
		iWantTucao.setOnClickListener(this);
		logout.setOnClickListener(this);
		hotApp.setOnClickListener(this);
		shareApp.setOnClickListener(this);
		camearCancel.setOnClickListener(this);
		takePhoto.setOnClickListener(this);
		pickPhoto.setOnClickListener(this);
		more.setOnClickListener(this);
		loginLayout.setOnClickListener(this);
		
		camearCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				photoDialog.cancel();
			}
		});
		
		takePhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				photoDialog.cancel();
				takePhoto();
			}
		});
		
		pickPhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				photoDialog.cancel();
				pickPhoto();
			}
		});
		
		return view;
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.username:
			startActivity(new Intent(context, SettingActivity.class));
			break;
		case R.id.person:
			photoDialog.show();
			break;
		case R.id.favorites:
			if(isLogin){
				startActivity(new Intent(context, LikeActivity.class));
			}else{
				showLogin();
			}
			break;
		case R.id.push_setting:
			startActivity(new Intent(context, PushSettingActivity.class));
			break;
		case R.id.i_want_tucao:
			startActivity(new Intent(context, FeedbackActivity.class));		
			break;
		case R.id.logout:
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(getString(R.string.exit_current_accout))
				.setNegativeButton(getString(R.string.cancel), dialogClickListener)
				.setPositiveButton(getString(R.string.yes), dialogClickListener)
			    .show();
			break;
		case R.id.hot_app:
			context.startActivity(new Intent(context, AppWall.class));
			break;
		case R.id.share_app:
			shareDialog.show();
			break;
		case R.id.more:
			startActivity(new Intent(context, MoreActivity.class));
			break;
		case R.id.login_layout:
			startActivity(new Intent(context, LoginActivity.class));
			break;
		}
	}
	
	private void showLogin(){
		Toast.makeText(context, getString(R.string.please_login), 
				Toast.LENGTH_SHORT).show();
		startActivity(new Intent(context, LoginActivity.class));		
	}
	
	private void takePhoto() {
		String SDState = Environment.getExternalStorageState();
		if(SDState.equals(Environment.MEDIA_MOUNTED))
		{
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			ContentValues values = new ContentValues();  
			photoUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
			startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
		}else{
			Toast.makeText(context, R.string.memory_card_does_not_exists, Toast.LENGTH_LONG).show();
		}
	}
	
	private void pickPhoto() {		
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == SELECT_PIC_BY_PICK_PHOTO || requestCode == SELECT_PIC_BY_TACK_PHOTO){
			if(requestCode == SELECT_PIC_BY_PICK_PHOTO ){
				if(data == null){
					Toast.makeText(context, "选择图片文件出错", Toast.LENGTH_LONG).show();
					return;
				}
				
				photoUri = data.getData();
				if(photoUri == null ){
					Toast.makeText(context, "选择图片文件出错", Toast.LENGTH_LONG).show();
					return;
				}
			}
			
			startPhotoZoom(photoUri);
		}
		
		if(requestCode == PHOTO_RESULT){
            Bundle extras = data.getExtras();  
            if (extras != null) {  
                Bitmap photo = extras.getParcelable("data");  
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
                
                byte[] imageBytes = baos.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                
                uploadPic(encodedImage);
            }
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void uploadPic(String encodedImage) {
		if(!CommonUtils.isOnline(context)){
			Toast.makeText(context, R.string.check_your_network_environment, 
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(tipsDialog == null){
			tipsDialog = CommonUtils.creatRequestDialog(context, 
					context.getString(R.string.uploading_photo));
		}
		
		tipsDialog.show();
		
		HashMap<String, String> data = new HashMap<String, String>();
		
		data.put("uid", user.get("id"));
		data.put("image", encodedImage);
		data.put("sessionid", user.get("sessionid"));
		data.put("site", AppData.Site);
		
		final CommonUtils.AsyncHttpPost asyncHttpPost = new CommonUtils.AsyncHttpPost(context, data, true);
		CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Integer>(){

			@Override
			protected Integer doInBackground(Object... arg0) {
				try {
					HashMap<String, Object> response = asyncHttpPost.execute(Api.PHOTO).get();
					if(response == null || (Integer)response.get("statusCode") != HttpURLConnection.HTTP_OK){
						if(response != null){
							Log.d(AppData.TAG, "上传图片时服务器出错:"+response.toString());
						}
						
						return -1;
					}
					
					String jsonString = (String) response.get("body");
					JSONObject json = new JSONObject(jsonString);
					int status = json.getInt("status");
					
					if(status == 0){
						String photo = json.getString("photo");
						editor.putString("photo", photo).commit();
					}
					
					return status;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d(AppData.TAG, "获取post数据异常:" + e.getMessage());
				}
				
				return -2;
			}

			@Override
			protected void onPostExecute(Integer result) {
				// TODO Auto-generated method stub
				tipsDialog.cancel();
				
				switch(result){
				case -1:
					Toast.makeText(context, R.string.server_exception_occurs, 
							Toast.LENGTH_SHORT).show();
					break;
				case -2:
					Toast.makeText(context, R.string.get_server_data_error, 
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(context, R.string.user_login_auth_error, 
							Toast.LENGTH_SHORT).show();
					break;
				case 2:
					Toast.makeText(context, R.string.upload_photo_error, 
							Toast.LENGTH_SHORT).show();
					break;
				case 3:
					Toast.makeText(context, "服务器出问题啦~", Toast.LENGTH_SHORT).show();
					break;
				case 0:
					String photo = sharedPreferences.getString("photo", null);
					RequestManager.loadImage(photo, RequestManager.getImageListener(
							person, mDefaultImageDrawable, mDefaultImageDrawable));
					Toast.makeText(context, R.string.update_photo_success, 
							Toast.LENGTH_SHORT).show();
					break;
				}
				
				super.onPostExecute(result);
			}
		});
	}

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");//调用Android系统自带的一个图片剪裁页面,
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");//进行修剪
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX",40);
        intent.putExtra("outputY",40);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_RESULT);
    }

	private void initPhotoDialog() {
		photoDialog = new Dialog(context, R.style.dialog);
		photoDialog.setContentView(dialogUpdatePhoto);
		Window window = photoDialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();	
		int width = CommonUtils.getScreenWidth(context);	
		lp.width = (int)(0.6 * width);	
	}
	
	private void initShareDialog() {
		shareDialog = new Dialog(context, R.style.dialog);
		shareDialog.setContentView(R.layout.dialog_share_app);
		Window window = shareDialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();	
		int width = CommonUtils.getScreenWidth(context);	
		lp.width = (int)(0.6 * width);	
	}
	
	private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	        switch (which){
	        case DialogInterface.BUTTON_POSITIVE:
	        	AuthUtils.logout(context);
	            break;

	        case DialogInterface.BUTTON_NEGATIVE:
	            break;
	        }
	    }
	};
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		AppConnect.getInstance(context).close();
		super.onDestroy();
	}
}
