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

import ch.indr.threethreefive.libs.utils.StringUtils;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public abstract class PageItem {

  private final int defaultIconResId;

  protected BehaviorSubject<String> title = BehaviorSubject.create();
  protected BehaviorSubject<String> subtitle = BehaviorSubject.create();
  protected BehaviorSubject<String> contentDescription = BehaviorSubject.create();
  protected BehaviorSubject<Uri> iconUri = BehaviorSubject.create();

  protected PageItem(final @NonNull String title) {
    this(title, null, title);
  }

  protected PageItem(final @NonNull String title, final @Nullable String subtitle,
                     final @NonNull String contentDescription) {
    this(title, subtitle, contentDescription, null, 0);
  }

  protected PageItem(final @NonNull String title, final @Nullable String subtitle,
                     final @NonNull String contentDescription, final @Nullable Uri iconUri,
                     final int defaultIconResId) {
    this.title.onNext(title);
    this.subtitle.onNext(StringUtils.isEmpty(subtitle) ? null : subtitle);
    this.contentDescription.onNext(contentDescription);
    this.iconUri.onNext(iconUri);
    this.defaultIconResId = defaultIconResId;
  }

  public final @NonNull String getTitle() {
    return title.getValue();
  }

  protected void setTitle(final @NonNull String title) {
    this.title.onNext(title);
  }

  public final @NonNull Observable<String> title() {
    return title;
  }

  public final @Nullable String getSubtitle() {
    return subtitle.getValue();
  }

  protected void setSubtitle(final @Nullable String subtitle) {
    this.subtitle.onNext(StringUtils.isEmpty(subtitle) ? null : subtitle);
  }

  public final @NonNull Observable<String> subtitle() {
    return subtitle;
  }

  public final @Nullable String getContentDescription() {
    return contentDescription.getValue();
  }

  protected void setContentDescription(final @Nullable String contentDescription) {
    this.contentDescription.onNext(contentDescription);
  }

  public final @NonNull Observable<String> description() {
    return contentDescription;
  }

  public final @Nullable Uri getIconUri() {
    return iconUri.getValue();
  }

  public final @NonNull Observable<Uri> iconUri() {
    return iconUri;
  }

  public final int getDefaultIconResId() {
    return this.defaultIconResId;
  }
}
