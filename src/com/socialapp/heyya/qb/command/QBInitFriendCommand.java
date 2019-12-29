package com.socialapp.heyya.qb.command;

import android.content.Context;
import android.os.Bundle;

import com.socialapp.heyya.core.command.ServiceCommand;
import com.socialapp.heyya.qb.helper.QBFriendHelper;

public class QBInitFriendCommand extends ServiceCommand{

	private QBFriendHelper friendHelper;
	public QBInitFriendCommand(Context context, QBFriendHelper friendHelper, String successAction,
			String failAction) {
		super(context, successAction, failAction);
		this.friendHelper = friendHelper;
	}

	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		friendHelper.init();
        return extras;
	}

}
