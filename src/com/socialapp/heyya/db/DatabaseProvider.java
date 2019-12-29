package com.socialapp.heyya.db;

import com.socialapp.heyya.db.table.FriendTable;
import com.socialapp.heyya.db.table.NotificationTable;
import com.socialapp.heyya.db.table.UserTable;
import com.socialapp.heyya.utils.Consts;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class DatabaseProvider extends ContentProvider{

	private static final String UNKNOWN_URI = "Unknown URI ";

    private DatabaseHelper dbHelper;

	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
        dbHelper.getWritableDatabase();
        return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
        final int token = ContentDescriptor.URI_MATCHER.match(uri);

        Cursor result = null;

        switch (token) {
        case UserTable.PATH_TOKEN: {
            result = doQuery(db, uri, UserTable.TABLE_NAME, projection, selection, selectionArgs,
                    sortOrder);
            break;
        }
        case FriendTable.PATH_TOKEN: {
            result = doQuery(db, uri, FriendTable.TABLE_NAME, projection, selection, selectionArgs,
                    sortOrder);
            break;
        }
        case NotificationTable.PATH_TOKEN: {
            result = doQuery(db, uri, NotificationTable.TABLE_NAME, projection, selection, selectionArgs,
                    sortOrder);
            break;
        }
        }
        return result;
	}
    private Cursor doQuery(SQLiteDatabase db, Uri uri, String tableName, String[] projection,
            String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(tableName);
        Cursor result = builder.query(db, projection, selection, selectionArgs, sortOrder, null, null);

        result.setNotificationUri(getContext().getContentResolver(), uri);

        return result;
    }
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
        int token = ContentDescriptor.URI_MATCHER.match(uri);

        Uri result = null;
        switch (token) {
        	case UserTable.PATH_TOKEN: {
        		result = doInsert(db, UserTable.TABLE_NAME, UserTable.CONTENT_URI, uri, values);
        		break;
        	}
        	case FriendTable.PATH_TOKEN: {
        		result = doInsert(db, FriendTable.TABLE_NAME, FriendTable.CONTENT_URI, uri, values);
        		break;
        	}
        	case NotificationTable.PATH_TOKEN: {
        		result = doInsert(db, NotificationTable.TABLE_NAME, NotificationTable.CONTENT_URI, uri, values);
        		break;
        	}
        }
        if (result == null) {
            throw new IllegalArgumentException(UNKNOWN_URI + uri);
        }

        return result;
	}

	private Uri doInsert(SQLiteDatabase db, String tableName, Uri contentUri, Uri uri, ContentValues values) {
        long id = db.insert(tableName, null, values);
        Uri result = contentUri.buildUpon().appendPath(String.valueOf(id)).build();
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }
	
	@Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        String table = null;
        int token = ContentDescriptor.URI_MATCHER.match(uri);

        switch (token) {
            case UserTable.PATH_TOKEN: {
                table = UserTable.TABLE_NAME;
                break;
            }
            case FriendTable.PATH_TOKEN: {
                table = FriendTable.TABLE_NAME;
                break;
            }
            case NotificationTable.PATH_TOKEN: {
                table = NotificationTable.TABLE_NAME;
                break;
            }
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();

        for (ContentValues cv : values) {
            db.insert(table, null, cv);
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        return values.length;
	}
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		 SQLiteDatabase db = dbHelper.getWritableDatabase();
	        int token = ContentDescriptor.URI_MATCHER.match(uri);

	        int result = Consts.ZERO_INT_VALUE;

	        switch (token) {
	            case UserTable.PATH_TOKEN: {
	                result = doDelete(db, uri, UserTable.TABLE_NAME, selection, selectionArgs);
	                break;
	            }
	            case FriendTable.PATH_TOKEN: {
	                result = doDelete(db, uri, FriendTable.TABLE_NAME, selection, selectionArgs);
	                break;
	            }
	            case NotificationTable.PATH_TOKEN: {
	                result = doDelete(db, uri, NotificationTable.TABLE_NAME, selection, selectionArgs);
	                break;
	            }
	        }
	        return result;
	}
	private int doDelete(SQLiteDatabase db, Uri uri, String tableName, String selection,
            String[] selectionArgs) {
        int result = db.delete(tableName, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
        int token = ContentDescriptor.URI_MATCHER.match(uri);

        int result = Consts.ZERO_INT_VALUE;

        switch (token) {
            case UserTable.PATH_TOKEN: {
                result = doUpdate(db, uri, UserTable.TABLE_NAME, selection, selectionArgs, values);
                break;
            }
            case FriendTable.PATH_TOKEN: {
                result = doUpdate(db, uri, FriendTable.TABLE_NAME, selection, selectionArgs, values);
                break;
            }
            case NotificationTable.PATH_TOKEN: {
                result = doUpdate(db, uri, NotificationTable.TABLE_NAME, selection, selectionArgs, values);
                break;
            }
        }
        return result;
	}
	private int doUpdate(SQLiteDatabase db, Uri uri, String tableName, String selection,
            String[] selectionArgs, ContentValues values) {
        int result = db.update(tableName, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

}
