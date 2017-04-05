/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data;

import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.List;

import ch.indr.threethreefive.data.db.music.model.Song;
import ch.indr.threethreefive.data.network.radioBrowser.model.Station;
import ch.indr.threethreefive.libs.MediaItem;
import ch.indr.threethreefive.libs.utils.UriUtils;

import static android.support.v4.media.MediaMetadataCompat.Builder;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ART_URI;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_URI;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE;
import static ch.indr.threethreefive.libs.MetadataKeys.METADATA_KEY_ALBUM_ID;
import static ch.indr.threethreefive.libs.MetadataKeys.METADATA_KEY_ARTIST_ID;
import static ch.indr.threethreefive.libs.MetadataKeys.METADATA_KEY_RADIO_ID;
import static ch.indr.threethreefive.libs.MetadataKeys.METADATA_KEY_SONG_ID;
import static ch.indr.threethreefive.libs.PageUris.musicSong;
import static ch.indr.threethreefive.libs.PageUris.radioStation;

public class MediaItemFactory {

  public static @NonNull List<MediaItem> make(final @NonNull List<Song> songs) {
    List<MediaItem> mediaItems = new ArrayList<>();
    for (Song song : songs) {
      mediaItems.add(make(song));
    }
    return mediaItems;
  }

  public static @NonNull MediaItem make(final @NonNull Song song) {
    final String pageUri = musicSong(song.getId()).toString();
    final String mediaUri = song.getData();

    final MediaMetadataCompat metadata = new Builder()
        .putString(METADATA_KEY_ALBUM_ARTIST, song.getArtist())
        .putString(METADATA_KEY_ALBUM, song.getAlbum())
        .putString(METADATA_KEY_ALBUM_ART_URI, UriUtils.getString(song.getAlbumArtworkUri()))
        .putString(METADATA_KEY_ALBUM_ID, song.getAlbumId())
        .putString(METADATA_KEY_ARTIST, song.getArtist())
        .putString(METADATA_KEY_ARTIST_ID, song.getArtistId())
        .putString(METADATA_KEY_DISPLAY_SUBTITLE, song.getArtist())
        .putString(METADATA_KEY_DISPLAY_TITLE, song.getName())
        .putLong(METADATA_KEY_DURATION, song.getDuration())
        .putString(METADATA_KEY_MEDIA_ID, mediaUri)
        .putString(METADATA_KEY_MEDIA_URI, mediaUri)
        .putString(METADATA_KEY_SONG_ID, song.getId())
        .putString(METADATA_KEY_TITLE, song.getName())
        .build();

    return new MediaItem(metadata, pageUri);
  }

  public static @NonNull MediaItem make(final @NonNull Station station) {
    final String pageUri = radioStation(station.getId()).toString();
    final String mediaUri = station.getStreamUri();

    final MediaMetadataCompat metadata = new Builder()
        .putString(METADATA_KEY_ART_URI, UriUtils.getString(station.getLogoUri()))
        .putString(METADATA_KEY_DISPLAY_SUBTITLE, station.getSubtitle())
        .putString(METADATA_KEY_DISPLAY_TITLE, station.getTitle())
        .putString(METADATA_KEY_MEDIA_ID, mediaUri)
        .putString(METADATA_KEY_MEDIA_URI, mediaUri)
        .putString(METADATA_KEY_RADIO_ID, station.getId())
        .putString(METADATA_KEY_TITLE, station.getTitle())
        .build();

    return new MediaItem(metadata, pageUri);
  }
}
