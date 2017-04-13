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

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.libs.pages.Page;

public class IndexPage extends Page {

  public IndexPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setDescription(getString(R.string.mainmenu_music_title));

    final PageItemsBuilder builder = pageItemsBuilder();

    builder.addLink(PageUris.musicArtists(), getString(R.string.music_artists));
    builder.addLink(PageUris.musicAlbums(), getString(R.string.music_albums));
    builder.addLink(PageUris.musicGenres(), getString(R.string.music_genres));
    builder.addLink(PageUris.musicSongs(), getString(R.string.music_songs));

    setPageItems(builder);
  }
}
