package com.socialapp.heyya;

import com.socialapp.heyya.media.MediaPlayerManager;
import com.socialapp.heyya.service.QBServiceConsts;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class FriendRequestReceiver extends BroadcastReceiver{
 
	final static int NOTIFICATION_ID = 0;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		MediaPlayerManager mediaPlayerManager = new MediaPlayerManager(context);
		mediaPlayerManager.playAssetSound("friend_alarm.mp3", false);
		String friendName = intent.getStringExtra(QBServiceConsts.EXTRA_FRIEND_NAME);
		//int numberFriendRequest = intent.getIntExtra(QBServiceConsts.EXTRA_IS_MANY_FRIEND_REQUEST, 0);
		Intent i = new Intent(context, SplashActivity.class);
		i.setAction(Intent.ACTION_MAIN);
		i.addCategory(Intent.CATEGORY_LAUNCHER);
		String msg = null;
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.notification_icon)
		.setContentTitle("Friend request")
		.setContentIntent(pendingIntent)
		.setAutoCancel(true);
		if(friendName!=null){
			msg = friendName + " sent to you a friend request";
			mBuilder.setContentText(msg);
		}else{
			msg = "You have friend request, click to view";
			mBuilder.setContentText(msg);
		}
		mBuilder.setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg));
		NotificationManager notification = (NotificationManager)
				context.getSystemService(Context.NOTIFICATION_SERVICE);
		notification.notify(NOTIFICATION_ID, mBuilder.build());
	}
}
