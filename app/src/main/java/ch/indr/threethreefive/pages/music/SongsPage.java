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

import java.util.List;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.commands.AddToPlaylist;
import ch.indr.threethreefive.commands.PlayMedias;
import ch.indr.threethreefive.data.db.music.MusicStore;
import ch.indr.threethreefive.data.db.music.model.Song;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.MediaItem;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.pages.Page;

import static ch.indr.threethreefive.data.MediaItemFactory.make;
import static ch.indr.threethreefive.libs.PageUris.musicSong;

public class SongsPage extends Page {

  public SongsPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setTitle("Songs");

    final MusicStore musicStore = new MusicStore(getContext());

    final List<Song> songs = musicStore.querySongs(null, "title");
    if (songs.size() == 0) {
      handle(getString(R.string.no_songs_found));
      return;
    }
    final List<MediaItem> mediaItems = make(songs);

    final PageItemsBuilder builder = pageItemsBuilder();
    builder.add(new PlayMedias("Play all Songs", mediaItems));
    builder.add(new AddToPlaylist("Add all Songs to Playlist", mediaItems));
    builder.addToggleFavorite(getCurrentPageLink());

    for (Song song : songs) {
      builder.addLink(musicSong(song.getId()), song.getName(), makeSubtitle(song), makeDescription(song));
    }

    setPageItems(builder);
  }

  private String makeDescription(Song song) {
    return song.getName() + " by " + song.getArtist();
  }

  private String makeSubtitle(Song song) {
    return song.getArtist();
  }
}
