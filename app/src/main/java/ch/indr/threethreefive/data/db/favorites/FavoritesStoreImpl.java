/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.db.favorites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ch.indr.threethreefive.data.db.favorites.model.Favorite;
import ch.indr.threethreefive.libs.PageLink;

public class FavoritesStoreImpl implements FavoritesStore {

  private final Context context;

  public FavoritesStoreImpl(@NonNull Context context) {
    this.context = context;
  }

  public long add(@NonNull Favorite favorite) {
    SQLiteDatabase db = getWritableDatabase();

    // Create a new map of values, where column names are the keys
    ContentValues values = new ContentValues();
    values.put(FavoritesContract.FavoritesEntry.COLUMN_NAME_TITLE, favorite.getTitle());
    values.put(FavoritesContract.FavoritesEntry.COLUMN_NAME_SUBTITLE, favorite.getSubtitle());
    values.put(FavoritesContract.FavoritesEntry.COLUMN_NAME_DESCRIPTION, favorite.getDescription());
    values.put(FavoritesContract.FavoritesEntry.COLUMN_NAME_PAGE_URI, favorite.getPageUri());
    values.put(FavoritesContract.FavoritesEntry.COLUMN_NAME_MEDIA_URI, favorite.getMediaUri());

    return db.insert(FavoritesContract.FavoritesEntry.TABLE_NAME, null, values);
  }

  @NonNull public List<Favorite> findAll() {
    return read(null, null);
  }

  private List<Favorite> findByPageUri(String pageUri) {
    String selection = FavoritesContract.FavoritesEntry.COLUMN_NAME_PAGE_URI + " = ?";
    String[] selectionArgs = {pageUri};

    return read(selection, selectionArgs);
  }

  public boolean isFavorite(@NonNull PageLink pageLink) {
    return isFavorite(pageLink.getUri().toString());
  }

  public boolean isFavorite(@NonNull String pageUri) {
    return findByPageUri(pageUri).size() > 0;
  }

  public void remove(@NonNull String pageUri) {
    // Define 'where' part of query.
    String selection = FavoritesContract.FavoritesEntry.COLUMN_NAME_PAGE_URI + " = ?";
    // Specify arguments in placeholder order.
    String[] selectionArgs = {pageUri};

    delete(selection, selectionArgs);
  }

  private List<Favorite> read(String selection, String[] selectionArgs) {
    SQLiteDatabase db = getReadableDatabase();

    // Define a projection that specifies which columns from the database
    // you will actually use after this query.
    String[] projection = {
        FavoritesContract.FavoritesEntry._ID,
        FavoritesContract.FavoritesEntry.COLUMN_NAME_TITLE,
        FavoritesContract.FavoritesEntry.COLUMN_NAME_SUBTITLE,
        FavoritesContract.FavoritesEntry.COLUMN_NAME_DESCRIPTION,
        FavoritesContract.FavoritesEntry.COLUMN_NAME_PAGE_URI,
        FavoritesContract.FavoritesEntry.COLUMN_NAME_MEDIA_URI
    };

    // How you want the results sorted in the resulting Cursor
    String sortOrder = FavoritesContract.FavoritesEntry.COLUMN_NAME_CREATED_AT + " DESC";

    Cursor cursor = db.query(FavoritesContract.FavoritesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    try {
      List<Favorite> result = new ArrayList<>();
      if (cursor != null && cursor.moveToFirst()) {
        do {
          result.add(new Favorite(
              cursor.getLong(cursor.getColumnIndexOrThrow(FavoritesContract.FavoritesEntry._ID)),
              cursor.getString(cursor.getColumnIndexOrThrow(FavoritesContract.FavoritesEntry.COLUMN_NAME_TITLE)),
              cursor.getString(cursor.getColumnIndexOrThrow(FavoritesContract.FavoritesEntry.COLUMN_NAME_SUBTITLE)),
              cursor.getString(cursor.getColumnIndexOrThrow(FavoritesContract.FavoritesEntry.COLUMN_NAME_DESCRIPTION)),
              cursor.getString(cursor.getColumnIndexOrThrow(FavoritesContract.FavoritesEntry.COLUMN_NAME_PAGE_URI)),
              cursor.getString(cursor.getColumnIndexOrThrow(FavoritesContract.FavoritesEntry.COLUMN_NAME_MEDIA_URI))
          ));
        } while (cursor.moveToNext());
      }
      return result;
    } finally {
      if (cursor != null) cursor.close();
    }
  }

  private void delete(String selection, String[] selectionArgs) {
    SQLiteDatabase db = getWritableDatabase();

    // Issue SQL statement.
    db.delete(FavoritesContract.FavoritesEntry.TABLE_NAME, selection, selectionArgs);
  }

  private SQLiteDatabase getReadableDatabase() {
    // Gets the data repository in read mode
    FavoritesDbHelper dbHelper = new FavoritesDbHelper(context);
    return dbHelper.getReadableDatabase();
  }

  private SQLiteDatabase getWritableDatabase() {
    // Gets the data repository in write mode
    FavoritesDbHelper dbHelper = new FavoritesDbHelper(context);
    return dbHelper.getWritableDatabase();
  }
}
