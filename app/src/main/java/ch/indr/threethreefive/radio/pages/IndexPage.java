/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.radio.pages;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.navigation.Page;

public class IndexPage extends Page {

  public IndexPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setTitle("Radio");

    final PageItemsBuilder builder = pageItemsBuilder();

    builder.addLink("/radio/recents", "New Stations");
    builder.addLink("/radio/genres", "Genres");
    builder.addLink("/radio/countries", "Countries");
    builder.addLink("/radio/languages", "Languages");

    setPageItems(builder);
  }
}
