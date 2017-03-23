/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.net.Uri;
import android.support.annotation.NonNull;

public abstract class PageUris {

  @NonNull public static Uri favorites() {
    return build("/favorites").build();
  }

  @NonNull public static Uri music() {
    return build("/music").build();
  }

  @NonNull public static Uri musicAlbum(@NonNull String id) {
    return build("/music/albums").appendPath(id).build();
  }

  @NonNull public static Uri musicArtist(@NonNull String id) {
    return build("/music/artists/").appendPath(id).build();
  }

  @NonNull public static Uri musicGenre(@NonNull String id) {
    return build("/music/genres/").appendPath(id).build();
  }

  @NonNull public static Uri musicSong(@NonNull String id) {
    return build("/music/songs/").appendPath(id).build();
  }

  @NonNull public static Uri nowPlaying() {
    return build("/now-playing").build();
  }

  @NonNull public static Uri radio() {
    return build("/radio").build();
  }

  @NonNull public static Uri radioCountryGenres(@NonNull String country) {
    return build("/radio/countries/").appendPath(country).appendPath("genres").build();
  }

  @NonNull public static Uri radioCountryGenre(@NonNull String country, @NonNull String genre) {
    return build("/radio/countries/").appendPath(country).appendPath("genres").appendPath(genre).build();
  }

  @NonNull public static Uri radioCountryStations(@NonNull String country) {
    return build("/radio/countries/").appendPath(country).appendPath("stations").build();
  }

  @NonNull public static Uri radioGenre(@NonNull String genre) {
    return build("/radio/genres/").appendPath(genre).build();
  }

  @NonNull public static Uri radioLanguage(@NonNull String language) {
    return build("/radio/languages/").appendPath(language).build();
  }

  @NonNull public static Uri radioStation(@NonNull String id) {
    return build("/radio/stations/").appendPath(id).build();
  }

  @NonNull public static Uri playlist() {
    return build("/playlist").build();
  }

  @NonNull public static Uri preferences() {
    return build("/preferences").build();
  }

  private static Uri.Builder build(@NonNull String uriString) {
    return Uri.parse(uriString).buildUpon();
  }
}
