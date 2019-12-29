package com.socialapp.heyya;

import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.socialapp.heyya.qb.command.GCMTokenRefreshCommand;

public class HeyyaInstanceIDListenerService extends InstanceIDListenerService{
	private static final String TAG = "HeyyaInstanceIDListenerService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
    	Log.d(TAG, "Token refresh");
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
    	GCMTokenRefreshCommand.start(getApplicationContext());
    }
    // [END refresh_token]
}
