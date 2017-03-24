/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.net;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public abstract class ResponseTransformer<TResult, TResponse> implements RequestListener<TResult> {

  protected final RequestListener<TResponse> listener;

  public ResponseTransformer(RequestListener<TResponse> listener) {
    this.listener = listener;
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    listener.onRequestFailure(spiceException);
  }
}
