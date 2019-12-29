package com.socialapp.heyya.db.table;

import com.socialapp.heyya.db.ContentDescriptor;
import android.net.Uri;

public class FriendTable {

	public static final String TABLE_NAME = "friend";
    public static final String PATH = "friend_table";
    public static final int PATH_TOKEN = 20;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

    public static class Cols {
        public static final String ID = "_id";
        public static final String FRIEND_ID = "user_id";
        public static final String FULL_NAME = "full_name";
        public static final String AVATAR_URL = "avatar_url";
       // public static final String RELATION_STATUS_ID = "relation_status_id";
        public static final String IS_STATUS_ASK = "is_status_ask";
        public static final String IS_REQUESTED_FRIEND = "is_requested_friend";
        public static final String IS_ONLINE = "is_online";
    }
}
