package com.socialapp.heyya.db.table;

import com.socialapp.heyya.db.ContentDescriptor;

import android.net.Uri;


public class UserTable {
	
	public static final String TABLE_NAME = "user";

    public static final String PATH = "user_table";
    public static final int PATH_TOKEN = 10;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();
    
    public static class Cols {
        public static final String ID = "_id";
        public static final String USER_ID = "user_id";
        public static final String LOGIN = "login";
        public static final String FULL_NAME = "full_name";
        public static final String AVATAR_URL = "avatar_url";
    }
}
