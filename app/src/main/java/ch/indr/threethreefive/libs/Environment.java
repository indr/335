/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaControllerCompat.TransportControls;

import com.example.android.uamp.playback.QueueManagerType;

import auto.parcel.AutoParcel;
import ch.indr.threethreefive.favorites.FavoritesStore;
import ch.indr.threethreefive.navigation.PageResolver;
import ch.indr.threethreefive.playlist.PlaylistManager;
import ch.indr.threethreefive.services.PlaybackClientType;
import ch.indr.threethreefive.services.SpeakerType;
import ch.indr.threethreefive.services.ToastManagerType;

@AutoParcel
public abstract class Environment implements Parcelable {

  public static Builder builder() {
    return new AutoParcel_Environment.Builder();
  }

  public abstract FavoritesStore favoritesStore();

  public abstract PageResolver pageResolver();

  public abstract PlaybackClientType playbackClient();

  @Nullable public TransportControls playbackControls() {
    PlaybackClientType playbackClient = playbackClient();
    if (playbackClient == null) return null;

    return playbackClient.transportControls();
  }

  @Deprecated
  public abstract PlaylistManager playlistManager();

  @NonNull public abstract PreferencesType preferences();

  @NonNull public abstract QueueManagerType queueManager();

  public abstract SharedPreferences sharedPreferences();

  public abstract SpeakerType speaker();

  @NonNull public abstract ToastManagerType toastManager();

  public abstract Builder toBuilder();

  @AutoParcel.Builder
  public abstract static class Builder {
    public abstract Builder favoritesStore(FavoritesStore __);

    public abstract Builder pageResolver(PageResolver __);

    public abstract Builder playbackClient(PlaybackClientType __);

    public abstract Builder playlistManager(PlaylistManager __);

    public abstract Builder preferences(PreferencesType __);

    public abstract Builder queueManager(QueueManagerType __);

    public abstract Builder sharedPreferences(SharedPreferences __);

    public abstract Builder speaker(SpeakerType speaker);

    public abstract Builder toastManager(ToastManagerType toastManager);

    public abstract Environment build();
  }
}
