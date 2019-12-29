package com.socialapp.heyya.db.table;

import android.net.Uri;

import com.socialapp.heyya.db.ContentDescriptor;

public class NotificationTable {

	public static final String TABLE_NAME = "notification";

    public static final String PATH = "notification_table";
    public static final int PATH_TOKEN = 30;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();
    
    public static class Cols {
        public static final String ID = "_id";
        public static final String SENDER_ID = "sender_id";
        public static final String IS_SENDER = "is_sender";
        public static final String IS_READ = "is_read";
        public static final String STATUS = "status";
        public static final String LATI = "latitude";
        public static final String LONGTI = "longtitude";
        public static final String MESSAGE_ID = "message_id";
        public static final String MESSAGE = "message";
        public static final String TIME = "time";
        public static final String DATE = "date";
    }
}
