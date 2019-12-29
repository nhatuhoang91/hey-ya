package com.socialapp.heyya.ui.main;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
//import com.google.maps.android.SphericalUtil;
import com.socialapp.heyya.R;
import com.socialapp.heyya.adapter.FriendListCursorAdapter;
import com.socialapp.heyya.base.LoggedInActivity;
import com.socialapp.heyya.core.command.Command;
import com.socialapp.heyya.db.DatabaseManager;
import com.socialapp.heyya.db.table.FriendTable;
import com.socialapp.heyya.db.table.NotificationTable;
import com.socialapp.heyya.gcm.GPSHelper;
import com.socialapp.heyya.model.Notification;
import com.socialapp.heyya.qb.command.QBDeleteFriendCommand;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.ui.notifications.TypeMessageDialog;
import com.socialapp.heyya.utils.DialogUtils;
import com.socialapp.heyya.utils.Utils;

import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.Time;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class FriendListActivity extends LoggedInActivity implements LoaderCallbacks<Cursor>{

	public static final int LOCATION_SETTING_CALLER = 2;
	private static final int LOADER_ID = 1;
	ListView listFriend;
	FriendListCursorAdapter adapter;
	GPSHelper gpsHelper;
	public static void start(Context context){
		Intent intent = new Intent(context, FriendListActivity.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_list);
		adapter = new FriendListCursorAdapter(this, null, 0);
		getSupportLoaderManager().initLoader(LOADER_ID, null, this);
		listFriend = (ListView)findViewById(R.id.activity_friend_list_listview);
        listFriend.setAdapter(adapter);
        registerForContextMenu(listFriend);
        listFriend.setOnItemClickListener(new sendHey());
        
        Log.d("FRIEND_LIST_ACTIVITY", "on create");	
        addActions();
        actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	 protected void onStart() {
		 super.onStart();
		 Log.d("FRIEND_LIST_ACTIVITY", "onStart");
	 }
	@Override
	 protected void onResume() {
	     super.onResume();
	     gpsHelper = new GPSHelper(this);
	     Log.d("FRIEND_LIST_ACTIVITY", "onResume");
	 }
	@Override
	 protected void onPause() {
		 super.onPause();
		 Log.d("FRIEND_LIST_ACTIVITY", "onPaused");
	 }
	@Override
	 protected void onStop() {
		 super.onStop();
		 if(gpsHelper!=null)
			 gpsHelper.disconnectToGoogleServer();
		 Log.d("FRIEND_LIST_ACTIVITY", "onStop");
	 }
	@Override
	 protected void onDestroy() {
		 super.onDestroy();
		 Log.d("FRIEND_LIST_ACTIVITY", "onDestroy");
	 }
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.context_menu_friend_fragment, menu);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.context_menu_friend_delete:
	        	createConfirmDialog(info.id);
	        	return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}

	private class sendHey implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			try{
        		if(DatabaseManager.checkIsFriendById(FriendListActivity.this, id)){
        			final String friendId = DatabaseManager.getFriendIdById(FriendListActivity.this, id);
        			if(checkIsSpam(friendId, gpsHelper)){
        			Log.d("FRIEND_LIST_ACTIVITY", "spam notification");
        			DialogUtils.showLong(FriendListActivity.this, "you have sent same position in very short time. " +
        					"Please try again in few minute!");
        			}else{
        				showProgress();
        				Log.d("FRIEND_LIST_ACTIVITY", "no spam notification"); 
        				new AsyncTask<Void, Void, QBUser>() {

							@Override
							protected QBUser doInBackground(Void... params) {
								// TODO Auto-generated method stub
								try {
									QBUser user = QBUsers.getUser(Integer.valueOf(friendId));
									return user;
								} catch (NumberFormatException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (QBResponseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								return null;
							}
							
							@Override
							protected void onPostExecute(QBUser user) {
								// TODO Auto-generated method stub
								hideProgress();
								TypeMessageDialog typeMessageDialog = new TypeMessageDialog(user, gpsHelper);
					        	typeMessageDialog.show(getSupportFragmentManager(), "type message dialog");
							}
						}.execute();
        			}
        		}else{
        			Log.d("FRIEND_LIST_ACTIVITY", "not yet friend");
        		}
        	}catch(Exception e){
        		Log.d("FRIEND_LIST_ACTIVITY","error at onContextItemSelected : ");
        		e.printStackTrace();
        	}
        	
		}
		
	}
	private void addActions(){
		this.addAction(QBServiceConsts.OPEN_GPS_SETTING, new OpenGPSSettings());
		this.addAction(QBServiceConsts.SEND_MESSAGE_SUCCESS_ACTION, new SendMessageSuccess());
		this.addAction(QBServiceConsts.SEND_MESSAGE_FAIL_ACTION, new SendMessageFail());
		this.addAction(QBServiceConsts.DELETE_FRIEND_SUCCESS_ACTION, new DeleteFriendSuccess());
		this.addAction(QBServiceConsts.DELETE_FRIEND_FAIL_ACTION, new DeleteFriendFail());
	}
	
	@Override
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 
	 }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			navigateToParent();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Uri uri = FriendTable.CONTENT_URI;
		String sortOrder = FriendTable.Cols.ID +" ORDER BY " + FriendTable.Cols.FULL_NAME ;
		CursorLoader cursorLoader = new CursorLoader(this, uri,
				null, null, null, sortOrder);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		adapter.swapCursor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);	
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
			DialogUtils.showLong(FriendListActivity.this, "Send message success");
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
			DatabaseManager.saveNotification(FriendListActivity.this, notification);
		}
	}
	
	private class SendMessageFail implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			DialogUtils.showLong(FriendListActivity.this, "Send message fail");
		}
	}
	
	private class DeleteFriendSuccess implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			DialogUtils.showLong(FriendListActivity.this, "Delete friend success");
		}
	}
	private class DeleteFriendFail implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			DialogUtils.showLong(FriendListActivity.this, "Delete friend fail");
		}
	}
	
	private void createConfirmDialog(final long id){
		DialogInterface.OnClickListener positiveButtonListener = new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
		        	String friendId = DatabaseManager.getFriendIdById(FriendListActivity.this, id);
		        	QBDeleteFriendCommand.start(FriendListActivity.this, friendId);
		        	}catch(Exception e){
		        		Log.e("FRIEND_LIST_ACTIVITY", "Error : "+e.toString());
		        	}
			}
		};
		
		DialogInterface.OnClickListener negativeButtonListener = new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
					return;
				}catch(Exception e){
		        		Log.e("FRIEND_LIST_ACTIVITY", "Error : "+e.toString());
		        	}
			}
		};
		DialogUtils.createDialog(FriendListActivity.this, R.string.confirm_delete_friend_dialog_title,R.string.confirm_delete_friend_dialog_message
				,positiveButtonListener, negativeButtonListener).show();
	}
	
	private void createOpenGPSSettingDialog(){
		DialogInterface.OnClickListener positiveButtonListener = new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				FriendListActivity.this.startActivityForResult(i, FriendListActivity.LOCATION_SETTING_CALLER);
			}
		};
		
		DialogInterface.OnClickListener negativeButtonListener = new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		};
		DialogUtils.createDialog(FriendListActivity.this, R.string.open_gps_setting_title,
				R.string.open_gps_setting_message,positiveButtonListener, negativeButtonListener).show();
	}
	
	private boolean checkIsSpam(String friendId, GPSHelper gpsHelper){
		Time time = new Time();
		time.setToNow();
		String timeFormated = Utils.createDate(time);
		int hour = time.hour;
		int minute = time.minute;
		int count =0;
		if(gpsHelper.getCurrentLocation() == null)
			throw new NullPointerException();
		double lat = gpsHelper.getCurrentLocation().getLatitude();
		double lon = gpsHelper.getCurrentLocation().getLongitude();
		try{
			Cursor cursor = DatabaseManager.getNotificationToCheck(this, friendId, timeFormated);
			Log.d("FRIEND_LIST_ACTIVITY", "timeFormated : "+timeFormated);
			Log.d("FRIEND_LIST_ACTIVITY", "lat : "+lat);
			Log.d("FRIEND_LIST_ACTIVITY", "lon : "+lon);
			Log.d("FRIEND_LIST_ACTIVITY", "count to check = "+cursor.getCount());
			if(cursor.moveToFirst()){
				if(cursor.getCount()<3)
					return false;
				do{
					String timeStringToCheck = cursor.getString(cursor.getColumnIndex(NotificationTable.Cols.TIME));
					Log.d("FRIEND_LIST_ACTIVITY", "timeStringToCheck : "+timeStringToCheck);
					if(!checkTimeValid(timeStringToCheck, hour, minute)){
						String latString = cursor.getString(cursor.getColumnIndex(NotificationTable.Cols.LATI));
						String lonString = cursor.getString(cursor.getColumnIndex(NotificationTable.Cols.LONGTI));
						if(!checkDistanceValid(new LatLng(lat, lon), new LatLng(Double.valueOf(latString)
								,Double.valueOf(lonString)))){
							count++;
							if(count==3){
								return true;
							}
						}
					}
				}while(cursor.moveToNext());
			}
			return false;
		}catch(Exception e){
			Log.d("FRIEND_LIST_ACTIVITY", e.toString());
			return false;
		}
	}
	private boolean checkTimeValid(String checkTime, int hour, int minute){
		ArrayList<String> extractedCurrentTime = extractHourAndMinute(checkTime);
		if(extractedCurrentTime.get(0).equals(String.valueOf(hour))){
			if(Integer.valueOf(extractedCurrentTime.get(1))> (minute-2)){
				return false;
			}
		}
		return true;
	}
	private ArrayList<String> extractHourAndMinute(String time){
		//hour:minute:second
		ArrayList<String> result = new ArrayList<String>();
		int posColonFist=-1;
		int posColonSecond = -1;
		for(int i=0;i<time.length();i++){
			if(time.charAt(i)==':'){
				posColonFist = i;
				result.add(time.substring(0, posColonFist));
				Log.d("EXTRACT_HOUR_MINUTE", "hour = "+time.substring(0, posColonFist));
				break;
			}
		}
		
		for(int i=posColonFist+1;i<time.length();i++){
			if(time.charAt(i)==':'){
				posColonSecond = i;
				result.add(time.substring(posColonFist+1, posColonSecond));
				Log.d("EXTRACT_HOUR_MINUTE", "minute = "+time.substring(posColonFist+1, posColonSecond));
				break;
			}
		}
		
		return result;
	}
	private boolean checkDistanceValid(LatLng p1, LatLng p2){
		/*double result = SphericalUtil.computeDistanceBetween(p1, p2);
		Log.d("FRIEND_LIST_ACTIVITY", "Distance : "+result);
		if(result < 10){
			return false;
		}else
			return true;
			*/
		return true;
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
