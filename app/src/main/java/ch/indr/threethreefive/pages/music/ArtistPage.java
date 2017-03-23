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

import ch.indr.threethreefive.commands.AddToPlaylist;
import ch.indr.threethreefive.commands.PlayMedias;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.MediaItem;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.data.db.music.MusicStore;
import ch.indr.threethreefive.libs.pages.Page;

import static ch.indr.threethreefive.data.MediaItemFactory.make;


public class ArtistPage extends Page {

  public ArtistPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    final String artistId = uri.getLastPathSegment();
    final MusicStore musicStore = new MusicStore(getContext());

    final MusicStore.Artist artist = musicStore.getArtistById(artistId);
    if (artist != null) {
      setTitle(artist.getName());
    } else {
      setTitle("Artist");
    }

    final PageItemsBuilder builder = pageItemsBuilder();

    final List<MusicStore.Song> songs = musicStore.getSongsByArtistId(artistId);
    final List<MediaItem> mediaItems = make(songs);

    builder.add(new PlayMedias("Play all Albums", mediaItems));
    builder.add(new AddToPlaylist("Add all Albums to Playlist", mediaItems));
    builder.addToggleFavorite(getCurrentPageLink());

    final List<MusicStore.Album> albums = musicStore.findAlbumsByArtistId(artistId);
    for (MusicStore.Album album : albums) {
      builder.addLink(PageUris.musicAlbum(album.getId()), album.getName());
    }

    setPageItems(builder);
  }
}
