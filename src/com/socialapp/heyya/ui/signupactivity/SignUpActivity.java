package com.socialapp.heyya.ui.signupactivity;

import java.util.ArrayList;

import com.socialapp.heyya.R;
import com.socialapp.heyya.base.BaseActivity;
import com.socialapp.heyya.core.command.Command;
import com.socialapp.heyya.qb.command.QBCheckIsUsernameExistedCommand;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.ui.loginactivity.LoginActivity;
import com.socialapp.heyya.utils.DialogUtils;
import com.socialapp.heyya.utils.ValidationUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class SignUpActivity extends BaseActivity{
	public static final String USERNAME = "username";
	public static final String FULLNAME = "fullname";
	public static final String PASSWORD = "password";
	public static final String RE_PASSWORD = "re-password";
	public static final String PHONE = "phone";
	private ValidationUtils validation;
	private EditText usernameEdittext;
	private EditText fullnameEdittext;
	private EditText passwordEdittext;
	private EditText rePasswordEdittext;
	
	public static void start(Context context){
		Intent intent = new Intent(context, SignUpActivity.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		validation = new ValidationUtils(this);
		initUi();
		addActions();
		Log.d("SIGNUP_ACTIVITY", "onCreate");
	}

	@Override
	 protected void onStart() {
	    super.onStart();
	    Log.d("SIGNUP_ACTIVITY", "onStart");
	 }

	 @Override
	 protected void onResume() {
	     super.onResume();
	     Log.d("SIGNUP_ACTIVITY", "onResume");
	 }
	
	@Override
	 protected void onPause() {
		 super.onPause();
		 Log.d("SIGNUP_ACTIVITY", "onPause");
	 }
	
	@Override
	 protected void onStop() {
		 super.onStop();
		 Log.d("SIGNUP_ACTIVITY", "onStop");
	 }
	 @Override
	 protected void onDestroy() {
		 super.onDestroy();
		 Log.d("SIGNUP_ACTIVITY", "onDestroy");
	 }
	private void initUi(){
		usernameEdittext = (EditText)findViewById(R.id.signup_edittext_username);
		fullnameEdittext = (EditText)findViewById(R.id.signup_edittext_fullname);
		passwordEdittext = (EditText)findViewById(R.id.signup_edittext_password);
		rePasswordEdittext = (EditText)findViewById(R.id.signup_edittext_repassword);
	}
	
	private void addActions(){
		addAction(QBServiceConsts.CHECK_USERNAME_EXISTED_SUCCESS_ACTION, new CheckUsernameExistedSuccessCommand());
		addAction(QBServiceConsts.CHECK_USERNAME_EXISTED_FAIL_ACTION, new UnexpectedError());
	}
	
	public void onClickNext(View view){
		ArrayList<EditText> listEdittext = new ArrayList<EditText>();
		listEdittext.add(usernameEdittext);
		listEdittext.add(fullnameEdittext);
		listEdittext.add(passwordEdittext);
		listEdittext.add(rePasswordEdittext);
		if(validation.isValidSignUpEdittext(listEdittext)){
			QBCheckIsUsernameExistedCommand.start(this, usernameEdittext.getText().toString());
		}
	}
	public void onClickCancel(View view){
		LoginActivity.start(this);
		finish();
	}
	
	
	private class CheckUsernameExistedSuccessCommand implements Command{

		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			boolean result = bundle.getBoolean(QBServiceConsts.EXTRA_IS_USERNAME_EXISTED);
			if(result){
				usernameEdittext.setError(SignUpActivity.this.getString(R.string.valid_edittext_username_existed));
			}else{
				Intent intent = new Intent(SignUpActivity.this, SignUpNextActivity.class);
				intent.putExtra(USERNAME, usernameEdittext.getText().toString());
				intent.putExtra(FULLNAME, fullnameEdittext.getText().toString());
				intent.putExtra(PASSWORD, passwordEdittext.getText().toString());
				startActivity(intent);
				finish();
			}
		}
		
	}
}
