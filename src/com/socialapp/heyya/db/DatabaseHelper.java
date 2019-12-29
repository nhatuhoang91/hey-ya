package com.socialapp.heyya.db;

import java.text.MessageFormat;

import com.socialapp.heyya.db.table.FriendTable;
import com.socialapp.heyya.db.table.NotificationTable;
import com.socialapp.heyya.db.table.UserTable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

	public static final String KEY_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS {0} ({1})";
    public static final String KEY_DROP_TABLE = "DROP TABLE IF EXISTS {0}";
    
    private static final int CURRENT_DB_VERSION = 1;
    private static final String DB_NAME = "heyyadb.db";
    
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, CURRENT_DB_VERSION);
    }
	@Override
	public void onCreate(SQLiteDatabase db) {
		createUserTable(db);
        createFriendTable(db);
        createNotificationTable(db);
	}
	private void createUserTable(SQLiteDatabase db) {
        StringBuilder userTableFields = new StringBuilder();
        userTableFields
                .append(UserTable.Cols.ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(UserTable.Cols.USER_ID).append(" TEXT, ")
                .append(UserTable.Cols.FULL_NAME).append(" TEXT, ")
                .append(UserTable.Cols.LOGIN).append(" TEXT, ")
                .append(UserTable.Cols.AVATAR_URL).append(" TEXT");
        createTable(db, UserTable.TABLE_NAME, userTableFields.toString());
    }
	private void createFriendTable(SQLiteDatabase db) {
        StringBuilder friendTableFields = new StringBuilder();
        friendTableFields
                .append(FriendTable.Cols.ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(FriendTable.Cols.FRIEND_ID).append(" TEXT, ")
                .append(FriendTable.Cols.FULL_NAME).append(" TEXT, ")
                .append(FriendTable.Cols.AVATAR_URL).append(" TEXT, ")
                //.append(FriendTable.Cols.RELATION_STATUS_ID).append(" INTEGER, ")
                .append(FriendTable.Cols.IS_STATUS_ASK).append(" INTEGER, ")
                .append(FriendTable.Cols.IS_REQUESTED_FRIEND).append(" INTEGER, ")
                .append(FriendTable.Cols.IS_ONLINE).append(" INTEGER");
        createTable(db, FriendTable.TABLE_NAME, friendTableFields.toString());
    }
	private void createNotificationTable(SQLiteDatabase db){
		StringBuilder notificationTableFields = new StringBuilder();
		notificationTableFields
				.append(NotificationTable.Cols.ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
				.append(NotificationTable.Cols.SENDER_ID).append(" TEXT, ")
				.append(NotificationTable.Cols.IS_SENDER).append(" INTEGER, ")
				.append(NotificationTable.Cols.IS_READ).append(" INTEGER, ")
				.append(NotificationTable.Cols.STATUS).append(" INTEGER, ")
				.append(NotificationTable.Cols.LATI).append(" TEXT, ")
				.append(NotificationTable.Cols.LONGTI).append(" TEXT, ")
				.append(NotificationTable.Cols.MESSAGE_ID).append(" TEXT, ")
				.append(NotificationTable.Cols.MESSAGE).append(" TEXT, ")
				.append(NotificationTable.Cols.TIME).append(" TEXT, ")
				.append(NotificationTable.Cols.DATE).append(" TEXT");
		createTable(db, NotificationTable.TABLE_NAME, notificationTableFields.toString());
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropTable(db, UserTable.TABLE_NAME);
        dropTable(db, FriendTable.TABLE_NAME);
        dropTable(db, NotificationTable.TABLE_NAME);
        onCreate(db);
	}

	public void dropTable(SQLiteDatabase db, String name) {
        String query = MessageFormat.format(DatabaseHelper.KEY_DROP_TABLE, name);
        db.execSQL(query);
    }

    public void createTable(SQLiteDatabase db, String name, String fields) {
        String query = MessageFormat.format(DatabaseHelper.KEY_CREATE_TABLE, name, fields);
        db.execSQL(query);
    }
}
