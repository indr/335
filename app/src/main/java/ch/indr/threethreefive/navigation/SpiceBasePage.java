/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.navigation;

import com.octo.android.robospice.request.listener.RequestListener;

import javax.inject.Inject;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.net.HttpClientSpiceRequest;
import ch.indr.threethreefive.libs.net.RobospiceManager;

public abstract class SpiceBasePage extends Page {

  protected @Inject RobospiceManager robospiceManager;

  public SpiceBasePage(Environment environment) {
    super(environment);
  }

  @Override public void onStart() {
    super.onStart();

    if (robospiceManager == null) {
      throw new RuntimeException("RobospiceManager has not been injected. Did you call component.inject(this) in your subclass of SpiceBasePage?");
    }
    robospiceManager.start(getContext());
  }

  @Override public void onStop() {
    robospiceManager.shouldStop();

    super.onStop();
  }

  protected <TResult> void executeRequest(HttpClientSpiceRequest<TResult> request, RequestListener<TResult> listener) {
    robospiceManager.getFromCacheAndLoadFromNetworkIfExpired(request, request.getCacheKey(), request.getCacheExpiryDuration(), listener);
  }
}
