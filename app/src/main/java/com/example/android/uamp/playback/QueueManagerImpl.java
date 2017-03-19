/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package com.example.android.uamp.playback;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat.QueueItem;

import com.example.android.uamp.AlbumArtCache;
import com.example.android.uamp.utils.LogHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.indr.threethreefive.libs.MediaItem;

/**
 * Simple data provider for queues. Keeps track of a current queue and a current index in the
 * queue. Also provides methods to set the current queue based on common queries, relying on a
 * given MusicProvider to provide the actual media metadata.
 */
public class QueueManagerImpl implements QueueManager {
  private static final String TAG = LogHelper.makeLogTag(QueueManagerImpl.class);

  private MetadataUpdateListener mListener;

  private Queue mQueue;
  private int mCurrentIndex;

  public QueueManagerImpl() {
    mQueue = new Queue();
    mCurrentIndex = 0;
  }

  private void setCurrentQueueIndex(int index) {
    if (index >= 0 && index < mQueue.size()) {
      mCurrentIndex = index;
      if (mListener != null) {
        mListener.onCurrentQueueIndexUpdated(mCurrentIndex);
      }
    }
  }

  public boolean setCurrentQueueItem(long queueId) {
    int index = mQueue.getIndexByQueueId(queueId);
    setCurrentQueueIndex(index);
    return index >= 0;
  }

  @Nullable @Override public Queue getCurrentQueue() {
    return mQueue;
  }

  @Override public boolean skipQueuePosition(int amount) {
    return skipQueuePosition(amount, true);
  }

  @Override public boolean skipQueuePosition(int amount, boolean autoRepeat) {
    if (mQueue.size() <= 0) {
      return false;
    }

    int index = mCurrentIndex + amount;
    if (index < 0) {
      // skip backwards before the first song will keep you on the first song
      index = 0;
    } else if (autoRepeat) {
      // skip forwards when in last song will cycle back to start of the queue
      index %= mQueue.size();
    }
    if (!mQueue.isIndexPlayable(index)) {
      LogHelper.e(TAG, "Cannot increment queue index by ", amount,
          ". Current=", mCurrentIndex, " queue length=", mQueue.size());
      return false;
    }
    mCurrentIndex = index;
    return true;
  }

  private MediaItem getCurrentMediaItem() {
    if (!mQueue.isIndexPlayable(mCurrentIndex)) {
      return null;
    }
    return mQueue.getMediaItem(mCurrentIndex);
  }

  public QueueItem getCurrentQueueItem() {
    if (!mQueue.isIndexPlayable(mCurrentIndex)) {
      return null;
    }
    return mQueue.getQueueItem(mCurrentIndex);
  }

  public int getCurrentQueueSize() {
    return mQueue.size();
  }

  private void setCurrentQueue(@NonNull Queue newQueue) {
    mQueue = newQueue;
    mCurrentIndex = 0;
    notifiyQueueUpdated();
  }

  private void notifiyQueueUpdated() {
    if (mListener != null) {
      mListener.onQueueUpdated(mQueue.getTitle(), mQueue.queueItems());
    }
  }

  public void updateMetadata() {
    MediaItem currentMediaItem = getCurrentMediaItem();
    if (currentMediaItem == null) {
      if (mListener != null) {
        mListener.onMetadataRetrieveError();
      }
      return;
    }

    MediaMetadataCompat metadata = currentMediaItem.getMediaMetadata();
    if (mListener != null) {
      mListener.onMetadataChanged(metadata);
    }

    // Set the proper album artwork on the media session, so it can be shown in the
    // locked screen and in other places.
    if (metadata.getDescription().getIconBitmap() == null &&
        metadata.getDescription().getIconUri() != null) {
      String albumUri = metadata.getDescription().getIconUri().toString();
      AlbumArtCache.getInstance().fetch(albumUri, new AlbumArtCache.FetchListener() {
        @Override
        public void onFetched(String artUrl, Bitmap bitmap, Bitmap icon) {
          currentMediaItem.setAlbumArt(bitmap);
          currentMediaItem.setDisplayIcon(icon);

          // If we are still playing the same music, notify the listeners:
          MediaItem currentMediaItem2 = getCurrentMediaItem();
          if (currentMediaItem2 == null) {
            return;
          }
          if (currentMediaItem.equals(currentMediaItem2)) {
            if (mListener != null) {
              mListener.onMetadataChanged(currentMediaItem.getMediaMetadata());
            }
          }
        }
      });
    }
  }

  public void addListener(@NonNull MetadataUpdateListener listener) {
    mListener = listener;
  }

  public void removeListener(@NonNull MetadataUpdateListener listener) {
    mListener = null;
  }

  public QueueItem createQueue(@NonNull String title, @NonNull MediaItem mediaItem) {
    ArrayList<MediaItem> mediaItems = new ArrayList<>(1);
    mediaItems.add(mediaItem);
    final List<QueueItem> queueItems = createQueue(title, mediaItems);
    return queueItems.size() > 0 ? queueItems.get(0) : null;
  }

  public List<QueueItem> createQueue(@NonNull String title, @NonNull Collection<MediaItem> mediaItems) {
    Queue newQueue = new Queue(title, mediaItems);
    setCurrentQueue(newQueue);
    updateMetadata();
    return newQueue.queueItems();
  }

  @Override public QueueItem addToQueue(@NonNull MediaItem mediaItem) {
    final QueueItem queueItem = mQueue.add(mediaItem);
    if (queueItem != null) {
      notifiyQueueUpdated();
    }

    return queueItem;
  }

  @Override public List<QueueItem> addToQueue(@NonNull List<MediaItem> mediaItems) {
    final List<QueueItem> queueItems = new ArrayList<>();
    for (MediaItem mediaItem : mediaItems) {
      queueItems.add(mQueue.add(mediaItem));
    }

    if (queueItems.size() > 0) {
      notifiyQueueUpdated();
    }

    return queueItems;
  }

  public void removeFromQueue(long queueItemId) {
    final MediaItem currentMediaItem = mQueue.getMediaItem(mCurrentIndex);
    if (!mQueue.remove(queueItemId)) {
      return;
    }
    if (currentMediaItem != null) {
      mCurrentIndex = mQueue.getIndexByMediaItem(currentMediaItem);
    }
    notifiyQueueUpdated();
  }

  public void clearQueue() {
    mQueue.clear();
    mCurrentIndex = 0;
    notifiyQueueUpdated();
  }
}
