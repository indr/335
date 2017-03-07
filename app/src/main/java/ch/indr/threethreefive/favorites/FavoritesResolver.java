package ch.indr.threethreefive.favorites;

import android.content.UriMatcher;
import android.net.Uri;

import ch.indr.threethreefive.favorites.pages.IndexPage;
import ch.indr.threethreefive.navigation.AbstractPageResolver;
import ch.indr.threethreefive.navigation.PageMeta;
import timber.log.Timber;

public class FavoritesResolver extends AbstractPageResolver {

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
