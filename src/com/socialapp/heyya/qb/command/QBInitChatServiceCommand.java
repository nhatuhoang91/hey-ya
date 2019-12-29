package com.socialapp.heyya.qb.command;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.socialapp.heyya.core.command.ServiceCommand;
import com.socialapp.heyya.qb.helper.QBChatRestHelper;
import com.socialapp.heyya.service.QBService;
import com.socialapp.heyya.service.QBServiceConsts;

public class QBInitChatServiceCommand extends ServiceCommand{

	private QBChatRestHelper chatRestHelper;
	 public QBInitChatServiceCommand(Context context, QBChatRestHelper chatRestHelper, String successAction,
			String failAction) {
		super(context, successAction, failAction);
		this.chatRestHelper = chatRestHelper;
	 }
	
	 public static void start(Context context) {
	        Intent intent = new Intent(QBServiceConsts.INIT_CHAT_SERVICE_ACTION, null, context, QBService.class);
	        context.startService(intent);
	    }
	 
	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		chatRestHelper.initChatService();
        return extras;
	}

}
