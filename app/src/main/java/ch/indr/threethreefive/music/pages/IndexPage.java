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

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.navigation.Page;

public class IndexPage extends Page {

  public IndexPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setTitle(context.getResources().getString(R.string.music_on_your_device));

    final PageItemsBuilder builder = pageItemsBuilder();

    builder.addLink("/music/artists", "Artists");
    builder.addLink("/music/albums", "Albums");
    //builder.addLink("/music/genres", "Genres");
    builder.addLink("/music/songs", "Songs");

    setPageItems(builder);
  }
}
