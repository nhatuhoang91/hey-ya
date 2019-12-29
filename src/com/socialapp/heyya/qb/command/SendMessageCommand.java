package com.socialapp.heyya.qb.command;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.format.Time;

import com.socialapp.heyya.App;
import com.socialapp.heyya.core.command.ServiceCommand;
import com.socialapp.heyya.gcm.GCMHelper;
import com.socialapp.heyya.gcm.GPSHelper;
import com.socialapp.heyya.service.QBService;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.utils.PrefsHelper;
import com.socialapp.heyya.utils.Utils;

public class SendMessageCommand extends ServiceCommand{

	private GCMHelper gcmHelper;
	private static GPSHelper gpsHelper;
	public SendMessageCommand(Context context,GCMHelper gcmHelper, String successAction,
			String failAction) {
		super(context, successAction, failAction);
		// TODO Auto-generated constructor stub
		this.gcmHelper = gcmHelper;
	}
	
	public static void start(Context context, String friendId, String message,
			GPSHelper gps_Helper) {
		gpsHelper = gps_Helper;
        Intent intent = new Intent(QBServiceConsts.SEND_MESSAGE_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_FRIEND_ID, friendId);
        intent.putExtra(QBServiceConsts.EXTRA_MESSAGE, message);
        context.startService(intent);
    }

	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		// TODO Auto-generated method stub
		Location location = gpsHelper.getCurrentLocation();
		String messageId = Utils.createUniqueId();
		
		extras.putString(QBServiceConsts.EXTRA_LATITUDE, String.valueOf(location.getLatitude()));
		extras.putString(QBServiceConsts.EXTRA_LONGTITUDE, String.valueOf(location.getLongitude()));
		extras.putString(QBServiceConsts.EXTRA_MESSAGE_ID, messageId);
		String login = App.getInstance().getPrefsHelper().getString(PrefsHelper.PREF_USER_ID, null);
		extras.putString(QBServiceConsts.EXTRA_USER_ID,login);
		
		this.gcmHelper.sendMessageInBackground(extras);
		return extras;
	}

}
