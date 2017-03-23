/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages.music;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import ch.indr.threethreefive.commands.AddToPlaylist;
import ch.indr.threethreefive.commands.PlayMedia;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.MediaItem;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.data.db.music.MusicStore;
import ch.indr.threethreefive.libs.pages.Page;

import static ch.indr.threethreefive.libs.PageUris.musicAlbum;
import static ch.indr.threethreefive.libs.PageUris.musicArtist;
import static ch.indr.threethreefive.data.MediaItemFactory.make;

public class SongPage extends Page {

  public SongPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    final String songId = uri.getLastPathSegment();
    final MusicStore musicStore = new MusicStore(getContext());

    final MusicStore.Song song = musicStore.getSongById(songId);
    if (song != null) {
      setTitle(song.getName());
    } else {
      setTitle("Song + " + songId);
      handle("Song not found: " + songId);
      return;
    }

    final PageItemsBuilder builder = pageItemsBuilder();

    final MediaItem mediaItem = make(song);
    builder.add(new PlayMedia(mediaItem));
    builder.add(new AddToPlaylist(mediaItem));
    builder.addToggleFavorite(getCurrentPageLink());

    builder.addLink(musicArtist(song.getArtistId()), "Artist: " + song.getArtist());
    builder.addLink(musicAlbum(song.getAlbumId()), "Album: " + song.getArtist());

    setPageItems(builder);
  }
}