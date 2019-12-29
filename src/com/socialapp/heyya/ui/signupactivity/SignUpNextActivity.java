package com.socialapp.heyya.ui.signupactivity;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.App;
import com.socialapp.heyya.R;
import com.socialapp.heyya.base.BaseActivity;
import com.socialapp.heyya.concurrency.ReceiveFileFromBitmapTask;
import com.socialapp.heyya.concurrency.ReceiveFileFromBitmapTask.ReceiveFileListener;
import com.socialapp.heyya.concurrency.ReceiveUriScaledBitmapTask;
import com.socialapp.heyya.concurrency.ReceiveUriScaledBitmapTask.ReceiveUriScaledBitmapListener;
import com.socialapp.heyya.core.command.Command;
import com.socialapp.heyya.customview.RoundedDrawable;
import com.socialapp.heyya.customview.RoundedImageView;
import com.socialapp.heyya.qb.command.QBInitVoiceCallCommand;
import com.socialapp.heyya.qb.command.QBSignUpCompositeCommand;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.ui.main.MainActivity;
import com.socialapp.heyya.utils.DialogUtils;
import com.socialapp.heyya.utils.ImageUtil;
import com.socialapp.heyya.utils.PrefsHelper;
import com.soundcloud.android.crop.Crop;

public class SignUpNextActivity extends BaseActivity implements ReceiveUriScaledBitmapListener, ReceiveFileListener{

	private Uri outputUri = null;
	private Bitmap avatarBitmap;
	private Bitmap rotatedBitmap;
	private boolean isNeedUpdateAvatar;
	private QBUser user;
	public static final int GALLERY_INTENT_CALLED = 1;
	private Intent intent;
	private String username;
	private String fullname;
	private String password;
	private String email;
	
	private RoundedImageView imageView;
	private ImageUtil imageUtil;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up_next);
		intent = getIntent();
		username = intent.getStringExtra(SignUpActivity.USERNAME);
		fullname = intent.getStringExtra(SignUpActivity.FULLNAME);
		password = intent.getStringExtra(SignUpActivity.PASSWORD);
		imageUtil = new ImageUtil(this);
		initUi();
		addActions();
	}

	@Override
	 protected void onPause() {
		 super.onPause();
	 }
	
	@Override
	 protected void onStart() {
	    super.onStart();
	 }

	 @Override
	 protected void onResume() {
	     super.onResume();
	 }

	 @Override
	 protected void onStop() {
		 super.onStop();
	 }
	private void initUi(){
		imageView = (RoundedImageView)_findViewById(R.id.signup_imageview);
	}
	private void addActions(){
		addAction(QBServiceConsts.EXTRA_UNEXPECTED_ERROR, new UnexpectedError());
		addAction(QBServiceConsts.SIGNUP_SUCCESS_ACTION, new SignUpSuccessAction());
		addAction(QBServiceConsts.SIGNUP_FAIL_ACTION, new SignUpFailAction());
	}
	public void onClickRotate(View view){
		if(!isNeedUpdateAvatar){
			return;
		}
		rotatedBitmap = ((RoundedDrawable)imageView.getDrawable()).getBitmap();
		avatarBitmap = rotateBitmap(rotatedBitmap);
		imageView.setImageBitmap(avatarBitmap);
	}
	public void onClickSignUp(View view){
		user = new QBUser();
		user.setLogin(username);
		user.setFullName(fullname);
		user.setPassword(password);
		showProgress();
		if(isNeedUpdateAvatar){
			new ReceiveFileFromBitmapTask(this).execute(imageUtil,avatarBitmap);
		}else{
			QBSignUpCompositeCommand.start(this, user,null);
		}
	}
	@Override
	public void onCachedImageFileReceived(File imageFile) {
		// TODO Auto-generated method stub
		QBSignUpCompositeCommand.start(this, user ,imageFile);
	}
	public void onClickBack(View view){
		SignUpActivity.start(this);
		finish();
	}
	@SuppressLint("InlinedApi") 
	public void onClickGetImage(View view){
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
			Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, GALLERY_INTENT_CALLED);
		}else {
			Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
	        intent.addCategory(Intent.CATEGORY_OPENABLE);
	        intent.setType("image/*");
	        startActivityForResult(intent, GALLERY_INTENT_CALLED);
        }
	}
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == Crop.REQUEST_CROP){
			handleCrop(resultCode, data);
		}else
		if (requestCode == GALLERY_INTENT_CALLED && resultCode == RESULT_OK) {
			//imageUri = data.getData(); 
			//imageView.setImageURI(imageUri);
			Uri originalUri = data.getData(); //get Uri of picture which was choosen
            if (originalUri != null) {
                showProgress();
                //scale picture from Uri. this function will call OnUriScaledBitmapTaskListener()...below.
                new ReceiveUriScaledBitmapTask(this).execute(imageUtil, originalUri);
            }
		}
	}
	 private void handleCrop(int resultCode, Intent result) {
	        if (resultCode == RESULT_OK) {
	            isNeedUpdateAvatar = true; // mark to user choosen picture. this var use in sign Up action
	            avatarBitmap = imageUtil.getBitmap(outputUri);
	            imageView.setImageBitmap(avatarBitmap);
	        } else if (resultCode == Crop.RESULT_ERROR) {
	            DialogUtils.showLong(this, Crop.getError(result).getMessage());
	        }
	    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up_next, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class SignUpSuccessAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			int num_signup = App.getInstance().getPrefsHelper().getInteger(PrefsHelper.PREF_NUM_SIGNUP, 0);
			App.getInstance().getPrefsHelper().savePref(PrefsHelper.PREF_NUM_SIGNUP, num_signup+1);
			hideProgress();
			MainActivity.start(SignUpNextActivity.this);
			finish();
		}
	}
	private class SignUpFailAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			hideProgress();
			String error = bundle.getString(QBServiceConsts.EXTRA_ERROR);
			DialogUtils.showLong(SignUpNextActivity.this, "Error : "+error);
		}
	}
	@Override
	public void onUriScaledBitmapReceived(Uri uri) {
		hideProgress();
        // start Crop activity.  next step is onActivityResult()...
        startCropActivity(uri);
	}
	private void startCropActivity(Uri originalUri) {
        outputUri = Uri.fromFile(new File(getCacheDir(), Crop.class.getName()));
        new Crop(originalUri).output(outputUri).asSquare().start(this);
    }
	
	private Bitmap rotateBitmap(Bitmap srcBitmap){
		Matrix matrix = new Matrix();
		matrix.setRotate(90f);
		Bitmap rotatedBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), 
				srcBitmap.getHeight(), matrix, true);
		return rotatedBitmap;
	}
}
