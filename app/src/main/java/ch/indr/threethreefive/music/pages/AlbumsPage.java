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

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.music.MusicStore;
import ch.indr.threethreefive.navigation.Page;

import static ch.indr.threethreefive.libs.PageUris.makeAlbumUri;

public class AlbumsPage extends Page {

  public AlbumsPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setTitle("Albums");

    final PageItemsBuilder builder = pageItemsBuilder();
    final MusicStore musicStore = new MusicStore(getContext());

    final List<MusicStore.Album> albums = musicStore.queryAlbums(null);
    for (MusicStore.Album album : albums) {
      builder.addLink(makeAlbumUri(album.getId()), album.getName(), makeSubtitle(album), makeDescription(album));
    }

    setPageItems(builder);
  }

  private String makeDescription(MusicStore.Album album) {
    final int numberOfTracks = album.getNumberOfTracks();
    return album.getName() + " by " + album.getArtist() + ", "
        + getResources().getQuantityString(R.plurals.music_tracks, numberOfTracks, numberOfTracks);
  }

  private String makeSubtitle(MusicStore.Album album) {
    final int numberOfTracks = album.getNumberOfTracks();
    return album.getArtist() + ", "
        + getResources().getQuantityString(R.plurals.music_tracks, numberOfTracks, numberOfTracks);
  }
}
