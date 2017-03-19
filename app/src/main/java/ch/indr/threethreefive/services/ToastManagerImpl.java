/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.services;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

public class ToastManagerImpl implements ToastManager {
  private List<ToastListener> toastListeners = new ArrayList<ToastListener>();
  private PublishSubject<Toast> toast = PublishSubject.create();

  @Override public void toast(@NonNull String message) {
    Toast toast = new Toast(message);
    this.toast.onNext(toast);

    for (ToastListener listener : toastListeners) {
      listener.toast(toast);
    }
  }

  @Override public Observable<Toast> toast() {
    return toast;
  }

  @Override public void addToastListener(@NonNull ToastListener listener) {
    toastListeners.add(listener);
  }

  @Override public void removeToastListener(@NonNull ToastListener listener) {
    toastListeners.remove(listener);
  }
}
