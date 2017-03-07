package ch.indr.threethreefive.playlist;

import android.content.UriMatcher;
import android.net.Uri;

import ch.indr.threethreefive.navigation.AbstractPageResolver;
import ch.indr.threethreefive.navigation.PageMeta;
import ch.indr.threethreefive.playlist.pages.QueueItemPage;
import ch.indr.threethreefive.playlist.pages.QueuePage;
import timber.log.Timber;

public class PlaylistResolver extends AbstractPageResolver {

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
