/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.net;

import android.content.Context;
import android.support.annotation.NonNull;

import com.octo.android.robospice.request.listener.RequestListener;

public interface RobospiceManager {

  void start(Context context);

  void shouldStop();

  <TResult> void execute(@NonNull HttpClientSpiceRequest<TResult> request, RequestListener<TResult> listener);
}
