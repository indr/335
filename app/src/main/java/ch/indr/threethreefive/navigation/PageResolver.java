package ch.indr.threethreefive.navigation;

import android.net.Uri;
import android.os.Bundle;

import java.util.List;

import ch.indr.threethreefive.favorites.FavoritesResolver;
import ch.indr.threethreefive.music.MusicResolver;
import ch.indr.threethreefive.pages.HomePage;
import ch.indr.threethreefive.pages.NowPlayingPage;
import ch.indr.threethreefive.pages.PreferencesPage;
import ch.indr.threethreefive.playlist.PlaylistResolver;
import ch.indr.threethreefive.radio.RadioResolver;
import timber.log.Timber;

public class PageResolver extends AbstractPageResolver {

  private final MusicResolver musicResolver;
  private final RadioResolver radioResolver;
  private final FavoritesResolver favoritesResolver;
  private final PlaylistResolver playlistResolver;

  public PageResolver() {
    musicResolver = new MusicResolver();
    radioResolver = new RadioResolver();
    favoritesResolver = new FavoritesResolver();
    playlistResolver = new PlaylistResolver();
  }

  @Override public PageMeta resolve(Uri uri) {
    uri = setDefaultAuthority(uri);
    Timber.d("Resolving %s, %s", uri.toString(), this.toString());

    String firstPathSegment = getFirstPathSegment(uri);
    switch (firstPathSegment.toLowerCase()) {
      case "":
        return new PageMeta(HomePage.class, uri, new Bundle());
      case "music":
        return musicResolver.resolve(uri);
      case "now-playing":
        return new PageMeta(NowPlayingPage.class, uri, new Bundle());
      case "favorites":
        return favoritesResolver.resolve(uri);
      case "playlist":
        return playlistResolver.resolve(uri);
      case "preferences":
        return makeMeta(PreferencesPage.class, uri);
      case "radio":
        return radioResolver.resolve(uri);
      default:
        throw new PageNotFoundException(uri);
    }
  }

  private String getFirstPathSegment(Uri uri) {
    List<String> pathSegments = uri.getPathSegments();
    if (pathSegments.size() > 0) {
      return pathSegments.get(0);
    }
    return "";
  }
}

