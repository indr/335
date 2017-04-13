/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages;

import android.net.Uri;
import android.os.Bundle;

import java.util.List;
import java.util.Locale;

import ch.indr.threethreefive.libs.pages.PageResolver;
import ch.indr.threethreefive.libs.pages.PageMeta;
import ch.indr.threethreefive.pages.favorites.FavoritesPageResolver;
import ch.indr.threethreefive.pages.music.MusicPageResolver;
import ch.indr.threethreefive.pages.playlist.PlaylistPageResolver;
import ch.indr.threethreefive.pages.radio.RadioPageResolver;
import timber.log.Timber;

public class RootPageResolver extends PageResolver {

  private final MusicPageResolver music;
  private final RadioPageResolver radio;
  private final FavoritesPageResolver favorites;
  private final PlaylistPageResolver playlist;

  public RootPageResolver() {
    music = new MusicPageResolver();
    radio = new RadioPageResolver();
    favorites = new FavoritesPageResolver();
    playlist = new PlaylistPageResolver();
  }

  @Override public PageMeta resolve(Uri uri) {
    uri = setDefaultAuthority(uri);
    Timber.d("Resolving %s, %s", uri.toString(), this.toString());

    String firstPathSegment = getFirstPathSegment(uri);
    switch (firstPathSegment.toLowerCase(Locale.US)) {
      case "":
        return new PageMeta(HomePage.class, uri, new Bundle());
      case "music":
        return music.resolve(uri);
      case "now-playing":
        return new PageMeta(NowPlayingPage.class, uri, new Bundle());
      case "favorites":
        return favorites.resolve(uri);
      case "playlist":
        return playlist.resolve(uri);
      case "preferences":
        return makeMeta(PreferencesPage.class, uri);
      case "radio":
        return radio.resolve(uri);
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

