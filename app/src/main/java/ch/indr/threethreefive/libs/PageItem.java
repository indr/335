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

public abstract class PageItem {
  private final BehaviorSubject<Description> description = BehaviorSubject.create(Description.EMPTY);
  private final BehaviorSubject<Uri> iconUri = BehaviorSubject.create();
  private final int defaultIconResId;

  protected PageItem() {
    this(Description.EMPTY);
  }

  protected PageItem(final @NonNull Description description) {
    this.description.onNext(description);
    this.iconUri.onNext(null);
    this.defaultIconResId = 0;
  }

  protected PageItem(final @NonNull Description description, final @Nullable Uri iconUri,
                     final int defaultIconResId) {
    this.description.onNext(description);
    this.iconUri.onNext(iconUri);
    this.defaultIconResId = defaultIconResId;
  }

  public final @NonNull String getTitle() {
    return description.getValue().getTitle();
  }

  protected void setTitle(final @NonNull String title) {
    description.onNext(description.getValue().setTitle(title));
  }

  public final @Nullable String getSubtitle() {
    return description.getValue().getSubtitle();
  }

  protected void setSubtitle(final @Nullable String subtitle) {
    description.onNext(description.getValue().setSubtitle(subtitle));
  }

  public final @Nullable String getContentDescription() {
    return description.getValue().getContentDescription();
  }

  protected void setContentDescription(final @Nullable String contentDescription) {
    description.onNext(description.getValue().setContentDescription(contentDescription));
  }

  @NonNull public final Observable<Description> description() {
    return description;
  }

  @NonNull public final Description getDescription() {
    return description.getValue();
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

  @Override public @NonNull String toString() {
    return "PageItem{" +
        "description=" + getDescription() +
        ", iconUri=" + getIconUri() + '\'' +
        ", defaultIconResId=" + getDefaultIconResId() + '\'' +
        '}';
  }
}
