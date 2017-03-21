/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

public abstract class PageUris {

  public static String makeArtistUri(String id) {
    return "/music/artists/" + id;
  }

  public static String makeAlbumUri(String id) {
    return "/music/albums/" + id;
  }

  public static String makeSongUri(String id) {
    return "/music/songs/" + id;
  }

  public static String makeGenreUri(String id) {
    return "/music/genres/" + id;
  }

  public static String makeStationUri(String id) {
    return "/radio/stations/" + id;
  }

  public static String makeCountryGenres(String country) {
    return "/radio/countries/" + country + "/genres";
  }

  public static String makeCountryGenre(String countryId, String genreId) {
    return "/radio/countries/" + countryId + "/genres/" + genreId;
  }
}
