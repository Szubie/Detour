package com.example.oluwole.historyapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Oluwole on 22/12/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "Detour.db";
    public static  int DATABASE_VERSION = 1;
    private static String CITY="";
    // Lo statement SQL di creazione del database


    // Costruttore
    public DatabaseHelper(Context context,String CITY) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.CITY=CITY;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

    }

    // Questo metodo viene chiamato durante la creazione del database
    @Override
    public void onCreate(SQLiteDatabase database) {
         String DATABASE_CREATE = "CREATE TABLE " + CITY +
                " (_id integer primary key autoincrement," +
                " Title text not null," +
                " Seen integer not null," +
                " HasFavourite integer not null);";
        database.execSQL(DATABASE_CREATE);

    }

    // Questo metodo viene chiamato durante l'upgrade del database, ad esempio quando viene incrementato il numero di versione
    @Override
    public void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion ) {
        database.execSQL("DROP TABLE IF EXISTS "+CITY);
        onCreate(database);
    }
}
