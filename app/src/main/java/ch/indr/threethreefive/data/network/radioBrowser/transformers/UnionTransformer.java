/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser.transformers;

import android.support.annotation.NonNull;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnionTransformer<T> implements RequestListener<T[]> {

  private final Object mutex = new Object();

  private int waitFor;
  private Boolean completed = false;

  private final RequestListener<List<T>> listener;

  private Set<T> result = new HashSet<>();

  public UnionTransformer(final int waitFor, final @NonNull RequestListener<List<T>> listener) {
    if (waitFor <= 0) throw new IllegalArgumentException("waitFor must be greater than zero");

    this.waitFor = waitFor;
    this.listener = listener;
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    synchronized (mutex) {
      if (completed) return;
      this.waitFor -= 1;
      this.completed = true;

      listener.onRequestFailure(spiceException);
    }
  }

  @Override public void onRequestSuccess(T[] results) {
    synchronized (mutex) {
      if (completed) return;
      this.waitFor -= 1;
      this.completed = this.waitFor == 0;

      this.result.addAll(Arrays.asList(results));

      if (completed) {
        listener.onRequestSuccess(new ArrayList<>(this.result));
      }
    }
  }
}
