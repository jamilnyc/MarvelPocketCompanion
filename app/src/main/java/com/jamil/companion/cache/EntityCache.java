package com.jamil.companion.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jamil.companion.cache.model.Response;

/**
 * Caching class for caching responses from the API.
 */

public class EntityCache extends SQLiteOpenHelper {
    public static final String TAG = EntityCache.class.getSimpleName();

    protected static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "cache_manager";
    protected static String TABLE_ENTITY_RESPONSE = "cached_response";

    protected static final String KEY_ID = "response_id";
    protected static String KEY_REQUEST_HASH = "request_hash";
    protected static final String KEY_RESPONSE_BODY = "response_body";
    protected static final String KEY_MODIFIED_DATE = "modified_date";

    SQLiteDatabase theDatabase;

    public EntityCache(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        theDatabase = getWritableDatabase(); // TODO: Remove
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating DB now");
        String createStatement = "CREATE TABLE " + TABLE_ENTITY_RESPONSE + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_REQUEST_HASH + " VARCHAR(32) UNIQUE NOT NULL, "
                + KEY_RESPONSE_BODY + " TEXT, "
                + KEY_MODIFIED_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL "
                + ")";
        Log.d(TAG, createStatement);
        db.execSQL(createStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropStatement = "DROP TABLE IF EXISTS " + TABLE_ENTITY_RESPONSE;
        db.execSQL(dropStatement);

        onCreate(db);
    }

    // Add a new response
    public long addResponse (Response response)
    {
        Log.d(TAG, "Attempting to add response to cache");

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_REQUEST_HASH, response.getRequestIdentifier());
        values.put(KEY_RESPONSE_BODY, response.getResponseBody());

        long id = db.insert(TABLE_ENTITY_RESPONSE, null, values);
        Log.d(TAG, "Response added with ID: " + id);

        db.close();
        return id;
    }

    public Response getResponse(String requestIdentifier)
    {
        Log.d(TAG, "Attempting to read from cache with identifier: " + requestIdentifier);
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {KEY_ID, KEY_REQUEST_HASH, KEY_RESPONSE_BODY, KEY_MODIFIED_DATE};
        String whereClause = KEY_REQUEST_HASH + " = ?";
        String[] whereArgs = {requestIdentifier};
        String orderBy = KEY_MODIFIED_DATE + " DESC";
        String limit = "1";
        Cursor cursor = db.query(false, TABLE_ENTITY_RESPONSE, columns, whereClause, whereArgs, null, null, orderBy, limit);

        Response response = null;

        if (cursor != null && cursor.moveToFirst()) {
            Log.d(TAG, "Found cache entry, constructing cache model");
            response = new Response(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            );
            Log.d(TAG, "Response modified time: " + cursor.getString(3));
        }
        cursor.close();

        return response;
    }

    public int clearOldResponses()
    {
        Log.d(TAG, "Attempting to delete from cache");
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = KEY_MODIFIED_DATE + " < datetime('now', '-1 week')";
        String[] whereArgs = {};
        int numAffected = db.delete(TABLE_ENTITY_RESPONSE, whereClause, whereArgs);
        Log.d(TAG, "Number of rows affected: " + numAffected);
        return numAffected;
    }

    public int clearAll()
    {
        Log.d(TAG, "Attempting to delete from cache");
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "1=1"; // Always true
        String[] whereArgs = {};
        int numAffected = db.delete(TABLE_ENTITY_RESPONSE, whereClause, whereArgs);
        Log.d(TAG, "Number of rows affected: " + numAffected);
        return numAffected;
    }
}
