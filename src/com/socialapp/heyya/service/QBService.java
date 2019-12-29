package com.socialapp.heyya.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.SmackException.NoResponseException;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.quickblox.core.exception.QBResponseException;
import com.socialapp.heyya.core.command.CompositeServiceCommand;
import com.socialapp.heyya.core.command.ServiceCommand;
import com.socialapp.heyya.gcm.GCMHelper;
import com.socialapp.heyya.qb.command.DeleteRegIdCommand;
import com.socialapp.heyya.qb.command.GCMTokenRefreshCommand;
import com.socialapp.heyya.qb.command.QBAcceptCallCommand;
import com.socialapp.heyya.qb.command.QBAcceptFriendCommand;
import com.socialapp.heyya.qb.command.QBCallCommand;
import com.socialapp.heyya.qb.command.QBCheckIsUsernameExistedCommand;
import com.socialapp.heyya.qb.command.QBDeleteFriendCommand;
import com.socialapp.heyya.qb.command.QBIgnoreFriendCommand;
import com.socialapp.heyya.qb.command.QBInitChatServiceCommand;
import com.socialapp.heyya.qb.command.QBInitFriendCommand;
import com.socialapp.heyya.qb.command.QBInitVoiceCallCommand;
import com.socialapp.heyya.qb.command.QBLoginChatServiceCommand;
import com.socialapp.heyya.qb.command.QBLoginCompositeCommand;
import com.socialapp.heyya.qb.command.QBLoginRestCommand;
import com.socialapp.heyya.qb.command.QBLogoutChatServiceCommand;
import com.socialapp.heyya.qb.command.QBLogoutCompositeCommand;
import com.socialapp.heyya.qb.command.QBLogoutRestCommand;
import com.socialapp.heyya.qb.command.QBRejectCallCommand;
import com.socialapp.heyya.qb.command.QBSearchFriendCommand;
import com.socialapp.heyya.qb.command.QBSendFriendRequest;
import com.socialapp.heyya.qb.command.QBSignUpCompositeCommand;
import com.socialapp.heyya.qb.command.QBSignUpRestCommand;
import com.socialapp.heyya.qb.command.QBStopCallCommand;
import com.socialapp.heyya.qb.command.QBUpdateUserCommand;
import com.socialapp.heyya.qb.command.RegisterGCMCommand;
import com.socialapp.heyya.qb.command.SendMessageCommand;
import com.socialapp.heyya.qb.helper.BaseHelper;
import com.socialapp.heyya.qb.helper.QBAuthHelper;
import com.socialapp.heyya.qb.helper.QBChatRestHelper;
import com.socialapp.heyya.qb.helper.QBFriendHelper;
import com.socialapp.heyya.qb.helper.QBVoiceCallHelper;
import com.socialapp.heyya.utils.Consts;
import com.socialapp.heyya.utils.ErrorUtils;
import com.socialapp.heyya.utils.Utils;

public class QBService extends Service{

	public static final int AUTH_HELPER = 1;
    public static final int FRIEND_HELPER = 2;
    public static final int CHAT_REST_HELPER = 3;
    public static final int GCM_HELPER = 4;
   // public static final int VOICE_CALL_HELPER = 5;
    
	private static final String TAG = QBService.class.getSimpleName();
	
	private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private final BlockingQueue<Runnable> threadQueue;
    private IBinder binder = new QBServiceBinder();
    
    private Map<String, ServiceCommand> serviceCommandMap = new HashMap<String, ServiceCommand>();
    
    private ThreadPoolExecutor threadPool;
    
    private QBAuthHelper authHelper;
    private QBFriendHelper friendHelper;
    private QBChatRestHelper chatRestHelper;
    private GCMHelper gcmHelper;
    private QBVoiceCallHelper voiceCallHelper;
    private Map<Integer, BaseHelper> helpers = new HashMap<Integer, BaseHelper>();
    
    public QBService() {
        threadQueue = new LinkedBlockingQueue<Runnable>();
        initThreads();
        
        initHelpers();
        initCommands();
    }
    
    private void initThreads() {
        threadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT, threadQueue);
        threadPool.allowCoreThreadTimeOut(true);
    }
    private void initHelpers(){
    	chatRestHelper = new QBChatRestHelper(this);
    	helpers.put(CHAT_REST_HELPER, chatRestHelper);
    	authHelper = new QBAuthHelper(this);
    	helpers.put(AUTH_HELPER, authHelper);
    	friendHelper = new QBFriendHelper(this);
    	helpers.put(FRIEND_HELPER, friendHelper);
    	gcmHelper = new GCMHelper(this);
    	helpers.put(GCM_HELPER, gcmHelper);
    	//voiceCallHelper = new QBVoiceCallHelper(this);
    	//helpers.put(VOICE_CALL_HELPER, voiceCallHelper);
    }
    private void initCommands(){
    	//register command
    	registerLoginRestCommand();
    	registerLoginChatServiceCommand();
    	registerSearchFriendCommand();
    	registerSendFriendRequestCommand();
    	registerAcceptFriendCommand();
    	registerIgnoreFriendCommand();
    	registerLogoutChatServiceCommand();
    	registerLogoutRestCommand();
    	registerUpdateUserCommand();
    	registerDeleteFriendCommand();
    	registerSendMessageCommand();
    	registerCheckIsUsernameExistedCommand();
    	registerGCMTokenRefreshCommand();
    	//registerCallCommand();
    	//registerAcceptCallCommand();
    	//registerRejectCallCommand();
    	//registerStopCallCommand();
    	//registerInitVoiceCallCommand();
    	
    	//register composite command
    	registerLoginCompositeCommand();
    	registerSignUpCompositeCommand();
    	registerLogoutCompositeCommand();
    }
    
    private void registerLoginRestCommand(){
    	QBAuthHelper authHelper = (QBAuthHelper)helpers.get(AUTH_HELPER);
    	QBLoginRestCommand loginRestCommand = new QBLoginRestCommand(this, authHelper,
                QBServiceConsts.LOGIN_REST_SUCCESS_ACTION, QBServiceConsts.LOGIN_REST_FAIL_ACTION);

        serviceCommandMap.put(QBServiceConsts.LOGIN_REST_ACTION, loginRestCommand);
    }
    
    private void registerLoginChatServiceCommand(){
    	QBChatRestHelper chatRestHelper = (QBChatRestHelper)helpers.get(CHAT_REST_HELPER);
    	QBLoginChatServiceCommand loginChatServiceCommand = new QBLoginChatServiceCommand(this, chatRestHelper,
    			QBServiceConsts.LOGIN_CHAT_SERVICE_SUCCESS_ACTION, QBServiceConsts.LOGIN_CHAT_SERVICE_FAIL_ACTION);
    	serviceCommandMap.put(QBServiceConsts.LOGIN_CHAT_SERVICE_ACTION, loginChatServiceCommand);
    }
    
    private void registerSearchFriendCommand(){
    	QBFriendHelper friendHelper = (QBFriendHelper)helpers.get(FRIEND_HELPER);
    	QBSearchFriendCommand searchFriendCommand = new QBSearchFriendCommand(this, friendHelper, 
    			QBServiceConsts.SEARCH_FRIEND_SUCCESS_ACTION, QBServiceConsts.SEARCH_FRIEND_FAIL_ACTION);
    	serviceCommandMap.put(QBServiceConsts.SEARCH_FRIEND_ACTION, searchFriendCommand);
    }
    
    private void registerSendFriendRequestCommand(){
    	QBFriendHelper friendHelper = (QBFriendHelper)helpers.get(FRIEND_HELPER);
    	QBSendFriendRequest sendFriendRequestCommand = new QBSendFriendRequest(this, friendHelper,
    			QBServiceConsts.SEND_FRIEND_REQUEST_SUCCESS_ACTION, QBServiceConsts.SEND_FRIEND_REQUEST_FAIL_ACTION);
    	serviceCommandMap.put(QBServiceConsts.SEND_FRIEND_REQUEST_ACTION, sendFriendRequestCommand);
    }
    
    private void registerAcceptFriendCommand(){
    	QBFriendHelper friendHelper = (QBFriendHelper)helpers.get(FRIEND_HELPER);
    	QBAcceptFriendCommand acceptFriendCommand = new QBAcceptFriendCommand(this, friendHelper,
    			QBServiceConsts.ACCEPT_FRIEND_SUCCESS_ACTION, QBServiceConsts.ACCEPT_FRIEND_FAIL_ACTION);
    	serviceCommandMap.put(QBServiceConsts.ACCEPT_FRIEND_ACTION, acceptFriendCommand);
    }
    
    private void registerIgnoreFriendCommand(){
    	QBFriendHelper friendHelper = (QBFriendHelper)helpers.get(FRIEND_HELPER);
    	QBIgnoreFriendCommand ignoreFriendCommand = new QBIgnoreFriendCommand(this, friendHelper,
    			QBServiceConsts.IGNORE_FRIEND_SUCCESS_ACTION, QBServiceConsts.IGNORE_FRIEND_FAIL_ACTION);
    	serviceCommandMap.put(QBServiceConsts.IGNORE_FRIEND_ACTION, ignoreFriendCommand);
    }
    
    private void registerLogoutChatServiceCommand(){
    	QBChatRestHelper chatRestHelper = (QBChatRestHelper)helpers.get(CHAT_REST_HELPER);
    	QBLogoutChatServiceCommand logoutChatServiceCommand = new QBLogoutChatServiceCommand(this, chatRestHelper,
    			QBServiceConsts.LOGOUT_CHAT_SERVICE_SUCCESS_ACTION, QBServiceConsts.LOGOUT_CHAT_SERVICE_FAIL_ACTION);
    	serviceCommandMap.put(QBServiceConsts.LOGOUT_CHAT_SERVICE_ACTION, logoutChatServiceCommand);
    }
    private void registerLogoutRestCommand(){
    	QBAuthHelper authHelper = (QBAuthHelper)helpers.get(AUTH_HELPER);
    	QBLogoutRestCommand logoutRestCommand = new QBLogoutRestCommand(this, authHelper,
    			QBServiceConsts.LOGOUT_REST_SUCCESS_ACTION, QBServiceConsts.LOGOUT_REST_FAIL_ACTION);
    	serviceCommandMap.put(QBServiceConsts.LOGOUT_REST_ACTION,logoutRestCommand);
    }
    
    private void registerUpdateUserCommand(){
    	QBAuthHelper authHelper = (QBAuthHelper)helpers.get(AUTH_HELPER);
    	QBUpdateUserCommand updateUserCommand = new QBUpdateUserCommand(this, authHelper,
    			QBServiceConsts.UPDATE_USER_SUCCESS_ACTION, QBServiceConsts.UPDATE_USER_FAIL_ACTION);
    	serviceCommandMap.put(QBServiceConsts.UPDATE_USER_ACTION, updateUserCommand);
    }
    
    private void registerDeleteFriendCommand(){
    	QBFriendHelper friendHelper = (QBFriendHelper)helpers.get(FRIEND_HELPER);
    	QBDeleteFriendCommand deleteFriendCommand = new QBDeleteFriendCommand(this, friendHelper,
    			QBServiceConsts.DELETE_FRIEND_SUCCESS_ACTION, QBServiceConsts.DELETE_FRIEND_FAIL_ACTION);
    	serviceCommandMap.put(QBServiceConsts.DELETE_FRIEND_ACTION, deleteFriendCommand);
    }
    
    private void registerSendMessageCommand(){
    	GCMHelper gcmHelper = (GCMHelper)helpers.get(GCM_HELPER);
    	SendMessageCommand sendMessageCommand = new SendMessageCommand(this, gcmHelper,
    			QBServiceConsts.SEND_MESSAGE_SUCCESS_ACTION, QBServiceConsts.SEND_MESSAGE_FAIL_ACTION);
    	serviceCommandMap.put(QBServiceConsts.SEND_MESSAGE_ACTION, sendMessageCommand);
    }
   
    private void registerCheckIsUsernameExistedCommand(){
    	QBAuthHelper authHelper = (QBAuthHelper)helpers.get(AUTH_HELPER);
    	QBCheckIsUsernameExistedCommand checkIsUsernameExisted = new QBCheckIsUsernameExistedCommand(this, authHelper,
    			QBServiceConsts.CHECK_USERNAME_EXISTED_SUCCESS_ACTION, 
    			QBServiceConsts.CHECK_USERNAME_EXISTED_FAIL_ACTION);
    	serviceCommandMap.put(QBServiceConsts.CHECK_USERNAME_EXISTED_ACTION, checkIsUsernameExisted);
    }
    
    private void registerGCMTokenRefreshCommand(){
    	GCMHelper gcmHelper = (GCMHelper)helpers.get(GCM_HELPER);
    	GCMTokenRefreshCommand gcmTokenRefreshCommand = new GCMTokenRefreshCommand(this,gcmHelper,
    			QBServiceConsts.UPDATE_GCM_SUCCESS_ACTION, QBServiceConsts.UPDATE_GCM_FAIL_ACTION);
    	serviceCommandMap.put(QBServiceConsts.UPDATE_GCM_ACTION, gcmTokenRefreshCommand);
    }
    
   /* private void registerCallCommand(){
    	QBVoiceCallHelper voiceCallHelper = (QBVoiceCallHelper)helpers.get(VOICE_CALL_HELPER);
    	QBCallCommand callCommand = new QBCallCommand(this, voiceCallHelper, QBServiceConsts.CALL_SUCCESS_ACTION,
    			QBServiceConsts.CALL_FAIL_ACTION);
    	serviceCommandMap.put(QBServiceConsts.CALL_ACTION, callCommand);
    }
    
    private void registerAcceptCallCommand(){
    	QBVoiceCallHelper voiceCallHelper = (QBVoiceCallHelper)helpers.get(VOICE_CALL_HELPER);
    	QBAcceptCallCommand acceptCallCommand = new QBAcceptCallCommand(this, voiceCallHelper, QBServiceConsts.ACCEPT_CALL_SUCCESS_ACTION,
    			QBServiceConsts.ACCEPT_CALL_FAIL_ACTION);
    	serviceCommandMap.put(QBServiceConsts.ACCEPT_CALL_ACTION, acceptCallCommand);
    }
    
    private void registerRejectCallCommand(){
    	QBVoiceCallHelper voiceCallHelper = (QBVoiceCallHelper)helpers.get(VOICE_CALL_HELPER);
    	QBRejectCallCommand rejectCallCommand = new QBRejectCallCommand(this, voiceCallHelper, QBServiceConsts.REJECT_CALL_SUCCESS_ACTION,
    			QBServiceConsts.REJECT_CALL_FAIL_ACTION);
    	serviceCommandMap.put(QBServiceConsts.REJECT_CALL_ACTION, rejectCallCommand);
    }
    
    private void registerStopCallCommand(){
    	QBVoiceCallHelper voiceCallHelper = (QBVoiceCallHelper)helpers.get(VOICE_CALL_HELPER);
    	QBStopCallCommand stopCallCommand = new QBStopCallCommand(this, voiceCallHelper, QBServiceConsts.STOP_CALL_SUCCESS_ACTION,
    			QBServiceConsts.STOP_CALL_FAIL_ACTION);
    	serviceCommandMap.put(QBServiceConsts.STOP_CALL_ACTION, stopCallCommand);
    }
    private void registerInitVoiceCallCommand(){
    	QBVoiceCallHelper voiceCallHelper = (QBVoiceCallHelper)helpers.get(VOICE_CALL_HELPER);
    	QBInitVoiceCallCommand initVoiceCallCommand = new QBInitVoiceCallCommand(this, voiceCallHelper,
    			QBServiceConsts.INIT_VOICE_CALL_SUCCESS_ACTION, QBServiceConsts.INIT_VOICE_CALL_FAIL_ACTION);
    	serviceCommandMap.put(QBServiceConsts.INIT_VOICE_CALL_ACTION, initVoiceCallCommand);
    }
    */
    // ------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------
    //------------------------------------------------------------------------------
    
    private void registerLoginCompositeCommand() {
        QBLoginCompositeCommand loginCompositeCommand = new QBLoginCompositeCommand(this,
                QBServiceConsts.LOGIN_SUCCESS_ACTION, QBServiceConsts.LOGIN_FAIL_ACTION);

        QBLoginRestCommand loginRestCommand = 
        		(QBLoginRestCommand)serviceCommandMap.get(QBServiceConsts.LOGIN_REST_ACTION);
        loginCompositeCommand.addCommand(loginRestCommand);
        
        addInitAndLoginChatCommand(loginCompositeCommand);
        serviceCommandMap.put(QBServiceConsts.LOGIN_COMPOSITE_ACTION, loginCompositeCommand);
    }
    
    private void registerSignUpCompositeCommand(){
    	QBAuthHelper authHelper = (QBAuthHelper)helpers.get(AUTH_HELPER);
    	QBSignUpCompositeCommand signUpCompositeCommand = new QBSignUpCompositeCommand(this,
    			QBServiceConsts.SIGNUP_SUCCESS_ACTION, QBServiceConsts.SIGNUP_FAIL_ACTION);
    	QBSignUpRestCommand signUpRestCommand = new QBSignUpRestCommand(this, authHelper,
    			QBServiceConsts.SIGNUP_REST_SUCCESS_ACTION, QBServiceConsts.SIGNUP_REST_FAIL_ACTION);
    	signUpCompositeCommand.addCommand(signUpRestCommand);
    	addInitAndLoginChatCommand(signUpCompositeCommand);
    	serviceCommandMap.put(QBServiceConsts.SIGNUP_COMPOSITE_ACTION, signUpCompositeCommand);
    }
    
    private void addInitAndLoginChatCommand(CompositeServiceCommand compositeCommand){
    	QBChatRestHelper chatRestHelper = (QBChatRestHelper)helpers.get(CHAT_REST_HELPER);
    	QBFriendHelper friendHelper = (QBFriendHelper)helpers.get(FRIEND_HELPER);
    	GCMHelper gcmHelper = (GCMHelper)helpers.get(GCM_HELPER);
    	
    	RegisterGCMCommand gcmCommand = new RegisterGCMCommand(this, gcmHelper,
    			QBServiceConsts.REGISRER_GCM_SUCCESS_ACTION, QBServiceConsts.REGISTER_GCM_FAIL_ACTION);
    	QBInitChatServiceCommand initChatServiceCommand = new QBInitChatServiceCommand(this, chatRestHelper,
    			QBServiceConsts.INIT_CHAT_SERVICE_SUCSESS_ACTION, QBServiceConsts.INIT_CHAT_SERVICE_FAIL_ACTION);
    	QBLoginChatServiceCommand loginChatServiceCommand = new QBLoginChatServiceCommand(this, chatRestHelper,
    			QBServiceConsts.LOGIN_CHAT_SERVICE_SUCCESS_ACTION, QBServiceConsts.LOGIN_CHAT_SERVICE_FAIL_ACTION);
    	QBInitFriendCommand initFriendCommand = new QBInitFriendCommand(this, friendHelper,
    			QBServiceConsts.INIT_FRIEND_SUCCESS_ACTION, QBServiceConsts.INIT_FRIEND_FAIL_ACTION);
    	compositeCommand.addCommand(gcmCommand);
    	compositeCommand.addCommand(initChatServiceCommand);
    	compositeCommand.addCommand(loginChatServiceCommand);
    	compositeCommand.addCommand(initFriendCommand);
    }
    
    private void registerLogoutCompositeCommand(){
    	GCMHelper gcmHelper = (GCMHelper)helpers.get(GCM_HELPER);
    	//QBVoiceCallHelper voiceCallHelper = (QBVoiceCallHelper)helpers.get(VOICE_CALL_HELPER);
    	
    	QBLogoutCompositeCommand logoutCompositeCommand = new QBLogoutCompositeCommand(this, QBServiceConsts.LOGOUT_COMPOSITE_SUCCESS_ACTION,
    			QBServiceConsts.LOGOUT_COMPOSITE_FAIL_ACTION);
    	QBLogoutChatServiceCommand logoutChatServiceCommand = 
    			(QBLogoutChatServiceCommand)serviceCommandMap.get(QBServiceConsts.LOGOUT_CHAT_SERVICE_ACTION);
    	QBLogoutRestCommand logoutRestCommand = 
    			(QBLogoutRestCommand)serviceCommandMap.get(QBServiceConsts.LOGOUT_REST_ACTION);
    	DeleteRegIdCommand deleteRegIdCommand = new DeleteRegIdCommand(this, gcmHelper,
    			QBServiceConsts.LOGOUT_COMPOSITE_SUCCESS_ACTION, QBServiceConsts.LOGOUT_COMPOSITE_FAIL_ACTION);
    	logoutCompositeCommand.addCommand(deleteRegIdCommand);
    	logoutCompositeCommand.addCommand(logoutChatServiceCommand);
    	logoutCompositeCommand.addCommand(logoutRestCommand);
    	
    	serviceCommandMap.put(QBServiceConsts.LOGOUT_COMPOSITE_ACTION, logoutCompositeCommand);
    }
    
    @Override
    public void onCreate(){
    }
    
    @Override
    public void onDestroy(){
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	String action;
        if (intent != null && (action = intent.getAction()) != null) {
            Log.d(TAG, "service started with resultAction=" + action);
            ServiceCommand command = serviceCommandMap.get(action);
            if (command != null) {
                startAsync(command, intent);
            }
        }
        return START_NOT_STICKY;
    }
    private void startAsync(final ServiceCommand command, final Intent intent) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "executing with resultAction=" + intent.getAction());
                try {
                    command.execute(intent.getExtras());
                } catch (QBResponseException e) {
                    ErrorUtils.logError(e);
                    if (Utils.isExactError(e, Consts.SESSION_DOES_NOT_EXIST)){
                        refreshSession();
                    }
                    else if (Utils.isTokenDestroyedError(e)) {
                        forceRelogin();
                    }
                }catch (NoResponseException e) {
					//refreshSession();
                	forceRelogin();
				} 
                catch (Exception e) {
                    ErrorUtils.logError(e);
                }
            }
        });
    }
    
    private void forceRelogin() {
        Intent intent = new Intent(QBServiceConsts.FORCE_RELOGIN);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void refreshSession() {
        Intent intent = new Intent(QBServiceConsts.REFRESH_SESSION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	public class QBServiceBinder extends Binder {

		public QBService getService() {
	            return QBService.this;
	        }
	 }
}
