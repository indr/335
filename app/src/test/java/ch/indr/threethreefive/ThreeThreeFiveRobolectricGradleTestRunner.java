/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;

import rx.Scheduler;
import rx.plugins.RxJavaSchedulersHook;
import rx.plugins.RxJavaTestPlugins;
import rx.schedulers.Schedulers;

public class ThreeThreeFiveRobolectricGradleTestRunner extends RobolectricTestRunner {
  public static final int DEFAULT_SDK = 21;

  public ThreeThreeFiveRobolectricGradleTestRunner(Class<?> testClass) throws InitializationError {
    super(testClass);

    RxJavaTestPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
      @Override public Scheduler getIOScheduler() {
        return Schedulers.immediate();
      }
    });
  }
}
