package com.socialapp.heyya.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialapp.heyya.R;
import com.socialapp.heyya.base.BaseActivity;
import com.socialapp.heyya.db.table.FriendTable;
import com.socialapp.heyya.qb.command.QBAcceptFriendCommand;
import com.socialapp.heyya.qb.command.QBIgnoreFriendCommand;
import com.socialapp.heyya.ui.main.MainActivity.HandleOnItemClick;
import com.socialapp.heyya.utils.Consts;

public class FriendRecycleViewAdapter extends RecycleViewAdapter<FriendRecycleViewAdapter.FriendViewHolder>{

	Context mContext;
	BaseActivity baseActivity;
	HandleOnItemClick handleOnItemClick;
	
	public FriendRecycleViewAdapter(Context context, Cursor cursor, HandleOnItemClick handleOnItemClick) {
		super(context, cursor);
		mContext = context;
		baseActivity = (BaseActivity)context;
		this.handleOnItemClick = handleOnItemClick; 
	}

	public class FriendViewHolder extends RecyclerView.ViewHolder implements OnLongClickListener, OnClickListener{
		protected TextView textview_Friend_Name;
		protected ImageView imageview_Avt;
		protected TextView textview_Request_Sent;
		//LinearLayout fragment_Friend_layout2;
		protected FloatingActionButton button_Accept;
		protected FloatingActionButton button_Ignore;
		
		public FriendViewHolder(View view){
			super(view);
			textview_Friend_Name = (TextView)view.findViewById(R.id.friend_item_textview_friend_name);
			textview_Request_Sent = (TextView)view.findViewById(R.id.friend_item_textview_sent_requested);
			
			imageview_Avt = (ImageView)view.findViewById(R.id.friend_item_imageview_avt);
			
			button_Accept = (FloatingActionButton)view.findViewById(R.id.friend_item_button_accept);
			
			button_Ignore = (FloatingActionButton)view.findViewById(R.id.friend_item_button_ignore);
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
			handleOnItemClick.onItemClick(FriendRecycleViewAdapter.this.getItemId(getAdapterPosition()));
		}
	}

	@Override
	public FriendViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		View v = LayoutInflater.from(arg0.getContext()).inflate(R.layout.listview_item_friend, arg0,false);
		FriendViewHolder friendViewHolder = new FriendViewHolder(v);
		return friendViewHolder;
	}

	@Override
	public void onBindViewHolder(FriendViewHolder viewHolder, Cursor cursor) {
		// TODO Auto-generated method stub
		final String friendId = cursor.getString(cursor.getColumnIndex(FriendTable.Cols.FRIEND_ID));
		
		String avt_Uri = cursor.getString(cursor.getColumnIndex(FriendTable.Cols.AVATAR_URL));
		String friend_name = cursor.getString(cursor.getColumnIndex(FriendTable.Cols.FULL_NAME));
		int is_Requested_friend = cursor.getInt(cursor.getColumnIndex(FriendTable.Cols.IS_REQUESTED_FRIEND));
		int is_Asked = cursor.getInt(cursor.getColumnIndex(FriendTable.Cols.IS_STATUS_ASK));
		
		viewHolder.button_Accept.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//baseActivity.showProgress();
				onClickAcceptFriend(friendId);
			}
		});
		
		viewHolder.button_Ignore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//baseActivity.showProgress();
				onClickIgnoreFriend(friendId);
			}
		});

		if(friend_name != null){
			viewHolder.textview_Friend_Name.setText(friend_name);
		}
		//if this friend who we sent request
		if(is_Requested_friend == 1){
			viewHolder.textview_Request_Sent.setVisibility(TextView.VISIBLE);
		}else{
			viewHolder.textview_Request_Sent.setVisibility(TextView.GONE);
		}

		if(is_Asked == 1){
			//fragment_Friend_layout2.setVisibility(LinearLayout.VISIBLE);
			viewHolder.button_Accept.setVisibility(Button.VISIBLE);
			viewHolder.button_Ignore.setVisibility(Button.VISIBLE);
		}else{
			//fragment_Friend_layout2.setVisibility(LinearLayout.GONE);
			viewHolder.button_Accept.setVisibility(Button.GONE);
			viewHolder.button_Ignore.setVisibility(Button.GONE);
		}
		
		if(avt_Uri != null){
			displayAvatarImage(avt_Uri, viewHolder.imageview_Avt);
		}
	}
	
	private void displayAvatarImage (String uri, ImageView imageView){
		try{
		ImageLoader.getInstance().displayImage(uri, imageView, Consts.UIL_USER_AVATAR_DISPLAY_OPTIONS);
		}catch(Exception e){
			Log.e("FRIEND_LIST_CURSOR", "error : "+e.toString());
		}
	}
	private void onClickAcceptFriend(String friendId){
		QBAcceptFriendCommand.start(mContext, friendId);
	}
	private void onClickIgnoreFriend(String friendId){
		QBIgnoreFriendCommand.start(mContext, friendId);
	}
}
