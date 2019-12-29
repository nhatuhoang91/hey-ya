package com.socialapp.heyya.utils;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.quickblox.chat.model.QBRosterEntry;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.R;
import com.socialapp.heyya.db.table.FriendTable;
import com.socialapp.heyya.model.Friend;
import com.socialapp.heyya.qb.helper.QBFriendHelper;

public class FriendUtil {
	Context context;
	QBFriendHelper friendHelper;
	public FriendUtil(Context context){
		this.context = context;
		friendHelper = null;
	}
	public FriendUtil(Context context, QBFriendHelper friendHelper){
		this.context = context;
		this.friendHelper = friendHelper;
	}
	public Friend createFriend(QBUser user, int isRequested, int isAsked, int isOnline){
		String defaultAvt = new String("android.resource://"+ context.getPackageName() + "/drawable/placeholder_user");
		Friend friend = new Friend();
		
		friend.setFriendId(user.getId().toString());
		friend.setFullName(user.getFullName());
		
		friend.setIsRequestedFriend(isRequested);
		friend.setIsAskFriend(isAsked);
		friend.setIsOnline(isOnline);
		friend.setUri(defaultAvt);
		return friend;
	}
	
	public Friend createFriendFromRosterEntry(QBRosterEntry rosterEntry){
		String defaultAvt = new String("android.resource://"+ context.getPackageName() + "/drawable/placeholder_user");
		//String defaultAvt = new String("drawable://"+ R.drawable.placeholder_user);
		String avt_uri;
		if(friendHelper == null){
			return null;
		}
		Friend friend = new Friend();
		try{
			Log.d("FRIEND_UTIL", "///--------------------BEGIN---------------------------///");
			Log.d("FRIEND_UTIL", "///----------------------------------------------------///");
			Log.d("FRIEND_UTIL", "///----------------------------------------------------///");
			Log.d("FRIEND_UTIL_CREATE_FR_ROSTER", "start create friend");
			if(rosterEntry == null){
				Log.d("FRIEND_UTIL_CREATE_FR_ROSTER", "rosterEntry = null");
			}
		QBUser user = QBUsers.getUser(rosterEntry.getUserId());
		if(user == null){
			Log.d("FRIEND_UTIL_CREATE_FR_ROSTER", "user = null");
		}
		friend.setFriendId(user.getId().toString());
		friend.setFullName(user.getFullName());
		avt_uri = user.getCustomData(); // get avt url
		boolean isStateSubcribe = friendHelper.isStateSubcribe(rosterEntry);
		boolean isStateNull = friendHelper.isStateNull(rosterEntry);
		boolean isTypeNone = friendHelper.isTypeNone(rosterEntry);
		boolean isTypeTo = friendHelper.isTypeTo(rosterEntry);
		boolean isTypeFrom = friendHelper.isTypeFrom(rosterEntry);
		boolean isTypeBoth = friendHelper.isTypeBoth(rosterEntry);
		//boolean isBoth = friendHelper.isTypeBoth(rosterEntry);
		boolean isOnline = friendHelper.isOnline(user.getId());
		
		if(isTypeNone && isStateNull){
			//skip this friend
			return null;
		}
		if(avt_uri!=null)
			friend.setUri(avt_uri);
		else{
			friend.setUri(defaultAvt);
		}
		if(isOnline){
			friend.setIsOnline(1);
			Log.d("CREATE_FR_FROM_ENTRY", "online");
		}else
		{
			friend.setIsOnline(0);
			Log.d("CREATE_FR_FROM_ENTRY", "offline");
		}
		if(isTypeNone && isStateSubcribe){
			friend.setIsRequestedFriend(1);
			friend.setIsAskFriend(0);
			Log.d("CREATE_FR_FROM_ENTRY", "Request is sent");
			//return friend;
		}else
			if(isTypeTo && isStateNull || isTypeBoth && isStateNull || isTypeFrom && isStateSubcribe
					|| isTypeFrom && isStateNull){
			friend.setIsRequestedFriend(0);
			friend.setIsAskFriend(0);
			Log.d("CREATE_FR_FROM_ENTRY", "both");
			//return friend;
		}//else{
			//friend.setIsRequestedFriend(0);
			//friend.setIsAskFriend(1);
			//Log.d("CREATE_FR_FROM_ENTRY", "asked");
			//}
		}catch(Exception e){
			Log.e("FRIEND_UTIL_ERROR", e.toString());
			e.printStackTrace();
		}
		Log.d("FRIEND_UTIL", "//-------------------END------------------//");
		Log.d("FRIEND_UTIL", "//----------------------------------------//");
		Log.d("FRIEND_UTIL", "//----------------------------------------//");
		return friend;
	}
	
	public Friend createFriendFromCursor(Cursor cursor){
		Friend friend = new Friend();
		
		String avt_Uri = cursor.getString(cursor.getColumnIndex(FriendTable.Cols.AVATAR_URL));
		String full_Name = cursor.getString(cursor.getColumnIndex(FriendTable.Cols.FULL_NAME));
		String friend_Id = cursor.getString(cursor.getColumnIndex(FriendTable.Cols.FRIEND_ID));
		int is_Requested_friend = cursor.getInt(cursor.getColumnIndex(FriendTable.Cols.IS_REQUESTED_FRIEND));
		int is_Asked = cursor.getInt(cursor.getColumnIndex(FriendTable.Cols.IS_STATUS_ASK));
		int is_Online = cursor.getInt(cursor.getColumnIndex(FriendTable.Cols.IS_ONLINE));
		
		friend.setFriendId(friend_Id);
		friend.setFullName(full_Name);
		friend.setUri(avt_Uri);
		friend.setIsRequestedFriend(is_Requested_friend);
		friend.setIsAskFriend(is_Asked);
		friend.setIsOnline(is_Online);
		if(cursor!=null && !cursor.isClosed())
			cursor.close();
		return friend;
	}
}
