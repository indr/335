package ch.indr.threethreefive.radio;

import android.content.UriMatcher;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import ch.indr.threethreefive.navigation.AbstractPageResolver;
import ch.indr.threethreefive.navigation.Page;
import ch.indr.threethreefive.navigation.PageMeta;
import ch.indr.threethreefive.radio.pages.CountriesPage;
import ch.indr.threethreefive.radio.pages.CountryGenrePage;
import ch.indr.threethreefive.radio.pages.CountryGenresPage;
import ch.indr.threethreefive.radio.pages.CountryPage;
import ch.indr.threethreefive.radio.pages.GenrePage;
import ch.indr.threethreefive.radio.pages.GenresPage;
import ch.indr.threethreefive.radio.pages.IndexPage;
import ch.indr.threethreefive.radio.pages.LanguagePage;
import ch.indr.threethreefive.radio.pages.LanguagesPage;
import ch.indr.threethreefive.radio.pages.RecentsPage;
import ch.indr.threethreefive.radio.pages.StationGenresPage;
import ch.indr.threethreefive.radio.pages.StationPage;
import timber.log.Timber;

public class RadioResolver extends AbstractPageResolver {

  private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
  private static final String base_path = "/radio";

  private static final int INDEX = 1;
  private static final int LANGUAGES = INDEX + 1;
  private static final int LANGUAGE_NAME = LANGUAGES + 1;
  private static final int GENRES = LANGUAGE_NAME + 1;
  private static final int GENRE_ID = GENRES + 1;
  private static final int RECENTS_ID = GENRE_ID + 1;
  private static final int STATION_ID = RECENTS_ID + 1;
  private static final int STATION_ID_GENRES = STATION_ID + 1;

  private static final List<UrlPattern> urlPatterns = new ArrayList<>();

  static {
    uriMatcher.addURI(AUTHORITY, base_path, INDEX);
    addPattern("/countries", CountriesPage.class);
    addPattern("/countries/([^/]+)", CountryPage.class, new String[]{"countryId"});
    addPattern("/countries/([^/]+)/genres", CountryGenresPage.class, new String[]{"countryId"});
    addPattern("/countries/([^/]+)/genres/([^/]+)", CountryGenrePage.class, new String[]{"countryId", "genreId"});
    uriMatcher.addURI(AUTHORITY, base_path + "/languages", LANGUAGES);
    uriMatcher.addURI(AUTHORITY, base_path + "/languages/*", LANGUAGE_NAME);
    uriMatcher.addURI(AUTHORITY, base_path + "/genres", GENRES);
    uriMatcher.addURI(AUTHORITY, base_path + "/genres/*", GENRE_ID);
    uriMatcher.addURI(AUTHORITY, base_path + "/recents", RECENTS_ID);
    uriMatcher.addURI(AUTHORITY, base_path + "/stations/*", STATION_ID);
    uriMatcher.addURI(AUTHORITY, base_path + "/stations/*/genres", STATION_ID_GENRES);
  }

  private static void addPattern(String path, Class<? extends Page> pageClass) {
    addPattern(path, pageClass, null);
  }

  private static void addPattern(String path, Class<? extends Page> pageClass, String[] keys) {
    urlPatterns.add(new UrlPattern("^" + base_path + path + "$", pageClass, keys));
  }

  public PageMeta resolve(Uri uri) {
    uri = setDefaultAuthority(uri);
    Timber.d("Resolving %s, %s", uri, this.toString());

    switch (uriMatcher.match(uri)) {
      case INDEX:
        return makeMeta(IndexPage.class, uri);
      case LANGUAGES:
        return makeMeta(LanguagesPage.class, uri);
      case LANGUAGE_NAME:
        return makeMeta(LanguagePage.class, uri, uri.getLastPathSegment());
      case GENRES:
        return makeMeta(GenresPage.class, uri);
      case GENRE_ID:
        return makeMeta(GenrePage.class, uri, uri.getLastPathSegment());
      case RECENTS_ID:
        return makeMeta(RecentsPage.class, uri);
      case STATION_ID:
        return makeMeta(StationPage.class, uri, uri.getLastPathSegment());
      case STATION_ID_GENRES:
        return makeMeta(StationGenresPage.class, uri, uri.getPathSegments().get(uri.getPathSegments().size() - 2));
      default:
        return resolvePatterns(urlPatterns, uri);
    }
  }
}
