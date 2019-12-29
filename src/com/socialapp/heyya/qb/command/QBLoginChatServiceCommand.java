package com.socialapp.heyya.qb.command;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.socialapp.heyya.core.command.ServiceCommand;
import com.socialapp.heyya.model.AppSession;
import com.socialapp.heyya.qb.helper.QBChatRestHelper;
import com.socialapp.heyya.service.QBService;
import com.socialapp.heyya.service.QBServiceConsts;

public class QBLoginChatServiceCommand extends ServiceCommand{
	
	private QBChatRestHelper chatRestHelper;
	
	public QBLoginChatServiceCommand(Context context, QBChatRestHelper chatRestHelper, String successAction,
			String failAction) {
		super(context, successAction, failAction);
		this.chatRestHelper = chatRestHelper;
	}

	 public static void start(Context context) {
	        Intent intent = new Intent(QBServiceConsts.LOGIN_CHAT_SERVICE_ACTION, null, context, QBService.class);
	        context.startService(intent);
	    }
	 
	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		//tryLogin();
		chatRestHelper.login(AppSession.getSession().getUser());
			//	
	//	 if (!chatRestHelper.isLoggedIn()) {
			//throw new Exception(QBChatErrorsConstants.AUTHENTICATION_FAILED);
	  //      }
	//	synchronized (a) {
		//	a.wait();
		//}
	        return extras;
	}

	//private void tryLogin() throws XMPPException, IOException, SmackException, QBResponseException {
      //  long startTime = new Date().getTime();
       // long currentTime = startTime;
       // while (!chatRestHelper.isLoggedIn() && (currentTime - startTime) < Consts.LOGIN_TIMEOUT) {
         //   currentTime = new Date().getTime();
          //  try {
            //    chatRestHelper.login(AppSession.getSession().getUser());
              //  Log.d("CHAT_LOGIN_COMMAND", "chat login success");
            //} catch (SmackException ignore) { /* NOP */ //}
     //   }
	//} */
}
