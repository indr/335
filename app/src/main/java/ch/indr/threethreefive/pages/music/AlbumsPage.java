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
import ch.indr.threethreefive.data.db.music.MusicStore;
import ch.indr.threethreefive.data.db.music.model.Album;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.pages.Page;

import static ch.indr.threethreefive.libs.PageUris.musicAlbum;

public class AlbumsPage extends Page {

  public AlbumsPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setDescription(getString(R.string.albums));

    final MusicStore musicStore = new MusicStore(getContext());

    final List<Album> albums = musicStore.queryAlbums(null);
    if (albums.size() == 0) {
      handle(getString(R.string.no_albums_found));
      return;
    }

    final PageItemsBuilder builder = pageItemsBuilder();
    for (Album album : albums) {
      builder.addLink(musicAlbum(album.getId()),
          album.getName(),
          makeSubtitle(album),
          makeContentDescription(album),
          album.getArtworkUri(),
          R.drawable.ic_default_art);
    }

    setPageItems(builder);
  }

  private String makeContentDescription(Album album) {
    final int numberOfTracks = album.getNumberOfTracks();
    return album.getName() + " by " + album.getArtist() + ", "
        + getResources().getQuantityString(R.plurals.music_tracks, numberOfTracks, numberOfTracks);
  }

  private String makeSubtitle(Album album) {
    final int numberOfTracks = album.getNumberOfTracks();
    return album.getArtist() + ", "
        + getResources().getQuantityString(R.plurals.music_tracks, numberOfTracks, numberOfTracks);
  }
}
