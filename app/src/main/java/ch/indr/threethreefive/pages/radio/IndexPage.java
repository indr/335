/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages.radio;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.pages.Page;

public class IndexPage extends Page {

  public IndexPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setTitle("Radio");

    final PageItemsBuilder builder = pageItemsBuilder();

    // builder.addLink("/radio/recents", R.string.new_stations);
    builder.addLink("/radio/trending", R.string.trending);
    builder.addLink("/radio/genres", R.string.genres);
    builder.addLink("/radio/countries", R.string.countries);
    builder.addLink("/radio/languages", R.string.languages);

    setPageItems(builder);
  }
}
