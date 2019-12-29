package com.socialapp.heyya.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBCallback;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.result.Result;
import com.quickblox.core.server.BaseService;
import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.App;
import com.socialapp.heyya.utils.Consts;
import com.socialapp.heyya.utils.ErrorUtils;
import com.socialapp.heyya.utils.PrefsHelper;

public class AppSession implements Serializable{
 
	private static final Object lock = new Object();
    private static AppSession activeSession;
    private QBUser user;
    private String sessionToken;
    private AppSession(QBUser user, String sessionToken) {
        this.user = user;
        this.sessionToken = sessionToken;
        save();
    }
    
    public static void startSession(QBUser user, String sessionToken) {
        activeSession = new AppSession(user, sessionToken);
    }
    private static AppSession getActiveSession() {
        synchronized (lock) {
            return activeSession;
        }
    }
    
    public static AppSession load() {
        PrefsHelper helper = App.getInstance().getPrefsHelper();
        String userId = helper.getString(PrefsHelper.PREF_USER_ID, Consts.EMPTY_STRING);
        String userAccount = helper.getString(PrefsHelper.PREF_USER_ACCOUNT, Consts.EMPTY_STRING);
        String userFullName = helper.getString(PrefsHelper.PREF_USER_FULL_NAME, Consts.EMPTY_STRING);
        String password = helper.getString(PrefsHelper.PREF_USER_PASSWORD, Consts.EMPTY_STRING);
        String sessionToken = helper.getString(PrefsHelper.PREF_SESSION_TOKEN, Consts.EMPTY_STRING);
        QBUser qbUser = new QBUser();
        qbUser.setId(Integer.valueOf(userId));
        qbUser.setPassword(password);
        qbUser.setLogin(userAccount);
        qbUser.setFullName(userFullName);
        return new AppSession(qbUser, sessionToken);
    }
    
    public static boolean isSessionExistOrNotExpired(long expirationTime) {
        try {
        	BaseService baseService = QBAuth.getBaseService();
            String token = baseService.getToken();
            if (token == null) {
                return false;
            }
            Date tokenExpirationDate = baseService.getTokenExpirationDate();
            Log.d("APP_SESSION", "tokenExpireationDate : "+tokenExpirationDate.toString());
            Log.d("APP_SESSION", "tokenExpireationDate.getTime : "+tokenExpirationDate.getTime());
            long tokenLiveOffset = tokenExpirationDate.getTime() - System.currentTimeMillis();
            Log.d("APP_SESSION", "tokenLiveOffset : "+tokenLiveOffset);
            Log.d("APP_SESSION", "expireTime : "+expirationTime);
            return tokenLiveOffset > expirationTime;
        } catch (BaseServiceException e) {
            ErrorUtils.logError(e);
        }
        return false;
    }
    
    public static AppSession getSession() {
        AppSession activeSession = AppSession.getActiveSession();
        if (activeSession == null) {
            activeSession = AppSession.load();
        }
        return activeSession;
    }
    
    public void save() {
        PrefsHelper prefsHelper = App.getInstance().getPrefsHelper();
        prefsHelper.savePref(PrefsHelper.PREF_SESSION_TOKEN, sessionToken);
        prefsHelper.savePref(PrefsHelper.PREF_IS_NETWORK_STATE_CHANGE, false);
        saveUser(user, prefsHelper);
    }
    
    public void updateUser(QBUser user) {
        this.user = user;
        saveUser(this.user, App.getInstance().getPrefsHelper());
    }
    
    private void saveUser(QBUser user, PrefsHelper prefsHelper) {
        prefsHelper.savePref(PrefsHelper.PREF_USER_ID, user.getId().toString());
        prefsHelper.savePref(PrefsHelper.PREF_USER_ACCOUNT, user.getLogin());
        prefsHelper.savePref(PrefsHelper.PREF_USER_FULL_NAME, user.getFullName());
        prefsHelper.savePref(PrefsHelper.PREF_USER_PASSWORD, user.getPassword());
    }
    
    public void closeAndClear() {
        PrefsHelper helper = App.getInstance().getPrefsHelper();
        helper.delete(PrefsHelper.PREF_USER_ACCOUNT);
        helper.delete(PrefsHelper.PREF_USER_FULL_NAME);
        helper.delete(PrefsHelper.PREF_USER_PASSWORD);
        helper.delete(PrefsHelper.PREF_SESSION_TOKEN);
        helper.delete(PrefsHelper.PREF_USER_ID);
        helper.delete(PrefsHelper.PREF_IS_NETWORK_STATE_CHANGE);
        helper.delete(PrefsHelper.PREF_IS_LOGINING);
        if(helper.isPrefExists(PrefsHelper.PREF_APP_VERSION))
        	helper.delete(PrefsHelper.PREF_APP_VERSION);
        if(helper.isPrefExists(PrefsHelper.PREF_GCM_REG_ID))
        	helper.delete(PrefsHelper.PREF_GCM_REG_ID);
        if(helper.isPrefExists(PrefsHelper.PREF_NOTIFICATION_ID))
        	helper.delete(PrefsHelper.PREF_NOTIFICATION_ID);
        if(helper.isPrefExists(PrefsHelper.PREF_IS_CALLING))
        	helper.delete(PrefsHelper.PREF_IS_CALLING);
        activeSession = null;
    }
    
    public QBUser getUser() {
        return user;
    }
    
    public boolean isSessionExist() {
    	PrefsHelper helper = App.getInstance().getPrefsHelper();
        return  helper.getString(PrefsHelper.PREF_USER_ACCOUNT, null)!= null && !TextUtils.isEmpty(sessionToken);
    }
}
