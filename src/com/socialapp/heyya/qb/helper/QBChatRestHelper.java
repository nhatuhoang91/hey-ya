package com.socialapp.heyya.qb.helper;

import java.io.IOException;
import java.util.List;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.App;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.utils.Consts;

public class QBChatRestHelper extends BaseHelper{

	private static final int AUTO_PRESENCE_INTERVAL_IN_SECONDS = 60;
	public static QBChatService chatService;
	public QBChatRestHelper(Context context) {
		super(context);
	}
	
	private ConnectionListener connectionListener = new ChatConnectionListener();
	
	public synchronized void initChatService() throws XMPPException, SmackException,QBResponseException {
        if (!QBChatService.isInitialized()) {
        	QBAuth.createSession();
            QBChatService.init(context);
        
            QBChatService.setDefaultPacketReplyTimeout(Consts.DEFAULT_PACKET_REPLY_TIMEOUT);
            
            chatService = QBChatService.getInstance();
            chatService.addConnectionListener(connectionListener);
        }
    }
	
	public synchronized void login(QBUser user) throws XMPPException, IOException, SmackException, InterruptedException {
		if (!chatService.isLoggedIn() && user != null) {
        	final Object a = new Object();
			chatService.login(user, new QBEntityCallback<QBSession>() {

				@Override
				public void onError(List<String> arg0) {
					synchronized (a) {
						a.notify();
					}
					Log.e("LOGIN_CHAT_ERROR", "login_chat_error");
				}

				@Override
				public void onSuccess() {
					
					try{
						chatService.enableCarbons();
					 chatService.startAutoSendPresence(AUTO_PRESENCE_INTERVAL_IN_SECONDS);
					}catch(Exception e){
						Log.e("START_AUTO_SEND_PRE_ERROR", e.toString());
					}
					synchronized (a) {
						a.notify();
					}
				}

				@Override
				public void onSuccess(QBSession arg0, Bundle arg1) {
					// TODO Auto-generated method stub
					
				}
			});
			synchronized (a) {
				a.wait();
			}
        }
    }
	
	public synchronized void logout() throws QBResponseException, SmackException.NotConnectedException {
		try{
        if (chatService != null && chatService.isLoggedIn()) {
            chatService.stopAutoSendPresence();
            chatService.setReconnectionAllowed(false);
            chatService.logout();
            chatService.destroy();
            App.releaseWakeLock();
        }
		}catch(Exception e){
			Intent i = new Intent(QBServiceConsts.EXTRA_UNEXPECTED_ERROR);
			i.putExtra(QBServiceConsts.EXTRA_ERROR, "error at logout method. " +
					". at QBChatRestHelper class. detail :"+e.toString());
			LocalBroadcastManager.getInstance(context).sendBroadcast(i);
		}
    }

    public static void destroy() {
    	if(chatService!=null)
    		chatService.destroy();
    }

    public boolean isLoggedIn() {
        return chatService != null && chatService.isLoggedIn();
    }
    
    public QBChatService getChatService(){
    	return chatService;
    }
    
	private class ChatConnectionListener implements ConnectionListener {

        @Override
        public void connected(XMPPConnection connection) {
        	Log.d("CHAT_CONNECT", "connected, connection : "+connection.toString());
        }

        @Override
        public void authenticated(XMPPConnection connection) {
        	Log.d("CHAT_CONNECT", "authenticated, connection : "+connection.toString());
        }

        @Override
        public void connectionClosed() {
        	Log.d("CHAT_CONNECT", "connection closed");
        }

        @Override
        public void connectionClosedOnError(Exception e) {
        	Log.d("CHAT_CONNECT", "connection closed on error : "+ e.toString());
        }

        @Override
        public void reconnectingIn(int seconds) {
        	Log.d("CHAT_CONNECT", "reconnecting in : "+seconds);
        }

        @Override
        public void reconnectionSuccessful() {
        }

        @Override
        public void reconnectionFailed(Exception error) {
        }
    }
}
