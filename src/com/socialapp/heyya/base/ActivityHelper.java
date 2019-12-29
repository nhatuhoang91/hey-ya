package com.socialapp.heyya.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.socialapp.heyya.R;
import com.socialapp.heyya.SplashActivity;
import com.socialapp.heyya.core.command.Command;
import com.socialapp.heyya.model.AppSession;
import com.socialapp.heyya.qb.command.QBLoginCompositeCommand;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.utils.ErrorUtils;

public class ActivityHelper extends BaseActivityDelegator{

	private Activity activity;
	// receive action in own activity
	private BaseBroadcastReceiver broadcastReceiver; 
	// receive action global eg.Force Login, Friend Alert
    private GlobalBroadcastReceiver globalBroadcastReceiver; 
    
    private Map<String, Set<Command>> broadcastCommandMap = new HashMap<String, Set<Command>>();
    private GlobalActionsListener actionsListener;
    private Handler handler;
    
	public ActivityHelper(Context context,GlobalActionsListener actionsListener) {
		super(context);
        this.actionsListener = actionsListener;
        activity = (Activity) context;
	}
	
	 public void onCreate() {
	    broadcastReceiver = new BaseBroadcastReceiver();
	    globalBroadcastReceiver = new GlobalBroadcastReceiver();
	 }
	 
	public void addAction(String action, Command command) {
		Log.d("ACTIVITY_HELPER", "add action :"+action);
	        Set<Command> commandSet = broadcastCommandMap.get(action);
	        if (commandSet == null) {
	            commandSet = new HashSet<Command>();
	            broadcastCommandMap.put(action, commandSet);
	        }
	        commandSet.add(command);
	}
	public boolean hasAction(String action) {
	        return broadcastCommandMap.containsKey(action);
	}

	public void removeAction(String action) {
	        broadcastCommandMap.remove(action);
	}
	
	public void updateBroadcastActionList() {
		//because we will use action in new activity. So we must remove old action of old activity
		Log.d("ACTIVITY_HELPER", "update broadcast");
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(broadcastReceiver);
        IntentFilter intentFilter = new IntentFilter();
        for (String commandName : broadcastCommandMap.keySet()) {
            intentFilter.addAction(commandName);
        }
        LocalBroadcastManager.getInstance(activity).registerReceiver(broadcastReceiver, intentFilter);
    }
	
	public void onStart() {
        registerGlobalReceiver();
        updateBroadcastActionList();
    }
	
	 /*public void onResume() {
    registerGlobalReceiver();
    updateBroadcastActionList();
}*/
	
	/*public void onPause() {
		//broadcastCommandMap.clear(); //very important
        unregisterBroadcastReceiver();
    }*/

	public void onStop() {
		//broadcastCommandMap.clear(); //very important
        //unregisterBroadcastReceiver();
    }
	
	public void onDestroy() {
		//broadcastCommandMap.clear(); //very important
        unregisterBroadcastReceiver();
    }
   
    
	
    private void registerGlobalReceiver() {
    	LocalBroadcastManager.getInstance(activity).unregisterReceiver(globalBroadcastReceiver);
        IntentFilter globalActionsIntentFilter = new IntentFilter();
        globalActionsIntentFilter.addAction(QBServiceConsts.FORCE_RELOGIN);
        globalActionsIntentFilter.addAction(QBServiceConsts.REFRESH_SESSION);
        LocalBroadcastManager.getInstance(activity).registerReceiver(globalBroadcastReceiver,
                globalActionsIntentFilter);
    }

    private void unregisterBroadcastReceiver() {
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(globalBroadcastReceiver);
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(broadcastReceiver);
    }
    
    public void forceRelogin() {
        ErrorUtils.showError(activity, activity.getString(
                R.string.force_relogin));
        SplashActivity.start(activity);
        activity.finish();
    }

    public void refreshSession() {
         QBLoginCompositeCommand.start(activity, AppSession.getSession().getUser());
         //QBLoginRestCommand.start(activity,  AppSession.getSession().getUser());
    }
    
    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }
    
    public interface GlobalActionsListener {
        public void onReceiveForceReloginAction(Bundle extras);
        public void onReceiveRefreshSessionAction(Bundle extras);
    }
    
    private class BaseBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent intent) {
        	String action = intent.getAction();
            if (intent != null && (action) != null) {
                Log.d("STEPS", "executing " + action);
                final Set<Command> commandSet = broadcastCommandMap.get(action);

                if (commandSet != null && !commandSet.isEmpty()) {
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            for (Command command : commandSet) {
                                try {
                                    command.execute(intent.getExtras());
                                } catch (Exception e) {
                                    ErrorUtils.logError(e);
                                }
                            }
                        }
                    });
                }
            }
        }
    }
    
    private class GlobalBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent intent) {
        	getHandler().post(new Runnable() {
                @Override
                public void run() {
                    //Bundle extras = intent.getExtras();
                     if (QBServiceConsts.FORCE_RELOGIN.equals(intent.getAction())) {
                        if (actionsListener != null) {
                            actionsListener.onReceiveForceReloginAction(intent.getExtras());
                        }
                    } else if (QBServiceConsts.REFRESH_SESSION.equals(intent.getAction())) {
                        if (actionsListener != null) {
                            actionsListener.onReceiveRefreshSessionAction(intent.getExtras());
                        }
                    } 
                }
            });
        }
    }

}
