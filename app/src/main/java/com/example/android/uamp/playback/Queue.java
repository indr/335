/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package com.example.android.uamp.playback;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaSessionCompat.QueueItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.indr.threethreefive.libs.MediaItem;

public class Queue {

  private String mTitle;
  private List<MediaItem> mMediaItems;
  private List<QueueItem> mQueueItems;
  private long mNextQueueItemId = 0;

  public Queue() {
    mTitle = "";
    createLists(new ArrayList<>());
  }

  public Queue(@NonNull String title, Collection<MediaItem> mediaItems) {
    mTitle = title;
    createLists(mediaItems);
  }

  public @NonNull String getTitle() {
    return mTitle;
  }

  public List<MediaItem> mediaItems() {
    return mMediaItems;
  }

  public List<QueueItem> queueItems() {
    return mQueueItems;
  }

  private void createLists(@NonNull Collection<MediaItem> mediaItems) {
    mMediaItems = new ArrayList<>(mediaItems);
    List<QueueItem> queueItems = new ArrayList<>();
    for (MediaItem mediaItem : mMediaItems) {
      queueItems.add(new QueueItem(mediaItem.getMediaDescription(), nextQueueItemId()));
    }
    mQueueItems = Collections.synchronizedList(queueItems);
  }

  public QueueItem add(@NonNull MediaItem mediaItem) {
    mMediaItems.add(mediaItem);
    final QueueItem queueItem = new QueueItem(mediaItem.getMediaDescription(), nextQueueItemId());
    mQueueItems.add(queueItem);
    return queueItem;
  }

  public boolean remove(long queueItemId) {
    int index = getIndexByQueueId(queueItemId);
    if (index < 0) {
      return false;
    }

    mMediaItems.remove(index);
    mQueueItems.remove(index);
    return true;
  }

  public int size() {
    return Math.min(mMediaItems.size(), mQueueItems.size());
  }

  int getIndexByQueueId(long queueId) {
    int index = 0;
    for (QueueItem queueItem : mQueueItems) {
      if (queueItem.getQueueId() == queueId) {
        return index;
      }
      index++;
    }
    return -1;
  }

  public boolean isIndexPlayable(int index) {
    return index >= 0 && index < this.size();
  }

  public MediaItem getMediaItem(int index) {
    if (!isIndexPlayable(index)) {
      return null;
    }
    return mMediaItems.get(index);
  }

  public QueueItem getQueueItem(int index) {
    return mQueueItems.get(index);
  }

  private long nextQueueItemId() {
    return mNextQueueItemId++;
  }

  public void clear() {
    mMediaItems.clear();
    mQueueItems.clear();
  }

  public int getIndexByMediaItem(MediaItem currentMediaItem) {
    return mMediaItems.indexOf(currentMediaItem);
  }

  @Nullable public MediaItem getMediaItem(QueueItem queueItem) {
    final int indexByQueueId = getIndexByQueueId(queueItem.getQueueId());
    return getMediaItem(indexByQueueId);
  }
}
