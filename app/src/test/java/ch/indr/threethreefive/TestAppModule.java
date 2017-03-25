/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import ch.indr.threethreefive.data.network.ApiClient;
import ch.indr.threethreefive.libs.net.RobospiceManager;
import ch.indr.threethreefive.libs.qualifiers.ApplicationContext;
import dagger.Provides;

import static org.mockito.Mockito.mock;

public class TestAppModule extends AppModule {

  private ApiClient apiClient;
  private RobospiceManager robospiceManager;

  public TestAppModule(@NonNull Application application) {
    super(application);
  }

  @Provides
  @NonNull @Override public ApiClient apiClient(@NonNull @ApplicationContext Context context) {
    if (apiClient == null) apiClient = mock(ApiClient.class);
    return apiClient;
  }

  @Provides
  @NonNull @Override public RobospiceManager provideRobospiceManager() {
    if (robospiceManager == null) robospiceManager = mock(RobospiceManager.class);
    return robospiceManager;
  }
}
