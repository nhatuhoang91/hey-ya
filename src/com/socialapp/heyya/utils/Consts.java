package com.socialapp.heyya.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.socialapp.heyya.R;

public class Consts {
	 public static final String QB_APP_ID = "16159";
	 public static final String QB_AUTH_KEY = "bLdbq7hW7V93ntP";
	 public static final String QB_AUTH_SECRET = "LMYSBUsBS3nh9mb";
	 
	 public static final String EMPTY_STRING = "";
	 public static final String USER_ACCOUNT = "user_account";
	 public static final String USER_PASSWORD = "user_password";
	 public static final String USER_ID = "user_id";
	 public static final String USER_FULLNAME = "user_fullname";
	 public static final String SESSION_TOKEN = "session_token";
	 public static final String IS_NETWORK_STATE_CHANGE = "is_network_state_change";
	 public static final String IS_LOGGING = "is_logging";
	 public static final String GCM_REG_ID = "gcm_reg_id";
	 public static final String NOTIFICATION_ID = "notification_id";
	 public static final String IS_CALLING = "is_calling";
	 public static final String NUM_SIGNUP = "num_signup";
	 
	 public static final int NOT_INITIALIZED_VALUE = -1;
	 public static final int ZERO_INT_VALUE = 0;
	 
	 public static final int TOKEN_VALID_TIME_IN_MINUTES = 1;
	 public static final String TOKEN_REQUIRED_ERROR = "Token is required";
	 public static final String SESSION_DOES_NOT_EXIST = "Required session does not exist";
	 
	 public static final String ASSET_SOUND_PATH = "sound/";
	 
	 public static final int DEFAULT_PACKET_REPLY_TIMEOUT = 15 * 1000;
	 
	 public static final int LOGIN_TIMEOUT = 40000;
	 
	 public static final int FULL_QUALITY = 100;
	 
	// Universal Image Loader
	    public static final DisplayImageOptions UIL_DEFAULT_DISPLAY_OPTIONS = new DisplayImageOptions.Builder()
	            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.RGB_565)
	            .cacheInMemory(true).build();

	    public static final DisplayImageOptions UIL_USER_AVATAR_DISPLAY_OPTIONS = new DisplayImageOptions.Builder()
	            .showImageOnLoading(R.drawable.default_avt_image).showImageForEmptyUri(R.drawable.default_avt_image)
	            .showImageOnFail(R.drawable.default_avt_image).cacheInMemory(true).build();
	    
	    public static final DisplayImageOptions UIL_USER_PROFILE_PICTURE_DISPLAY_OPTIONS = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.default_avt_image).showImageForEmptyUri(R.drawable.default_avt_image)
        .showImageOnFail(R.drawable.default_avt_image).cacheInMemory(true).build();
}
