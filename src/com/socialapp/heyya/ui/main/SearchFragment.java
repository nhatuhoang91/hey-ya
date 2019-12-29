package com.socialapp.heyya.ui.main;

import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.R;
import com.socialapp.heyya.base.BaseActivity;
import com.socialapp.heyya.core.command.Command;
import com.socialapp.heyya.qb.command.QBSearchFriendCommand;
import com.socialapp.heyya.qb.command.QBSendFriendRequest;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.utils.DialogUtils;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchFragment extends Fragment{
	
	private QBUser user;
	//element of layout 1
	EditText edittext_Search;
	FloatingActionButton button_Search;
	//element of layout 2
	RelativeLayout activity_search_friend_layout2;
	ImageView image_friend;
	ImageView image_addfriend;
	TextView textView_name;
	TextView textView_Was_Asked;
	
	Context mContext;
	private static SearchFragment instance =null;
	public static SearchFragment getInstance(){
		if(instance == null){
			instance = new SearchFragment();
		}
		
		return instance;
	}
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        Log.d("SEARCH_FRAGMENT", "onCreate");
	        mContext = getActivity();
	        if(mContext!=null)
	        	addActions();
	    }

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	    	Log.d("SEARCH_FRAGMENT", "onCreateView");
	        View view = inflater.inflate(R.layout.fragment_search, container, false);
	        activity_search_friend_layout2 = (RelativeLayout)view.findViewById(R.id.activity_search_friend_layout2);
	        image_friend = (ImageView)view.findViewById(R.id.activity_search_friend_imageview_avt);
	        image_addfriend = (ImageView)view.findViewById(R.id.activity_search_friend_image_add);
	        image_addfriend.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(user != null)
						QBSendFriendRequest.start(mContext, user);
				}
			});
	        textView_name = (TextView)view.findViewById(R.id.activity_search_friend_textview_name);
	        
	        //set UI layout 1
	        edittext_Search = (EditText)view.findViewById(R.id.activity_search_friend_edittext);
	        button_Search = (FloatingActionButton)view.findViewById(R.id.activity_search_friend_button_search);
	        button_Search.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onClickSearch();
				}
			});
			
	        return view;
	    }
	    @Override
		 public void onPause() {
			 super.onPause();
			 Log.d("SEARCH_FRAGMENT", "onPaused");
		 }
		@Override
		 public void onStop() {
			 super.onStop();
			 //if(gpsHelper!=null)
				// gpsHelper.disconnectToGoogleServer();
			 Log.d("SEARCH_FRAGMENT", "onStop");
		 }
		@Override
		 public void onDestroy() {
			 super.onDestroy();
			 Log.d("SEARCH_FRAGMENT", "onDestroy");
		 }
		
		private void addActions(){
			((BaseActivity)mContext).addAction(QBServiceConsts.SEARCH_FRIEND_SUCCESS_ACTION, new SearchFriendSuccessAction());
			((BaseActivity)mContext).addAction(QBServiceConsts.SEARCH_FRIEND_FAIL_ACTION, new SearchFriendFailAction());
			((BaseActivity)mContext).addAction(QBServiceConsts.SEND_FRIEND_REQUEST_SUCCESS_ACTION, new SendFriendRequestSuccessAction());
			((BaseActivity)mContext).addAction(QBServiceConsts.SEND_FRIEND_REQUEST_FAIL_ACTION, new SendFriendRequestFailAction());
			((BaseActivity)mContext).updateBroadcastActionList();
			Log.d("SEARCH_FRIEND_ACTIVITY", "add actions success");
		}
		
		private void onClickSearch(){
			if(edittext_Search.getText().toString().trim().isEmpty())
				return;
			QBSearchFriendCommand.start(mContext, edittext_Search.getText().toString().trim());
			((BaseActivity)mContext).hideKeyBoard(edittext_Search);
		}
		
		private class SearchFriendSuccessAction implements Command{
			@Override
			public void execute(Bundle bundle) throws Exception {
				user = (QBUser)bundle.get(QBServiceConsts.EXTRA_USER);
				boolean isInvite = bundle.getBoolean(QBServiceConsts.IS_INVITE);
				activity_search_friend_layout2.setVisibility(RelativeLayout.VISIBLE);
				textView_name.setText(user.getLogin());
				if(!isInvite){
					image_addfriend.setVisibility(ImageView.VISIBLE);
				}else
					image_addfriend.setVisibility(ImageView.GONE);
			}
		}
		private class SearchFriendFailAction implements Command{

			@Override
			public void execute(Bundle bundle) throws Exception {
				if(activity_search_friend_layout2.isShown()){
					activity_search_friend_layout2.setVisibility(RelativeLayout.GONE);
				}
				//DialogUtils.show(baseActivity, "The user "+edittext_Search.getText().toString()+" not exist");
				//Exception e = (Exception) bundle.get(QBServiceConsts.EXTRA_ERROR);
				DialogUtils.show(mContext, "User was not found");
			}
		}
		private class SendFriendRequestSuccessAction implements Command{

			@Override
			public void execute(Bundle bundle) throws Exception {
				// TODO Auto-generated method stub
				if(image_addfriend.isShown())
					image_addfriend.setVisibility(ImageView.GONE);
				DialogUtils.show(mContext, "Request is sent");
			}
			
		}
		private class SendFriendRequestFailAction implements Command{

			@Override
			public void execute(Bundle bundle) throws Exception {
				// TODO Auto-generated method stub
				Exception e = (Exception) bundle.get(QBServiceConsts.EXTRA_ERROR);
				//DialogUtils.show(baseActivity, "Request sent fail. error:"+e.toString());
			}
		}
}
