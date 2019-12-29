package com.socialapp.heyya.base;

import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.util.Log;

import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.App;
import com.socialapp.heyya.core.command.Command;
import com.socialapp.heyya.model.AppSession;
import com.socialapp.heyya.qb.command.QBLoginCompositeCommand;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.utils.Consts;
import com.socialapp.heyya.utils.PrefsHelper;

public abstract class LoggedInActivity extends BaseActivity{

	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	     super.onCreate(savedInstanceState);
	 }
	 
	 @Override
	 protected void onResume() {
	     super.onResume();
	     checkSessionValid(App.getInstance().getPrefsHelper());
	     progress.onResume();
	 }
	 
	 private void doAutoLogin(String username, String password){
		 	showProgress();
			QBUser user = new QBUser(username, password);
	        QBLoginCompositeCommand.start(this, user);
		}
	 private class LoginSuccessAction implements Command{
			@Override
			public void execute(Bundle bundle) throws Exception {
				// TODO Auto-generated method stub
				Log.d("DO AUTOLOGIN", "do auto login success");
				hideProgress();
				//MainActivity.start(BaseActivity.this);
				//finish();
				handleLoginSuccessAction();
			}
		}
		
		abstract public void handleLoginSuccessAction();
		abstract public void handleLoginFailAction();
		
		private class LoginFailAction implements Command{
			@Override
			public void execute(Bundle bundle) throws Exception {
				// TODO Auto-generated method stub
				hideProgress();
				//Exception e = (Exception)bundle.get(QBServiceConsts.EXTRA_ERROR);
				//DialogUtils.showLong(BaseActivity.this, e.getMessage());
				//Log.e("AUTO LOGIN FAIL", e.toString() , null);
				handleLoginFailAction();
			}
		}
		protected void checkSessionValid(PrefsHelper prefsHelper){
			String username = prefsHelper.getString(PrefsHelper.PREF_USER_ACCOUNT, Consts.EMPTY_STRING);
			String password = prefsHelper.getString(PrefsHelper.PREF_USER_PASSWORD, Consts.EMPTY_STRING);
			if(!AppSession.isSessionExistOrNotExpired(TimeUnit.MINUTES
					.toMillis(Consts.TOKEN_VALID_TIME_IN_MINUTES))){
				Log.d("SPLASH_ACTIVITY","do auto login");
				addAction(QBServiceConsts.LOGIN_SUCCESS_ACTION, new LoginSuccessAction());
			     addAction(QBServiceConsts.LOGIN_FAIL_ACTION, new LoginFailAction());
			     activityHelper.updateBroadcastActionList();
				doAutoLogin(username,password);
			}
		}
}
