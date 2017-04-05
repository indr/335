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

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.commands.AddToPlaylist;
import ch.indr.threethreefive.commands.PlayMedia;
import ch.indr.threethreefive.data.db.music.MusicStore;
import ch.indr.threethreefive.data.db.music.model.Song;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.MediaItem;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.pages.Page;

import static ch.indr.threethreefive.data.MediaItemFactory.make;
import static ch.indr.threethreefive.libs.PageUris.musicAlbum;
import static ch.indr.threethreefive.libs.PageUris.musicArtist;

public class SongPage extends Page {

  private final MusicStore musicStore;

  public SongPage(Environment environment) {
    super(environment);

    this.musicStore = environment.musicStore();
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    final String songId = uri.getLastPathSegment();

    final Song song = musicStore.getSongById(songId);
    if (song == null) {
      handle(getString(R.string.song_not_found_error, songId));
      return;
    }

    setTitle(song.getName());
    setIconUri(song.getAlbumArtworkUri());

    final MediaItem mediaItem = make(song);

    final PageItemsBuilder builder = pageItemsBuilder();

    builder.add(new PlayMedia(mediaItem));
    builder.add(new AddToPlaylist(mediaItem));
    builder.addToggleFavorite(getCurrentPageLink());

    builder.addLink(musicArtist(song.getArtistId()), "Artist: " + song.getArtist());
    builder.addLink(musicAlbum(song.getAlbumId()), "Album: " + song.getArtist());

    setPageItems(builder);
  }
}
