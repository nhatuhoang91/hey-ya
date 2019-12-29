package com.socialapp.heyya.qb.command;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.socialapp.heyya.core.command.ServiceCommand;
import com.socialapp.heyya.gcm.GCMHelper;
import com.socialapp.heyya.service.QBService;
import com.socialapp.heyya.service.QBServiceConsts;

public class GCMTokenRefreshCommand extends ServiceCommand{

	private GCMHelper gcmHelper;
	public GCMTokenRefreshCommand(Context context, GCMHelper gcmHelper, String successAction,
			String failAction) {
		super(context, successAction, failAction);
		// TODO Auto-generated constructor stub
		this.gcmHelper = gcmHelper;
	}

	public static void start(Context context) {
        Intent intent = new Intent(QBServiceConsts.UPDATE_GCM_ACTION, null, context, QBService.class);
        context.startService(intent);
	}
	
	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		// TODO Auto-generated method stub
		this.gcmHelper.registerGcm(Boolean.valueOf(true));
		return extras;
	}

}
