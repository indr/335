/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive;

import android.support.annotation.NonNull;

public class TestTtfApplication extends ThreeThreeFiveApp {

  private TestAppModule appModule;

  @NonNull @Override protected AppModule getAppModule() {
    appModule = new TestAppModule(this);
    return appModule;
  }

  @NonNull protected TestAppModule appModule() {
    return appModule;
  }

  @Override public boolean isInUnitTests() {
    return true;
  }
}
