package com.socialapp.heyya.adapter;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialapp.heyya.R;
import com.socialapp.heyya.db.DatabaseManager;
import com.socialapp.heyya.db.table.FriendTable;
import com.socialapp.heyya.db.table.NotificationTable;
import com.socialapp.heyya.utils.Consts;
import com.socialapp.heyya.utils.Utils;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NotificationsListCursorAdapter extends CursorAdapter{

	LayoutInflater layoutInflater = null;
	String currentTime;
	Time time;
	public NotificationsListCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		time = new Time();
		
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder viewHolder = (ViewHolder)view.getTag();
		
		int isRead = cursor.getInt(cursor.getColumnIndex(NotificationTable.Cols.IS_READ));
		if(isRead == 1){
			viewHolder.relativeLayout_notification.setBackgroundResource(R.drawable.notification_item_background);
		}else{
			viewHolder.relativeLayout_notification.setBackgroundResource(R.drawable.notification_not_read_background);
		}
		
		String friend_Id = cursor.getString(cursor.getColumnIndex(NotificationTable.Cols.SENDER_ID));
		
		int state = cursor.getInt(cursor.getColumnIndex(NotificationTable.Cols.STATUS));
		if(state == 0){
			if(!viewHolder.textview_state.isShown())
				viewHolder.textview_state.setVisibility(TextView.VISIBLE);
			viewHolder.textview_state.setText("Sending");
		}else{
			if(state == 1){
				if(!viewHolder.textview_state.isShown())
					viewHolder.textview_state.setVisibility(TextView.VISIBLE);
				viewHolder.textview_state.setText("Sent");
			}
			else
				viewHolder.textview_state.setVisibility(TextView.GONE);
		}
		int isSender = cursor.getInt(cursor.getColumnIndex(NotificationTable.Cols.IS_SENDER));
		if(isSender == 1){
			viewHolder.imageview_Direction.setImageResource(R.drawable.send_icon);
		}else{
			viewHolder.imageview_Direction.setImageResource(R.drawable.receive_icon);
		}
		
		time.setToNow();
		currentTime = Utils.createDate(time) +" "+Utils.createTime(time);
		String time = cursor.getString(cursor.getColumnIndex(NotificationTable.Cols.TIME));
		String date = cursor.getString(cursor.getColumnIndex(NotificationTable.Cols.DATE));
		String diffTime = Utils.calculateDiffTime(date +" "+time, currentTime);
		if(diffTime != null){
			viewHolder.textview_Time.setText(diffTime);
		}
		
		try{
			Cursor c = DatabaseManager.getFriendByFriendId(context, friend_Id);
			if(c.moveToFirst()){
				String avt = c.getString(c.getColumnIndex(FriendTable.Cols.AVATAR_URL));
				String fullName = c.getString(c.getColumnIndex(FriendTable.Cols.FULL_NAME));
				viewHolder.textview_Name.setText(fullName);
				displayAvatarImage(avt, viewHolder.imageview_avt);
			}
			if(!c.isClosed()){
				c.close();
			}
		}catch(Exception e){
			Log.e("NOTIFICATION_ADAPTER", "error: "+e.toString());
		}
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		Log.d("FRIEND_LIST_CURSOR", "new view");
		View view =(View)layoutInflater.inflate(R.layout.notification_items, arg2, false);
		ViewHolder viewHolder = new ViewHolder(view);
		view.setTag(viewHolder);
		return view;
	}
	
	private void displayAvatarImage (String uri, ImageView imageView){
		try{
		ImageLoader.getInstance().displayImage(uri, imageView, Consts.UIL_USER_AVATAR_DISPLAY_OPTIONS);
		}catch(Exception e){
			Log.e("FRIEND_LIST_CURSOR", "error : "+e.toString());
		}
	}
	
	private class ViewHolder{
		RelativeLayout relativeLayout_notification;
		ImageView imageview_avt;
		TextView textview_Name;
		ImageView imageview_Direction;
		TextView textview_Time;
		TextView textview_state;
		
		public ViewHolder(View view){
			relativeLayout_notification = (RelativeLayout)view.findViewById(R.id.notification_layout);
			imageview_avt = (ImageView)view.findViewById(R.id.notification_item_imageview_avt);
			textview_Name = (TextView)view.findViewById(R.id.notification_item_textview_name);
			textview_Time = (TextView)view.findViewById(R.id.notification_item_textview_time);
			textview_state = (TextView)view.findViewById(R.id.notification_item_textview_state);
		}
	}
}
