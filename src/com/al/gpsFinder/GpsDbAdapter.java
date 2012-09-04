
package com.al.gpsFinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class GpsDbAdapter {

    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "GpsDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table waypoints (_id integer primary key autoincrement, "
        + "title text not null, desc text not null, coord_lat float not null, coord_lon float not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "waypoints";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS waypoints");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public GpsDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public GpsDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long createPoint(String title, String body, Float coord_lat, Float coord_lon) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);
        initialValues.put("coord_lat", 1.0);
        initialValues.put("coord_lon", 1.0);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deletePoint(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }


    public Cursor fetchAllPoints() {
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_BODY}, null, null, null, null, null);
    }


    public Cursor fetchPoint(long rowId) throws SQLException {

        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_TITLE, KEY_BODY}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }


}
