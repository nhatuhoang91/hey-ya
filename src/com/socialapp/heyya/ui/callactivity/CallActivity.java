package com.socialapp.heyya.ui.callactivity;

public class CallActivity{
//extends BaseActivity{
	/*private static final String TAG = CallActivity.class.getSimpleName();
	public static final int CALLING_STATE = 0;
	public static final int INCOMING_STATE = 1;
	public static final int OUTCOMING_STATE = 2;
	public static final int STOP_STATE = 3;
	
	MediaPlayerManager mediaPlayerManager;
	
	private Chronometer chronometer;
	private ImageView imageView_Avt;
	private TextView textview_Fullname;
	private TextView textview_State;
	
	private RelativeLayout layout_Calling;
	private Button button_Speaker;
	private Button button_Mute;
	private Button button_EndCall;
	
	private RelativeLayout layout_IncomingCall;
	private Button button_Accept;
	private Button button_Reject;
	
	private Bundle bundle;
	String friendId;
	String fullname;
	int state = STOP_STATE;
	QBVoiceCallHelper voiceCallHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call);
		
		voiceCallHelper = new QBVoiceCallHelper(this);
		voiceCallHelper.init(this);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if(mediaPlayerManager==null){
	    	 mediaPlayerManager = new MediaPlayerManager(this);
	     }
		initView();
		bundle = getIntent().getExtras();
		if(bundle!=null){
			friendId = bundle.getString(QBServiceConsts.EXTRA_FRIEND_ID);
			fullname = bundle.getString(QBServiceConsts.EXTRA_FRIEND_NAME);
			state = bundle.getInt(QBServiceConsts.EXTRA_CALL_STATE);
		}
		setView(state);
		addActions();
	}
	
	@Override
	 protected void onStart() {
		 super.onStart();
		 Log.d("CALL_ACTIVITY", "onStart");
		 if(state == OUTCOMING_STATE){
			 Log.d("CALL_ACTIVITY", "outgoing call");
			 List<Integer> userIds = new ArrayList<Integer>();
			 userIds.add(Integer.valueOf(friendId));
			 voiceCallHelper.call(userIds);
			// QBCallCommand.start(this, friendId);
		 }
	 }
	@Override
	 protected void onResume() {
	     super.onResume();
	     if(mediaPlayerManager==null){
	    	 mediaPlayerManager = new MediaPlayerManager(this);
	     }
	     if(fullname!=null){
	    	 textview_Fullname.setText(fullname);
	     }
	     Log.d("CALL_ACTIVITY", "onResume");
	 }
	@Override
	 protected void onPause() {
		 super.onPause();
		 Log.d("CALL_ACTIVITY", "onPaused");
	 }
	@Override
	 protected void onStop() {
		 super.onStop();
		 Log.d("CALL_ACTIVITY", "onStop");
		 if(mediaPlayerManager!=null){
			 mediaPlayerManager.stop();
		 }
	 }
	@Override
	 protected void onDestroy() {
		 super.onDestroy();
		 Log.d("CALL_ACTIVITY", "onDestroy");
	 }
	
	 @Override
	 public void onBackPressed() {
	 }
	
	private void addActions(){
		addAction(QBServiceConsts.CALL_ACCEPTED_ACTION, new CallAccepted());
		addAction(QBServiceConsts.CALL_REJECTED_ACTION, new CallRejected());
		addAction(QBServiceConsts.CALL_STOPED_ACTION, new CallStoped());
		
		addAction(QBServiceConsts.CALL_SUCCESS_ACTION, new CallSuccessAction());
		addAction(QBServiceConsts.CALL_FAIL_ACTION, new CallFailAction());
		
		addAction(QBServiceConsts.ACCEPT_CALL_SUCCESS_ACTION, new AcceptCallSuccessAction());
		addAction(QBServiceConsts.ACCEPT_CALL_FAIL_ACTION, new AcceptCallFailAction());
		
		addAction(QBServiceConsts.REJECT_CALL_SUCCESS_ACTION, new RejectCallSuccessAction());
		addAction(QBServiceConsts.REJECT_CALL_FAIL_ACTION, new RejectCallFailAction());
		
		addAction(QBServiceConsts.STOP_CALL_SUCCESS_ACTION, new StopCallSuccessAction());
		addAction(QBServiceConsts.STOP_CALL_FAIL_ACTION, new StopCallFailAction());
	}
	
	private class CallSuccessAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			DialogUtils.showLong(CallActivity.this, "Calling");
		}
	}
	
	private class CallFailAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			DialogUtils.showLong(CallActivity.this, "Call fail");
		}
	}
	
	private class AcceptCallSuccessAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			state  = CALLING_STATE;
			setView(state);
			//show time;
		}
	}
	
	private class AcceptCallFailAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			DialogUtils.showLong(CallActivity.this, "Accept fail action");
		}
	}
	
	private class RejectCallSuccessAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			DialogUtils.showLong(CallActivity.this, "reject call success action");
			state = STOP_STATE;
			setView(state);
			finishActivityWithDelay();
		}
	}
	
	private class RejectCallFailAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			DialogUtils.showLong(CallActivity.this, "reject call fail action");
		}
	}
	
	private class StopCallSuccessAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			//show time...
			state = STOP_STATE;
			setView(state);
			finishActivityWithDelay();
		}
	}
	
	private class StopCallFailAction implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			DialogUtils.showLong(CallActivity.this, "stop call fail action");
		}
	}
	
	private class CallAccepted implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			DialogUtils.showLong(CallActivity.this, "Friend accepted call");
			state=CALLING_STATE;
			setView(state);
		}
	}
	
	private class CallRejected implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			state = STOP_STATE;
			setView(state);
			finishActivityWithDelay();
		}
	}
	
	private class CallStoped implements Command{
		@Override
		public void execute(Bundle bundle) throws Exception {
			// TODO Auto-generated method stub
			state = STOP_STATE;
			setView(state);
			finishActivityWithDelay();
		}
	}
	
	private void setView(int state){
		if(state == CALLING_STATE){
			disableView(layout_IncomingCall);
			disableView(textview_State);
			enableView(layout_Calling);
			enableView(chronometer);
			startTimer();
			stopSound();
		}else
			if(state == INCOMING_STATE){
				disableView(layout_Calling);
				disableView(chronometer);
				enableView(layout_IncomingCall);
				enableView(textview_State);
				textview_State.setText("Incoming Call");
				playSound(state);
			}else
				if(state == OUTCOMING_STATE){
					disableView(layout_IncomingCall);
					disableView(chronometer);
					enableView(layout_Calling);
					enableView(textview_State);
					playSound(state);
				}else
					if(state == STOP_STATE){
						stopSound();
						stopTimer();
						disableView(button_Accept);
						disableView(button_EndCall);
						disableView(button_Reject);
						disableView(button_Mute);
						disableView(button_Speaker);
					}
	}
	
	private void startTimer(){
		if(chronometer !=null){
			chronometer.setBase(SystemClock.elapsedRealtime());
			chronometer.start();
		}
	}
	private void stopTimer(){
		if(chronometer != null)
			chronometer.stop();
	}
	private void disableView(View view){
		if(view !=null)
			view.setVisibility(View.GONE);
	}
	private void enableView(View view){ 
		if(view!=null)
			view.setVisibility(View.VISIBLE);
	}
	private void initView(){
		chronometer = (Chronometer)findViewById(R.id.activity_call_chronometer);
		imageView_Avt = (ImageView)findViewById(R.id.activity_call_imageview_avt);
		textview_Fullname = (TextView)findViewById(R.id.activity_call_textview_fullname);
		textview_State = (TextView)findViewById(R.id.activity_call_textview_state);
		
		layout_Calling = (RelativeLayout)findViewById(R.id.activity_call_layout_calling);
		button_Speaker = (Button)findViewById(R.id.activity_call_button_speaker);
		button_Mute = (Button)findViewById(R.id.activity_call_button_mutespeaker);
		button_EndCall = (Button)findViewById(R.id.activity_call_button_endcall);
		button_EndCall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stopCall();
			}
		});
		
		layout_IncomingCall = (RelativeLayout)findViewById(R.id.activity_call_layout_incoming_call);
		button_Accept = (Button)findViewById(R.id.activity_call_button_accept);
		button_Accept.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				accept();
			}
		});
		button_Reject = (Button)findViewById(R.id.activity_call_button_reject);
		button_Reject.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				reject();
			}
		});
	}

	private void accept(){
		Log.d(TAG, "accept pressed");
		QBAcceptCallCommand.start(this);
	}
	private void reject(){
		Log.d(TAG, "reject pressed");
		QBRejectCallCommand.start(this);
	}
	private void stopCall(){
		Log.d(TAG, "stop call pressed");
		QBStopCallCommand.start(this);
	}
	
	private void playSound(int state){
		if(mediaPlayerManager!=null){
			if(state==INCOMING_STATE){
				mediaPlayerManager.playDefaultRingtone();
			}else
				if(state == OUTCOMING_STATE){
					mediaPlayerManager.playAssetSound("calling.mp3", true);
				}
		}
	}
	private void stopSound(){
		if(mediaPlayerManager!=null){
			mediaPlayerManager.stop();
		}
	}
	private void finishActivityWithDelay(){
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				finish();
			}
		}, 3000);
	}
	*/
}
