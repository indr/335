/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.services;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Pair;

import com.example.android.uamp.MusicService;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class PlaybackClientImpl implements PlaybackClient {

  private final @NonNull Context context;
  private final @NonNull MediaBrowserCompat mediaBrowser;
  private @Nullable MediaControllerCompat mediaController;

  private final BehaviorSubject<Integer> playbackState = BehaviorSubject.create(PlaybackStateCompat.STATE_NONE);
  private final BehaviorSubject<PlaybackStateCompat> playbackStateCompat = BehaviorSubject.create((PlaybackStateCompat) null);
  private final BehaviorSubject<MediaMetadataCompat> mediaMetadata = BehaviorSubject.create((MediaMetadataCompat) null);
  private final PublishSubject<Pair<String, Bundle>> customEvent = PublishSubject.create();

  public PlaybackClientImpl(final @NonNull Context context) {
    this.context = context;

    mediaBrowser = new MediaBrowserCompat(context, new ComponentName(context, MusicService.class),
        mediaBrowserConnectionCallback, null);
    mediaBrowser.connect();
  }

  @Nullable public MediaControllerCompat mediaController() {
    if (mediaController == null) {
      // In case we were disconnected or the initial connection failed, we shot a (re-) connect.
      // This will trigger an async reconnection. Maybe even multiple times at the
      // same time (?).
      mediaBrowser.connect();
    }
    return mediaController;
  }

  @NonNull public Observable<MediaMetadataCompat> mediaMetadata() {
    return mediaMetadata;
  }

  @NonNull public BehaviorSubject<Integer> playbackState() {
    return playbackState;
  }

  @NonNull public BehaviorSubject<PlaybackStateCompat> playbackStateCompat() {
    return playbackStateCompat;
  }

  @NonNull @Override public Observable<Pair<String, Bundle>> customEvent() {
    return customEvent;
  }

  @Override public void playPause() {
    MediaControllerCompat.TransportControls transportControls = transportControls();
    if (transportControls == null) {
      return;
    }

    // In case we haven't received a playbackState update
    if (!playbackState.hasValue()) {
      transportControls.stop();
      return;
    }

    switch (playbackState.getValue()) {
      case PlaybackStateCompat.STATE_NONE:
        transportControls.play();
        break;
      case PlaybackStateCompat.STATE_PAUSED:
        transportControls.play();
        break;
      case PlaybackStateCompat.STATE_PLAYING:
        transportControls.pause();
        break;
      case PlaybackStateCompat.STATE_STOPPED:
        transportControls.play();
        break;
      default:
        transportControls.stop();
        break;
    }
  }

  @Nullable public MediaControllerCompat.TransportControls transportControls() {
    if (mediaController == null) return null;

    return mediaController.getTransportControls();
  }

  private MediaBrowserCompat.ConnectionCallback mediaBrowserConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {
    /**
     * Invoked when the client is disconnected from the media browser.
     */
    @Override public void onConnectionSuspended() {
      super.onConnectionSuspended();
      Timber.w("Connection to MediaBrowser suspended");

      if (mediaController != null) {
        mediaController.unregisterCallback(mediaControllerCallback);
        mediaController = null;
      }
    }

    /**
     * Invoked when the connection to the media browser failed.
     */
    @Override public void onConnectionFailed() {
      super.onConnectionFailed();
      Timber.w("Connection to MediaBrowser failed");
    }

    /**
     * Invoked after connect() when the request has successfully completed.
     */
    @Override public void onConnected() {
      super.onConnected();
      Timber.d("Connection to MediaBrowser established");

      try {
        mediaController = new MediaControllerCompat(context, mediaBrowser.getSessionToken());
        mediaController.registerCallback(mediaControllerCallback);
      } catch (RemoteException e) {
        Timber.e("Error creating media controller", e);
      }
    }
  };

  private MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback() {

    @Override public void onPlaybackStateChanged(PlaybackStateCompat state) {
      super.onPlaybackStateChanged(state);
      Timber.d("onPlaybackStateChanged %s", state != null ? state.toString() : "null");

      if (state == null) {
        playbackState.onNext(PlaybackStateCompat.STATE_NONE);
      } else {
        playbackState.onNext(state.getState());
      }

      playbackStateCompat.onNext(state);
    }

    @Override public void onMetadataChanged(MediaMetadataCompat metadata) {
      super.onMetadataChanged(metadata);
      Timber.d("onMetadataChanged %s", metadata != null ? metadata.toString() : "null");

      mediaMetadata.onNext(metadata);
    }

    @Override public void onSessionEvent(String event, Bundle extras) {
      Timber.d("onSessionEvent %s, %s", event, this.toString());
      super.onSessionEvent(event, extras);
      customEvent.onNext(Pair.create(event, extras));
    }
  };

}
