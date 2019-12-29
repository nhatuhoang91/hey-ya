package com.socialapp.heyya.qb.command;

public class QBCallCommand{
/*extends ServiceCommand{

	private QBVoiceCallHelper voiceCallHelper;
	public QBCallCommand(Context context,QBVoiceCallHelper voiceCallHelper, String successAction,
			String failAction) {
		super(context, successAction, failAction);
		// TODO Auto-generated constructor stub
		this.voiceCallHelper = voiceCallHelper;
	}

	public static void start(Context context, String friendId) {
        Intent intent = new Intent(QBServiceConsts.CALL_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_FRIEND_ID, friendId);
        context.startService(intent);
	}
	
	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		String friendId = extras.getString(QBServiceConsts.EXTRA_FRIEND_ID);
		ArrayList<Integer> friendIds = new ArrayList<Integer>();
		friendIds.add(Integer.valueOf(friendId));
		
		this.voiceCallHelper.call(friendIds);
		return extras;
	}
*/
}
