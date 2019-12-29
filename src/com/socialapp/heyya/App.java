package com.socialapp.heyya;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.core.QBSettings;
import com.socialapp.heyya.utils.Consts;
import com.socialapp.heyya.utils.ImageUtil;
import com.socialapp.heyya.utils.PrefsHelper;
import android.app.Application;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class App extends Application{

	private static App instance;
	private PrefsHelper prefsHelper;
	private static WakeLock wakeLock; 
	private static  PowerManager powerManager;
	public static App getInstance() {
	      return instance;
	  }
	 @Override
	 public void onCreate() {
	     super.onCreate();
	     initApplication();
	     powerManager = (PowerManager) getSystemService(POWER_SERVICE);
	    startWakeLock();
	 }
	 public static void startWakeLock(){
		 	wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
		 	        "MyWakelockTag");
		 	wakeLock.acquire();
	 }
	 //call in QBChatRestHelper logout
	 public static void releaseWakeLock(){
		 if(wakeLock.isHeld())
			 wakeLock.release();
	 }
	 public PrefsHelper getPrefsHelper() {
	        return prefsHelper;
	    }
	 private void initImageLoader(Context context) {
	        ImageLoader.getInstance().init(ImageUtil.getImageLoaderConfiguration(context));
	    }
	 private void initApplication() {
	        instance = this;        
	        initImageLoader(this);
	        //QBChatService.setDebugEnabled(true);
	        //QBChatService.init(this);
	        QBSettings.getInstance().fastConfigInit(Consts.QB_APP_ID, Consts.QB_AUTH_KEY, Consts.QB_AUTH_SECRET);
	        prefsHelper = new PrefsHelper(this);
    }
}
