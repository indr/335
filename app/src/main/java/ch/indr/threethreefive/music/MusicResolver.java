package ch.indr.threethreefive.music;

import android.content.UriMatcher;
import android.net.Uri;

import ch.indr.threethreefive.music.pages.AlbumPage;
import ch.indr.threethreefive.music.pages.AlbumsPage;
import ch.indr.threethreefive.music.pages.ArtistPage;
import ch.indr.threethreefive.music.pages.ArtistsPage;
import ch.indr.threethreefive.music.pages.GenrePage;
import ch.indr.threethreefive.music.pages.GenresPage;
import ch.indr.threethreefive.music.pages.IndexPage;
import ch.indr.threethreefive.music.pages.SongPage;
import ch.indr.threethreefive.music.pages.SongsPage;
import ch.indr.threethreefive.navigation.AbstractPageResolver;
import ch.indr.threethreefive.navigation.PageMeta;
import timber.log.Timber;

public class MusicResolver extends AbstractPageResolver {

  private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
  private static final String BASE_PATH = "/music";

  private static final int INDEX = 1;
  private static final int ARTISTS = INDEX + 1;
  private static final int ARTIST_ID = ARTISTS + 1;
  private static final int ALBUMS = ARTIST_ID + 1;
  private static final int ALBUM_ID = ALBUMS + 1;
  private static final int GENRES = ALBUM_ID + 1;
  private static final int GENRE_ID = GENRES + 1;
  private static final int SONGS = GENRE_ID + 1;
  private static final int SONG_ID = SONGS + 1;

  static {
    uriMatcher.addURI(AUTHORITY, BASE_PATH + "", INDEX);
    uriMatcher.addURI(AUTHORITY, BASE_PATH + "/artists", ARTISTS);
    uriMatcher.addURI(AUTHORITY, BASE_PATH + "/artists/*", ARTIST_ID);
    uriMatcher.addURI(AUTHORITY, BASE_PATH + "/albums", ALBUMS);
    uriMatcher.addURI(AUTHORITY, BASE_PATH + "/albums/*", ALBUM_ID);
    uriMatcher.addURI(AUTHORITY, BASE_PATH + "/genres", GENRES);
    uriMatcher.addURI(AUTHORITY, BASE_PATH + "/genres/*", GENRE_ID);
    uriMatcher.addURI(AUTHORITY, BASE_PATH + "/songs", SONGS);
    uriMatcher.addURI(AUTHORITY, BASE_PATH + "/songs/*", SONG_ID);
  }

  @Override public PageMeta resolve(Uri uri) {
    uri = setDefaultAuthority(uri);
    Timber.d("Resolving %s, %s", uri.toString(), this.toString());

    switch (uriMatcher.match(uri)) {
      case INDEX:
        return makeMeta(IndexPage.class, uri);
      case ARTISTS:
        return makeMeta(ArtistsPage.class, uri);
      case ARTIST_ID:
        return makeMeta(ArtistPage.class, uri, uri.getLastPathSegment());
      case ALBUMS:
        return makeMeta(AlbumsPage.class, uri);
      case ALBUM_ID:
        return makeMeta(AlbumPage.class, uri, uri.getLastPathSegment());
      case GENRES:
        return makeMeta(GenresPage.class, uri);
      case GENRE_ID:
        return makeMeta(GenrePage.class, uri, uri.getLastPathSegment());
      case SONGS:
        return makeMeta(SongsPage.class, uri);
      case SONG_ID:
        return makeMeta(SongPage.class, uri, uri.getLastPathSegment());
      default:
        throw new PageNotFoundException(uri);
    }
  }
}
