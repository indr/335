/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.db.music;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.Media;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.indr.threethreefive.data.db.music.model.Album;
import ch.indr.threethreefive.data.db.music.model.Artist;
import ch.indr.threethreefive.data.db.music.model.Genre;
import ch.indr.threethreefive.data.db.music.model.Song;

public class MusicStore {

  private final Context context;
  private HashMap<String, String> albumArts;

  public MusicStore(final @NonNull Context context) {
    this.context = context;
  }

  @Nullable public Artist getArtistById(String artistId) {
    ArrayList<Artist> artists = queryArtists("_id=" + artistId);
    if (artists.size() < 1) return null;

    return artists.get(0);
  }

  @NonNull public ArrayList<Artist> queryArtists(String selection) {
    ArrayList<Artist> result = new ArrayList<>();
    Uri uri = Audio.Artists.EXTERNAL_CONTENT_URI;
    Cursor cursor = getContentResolver().query(uri,
        new String[]{"_id", "artist",
            Audio.ArtistColumns.NUMBER_OF_ALBUMS,
            Audio.ArtistColumns.NUMBER_OF_TRACKS
        },
        selection, null, "artist");
    if (cursor != null && cursor.moveToFirst()) {
      do {
        result.add(new Artist(
            cursor.getString(0),
            cursor.getString(1),
            cursor.getInt(2),
            cursor.getInt(3)
        ));
      }
      while (cursor.moveToNext());
      cursor.close();
    }
    return result;
  }

  @NonNull public ArrayList<Album> findAlbumsByArtistId(String artistId) {
    return queryAlbums("artist_id=" + artistId);
  }

  @Nullable public Album getAlbumById(String albumId) {
    ArrayList<Album> albums = queryAlbums("_id=" + albumId);
    if (albums.size() < 1) return null;

    return albums.get(0);
  }

  @NonNull public ArrayList<Album> queryAlbums(String selection) {
    ArrayList<Album> albums = new ArrayList<>();

    Uri uri = Audio.Albums.EXTERNAL_CONTENT_URI;
    Cursor cursor = getContentResolver().query(uri,
        new String[]{"_id", "album", "artist", "artist_id",
            Audio.AlbumColumns.NUMBER_OF_SONGS},
        selection, null, "album");

    if (cursor != null && cursor.moveToFirst()) {
      do {
        albums.add(new Album(
            cursor.getString(0),
            cursor.getString(1),
            cursor.getString(2),
            cursor.getString(3),
            cursor.getInt(4)
        ));
      }
      while (cursor.moveToNext());
      cursor.close();
    }
    return albums;
  }

  @Nullable public Song getSongById(String songId) {
    ArrayList<Song> songs = querySongs("_id=" + songId, null);
    if (songs.size() < 1) return null;

    return songs.get(0);
  }

  @NonNull public ArrayList<Song> getSongsByAlbumId(String albumId) {
    return querySongs("album_id=" + albumId, null);
  }

  @NonNull public ArrayList<Song> getSongsByArtistId(String artistId) {
    return querySongs("artist_id=" + artistId, "album, track, title");
  }

  @NonNull public ArrayList<Song> querySongs(String selection, String sortOrder) {
    sortOrder = sortOrder != null ? sortOrder : "track, title";
    ArrayList<Song> songs = new ArrayList<>();

    Uri uri = Media.EXTERNAL_CONTENT_URI;
    Cursor cursor = getContentResolver().query(uri,
        new String[]{"_id", "title", "artist", "artist_id", "album", "album_id", "_data", "duration"},
        selection, null, sortOrder);

    if (cursor != null && cursor.moveToFirst()) {
      do {
        final String albumArt = getAlbumArtByAlbumId(cursor.getString(5));
        songs.add(new Song(
            cursor.getString(0),
            cursor.getString(1),
            cursor.getString(2),
            cursor.getString(3),
            cursor.getString(4),
            cursor.getString(5),
            cursor.getString(6),
            cursor.getLong(7),
            albumArt
        ));
      }
      while (cursor.moveToNext());
      cursor.close();
    }
    return songs;
  }

  @NonNull public List<Genre> queryGenres() {
    List<Genre> genres = new ArrayList<>();

    Uri uri = Audio.Genres.EXTERNAL_CONTENT_URI;
    Cursor cursor = getContentResolver().query(uri,
        new String[]{"_id, name"},
        null, null, "name");

    if (cursor != null && cursor.moveToFirst()) {
      do {
        final String name = cursor.getString(1);
        if (name == null || MediaStore.UNKNOWN_STRING.equals(name)) {
          continue;
        }
        genres.add(new Genre(cursor.getLong(0), name));
      }
      while (cursor.moveToNext());
      cursor.close();
    }
    return genres;
  }

  @NonNull public List<Song> getSongsByGenreId(long genreId) {
    ArrayList<Song> songs = new ArrayList<>();

    Uri uri = Audio.Genres.Members.getContentUri("external", genreId);
    Cursor cursor = getContentResolver().query(uri,
        new String[]{
            Media._ID,
            Media.TITLE,
            Media.ARTIST,
            Media.ARTIST_ID,
            Media.ALBUM,
            Media.ALBUM_ID,
            Media.DATA,
            Media.DURATION
        },
        null, null, Media.TITLE);

    if (cursor != null && cursor.moveToFirst()) {
      do {
        final String albumArt = getAlbumArtByAlbumId(cursor.getString(5));
        songs.add(new Song(
            cursor.getString(0),
            cursor.getString(1),
            cursor.getString(2),
            cursor.getString(3),
            cursor.getString(4),
            cursor.getString(5),
            cursor.getString(6),
            cursor.getLong(7),
            albumArt));
      }
      while (cursor.moveToNext());
      cursor.close();
    }
    return songs;
  }

  @Nullable private String getAlbumArtByAlbumId(String albumId) {
    if (this.albumArts == null) {
      this.albumArts = new HashMap<>();
      Uri uri = Audio.Albums.EXTERNAL_CONTENT_URI;
      Cursor cursor = getContentResolver().query(uri,
          new String[]{"_id", Audio.AlbumColumns.ALBUM_ART},
          null, null, null);

      if (cursor != null && cursor.moveToFirst()) {
        do {
          albumArts.put(
              cursor.getString(0),
              cursor.getString(1)
          );
        }
        while (cursor.moveToNext());
        cursor.close();
      }
    }

    return albumArts.get(albumId);
  }

  private ContentResolver getContentResolver() {
    return this.context.getContentResolver();
  }
}
