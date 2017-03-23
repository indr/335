/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.example.android.uamp.playback.QueueManager;
import com.example.android.uamp.playback.QueueManagerImpl;

import javax.inject.Singleton;

import ch.indr.threethreefive.data.network.ApiClient;
import ch.indr.threethreefive.favorites.FavoritesStore;
import ch.indr.threethreefive.favorites.FavoritesStoreImpl;
import ch.indr.threethreefive.libs.Build;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.Preferences;
import ch.indr.threethreefive.libs.PreferencesImpl;
import ch.indr.threethreefive.libs.net.RobospiceManager;
import ch.indr.threethreefive.libs.net.RobospiceManagerImpl;
import ch.indr.threethreefive.libs.preferences.IntPreferenceType;
import ch.indr.threethreefive.libs.qualifiers.ApplicationContext;
import ch.indr.threethreefive.libs.qualifiers.AutoRepeatModePreference;
import ch.indr.threethreefive.pages.RootPageResolver;
import ch.indr.threethreefive.playlist.PlaylistManager;
import ch.indr.threethreefive.services.PlaybackAnnouncer;
import ch.indr.threethreefive.services.PlaybackAnnouncerImpl;
import ch.indr.threethreefive.services.PlaybackClient;
import ch.indr.threethreefive.services.PlaybackClientImpl;
import ch.indr.threethreefive.services.Speaker;
import ch.indr.threethreefive.services.SpeakerImpl;
import ch.indr.threethreefive.services.ToastManager;
import ch.indr.threethreefive.services.ToastManagerImpl;
import ch.indr.threethreefive.services.UiModeManager;
import ch.indr.threethreefive.services.UiModeManagerImpl;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

  private final Application application;

  public AppModule(final @NonNull Application application) {
    this.application = application;
  }

  @Provides
  @Singleton
  Environment provideEnvironment(
      final @NonNull FavoritesStore favoritesStore,
      final @NonNull PlaybackClient playbackClient,
      final @NonNull QueueManager queueManager,
      final @NonNull SharedPreferences sharedPreferences,
      final @NonNull Preferences preferences,
      final @NonNull Speaker speaker,
      final @NonNull ToastManager toastManager) {
    return Environment.builder()
        .favoritesStore(favoritesStore)
        .playbackClient(playbackClient)
        .queueManager(queueManager)
        .sharedPreferences(sharedPreferences)
        .preferences(preferences)
        .speaker(speaker)
        .toastManager(toastManager)
        .build();
  }

  @Provides
  @NonNull ApiClient apiClient() {
    return new ApiClient();
  }

  @Provides
  @Singleton
  Application provideApplication() {
    return application;
  }

  @Provides
  @Singleton
  @ApplicationContext
  Context provideApplicationContext() {
    return application;
  }

  @Provides
  @Singleton
  @AutoRepeatModePreference
  @NonNull IntPreferenceType provideAutoRepeatModePreference(final @NonNull Preferences preferences) {
    return preferences.autoRepeatMode();
  }

  @Provides
  @Singleton
  @NonNull Build provideBuild(final @NonNull PackageInfo packageInfo) {
    return new Build(packageInfo);
  }

  @Provides
  @Singleton
  FavoritesStore provideFavoritesStore() {
    return new FavoritesStoreImpl(application);
  }

  @Provides
  @Singleton
  PackageInfo providePackageInfo(final @NonNull Application application) {
    try {
      return application.getPackageManager().getPackageInfo(application.getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
  }

  @Provides
  @Singleton
  RootPageResolver providePageResolver() {
    return new RootPageResolver();
  }

  @Provides
  @Singleton
  PlaybackAnnouncer providePlaybackAnnouncer(@NonNull PlaybackClient playbackClient, @NonNull Speaker speaker) {
    return new PlaybackAnnouncerImpl(playbackClient, speaker);
  }

  @Provides
  @Singleton
  PlaybackClient providePlaybackClient() {
    return new PlaybackClientImpl(application);
  }

  @Provides
  @Singleton
  PlaylistManager providePlaylistManager() {
    return new PlaylistManager(application);
  }

  @Provides
  @Singleton
  Preferences providesPreferences(final @ApplicationContext @NonNull Context context) {
    return new PreferencesImpl(PreferenceManager.getDefaultSharedPreferences(context));
  }

  @Provides
  @Singleton
  QueueManager provideQueueManager() {
    return new QueueManagerImpl();
  }

  @Provides
  @Singleton
  Resources provideResources(final @ApplicationContext @NonNull Context context) {
    return context.getResources();
  }

  @Provides
  RobospiceManager provideRobospiceManager() {
    return new RobospiceManagerImpl();
  }

  @Provides
  @Singleton
  SharedPreferences provideSharedPreferences(final @ApplicationContext @NonNull Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context);
  }

  @Provides
  @Singleton
  Speaker provideSpeaker(final @ApplicationContext @NonNull Context context) {
    return new SpeakerImpl(context);
  }

  @Provides
  @Singleton
  ToastManager provideToastManager() {
    return new ToastManagerImpl();
  }

  @Provides
  @Singleton
  UiModeManager provideUiModeManager(final @ApplicationContext @NonNull Context context, final Preferences preferences) {
    return new UiModeManagerImpl(context, preferences);
  }
}
