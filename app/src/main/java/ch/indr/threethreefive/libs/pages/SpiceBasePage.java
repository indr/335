/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.pages;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import ch.indr.threethreefive.data.network.ApiClient;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.net.RobospiceManager;

public abstract class SpiceBasePage extends Page {

  protected @Inject ApiClient apiClient;
  protected @Inject RobospiceManager robospiceManager;

  public SpiceBasePage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
  }

  @Override public void onStart() {
    super.onStart();

    if (robospiceManager == null) {
      throw new RuntimeException("RobospiceManager has not been injected. Did you call component.inject(this) in your subclass of SpiceBasePage?");
    }
    apiClient.setRobospiceManager(robospiceManager);
    robospiceManager.start(getContext());
  }

  @Override public void onStop() {
    robospiceManager.shouldStop();

    super.onStop();
  }
}
