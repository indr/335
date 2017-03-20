/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages.errors;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.List;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.navigation.Page;

public class NotFound extends Page {

  public NotFound(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setTitle("Not Found");
    final List<PageItem> pageItems = pageItemsBuilder().addText("Page not found: " + uri.toString()).build();
    setPageItems(pageItems);
  }
}
