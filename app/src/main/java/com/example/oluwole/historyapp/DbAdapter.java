package com.example.oluwole.historyapp;

/**
 * Created by Oluwole on 30/12/2015.
 */


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

import com.google.android.gms.maps.model.LatLng;

public class DbAdapter {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = DbAdapter.class.getSimpleName();

    private Context context;
    public  SQLiteDatabase database;
    public  DatabaseHelper dbHelper;


    // Database fields
    private static String DATABASE_TABLE = "";//it will be the country and city's name
    public static final String KEY_LOCATIONID = "_id";
    public static final String KEY_TITLE = "Title";
    public static final String KEY_SEEN = "Seen";
    public static final String KEY_HASFAVOURITE = "HasFavourite";

    public DbAdapter(Context context, String DATABASE_TABLE) {
        this.context = context;
        this.DATABASE_TABLE = DATABASE_TABLE;
    }

    public DbAdapter open() throws SQLException {
        dbHelper = new DatabaseHelper(context, DATABASE_TABLE);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void CheckAndReplaceTable(){
        SQLiteCursor cursor = fetchAllContacts();
        if (cursor==null){
            database = dbHelper.getWritableDatabase();
            String DATABASE_CREATE = "CREATE TABLE " + DATABASE_TABLE +
                    " (_id integer primary key autoincrement," +
                    " Title text not null," +
                    " Seen integer not null," +
                    " HasFavourite integer not null);";
            database.execSQL(DATABASE_CREATE);
        }
    }

    private ContentValues createContentValues(String title, int seen, int hasfavourite) {
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, title);
        values.put(KEY_SEEN, seen);
        values.put(KEY_HASFAVOURITE, hasfavourite);

        return values;
    }

    //create a contact
    public long createContact( String title, int seen, int hasfavourite) {
        database = dbHelper.getWritableDatabase();
        ContentValues initialValues = createContentValues(
                title,
                seen,
                hasfavourite);

        return database.insertOrThrow(DATABASE_TABLE, null, initialValues);
    }

    //update a contact
    public boolean updateContact(String title, int seen, int hasfavourite) {
        database = dbHelper.getWritableDatabase();
        ContentValues updateValues = createContentValues(
                title,
                seen,
                hasfavourite);

        return database.update(DATABASE_TABLE, updateValues, KEY_TITLE + "=?" ,new String[]{title}) > 0;
    }

    //delete a contact
    public boolean deleteContact(long contactID) {
        return database.delete(DATABASE_TABLE, KEY_LOCATIONID + "=" + contactID, null) > 0;
    }

    //fetch all contacts
    public SQLiteCursor fetchAllContacts() {
        database = dbHelper.getReadableDatabase();
        return (SQLiteCursor) database.query(DATABASE_TABLE, new String[]{KEY_LOCATIONID,
                 KEY_TITLE,KEY_SEEN,KEY_HASFAVOURITE}, null, null, null, null, null);
    }

    //fetch contacts filter by a string
    public SQLiteCursor fetchContactsByFilter(String filter) {
        database = dbHelper.getReadableDatabase();
        Cursor mCursor = database.query(true, DATABASE_TABLE, new String[]{
                        KEY_LOCATIONID, KEY_TITLE, KEY_SEEN, KEY_HASFAVOURITE},
                KEY_TITLE + " GLOB '*" + filter + "*'", null, null, null, null, null);

        return (SQLiteCursor) mCursor;
    }

    //fetch contacts filter by a string
    public SQLiteCursor fetchContactsById(Long filter) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor mCursor = database.query(true, DATABASE_TABLE, new String[]{
                        KEY_LOCATIONID, KEY_TITLE, KEY_SEEN, KEY_HASFAVOURITE},
                KEY_LOCATIONID + " = ?",new String[] { String.valueOf(filter) }, null, null, null, null, null);

        return (SQLiteCursor) mCursor;
    }

}