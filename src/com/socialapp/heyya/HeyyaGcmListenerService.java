package com.socialapp.heyya;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.socialapp.heyya.db.DatabaseManager;
import com.socialapp.heyya.media.MediaPlayerManager;
import com.socialapp.heyya.model.Notification;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.utils.PrefsHelper;
import com.socialapp.heyya.utils.Utils;

public class HeyyaGcmListenerService extends GcmListenerService{

	 private static final String TAG = "HeyyaGcmListenerService";
	 /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
	 public static final int NOTIFICATION_ID = 2;
	    private NotificationManager mNotificationManager;
	    NotificationCompat.Builder builder;
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
    	Log.d(TAG,"onMessageReceived. data : " + data.toString());
    	String lat = data.getString(QBServiceConsts.EXTRA_LATITUDE);
    	
    	if(lat != null){
    		Log.d(TAG,"handle incoming message");
    		handleIncomingMessage(data);
    	}else{
    		Log.d(TAG, "handle reply message");
    		handleReplyMessage(data);
    	}
    }
    // [END receive_message]

    private void handleIncomingMessage(Bundle data){
    	Time currentTime = new Time();
		currentTime.setToNow();
		String time = Utils.createTime(currentTime);
		String date = Utils.createDate(currentTime);
		
    	String senderId = data.getString(QBServiceConsts.EXTRA_USER_ID);
    	String lat = data.getString(QBServiceConsts.EXTRA_LATITUDE);
    	String lon = data.getString(QBServiceConsts.EXTRA_LONGTITUDE);
    	String message = data.getString(QBServiceConsts.EXTRA_MESSAGE);
    	
    	Notification notification = new Notification();
    	notification.setSenderId(senderId);
    	notification.setIsSender(0);//=1:sender ; =0:reveiver
		notification.setIsRead(0);//=0:not yet ; =1:read
		notification.setStatus(2);
		notification.setLat(Double.valueOf(lat));
		notification.setLon(Double.valueOf(lon));
		notification.setMessage(message);
		notification.setTime(time);
		notification.setDate(date);
		sendNotification(message,senderId);
		try{
		DatabaseManager.saveNotification(this, notification);
		}catch(Exception e){
			Log.e(TAG, "Error : "+e.toString());
		}
		
    }
    private void handleReplyMessage(Bundle data){
    	String messageId = data.getString(QBServiceConsts.EXTRA_MESSAGE_ID);
    	try{
    		DatabaseManager.updateStatusNotification(getApplicationContext(), messageId, 1);
    	}catch(Exception e){
    		Log.e(TAG, e.toString());
    	}
    }
    
    private void sendNotification(String msg, String friendId) {
    	try {
			String fullname = DatabaseManager.getFriendFullNameByFriendId(this, friendId);
		
    	MediaPlayerManager mediaPlayerManager = new MediaPlayerManager(getApplicationContext());
		mediaPlayerManager.playAssetSound("notification_alarm.mp3", false);
		 int id = App.getInstance().getPrefsHelper().getInteger(PrefsHelper.PREF_NOTIFICATION_ID, -1);
		 if(++id > 5000)
			 id=1;
		 
		 Intent i = new Intent(this, SplashActivity.class);
			i.setAction(Intent.ACTION_MAIN);
			i.addCategory(Intent.CATEGORY_LAUNCHER);
			
		 App.getInstance().getPrefsHelper().savePref(PrefsHelper.PREF_NOTIFICATION_ID, id);
		 
		 mNotificationManager = (NotificationManager)
			this.getSystemService(Context.NOTIFICATION_SERVICE);

	     PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
	    		 i, 0);

	     NotificationCompat.Builder mBuilder =
	             new NotificationCompat.Builder(this)
	        .setSmallIcon(R.drawable.notification_icon)
	        .setContentTitle(fullname)
	        .setStyle(new NotificationCompat.BigTextStyle()
	        .bigText(msg))
	        .setContentText(msg)
	        .setAutoCancel(true);

	        mBuilder.setContentIntent(contentIntent);
	        mNotificationManager.notify(id, mBuilder.build());
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
}
