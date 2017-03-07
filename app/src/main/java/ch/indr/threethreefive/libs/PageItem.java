/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public abstract class PageItem {

  protected BehaviorSubject<String> name = BehaviorSubject.create();
  protected BehaviorSubject<String> description = BehaviorSubject.create();

  public @NonNull String getName() {
    return name.getValue();
  }

  public @NonNull Observable<String> name() {
    if (!name.hasValue()) {
      name.onNext(getName());
    }
    return name;
  }

  public @Nullable String getDescription() {
    return getName();
  }

  public @NonNull Observable<String> description() {
    if (!description.hasValue()) {
      description.onNext(getDescription());
    }
    return description;
  }
}
