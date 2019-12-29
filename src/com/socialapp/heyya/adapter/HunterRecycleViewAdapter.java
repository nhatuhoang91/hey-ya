package com.socialapp.heyya.adapter;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialapp.heyya.R;
import com.socialapp.heyya.base.BaseActivity;
import com.socialapp.heyya.db.DatabaseManager;
import com.socialapp.heyya.db.table.FriendTable;
import com.socialapp.heyya.db.table.NotificationTable;
import com.socialapp.heyya.ui.main.MainActivity.HandleOnItemClick;
import com.socialapp.heyya.utils.Consts;
import com.socialapp.heyya.utils.Utils;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HunterRecycleViewAdapter extends RecycleViewAdapter<HunterRecycleViewAdapter.HunterViewHolder>{

	Context mContext;
	BaseActivity baseActivity;
	String currentTime;
	Time time;
	HandleOnItemClick handleOnItemClick;
	public HunterRecycleViewAdapter(Context context, Cursor cursor, HandleOnItemClick handleOnItemClick) {
		super(context, cursor);
		// TODO Auto-generated constructor stub
		mContext = context;
		baseActivity = (BaseActivity)context;
		time = new Time();
		this.handleOnItemClick = handleOnItemClick;
	}

	public class HunterViewHolder extends RecyclerView.ViewHolder implements OnLongClickListener, OnClickListener{
		RelativeLayout relativeLayout_notification;
		ImageView imageview_avt;
		TextView textview_Name;
		TextView textview_Time;
		TextView textview_state;
		
		public HunterViewHolder(View view){
			super(view);
			relativeLayout_notification = (RelativeLayout)view.findViewById(R.id.notification_layout);
			imageview_avt = (ImageView)view.findViewById(R.id.notification_item_imageview_avt);
			textview_Name = (TextView)view.findViewById(R.id.notification_item_textview_name);
			textview_Time = (TextView)view.findViewById(R.id.notification_item_textview_time);
			textview_state = (TextView)view.findViewById(R.id.notification_item_textview_state);
			view.setOnLongClickListener(this);
			view.setOnClickListener(this);
		}

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			v.showContextMenu();
			return true;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			handleOnItemClick.onItemClick(HunterRecycleViewAdapter.this.getItemId(getAdapterPosition()));
		}
	}

	@Override
	public HunterViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		View v = LayoutInflater.from(arg0.getContext()).inflate(R.layout.notification_items, arg0,false);
		HunterViewHolder hunterViewHolder = new HunterViewHolder(v);
		return hunterViewHolder;
	}
	
	@Override
	public void onBindViewHolder(HunterViewHolder viewHolder, Cursor cursor) {
		// TODO Auto-generated method stub
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
		
		time.setToNow();
		currentTime = Utils.createDate(time) +" "+Utils.createTime(time);
		String time = cursor.getString(cursor.getColumnIndex(NotificationTable.Cols.TIME));
		String date = cursor.getString(cursor.getColumnIndex(NotificationTable.Cols.DATE));
		String diffTime = Utils.calculateDiffTime(date +" "+time, currentTime);
		if(diffTime != null){
			viewHolder.textview_Time.setText(diffTime);
		}
		
		try{
			Cursor c = DatabaseManager.getFriendByFriendId(mContext, friend_Id);
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

	private void displayAvatarImage (String uri, ImageView imageView){
		try{
		ImageLoader.getInstance().displayImage(uri, imageView, Consts.UIL_USER_AVATAR_DISPLAY_OPTIONS);
		}catch(Exception e){
			Log.e("FRIEND_LIST_CURSOR", "error : "+e.toString());
		}
	}
}
