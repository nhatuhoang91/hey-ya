package com.socialapp.heyya.qb.command;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.socialapp.heyya.core.command.ServiceCommand;
import com.socialapp.heyya.qb.helper.QBFriendHelper;
import com.socialapp.heyya.service.QBService;
import com.socialapp.heyya.service.QBServiceConsts;

public class QBAcceptFriendCommand extends ServiceCommand{

	private QBFriendHelper friendHelper;
	public QBAcceptFriendCommand(Context context, QBFriendHelper friendHelper, String successAction,
			String failAction) {
		super(context, successAction, failAction);
		this.friendHelper = friendHelper;
	}

	 public static void start(Context context, String userId) {
	        Intent intent = new Intent(QBServiceConsts.ACCEPT_FRIEND_ACTION, null, context, QBService.class);
	        intent.putExtra(QBServiceConsts.EXTRA_USER_ID, userId);
	        context.startService(intent);
	}
	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		//QBUser user = (QBUser)extras.get(QBServiceConsts.EXTRA_USER);
		String userId = extras.getString(QBServiceConsts.EXTRA_USER_ID);
		//QBUser user = QBUsers.getUser(userId);
		friendHelper.acceptFriend(userId);
		return extras;
	}

}
