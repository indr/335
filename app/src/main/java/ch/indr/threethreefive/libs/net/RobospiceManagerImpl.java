/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.net;

import android.content.Context;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.request.listener.RequestListener;

public class RobospiceManagerImpl implements RobospiceManager {

  private final SpiceManager spiceManager;

  public RobospiceManagerImpl() {
    this.spiceManager = new SpiceManager(HttpClientSpiceService.class);
  }

  @Override public void start(Context context) {
    this.spiceManager.start(context);
  }

  @Override public void shouldStop() {
    this.spiceManager.shouldStop();
  }

  @Override public <TResult> void getFromCacheAndLoadFromNetworkIfExpired(HttpClientSpiceRequest<TResult> request, String cacheKey, long cacheExpiryDuration, RequestListener<TResult> listener) {
    this.spiceManager.getFromCacheAndLoadFromNetworkIfExpired(request, cacheKey, cacheExpiryDuration, listener);
  }
}
