/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;

public class ThreeThreeFiveRobolectricGradleTestRunner extends RobolectricTestRunner {
  public static final int DEFAULT_SDK = 21;

  public ThreeThreeFiveRobolectricGradleTestRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
  }
}
