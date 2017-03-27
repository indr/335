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
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.libs.pages.Page;

public class IndexPage extends Page {

  public IndexPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setTitle("Radio");

    addPageItems();
  }

  private void addPageItems() {
    final PageItemsBuilder builder = pageItemsBuilder();

    builder.addLink(PageUris.radioTrending(), R.string.trending);
    builder.addLink(PageUris.radioGenres(), R.string.genres);
    builder.addLink(PageUris.radioCountries(), R.string.countries);
    builder.addLink(PageUris.radioLanguages(), R.string.languages);

    setPageItems(builder);
  }
}
