/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.uamp;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.ui.activities.NowPlayingProxyActivity;
import timber.log.Timber;

/**
 * Keeps track of a notification and updates it automatically for a given
 * MediaSession. Maintaining a visible notification (usually) guarantees that the music service
 * won't be killed during playback.
 */
public class MediaNotificationManager extends BroadcastReceiver {

  private static final int NOTIFICATION_ID = 412;
  private static final int REQUEST_CODE = 100;

  public static final String ACTION_PAUSE = "com.example.android.uamp.pause";
  public static final String ACTION_PLAY = "com.example.android.uamp.play";
  public static final String ACTION_STOP = "com.exmaple.android.uamp.stop";
  public static final String ACTION_PREV = "com.example.android.uamp.prev";
  public static final String ACTION_NEXT = "com.example.android.uamp.next";
  public static final String ACTION_STOP_CASTING = "com.example.android.uamp.stop_cast";

  private final MusicService mService;
  private MediaSessionCompat.Token mSessionToken;
  private MediaControllerCompat mController;
  private MediaControllerCompat.TransportControls mTransportControls;

  private PlaybackStateCompat mPlaybackState;
  private MediaMetadataCompat mMetadata;

  private final NotificationManagerCompat mNotificationManager;

  private final PendingIntent mPauseIntent;
  private final PendingIntent mPlayIntent;
  private final PendingIntent mStopIntent;
  private final PendingIntent mPreviousIntent;
  private final PendingIntent mNextIntent;

  private final PendingIntent mStopCastIntent;

  private boolean mStarted = false;

  public MediaNotificationManager(MusicService service) throws RemoteException {
    mService = service;
    updateSessionToken();

    mNotificationManager = NotificationManagerCompat.from(service);

    String pkg = mService.getPackageName();
    mPauseIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
        new Intent(ACTION_PAUSE).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
    mPlayIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
        new Intent(ACTION_PLAY).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
    mStopIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
        new Intent(ACTION_STOP).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
    mPreviousIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
        new Intent(ACTION_PREV).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
    mNextIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
        new Intent(ACTION_NEXT).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
    mStopCastIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
        new Intent(ACTION_STOP_CASTING).setPackage(pkg),
        PendingIntent.FLAG_CANCEL_CURRENT);

    // Cancel all notifications to handle the case where the Service was killed and
    // restarted by the system.
    mNotificationManager.cancelAll();
  }

  /**
   * Posts the notification and starts tracking the session to keep it
   * updated. The notification will automatically be removed if the session is
   * destroyed before {@link #stopNotification} is called.
   */
  public void startNotification() {
    Timber.d("startNotification started=%s, %s", mStarted, this.toString());
    if (!mStarted) {
      mMetadata = mController.getMetadata();
      mPlaybackState = mController.getPlaybackState();

      // The notification must be updated after setting started to true
      Notification notification = createNotification();
      if (notification != null) {
        mController.registerCallback(mCb);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NEXT);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_STOP);
        filter.addAction(ACTION_PREV);
        filter.addAction(ACTION_STOP_CASTING);
        mService.registerReceiver(this, filter);

        mService.startForeground(NOTIFICATION_ID, notification);
        mStarted = true;
      }
    }
  }

  /**
   * Removes the notification and stops tracking the session. If the session
   * was destroyed this has no effect.
   */
  public void stopNotification() {
    Timber.d("stopNotification started=%s, %s", mStarted, this.toString());
    if (mStarted) {
      mStarted = false;
      mController.unregisterCallback(mCb);
      try {
        mNotificationManager.cancel(NOTIFICATION_ID);
        mService.unregisterReceiver(this);
      } catch (IllegalArgumentException ex) {
        // ignore if the receiver is not registered.
        Timber.e(ex, "Error stopping notification");
      }
      mService.stopForeground(true);
    }
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    final String action = intent.getAction();
    Timber.d("Received intent with action " + action);
    switch (action) {
      case ACTION_PAUSE:
        mTransportControls.pause();
        break;
      case ACTION_PLAY:
        mTransportControls.play();
        break;
      case ACTION_STOP:
        mTransportControls.stop();
        break;
      case ACTION_NEXT:
        mTransportControls.skipToNext();
        break;
      case ACTION_PREV:
        mTransportControls.skipToPrevious();
        break;
      default:
        Timber.w("Unknown intent ignored. Action=%s", action);
    }
  }

  /**
   * Update the state based on a change on the session token. Called either when
   * we are running for the first time or when the media session owner has destroyed the session
   * (see {@link android.media.session.MediaController.Callback#onSessionDestroyed()})
   */
  private void updateSessionToken() throws RemoteException {
    MediaSessionCompat.Token freshToken = mService.getSessionToken();
    if (mSessionToken == null && freshToken != null ||
        mSessionToken != null && !mSessionToken.equals(freshToken)) {
      if (mController != null) {
        mController.unregisterCallback(mCb);
      }
      mSessionToken = freshToken;
      if (mSessionToken != null) {
        mController = new MediaControllerCompat(mService, mSessionToken);
        mTransportControls = mController.getTransportControls();
        if (mStarted) {
          mController.registerCallback(mCb);
        }
      }
    }
  }

  private PendingIntent createContentIntent(MediaDescriptionCompat description) {
    Intent openUI = new Intent(mService, NowPlayingProxyActivity.class);
    openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        openUI.putExtra(StartActivity.EXTRA_START_FULLSCREEN, true);
//        if (description != null) {
//            openUI.putExtra(StartActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, description);
//        }
    return PendingIntent.getActivity(mService, REQUEST_CODE, openUI,
        PendingIntent.FLAG_CANCEL_CURRENT);
  }

  private final MediaControllerCompat.Callback mCb = new MediaControllerCompat.Callback() {
    @Override
    public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
      Timber.d("onPlaybackStateChanged state=%s, %s", state, this.toString());

      mPlaybackState = state;
      if (state.getState() == PlaybackStateCompat.STATE_NONE ||
          state.getState() == PlaybackStateCompat.STATE_PAUSED ||
          state.getState() == PlaybackStateCompat.STATE_STOPPED) {
        Timber.d("Stopping notification");
        stopNotification();
      } else {
        Timber.d("Creating notification");
        Notification notification = createNotification();
        if (notification != null) {
          mNotificationManager.notify(NOTIFICATION_ID, notification);
        }
      }
    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat metadata) {
      Timber.d("onMetadataChanged metadata=%s, %s", metadata, this.toString());

      mMetadata = metadata;
      Timber.d("Creating notification");
      Notification notification = createNotification();
      if (notification != null) {
        mNotificationManager.notify(NOTIFICATION_ID, notification);
      }
    }

    @Override
    public void onSessionDestroyed() {
      super.onSessionDestroyed();
      Timber.d("Session was destroyed, resetting to the new session token");
      try {
        updateSessionToken();
      } catch (RemoteException e) {
        Timber.e(e, "could not connect media controller");
      }
    }
  };

  private Notification createNotification() {
    Timber.d("createNotification metadata=%s, %s", mMetadata, this.toString());
    if (mMetadata == null || mPlaybackState == null) {
      return null;
    }

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mService);
    int playPauseButtonPosition = 0;

    // If skip to previous action is enabled
    if ((mPlaybackState.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
      notificationBuilder.addAction(R.drawable.ic_skip_previous_white_24dp,
          mService.getString(R.string.label_previous), mPreviousIntent);

      // If there is a "skip to previous" button, the play/pause button will
      // be the second one. We need to keep track of it, because the MediaStyle notification
      // requires to specify the index of the buttons (actions) that should be visible
      // when in compact view.
      playPauseButtonPosition = 1;
    }

    addStopAction(notificationBuilder);
    // addPlayPauseAction(notificationBuilder);

    // If skip to next action is enabled
    if ((mPlaybackState.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
      notificationBuilder.addAction(R.drawable.ic_skip_next_white_24dp,
          mService.getString(R.string.label_next), mNextIntent);
    }

    MediaDescriptionCompat description = mMetadata.getDescription();

    String fetchArtUrl = null;
    Bitmap art = null;
    if (description.getIconUri() != null) {
      // This sample assumes the iconUri will be a valid URL formatted String, but
      // it can actually be any valid Android Uri formatted String.
      // async fetch the album art icon
      String artUrl = description.getIconUri().toString();
      art = AlbumArtCache.getInstance().getBigImage(artUrl);
      if (art == null) {
        fetchArtUrl = artUrl;
        // use a placeholder art while the remote art is being downloaded
        art = BitmapFactory.decodeResource(mService.getResources(),
            R.drawable.ic_default_art);
      }
    }

    notificationBuilder
        .setStyle(new NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(new int[]{playPauseButtonPosition})  // show only play/pause in compact view
            .setMediaSession(mSessionToken))
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setSmallIcon(R.drawable.ic_notification)
        .setLargeIcon(art)
        .setContentTitle(description.getTitle())
        .setContentText(description.getSubtitle())
        .setContentIntent(createContentIntent(description))
        .setShowWhen(false);

    setNotificationPlaybackState(notificationBuilder);
    if (fetchArtUrl != null) {
      fetchBitmapFromURLAsync(fetchArtUrl, notificationBuilder);
    }

    return notificationBuilder.build();
  }

  private void addStopAction(NotificationCompat.Builder builder) {
    Timber.d("updateStopAction");
    builder.addAction(new NotificationCompat.Action(R.drawable.ic_stop_white_24dp, "Stop", mStopIntent));
  }

  private void addPlayPauseAction(NotificationCompat.Builder builder) {
    Timber.d("addPlayPauseAction %s", this.toString());
    String label;
    int icon;
    PendingIntent intent;
    if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
      label = mService.getString(R.string.label_pause);
      icon = R.drawable.uamp_ic_pause_white_24dp;
      intent = mPauseIntent;
    } else {
      label = mService.getString(R.string.label_play);
      icon = R.drawable.uamp_ic_play_arrow_white_24dp;
      intent = mPlayIntent;
    }
    builder.addAction(new NotificationCompat.Action(icon, label, intent));
  }

  private void setNotificationPlaybackState(NotificationCompat.Builder builder) {
    Timber.d("setNotificationPlaybackState mPlaybackState=%s, %s", mPlaybackState.toString(), this.toString());

    if (mPlaybackState == null || !mStarted) {
      Timber.d("setNotificationPlaybackState cancelling notification!");
      mService.stopForeground(true);
      return;
    }
    /*
    // setWhen(long) doesn't seem to get updated. It always remains on the value that
    // is set the first time...

    if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING
        && mPlaybackState.getPosition() >= 0) {
      LogHelper.d(TAG, "updateNotificationPlaybackState. updating playback position to ",
          (System.currentTimeMillis() - mPlaybackState.getPosition()) / 1000, " seconds");
      builder
          .setWhen(System.currentTimeMillis() - mPlaybackState.getPosition())
          .setShowWhen(true)
          .setUsesChronometer(true);
    } else {
      LogHelper.d(TAG, "updateNotificationPlaybackState. hiding playback position");
      builder
          .setWhen(0)
          .setShowWhen(false)
          .setUsesChronometer(false);
    }
    */

    // Make sure that the notification can be dismissed by the user when we are not playing:
    builder.setOngoing(mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING);
  }

  private void fetchBitmapFromURLAsync(final String bitmapUrl,
                                       final NotificationCompat.Builder builder) {
    AlbumArtCache.getInstance().fetch(bitmapUrl, new AlbumArtCache.FetchListener() {
      @Override
      public void onFetched(String artUrl, Bitmap bitmap, Bitmap icon) {
        if (mMetadata != null && mMetadata.getDescription().getIconUri() != null &&
            mMetadata.getDescription().getIconUri().toString().equals(artUrl)) {
          // If the media is still the same, update the notification:
          Timber.d("fetchBitmapFromURLAsync: set bitmap to %s", artUrl);
          builder.setLargeIcon(bitmap);
          mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        }
      }
    });
  }
}
