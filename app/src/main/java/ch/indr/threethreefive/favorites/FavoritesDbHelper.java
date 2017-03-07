/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.favorites;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static ch.indr.threethreefive.favorites.FavoritesContract.FavoritesEntry;

public class FavoritesDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FavoritesStore.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String DATETIME_TYPE = " DATETIME";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +
                    FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FavoritesEntry.COLUMN_NAME_CREATED_AT + DATETIME_TYPE + " DEFAULT CURRENT_TIMESTAMP" + COMMA_SEP +
                    FavoritesEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    FavoritesEntry.COLUMN_NAME_PAGE_URI + TEXT_TYPE + COMMA_SEP +
                    FavoritesEntry.COLUMN_NAME_MEDIA_URI + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FavoritesEntry.TABLE_NAME;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
