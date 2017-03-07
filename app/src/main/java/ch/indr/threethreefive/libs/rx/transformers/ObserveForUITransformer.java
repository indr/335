/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.rx.transformers;

import ch.indr.threethreefive.libs.utils.ThreadUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class ObserveForUITransformer<T> implements Observable.Transformer<T, T> {
  @Override
  public Observable<T> call(Observable<T> source) {

    return source.flatMap(value -> {
      if (ThreadUtils.isMainThread()) {
        return Observable.just(value).observeOn(Schedulers.immediate());
      } else {
        return Observable.just(value).observeOn(AndroidSchedulers.mainThread());
      }
    });
  }
}
