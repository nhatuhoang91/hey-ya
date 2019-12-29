package com.socialapp.heyya.qb.command;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.socialapp.heyya.core.command.ServiceCommand;
import com.socialapp.heyya.qb.helper.QBFriendHelper;
import com.socialapp.heyya.service.QBService;
import com.socialapp.heyya.service.QBServiceConsts;

public class QBIgnoreFriendCommand extends ServiceCommand{

	private QBFriendHelper friendHelper;
	public QBIgnoreFriendCommand(Context context, QBFriendHelper friendHelper, String successAction,
			String failAction) {
		super(context, successAction, failAction);
		this.friendHelper = friendHelper;
	}

	 public static void start(Context context, String userId) {
	     Intent intent = new Intent(QBServiceConsts.IGNORE_FRIEND_ACTION, null, context, QBService.class);
	     intent.putExtra(QBServiceConsts.EXTRA_USER_ID, userId);
	     context.startService(intent);
	 }
	 
	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		String userId = extras.getString(QBServiceConsts.EXTRA_USER_ID);
		//QBUser user = QBUsers.getUser(userId);
		friendHelper.ignoreFriend(userId);
		return extras;
	}
}
