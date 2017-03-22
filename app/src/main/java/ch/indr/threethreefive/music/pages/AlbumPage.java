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

public class AlbumPage extends Page {

  public AlbumPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    final String albumId = uri.getLastPathSegment();
    final MusicStore musicStore = new MusicStore(getContext());

    final MusicStore.Album album = musicStore.getAlbumById(albumId);
    if (album != null) {
      setTitle(album.getName());
    } else {
      setTitle("Album");
    }

    final PageItemsBuilder builder = pageItemsBuilder();

    final List<MusicStore.Song> songs = musicStore.getSongsByAlbumId(albumId);
    final List<MediaItem> mediaItems = make(songs);

    builder.addItem(new PlayMedias("Play all Songs", mediaItems));
    builder.addItem(new AddToPlaylist("Add all Songs to Playlist", mediaItems));
    builder.addToggleFavorite(getCurrentPageLink());

    for (MusicStore.Song song : songs) {
      builder.addLink(musicSong(song.getId()), song.getName());
    }

    setPageItems(builder);
  }
}
