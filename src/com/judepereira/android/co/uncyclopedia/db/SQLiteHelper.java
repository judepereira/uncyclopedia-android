/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judepereira.android.co.uncyclopedia.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * @author Jude Pereira
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "uncyclopedia";
    public static final String SAVED_ARTICLES_TABLE_NAME = "saved_articles";
    public static final String SETTINGS_TABLE_NAME = "settings";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + SAVED_ARTICLES_TABLE_NAME + " (title text, url text)");
        db.execSQL("create table " + SETTINGS_TABLE_NAME + " (key text, value text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }
}
