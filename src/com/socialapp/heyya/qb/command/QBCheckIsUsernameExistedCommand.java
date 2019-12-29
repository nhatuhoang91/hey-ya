package com.socialapp.heyya.qb.command;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.socialapp.heyya.core.command.ServiceCommand;
import com.socialapp.heyya.qb.helper.QBAuthHelper;
import com.socialapp.heyya.service.QBService;
import com.socialapp.heyya.service.QBServiceConsts;

public class QBCheckIsUsernameExistedCommand extends ServiceCommand{

	private QBAuthHelper authHelper;
	public QBCheckIsUsernameExistedCommand(Context context, QBAuthHelper authHelper,
			String successAction, String failAction) {
		super(context, successAction, failAction);
		// TODO Auto-generated constructor stub
		this.authHelper = authHelper;
	}

	public static void start(Context context, String username) {
	    Intent intent = new Intent(QBServiceConsts.CHECK_USERNAME_EXISTED_ACTION, 
	       	null, context, QBService.class);
	    intent.putExtra(QBServiceConsts.EXTRA_LOGIN, username);
	    context.startService(intent);
	}
	
	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		// TODO Auto-generated method stub
		String username = extras.getString(QBServiceConsts.EXTRA_LOGIN);
		boolean result = this.authHelper.isUsernameExisted(username);
		extras.putBoolean(QBServiceConsts.EXTRA_IS_USERNAME_EXISTED, result);
		return extras;
	}

}
