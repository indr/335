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

import ch.indr.threethreefive.data.db.favorites.FavoritesStore;
import ch.indr.threethreefive.data.db.music.MusicStore;
import ch.indr.threethreefive.data.network.ApiClient;
import ch.indr.threethreefive.libs.Preferences;
import ch.indr.threethreefive.libs.net.RobospiceManager;
import ch.indr.threethreefive.libs.qualifiers.ApplicationContext;
import ch.indr.threethreefive.services.CommandSpeaker;
import ch.indr.threethreefive.services.InstructionsSpeaker;
import ch.indr.threethreefive.services.Speaker;
import ch.indr.threethreefive.services.UiModeManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestAppModule extends AppModule {

  private ApiClient apiClient;
  private FavoritesStore favoritesStore;
  private MusicStore musicStore;
  private RobospiceManager robospiceManager;
  private Speaker speaker;
  private UiModeManager uiModeManager;

  public TestAppModule(@NonNull Application application) {
    super(application);
  }

  @NonNull @Override public ApiClient apiClient(@ApplicationContext @NonNull Context context) {
    if (apiClient == null) apiClient = mock(ApiClient.class);
    return apiClient;
  }

  @NonNull @Override public FavoritesStore provideFavoritesStore(@ApplicationContext @NonNull Context context) {
    if (favoritesStore == null) favoritesStore = mock(FavoritesStore.class);
    return favoritesStore;
  }

  @NonNull @Override public MusicStore provideMusicStore(@ApplicationContext @NonNull Context context) {
    if (musicStore == null) musicStore = mock(MusicStore.class);
    return musicStore;
  }

  @NonNull @Override public RobospiceManager provideRobospiceManager() {
    if (robospiceManager == null) robospiceManager = mock(RobospiceManager.class);
    return robospiceManager;
  }

  @NonNull @Override public Speaker provideSpeaker(@ApplicationContext @NonNull Context context) {
    if (speaker == null) {
      speaker = mock(Speaker.class);
      when(speaker.instructions()).thenReturn(mock(InstructionsSpeaker.class));
      when(speaker.command()).thenReturn(mock(CommandSpeaker.class));
    }
    return speaker;
  }

  @NonNull @Override public UiModeManager provideUiModeManager(@ApplicationContext @NonNull Context context, Preferences preferences) {
    if (uiModeManager == null) uiModeManager = mock(UiModeManager.class);
    return uiModeManager;
  }
}
