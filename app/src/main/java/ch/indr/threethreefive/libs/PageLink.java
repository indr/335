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

  public static final PageLink HomePage = new PageLink("/", "Home");
  public static final PageLink NowPlaying = new PageLink("/now-playing", "Now Playing");

  protected BehaviorSubject<Uri> uri = BehaviorSubject.create();

  private final String title;

  public PageLink(@NonNull Uri uri, @Nullable String name) {
    this.uri.onNext(uri);
    this.title = name != null ? name : uri.toString();
  }

  public PageLink(@NonNull String uri, @Nullable String name) {
    this(Uri.parse(uri), name);
  }

  public @NonNull String getName() {
    return title;
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
