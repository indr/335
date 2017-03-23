/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages.favorites;

import android.content.UriMatcher;
import android.net.Uri;

import ch.indr.threethreefive.libs.pages.PageMeta;
import ch.indr.threethreefive.libs.pages.PageResolver;
import timber.log.Timber;

import static ch.indr.threethreefive.libs.PageUris.AUTHORITY;

public class FavoritesPageResolver extends PageResolver {

  private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
  private static final String BASE_PATH = "/favorites";

  private static final int INDEX = 1;

  static {
    uriMatcher.addURI(AUTHORITY, BASE_PATH + "", INDEX);
  }

  public PageMeta resolve(Uri uri) {
    uri = setDefaultAuthority(uri);
    Timber.d("Resolving %s, %s", uri.toString(), this.toString());

    switch (uriMatcher.match(uri)) {
      case INDEX:
        return makeMeta(IndexPage.class, uri);
      default:
        throw new PageNotFoundException(uri);
    }
  }
}
