/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive;

import android.content.Context;
import android.support.annotation.NonNull;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.multidex.ShadowMultiDex;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.navigation.PageResolver;

@RunWith(ThreeThreeFiveRobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, shadows = ShadowMultiDex.class, sdk = ThreeThreeFiveRobolectricGradleTestRunner.DEFAULT_SDK)
public class ThreeThreeFiveRobolectricTestCase extends TestCase {
  private TestThreeThreeFiveApplication application;
  private Environment environment;


  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();


    AppComponent component = application().component();
    Environment env = component.environment();
    environment = env.toBuilder()
        .build();
  }

  protected @NonNull TestThreeThreeFiveApplication application() {
    if (application != null) {
      return application;
    }

    application = (TestThreeThreeFiveApplication) RuntimeEnvironment.application;
    return application;
  }

  protected @NonNull Context context() {
    return application().getApplicationContext();
  }

  protected @NonNull Environment environment() {
    return environment;
  }
}
