/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.androidnetworking.AndroidNetworking;

import timber.log.Timber;

public class ThreeThreeFiveApp extends MultiDexApplication {

  private AppComponent component;

  @CallSuper
  @Override public void onCreate() {
    super.onCreate();

    MultiDex.install(this);

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }

    if (!isInUnitTests()) {
      AndroidNetworking.initialize(getApplicationContext());
    }

    component = DaggerAppComponent.builder()
        .appModule(getAppModule())
        .build();
    component().inject(this);
  }

  @NonNull protected AppModule getAppModule() {
    return new AppModule(this);
  }

  public AppComponent component() {
    return component;
  }

  public boolean isInUnitTests() {
    return false;
  }
}
