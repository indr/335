/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

public abstract class PageUris {

  public static String musicAlbum(String id) {
    return "/music/albums/" + id;
  }

  public static String musicArtist(String id) {
    return "/music/artists/" + id;
  }

  public static String musicGenre(String id) {
    return "/music/genres/" + id;
  }

  public static String musicSong(String id) {
    return "/music/songs/" + id;
  }

  public static String radioCountryGenres(String country) {
    return "/radio/countries/" + country + "/genres";
  }

  public static String radioCountryGenre(String country, String genre) {
    return "/radio/countries/" + country + "/genres/" + genre;
  }

  public static String radioCountryStations(String country) {
    return "/radio/countries/" + country + "/stations";
  }

  public static String radioGenre(String genre) {
    return "/radio/genres/" + genre;
  }

  public static String radioLanguage(String language) {
    return "/radio/languages/" + language;
  }

  public static String radioStation(String id) {
    return "/radio/stations/" + id;
  }

}
