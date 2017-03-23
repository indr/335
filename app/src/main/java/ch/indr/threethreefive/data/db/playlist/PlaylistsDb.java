/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.db.playlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ch.indr.threethreefive.data.db.playlist.model.PlaylistItem;

public class PlaylistsDb {
  private final PlaylistsDbHelper dbHelper;

  public PlaylistsDb(Context context) {
    this.dbHelper = new PlaylistsDbHelper(context);
  }

  public PlaylistItem insert(String title, String pageUri, String mediaUri) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(PlaylistsContract.PlaylistItem.COLUMN_NAME_TITLE, title);
    values.put(PlaylistsContract.PlaylistItem.COLUMN_NAME_PAGE_URI, pageUri);
    values.put(PlaylistsContract.PlaylistItem.COLUMN_NAME_MEDIA_URI, mediaUri);

    long id = db.insert(PlaylistsContract.PlaylistItem.TABLE_NAME, null, values);

    return new PlaylistItem(id, title, pageUri, mediaUri);
  }

  public void delete() {
    delete(null, null);
  }

  public List<PlaylistItem> findAll() {
    return read(null, null);
  }

  private List<PlaylistItem> read(String selection, String[] selectionArgs) {
    SQLiteDatabase db = dbHelper.getReadableDatabase();

    String[] projection = {
        PlaylistsContract.PlaylistItem._ID,
        PlaylistsContract.PlaylistItem.COLUMN_NAME_TITLE,
        PlaylistsContract.PlaylistItem.COLUMN_NAME_PAGE_URI,
        PlaylistsContract.PlaylistItem.COLUMN_NAME_MEDIA_URI
    };

    String sortOrder = PlaylistsContract.PlaylistItem._ID + " ASC";

    Cursor cursor = db.query(PlaylistsContract.PlaylistItem.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    try {
      List<PlaylistItem> result = new ArrayList<>();
      if (cursor != null && cursor.moveToFirst()) {
        do {
          result.add(new PlaylistItem(
              cursor.getLong(cursor.getColumnIndexOrThrow(PlaylistsContract.PlaylistItem._ID)),
              cursor.getString(cursor.getColumnIndexOrThrow(PlaylistsContract.PlaylistItem.COLUMN_NAME_TITLE)),
              cursor.getString(cursor.getColumnIndexOrThrow(PlaylistsContract.PlaylistItem.COLUMN_NAME_PAGE_URI)),
              cursor.getString(cursor.getColumnIndexOrThrow(PlaylistsContract.PlaylistItem.COLUMN_NAME_MEDIA_URI))
          ));
        } while (cursor.moveToNext());
      }
      return result;
    } finally {
      if (cursor != null) cursor.close();
    }
  }

  public void delete(PlaylistItem item) {
    String selection = PlaylistsContract.PlaylistItem._ID + " = ?";
    String[] selectionArgs = {item.getId()};

    delete(selection, selectionArgs);
  }

  private void delete(String selection, String[] selectionArgs) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    // Issue SQL statement.
    db.delete(PlaylistsContract.PlaylistItem.TABLE_NAME, selection, selectionArgs);
  }
}
