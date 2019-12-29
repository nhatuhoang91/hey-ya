package com.socialapp.heyya.qb.helper;

import java.util.Collection;

import org.jivesoftware.smack.packet.RosterPacket;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRoster;
import com.quickblox.chat.listeners.QBRosterListener;
import com.quickblox.chat.listeners.QBSubscriptionListener;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.chat.model.QBRosterEntry;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.App;
import com.socialapp.heyya.db.DatabaseManager;
import com.socialapp.heyya.db.table.FriendTable;
import com.socialapp.heyya.model.Friend;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.utils.FriendUtil;
import com.socialapp.heyya.utils.PrefsHelper;

public class QBFriendHelper extends BaseHelper{

	  private QBRoster roster;
	  private FriendUtil friendUtil;
	public QBFriendHelper(Context context) {
		super(context);
		friendUtil = new FriendUtil(context, this);
	}

	public void init() throws Exception{
        roster = QBChatService.getInstance().getRoster(QBRoster.SubscriptionMode.mutual, new SubscriptionListener());
        roster.setSubscriptionMode(QBRoster.SubscriptionMode.mutual);
        roster.addRosterListener(new RosterListener());
        if(App.getInstance().getPrefsHelper().getBoolean(PrefsHelper.PREF_IS_LOGINING, false)){
    		initFriendWhenLogin(roster);
    	}
    }
	/*this function call when login first time success to load friend on roster and show in main activity*/
	private void initFriendWhenLogin(QBRoster roster)throws Exception{
		Log.d("INIT_FRIEND_LOGIN","///-------------------BEGIN_INIT_FRIEND--------------------------///");
		Log.d("INIT_FRIEND_LOGIN","///-------------------//--------------------------///");
		Log.d("INIT_FRIEND_LOGIN","///-------------------//--------------------------///");
		Friend friend ;
		Collection<QBRosterEntry> entries = roster.getEntries();
		for(QBRosterEntry rosterEntry : entries){
			Log.d("CREATE_FRIEND","///-------------------BEGIN_CREATE_FRIEND--------------------------///");
			Log.d("CREATE_FRIEND","///-------------------//--------------------------///");
			Log.d("CREATE_FRIEND","///-------------------//--------------------------///");
			friend = friendUtil.createFriendFromRosterEntry(rosterEntry);
			if(friend != null){
				//DatabaseManager.saveFriend(context, friend);
				DatabaseManager.checkExistAndSaveFriend(context, friend);
			}else{
				Log.d("SKIP_THIS_FRIEND","skip this friend");
			}
			Log.d("CREATE_FRIEND","//-------------------END_CREATE_FRIEND------------------//");
			Log.d("CREATE_FRIEND","//-------------------------------------//");
			Log.d("CREATE_FRIEND","//-------------------------------------//");
		}
		Log.d("INIT_FRIEND_LOGIN","//-------------------END_INIT_FRIEND------------------//");
		Log.d("INIT_FRIEND_LOGIN","//-------------------------------------//");
		Log.d("INIT_FRIEND_LOGIN","//-------------------------------------//");
	}
	
	/*this function was called when user press invite button in invite fragment*/
	public void addFriend(QBUser user)throws Exception{
		try{
			Log.d("ADD_FRIEND","///-------------------BEGIN--------------------------///");
			Log.d("ADD_FRIEND","///-------------------//--------------------------///");
			Log.d("ADD_FRIEND","///-------------------//--------------------------///");
		if(roster.contains(user.getId())){
			roster.subscribe(user.getId());
			Log.d("ADD_FRIEND","contain");
		}else
		{
			roster.createEntry(user.getId(), null);
			roster.subscribe(user.getId());
			Log.d("ADD_FRIEND","don't contain");
		}
		//Friend friend = friendUtil.createFriend(user, 1, 0, 0);
		//DatabaseManager.saveFriend(context, friend);
		
		Log.d("ADD_FRIEND","//-------------------END------------------//");
		Log.d("ADD_FRIEND","//-------------------------------------//");
		Log.d("ADD_FRIEND","//-------------------------------------//");
		}catch(Exception e){
			Intent i = new Intent(QBServiceConsts.EXTRA_UNEXPECTED_ERROR);
			i.putExtra(QBServiceConsts.EXTRA_ERROR, "error at addFriend() method. " +
					"at QBFriendHelper class. detail :"+e.toString());
			LocalBroadcastManager.getInstance(context).sendBroadcast(i);
			throw e;
		}
	}

	public void acceptFriend(String userId) throws Exception{
		try{
		Log.d("ACCEPT_FRIEND", "///--------------------BEGIN-------------------------------///");
		Log.d("ACCEPT_FRIEND", "///--------------------//----------------------------------///");
		Log.d("ACCEPT_FRIEND", "///--------------------//----------------------------------///");
		roster.confirmSubscription(Integer.valueOf(userId));
		Log.d("ACCEPT_FRIEND", "//---------------END-------------------//");
		Log.d("ACCEPT_FRIEND", "//------------------------------------//");
		Log.d("ACCEPT_FRIEND", "//------------------------------------//");
		}catch(Exception e){
			Intent i = new Intent(QBServiceConsts.EXTRA_UNEXPECTED_ERROR);
			i.putExtra(QBServiceConsts.EXTRA_ERROR, "error at acceptFriend() method. " +
					"at QBFriendHelper class. detail :"+e.toString());
			LocalBroadcastManager.getInstance(context).sendBroadcast(i);
			throw e;
		}
	}
	public void ignoreFriend(String friendId) throws Exception{
		try{
		Log.d("IGNORE_FRIEND", "///--------------------BEGIN-------------------------------///");
		Log.d("IGNORE_FRIEND", "///--------------------//----------------------------------///");
		Log.d("IGNORE_FRIEND", "///--------------------//----------------------------------///");
		roster.reject(Integer.valueOf(friendId));
		removeEntry(Integer.valueOf(friendId));
		DatabaseManager.deleteFriendById(context, friendId);
		Log.d("IGNORE_FRIEND", "//---------------END-------------------//");
		Log.d("IGNORE_FRIEND", "//------------------------------------//");
		Log.d("IGNORE_FRIEND", "//------------------------------------//");
		}catch(Exception e){
			Intent i = new Intent(QBServiceConsts.EXTRA_UNEXPECTED_ERROR);
			i.putExtra(QBServiceConsts.EXTRA_ERROR, "error at ignoreFriend() method. " +
					"at QBFriendHelper class. detail :"+e.toString());
			LocalBroadcastManager.getInstance(context).sendBroadcast(i);
			throw e;
		}
	}
	
	public void deleteFriend(String friendId) throws Exception {
		try{
			Log.d("IGNORE_FRIEND", "///--------------------BEGIN-------------------------------///");
			Log.d("IGNORE_FRIEND", "///--------------------//----------------------------------///");
			Log.d("IGNORE_FRIEND", "///--------------------//----------------------------------///");
			roster.unsubscribe(Integer.valueOf(friendId));
			removeEntry(Integer.valueOf(friendId));
			Log.d("IGNORE_FRIEND", "//---------------END-------------------//");
			Log.d("IGNORE_FRIEND", "//------------------------------------//");
			Log.d("IGNORE_FRIEND", "//------------------------------------//");
			DatabaseManager.deleteFriendById(context, friendId);
		}catch(Exception e){
			Intent i = new Intent(QBServiceConsts.EXTRA_UNEXPECTED_ERROR);
			i.putExtra(QBServiceConsts.EXTRA_ERROR, "error at deleteFriend() method. " +
					"at QBFriendHelper class. detail :"+e.toString());
			LocalBroadcastManager.getInstance(context).sendBroadcast(i);
			throw e;
		}
    }
	private void removeEntry(int userId)throws Exception{
		QBRosterEntry entry = roster.getEntry(userId);
		if(entry!=null && roster.contains(userId)){
			Log.d("QBFRIEND_HELPER", "removeEntry(). entry != null && roster.contains(userId)");
			roster.removeEntry(entry);
		}
	}
	public boolean isNotInvite(String userId)throws Exception{
		return !isInvite(userId);
	}
	public boolean isInvite(String friendId)throws Exception{
		Cursor cursor = DatabaseManager.getFriendByFriendId(context, friendId);
		if(!cursor.moveToFirst()){
			if(cursor!=null)
				cursor.close();
			return false;
		}
		if(cursor!=null)
			cursor.close();
		return true;
	}
	/*this funtion check whether this friendId sent friend request to user*/
	public boolean isAsked(String friendId)throws Exception{
		Cursor cursor = DatabaseManager.getFriendByFriendId(context, friendId);
		if(!cursor.moveToFirst()){
			return false;
		}
		int isAsked = cursor.getInt(cursor.getColumnIndex(FriendTable.Cols.IS_STATUS_ASK));
		if(isAsked != 1){
			return false;
		}
		if(cursor!=null)
			cursor.close();
		return true;
	}
	
	public boolean isRequestSent(QBRosterEntry rosterEntry)throws Exception{
		if(isTypeNone(rosterEntry) && isStateSubcribe(rosterEntry))
		{
			return true;
		}else{
			return false;
		}
	}
	public boolean isFriend(QBRosterEntry rosterEntry)throws Exception{
		boolean isTypeTo = isTypeTo(rosterEntry);
		boolean isTypeFrom = isTypeFrom(rosterEntry);
		boolean isTypeBoth = isTypeBoth(rosterEntry);
		boolean isStateNull = isStateNull(rosterEntry);
		boolean isStateSubcribe = isStateSubcribe(rosterEntry);
		
		if(isTypeTo && isStateNull || isTypeBoth && isStateNull || isTypeFrom && isStateSubcribe){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isStateSubcriptionPending(QBRosterEntry rosterEntry)throws Exception{
		boolean isSubcriptionPending = rosterEntry.getStatus() == RosterPacket.ItemStatus.SUBSCRIPTION_PENDING;
		return isSubcriptionPending;
	}
	public boolean isStateSubcribe(QBRosterEntry rosterEntry)throws Exception{
		boolean isSubcribe = rosterEntry.getStatus() == RosterPacket.ItemStatus.subscribe;
		return isSubcribe;
	}
	public boolean isStateNull (QBRosterEntry rosterEntry)throws Exception{
		boolean isStateNull = rosterEntry.getStatus() == null;
		return isStateNull;
	}
	public boolean isTypeBoth(QBRosterEntry rosterEntry)throws Exception{
		boolean isBoth = rosterEntry.getType() == RosterPacket.ItemType.both;
		return isBoth;
	}
	public boolean isTypeFrom(QBRosterEntry rosterEntry)throws Exception{
		boolean isFrom = rosterEntry.getType() == RosterPacket.ItemType.from;
		return isFrom;
	}
	public boolean isTypeTo(QBRosterEntry rosterEntry)throws Exception{
		boolean isTo = rosterEntry.getType() == RosterPacket.ItemType.to;
		return isTo;
	}
	public boolean isTypeNone(QBRosterEntry rosterEntry)throws Exception{
		boolean isNone = rosterEntry.getType() == RosterPacket.ItemType.none;
		return isNone;
	}
	public boolean isOnline(int userId)throws Exception{
		QBPresence presence = roster.getPresence(userId);
		boolean isOnline = presence.getType() == QBPresence.Type.online;
		return isOnline;
	}
	
	 private class SubscriptionListener implements QBSubscriptionListener {

	        @Override
	        public void subscriptionRequested(int userId) {
	        	try{
	        		Log.d("SUBCRIPTION_LISTENER", "//-------------------------BEGIN------------------------//");
	        		Log.d("SUBCRIPTION_LISTENER", "//-------------------------///------------------------//");
	        		Log.d("SUBCRIPTION_LISTENER", "//-------------------------///------------------------//");
	        		Log.d("SUBCRIPTION_LISTENER", "received subcription");
	        		//if(roster!=null)
	        			//roster.reload();
	        		
	        		QBUser user = QBUsers.getUser(userId);
	        		Log.d("SUBCRIPTION_LISTENER", "name : " + user.getFullName());
	        		Friend friend = friendUtil.createFriend(user, 0, 1, 0);
	        		boolean isExist = DatabaseManager.checkExistAndSaveFriend(context, friend);
	        		Log.d("SUBCRIPTION_LISTENER", "//-----------------END------------------//");
	        		Log.d("SUBCRIPTION_LISTENER", "//-----------------------------------//");
	        		Log.d("SUBCRIPTION_LISTENER", "//-----------------------------------//");
	        		Log.d("SUBCRIPTION_LISTENER", "is exist : "+isExist);
	        		if(App.getInstance().getPrefsHelper().getBoolean(PrefsHelper.PREF_IS_LOGINING, false)){
	            		/* only use this scope when login*/
	        			Log.d("SUBCRIPTION_LISTENER", "pref_is_loging = true");
	        			Intent intent = new Intent("com.socialapp.heyya.friend.request.action");
            			context.sendBroadcast(intent);
	            	}else
	            		//check friend wheather added to database;
	            		if(!isExist){
	            			Log.d("SUBCRIPTION_LISTENER", "isExist = false (!isExist)");
	            			Intent intent = new Intent("com.socialapp.heyya.friend.request.action");
	            			intent.putExtra(QBServiceConsts.EXTRA_FRIEND_NAME, user.getFullName());
	            			context.sendBroadcast(intent);
	            		}
	        	}catch(Exception e){
	        		Intent i = new Intent(QBServiceConsts.EXTRA_UNEXPECTED_ERROR);
	    			i.putExtra(QBServiceConsts.EXTRA_ERROR, "error at subcriptionRequested() method. " +
	    					"at SubcriptionListener class. at QBFriendHelper class. detail :"+e.toString());
	    			LocalBroadcastManager.getInstance(context).sendBroadcast(i);
		        }
	        }
	    }
	 private class RosterListener implements QBRosterListener{
		@Override
		public void entriesAdded(Collection<Integer> arg0) {
			Log.d("ENTRIES_ADD", "entries add");
		}
		@Override
		public void entriesDeleted(Collection<Integer> friendsId) {
			Log.d("ENTRIES_DELETE", "entries delete");
			try{
			for(Integer i : friendsId){
				Log.d("ENTRIES_DELETE", "delete friend in database");
				DatabaseManager.deleteFriendById(context, i.toString());
			}
			}catch(Exception e){
				Intent i = new Intent(QBServiceConsts.EXTRA_UNEXPECTED_ERROR);
    			i.putExtra(QBServiceConsts.EXTRA_ERROR, "error at entriesDelete() method. " +
    					"at RosterListener class. at QBFriendHelper class. detail :"+e.toString());
    			LocalBroadcastManager.getInstance(context).sendBroadcast(i);
			}
		}
		@Override
		public void entriesUpdated(Collection<Integer> arg0) {
			Log.d("ENTRIES_UPDATE","//-----------------------BEGIN-------------efwsefwe---------//");
			if(App.getInstance().getPrefsHelper().getBoolean(PrefsHelper.PREF_IS_LOGINING, false)){
				Log.d("ENTRIES_UPDATE","pref is logging = true");
        		return;
        	}
			Log.d("ENTRIES_UPDATE","pref is logging = false");
			try{
				Log.d("ENTRIES_UPDATE","//-----------------------BEGIN----------------------//");
				Log.d("ENTRIES_UPDATE","//-----------------------///----------------------//");
				Log.d("ENTRIES_UPDATE","//-----------------------///----------------------//");
				Log.d("ENTRIES_UPDATE"," size : "+arg0.size());
				Log.d("ENTRIES_UPDATE", "entries update");
				updateFriendList(arg0);
				Log.d("ENTRIES_UPDATE","//------------END------------//");
				Log.d("ENTRIES_UPDATE","//------------------------//");
				Log.d("ENTRIES_UPDATE","//------------------------//");
				//notify for user know, friend accepted
			}catch(Exception e){
				Log.e("UPDATE_ROSTER", "update roster fail : "+ e.toString());
				Intent intent = new Intent(QBServiceConsts.EXTRA_UNEXPECTED_ERROR);
				intent.putExtra(QBServiceConsts.EXTRA_ERROR, "Error : at entriesUpdated method." +
						"at QBFriendHelper class. "+"detail : " + e.toString());
				LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
			}
		}

		@Override
		public void presenceChanged(QBPresence presence) {
		/*	try{
				boolean isChanged = false;
				QBUser qbUser = QBUsers.getUser(presence.getUserId());
				Cursor cursor = DatabaseManager.getFriendByFriendId(context, presence.getUserId().toString());
				if(!cursor.moveToFirst()){
					return;
				}
				Friend friend = friendUtil.createFriendFromCursor(cursor);
				Log.d("PRESENCE_CHANGE", "presence change");
				Log.d("PRESENCE_CHANGE", "friend name : " + friend.getFullName());
				if(!qbUser.getFullName().equals(friend.getFullName())){
					friend.setFullName(qbUser.getFullName());
					if(!isChanged)
						isChanged=true;
				}
				if(qbUser.getCustomData()!=null){
					if(!qbUser.getCustomData().equals(friend.getUri())){
						friend.setUri(qbUser.getCustomData());
						if(!isChanged)
							isChanged=true;
					}
				}
				if(QBPresence.Type.online.equals(presence.getType())){
					if(friend.getIsOnline() != 1){
						friend.setIsOnline(1);
						if(!isChanged)
							isChanged=true;
					}
				}else{
					if(friend.getIsOnline() != 0){
						friend.setIsOnline(0);
						if(!isChanged)
							isChanged=true;
					}
				}
				if(isChanged)
					DatabaseManager.updateFriend(context, friend);
				if(cursor!=null && !cursor.isClosed()){
					cursor.close();
				}
			}catch(Exception e){
				e.printStackTrace();
				Intent i = new Intent(QBServiceConsts.EXTRA_UNEXPECTED_ERROR);
    			i.putExtra(QBServiceConsts.EXTRA_ERROR, "error at presentChanges() method. " +
    					"at RosterListener class. at QBFriendHelper class. detail :"+e.toString());
    			LocalBroadcastManager.getInstance(context).sendBroadcast(i);
			}*/
		}
	 }
	 
	public QBUser getFriendFromLogin(String login)throws QBResponseException{
		 QBUser user;
		 try{
			 user = QBUsers.getUserByLogin(login);
		 }catch(QBResponseException e){
			throw e;
		 }
		 return user;
	 }
	 
	 private void updateFriendList(Collection<Integer> friends) throws Exception{
		 for(Integer friendId : friends){
			 updateFriend(friendId);
		 }
	 }
	 private void updateFriend(int friendId){
		 try{
		QBRosterEntry rosterEntry = roster.getEntry(friendId);
		Friend friend = friendUtil.createFriendFromRosterEntry(rosterEntry);
		 if(friend == null){
			 Log.d("ENTRIED_UPDATE", "Friend request is Ignored");
			// DatabaseManager.deleteFriendById(context, friendId);
			 roster.removeEntry(rosterEntry);
		 }else{
			 Log.d("ENTRIED_UPDATE OR SAVE FRIEND", "name : "+friend.getFullName());
			 DatabaseManager.updateOrSaveFriend(context, friend);
		 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	 
		/*Log.d("UPDATE_FRIEND", "type : "+rosterEntry.getType());
		if(rosterEntry.getStatus()==null){
			Log.d("UPDATE_FRIEND", "status : null");
		}else{
			Log.d("UPDATE_FRIEND", "status : "+rosterEntry.getStatus());
		}*/
	 } 
}
