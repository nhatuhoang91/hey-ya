package com.socialapp.heyya.qb.command;

import java.io.File;

import android.content.Context;
import android.os.Bundle;

import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.core.command.ServiceCommand;
import com.socialapp.heyya.qb.helper.QBAuthHelper;
import com.socialapp.heyya.service.QBServiceConsts;

public class QBSignUpRestCommand extends ServiceCommand{

	private QBAuthHelper authHelper;
	public QBSignUpRestCommand(Context context,QBAuthHelper authHelper, String successAction,
			String failAction) {
		super(context, successAction, failAction);
		this.authHelper = authHelper;
	}

	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		QBUser user = (QBUser) extras.getSerializable(QBServiceConsts.EXTRA_USER);
		File imageFile = (File)extras.getSerializable(QBServiceConsts.EXTRA_FILE);
        user = authHelper.signup(user, imageFile);
        extras.putSerializable(QBServiceConsts.EXTRA_USER, user);
        return extras;
	}

}
