package com.socialapp.heyya.qb.command;

import android.content.Context;
import android.os.Bundle;

import com.socialapp.heyya.core.command.ServiceCommand;
import com.socialapp.heyya.gcm.GCMHelper;

public class RegisterGCMCommand extends ServiceCommand{

	private GCMHelper gcmHelper;
	public RegisterGCMCommand(Context context, GCMHelper gcmHelper, String successAction,
			String failAction) {
		super(context, successAction, failAction);
		// TODO Auto-generated constructor stub
		this.gcmHelper = gcmHelper;
	}

	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		gcmHelper.registerAction();
		return extras;
	}

}
