/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package com.example.android.uamp.playback;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat.QueueItem;

import java.util.Collection;
import java.util.List;

import ch.indr.threethreefive.libs.MediaItem;

public interface QueueManager {
  QueueItem addToQueue(@NonNull MediaItem mediaItem);

  List<QueueItem> addToQueue(@NonNull List<MediaItem> mediaItems);

  QueueItem createQueue(@NonNull String title, @NonNull MediaItem mediaItem);

  List<QueueItem> createQueue(@NonNull String title, @NonNull Collection<MediaItem> mediaItems);

  void removeFromQueue(long queueId);

  void addListener(@NonNull MetadataUpdateListener queueListener);

  void removeListener(@NonNull MetadataUpdateListener queueListener);

  @Nullable QueueItem getCurrentQueueItem();

  boolean skipQueuePosition(int i);

  boolean skipQueuePosition(int i, boolean autoRepeat);

  void updateMetadata();

  boolean setCurrentQueueItem(long queueId);

  @Nullable Queue getCurrentQueue();

  void clearQueue();

  interface MetadataUpdateListener {
    void onMetadataChanged(MediaMetadataCompat metadata);

    void onMetadataRetrieveError();

    void onCurrentQueueIndexUpdated(int queueIndex);

    void onQueueUpdated(String title, List<QueueItem> newQueue);
  }
}
