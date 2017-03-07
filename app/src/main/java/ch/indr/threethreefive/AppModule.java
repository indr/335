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
import com.example.android.uamp.playback.QueueManagerType;

import javax.inject.Singleton;

import ch.indr.threethreefive.favorites.FavoritesStore;
import ch.indr.threethreefive.libs.ActivityStackType;
import ch.indr.threethreefive.libs.Build;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.Preferences;
import ch.indr.threethreefive.libs.PreferencesType;
import ch.indr.threethreefive.libs.preferences.IntPreferenceType;
import ch.indr.threethreefive.libs.qualifiers.ApplicationContext;
import ch.indr.threethreefive.libs.qualifiers.AutoRepeatModePreference;
import ch.indr.threethreefive.navigation.PageResolver;
import ch.indr.threethreefive.playlist.PlaylistManager;
import ch.indr.threethreefive.services.PlaybackAnnouncer;
import ch.indr.threethreefive.services.PlaybackAnnouncerType;
import ch.indr.threethreefive.services.PlaybackClient;
import ch.indr.threethreefive.services.PlaybackClientType;
import ch.indr.threethreefive.services.Speaker;
import ch.indr.threethreefive.services.SpeakerType;
import ch.indr.threethreefive.services.ToastManager;
import ch.indr.threethreefive.services.ToastManagerType;
import ch.indr.threethreefive.services.UiModeManager;
import ch.indr.threethreefive.services.UiModeManagerType;
import dagger.Module;
import dagger.Provides;

@Module
public final class AppModule {
  private final Application application;
  private final ActivityStackType activityStack;

  public AppModule(final @NonNull Application application, final @NonNull ActivityStackType activityStack) {
    this.application = application;
    this.activityStack = activityStack;
  }

  @Provides
  @Singleton
  Environment provideEnvironment(
      final @NonNull FavoritesStore favoritesStore,
      final @NonNull PageResolver pageResolver,
      final @NonNull PlaybackClientType playbackClient,
      final @NonNull PlaylistManager playlistManager,
      final @NonNull QueueManagerType queueManager,
      final @NonNull SharedPreferences sharedPreferences,
      final @NonNull PreferencesType preferences,
      final @NonNull SpeakerType speaker,
      final @NonNull ToastManagerType toastManager) {
    return Environment.builder()
        .favoritesStore(favoritesStore)
        .pageResolver(pageResolver)
        .playbackClient(playbackClient)
        .playlistManager(playlistManager)
        .queueManager(queueManager)
        .sharedPreferences(sharedPreferences)
        .preferences(preferences)
        .speaker(speaker)
        .toastManager(toastManager)
        .build();
  }

  @Provides
  @Singleton
  ActivityStackType provideActivityStackType() {
    return activityStack;
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
  @NonNull IntPreferenceType provideAutoRepeatModePreference(final @NonNull PreferencesType preferences) {
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
    return new FavoritesStore(application);
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
  PageResolver providePageResolver() {
    return new PageResolver();
  }

  @Provides
  @Singleton
  PlaybackAnnouncerType providePlaybackAnnouncerType(@NonNull PlaybackClientType playbackClient, @NonNull SpeakerType speaker) {
    return new PlaybackAnnouncer(playbackClient, speaker);
  }

  @Provides
  @Singleton
  PlaybackClientType providePlaybackClientType() {
    return new PlaybackClient(application);
  }

  @Provides
  @Singleton
  PlaylistManager providePlaylistManager() {
    return new PlaylistManager(application);
  }

  @Provides
  @Singleton
  PreferencesType providesPreferencesType(final @ApplicationContext @NonNull Context context) {
    return new Preferences(PreferenceManager.getDefaultSharedPreferences(context));
  }

  @Provides
  @Singleton
  QueueManagerType provideQueueManagerType() {
    return new QueueManager();
  }

  @Provides
  @Singleton
  Resources provideResources(final @ApplicationContext @NonNull Context context) {
    return context.getResources();
  }

  @Provides
  @Singleton
  SharedPreferences provideSharedPreferences(final @ApplicationContext @NonNull Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context);
  }

  @Provides
  @Singleton
  SpeakerType provideSpeakerType(final @ApplicationContext @NonNull Context context) {
    return new Speaker(context);
  }

  @Provides
  @Singleton
  ToastManagerType provideToastManagerType() {
    return new ToastManager();
  }

  @Provides
  @Singleton
  UiModeManagerType provideUiModeManagerType(final @ApplicationContext @NonNull Context context, final PreferencesType preferences) {
    return new UiModeManager(context, preferences);
  }
}
