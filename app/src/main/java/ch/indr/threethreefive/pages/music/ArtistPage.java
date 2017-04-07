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
import ch.indr.threethreefive.data.db.music.model.Album;
import ch.indr.threethreefive.data.db.music.model.Artist;
import ch.indr.threethreefive.data.db.music.model.Song;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.MediaItem;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.libs.pages.Page;

import static ch.indr.threethreefive.data.MediaItemFactory.make;

public class ArtistPage extends Page {

  private final MusicStore musicStore;

  public ArtistPage(Environment environment) {
    super(environment);

    this.musicStore = environment.musicStore();
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    final String artistId = uri.getLastPathSegment();

    final Artist artist = musicStore.getArtistById(artistId);
    if (artist == null) {
      handle(getString(R.string.artist_not_found_error, artistId));
      return;
    }

    setTitle(artist.getName());

    final List<Song> songs = musicStore.getSongsByArtistId(artistId);
    final List<MediaItem> mediaItems = make(songs);

    final PageItemsBuilder builder = pageItemsBuilder();

    builder.add(new PlayMedias(getString(R.string.play_all_albums), mediaItems));
    builder.add(new AddToPlaylist(getString(R.string.add_all_albums_to_playlist), mediaItems));
    builder.addToggleFavorite(getCurrentPageLink());

    final List<Album> albums = musicStore.findAlbumsByArtistId(artistId);
    for (Album album : albums) {
      builder.addLink(PageUris.musicAlbum(album.getId()),
          album.getName(),
          makeSubtitle(album),
          makeDescription(album),
          album.getArtworkUri(),
          R.drawable.ic_default_art);
    }

    setPageItems(builder);
  }

  private String makeDescription(Album album) {
    final int numberOfTracks = album.getNumberOfTracks();
    return album.getName() + ", " + getResources().getQuantityString(R.plurals.music_tracks, numberOfTracks, numberOfTracks);
  }

  private String makeSubtitle(Album album) {
    final int numberOfTracks = album.getNumberOfTracks();
    return getResources().getQuantityString(R.plurals.music_tracks, numberOfTracks, numberOfTracks);
  }
}
