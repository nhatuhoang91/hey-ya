package com.socialapp.heyya.qb.command;

import java.io.File;

import android.content.Context;
import android.content.Intent;

import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.core.command.CompositeServiceCommand;
import com.socialapp.heyya.service.QBService;
import com.socialapp.heyya.service.QBServiceConsts;

public class QBSignUpCompositeCommand extends CompositeServiceCommand{

	public QBSignUpCompositeCommand(Context context, String successAction,
			String failAction) {
		super(context, successAction, failAction);
	}

	 public static void start(Context context, QBUser user, File imageFile) {
	        Intent intent = new Intent(QBServiceConsts.SIGNUP_COMPOSITE_ACTION, null, context, QBService.class);
	        intent.putExtra(QBServiceConsts.EXTRA_USER, user);
	        intent.putExtra(QBServiceConsts.EXTRA_FILE, imageFile);
	        context.startService(intent);
	    }
}
