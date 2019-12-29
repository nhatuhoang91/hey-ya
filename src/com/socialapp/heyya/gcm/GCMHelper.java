package com.socialapp.heyya.gcm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.socialapp.heyya.App;
import com.socialapp.heyya.R;
import com.socialapp.heyya.base.BaseActivity;
import com.socialapp.heyya.model.MessageObject;
import com.socialapp.heyya.qb.helper.BaseHelper;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.utils.JSONUtil;
import com.socialapp.heyya.utils.PrefsHelper;
import com.socialapp.heyya.utils.SecureUtil;
import com.socialapp.heyya.utils.Utils;

public class GCMHelper extends BaseHelper{
	
	static final String TAG = "GCM HELPER";
	public static final String EXTRA_MESSAGE = "message";
	private static final String URL = "http://45.55.249.49:8080/serverdemo/";
	//http://10.1.10.115:8080/serverdemo/
	//private static final String URL = "http://10.1.10.115:8080/serverdemo/";
	//private static final String URL = "http://192.168.1.83:8080/serverdemo/";
	//private static final String URL = "http://upheld-producer-724.appspot.com/";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    
    String SENDER_ID = "61259152065";
    //GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    
    String regid;
    Context context;
    public GCMHelper(Context context){
    	super(context);
    	this.context = context;
    }
    public void registerAction()throws Exception{
		if (checkPlayServices()) {
           // gcm = GoogleCloudMessaging.getInstance(context);
          //  if(gcm == null){
         //   	Log.d(TAG, "gcm == null");
          //  }else{
         //   	Log.d(TAG,"gcm ko null");
         //   }
            regid = getRegistrationId(context);
            if (regid.isEmpty()) {
            	Log.d("MAIN_ACTIVITY", "reg id is empty");
            	registerGcm(Boolean.valueOf(false));
            }
		} else {
            Log.i(TAG, "No valid Google Play Services APK found.");
		}
	}
    public void registerGcm(Boolean isUpdateToken) throws Exception{
    	Log.d(TAG, "registerGcm()");
        StringBuilder msg = new StringBuilder();
        try {
        	InstanceID instanceID = InstanceID.getInstance(context);
        	String iid = instanceID.getId();
        	Log.d("GCM_HELPER", "iid : " + iid);
        	regid = instanceID.getToken(SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
        	
        	//if (gcm == null) {
        	//GoogleCloudMessaging	gcm = GoogleCloudMessaging.getInstance(context);
            //}
              //  regid = gcm.register(SENDER_ID);
                //gcm.close();
                msg.append("Device registered, registration ID=" + regid);
                sendRegistrationIdToBackend(isUpdateToken);
                
                Log.d(TAG, msg.toString());
             } catch (Exception ex) {
                  msg.append("Error :" + ex.getMessage());
                  throw ex;
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
             }
    }

	public void sendMessageInBackground(Bundle data)throws Exception {
        	Log.d("GCM_HELPER", "send message");
        	sendMessage(data);
    }
	
	  private void storeRegistrationId(Context context, String regId) {
	        int appVersion = getAppVersion(context);
	        Log.i(TAG, "Saving regId on app version " + appVersion);
	        App.getInstance().getPrefsHelper().savePref(PrefsHelper.PREF_GCM_REG_ID, regId);
	        App.getInstance().getPrefsHelper().savePref(PrefsHelper.PREF_APP_VERSION, appVersion);
	    }
	 private String getRegistrationId(Context context) {
	        String registrationId = App.getInstance().getPrefsHelper().getString(PrefsHelper.PREF_GCM_REG_ID,
	        		"");
	        if (registrationId.isEmpty()) {
	            Log.i(TAG, "Registration not found.");
	            return "";
	        }
	        // Check if app was updated; if so, it must clear the registration ID
	        // since the existing regID is not guaranteed to work with the new
	        // app version.
	        int registeredVersion = App.getInstance().getPrefsHelper().getInteger(PrefsHelper.PREF_APP_VERSION,
	        		Integer.MIN_VALUE);
	        
	        int currentVersion = getAppVersion(context);
	        if (registeredVersion != currentVersion) {
	            Log.i(TAG, "App version changed.");
	            return "";
	        }
	        return registrationId;
	}

	private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (BaseActivity)context,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

	private void sendRegistrationIdToBackend(Boolean isUpdateToken) throws Exception{
		String userId = App.getInstance().getPrefsHelper().getString(PrefsHelper.PREF_USER_ID,null);
		String userIdEncrypted = SecureUtil.encryptUsernameAndPassword(userId);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(URL+"store");
		//request.setHeader("Content-type", "application/x-www-form-urlencoded");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("regId", regid));
		if(userId.isEmpty())
		{
			Log.d(TAG, "login is empty");
			return;
		}
		nameValuePairs.add(new BasicNameValuePair("login", userId));
		Log.d(TAG, "login : "+userId);
		nameValuePairs.add(new BasicNameValuePair("loginEncrypted", userIdEncrypted));
		nameValuePairs.add(new BasicNameValuePair("key", String.valueOf(new Random().nextInt())));
		nameValuePairs.add(new BasicNameValuePair("type_os", String.valueOf(0)));//0 is android
		if(isUpdateToken != null)
			nameValuePairs.add(new BasicNameValuePair("is_update_token", isUpdateToken.toString()));//0 is android
		else{
			Log.e(TAG, "isUpdateToken null");
		}
		try{
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse respone = client.execute(request);
			int result = respone.getStatusLine().getStatusCode();
			String errorString = respone.getStatusLine().getReasonPhrase();
			Log.d(TAG, "status code : "+ result);
			Log.d(TAG, "status : "+ respone.toString());
			switch(result){
				case 200:
				case 201:
				case 202: storeRegistrationId(context, regid);
				Log.d(TAG, "Send OfflineRequestMessage to third-party server");
					Bundle data = createOfflineRequestMessage();
					sendMessage(data);
					break;
				case 506: 
					Exception e506 = new Exception("506");
					throw e506;
				case 507:
					Exception e507 = new Exception("507");
					throw e507;
				case 508:
					Exception e508 = new Exception("508");
					throw e508;
				case 509:
					Exception e509 = new Exception("509");
					throw e509;
				default: 
					Exception errorDef = new Exception("error default : "+errorString);
					throw errorDef;
			}
		}catch(Exception e){
			Log.e(TAG, e.toString());
			throw e;
		}
	}
	
	public void deleteRegistrationId(String login)throws Exception{
		Log.d(TAG, "deleteRegistrationId() . login : "+login);
		String encryptedLogin = SecureUtil.encryptUsernameAndPassword(login);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(URL+"delete");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//if(regid!=null)
		//nameValuePairs.add(new BasicNameValuePair("regId", regid));
		nameValuePairs.add(new BasicNameValuePair("login", login));
		nameValuePairs.add(new BasicNameValuePair("loginEncrypted", encryptedLogin));
		nameValuePairs.add(new BasicNameValuePair("key", String.valueOf(new Random().nextInt())));
		try{
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse respone = client.execute(request);
			String result = String.valueOf(respone.getStatusLine().getStatusCode());
			//int result = respone.getStatusLine().getStatusCode();
			Log.d(TAG, "delete status code : "+ result);
			Log.d(TAG, "delete status : "+ respone.toString());
		}catch(Exception e){
			Log.e(TAG, e.toString());
			throw e;
		}
	}
	
	private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
	
	private void sendMessage(Bundle data){
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		if(gcm != null){
			Log.d(TAG,"sendMessage . gcm != null");
			try{
				String messageId = data.getString(QBServiceConsts.EXTRA_MESSAGE_ID);
				if(messageId != null){
					Log.d(TAG,"messageId !=null : "+messageId);
				}else{
					Log.d(TAG,"messageId == null");
				}
				gcm.send(SENDER_ID+"@gcm.googleapis.com", messageId,86400, data);
			//	gcm.close();
			}catch(IOException e){
				throw new RuntimeException("Send message failed : " + e);
			}
		}else{
			Log.d(TAG,"gcm ==null");
		}
	}
	
	private Bundle createOfflineRequestMessage(){
		String login = App.getInstance().getPrefsHelper().getString(PrefsHelper.PREF_USER_ID, null);
		Bundle data = new Bundle();
		if(login == null){
			Log.d(TAG, "login null");
		}else{
			Log.d(TAG, "login ko null");
		}
		data.putString(QBServiceConsts.EXTRA_USER_ID, login);
		data.putString(QBServiceConsts.EXTRA_GET_OFFLINE_MESSAGE, "true");
		data.putString(QBServiceConsts.EXTRA_MESSAGE_ID, Utils.createUniqueId());
		return data;
	}
}
