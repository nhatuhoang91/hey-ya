package com.socialapp.heyya.ui.main;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialapp.heyya.R;
import com.socialapp.heyya.SplashActivity;
import com.socialapp.heyya.adapter.TabPageAdapter;
import com.socialapp.heyya.base.LoggedInActivity;
import com.socialapp.heyya.core.command.Command;
import com.socialapp.heyya.db.DatabaseManager;
import com.socialapp.heyya.db.table.UserTable;
import com.socialapp.heyya.gcm.GPSHelper;
import com.socialapp.heyya.model.Notification;
import com.socialapp.heyya.qb.command.QBLogoutCompositeCommand;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.ui.accountactivity.AccountActivity;
import com.socialapp.heyya.utils.Consts;
import com.socialapp.heyya.utils.DialogUtils;
import com.socialapp.heyya.utils.Utils;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends LoggedInActivity{
//	implements LoaderCallbacks<Cursor>{
	//, HandleDatabaseChanges{
//	TextView friendCountTextview;
	//TextView notificationCountTextview;
	
	public static void start(Context context){
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
	}
	
	NavigationView navigationView;
	ImageView header_avt;
	TextView header_name;
	
	CoordinatorLayout coordinatorLayout;
	CollapsingToolbarLayout collapsingToolbarLayout;
	TabLayout tabLayout;
	 Toolbar toolbar;
	 
	 GPSHelper gpsHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("MAIN_ACTIVITY", "onCreate");
		useDoubleBackPressed = true;
		
		coordinatorLayout =(CoordinatorLayout)findViewById(R.id.coordinatorLayout);
		setupNavigationView();
		setupColapsingToolBarLayout();
		setupToolbar();
		setupTablayout();
		
		 // Get the ViewPager and set it's PagerAdapter so that it can display items
		
		//friendCountTextview = (TextView)findViewById(R.id.main_activity_layout_friend_counter);
		//notificationCountTextview = (TextView)findViewById(R.id.main_activity_layout_notifications_counter);
		
		/*FriendDBLoaderCallBack friendDBLoaderCallBack = new FriendDBLoaderCallBack(this);
		NotificationsDBLoaderCallBack notificationsDBLoaderCallBack = new NotificationsDBLoaderCallBack(this);
		getSupportLoaderManager().initLoader(FriendDBLoaderCallBack.LOADER_ID, null, friendDBLoaderCallBack);
		getSupportLoaderManager().initLoader(NotificationsDBLoaderCallBack.LOADER_ID, null, notificationsDBLoaderCallBack);
		*/
		addActions();
	}
	private void setupNavigationView(){
		header_avt = (ImageView)findViewById(R.id.nav_header_avt);
		header_name = (TextView)findViewById(R.id.nav_header_name);
		Cursor cursor;
		try {
			cursor = DatabaseManager.getUser(this);
			if(cursor.moveToFirst()){
				String avt_uri = cursor.getString(cursor.getColumnIndex(UserTable.Cols.AVATAR_URL));
				displayAvatarImage(avt_uri, header_avt);
				String name = cursor.getString(cursor.getColumnIndex(UserTable.Cols.FULL_NAME));
				header_name.setText(name);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		navigationView = (NavigationView)findViewById(R.id.navigation_view);
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
	        @Override
	        public boolean onNavigationItemSelected(MenuItem menuItem) {
	            int id = menuItem.getItemId();
	            switch (id) {
	                case R.id.nav_menu_account:
	                	AccountActivity.start(MainActivity.this);
	                    break;
	                case R.id.nav_menu_logout:
	                		createLogoutConfirmDialog();
	                    break;
	            }
	            return false;
	        }
	    });
	 }
	 private void setupToolbar(){
	        toolbar = (Toolbar) findViewById(R.id.toolbar);
	        setSupportActionBar(toolbar);
	    // Show menu icon
	        final ActionBar ab = getSupportActionBar();
	        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
	        ab.setDisplayHomeAsUpEnabled(true);
	 }
	 private void setupColapsingToolBarLayout(){
		 collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
		 collapsingToolbarLayout.setTitle("Event Hunter");
	 }
	private void setupTablayout(){
		TabPageAdapter tabPageAdapter = new TabPageAdapter(getSupportFragmentManager());
		//tabPageAdapter.addFrag(FriendFragment.getInstance(), "FRIEND");
		//tabPageAdapter.addFrag(HunterFragment.getInstance(), "HUNTER");
		//tabPageAdapter.addFrag(SearchFragment.getInstance(), "SEARCH");
		
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(tabPageAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);
	}
	@Override
	 protected void onStart() {
		 super.onStart();
		 Log.d("MAIN_ACTIVITY", "onStart");
	 }
	@Override
	 protected void onResume() {
	     super.onResume();
	     Log.d("MAIN_ACTIVITY", "onResume");
	     gpsHelper = new GPSHelper(this);
	 }
	@Override
	 protected void onPause() {
		 super.onPause();
		 Log.d("MAIN_ACTIVITY", "onPaused");
	 }
	@Override
	 protected void onStop() {
		 super.onStop();
		 Log.d("MAIN_ACTIVITY", "onStop");
		 if(gpsHelper!=null)
			 gpsHelper.disconnectToGoogleServer();
	 }
	@Override
	 protected void onDestroy() {
		 super.onDestroy();
		 Log.d("MAIN_ACTIVITY", "onDestroy");
	 }
	
	private void addActions(){
		addAction(QBServiceConsts.EXTRA_UNEXPECTED_ERROR, new UnexpectedError());
		addAction(QBServiceConsts.DATABASE_ERROR, new DatabaseErrorCommand());
		addAction(QBServiceConsts.LOGOUT_COMPOSITE_SUCCESS_ACTION, new LogoutSuccessAction());
		addAction(QBServiceConsts.LOGOUT_COMPOSITE_FAIL_ACTION, new LogoutFailAction());
		
		//Friend Fragment
		addAction(QBServiceConsts.OPEN_GPS_SETTING, new OpenGPSSettings());
		addAction(QBServiceConsts.SEND_MESSAGE_SUCCESS_ACTION, new SendMessageSuccess());
		addAction(QBServiceConsts.SEND_MESSAGE_FAIL_ACTION, new SendMessageFail());
		addAction(QBServiceConsts.DELETE_FRIEND_SUCCESS_ACTION, new DeleteFriendSuccess());
		addAction(QBServiceConsts.DELETE_FRIEND_FAIL_ACTION, new DeleteFriendFail());
		
		addAction(QBServiceConsts.ACCEPT_FRIEND_SUCCESS_ACTION, new AcceptFriendSuccessAction());
		addAction(QBServiceConsts.ACCEPT_FRIEND_FAIL_ACTION, new AcceptFriendFailAction());
		
		addAction(QBServiceConsts.IGNORE_FRIEND_SUCCESS_ACTION, new IgnoreFriendSuccessAction());
		addAction(QBServiceConsts.IGNORE_FRIEND_FAIL_ACTION, new IgnoreFriendFailAction());
		
		Log.d("MAIN_ACTIVITY", "add actions success");
		//updateBroadcastActionList();
	}
	
	public GPSHelper getGPSHelper(){
		return this.gpsHelper;
	}
	
/*	public void onClickLayoutFriend(View view){
		FriendListActivity.start(this);
	}
	
	public void onClickLayoutNotifications(View view){
		NotificationsListActivity.start(this);
	}
	public void onClickLayoutSearch(View view){
		SearchFriendActivity.start(this);
	}
	public void onClickLayoutAccount(View view){
		AccountActivity.start(this);
	}
	public void onClickLayoutLogout(View view){
		createConfirmDialog();
	}
	
*/
	private class DatabaseErrorCommand implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			String e = bundle.getString(QBServiceConsts.EXTRA_ERROR);
			DialogUtils.showLong(MainActivity.this, e);
		}
	}
	private class LogoutSuccessAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			hideProgress();
			SplashActivity.start(MainActivity.this);
			Log.d("LOGOUT_SUCCESS", "logout_success");
			finish();
		}
	}
	private class LogoutFailAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			hideProgress();
			//DialogUtils.showLong(MainActivity.this, "Logout fail, try again. Contact : nhatuhoang91@gmail.com");
		}
	}
	interface SuccessAction extends Command{
		public void execute(Bundle bundle);
	}
	
	private class OpenGPSSettings implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			
			// TODO Auto-generated method stub
			createOpenGPSSettingDialog();
		}
	}
	
	private class SendMessageSuccess implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			DialogUtils.showLong(MainActivity.this, "Send message success");
			Time currentTime = new Time();
			currentTime.setToNow();
			String time = Utils.createTime(currentTime);
			String date = Utils.createDate(currentTime);
			
			String lat = bundle.getString(QBServiceConsts.EXTRA_LATITUDE);
			String lon = bundle.getString(QBServiceConsts.EXTRA_LONGTITUDE);
			Log.d("FRIEND_LIST_ACTIVITY", "saved lat : "+lat.toString());
			Log.d("FRIEND_LIST_ACTIVITY", "saved lon : "+lon.toString());
			String friendId = bundle.getString(QBServiceConsts.EXTRA_FRIEND_ID);
			Notification notification = new Notification();
			notification.setSenderId(friendId);
			notification.setIsSender(1); // =1:sender ; =0:reveiver
			notification.setIsRead(1); //=0:not yet; =1:read
			notification.setStatus(0); // status =0:sending; =1:sent ; =2:none
			notification.setLat(Double.valueOf(lat));
			notification.setLon(Double.valueOf(lon));
			notification.setMessage(bundle.getString(QBServiceConsts.EXTRA_MESSAGE));
			notification.setMessageId(bundle.getString(QBServiceConsts.EXTRA_MESSAGE_ID));
			notification.setTime(time);
			notification.setDate(date);
			DatabaseManager.saveNotification(MainActivity.this, notification);
		}
	}
	
	private class SendMessageFail implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			DialogUtils.showLong(MainActivity.this, "Send message fail");
		}
	}
	
	private class DeleteFriendSuccess implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			DialogUtils.showLong(MainActivity.this, "Delete friend success");
		}
	}
	private class DeleteFriendFail implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			DialogUtils.showLong(MainActivity.this, "Delete friend fail");
		}
	}
	
	private class AcceptFriendSuccessAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			Log.d("FRIEND_FRAGMENT", "accept friend success");
			//hideProgress();
			
			DialogUtils.show(MainActivity.this, "we are friend");
		}
	}
	private class AcceptFriendFailAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			Log.d("FRIEND_FRAGMENT", "accept friend fail");
			//hideProgress();
			DialogUtils.show(MainActivity.this, "Accept friend action fail may be due to network error");
		}
	}
	private class IgnoreFriendSuccessAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			Log.d("FRIEND_FRAGMENT", "ignore friend success");
			//hideProgress();
			DialogUtils.show(MainActivity.this, "Friend is ignored");
		}
	}
	private class IgnoreFriendFailAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			Log.d("FRIEND_FRAGMENT", "ignore friend fail");
			//hideProgress();
			DialogUtils.show(MainActivity.this, "Ignore friend action fail may be due to network error");
		}
	}
	
	private void createOpenGPSSettingDialog(){
		DialogInterface.OnClickListener positiveButtonListener = new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				MainActivity.this.startActivityForResult(i, FriendListActivity.LOCATION_SETTING_CALLER);
			}
		};
		
		DialogInterface.OnClickListener negativeButtonListener = new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		};
		DialogUtils.createDialog(MainActivity.this, R.string.open_gps_setting_title,
				R.string.open_gps_setting_message,positiveButtonListener, negativeButtonListener).show();
	}
	
	
	
	private void createLogoutConfirmDialog(){
		DialogInterface.OnClickListener positiveButtonListener = new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
					 QBLogoutCompositeCommand.start(MainActivity.this);
			            Log.d("MAIN_ACTIVITY", "clicked logout");
			            showProgress();
				}catch(Exception e){
		        		Log.e("FRIEND_FRAGMENT", "Error : "+e.toString());
		        	}
			}
		};
		
		DialogInterface.OnClickListener negativeButtonListener = new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
					return;
				}catch(Exception e){
		        		Log.e("FRIEND_FRAGMENT", "Error : "+e.toString());
		        	}
			}
		};
		DialogUtils.createDialog(MainActivity.this, R.string.confirm_logout,R.string.confirm_logout_warning
				,positiveButtonListener, negativeButtonListener).show();
	}
	
/*	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}*/
	/*
	@Override
	public void friendRequestChange(int count) {
		// TODO Auto-generated method stub
		if(count>0){
			if(!friendCountTextview.isShown()){
				friendCountTextview.setVisibility(TextView.VISIBLE);
			}
				friendCountTextview.setText(""+count);
		}
		else{
			if(friendCountTextview.isShown())
				friendCountTextview.setVisibility(TextView.GONE);
		}
	}
	@Override
	public void notificationChange(int count) {
		// TODO Auto-generated method stub
		if(count>0){
			if(!notificationCountTextview.isShown()){
				notificationCountTextview.setVisibility(TextView.VISIBLE);
			}
				notificationCountTextview.setText(""+count);
		}
		else{
			if(notificationCountTextview.isShown())
				notificationCountTextview.setVisibility(TextView.GONE);
		}
	}
	*/
	@Override
	public void handleLoginSuccessAction() {
		// TODO Auto-generated method stub
	}
	@Override
	public void handleLoginFailAction() {
		// TODO Auto-generated method stub
	}
	public interface HandleOnItemClick{
		public void onItemClick(long id);
	}

	private void displayAvatarImage (String uri, ImageView imageView){
		try{
		ImageLoader.getInstance().displayImage(uri, imageView, Consts.UIL_USER_AVATAR_DISPLAY_OPTIONS);
		}catch(Exception e){
			Log.e("FRIEND_LIST_CURSOR", "error : "+e.toString());
		}
	}
}
