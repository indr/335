/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.db.favorites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ch.indr.threethreefive.data.db.favorites.model.Favorite;
import ch.indr.threethreefive.libs.utils.UriUtils;

public class FavoritesStoreImpl implements FavoritesStore {
  private final Context context;

  public FavoritesStoreImpl(final @NonNull Context context) {
    this.context = context;
  }

  public long add(final @NonNull Favorite favorite) {
    SQLiteDatabase db = getWritableDatabase();

    // Create a new map of values, where column names are the keys
    ContentValues values = new ContentValues();
    values.put(FavoritesContract.FavoritesEntry.COLUMN_NAME_TITLE, favorite.getTitle());
    values.put(FavoritesContract.FavoritesEntry.COLUMN_NAME_SUBTITLE, favorite.getSubtitle());
    values.put(FavoritesContract.FavoritesEntry.COLUMN_NAME_DESCRIPTION, favorite.getContentDescription());
    values.put(FavoritesContract.FavoritesEntry.COLUMN_NAME_PAGE_URI, favorite.getPageUri().toString());
    values.put(FavoritesContract.FavoritesEntry.COLUMN_NAME_ICON_URI, UriUtils.getString(favorite.getIconUri()));

    return db.insert(FavoritesContract.FavoritesEntry.TABLE_NAME, null, values);
  }

  @NonNull public List<Favorite> findAll() {
    return read(null, null);
  }

  public boolean isFavorite(final @NonNull Uri pageUri) {
    return findByPageUri(pageUri.toString()).size() > 0;
  }

  public void remove(final @NonNull Uri pageUri) {
    // Define 'where' part of query.
    String selection = FavoritesContract.FavoritesEntry.COLUMN_NAME_PAGE_URI + " = ?";
    // Specify arguments in placeholder order.
    String[] selectionArgs = {pageUri.toString()};

    delete(selection, selectionArgs);
  }

  @NonNull private List<Favorite> findByPageUri(final @NonNull String pageUri) {
    String selection = FavoritesContract.FavoritesEntry.COLUMN_NAME_PAGE_URI + " = ?";
    String[] selectionArgs = {pageUri};

    return read(selection, selectionArgs);
  }

  @NonNull private List<Favorite> read(final @Nullable String selection, final @Nullable String[] selectionArgs) {
    SQLiteDatabase db = getReadableDatabase();

    // Define a projection that specifies which columns from the database
    // you will actually use after this query.
    String[] projection = {
        FavoritesContract.FavoritesEntry._ID,
        FavoritesContract.FavoritesEntry.COLUMN_NAME_TITLE,
        FavoritesContract.FavoritesEntry.COLUMN_NAME_SUBTITLE,
        FavoritesContract.FavoritesEntry.COLUMN_NAME_DESCRIPTION,
        FavoritesContract.FavoritesEntry.COLUMN_NAME_PAGE_URI,
        FavoritesContract.FavoritesEntry.COLUMN_NAME_ICON_URI
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
              UriUtils.tryParse(cursor.getString(cursor.getColumnIndexOrThrow(FavoritesContract.FavoritesEntry.COLUMN_NAME_PAGE_URI))),
              UriUtils.tryParse(cursor.getString(cursor.getColumnIndexOrThrow(FavoritesContract.FavoritesEntry.COLUMN_NAME_ICON_URI)))
          ));
        } while (cursor.moveToNext());
      }
      return result;
    } finally {
      if (cursor != null) cursor.close();
    }
  }

  private void delete(final @Nullable String selection, final @Nullable String[] selectionArgs) {
    SQLiteDatabase db = getWritableDatabase();

    // Issue SQL statement.
    db.delete(FavoritesContract.FavoritesEntry.TABLE_NAME, selection, selectionArgs);
  }

  @NonNull private SQLiteDatabase getReadableDatabase() {
    // Gets the data repository in read mode
    FavoritesDbHelper dbHelper = new FavoritesDbHelper(context);
    return dbHelper.getReadableDatabase();
  }

  @NonNull private SQLiteDatabase getWritableDatabase() {
    // Gets the data repository in write mode
    FavoritesDbHelper dbHelper = new FavoritesDbHelper(context);
    return dbHelper.getWritableDatabase();
  }
}
