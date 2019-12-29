
package com.socialapp.heyya.qb.helper;

public class QBVoiceCallHelper{
//extends BaseHelper implements QBRTCClientCallback{
/*	private String TAG = "QBVoiceCallHelper"; 
	private String currentSession;
	public QBVoiceCallHelper(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public QBRTCSession getCurrentSession() {
		Log.d(TAG, "getCurrentSession()");
        return QBRTCClient.getInstance().getSessions().get(currentSession);
    }

    public void setCurrentSession(QBRTCSession session) {
    	Log.d(TAG, "setCurrentSession()");
        if (!QBRTCClient.getInstance().getSessions().containsKey(session.getSessionID())) {
            addSession(session);
        }
        currentSession = session.getSessionID();
    }
    
	public void addSession(QBRTCSession session) {
        QBRTCClient.getInstance().getSessions().put(session.getSessionID(), session);
    }
	
	public void removeSession(QBRTCSession session) {
		Log.d(TAG, "removeSession()");
        if (QBRTCClient.getInstance().getSessions().containsKey(session.getSessionID())) {
        	QBRTCClient.getInstance().getSessions().remove(session.getSessionID());
        }
    }
	
	public void init(Activity activity){
		Log.d(TAG, "init()");
		if(!QBRTCClient.isInitiated())
			QBRTCClient.init(activity);
	//	if(QBChatService.getInstance()
		//		.getVideoChatWebRTCSignalingManager() != null){
			//Log.d(TAG, "init() . qbChatService.getvideochat.... != null");
		QBRTCClient.getInstance().setSignalingManager(QBChatService.getInstance()
				.getVideoChatWebRTCSignalingManager());
		//}else{
			//Log.d(TAG, "init() . qbChatService.getvideochat.... == null");
		//}
		
		// List<PeerConnection.IceServer> iceServerList = new LinkedList<>();
	     //   iceServerList.add(new PeerConnection.IceServer("turn:numb.viagenie.ca", "petrbubnov@grr.la", "petrbubnov@grr.la"));
	       // iceServerList.add(new PeerConnection.IceServer("turn:numb.viagenie.ca:3478?transport=udp", "petrbubnov@grr.la", "petrbubnov@grr.la"));
	       // iceServerList.add(new PeerConnection.IceServer("turn:numb.viagenie.ca:3478?transport=tcp", "petrbubnov@grr.la", "petrbubnov@grr.la"));
	       // QBRTCConfig.setIceServerList(iceServerList);
		// Add activity as callback to RTCClient
		
        if (QBRTCClient.isInitiated()) {
        	Log.d(TAG, "init() . add call back");
            QBRTCClient.getInstance().addCallback(this);
        }
	}

	@Override
	public void onCallRejectByUser(QBRTCSession arg0, Integer arg1,
			Map<String, String> arg2) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onCallRejectByUser()");
		Intent i = new Intent(QBServiceConsts.CALL_REJECTED_ACTION);
		LocalBroadcastManager.getInstance(context).sendBroadcast(i);
	}

	@Override
	public void onConnectedToUser(QBRTCSession arg0, Integer arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onConnectToUser() method");
		Intent i = new Intent(QBServiceConsts.CALL_ACCEPTED_ACTION);
		LocalBroadcastManager.getInstance(context).sendBroadcast(i);
	}

	@Override
	public void onConnectionClosedForUser(QBRTCSession arg0, Integer arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onConnectionClosedForUser()");
	}

	@Override
	public void onConnectionFailedWithUser(QBRTCSession arg0, Integer arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onConnectionFailedWithUser()");
	}

	@Override
	public void onDisconnectedFromUser(QBRTCSession arg0, Integer arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onDisconnectedFromUser()");
	}

	@Override
	public void onDisconnectedTimeoutFromUser(QBRTCSession arg0, Integer arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onDisconnectedTimeoutFromUser()");
	}

	@Override
	public void onLocalVideoTrackReceive(QBRTCSession arg0, QBRTCVideoTrack arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onLocalVideoTrackReceive()");
	}

	@Override
	public void onReceiveHangUpFromUser(QBRTCSession arg0, Integer arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onReceiveHangUpFromUser()");
		Intent i = new Intent(QBServiceConsts.CALL_STOPED_ACTION);
		LocalBroadcastManager.getInstance(context).sendBroadcast(i);

	}

	@Override
	public void onReceiveNewSession(QBRTCSession session) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onReceiveNewSession()");
		if (currentSession == null) {
            Log.d(TAG, "Start new session");
            QBRTCClient.getInstance().getSessions().put(session.getSessionID(), session);
            setCurrentSession(session);
            try{
            	int friendId = session.getOpponents().get(0);
    			String friendName = DatabaseManager.getFriendFullNameByFriendId(context, 
    					String.valueOf(friendId));
    			
    			Intent i = new Intent("com.socialapp.heyya.incomingcall.action");
    			i.putExtra(QBServiceConsts.EXTRA_FRIEND_ID, friendId);
    			i.putExtra(QBServiceConsts.EXTRA_FRIEND_NAME, friendName);
    			i.putExtra(QBServiceConsts.EXTRA_CALL_STATE, CallActivity.INCOMING_STATE);
    			//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			context.sendBroadcast(i);
    		}catch(Exception e){
    			Log.e(TAG, e.toString());
    		}
        } else {
            Log.d(TAG, "onReceinewSession() . Stop new session. Device now is busy");
            session.rejectCall(null);
        }
	}

	@Override
	public void onRemoteVideoTrackReceive(QBRTCSession arg0,
			QBRTCVideoTrack arg1, Integer arg2) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onRemoteVideoTrackReceive()");
	}

	@Override
	public void onSessionClosed(QBRTCSession arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onSessionClosed()");
		removeSession(arg0);
	}

	@Override
	public void onSessionStartClose(QBRTCSession arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onSessionStartClose()");
	}

	@Override
	public void onStartConnectToUser(QBRTCSession arg0, Integer arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onStartConnectToUser()");
	}

	@Override
	public void onUserNotAnswer(QBRTCSession arg0, Integer arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onUserNotAnswer()");
		Intent i = new Intent(QBServiceConsts.CALL_STOPED_ACTION);
		LocalBroadcastManager.getInstance(context).sendBroadcast(i);
	}
	
	public void call(List<Integer> userIds){
		Log.d(TAG, "call()");
		Map<String, String> userInfo = new HashMap<>();
        userInfo.put("any_custom_data", "some data");
        userInfo.put("my_avatar_url", "avatar_reference");
        try{
        	QBRTCSession newSessionWithOpponents = QBRTCClient.getInstance()
        			.createNewSessionWithOpponents(userIds, QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO);
		
        	if(newSessionWithOpponents != null){
        		Log.d(TAG, "newSessionWithOpponents != null");
        		setCurrentSession(newSessionWithOpponents);
        		newSessionWithOpponents.startCall(userInfo);
        	}else{
        		Log.d(TAG, "newSessionWithOpponents == null");
				
			}
		}catch(IllegalStateException e){
        	Log.d(TAG, "Error: "+e.toString());
        }
	}
	
	public void accept()throws Exception{
		Log.d(TAG, "accept()");
		if(getCurrentSession()!=null)
			getCurrentSession().acceptCall(null);//don't need user info
	}

	public void reject()throws Exception{
		Log.d(TAG, "reject()");
		if(getCurrentSession()!=null)
			getCurrentSession().rejectCall(null);//don't need user info
	}
	public void stopCall()throws Exception{
		Log.d(TAG, "stopCall()");
		if(getCurrentSession()!=null)
			getCurrentSession().hangUp(null);//don't need user info
	}
	*/
}