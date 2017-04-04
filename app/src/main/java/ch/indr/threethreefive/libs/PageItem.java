/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ch.indr.threethreefive.libs.utils.StringUtils;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public abstract class PageItem {

  protected BehaviorSubject<String> title = BehaviorSubject.create();
  protected BehaviorSubject<String> subtitle = BehaviorSubject.create();
  protected BehaviorSubject<String> description = BehaviorSubject.create();

  protected PageItem(final @NonNull String title) {
    this.title.onNext(title);
    this.subtitle.onNext(null);
    this.description.onNext(title);
  }

  protected PageItem(final @NonNull String title, final @Nullable String subtitle, final @NonNull String description) {
    this.title.onNext(title);
    this.subtitle.onNext(StringUtils.isEmpty(subtitle) ? null : subtitle);
    this.description.onNext(description);
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

  public final @Nullable String getDescription() {
    return description.getValue();
  }

  protected void setDescription(final @Nullable String description) {
    this.description.onNext(description);
  }

  public final @NonNull Observable<String> description() {
    return description;
  }
}
