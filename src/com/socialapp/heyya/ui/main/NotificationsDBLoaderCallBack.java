package com.socialapp.heyya.ui.main;

import com.socialapp.heyya.db.table.FriendTable;
import com.socialapp.heyya.db.table.NotificationTable;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

public class NotificationsDBLoaderCallBack implements LoaderCallbacks<Cursor>{

	private static final String TAG = NotificationsDBLoaderCallBack.class.getSimpleName();
	public static final int LOADER_ID = 3;
	private Context context;
	private HandleDatabaseChanges handleDatabaseChanges;
	
	public NotificationsDBLoaderCallBack(Context context){
		this.context = context;
		this.handleDatabaseChanges = (HandleDatabaseChanges)context;
	}
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onCreateLoader");
		Uri uri = NotificationTable.CONTENT_URI;
		String selection = NotificationTable.Cols.IS_READ+" = '"+"0'";
		Log.d(TAG, "selection :"+selection);
		CursorLoader cursorLoader = new CursorLoader(context, uri,
				null, selection, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		int count = arg1.getCount();
		Log.d(TAG, "onLoadFinish . count = "+count);
		handleDatabaseChanges.notificationChange(count);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}

}
