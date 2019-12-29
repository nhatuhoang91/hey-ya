package com.socialapp.heyya;

import java.util.concurrent.TimeUnit;

import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.base.BaseActivity;
import com.socialapp.heyya.core.command.Command;
import com.socialapp.heyya.model.AppSession;
import com.socialapp.heyya.qb.command.QBLoginCompositeCommand;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.ui.loginactivity.LoginActivity;
import com.socialapp.heyya.ui.main.MainActivity;
import com.socialapp.heyya.utils.Consts;
import com.socialapp.heyya.utils.DialogUtils;
import com.socialapp.heyya.utils.PrefsHelper;

import android.text.TextUtils;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends BaseActivity{

	public static void start(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        context.startActivity(intent);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("SPLASH_ACTIVITY", "onCreate");
		setContentView(R.layout.activity_splash);
		useDoubleBackPressed = true;
		chooseActivity(App.getInstance().getPrefsHelper());
		Log.d("SPLASH_ACTIVITY", "onCreate");
	}
	
	@Override
	 protected void onStart() {
	    super.onStart();
	    Log.d("SPLASH_ACTIVITY", "onStart");
	 }

	 @Override
	 protected void onResume() {
	     super.onResume();
	     Log.d("SPLASH_ACTIVITY", "onResume");
	 }
	 @Override
	 protected void onPause() {
		 super.onPause();
		 Log.d("SPLASH_ACTIVITY", "onPaused");
	 }
	 @Override
	 protected void onStop() {
		 super.onStop();
		 Log.d("SPLASH_ACTIVITY", "onStop");
	 }
	 @Override
	 protected void onDestroy() {
		 super.onDestroy();
		 Log.d("SPLASH_ACTIVITY", "onDestroy");
	 }
	private void addActions() {
		addAction(QBServiceConsts.EXTRA_UNEXPECTED_ERROR, new UnexpectedError());
        addAction(QBServiceConsts.LOGIN_SUCCESS_ACTION, new LoginSuccessAction());
        addAction(QBServiceConsts.LOGIN_FAIL_ACTION, new LoginFailAction());
       //updateBroadcastActionList();
    }
	
	private void chooseActivity(PrefsHelper prefsHelper)
	{
		String username = prefsHelper.getString(PrefsHelper.PREF_USER_ACCOUNT, Consts.EMPTY_STRING);
		String password = prefsHelper.getString(PrefsHelper.PREF_USER_PASSWORD, Consts.EMPTY_STRING);
		//String username = prefsHelper.getPref(PrefsHelper.PREF_USER_ACCOUNT, Consts.EMPTY_STRING);
		//String password = prefsHelper.getPref(PrefsHelper.PREF_USER_PASSWORD, Consts.EMPTY_STRING);
		boolean isUsernameEntered = !TextUtils.isEmpty(username);
		boolean isPasswordEntered = !TextUtils.isEmpty(password);
		
		if(!isUsernameEntered && !isPasswordEntered)
		{
			LoginActivity.start(this);
			finish();
		}else{
			/*PREF_IS_NETWORK_STATE_CHANGE is save to false in AppSession model*/
			if (App.getInstance().getPrefsHelper().getBoolean(PrefsHelper.PREF_IS_NETWORK_STATE_CHANGE, false)) {
				addActions();
				doAutoLogin(username, password);
				return;
			}
			if(AppSession.isSessionExistOrNotExpired(TimeUnit.MINUTES
					.toMillis(Consts.TOKEN_VALID_TIME_IN_MINUTES)))
			{
				Log.d("SPLASH_ACTIVITY", "session good");
				MainActivity.start(this);
				finish();
			}else
			{
				Log.d("SPLASH_ACTIVITY","do auto login");
				addActions();
				doAutoLogin(username,password);
			}
		}
	}
	
	private void doAutoLogin(String username, String password){
		QBUser user = new QBUser(username, password);
        QBLoginCompositeCommand.start(this, user);
	}

	private class LoginSuccessAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			Log.d("DO AUTOLOGIN", "do auto login success");
			hideProgress();
			MainActivity.start(SplashActivity.this);
			finish();
		}
	}
	
	
	private class LoginFailAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			Exception e = (Exception)bundle.get(QBServiceConsts.EXTRA_ERROR);
			DialogUtils.showLong(SplashActivity.this, e.getMessage());
			Log.e("AUTO LOGIN FAIL", e.toString() , null);
		}
	}
}
