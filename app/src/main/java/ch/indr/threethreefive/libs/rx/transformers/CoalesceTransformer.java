/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.rx.transformers;

import android.support.annotation.NonNull;

import rx.Observable;

import static ch.indr.threethreefive.libs.utils.ObjectUtils.coalesceWith;

public final class CoalesceTransformer<T> implements Observable.Transformer<T, T> {
  private final T theDefault;

  public CoalesceTransformer(final @NonNull T theDefault) {
    this.theDefault = theDefault;
  }

  @Override
  public @NonNull Observable<T> call(final @NonNull Observable<T> source) {
    return source
        .map(coalesceWith(theDefault));
  }
}
