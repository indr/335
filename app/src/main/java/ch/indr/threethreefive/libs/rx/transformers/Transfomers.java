/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.rx.transformers;

import android.support.annotation.NonNull;

import rx.Observable;

public final class Transfomers {
  private Transfomers() {
  }

  /**
   * Transforms `null` values emitted from an observable into `theDefault`.
   */
  public static @NonNull <T> CoalesceTransformer<T> coalesce(final @NonNull T theDefault) {
    return new CoalesceTransformer<>(theDefault);
  }

  /**
   * Emits the latest values from two observables whenever either emits.
   */
  public static <S, T> CombineLatestPairTransformer<S, T> combineLatestPair(final @NonNull Observable<T> second) {
    return new CombineLatestPairTransformer<>(second);
  }

  /**
   * If called on the main thread, schedule the work immediately. Otherwise delay execution of the work by adding it
   * to a message queue, where it will be executed on the main thread.
   */
  public static @NonNull <T> ObserveForUITransformer<T> observeForUI() {
    return new ObserveForUITransformer<>();
  }

  /**
   * Emits the latest value of the source observable whenever the `when`
   * observable emits.
   */
  public static <S, T> TakeWhenTransformer<S, T> takeWhen(final @NonNull Observable<T> when) {
    return new TakeWhenTransformer<>(when);
  }

  /**
   * Emits the latest value of the source `when` observable whenever the
   * `when` observable emits.
   */
  public static <S, T> TakePairWhenTransformer<S, T> takePairWhen(final @NonNull Observable<T> when) {
    return new TakePairWhenTransformer<>(when);
  }
}
