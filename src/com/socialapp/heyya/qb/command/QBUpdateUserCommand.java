package com.socialapp.heyya.qb.command;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.core.command.ServiceCommand;
import com.socialapp.heyya.qb.helper.QBAuthHelper;
import com.socialapp.heyya.service.QBService;
import com.socialapp.heyya.service.QBServiceConsts;

public class QBUpdateUserCommand extends ServiceCommand{

	private QBAuthHelper authHelper;
	public QBUpdateUserCommand(Context context,QBAuthHelper authHelper, String successAction,
			String failAction) {
		super(context, successAction, failAction);
		this.authHelper = authHelper;
	}

	 public static void start(Context context, QBUser user, File imageFile) {
	        Intent intent = new Intent(QBServiceConsts.UPDATE_USER_ACTION, null, context, QBService.class);
	        intent.putExtra(QBServiceConsts.EXTRA_USER, user);
	        intent.putExtra(QBServiceConsts.EXTRA_FILE, imageFile);
	        context.startService(intent);
	 }
	 
	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		// TODO Auto-generated method stub
		QBUser user = (QBUser) extras.getSerializable(QBServiceConsts.EXTRA_USER);
		File imageFile = (File)extras.getSerializable(QBServiceConsts.EXTRA_FILE);
        user = authHelper.updateUser(user, imageFile);
        //extras.putSerializable(QBServiceConsts.EXTRA_USER, user);
        extras.putString(QBServiceConsts.EXTRA_AVT_URI, user.getCustomData());
        return extras;
	}

}
