/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.navigation;

import com.octo.android.robospice.JacksonGoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.request.listener.RequestListener;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.net.NetworkRequest;

public abstract class SpiceBasePage extends Page {

  private final SpiceManager spiceManager;

  public SpiceBasePage(Environment environment) {
    super(environment);

    spiceManager = new SpiceManager(JacksonGoogleHttpClientSpiceService.class);
  }

  @Override public void onStart() {
    super.onStart();

    spiceManager.start(getContext());
  }

  @Override public void onStop() {
    spiceManager.shouldStop();

    super.onStop();
  }

  protected <TResult> void executeRequest(NetworkRequest<TResult> request, RequestListener<TResult> listener) {
    spiceManager.getFromCacheAndLoadFromNetworkIfExpired(request, request.getCacheKey(), request.getCacheExpiryDuration(), listener);
  }
}
