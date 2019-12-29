package com.socialapp.heyya.qb.command;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.socialapp.heyya.App;
import com.socialapp.heyya.core.command.ServiceCommand;
import com.socialapp.heyya.gcm.GCMHelper;
import com.socialapp.heyya.utils.PrefsHelper;

public class DeleteRegIdCommand extends ServiceCommand{

	private GCMHelper gcmHelper;
	public DeleteRegIdCommand(Context context,GCMHelper gcmHelper, String successAction,
			String failAction) {
		super(context, successAction, failAction);
		this.gcmHelper = gcmHelper;
	}

	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		// TODO Auto-generated method stub
		String login = App.getInstance().getPrefsHelper().getString(PrefsHelper.PREF_USER_ID,null);
		if(login!=null){
			Log.d("DELETE_REGID_COMMAND", "login : "+ login);
			gcmHelper.deleteRegistrationId(login);
		}
		return extras;
	}

}
