/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.subjects.BehaviorSubject;

public class PageLink extends PageItem {

  public static final PageLink HomePage = new PageLink(Uri.parse("/"), "Home");
  public static final PageLink NowPlaying = new PageLink(Uri.parse("/now-playing"), "Now Playing");

  protected BehaviorSubject<Uri> uri = BehaviorSubject.create();

  private final String title;
  private final String subtitle;

  public PageLink(@NonNull Uri uri, @Nullable String name) {
    this(uri, name, null);
  }

  public PageLink(@NonNull Uri uri, @Nullable String title, @Nullable String subtitle) {
    this.uri.onNext(uri);
    this.title = title != null ? title : uri.toString();
    this.subtitle = subtitle;
  }

  public @NonNull String getName() {
    return title;
  }

  public @Nullable String getSubtitle() {
    return subtitle;
  }

  @Override public @Nullable String getDescription() {
    return null;
  }

  public @NonNull Uri getUri() {
    return uri.getValue();
  }

  @Override public @NonNull String toString() {
    return "PageLink [name=" + title + ", uri=" + uri + "]";
  }
}
