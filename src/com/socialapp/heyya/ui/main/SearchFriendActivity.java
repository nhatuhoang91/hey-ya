package com.socialapp.heyya.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.R;
import com.socialapp.heyya.base.BaseActivity;
import com.socialapp.heyya.base.LoggedInActivity;
import com.socialapp.heyya.core.command.Command;
import com.socialapp.heyya.qb.command.QBSearchFriendCommand;
import com.socialapp.heyya.qb.command.QBSendFriendRequest;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.utils.DialogUtils;

public class SearchFriendActivity extends LoggedInActivity{

	private QBUser user;
	//element of layout 1
	EditText edittext_Search;
	Button button_Search;
	//element of layout 2
	RelativeLayout activity_search_friend_layout2;
	ImageView image_friend;
	ImageView image_addfriend;
	TextView textView_name;
	TextView textView_Was_Asked;
	Button button_Accept;
	Button button_Ignore;
		
	public static void start(Context context){
		Intent intent = new Intent(context, SearchFriendActivity.class);
		context.startActivity(intent);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_friend);
		Log.d("SEARCH_FRIEND_ACTIVITY", "on create");
		
		//set UI layout2
        activity_search_friend_layout2 = (RelativeLayout)findViewById(R.id.activity_search_friend_layout2);
        image_friend = (ImageView)findViewById(R.id.activity_search_friend_imageview_avt);
        image_addfriend = (ImageView)findViewById(R.id.activity_search_friend_image_add);
        image_addfriend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(user != null)
					QBSendFriendRequest.start(SearchFriendActivity.this, user);
			}
		});
        textView_name = (TextView)findViewById(R.id.activity_search_friend_textview_name);
        
        //set UI layout 1
        edittext_Search = (EditText)findViewById(R.id.activity_search_friend_edittext);
        button_Search = (Button)findViewById(R.id.activity_search_friend_button_search);
        button_Search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onClickSearch();
			}
		});
		
		addActions();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	 protected void onStart() {
		 super.onStart();
		 Log.d("SEARCH_FRIEND_ACTIVITY", "onStart");
	 }
	@Override
	 protected void onResume() {
	     super.onResume();
	     Log.d("SEARCH_FRIEND_ACTIVITY", "onResume");
	 }
	@Override
	 protected void onPause() {
		 super.onPause();
		 Log.d("SEARCH_FRIEND_ACTIVITY", "onPaused");
	 }
	@Override
	 protected void onStop() {
		 super.onStop();
		 Log.d("SEARCH_FRIEND_ACTIVITY", "onStop");
	 }
	@Override
	 protected void onDestroy() {
		 super.onDestroy();
		 Log.d("SEARCH_FRIEND_ACTIVITY", "onDestroy");
	 }
	
	private void onClickSearch(){
		if(edittext_Search.getText().toString().trim().isEmpty())
			return;
		QBSearchFriendCommand.start(this, edittext_Search.getText().toString().trim());
		this.hideKeyBoard(edittext_Search);
	}
	private void addActions(){
		this.addAction(QBServiceConsts.SEARCH_FRIEND_SUCCESS_ACTION, new SearchFriendSuccessAction());
		this.addAction(QBServiceConsts.SEARCH_FRIEND_FAIL_ACTION, new SearchFriendFailAction());
		this.addAction(QBServiceConsts.SEND_FRIEND_REQUEST_SUCCESS_ACTION, new SendFriendRequestSuccessAction());
		this.addAction(QBServiceConsts.SEND_FRIEND_REQUEST_FAIL_ACTION, new SendFriendRequestFailAction());
		Log.d("SEARCH_FRIEND_ACTIVITY", "add actions success");
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
			DialogUtils.show(SearchFriendActivity.this, "User was not found");
		}
	}
	private class SendFriendRequestSuccessAction implements Command{

		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			if(image_addfriend.isShown())
				image_addfriend.setVisibility(ImageView.GONE);
			DialogUtils.show(SearchFriendActivity.this, "Request is sent");
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
	@Override
	public void handleLoginSuccessAction() {
		// TODO Auto-generated method stub
	}
	@Override
	public void handleLoginFailAction() {
		// TODO Auto-generated method stub
	}
}
