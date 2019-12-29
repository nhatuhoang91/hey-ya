package com.socialapp.heyya.qb.command;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.core.command.ServiceCommand;
import com.socialapp.heyya.qb.helper.QBAuthHelper;
import com.socialapp.heyya.service.QBService;
import com.socialapp.heyya.service.QBServiceConsts;

public class QBLoginRestCommand extends ServiceCommand{

	private final QBAuthHelper authHelper;
	
	public QBLoginRestCommand(Context context, QBAuthHelper authHelper, String successAction,
			String failAction) {
		super(context, successAction, failAction);
		this.authHelper = authHelper;
	}

	public static void start(Context context, QBUser user) {
        Intent intent = new Intent(QBServiceConsts.LOGIN_REST_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_USER, user);
        context.startService(intent);
    }
	
	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		QBUser user = (QBUser) extras.getSerializable(QBServiceConsts.EXTRA_USER);
        user = authHelper.login(user);
        extras.putSerializable(QBServiceConsts.EXTRA_USER, user);
        return extras;
	}
}
