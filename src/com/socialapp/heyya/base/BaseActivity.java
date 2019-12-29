package com.socialapp.heyya.base;

import com.socialapp.heyya.R;
import com.socialapp.heyya.core.command.Command;
import com.socialapp.heyya.ui.dialog.ProgressDialog;
import com.socialapp.heyya.utils.DialogUtils;
import com.socialapp.heyya.service.*;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public abstract class BaseActivity extends AppCompatActivity{

	public static final int DOUBLE_BACK_DELAY = 2000;
	protected boolean useDoubleBackPressed;
	private boolean doubleBackToExitPressOnce=false;
	protected ProgressDialog progress;
	protected ActivityHelper activityHelper;
	protected ActionBar actionBar;
	private boolean bounded;
	private QBService service;
	private ServiceConnection serviceConnection = new QBChatServiceConnection();
	
	protected ConnectivityChangeReceiver networkReceiver;
	
	public void addAction(String action, Command command) {
        activityHelper.addAction(action, command);
    }

    public boolean hasAction(String action) {
        return activityHelper.hasAction(action);
    }

    public void removeAction(String action) {
        activityHelper.removeAction(action);
    }
    
    public void updateBroadcastActionList() {
        activityHelper.updateBroadcastActionList();
    }
   public synchronized void showProgress() {
        if (!progress.isAdded()) {
            progress.show(getFragmentManager(), null);
        }
    }

    public synchronized void hideProgress() {
        if (progress != null && progress.getActivity() != null) {
            progress.dismissAllowingStateLoss();
        }
    }
    public void hideKeyBoard(EditText edittext){
    	InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	inputManager.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
    }
    @Override
	 protected void onCreate(Bundle savedInstanceState) {
	     super.onCreate(savedInstanceState);
	     Log.d("BASE", "oncreate");
	     actionBar = getSupportActionBar();
	     activityHelper = new ActivityHelper(this, new GlobalListener());
	     activityHelper.onCreate();
	     progress = ProgressDialog.newInstance(R.string.dlg_please_wait);
	    NotificationManager mNotificationManager = (NotificationManager)
	 			this.getSystemService(Context.NOTIFICATION_SERVICE);
	    mNotificationManager.cancelAll();
	 }
   
	 @Override
	 protected void onStart() {
	    super.onStart();
	    registerNetworkChangeReceiver();
	    activityHelper.onStart();
	    connectToService();
	 }

	 @Override
	 protected void onResume() {
	     super.onResume();
	     //activityHelper.onResume();
	     progress.onResume();
	 }

	 @Override
	 protected void onPause() {
		 super.onPause();
		 //activityHelper.onPause();
		 progress.onPause();
	 }

	 @Override
	 protected void onStop() {
		 super.onStop();
		 unRegisterNetworkChangerReceiver();
		 //activityHelper.onStop();
		 unbindService();
	 }
	 
	 @Override
	 protected void onDestroy() {
		 super.onDestroy();
		 activityHelper.onDestroy();
	 }
	    
	 @Override
	 public void onBackPressed() {
	        if (doubleBackToExitPressOnce || !useDoubleBackPressed) {
	            super.onBackPressed();
	            return;
	        }
	        this.doubleBackToExitPressOnce = true;
	        DialogUtils.show(this, getString(R.string.dlg_click_back_again));
	        new Handler().postDelayed(new Runnable() {

	            @Override
	            public void run() {
	                doubleBackToExitPressOnce = false;
	            }
	        }, DOUBLE_BACK_DELAY);
	 }
	 
	 protected void navigateToParent() {
	        Intent intent = NavUtils.getParentActivityIntent(this);
	        if (intent == null) {
	            finish();
	        } else {
	            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	            NavUtils.navigateUpTo(this, intent);
	        }
	    }
	 
	 @SuppressWarnings("unchecked")
	    protected <T> T _findViewById(int viewId) {
	        return (T) findViewById(viewId);
	 }
	 
	 private class GlobalListener implements ActivityHelper.GlobalActionsListener {

		@Override
		public void onReceiveForceReloginAction(Bundle extras) {
			activityHelper.forceRelogin();
		}

		@Override
		public void onReceiveRefreshSessionAction(Bundle extras) {
			activityHelper.refreshSession();
		}
		/*
		@Override
        public void onReceiveFriendActionAction(Bundle extras) {
            String alertMessage = extras.getString(QBServiceConsts.EXTRA_FRIEND_ALERT_MESSAGE);
            activityHelper.showFriendAlert(alertMessage);
        }*/
	 }
	 
	 protected class ConnectivityChangeReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean is_NoNetwork = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			if(is_NoNetwork){
				DialogInterface.OnClickListener listener = new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				};
				DialogUtils.createNoNetworkDialog(context, listener).show();
				Log.d("NETWORK_REVEIVER", "No network reveiver");
			}
		}
	 }
	 
	 protected void registerNetworkChangeReceiver (){
		 if(networkReceiver == null)
			 networkReceiver = new ConnectivityChangeReceiver();
	     registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	 }
	 protected void unRegisterNetworkChangerReceiver(){
		 if(networkReceiver!=null)
			 unregisterReceiver(networkReceiver);
	 }
	 
	 private void connectToService() {
	        Intent intent = new Intent(this, QBService.class);
	        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	 }
	 
	 private void unbindService() {
	        if (bounded) {
	            unbindService(serviceConnection);
	        }
	 }
	 private class QBChatServiceConnection implements ServiceConnection {

	        @Override
	        public void onServiceConnected(ComponentName name, IBinder binder) {
	            bounded = true;
	            service = ((QBService.QBServiceBinder) binder).getService();
	            //onConnectedToService();
	        }

	        @Override
	        public void onServiceDisconnected(ComponentName name) {

	        }
	 }
	 
	 public class UnexpectedError implements Command{

			@Override
			public void execute(Bundle bundle) throws Exception {
				// TODO Auto-generated method stub
				String e = bundle.getString(QBServiceConsts.EXTRA_ERROR);
				//DialogUtils.showLong(BaseActivity.this, "Unexpected Error");
				DialogUtils.showLong(BaseActivity.this, "Error : "+e);
			}
			
		}
	
}
