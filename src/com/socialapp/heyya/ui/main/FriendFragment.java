package com.socialapp.heyya.ui.main;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.R;
import com.socialapp.heyya.adapter.FriendListCursorAdapter;
import com.socialapp.heyya.adapter.FriendRecycleViewAdapter;
import com.socialapp.heyya.base.BaseActivity;
import com.socialapp.heyya.core.command.Command;
import com.socialapp.heyya.customview.MyRecycleView;
import com.socialapp.heyya.customview.MyRecycleView.RecyclerContextMenuInfo;
import com.socialapp.heyya.db.DatabaseManager;
import com.socialapp.heyya.db.table.FriendTable;
import com.socialapp.heyya.db.table.NotificationTable;
import com.socialapp.heyya.gcm.GPSHelper;
import com.socialapp.heyya.model.Notification;
import com.socialapp.heyya.qb.command.QBDeleteFriendCommand;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.ui.main.MainActivity.HandleOnItemClick;
import com.socialapp.heyya.ui.notifications.TypeMessageDialog;
import com.socialapp.heyya.utils.DialogUtils;
import com.socialapp.heyya.utils.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class FriendFragment extends Fragment implements LoaderCallbacks<Cursor>{

	public static final int LOCATION_SETTING_CALLER = 2;
	private static final int LOADER_ID = 1;
	MyRecycleView listFriend;
	FriendRecycleViewAdapter adapter;
	Context mContext;
	GPSHelper gpsHelper;
	private static FriendFragment instance = null;
	public static FriendFragment getInstance(){
		if(instance == null){
			instance = new FriendFragment();
		}
		return instance;
	}
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        Log.d("FRIEND_FRAGMENT", "onCreate");
	        mContext = getActivity();
	        gpsHelper = new GPSHelper(mContext);
	        adapter = new FriendRecycleViewAdapter(mContext, null, new SendHeyMessage());
	        getLoaderManager().initLoader(LOADER_ID, null, this);
	    }

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	    	Log.d("FRIEND_FRAGMENT", "onCreateView");
	        View view = inflater.inflate(R.layout.fragment_friend, container, false);

	        listFriend = (MyRecycleView)view.findViewById(R.id.fragment_friend_recycler_view);
	        listFriend.setLayoutManager(new LinearLayoutManager(mContext));
	        listFriend.setAdapter(adapter);
	        registerForContextMenu(listFriend);
	        return view;
	    }

	    @Override
		 public void onPause() {
			 super.onPause();
			 Log.d("FRIEND_FRAGMENT", "onPaused");
		 }
		@Override
		 public void onStop() {
			 super.onStop();
			 //if(gpsHelper!=null)
				// gpsHelper.disconnectToGoogleServer();
			 Log.d("FRIEND_FRAGMENT", "onStop");
		 }
		@Override
		 public void onDestroy() {
			 super.onDestroy();
			 Log.d("FRIEND_FRAGMENT", "onDestroy");
			 if(gpsHelper!=null)
				 gpsHelper.disconnectToGoogleServer();
		 }
		
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			// TODO Auto-generated method stub
			MenuInflater inflater = getActivity().getMenuInflater();
		    inflater.inflate(R.menu.context_menu_friend_fragment, menu);
		}
		@Override
		public boolean onContextItemSelected(MenuItem item) {
		    RecyclerContextMenuInfo info = (RecyclerContextMenuInfo) item.getMenuInfo();
		    Log.d("FRIEND_FRAGMENT", String.valueOf(item.getItemId()));
		    switch (item.getItemId()) {
		        case R.id.context_menu_friend_delete:
		        	if(info == null)
		        	Log.d("FRIEND_FRAGMENT","info null" );
		        	Log.d("FRIEND_FRAGMENT","info id:"+info.id);
		        	createConfirmDialog(info.id);
		        	return super.onContextItemSelected(item);
		        default:
		            return super.onContextItemSelected(item);
		    }
		}
	   
		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
			// TODO Auto-generated method stub
			Uri uri = FriendTable.CONTENT_URI;
			String sortOrder = FriendTable.Cols.ID +" ORDER BY " + FriendTable.Cols.FULL_NAME ;
			CursorLoader cursorLoader = new CursorLoader(mContext, uri,
					null, null, null, sortOrder);
			return cursorLoader;
		}
		@Override
		public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
			// TODO Auto-generated method stub
			adapter.swapCursor(arg1);
		}
		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			// TODO Auto-generated method stub
			adapter.swapCursor(null);	
		}
		private class SendHeyMessage implements HandleOnItemClick{

			@Override
			public void onItemClick(long id) {
				// TODO Auto-generated method stub
				Log.d("FRIEND_FRAGMENT", "id == "+id); 
				sendHey(id);
			}
			
		}
		
		private void sendHey (long id){
				// TODO Auto-generated method stub
				try{
	        		if(DatabaseManager.checkIsFriendById(mContext, id)){
	        			final String friendId = DatabaseManager.getFriendIdById(mContext, id);
	        			if(checkIsSpam(friendId, gpsHelper)){
	        			Log.d("FRIEND_FRAGMENT", "spam notification");
	        			DialogUtils.showLong(getActivity(), "you have sent same position in very short time. " +
	        					"Please try again in few minute!");
	        			}else{
	        				((BaseActivity) mContext).showProgress();
	        				Log.d("FRIEND_FRAGMENT", "no spam notification"); 
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
									((BaseActivity) mContext).hideProgress();
									TypeMessageDialog typeMessageDialog = new TypeMessageDialog(user, gpsHelper);
						        	typeMessageDialog.show(getFragmentManager(), "type message dialog");
								}
							}.execute();
	        			}
	        		}else{
	        			Log.d("FRIEND_FRAGMENT", "not yet friend");
	        		}
	        	}catch(Exception e){
	        		Log.d("FRIEND_FRAGMENT","error at onContextItemSelected : ");
	        		e.printStackTrace();
	        	}
		}
		
		
		private void createConfirmDialog(final long id){
			DialogInterface.OnClickListener positiveButtonListener = new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try{
			        	String friendId = DatabaseManager.getFriendIdById(mContext, id);
			        	QBDeleteFriendCommand.start(mContext, friendId);
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
			DialogUtils.createDialog(getActivity(), R.string.confirm_delete_friend_dialog_title,R.string.confirm_delete_friend_dialog_message
					,positiveButtonListener, negativeButtonListener).show();
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
				Cursor cursor = DatabaseManager.getNotificationToCheck(mContext, friendId, timeFormated);
				Log.d("FRIEND_FRAGMENT", "timeFormated : "+timeFormated);
				Log.d("FRIEND_FRAGMENT", "lat : "+lat);
				Log.d("FRIEND_FRAGMENT", "lon : "+lon);
				Log.d("FRIEND_FRAGMENT", "count to check = "+cursor.getCount());
				if(cursor.moveToFirst()){
					if(cursor.getCount()<3)
						return false;
					do{
						String timeStringToCheck = cursor.getString(cursor.getColumnIndex(NotificationTable.Cols.TIME));
						Log.d("FRIEND_FRAGMENT", "timeStringToCheck : "+timeStringToCheck);
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
				Log.d("FRIEND_FRAGMENT", e.toString());
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
			double result = SphericalUtil.computeDistanceBetween(p1, p2);
			Log.d("FRIEND_FRAGMENT", "Distance : "+result);
			if(result < 10){
				return false;
			}else
				return true;
				
		}

}
