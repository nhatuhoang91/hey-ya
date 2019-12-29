package com.socialapp.heyya.qb.command;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.socialapp.heyya.core.command.ServiceCommand;
import com.socialapp.heyya.qb.helper.QBFriendHelper;
import com.socialapp.heyya.service.QBService;
import com.socialapp.heyya.service.QBServiceConsts;

public class QBDeleteFriendCommand extends ServiceCommand{

	private QBFriendHelper friendHelper;
	public QBDeleteFriendCommand(Context context, QBFriendHelper friendHelper, String successAction,
			String failAction) {
		super(context, successAction, failAction);
		// TODO Auto-generated constructor stub
		this.friendHelper = friendHelper;
	}

	public static void start(Context context, String userId) {
        Intent intent = new Intent(QBServiceConsts.DELETE_FRIEND_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_USER_ID, userId);
        context.startService(intent);
    }
	
	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		String friendId = extras.getString(QBServiceConsts.EXTRA_USER_ID);
		friendHelper.deleteFriend(friendId);
		return extras;
	}

}
