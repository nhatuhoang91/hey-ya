package com.socialapp.heyya.ui.notifications;

import com.socialapp.heyya.R;
import com.socialapp.heyya.adapter.NotificationsListCursorAdapter;
import com.socialapp.heyya.base.LoggedInActivity;
import com.socialapp.heyya.db.DatabaseManager;
import com.socialapp.heyya.db.table.NotificationTable;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.utils.DialogUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

public class NotificationsListActivity extends LoggedInActivity implements LoaderCallbacks<Cursor>{

	private static final int LOADER_ID = 2;
	private ListView listNotifications;
	private NotificationsListCursorAdapter adapter;
	
	public static void start(Context context){
		Intent intent = new Intent(context, NotificationsListActivity.class);
		context.startActivity(intent);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notifications_list);
		adapter = new NotificationsListCursorAdapter(this, null, 0);
		getSupportLoaderManager().initLoader(LOADER_ID, null, this);
		listNotifications = (ListView)findViewById(R.id.activity_notifications_listview);
		listNotifications.setAdapter(adapter);
		registerForContextMenu(listNotifications);
		listNotifications.setOnItemClickListener(new itemClickListener());
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		 Log.d("NOTIFICATIONS_LIST_ACTIVITY", "on create");	
	}

	@Override
	 protected void onStart() {
		 super.onStart();
		 Log.d("NOTIFICATIONS_LIST_ACTIVITY", "onStart");
	 }
	@Override
	 protected void onResume() {
	     super.onResume();
	     Log.d("NOTIFICATIONS_LIST_ACTIVITY", "onResume");
	 }
	@Override
	 protected void onPause() {
		 super.onPause();
		 Log.d("NOTIFICATIONS_LIST_ACTIVITY", "onPaused");
	 }
	@Override
	 protected void onStop() {
		 super.onStop();
		 Log.d("NOTIFICATIONS_LIST_ACTIVITY", "onStop");
	 }
	@Override
	 protected void onDestroy() {
		 super.onDestroy();
		 Log.d("NOTIFICATION_LIST_ACTIVITY", "onDestroy");
	 }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.context_menu_notification_list, menu);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
		Uri uri = NotificationTable.CONTENT_URI;
		String sortOrder = NotificationTable.Cols.ID + " ORDER BY "+NotificationTable.Cols.ID+" DESC" ;
		CursorLoader cursorLoader = new CursorLoader(this, uri,
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
	
	private class itemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			try{
				Cursor cursor = DatabaseManager.getNotificationById(NotificationsListActivity.this, id);
				if(!cursor.moveToFirst()){
					Log.d("ITEM_CLICK", "cursor empty :"+cursor.moveToFirst()+", position : "+position+"; id: "+id);
					return;
				}
				String message  = cursor.getString(cursor.getColumnIndex(NotificationTable.Cols.MESSAGE));
				String lat = cursor.getString(cursor.getColumnIndex(NotificationTable.Cols.LATI));
				String lon = cursor.getString(cursor.getColumnIndex(NotificationTable.Cols.LONGTI));
				int isRead = cursor.getInt(cursor.getColumnIndex(NotificationTable.Cols.IS_READ));
				int isSender = cursor.getInt(cursor.getColumnIndex(NotificationTable.Cols.IS_SENDER));
				
				cursor.close();
				
				if(isRead == 0){
					DatabaseManager.updateIsReadNotification(NotificationsListActivity.this, id, 1);
					Log.d("NOTIFICATIONS_LIST_ACTIVITY", "is read : "+isRead);
				}
				Intent i = new Intent(NotificationsListActivity.this,MapActivity.class);
				i.putExtra(QBServiceConsts.EXTRA_MESSAGE,message);
				i.putExtra(QBServiceConsts.EXTRA_LATITUDE, Double.valueOf(lat));
				i.putExtra(QBServiceConsts.EXTRA_LONGTITUDE, Double.valueOf(lon));
				if(isSender == 1)
					i.putExtra(QBServiceConsts.EXTRA_IS_RECEIVE, false);
				else
					i.putExtra(QBServiceConsts.EXTRA_IS_RECEIVE, true);
				Log.d("ITEM_CLICK", "cursor ok");
				startActivity(i);
			}catch(Exception e){
				Log.e("NOTIFICATION_LIST_ACTIVITY", "Error: "+e.toString());
			}
		}
	}
	
	private void createConfirmDialog(final long id){
		DialogInterface.OnClickListener positiveButtonListener = new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
		        	DatabaseManager.deleteNotificationById(NotificationsListActivity.this, id);
		        	}catch(Exception e){
		        		Log.e("NOTIFICATION_LIST_ACTIVITY", "Error : "+e.toString());
		        	}
			}
		};
		
		DialogInterface.OnClickListener negativeButtonListener = new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
					return;
				}catch(Exception e){
		        		Log.e("NOTIFICATION_LIST_ACTIVITY", "Error : "+e.toString());
		        	}
			}
		};
		DialogUtils.createDialog(NotificationsListActivity.this, R.string.confirm_delete_notification_dialog_title
				,R.string.confirm_delete_notification_dialog_message
				,positiveButtonListener, negativeButtonListener).show();
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
