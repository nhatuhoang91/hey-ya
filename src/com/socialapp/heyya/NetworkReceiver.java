package com.socialapp.heyya;

import com.socialapp.heyya.qb.command.QBLogoutChatServiceCommand;
import com.socialapp.heyya.utils.PrefsHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver{

	private PrefsHelper prefsHelper;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		boolean is_NoNetwork = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		if(is_NoNetwork){
			prefsHelper = new PrefsHelper(context);
			prefsHelper.savePref(PrefsHelper.PREF_IS_NETWORK_STATE_CHANGE, true);
			try{
				QBLogoutChatServiceCommand.start(context);
			}catch(Exception e){
				Log.e("NETWORK_RECEIVER", "logout Chat Service error : "+ e.toString());
			}
			Log.d("NETWORK_RECEIVER", "network change");
		}
		
	}

}
