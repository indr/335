/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive;

import android.app.Application;
import android.support.annotation.NonNull;

import ch.indr.threethreefive.libs.net.RobospiceManager;
import dagger.Provides;

import static org.mockito.Mockito.mock;

public class TestAppModule extends AppModule {

  private RobospiceManager robospiceManager;

  public TestAppModule(@NonNull Application application) {
    super(application);
  }

  @Provides
  @Override public RobospiceManager provideRobospiceManager() {
    if (robospiceManager == null) robospiceManager = mock(RobospiceManager.class);
    return robospiceManager;
  }
}
