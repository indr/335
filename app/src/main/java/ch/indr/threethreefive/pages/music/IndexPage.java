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

    setTitle(context.getResources().getString(R.string.mainmenu_music_title));

    final PageItemsBuilder builder = pageItemsBuilder();

    builder.addLink(PageUris.musicArtists(), "Artists");
    builder.addLink(PageUris.musicAlbums(), "Albums");
    builder.addLink(PageUris.musicGenres(), "Genres");
    builder.addLink(PageUris.musicSongs(), "Songs");

    setPageItems(builder);
  }
}
