package com.socialapp.heyya.qb.command;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.core.command.ServiceCommand;
import com.socialapp.heyya.qb.helper.QBFriendHelper;
import com.socialapp.heyya.service.QBService;
import com.socialapp.heyya.service.QBServiceConsts;

public class QBSearchFriendCommand extends ServiceCommand{

	private QBFriendHelper friendHelper;
	public QBSearchFriendCommand(Context context, QBFriendHelper friendHelper, String successAction,
			String failAction) {
		super(context, successAction, failAction);
		this.friendHelper = friendHelper;
	}

	public static void start(Context context, String login) {
        Intent intent = new Intent(QBServiceConsts.SEARCH_FRIEND_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_LOGIN, login);
        context.startService(intent);
    }
	
	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		String login = (String)extras.getString(QBServiceConsts.EXTRA_LOGIN);
		QBUser user = friendHelper.getFriendFromLogin(login);
		extras.putSerializable(QBServiceConsts.EXTRA_USER, user);
		boolean isInvite = friendHelper.isInvite(user.getId().toString());
		extras.putBoolean(QBServiceConsts.IS_INVITE, isInvite);
		return extras;
	}

}
