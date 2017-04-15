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
import ch.indr.threethreefive.data.db.music.model.Genre;
import ch.indr.threethreefive.data.db.music.model.Song;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.MediaItem;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.pages.Page;

import static ch.indr.threethreefive.data.MediaItemFactory.make;
import static ch.indr.threethreefive.libs.PageUris.musicSong;

public class GenrePage extends Page {
  private final MusicStore musicStore;

  public GenrePage(Environment environment) {
    super(environment);

    this.musicStore = environment.musicStore();
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    setFavorable(true);

    final long genreId = Long.parseLong(uri.getLastPathSegment());

    final Genre genre = musicStore.getGenreById(genreId);
    if (genre == null) {
      handle(getString(R.string.genre_not_found_error, genreId));
      return;
    }
    setDescription(genre.getName(), getString(R.string.music_genre));

    final List<Song> songs = musicStore.getSongsByGenreId(genreId);
    if (songs.size() == 0) {
      handle(getString(R.string.no_songs_found));
      return;
    }
    final List<MediaItem> mediaItems = make(songs);

    final PageItemsBuilder builder = pageItemsBuilder();
    builder.add(new PlayMedias(getString(R.string.play_all_songs), mediaItems));
    builder.add(new AddToPlaylist(getString(R.string.add_all_songs_to_playlist), mediaItems));
    builder.addToggleFavorite(getCurrentPageLink());

    for (Song song : songs) {
      builder.addLink(musicSong(song.getId()),
          song.getName(),
          song.getArtist(),
          getString(R.string.speech_song_by_artist, song.getName(), song.getArtist()),
          song.getAlbumArtworkUri(),
          R.drawable.ic_default_art);
    }

    setPageItems(builder);
  }
}
