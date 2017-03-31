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
import ch.indr.threethreefive.data.db.music.model.Genre;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.pages.Page;

import static ch.indr.threethreefive.libs.PageUris.musicGenre;

public class GenresPage extends Page {

  public GenresPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setTitle("Genres");

    final MusicStore musicStore = new MusicStore(getContext());

    final List<Genre> genres = musicStore.queryGenres();
    if (genres.size() == 0) {
      handle(getString(R.string.no_genres_found));
      return;
    }

    final PageItemsBuilder builder = pageItemsBuilder();
    for (Genre genre : genres) {
      builder.addLink(musicGenre(genre.getId()), genre.getName());
    }

    setPageItems(builder);
  }
}
