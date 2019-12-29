
package com.socialapp.heyya.db;

import com.socialapp.heyya.db.table.FriendTable;
import com.socialapp.heyya.db.table.NotificationTable;
import com.socialapp.heyya.db.table.UserTable;
import com.socialapp.heyya.model.Friend;
import com.socialapp.heyya.model.Notification;
import com.socialapp.heyya.model.User;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class DatabaseManager {

	public static Cursor getAllFriend(Context context) throws Exception{
		ContentResolver resolver = context.getContentResolver();
		
		Cursor cursor = resolver.query(FriendTable.CONTENT_URI, null, null, null, null);
		return cursor;
	}
	
	public static Cursor getFriendByFriendId(Context context, String friendId)throws Exception{
		ContentResolver resolver = context.getContentResolver();
		StringBuilder condition = new StringBuilder();
		condition.append(FriendTable.Cols.FRIEND_ID +" = '"+friendId+"'");
		Cursor cursor = resolver.query(FriendTable.CONTENT_URI, null,
				condition.toString(), null, null);
		return cursor;
	}
	
	public static String getFriendIdById(Context context, long id)throws Exception{
		String friendId;
		ContentResolver resolver = context.getContentResolver();
		StringBuilder condition = new StringBuilder();
		condition.append(FriendTable.Cols.ID +" = '"+id+"'");
		Cursor cursor = resolver.query(FriendTable.CONTENT_URI, null,
				condition.toString(), null, null);
		if(cursor.moveToFirst()){
			friendId = cursor.getString(cursor.getColumnIndex(FriendTable.Cols.FRIEND_ID));
		}else{
			friendId = "";
		}
		
		if(cursor!=null)
			cursor.close();
		return friendId;
	}
	
	public static boolean checkIsFriendById(Context context, long id)throws Exception{
		boolean isFriend = false;
		ContentResolver resolver = context.getContentResolver();
		StringBuilder condition = new StringBuilder();
		condition.append(FriendTable.Cols.ID +" = '"+id+"'");
		Cursor cursor = resolver.query(FriendTable.CONTENT_URI, null,
				condition.toString(), null, null);
		if(cursor.moveToFirst()){
			int is_Asked = cursor.getInt(cursor.getColumnIndex(FriendTable.Cols.IS_STATUS_ASK));
			int is_Requested = cursor.getInt(cursor.getColumnIndex(FriendTable.Cols.IS_REQUESTED_FRIEND));
			if(is_Asked == 0 && is_Requested ==0)
				isFriend = true;
		}
		if(cursor!=null)
			cursor.close();
		return isFriend;
	}
	
	public static String getFriendFullNameById(Context context, int id)throws Exception{
		String fullName = null;
		ContentResolver resolver = context.getContentResolver();
		StringBuilder condition = new StringBuilder();
		condition.append(FriendTable.Cols.ID +" = '"+id+"'");
		Cursor cursor = resolver.query(FriendTable.CONTENT_URI, null,
				condition.toString(), null, null);
		if(cursor.moveToFirst()){
			fullName = cursor.getString(cursor.getColumnIndex(FriendTable.Cols.FULL_NAME));
		}
		
		if(cursor!=null)
			cursor.close();
		return fullName;
	}
	public static String getFriendFullNameByFriendId(Context context, String friendId)throws Exception{
		String fullName = null;
		ContentResolver resolver = context.getContentResolver();
		StringBuilder condition = new StringBuilder();
		condition.append(FriendTable.Cols.FRIEND_ID +" = '"+friendId+"'");
		Cursor cursor = resolver.query(FriendTable.CONTENT_URI, null,
				condition.toString(), null, null);
		if(cursor.moveToFirst()){
			fullName = cursor.getString(cursor.getColumnIndex(FriendTable.Cols.FULL_NAME));
		}
		
		if(cursor!=null)
			cursor.close();
		return fullName;
	}
	
	public static String getAvtUriByFriendId(Context context, String friendId)throws Exception{
		String avtUri = null;
		ContentResolver resolver = context.getContentResolver();
		StringBuilder condition = new StringBuilder();
		condition.append(FriendTable.Cols.FRIEND_ID +" = '"+friendId+"'");
		Cursor cursor = resolver.query(FriendTable.CONTENT_URI, null,
				condition.toString(), null, null);
		if(cursor.moveToFirst()){
			avtUri = cursor.getString(cursor.getColumnIndex(FriendTable.Cols.AVATAR_URL));
		}
		
		if(cursor!=null)
			cursor.close();
		return avtUri;
	}
	
	public static int deleteAllFriend(Context context) throws Exception{
		ContentResolver resolver = context.getContentResolver();
		 int i = resolver.delete(FriendTable.CONTENT_URI, null, null);
		 return i;
	}
	
	public static boolean checkExistAndSaveFriend(Context context, Friend friend) throws Exception{
		boolean result;
		Cursor cursor = getFriendByFriendId(context, friend.getFriendId());
		if(cursor.moveToFirst()){
			result = true;
			updateFriend(context, friend);
		}else{
			saveFriend(context, friend);
			result = false;
		}
		if(cursor!=null)
			cursor.close();
		return result;
	}
	
	public static void updateOrSaveFriend(Context context, Friend friend) throws Exception{
		Cursor cursor = getFriendByFriendId(context, friend.getFriendId());
		if(cursor.moveToFirst()){
			updateFriend(context, friend);
		}else{
			saveFriend(context, friend);
		}
		if(cursor != null){
			cursor.close();
		}
	}
	/*public static Cursor getRequestedAndAskedFriend(Context context){
		ContentResolver resolver = context.getContentResolver();
		StringBuilder condition  = new StringBuilder();
			condition.append(FriendTable.TABLE_NAME +"."+ FriendTable.Cols.IS_REQUESTED_FRIEND)
			.append(" = ")
			.append(" 1 ,")
			.append(" OR ")
			.append(FriendTable.TABLE_NAME+"."+FriendTable.Cols.IS_STATUS_ASK)
			.append(" = ")
			.append("1");
				
		Cursor cursor = resolver.query(FriendTable.CONTENT_URI, null, condition.toString(), null, null);
		return cursor;
	}*/
	
	public static void saveFriend(Context context, Friend friend) throws Exception{
		ContentValues values = new ContentValues();
		values.put(FriendTable.Cols.FRIEND_ID, friend.getFriendId());
		values.put(FriendTable.Cols.FULL_NAME, friend.getFullName());
		values.put(FriendTable.Cols.AVATAR_URL, friend.getUri().toString());
		values.put(FriendTable.Cols.IS_REQUESTED_FRIEND, friend.getIsRequestedFriend());
		values.put(FriendTable.Cols.IS_STATUS_ASK, friend.getIsAskFriend());
		values.put(FriendTable.Cols.IS_ONLINE, friend.getIsOnline());
		ContentResolver resolver = context.getContentResolver();
		Uri result = resolver.insert(FriendTable.CONTENT_URI, values);
		if(result != null)
		{
			Log.d("SAVE_SUCCESS", "save success");
		}else{
			Log.d("SAVE_FAIL", "save fail");
		}
	}
	
	public static boolean saveUser(Context context, User user)throws Exception{
		ContentValues values = new ContentValues();
		values.put(UserTable.Cols.USER_ID, user.getUserId().toString());
		values.put(UserTable.Cols.LOGIN, user.getLogin());
		values.put(UserTable.Cols.FULL_NAME, user.getFullname());
		values.put(UserTable.Cols.AVATAR_URL, user.getAvtUri().toString());
		ContentResolver resolver = context.getContentResolver();
		Uri result = resolver.insert(UserTable.CONTENT_URI, values);
		
		if(result == null){
			return false;
		}
		return true;
	}
	public static int updateUser(Context context, User user)throws Exception{
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(UserTable.Cols.USER_ID, user.getUserId());
		values.put(UserTable.Cols.FULL_NAME, user.getFullname());
		values.put(UserTable.Cols.AVATAR_URL, user.getAvtUri());
		String condition = UserTable.Cols.USER_ID+ "='"+user.getUserId()+"'";
		int i = resolver.update(UserTable.CONTENT_URI, values, condition, null);
		
		return i;
	}
	
	public static Cursor getUser(Context context)throws Exception{
		ContentResolver resolver = context.getContentResolver();
		
		Cursor cursor = resolver.query(UserTable.CONTENT_URI, null, null, null, null);
		return cursor;
	}
	
	public static void deleteUser(Context context)throws Exception{
		ContentResolver resolver = context.getContentResolver();
		resolver.delete(UserTable.CONTENT_URI, null, null);
	}
	
	public static int updateFriend(Context context, Friend friend)throws Exception{
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(FriendTable.Cols.FRIEND_ID, friend.getFriendId());
		values.put(FriendTable.Cols.FULL_NAME, friend.getFullName());
		values.put(FriendTable.Cols.AVATAR_URL, friend.getUri().toString());
		values.put(FriendTable.Cols.IS_REQUESTED_FRIEND, friend.getIsRequestedFriend());
		values.put(FriendTable.Cols.IS_STATUS_ASK, friend.getIsAskFriend());
		values.put(FriendTable.Cols.IS_ONLINE, friend.getIsOnline());
		String condition = FriendTable.Cols.FRIEND_ID + "='" + friend.getFriendId() +"'";
		
		int i = resolver.update(FriendTable.CONTENT_URI, values, condition, null);
		
		return i;
	}
	
	public static int updateFriendFullnameByFriendId(Context context,String friendId, String fullname)
			throws Exception{
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(FriendTable.Cols.FULL_NAME, fullname);
		String condition = FriendTable.Cols.FRIEND_ID + "='" +friendId+"'";
		int i = resolver.update(FriendTable.CONTENT_URI, values, condition, null);
		
		return i;
	}
	
	public static int updateAvtUriByFriendId(Context context,String friendId, String avtUri)
			throws Exception{
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(FriendTable.Cols.AVATAR_URL, avtUri);
		String condition = FriendTable.Cols.FRIEND_ID + "='" +friendId+"'";
		int i = resolver.update(FriendTable.CONTENT_URI, values, condition, null);
		
		return i;
	}
	
	public static int deleteFriendById(Context context, String friendId)throws Exception{
		
		Cursor cursor = getFriendByFriendId(context, friendId);
		if(cursor.moveToFirst()){
			ContentResolver resolver = context.getContentResolver();
			String condition = FriendTable.Cols.FRIEND_ID + " = '" + friendId + "'";
			 int i = resolver.delete(FriendTable.CONTENT_URI, condition, null);
			 return i;
		}
		
		if(cursor!=null)
			cursor.close();
		return -1;
	}
	
	public static void saveNotification(Context context, Notification notification) throws Exception{
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(NotificationTable.Cols.SENDER_ID, notification.getSenderId());
		values.put(NotificationTable.Cols.IS_SENDER, notification.getIsSender());
		values.put(NotificationTable.Cols.IS_READ, notification.getIsRead());
		values.put(NotificationTable.Cols.STATUS, notification.getStatus());
		values.put(NotificationTable.Cols.LATI, notification.getLat().toString());
		values.put(NotificationTable.Cols.LONGTI, notification.getLon().toString());
		values.put(NotificationTable.Cols.MESSAGE, notification.getMessage());
		values.put(NotificationTable.Cols.MESSAGE_ID, notification.getMessageId());
		values.put(NotificationTable.Cols.TIME, notification.getTime());
		values.put(NotificationTable.Cols.DATE, notification.getDate());
		Uri result = resolver.insert(NotificationTable.CONTENT_URI, values);
		if(result != null)
		{
			Log.d("SAVE__NOTIFICATION_SUCCESS", "save success : Uri = "+result.toString());
		}else{
			Log.d("SAVE__NOTIFICATION_FAIL", "save fail");
		}
	}
	
	public static int updateStatusNotification(Context context, String messageId, int status)throws Exception{
		Log.d("UPDATE_STATUS_NOTIFICATION", "updateStatusNotification() . messageId = "+messageId);
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(NotificationTable.Cols.STATUS, status);
		String condition = NotificationTable.Cols.MESSAGE_ID + "='"+messageId+"'";
		
		int i = resolver.update(NotificationTable.CONTENT_URI, values, condition, null);
		Log.d("UPDATE_STATUS_NOTIFICATION", "num record : "+i);
		return i;
	}
	
	public static int updateIsReadNotification(Context context, long id, int isRead)throws Exception{
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(NotificationTable.Cols.IS_READ,isRead);
		String condition = NotificationTable.Cols.ID + "='" +id+"'";
		
		int i = resolver.update(NotificationTable.CONTENT_URI, values, condition, null);
		Log.d("DATABASE_MANAGER", "i : "+i);
		return i;
	}
	
	public static Cursor getNotificationById(Context context, long id)throws Exception{
		ContentResolver resolver = context.getContentResolver();
		StringBuilder condition = new StringBuilder();
		//condition.append(NotificationTable.Cols.ID +" = '1'");
		condition.append(NotificationTable.Cols.ID +" = '"+id+"'");
		Cursor cursor = resolver.query(NotificationTable.CONTENT_URI, null,
				condition.toString(), null, null);
		return cursor;
	}
	
	public static Cursor getNotificationToCheck(Context context, String friendId, String date)throws Exception{
		ContentResolver resolver = context.getContentResolver();
		StringBuilder condition = new StringBuilder();
		//condition.append(NotificationTable.Cols.ID +" = '1'");
		condition.append(NotificationTable.Cols.SENDER_ID +" = '"+friendId+"' AND "
		+NotificationTable.Cols.DATE+"='"+date+"' AND "+NotificationTable.Cols.IS_SENDER+" ='1'");
		Cursor cursor = resolver.query(NotificationTable.CONTENT_URI, null,
				condition.toString(), null, null);
		return cursor;
	}
	
	public static int deleteAllNotifications(Context context) throws Exception{
		ContentResolver resolver = context.getContentResolver();
		 int i = resolver.delete(NotificationTable.CONTENT_URI, null, null);
		 return i;
	}
	public static int deleteNotificationById(Context context, long id)throws Exception{
		ContentResolver resolver = context.getContentResolver();
		String condition = NotificationTable.Cols.ID + " = '" + id + "'";
		 int i = resolver.delete(NotificationTable.CONTENT_URI, condition, null);
		 return i;
	}
	
	public static void clearDatabase(Context context)throws Exception{
		deleteAllFriend(context);
		deleteUser(context);
		deleteAllNotifications(context);
	}
}
