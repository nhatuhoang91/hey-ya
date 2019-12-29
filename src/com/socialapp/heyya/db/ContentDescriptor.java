package com.socialapp.heyya.db;

import com.socialapp.heyya.db.table.FriendTable;
import com.socialapp.heyya.db.table.NotificationTable;
import com.socialapp.heyya.db.table.UserTable;

import android.content.UriMatcher;
import android.net.Uri;

public class ContentDescriptor {
	
	public static final String AUTHORITY = "com.socialapp.heyya.auth";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final UriMatcher URI_MATCHER = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(AUTHORITY, UserTable.PATH, UserTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, FriendTable.PATH, FriendTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, NotificationTable.PATH, NotificationTable.PATH_TOKEN);
        return matcher;
    }
}
