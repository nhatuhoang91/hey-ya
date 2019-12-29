package com.socialapp.heyya.qb.command;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.core.command.ServiceCommand;
import com.socialapp.heyya.qb.helper.QBFriendHelper;
import com.socialapp.heyya.service.QBService;
import com.socialapp.heyya.service.QBServiceConsts;

public class QBSendFriendRequest extends ServiceCommand{

	private QBFriendHelper friendHelper;
	public QBSendFriendRequest(Context context, QBFriendHelper friendHelper, String successAction,
			String failAction) {
		super(context, successAction, failAction);
		this.friendHelper = friendHelper;
	}
	
	public static void start(Context context, QBUser user){
		Intent intent = new Intent(QBServiceConsts.SEND_FRIEND_REQUEST_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_USER, user);
        context.startService(intent);
	}
	
	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		QBUser user = (QBUser)extras.get(QBServiceConsts.EXTRA_USER);
	
		friendHelper.addFriend(user);
		return extras;
	}
}
