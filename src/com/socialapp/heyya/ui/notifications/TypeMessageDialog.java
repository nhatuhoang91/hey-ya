package com.socialapp.heyya.ui.notifications;

import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.R;
import com.socialapp.heyya.db.DatabaseManager;
import com.socialapp.heyya.gcm.GPSHelper;
import com.socialapp.heyya.qb.command.SendMessageCommand;
import com.socialapp.heyya.utils.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TypeMessageDialog extends DialogFragment{

	QBUser friend;
	GPSHelper gpsHelper;
	public TypeMessageDialog (QBUser friend, GPSHelper gpsHelper){
		this.friend = friend;
		this.gpsHelper = gpsHelper;
	}
	 @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		 LayoutInflater inflater = getActivity().getLayoutInflater();
		 View v = inflater.inflate(R.layout.dialog_type_message, null);
		 final EditText editText = (EditText)v.findViewById(R.id.dialog_type_message_edittext);
		 final TextView textViewStatus = (TextView)v.findViewById(R.id.dialog_type_message_textview_status);
		 final TextView textViewFriendName = (TextView)v.findViewById(R.id.dialog_type_message_textview_friend_name);
	     final FloatingActionButton buttonCancel = (FloatingActionButton)v.findViewById(R.id.dialog_type_message_button_cancel);
	     final FloatingActionButton buttonSend = (FloatingActionButton)v.findViewById(R.id.dialog_type_message_button_send);
	     
		 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        try {
	        	StringBuilder timeStatus = new StringBuilder();
	        	final String friendId = friend.getId().toString();
	        	String friendFullname = DatabaseManager
	        			.getFriendFullNameByFriendId(getActivity(), friendId);
	        	String avtUri = DatabaseManager.getAvtUriByFriendId(getActivity(), friendId);
	        	
	        	long currentTime = System.currentTimeMillis();
	        	long userLastRequestAt = friend.getLastRequestAt().getTime();
	        	long diffTime = currentTime - userLastRequestAt;
	        	if(diffTime > 7*60*1000)
	        	{
	        		timeStatus.append(Utils.calculateDiffTime(diffTime));
	        	}else{
	        		timeStatus.append("Online");
	        	}
	        	
	        	if(!friendFullname.equals(friend.getFullName())){
	        		Log.d("TYPE_MESSAGE_DIALOG", "update fullname");
	        		DatabaseManager.updateFriendFullnameByFriendId(getActivity(), friendId, friend.getFullName());
	        	}
	        	if(friend.getCustomData()!=null){
	        		if(!avtUri.equals(friend.getCustomData())){
	        			Log.d("TYPE_MESSAGE_DIALOG", "update avt");
	        			DatabaseManager.updateAvtUriByFriendId(getActivity(), friendId, friend.getCustomData());
	        		}
	        	}
	        	
	        	textViewFriendName.setText(friend.getFullName());
	        	textViewStatus.setText(timeStatus);
				builder//.setMessage(friend.getFullName())
				.setView(v);
				buttonCancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dismiss();
					}
				});
				buttonSend.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						try{
				        	 //  String friendId = DatabaseManager.getFriendIdById(getActivity(), position);
				        	  // String friendId = App.getInstance().getPrefsHelper().getPref(PrefsHelper.PREF_USER_ID);
				        	   SendMessageCommand.start(getActivity(), friendId,
				        			   editText.getText().toString(),gpsHelper);
				        	   dismiss();
				        	}
				        	catch(Exception e){
				        		Log.e("TYPE_MESSAGE_DIALOG", "errors: "+e.toString());
				        	}
					}
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        // Create the AlertDialog object and return it
	        return builder.create();
	 }
}
