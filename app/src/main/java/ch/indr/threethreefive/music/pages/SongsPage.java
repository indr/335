/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.music.pages;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.List;

import ch.indr.threethreefive.commands.AddToPlaylist;
import ch.indr.threethreefive.commands.PlayMedias;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.MediaItem;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.music.MusicStore;
import ch.indr.threethreefive.navigation.Page;

import static ch.indr.threethreefive.libs.PageUris.musicSong;
import static ch.indr.threethreefive.music.MusicMediaItemFactory.make;

public class SongsPage extends Page {

  public SongsPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setTitle("Songs");

    final MusicStore musicStore = new MusicStore(getContext());
    final PageItemsBuilder builder = pageItemsBuilder();

    final List<MusicStore.Song> songs = musicStore.querySongs(null, "title");
    final List<MediaItem> mediaItems = make(songs);

    builder.addItem(new PlayMedias("Play all Songs", mediaItems));
    builder.addItem(new AddToPlaylist("Add all Songs to Playlist", mediaItems));
    builder.addToggleFavorite(getCurrentPageLink());

    for (MusicStore.Song song : songs) {
      builder.addLink(musicSong(song.getId()), song.getName(), makeSubtitle(song), makeDescription(song));
    }

    setPageItems(builder);
  }

  private String makeDescription(MusicStore.Song song) {
    return song.getName() + " by " + song.getArtist();
  }

  private String makeSubtitle(MusicStore.Song song) {
    return song.getArtist();
  }
}
