package com.socialapp.heyya.ui.accountactivity;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.R;
import com.socialapp.heyya.base.BaseActivity;
import com.socialapp.heyya.base.LoggedInActivity;
import com.socialapp.heyya.concurrency.ReceiveFileFromBitmapTask;
import com.socialapp.heyya.concurrency.ReceiveFileFromBitmapTask.ReceiveFileListener;
import com.socialapp.heyya.concurrency.ReceiveUriScaledBitmapTask;
import com.socialapp.heyya.concurrency.ReceiveUriScaledBitmapTask.ReceiveUriScaledBitmapListener;
import com.socialapp.heyya.core.command.Command;
import com.socialapp.heyya.customview.RoundedDrawable;
import com.socialapp.heyya.customview.RoundedImageView;
import com.socialapp.heyya.db.DatabaseManager;
import com.socialapp.heyya.db.table.UserTable;
import com.socialapp.heyya.model.AppSession;
import com.socialapp.heyya.qb.command.QBUpdateUserCommand;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.utils.Consts;
import com.socialapp.heyya.utils.DialogUtils;
import com.socialapp.heyya.utils.ImageUtil;
import com.socialapp.heyya.utils.ValidationUtils;
import com.soundcloud.android.crop.Crop;

public class AccountActivity extends LoggedInActivity implements ReceiveUriScaledBitmapListener, ReceiveFileListener{

	public static final int GALLERY_INTENT_CALLED = 1;
	public static final int CROP_IMAGE_INTENT_CALLED = 2;
	static final String IMAGE_URI = "image_uri";
	private ImageUtil imageUtil;
	private Uri outputUri = null;
	private Bitmap avatarBitmap;
	private Bitmap rotatedBitmap;
	private boolean isChangedProfilePicture = false;
	private EditText edittext_Change_Fullname;
	private RoundedImageView imageview_profile_picture;
	private FloatingActionButton button_Rotate_Bitmap;
	public static void start(Context context){
		Intent intent = new Intent(context, AccountActivity.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		edittext_Change_Fullname = (EditText)findViewById(R.id.account_activity_edittext_change_fullname);
		imageview_profile_picture = (RoundedImageView)findViewById(R.id.account_imageview_profile_picture);
		button_Rotate_Bitmap = (FloatingActionButton)findViewById(R.id.account_button_rotate);
		
		setUI();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		imageUtil = new ImageUtil(this);
		addActions();
		
		 Log.d("ACCOUNT_ACTIVITY", "onCreate");
	}

	@Override
	 protected void onStart() {
	    super.onStart();
	    Log.d("ACCOUNT_ACTIVITY", "onStart");
	 }

	 @Override
	 protected void onResume() {
	     super.onResume();
	     Log.d("ACCOUNT_ACTIVITY", "onResume");
	 }
	 @Override
	 protected void onPause() {
		 super.onPause();
		 Log.d("ACCOUNT_ACTIVITY", "onPaused");
	 }
	 
	 @Override
	 protected void onStop() {
		 super.onStop();
		 Log.d("ACCOUNT_ACTIVITY", "onStop");
	 }
	 @Override
	 protected void onDestroy() {
		 super.onDestroy();
		 Log.d("ACCOUNT_ACTIVITY", "onDestroy");
	 }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.account, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			navigateToParent();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void addActions(){
		addAction(QBServiceConsts.UPDATE_USER_SUCCESS_ACTION, new UpdateUserSuccess());
		addAction(QBServiceConsts.UPDATE_USER_FAIL_ACTION, new UpdateUserFail());
	}
	private void setUI(){
		try{
			Cursor cursor = DatabaseManager.getUser(this);
			if(!cursor.moveToFirst())
			{
				return;
			}
			String avt_Uri = cursor.getString(cursor.getColumnIndex(UserTable.Cols.AVATAR_URL));
			String fullname= cursor.getString(cursor.getColumnIndex(UserTable.Cols.FULL_NAME));
			
			displayAvatarImage(avt_Uri, imageview_profile_picture);
			edittext_Change_Fullname.setText(fullname);
			button_Rotate_Bitmap.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					rotatedBitmap = ((RoundedDrawable)imageview_profile_picture.getDrawable()).getBitmap();
				
					isChangedProfilePicture=true;
					avatarBitmap = rotateBitmap(rotatedBitmap);
					imageview_profile_picture.setImageBitmap(avatarBitmap);
				}
			});
			
			if(cursor!=null){
				cursor.close();
			}
		}catch(Exception e){
			Log.e("ACCOUNT_ACTIVITY", "error : "+e.toString());
		}
	}
	private void displayAvatarImage (String uri, ImageView imageView){
		try{
		ImageLoader.getInstance().displayImage(uri, imageView, Consts.UIL_USER_PROFILE_PICTURE_DISPLAY_OPTIONS);
		}catch(Exception e){
			Log.e("ACCOUNT_ACTIVITY", "error : "+e.toString());
		}
	}
	@SuppressLint("InlinedApi") 
	public void onClickChangeImage(View view){
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
        	isChangedProfilePicture = true;
            avatarBitmap = imageUtil.getBitmap(outputUri);
            imageview_profile_picture.setImageBitmap(avatarBitmap);
        } else if (resultCode == Crop.RESULT_ERROR) {
            DialogUtils.showLong(this, Crop.getError(result).getMessage());
        }
    }

	@Override
	public void onUriScaledBitmapReceived(Uri uri) {
        // start Crop activity.  next step is onActivityResult()...
		hideProgress();
		startCropActivity(uri);
	}
	private void startCropActivity(Uri originalUri) {
        outputUri = Uri.fromFile(new File(getCacheDir(), Crop.class.getName()));
        new Crop(originalUri).output(outputUri).asSquare().start(this);
    }
	
	public void onClickSaveUser(View view){
		if(isChangedProfilePicture){
			new ReceiveFileFromBitmapTask(this).execute(imageUtil, avatarBitmap);
		}else{
			QBUser qbUser = AppSession.load().getUser();
			if(!edittext_Change_Fullname.getText().toString().equals(qbUser.getFullName())){
				ValidationUtils validation = new ValidationUtils(this);
				if(validation.isInvalidLength(edittext_Change_Fullname.getText().toString(),
						edittext_Change_Fullname, 1, 30))
				{
					return;
				}
				qbUser.setFullName(edittext_Change_Fullname.getText().toString());	
				showProgress();
				QBUpdateUserCommand.start(this, qbUser, null);
			}
		}
	}
	@Override
	public void onCachedImageFileReceived(File imageFile) {
		// TODO Auto-generated method stub
		QBUser qbUser = AppSession.load().getUser();
		if(edittext_Change_Fullname.getText().toString() != qbUser.getFullName())
		{
			ValidationUtils validation = new ValidationUtils(this);
			if(validation.isInvalidLength(edittext_Change_Fullname.getText().toString(),
					edittext_Change_Fullname, 1, 30)){
				return;
			}
			qbUser.setFullName(edittext_Change_Fullname.getText().toString());
		}
		showProgress();
		QBUpdateUserCommand.start(this, qbUser, imageFile);
	}
	
	private class UpdateUserSuccess implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			isChangedProfilePicture = false;
			hideProgress();
			//QBUser qbUser = (QBUser)bundle.getSerializable(QBServiceConsts.EXTRA_USER);
			String avt_Uri = bundle.getString(QBServiceConsts.EXTRA_AVT_URI);
			
			displayAvatarImage(avt_Uri, imageview_profile_picture);
			
			DialogUtils.showLong(AccountActivity.this, "Update user success");
		}
	}
	private class UpdateUserFail implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			hideProgress();
			DialogUtils.showLong(AccountActivity.this, "Update user fail");
		}
	}
	private Bitmap rotateBitmap(Bitmap srcBitmap){
		Matrix matrix = new Matrix();
		matrix.setRotate(90f);
		Bitmap rotatedBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), 
				srcBitmap.getHeight(), matrix, true);
		return rotatedBitmap;
	}

	@Override
	public void handleLoginSuccessAction() {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleLoginFailAction() {
		// TODO Auto-generated method stub
	}
}
