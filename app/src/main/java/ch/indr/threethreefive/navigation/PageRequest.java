/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.navigation;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ch.indr.threethreefive.ui.IntentKey;

public class PageRequest {

  public static PageRequest HomePage = new PageRequest(Uri.parse("/"), "Home");

  private final Uri uri;
  private final String title;

  private PageRequest(@NonNull Uri uri, final @Nullable String title) {
    this.uri = uri;
    this.title = title;
  }

  public @NonNull Uri getUri() {
    return uri;
  }

  public @Nullable String getTitle() {
    return title;
  }

  public static @Nullable PageRequest fromIntent(Intent intent) {
    if (!intent.hasExtra(IntentKey.PAGE_URI)) {
      return null;
    }

    Uri uri = Uri.parse(intent.getStringExtra(IntentKey.PAGE_URI));
    String title = null;

    if (intent.hasExtra(IntentKey.PAGE_TITLE)) {
      title = intent.getStringExtra(IntentKey.PAGE_TITLE);
    }

    return new PageRequest(uri, title);
  }
}
