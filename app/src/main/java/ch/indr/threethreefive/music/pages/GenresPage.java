/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
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

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.music.MusicStore;
import ch.indr.threethreefive.navigation.Page;

import static ch.indr.threethreefive.libs.PageUris.makeGenreUri;

public class GenresPage extends Page {

  public GenresPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setTitle("Genres");

    final PageItemsBuilder builder = pageItemsBuilder();
    final MusicStore musicStore = new MusicStore(getContext());

    final List<MusicStore.Genre> genres = musicStore.queryGenres();
    for (MusicStore.Genre genre : genres) {
      builder.addLink(makeGenreUri(genre.getId()), genre.getName());
    }

    setPageItems(builder);
  }
}
