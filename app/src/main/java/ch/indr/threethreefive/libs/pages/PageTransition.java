/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.pages;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ch.indr.threethreefive.ui.IntentKey;

public class PageTransition {
  private final Uri pageUri;
  private final String title;
  private boolean replace = false;

  public PageTransition(final @NonNull Uri pageUri) {
    this(pageUri, null);
  }

  public PageTransition(final @NonNull Uri pageUri, final @Nullable String title) {
    this(pageUri, title, false);
  }

  public PageTransition(final @NonNull Uri pageUri, final @Nullable String title, final boolean replace) {
    this.pageUri = pageUri;
    this.title = title;
    this.replace = replace;
  }

  public @NonNull Uri getPageUri() {
    return pageUri;
  }

  public @Nullable String getTitle() {
    return title;
  }

  public boolean getReplace() {
    return replace;
  }

  public static @Nullable PageTransition fromIntent(Intent intent) {
    if (!intent.hasExtra(IntentKey.PAGE_URI)) {
      return null;
    }

    Uri uri = Uri.parse(intent.getStringExtra(IntentKey.PAGE_URI));
    String title = intent.getStringExtra(IntentKey.PAGE_TITLE);
    boolean replace = intent.getBooleanExtra(IntentKey.PAGE_REPLACE, false);

    return new PageTransition(uri, title, replace);
  }
}
