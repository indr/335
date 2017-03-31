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

  public static final String AUTHORITY = "ch.indr.threethreefive";

  @NonNull public static Uri favorites() {
    return build("/favorites").build();
  }

  @NonNull public static Uri music() {
    return build("/music").build();
  }

  @NonNull public static Uri musicAlbum(final @NonNull String albumId) {
    return build("/music/albums").appendPath(albumId).build();
  }

  @NonNull public static Uri musicAlbums() {
    return build("/music/albums").build();
  }

  @NonNull public static Uri musicArtist(final @NonNull String artistId) {
    return build("/music/artists/").appendPath(artistId).build();
  }

  @NonNull public static Uri musicArtists() {
    return build("/music/artists").build();
  }

  @NonNull public static Uri musicGenre(final @NonNull String genreId) {
    return build("/music/genres/").appendPath(genreId).build();
  }

  @NonNull public static Uri musicGenres() {
    return build("/music/genres").build();
  }

  @NonNull public static Uri musicSong(final @NonNull String songId) {
    return build("/music/songs/").appendPath(songId).build();
  }

  @NonNull public static Uri musicSongs() {
    return build("/music/songs").build();
  }

  @NonNull public static Uri nowPlaying() {
    return build("/now-playing").build();
  }

  @NonNull public static Uri radio() {
    return build("/radio").build();
  }

  @NonNull public static Uri radioCountries() {
    return build("/radio/countries").build();
  }

  @NonNull public static Uri radioCountry(final @NonNull String countryId) {
    return radioCountryGenres(countryId);
  }

  @NonNull public static Uri radioCountryGenres(final @NonNull String countryId) {
    return build("/radio/countries/").appendPath(countryId).appendPath("genres").build();
  }

  @NonNull public static Uri radioCountryGenre(final @NonNull String countryId, final @NonNull String genreId) {
    return build("/radio/countries/").appendPath(countryId).appendPath("genres").appendPath(genreId).build();
  }

  @NonNull public static Uri radioCountryStations(final @NonNull String countryId) {
    return build("/radio/countries/").appendPath(countryId).appendPath("stations").build();
  }

  @NonNull public static Uri radioGenre(final @NonNull String genreId) {
    return build("/radio/genres/").appendPath(genreId).build();
  }

  @NonNull public static Uri radioGenres() {
    return build("/radio/genres").build();
  }

  @NonNull public static Uri radioLanguage(final @NonNull String languageId) {
    return build("/radio/languages/").appendPath(languageId).build();
  }

  @NonNull public static Uri radioLanguages() {
    return build("/radio/languages").build();
  }

  @NonNull public static Uri radioStation(final @NonNull String stationId) {
    return build("/radio/stations/").appendPath(stationId).build();
  }

  @NonNull public static Uri radioStationGenres(final @NonNull String stationId) {
    return build("/radio/stations/").appendPath(stationId).appendPath("genres").build();
  }

  @NonNull public static Uri radioTrending() {
    return build("/radio/trending").build();
  }

  @NonNull public static Uri playlist() {
    return build("/playlist").build();
  }

  @NonNull public static Uri preferences() {
    return build("/preferences").build();
  }

  private static Uri.Builder build(final @NonNull String uriString) {
    return Uri.parse(uriString).buildUpon().authority(AUTHORITY);
  }
}
