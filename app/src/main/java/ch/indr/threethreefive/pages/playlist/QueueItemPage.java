/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages.playlist;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat.QueueItem;

import com.example.android.uamp.playback.Queue;
import com.example.android.uamp.playback.QueueManager;

import java.util.List;

import ch.indr.threethreefive.commands.AddToPlaylist;
import ch.indr.threethreefive.commands.PlayMedia;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.MediaItem;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.utils.StringUtils;
import ch.indr.threethreefive.libs.pages.Page;
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

public class QueueItemPage extends Page {

  private final QueueManager queueManager;
  private final PlaybackClient playbackClient;

  private long queueItemId;
  private PlayQueueItem playQueueItem;
  private RemoveQueueItem removeQueueItem;

  public QueueItemPage(Environment environment) {
    super(environment);

    this.playbackClient = environment.playbackClient();
    this.queueManager = environment.queueManager();
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    queueItemId = Long.parseLong(bundle.getString("id"));
  }

  @Override public void onStart() {
    super.onStart();

    final PageItemsBuilder builder = pageItemsBuilder();

    QueueItem queueItem = getQueueItem();
    if (queueItem != null) {
      setTitle(queueItem.getDescription().getTitle());

      final Queue currentQueue = queueManager.getCurrentQueue();
      final MediaItem mediaItem = currentQueue != null ? currentQueue.getMediaItem(queueItem) : null;

      playQueueItem = new PlayQueueItem(mediaItem);
      builder.add(playQueueItem);
      removeQueueItem = new RemoveQueueItem(mediaItem);
      builder.add(removeQueueItem);

      addPageItems(builder, mediaItem != null ? mediaItem.getMediaMetadata() : null);
    } else {
      setTitle("Playlist Item " + queueItemId);

      builder.addText("Playlist item not found");
    }

    setPageItems(builder);
  }

  @Nullable private QueueItem getQueueItem() {
    final MediaControllerCompat mediaController = playbackClient.mediaController();
    if (mediaController == null) return null;

    for (QueueItem each : mediaController.getQueue()) {
      if (each.getQueueId() == queueItemId) {
        return each;
      }
    }
    return null;
  }

  private void addPageItems(PageItemsBuilder builder, @Nullable MediaMetadataCompat metadata) {
    final MediaDescriptionCompat description = metadata != null ? metadata.getDescription() : null;

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

    } else if (description != null) {
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
    return metadata != null
        && metadata.containsKey(METADATA_KEY_ALBUM_ID)
        && metadata.containsKey(METADATA_KEY_ARTIST_ID)
        && metadata.containsKey(METADATA_KEY_SONG_ID);
  }

  private static boolean isRadio(MediaMetadataCompat metadata) {
    return metadata != null && metadata.containsKey(METADATA_KEY_RADIO_ID);
  }

  private class PlayQueueItem extends PlayMedia {

    public PlayQueueItem(MediaItem mediaItem) {
      super(mediaItem);
    }

    @Override public void execute(@NonNull Environment environment) {
      if (QueueItemPage.this.getQueueItem() == null) {
        final QueueItem queueItem = super.playMedia(environment);
        if (queueItem != null) {
          QueueItemPage.this.queueItemId = queueItem.getQueueId();
        }
      } else {
        MediaControllerCompat.TransportControls controls = environment.playbackControls();
        if (controls == null) return;

        long queueId = QueueItemPage.this.queueItemId;
        controls.skipToQueueItem(queueId);
        controls.play();
      }

      removeQueueItem.updateName();
    }
  }

  private class RemoveQueueItem extends AddToPlaylist {

    private final CharSequence mediaItemTitle;

    RemoveQueueItem(@NonNull MediaItem mediaItem) {
      super(mediaItem);

      setTitle("Remove from Playlist");
      setDescription("Remove from Playlist");
      this.mediaItemTitle = mediaItem.getName();
    }

    @Override public void execute(@NonNull Environment environment) {
      if (QueueItemPage.this.getQueueItem() == null) {
        addToQueue(environment);
      } else {
        removeFromQueue(environment);
      }
    }

    @Override protected List<QueueItem> addToQueue(@NonNull Environment environment) {
      final List<QueueItem> queueItems = super.addToQueue(environment);

      final QueueItem queueItem = queueItems.size() > 0 ? queueItems.get(0) : null;
      if (queueItem != null) {
        QueueItemPage.this.queueItemId = queueItem.getQueueId();
        updateName();
      }
      return queueItems;
    }

    private void removeFromQueue(@NonNull Environment environment) {
      environment.queueManager().removeFromQueue(QueueItemPage.this.queueItemId);
      environment.toastManager().toast("Removed: " + this.mediaItemTitle);
      environment.speaker().command().playlistItemRemoved();

      QueueItemPage.this.queueItemId = -1;
      updateName();
    }

    public void updateName() {
      final String text = QueueItemPage.this.queueItemId > -1 ? "Remove from Playlist" : "Add to Playlist";
      setTitle(text);
      setDescription(text);
    }
  }
}
