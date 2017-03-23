/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.db.playlist.model;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ch.indr.threethreefive.libs.MediaItem;

public class Playlist {

  private List<PlaylistItem> items = new ArrayList<>();

  public Playlist(List<PlaylistItem> items) {
    this.items = items;
  }

  public void add(PlaylistItem playlistItem) {
    this.items.add(playlistItem);
  }

  public PlaylistItem add(MediaItem mediaItem) {
    if (mediaItem == null) return null;

    PlaylistItem playlistItem = new PlaylistItem(mediaItem);
    items.add(playlistItem);
    return playlistItem;
  }

  public PlaylistItem add(List<MediaItem> mediaItems) {
    if (mediaItems == null || mediaItems.size() < 1) return null;

    int firstIndex = items.size();
    for (MediaItem mediaItem : mediaItems) {
      add(mediaItem);
    }

    return items.get(firstIndex);
  }

  public void remove(PlaylistItem item) {
    items.remove(item);
  }

  public List<PlaylistItem> getItems() {
    return items;
  }

  public void clear() {
    items.clear();
  }

  public PlaylistItem getItemById(String playlistItemId) {
    for (PlaylistItem item : items) {
      if (item.getUuid().equals(playlistItemId)) {
        return item;
      }
    }
    return null;
  }

  public int indexOf(PlaylistItem playlistItem) {
    return items.indexOf(playlistItem);
  }

  @Nullable public PlaylistItem getItemByIndex(int index) {
    if (index < 0 || index >= items.size()) {
      return null;
    }
    return items.get(index);
  }

  public int size() {
    return items.size();
  }

  public int getItemIndex(PlaylistItem playlistItem) {
    return items.indexOf(playlistItem);
  }

  public interface PlaylistChangedListener {
    void onItemAdded(Playlist playlist, PlaylistItem item);
    void onItemRemoved(Playlist playlist, PlaylistItem item);
  }
}

