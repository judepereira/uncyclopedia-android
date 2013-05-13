/*
 * This work is licensed under the
 * Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * 
 * To view a copy of this license,
 * visit http://creativecommons.org/licenses/by-sa/3.0/.
 */
package com.judepereira.android.co.uncyclopedia.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import com.judepereira.android.co.uncyclopedia.dto.Bookmark;
import java.util.ArrayList;

/**
 *
 * @author Jude Pereira
 */
public class Dao {

    private SQLiteHelper sql;
    private SQLiteDatabase db;

    public Dao(Context context) {
        sql = new SQLiteHelper(context);
        db = sql.getWritableDatabase();
    }

    public void close() {
        db.close();
        sql.close();
    }

    public boolean insertBookmark(String title, String url) {
        try {
            if (!isBookmarked(title)) {
                db.execSQL("insert into " + SQLiteHelper.SAVED_ARTICLES_TABLE_NAME + " values('" + title + "', '" + url + "')");
            }
        } catch (SQLException ex) {
            return false;
        }
        return true;
    }

    public ArrayList<Bookmark> getBookmarkList() {
        ArrayList<Bookmark> bookmarkList = new ArrayList<Bookmark>();
        Cursor c = (SQLiteCursor) db.rawQuery("select title, url from " + SQLiteHelper.SAVED_ARTICLES_TABLE_NAME, null);
        if (c.moveToFirst()) {
            do {
                bookmarkList.add(new Bookmark(c.getString(0), c.getString(1)));
            } while (c.moveToNext());
        }
        c.close();
        return bookmarkList;
    }

    public boolean removeBookmark(String title) {
        try {
            db.execSQL("delete from " + SQLiteHelper.SAVED_ARTICLES_TABLE_NAME + " where title='" + title + "'");
        } catch (SQLException ex) {
            return false;
        }
        return true;
    }

    public boolean isBookmarked(String title) {
        Cursor cursor = db.rawQuery("select title from " + SQLiteHelper.SAVED_ARTICLES_TABLE_NAME + " where title='" + title + "'", null);
        boolean exists;
        if (cursor.moveToFirst()) {
            exists = true;
        } else {
            exists = false;
        }
        cursor.close();
        return exists;
    }
}
