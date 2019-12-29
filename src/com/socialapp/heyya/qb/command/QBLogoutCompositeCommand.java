package com.socialapp.heyya.qb.command;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.socialapp.heyya.core.command.CompositeServiceCommand;
import com.socialapp.heyya.db.DatabaseManager;
import com.socialapp.heyya.service.QBService;
import com.socialapp.heyya.service.QBServiceConsts;

public class QBLogoutCompositeCommand extends CompositeServiceCommand{

	public QBLogoutCompositeCommand(Context context,
			String successAction, String failAction) {
		super(context, successAction, failAction);
		// TODO Auto-generated constructor stub
	}

	public static void start(Context context) {
        Intent intent = new Intent(QBServiceConsts.LOGOUT_COMPOSITE_ACTION, null, context, QBService.class);
        context.startService(intent);
    }
}
