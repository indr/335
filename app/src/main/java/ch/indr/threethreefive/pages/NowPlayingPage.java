/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageLink;
import ch.indr.threethreefive.libs.utils.StringUtils;
import ch.indr.threethreefive.navigation.Page;
import ch.indr.threethreefive.services.PlaybackClient;

import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE;
import static ch.indr.threethreefive.libs.MetadataKeys.METADATA_KEY_ALBUM_ID;
import static ch.indr.threethreefive.libs.MetadataKeys.METADATA_KEY_ARTIST_ID;
import static ch.indr.threethreefive.libs.MetadataKeys.METADATA_KEY_RADIO_ID;
import static ch.indr.threethreefive.libs.MetadataKeys.METADATA_KEY_SONG_ID;
import static ch.indr.threethreefive.libs.PageUris.musicAlbum;
import static ch.indr.threethreefive.libs.PageUris.musicArtist;
import static ch.indr.threethreefive.libs.PageUris.musicSong;
import static ch.indr.threethreefive.libs.PageUris.radioStation;

public class NowPlayingPage extends Page {

  private final PlaybackClient playbackClient;

  public NowPlayingPage(Environment environment) {
    super(environment);

    this.playbackClient = environment.playbackClient();
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setTitle("Now Playing");
    setParentPageLink(PageLink.HomePage);

    playbackClient.mediaMetadata()
        .subscribe(this::updatePageItems);
  }

  private void updatePageItems(final MediaMetadataCompat mediaMetadata) {
    final PageItemsBuilder builder = pageItemsBuilder();

    if (mediaMetadata == null) {
      builder.addText("Playback stopped");
    } else {
      addPageItems(builder, mediaMetadata);
    }

    setPageItems(builder);
  }

  private void addPageItems(PageItemsBuilder builder, MediaMetadataCompat metadata) {
    final MediaDescriptionCompat description = metadata.getDescription();

    if (isMusic(metadata)) {
      // Add page items for a song from local music

      final String artistId = metadata.getString(METADATA_KEY_ARTIST_ID);
      if (StringUtils.isNotEmpty(artistId)) {
        builder.addLink(musicArtist(artistId), "Artist: " + metadata.getString(METADATA_KEY_ARTIST));
      }

      final String albumId = metadata.getString(METADATA_KEY_ALBUM_ID);
      if (StringUtils.isNotEmpty(albumId)) {
        builder.addLink(musicAlbum(albumId), "Album: " + metadata.getString(METADATA_KEY_ALBUM));
      }

      final String songId = metadata.getString(METADATA_KEY_SONG_ID);
      if (StringUtils.isNotEmpty(songId)) {
        builder.addLink(musicSong(songId), "Song: " + metadata.getString(METADATA_KEY_TITLE));
      }

    } else if (isRadio(metadata)) {
      // Add radio station info and links

      final String radioId = metadata.getString(METADATA_KEY_RADIO_ID);
      if (StringUtils.isNotEmpty(radioId)) {
        builder.addLink(radioStation(radioId), "Radio: " + metadata.getString(METADATA_KEY_TITLE));
      }

    } else {
      // Add generic title and subtitle without page links etc...

      final CharSequence title = description.getTitle();
      if (StringUtils.isNotEmpty(title)) {
        builder.addText(title.toString());
      }

      final CharSequence subtitle = description.getSubtitle();
      if (StringUtils.isNotEmpty(subtitle))
        builder.addText(subtitle.toString());
    }
  }

  private static boolean isMusic(MediaMetadataCompat metadata) {
    return metadata.containsKey(METADATA_KEY_ALBUM_ID)
        && metadata.containsKey(METADATA_KEY_ARTIST_ID)
        && metadata.containsKey(METADATA_KEY_SONG_ID);
  }

  private static boolean isRadio(MediaMetadataCompat metadata) {
    return metadata.containsKey(METADATA_KEY_RADIO_ID);
  }
}
