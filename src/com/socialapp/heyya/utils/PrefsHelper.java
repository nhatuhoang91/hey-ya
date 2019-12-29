package com.socialapp.heyya.utils;


import com.socialapp.heyya.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.renderscript.Sampler.Value;

public class PrefsHelper {

	public static final String PREF_USER_ACCOUNT = "account";// is user login
	public static final String PREF_USER_PASSWORD = "password";
	public static final String PREF_USER_ID = "user_id";
	public static final String PREF_USER_FULL_NAME = "user_full_name";
	public static final String PREF_SESSION_TOKEN = "session_token"; //use for login
	public static final String PREF_IS_NETWORK_STATE_CHANGE = "network_state_change";
	public static final String PREF_IS_LOGINING = "is_logining";
	public static final String PREF_GCM_REG_ID = "gcm_reg_id";
	public static final String PREF_APP_VERSION = "app_version";
	public static final String PREF_NOTIFICATION_ID = "notification_id";
	public static final String PREF_IS_CALLING = "is_calling";
	public static final String PREF_NUM_SIGNUP = "num_signup";
	 private final SecuredSharedPreferences sharedPreferences;
	 private final SecuredSharedPreferences.Editor editor;
	 
	 public PrefsHelper(Context context) {
	        String prefsFile = context.getString(R.string.pref_name);
	        sharedPreferences = new SecuredSharedPreferences(context, prefsFile);
	        editor = sharedPreferences.edit();
	    }
	 
	 public void delete(String key) {
	        if (sharedPreferences.contains(key)) {
	            editor.remove(key).commit();
	        }
	    }

	    public void savePref(String key, Object value) {
	        delete(key);

	        if (value instanceof Boolean) {
	            editor.putBoolean(key, (Boolean) value);
	        } else if (value instanceof Integer) {
	            editor.putInt(key, (Integer) value);
	        } else if (value instanceof Float) {
	            editor.putFloat(key, (Float) value);
	        } else if (value instanceof Long) {
	            editor.putLong(key, (Long) value);
	        } else if (value instanceof String) {
	            editor.putString(key, (String) value);
	        } else if (value instanceof Enum) {
	            editor.putString(key, value.toString());
	        } else if (value != null) {
	            throw new RuntimeException("Attempting to save non-primitive preference");
	        }

	        editor.commit();
	    }
	    
	    public Boolean getBoolean(String key, Boolean defValue){
	    	return sharedPreferences.getBoolean(key, defValue);
	    }
	    
	    public Integer getInteger(String key, Integer defValue){
	    	return sharedPreferences.getInt(key, defValue);
	    }
	    
	    public String getString(String key, String defValue){
	    	return sharedPreferences.getString(key, defValue);
	    }
	    
	    public Long getLong(String key, Long defValue){
	    	return sharedPreferences.getLong(key, defValue);
	    }
	    
	    public Float getFloat(String key, Float defValue){
	    	return sharedPreferences.getFloat(key, defValue);
	    }
	    
	    public boolean isPrefExists(String key) {
	        return sharedPreferences.contains(key);
	    }
}
