package com.socialapp.heyya.qb.helper;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.quickblox.auth.QBAuth;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.db.DatabaseManager;
import com.socialapp.heyya.model.AppSession;
import com.socialapp.heyya.model.User;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.utils.UserUtil;

public class QBAuthHelper extends BaseHelper{

	private final String TAG = "QBAuthHelper";
	Context context;
	public QBAuthHelper(Context context) {
        super(context);
        this.context= context;
    }

	public QBUser login(QBUser inputUser) throws Exception{
		QBUser qbUser ;
		try{
		String defaultAvt = new String("android.resource://"+ context.getPackageName() + "/drawable/placeholder_user.png");
		
		User user;
		QBAuth.createSession();
		String password = inputUser.getPassword();
		qbUser = QBUsers.signIn(inputUser);
		String token = QBAuth.getBaseService().getToken();
		qbUser.setPassword(password);
		AppSession.startSession(qbUser, token);
		String avt_Url = qbUser.getCustomData();
		if(avt_Url != null)
			user = UserUtil.createUser(qbUser, avt_Url);
		else{
			user = UserUtil.createUser(qbUser, defaultAvt);
		}
		DatabaseManager.saveUser(context, user);
		}catch(Exception e){
			Intent i = new Intent(QBServiceConsts.EXTRA_UNEXPECTED_ERROR);
			i.putExtra(QBServiceConsts.EXTRA_ERROR, "error at login() method. " +
					"at QBAuthHelper class. detail :"+e.toString());
			LocalBroadcastManager.getInstance(context).sendBroadcast(i);
			throw e;
		}
		return qbUser;
    }
	public QBUser signup(QBUser inputUser, File imageFile)throws Exception{
		String defaultAvt = new String("android.resource://"+ context.getPackageName() + "/drawable/placeholder_user.png");
		User user;
		QBFile qbFile = null; 
		QBUser qbUser;
		try{
		QBAuth.createSession();
		
        String password = inputUser.getPassword();
        inputUser.setOldPassword(password);
        
        qbUser = QBUsers.signUpSignInTask(inputUser);
        
        if(imageFile != null){
        	qbFile = QBContent.uploadFileTask(imageFile, true, (String)null);
        	inputUser.setCustomData(qbFile.getPublicUrl());
        	qbUser = QBUsers.updateUser(inputUser);
        }
        
        qbUser.setPassword(password);
        String token = QBAuth.getBaseService().getToken();
        AppSession.startSession(qbUser, token);
        if(qbFile != null){
        	user = UserUtil.createUser(qbUser, qbFile.getPublicUrl());
        }else{
        	user = UserUtil.createUser(qbUser, defaultAvt);
        }
        DatabaseManager.saveUser(context, user);
		}catch(Exception e){
			Intent i = new Intent(QBServiceConsts.EXTRA_UNEXPECTED_ERROR);
			i.putExtra(QBServiceConsts.EXTRA_ERROR, "error at signup() method. " +
					"at QBAuthHelper class. detail :"+e.toString());
			LocalBroadcastManager.getInstance(context).sendBroadcast(i);
			throw e;
		}
        return qbUser;
	}
	
	public void logout()throws Exception{
		try{
		AppSession activeSession = AppSession.getSession();
		if(activeSession != null){
			activeSession.closeAndClear();
		}
		}catch(Exception e){
			Intent i = new Intent(QBServiceConsts.EXTRA_UNEXPECTED_ERROR);
			i.putExtra(QBServiceConsts.EXTRA_ERROR, "error at logout() method. " +
					"at QBAuthHelper class. detail :"+e.toString());
			LocalBroadcastManager.getInstance(context).sendBroadcast(i);
			throw e;
		}
		QBAuth.deleteSession();
	}
	
	public QBUser updateUser(QBUser inputUser, File imageFile)throws Exception{
		String password = inputUser.getPassword();
		inputUser.setOldPassword(password);
		QBUser qbUser = QBUsers.signIn(inputUser);
		if(imageFile!=null){
			QBFile qbFile = QBContent.uploadFileTask(imageFile, true, (String)null);
			inputUser.setCustomData(qbFile.getPublicUrl());
		}
		qbUser = QBUsers.updateUser(inputUser);
		qbUser.setPassword(password);
		AppSession.getSession().updateUser(qbUser);
		User user = UserUtil.createUser(qbUser, qbUser.getCustomData());
		int i = DatabaseManager.updateUser(context, user);
		if(i==1){
			Log.d("QBAUTH_HELPER", "update success");
		}else{
			Log.d("QBAUTH_HELPER", "update fail...i="+i);
		}
		return qbUser;
	}
	
	public boolean isUsernameExisted(String username){
		try{
    		QBAuth.createSession();
    		QBUser user = QBUsers.getUserByLogin(username);
    		if(user!=null){
    			return true;
    		}
    		return false;
    	}catch(QBResponseException e){
    		Log.e(TAG, "Error : "+e.toString());
    		return false;
    	}
	}
}
