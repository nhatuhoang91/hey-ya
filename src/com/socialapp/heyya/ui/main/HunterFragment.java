package com.socialapp.heyya.ui.main;

import com.socialapp.heyya.R;
import com.socialapp.heyya.adapter.HunterRecycleViewAdapter;
import com.socialapp.heyya.customview.MyRecycleView;
import com.socialapp.heyya.customview.MyRecycleView.RecyclerContextMenuInfo;
import com.socialapp.heyya.db.DatabaseManager;
import com.socialapp.heyya.db.table.NotificationTable;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.ui.main.MainActivity.HandleOnItemClick;
import com.socialapp.heyya.ui.notifications.MapActivity;
import com.socialapp.heyya.utils.DialogUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;

public class HunterFragment extends Fragment implements LoaderCallbacks<Cursor>{
	
	private static final int LOADER_ID = 2;
	private MyRecycleView listNotifications;
	private HunterRecycleViewAdapter adapter;
	
	Context mContext;
	
	private static HunterFragment instance = null;
	public static HunterFragment getInstance(){
		if(instance == null){
			instance = new HunterFragment();
		}
		return instance;
	}
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        Log.d("HUNTER_FRAGMENT", "onCreate");
	        mContext = getActivity();
	        adapter = new HunterRecycleViewAdapter(mContext, null, new OpenMap());
			getLoaderManager().initLoader(LOADER_ID, null, this);
	    }

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	    	 Log.d("HUNTER_FRAGMENT", "onCreateView");
	        View view = inflater.inflate(R.layout.fragment_hunter, container, false);
	        
	        listNotifications = (MyRecycleView)view.findViewById(R.id.fragment_hunter_recycler_view);
	        listNotifications.setLayoutManager(new LinearLayoutManager(mContext));
	        listNotifications.setAdapter(adapter);
	        registerForContextMenu(listNotifications);
	        return view;
	    }
	    
	    @Override
		 public void onPause() {
			 super.onPause();
			 Log.d("HUNTER_FRAGMENT", "onPaused");
		 }
		@Override
		 public void onStop() {
			 super.onStop();
			 Log.d("HUNTER_FRAGMENT", "onStop");
		 }
		@Override
		 public void onDestroy() {
			 super.onDestroy();
			 Log.d("HUNTER_FRAGMENT", "onDestroy");
		 }

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			// TODO Auto-generated method stub
			MenuInflater inflater = getActivity().getMenuInflater();
		    inflater.inflate(R.menu.context_menu_notification_list, menu);
		}
		@Override
		public boolean onContextItemSelected(MenuItem item) {
		    RecyclerContextMenuInfo info = (RecyclerContextMenuInfo) item.getMenuInfo();
		    Log.d("HUNTER_FRAGMENT", String.valueOf(item.getItemId()));
		    switch (item.getItemId()) {
		        case R.id.context_menu_hunter_delete:
		        	if(info == null)
		        		Log.d("HUNTER_FRAGMENT","info null" );
		        	Log.d("HUNTER_FRAGMENT","info id:"+info.id);
		        	createConfirmDialog(info.id);
		        	return super.onContextItemSelected(item);
		        default:
		            return super.onContextItemSelected(item);
		    }
		}
		
		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
			Uri uri = NotificationTable.CONTENT_URI;
			String sortOrder = NotificationTable.Cols.ID + " ORDER BY "+NotificationTable.Cols.ID+" DESC" ;
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
		
		private class OpenMap implements HandleOnItemClick{
			@Override
			public void onItemClick(long id) {
				// TODO Auto-generated method stub
				openHunterMap(id);
			}
		}
		
		private void openHunterMap(long id){
			try{
				Cursor cursor = DatabaseManager.getNotificationById(mContext, id);
				if(!cursor.moveToFirst()){
					//Log.d("ITEM_CLICK", "cursor empty :"+cursor.moveToFirst()+", position : "+position+"; id: "+id);
					return;
				}
				String message  = cursor.getString(cursor.getColumnIndex(NotificationTable.Cols.MESSAGE));
				String lat = cursor.getString(cursor.getColumnIndex(NotificationTable.Cols.LATI));
				String lon = cursor.getString(cursor.getColumnIndex(NotificationTable.Cols.LONGTI));
				int isRead = cursor.getInt(cursor.getColumnIndex(NotificationTable.Cols.IS_READ));
				int isSender = cursor.getInt(cursor.getColumnIndex(NotificationTable.Cols.IS_SENDER));
				
				cursor.close();
				
				if(isRead == 0){
					DatabaseManager.updateIsReadNotification(mContext, id, 1);
					Log.d("NOTIFICATIONS_LIST_ACTIVITY", "is read : "+isRead);
				}
				Intent i = new Intent(mContext,MapActivity.class);
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
		
		private void createConfirmDialog(final long id){
			DialogInterface.OnClickListener positiveButtonListener = new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try{
			        	DatabaseManager.deleteNotificationById(mContext, id);
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
			DialogUtils.createDialog(mContext, R.string.confirm_delete_notification_dialog_title
					,R.string.confirm_delete_notification_dialog_message
					,positiveButtonListener, negativeButtonListener).show();
		}
}
