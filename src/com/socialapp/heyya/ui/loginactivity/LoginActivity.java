package com.socialapp.heyya.ui.loginactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.App;
import com.socialapp.heyya.R;
import com.socialapp.heyya.base.BaseActivity;
import com.socialapp.heyya.core.command.Command;
import com.socialapp.heyya.qb.command.QBLoginCompositeCommand;
import com.socialapp.heyya.qb.command.QBLogoutCompositeCommand;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.ui.main.MainActivity;
import com.socialapp.heyya.ui.signupactivity.SignUpActivity;
import com.socialapp.heyya.utils.DialogUtils;
import com.socialapp.heyya.utils.PrefsHelper;
import com.socialapp.heyya.utils.ValidationUtils;

public class LoginActivity extends BaseActivity{

	private ValidationUtils validationUtils;
	private  EditText usernameEditText;
	private  EditText passwordEditText;
	public static void start(Context context){
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		useDoubleBackPressed = true;
		validationUtils = new ValidationUtils(this);
		initUI();
		addActions();
		 Log.d("LOGIN_ACTIVITY", "onCreate");
	}
	
	@Override
	 protected void onStart() {
	    super.onStart();
	    Log.d("LOGIN_ACTIVITY", "onStart");
	 }

	 @Override
	 protected void onResume() {
	     super.onResume();
	     Log.d("LOGIN_ACTIVITY", "onResume");
	 }
	 @Override
	 protected void onPause() {
		 super.onPause();
		 Log.d("LOGIN_ACTIVITY", "onPaused");
	 }
	 
	 @Override
	 protected void onStop() {
		 super.onStop();
		 Log.d("LOGIN_ACTIVITY", "onStop");
	 }
	 @Override
	 protected void onDestroy() {
		 super.onDestroy();
		 Log.d("LOGIN_ACTIVITY", "onDestroy");
	 }
	private void initUI(){
		usernameEditText = _findViewById(R.id.login_edittext_username);
		passwordEditText = _findViewById(R.id.login_edittext_password);
	}
	public void onClickLogin(View view){
		String username = usernameEditText.getText().toString().trim();
		String password = passwordEditText.getText().toString().trim();
		if(validationUtils.isValidLoginEditText(usernameEditText, passwordEditText, username, password)){
			App.getInstance().getPrefsHelper().savePref(PrefsHelper.PREF_IS_LOGINING, true); //notify to QBFriendHelper
			login(username,password);
		}
	}
	public void onClickStartSignUp(View view){
		if(App.getInstance().getPrefsHelper().getInteger(PrefsHelper.PREF_NUM_SIGNUP, 0) < 5){
			SignUpActivity.start(this);
			finish();
		}else{
			DialogUtils.showLong(this, "This device have sign up 5 account." +
					" So you can't sign up any more. Please uninstall and reinstall this app");
		}
	}
	private void addActions() {
		addAction(QBServiceConsts.EXTRA_UNEXPECTED_ERROR, new UnexpectedError());
		addAction(QBServiceConsts.EXTRA_GCM_ERROR, new GCMError());
        addAction(QBServiceConsts.LOGIN_SUCCESS_ACTION, new LoginSuccessAction());
        addAction(QBServiceConsts.LOGIN_FAIL_ACTION, new LoginFailAction());
        //updateBroadcastActionList();
    }
	
	private void login(String username, String password){
		QBUser user = new QBUser(username, password);
        showProgress();
        QBLoginCompositeCommand.start(this, user);
        
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	
	private class LoginSuccessAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			App.getInstance().getPrefsHelper().savePref(PrefsHelper.PREF_IS_LOGINING, false);
			hideProgress();
			MainActivity.start(LoginActivity.this);
			finish();
		}
	}
	
	private class GCMError implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			//hideProgress();
			//Log.d("LOGIN_ACTIVITY", "GCMERROR: logout now");
			//String error = bundle.getString(QBServiceConsts.EXTRA_ERROR);
			//DialogUtils.showLong(LoginActivity.this, error);
			//QBLogoutCompositeCommand.start(LoginActivity.this);
		}
	}
	
	private class LoginFailAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			hideProgress();
			App.getInstance().getPrefsHelper().savePref(PrefsHelper.PREF_IS_LOGINING, false);
			Exception e = (Exception)bundle.get(QBServiceConsts.EXTRA_ERROR);
			if(e.toString().contains("506")){
				DialogUtils.showLong(LoginActivity.this, "This account already use in another device" +
						". Please logout first then re-login in this device");
			}else
				if(e.toString().contains("507")
						|| e.toString().contains("508")
						|| e.toString().contains("509")
						|| e.toString().contains("error default")){
					DialogUtils.showLong(LoginActivity.this, "Server error. Please try again");
				}else{
					DialogUtils.showLong(LoginActivity.this, "Login fail. Make sure username and password is correct");
					QBLogoutCompositeCommand.start(LoginActivity.this);
					hideProgress();
				}
				
		}
	}
}
