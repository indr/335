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

import com.example.android.uamp.playback.QueueManager;

import auto.parcel.AutoParcel;
import ch.indr.threethreefive.data.db.favorites.FavoritesStore;
import ch.indr.threethreefive.services.PlaybackClient;
import ch.indr.threethreefive.services.Speaker;
import ch.indr.threethreefive.services.ToastManager;

@AutoParcel
public abstract class Environment implements Parcelable {

  public static Builder builder() {
    return new AutoParcel_Environment.Builder();
  }

  public abstract FavoritesStore favoritesStore();

  public abstract PlaybackClient playbackClient();

  @Nullable public TransportControls playbackControls() {
    PlaybackClient playbackClient = playbackClient();
    if (playbackClient == null) return null;

    return playbackClient.transportControls();
  }

  @NonNull public abstract Preferences preferences();

  @NonNull public abstract QueueManager queueManager();

  public abstract SharedPreferences sharedPreferences();

  public abstract Speaker speaker();

  @NonNull public abstract ToastManager toastManager();

  public abstract Builder toBuilder();

  @AutoParcel.Builder
  public abstract static class Builder {
    public abstract Builder favoritesStore(FavoritesStore __);

    public abstract Builder playbackClient(PlaybackClient __);

    public abstract Builder preferences(Preferences __);

    public abstract Builder queueManager(QueueManager __);

    public abstract Builder sharedPreferences(SharedPreferences __);

    public abstract Builder speaker(Speaker speaker);

    public abstract Builder toastManager(ToastManager toastManager);

    public abstract Environment build();
  }
}
