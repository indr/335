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

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class PageLink extends PageItem {
  public static final PageLink HomePage = new PageLink(PageUris.home(), new Description("Home"));
  public static final PageLink NowPlaying = new PageLink(PageUris.nowPlaying(), new Description("Now Playing"));

  private BehaviorSubject<Uri> uri = BehaviorSubject.create();
  private boolean replace;

  public PageLink(@NonNull Uri uri, final @NonNull Description description) {
    super(description);
    this.uri.onNext(uri);
  }

  public PageLink(@NonNull Uri uri, final @NonNull Description description, final boolean replace) {
    super(description);
    this.uri.onNext(uri);
    this.replace = replace;
  }

  public PageLink(final @NonNull Uri uri, final @NonNull Description description,
                  final @Nullable Uri iconUri, final int defaultIconResId) {
    super(description, iconUri, defaultIconResId);
    this.uri.onNext(uri);
  }

  public @NonNull Uri getUri() {
    return uri.getValue();
  }

  protected void setUri(final @NonNull Uri uri) {
    this.uri.onNext(uri);
  }

  public @NonNull Observable<Uri> uri() {
    return this.uri;
  }

  public boolean getReplace() {
    return replace;
  }

  @Override public @NonNull String toString() {
    return "PageLink{" +
        "uri='" + uri.getValue() + '\'' +
        ", description=" + getDescription() +
        ", iconUri=" + getIconUri() + '\'' +
        ", defaultIconResId=" + getDefaultIconResId() + '\'' +
        '}';
  }
}
