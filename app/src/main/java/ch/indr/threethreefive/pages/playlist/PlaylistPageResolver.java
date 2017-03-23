/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages.playlist;

import android.content.UriMatcher;
import android.net.Uri;

import ch.indr.threethreefive.libs.pages.PageResolver;
import ch.indr.threethreefive.libs.pages.PageMeta;
import timber.log.Timber;

public class PlaylistPageResolver extends PageResolver {

  private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
  private static final String BASE_PATH = "/playlist";

  private static final int INDEX = 1;
  private static final int ITEM_ID = INDEX + 1;

  static {
    uriMatcher.addURI(AUTHORITY, BASE_PATH + "", INDEX);
    uriMatcher.addURI(AUTHORITY, BASE_PATH + "/*", ITEM_ID);
  }

  public PageMeta resolve(Uri uri) {
    uri = setDefaultAuthority(uri);
    Timber.d("Resolving %s, %s", uri.toString(), this.toString());

    switch (uriMatcher.match(uri)) {
      case INDEX:
        return makeMeta(QueuePage.class, uri);
      case ITEM_ID:
        return makeMeta(QueueItemPage.class, uri, uri.getLastPathSegment());
      default:
        throw new PageNotFoundException(uri);
    }
  }
}
