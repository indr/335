/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.db.playlist;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import ch.indr.threethreefive.libs.MediaItem;
import ch.indr.threethreefive.data.db.playlist.model.Playlist;
import ch.indr.threethreefive.data.db.playlist.model.PlaylistItem;

public class PlaylistManager {

  private final PlaylistsDb playlistsDb;
  private Playlist playlist;

  public PlaylistManager(@NonNull Context context) {
    playlistsDb = new PlaylistsDb(context);
    playlist = new Playlist(playlistsDb.findAll());
  }

  @NonNull public Playlist getPlaylist() {
    return playlist;
  }

  public void addToPlaylist(@NonNull MediaItem mediaItem) {
    PlaylistItem playlistItem = playlistsDb.insert(mediaItem.getName(), mediaItem.getPageUri(), mediaItem.getMediaUri());
    playlist.add(playlistItem);
  }

  public void addToPlaylist(@NonNull List<MediaItem> mediaItems) {
    for (MediaItem each : mediaItems) {
      addToPlaylist(each);
    }
  }

  public void removeFromPlaylist(@NonNull PlaylistItem playlistItem) {
    playlistsDb.delete(playlistItem);
    playlist.remove(playlistItem);
  }

  public void clearPlaylist() {
    playlistsDb.delete();
    playlist.clear();
  }
}
