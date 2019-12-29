package com.socialapp.heyya;

import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.*;
import com.socialapp.heyya.db.DatabaseManager;
import com.socialapp.heyya.gcm.GCMHelper;
import com.socialapp.heyya.media.MediaPlayerManager;
import com.socialapp.heyya.model.Notification;
import com.socialapp.heyya.qb.command.SendMessageCommand;
import com.socialapp.heyya.utils.PrefsHelper;
import com.socialapp.heyya.utils.Utils;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
import android.util.Log;

public class GcmIntentService{// extends IntentService{
/*
	public static final String TAG = "GCM Heyya";
	public GcmIntentService() {
		super("GCMIntentService");
		// TODO Auto-generated constructor stub
	}

	public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
	/*
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.
            	String messageJSONFormat = extras.getString("message");
            	
            	try{
            		JSONObject messageObject = new JSONObject(messageJSONFormat);
            		handleIncomingMessage(messageJSONFormat);
            	}catch(Exception e){
            		Log.i(TAG, "Error : "+e.toString());
            	}
                //sendNotification(extras.getString("message"));
            	 Log.i(TAG, "Received: " + extras.toString());
            	Log.i(TAG, "message json format " +messageJSONFormat);
               
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	
	private void handleIncomingMessage(String messageJSONFormat){
		try{
			MediaPlayerManager mediaPlayerManager = new MediaPlayerManager(getApplicationContext());
			mediaPlayerManager.playAssetSound("notification_alarm.mp3", false);
		/*	Time time = new Time();
			time.setToNow();
			JSONObject messageBody = new JSONObject(messageJSONFormat);
			Notification notification = new Notification();
			notification.setSenderId(messageBody.getString("sender"));
			notification.setIsSender(0);//=1:sender ; =0:reveiver
			notification.setIsRead(0);//=0:not yet ; =1:read
			notification.setStatus(2);
			notification.setLat(messageBody.getDouble("lat"));
			notification.setLon(messageBody.getDouble("lon"));
			notification.setMessage(messageBody.getString("message"));
			notification.setTime(Utils.createTime(time));
			notification.setDate(Utils.createDate(time));
			DatabaseManager.saveNotification(this, notification);
			*/
			//send reply
			/*String userId = App.getInstance().getPrefsHelper().getString(PrefsHelper.PREF_USER_ID, null);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("sender", userId);
			jsonObject.put("reply", "reply");
			jsonObject.put("time", messageBody.getString("time"));
			jsonObject.put("date", messageBody.getString("date"));
			
			GCMHelper gcmHelper = new GCMHelper(this);
			gcmHelper.sendMessageInBackground(notification.getSenderId(), jsonObject.toString(),null,null,
					null, true);
			
			sendNotification(messageBody.getString("message"));
			*/
			/*Log.d("GCMIntentService", "handle incoming message");
			Log.d("GCMIntentService", "Message : "+messageJSONFormat);
		}catch(Exception e){
			Log.e("GCMIntentService", "error: "+e.toString());
		}
	}
	private void handleReplyMessage(String message){
		try{
			Log.d("GCMIntentService", "handle reply message");
			JSONObject messageBody = new JSONObject(message);
			String friendId = messageBody.getString("sender");
			String time = messageBody.getString("time");
			String date = messageBody.getString("date");
			int i =DatabaseManager.updateStatusNotification(this, friendId, time,date, 1);
			if(i==1){
				Log.d("GCMIntentService", "update status success (sending ---> sent)");
			}else{
				Log.d("GCMIntentService", "update status fail");
			}
		}catch(Exception e){
			Log.e("GCMIntentService", "error: "+e.toString());
		}
	}

	 private void sendNotification(String msg) {
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
	        .setSmallIcon(R.drawable.ic_launcher)
	        .setContentTitle("GCM Notification")
	        .setStyle(new NotificationCompat.BigTextStyle()
	        .bigText(msg))
	        .setContentText(msg)
	        .setAutoCancel(true);

	        mBuilder.setContentIntent(contentIntent);
	        mNotificationManager.notify(id, mBuilder.build());
	 }
*/	 
}
