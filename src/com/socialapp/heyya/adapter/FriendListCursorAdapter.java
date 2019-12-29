package com.socialapp.heyya.adapter;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.R;
import com.socialapp.heyya.base.BaseActivity;
import com.socialapp.heyya.core.command.Command;
import com.socialapp.heyya.db.table.FriendTable;
import com.socialapp.heyya.qb.command.QBAcceptFriendCommand;
import com.socialapp.heyya.qb.command.QBIgnoreFriendCommand;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.utils.Consts;
import com.socialapp.heyya.utils.DialogUtils;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendListCursorAdapter extends CursorAdapter{
	final Context mContext;
	BaseActivity baseActivity;
	QBUser user;
	LayoutInflater layoutInflater = null;
	
	
	public FriendListCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		mContext = context;
		baseActivity = (BaseActivity)context;
		layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		addActions();
		Log.d("FRIEND_LIST_ON_CREATE", "add actions in friend list");
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder viewHolder = (ViewHolder)view.getTag();
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

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		View view = layoutInflater.inflate(R.layout.listview_item_friend, arg2, false);
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
	private void onClickAcceptFriend(String friendId){
		QBAcceptFriendCommand.start(baseActivity, friendId);
	}
	private void onClickIgnoreFriend(String friendId){
		QBIgnoreFriendCommand.start(baseActivity, friendId);
	}
	
	private void addActions(){
		baseActivity.addAction(QBServiceConsts.ACCEPT_FRIEND_SUCCESS_ACTION, new AcceptFriendSuccessAction());
		baseActivity.addAction(QBServiceConsts.ACCEPT_FRIEND_FAIL_ACTION, new AcceptFriendFailAction());
		
		baseActivity.addAction(QBServiceConsts.IGNORE_FRIEND_SUCCESS_ACTION, new IgnoreFriendSuccessAction());
		baseActivity.addAction(QBServiceConsts.IGNORE_FRIEND_FAIL_ACTION, new IgnoreFriendFailAction());
		//baseActivity.updateBroadcastActionList();
	}
	private class AcceptFriendSuccessAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			Log.d("FRIEND_FRAGMENT", "accept friend success");
			baseActivity.hideProgress();
			//if(fragment_Friend_layout2.isShown()){
				//fragment_Friend_layout2.setVisibility(LinearLayout.GONE);
			//}
			//if(button_Accept.isShown()){
				//button_Accept.setVisibility(Button.GONE);
		//	}
			//if(button_Ignore.isShown()){
				//button_Ignore.setVisibility(Button.GONE);
			//}
			DialogUtils.show(baseActivity, "we are friend");
		}
	}
	private class AcceptFriendFailAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			Log.d("FRIEND_FRAGMENT", "accept friend fail");
			baseActivity.hideProgress();
			DialogUtils.show(baseActivity, "Accept friend action fail may be due to network error");
		}
	}
	private class IgnoreFriendSuccessAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			Log.d("FRIEND_FRAGMENT", "ignore friend success");
			//baseActivity.hideProgress();
		//	if(button_Accept.isShown()){
			//	button_Accept.setVisibility(Button.GONE);
			//}
		//	if(button_Ignore.isShown()){
			//	button_Ignore.setVisibility(Button.GONE);
			//}
			DialogUtils.show(baseActivity, "Friend is ignored");
		}
	}
	private class IgnoreFriendFailAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			Log.d("FRIEND_FRAGMENT", "ignore friend fail");
			//baseActivity.hideProgress();
			DialogUtils.show(baseActivity, "Ignore friend action fail may be due to network error");
		}
	}
	
	private class ViewHolder{
		TextView textview_Friend_Name;
		ImageView imageview_Avt;
		TextView textview_Request_Sent;
		//LinearLayout fragment_Friend_layout2;
		Button button_Accept;
		Button button_Ignore;
		public ViewHolder(View view){
			textview_Friend_Name = (TextView)view.findViewById(R.id.friend_item_textview_friend_name);
			textview_Request_Sent = (TextView)view.findViewById(R.id.friend_item_textview_sent_requested);
			
			imageview_Avt = (ImageView)view.findViewById(R.id.friend_item_imageview_avt);
			
			//fragment_Friend_layout2 = (LinearLayout)view.findViewById(R.id.fragment_friend_layout2);
			button_Accept = (Button)view.findViewById(R.id.friend_item_button_accept);
			
			button_Ignore = (Button)view.findViewById(R.id.friend_item_button_ignore);
		}
	}
}
