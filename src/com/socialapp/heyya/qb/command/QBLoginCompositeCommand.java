package com.socialapp.heyya.qb.command;

import android.content.Context;
import android.content.Intent;

import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.core.command.CompositeServiceCommand;
import com.socialapp.heyya.service.QBService;
import com.socialapp.heyya.service.QBServiceConsts;

public class QBLoginCompositeCommand extends CompositeServiceCommand{

	public QBLoginCompositeCommand(Context context, String successAction,
			String failAction) {
		super(context, successAction, failAction);
	}

	public static void start(Context context, QBUser user) {
        Intent intent = new Intent(QBServiceConsts.LOGIN_COMPOSITE_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_USER, user);
        context.startService(intent);
    }
}
