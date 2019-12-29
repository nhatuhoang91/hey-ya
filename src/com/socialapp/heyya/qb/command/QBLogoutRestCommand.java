package com.socialapp.heyya.qb.command;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.socialapp.heyya.core.command.ServiceCommand;
import com.socialapp.heyya.db.DatabaseManager;
import com.socialapp.heyya.qb.helper.QBAuthHelper;
import com.socialapp.heyya.service.QBService;
import com.socialapp.heyya.service.QBServiceConsts;

public class QBLogoutRestCommand extends ServiceCommand{

	QBAuthHelper authHelper;
	public QBLogoutRestCommand(Context context, QBAuthHelper authHelper, String successAction,
			String failAction) {
		super(context, successAction, failAction);
		// TODO Auto-generated constructor stub
		this.authHelper = authHelper;
	}

	public static void start(Context context) {
        Intent intent = new Intent(QBServiceConsts.LOGOUT_REST_ACTION, null, context, QBService.class);
        context.startService(intent);
}
	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		// TODO Auto-generated method stub
		this.authHelper.logout();
		DatabaseManager.clearDatabase(context);
		Log.d("LOGOUT_REST_COMMAND", "logout rest");
		return extras;
	}
	
}
