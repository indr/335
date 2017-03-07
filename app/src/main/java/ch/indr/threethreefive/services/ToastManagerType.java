/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.services;

import android.support.annotation.NonNull;

import rx.Observable;

public interface ToastManagerType {

  void toast(String message);

  Observable<Toast> toast();

  void addToastListener(@NonNull ToastListener listener);

  void removeToastListener(@NonNull ToastListener listener);

  interface ToastListener {
    void toast(Toast toast);
  }

  public class Toast {
    private String text;

    public Toast(@NonNull String text) {
      this.text = text;
    }

    public String getText() {
      return text;
    }
  }
}
