/*
* Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
*
* For the full copyright and license information, please view
* the LICENSE file that was distributed with this source code.
*/

package com.example.android.uamp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat.MediaItem;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.example.android.uamp.playback.LocalPlayback;
import com.example.android.uamp.playback.PlaybackManager;
import com.example.android.uamp.playback.QueueManager;
import com.example.android.uamp.playback.QueueManagerImpl;
import com.example.android.uamp.utils.LogHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ch.indr.threethreefive.AppComponent;
import ch.indr.threethreefive.R;
import ch.indr.threethreefive.ThreeThreeFiveApp;
import ch.indr.threethreefive.ui.activities.StartActivity;
import timber.log.Timber;

import static com.example.android.uamp.utils.MediaIDHelper.MEDIA_ID_EMPTY_ROOT;

/**
 * This class provides a MediaBrowser through a service. It exposes the media library to a browsing
 * client, through the onGetRoot and onLoadChildren methods. It also creates a MediaSession and
 * exposes it through its MediaSession.Token, which allows the client to create a MediaController
 * that connects to and send control commands to the MediaSession remotely. This is useful for
 * user interfaces that need to interact with your media session, like Android Auto. You can
 * (should) also use the same service from your app's UI, which gives a seamless playback
 * experience to the user.
 * <p>
 * To implement a MediaBrowserService, you need to:
 * <p>
 * <ul>
 * <p>
 * <li> Extend {@link android.service.media.MediaBrowserService}, implementing the media browsing
 * related methods {@link android.service.media.MediaBrowserService#onGetRoot} and
 * {@link android.service.media.MediaBrowserService#onLoadChildren};
 * <li> In onCreate, start a new {@link android.media.session.MediaSession} and notify its parent
 * with the session's token {@link android.service.media.MediaBrowserService#setSessionToken};
 * <p>
 * <li> Set a callback on the
 * {@link android.media.session.MediaSession#setCallback(android.media.session.MediaSession.Callback)}.
 * The callback will receive all the user's actions, like play, pause, etc;
 * <p>
 * <li> Handle all the actual music playing using any method your app prefers (for example,
 * {@link android.media.MediaPlayer})
 * <p>
 * <li> Update playbackState, "now playing" metadata and queue, using MediaSession proper methods
 * {@link android.media.session.MediaSession#setPlaybackState(android.media.session.PlaybackState)}
 * {@link android.media.session.MediaSession#setMetadata(android.media.MediaMetadata)} and
 * {@link android.media.session.MediaSession#setQueue(List)})
 * <p>
 * <li> Declare and export the service in AndroidManifest with an intent receiver for the action
 * android.media.browse.MediaBrowserService
 * <p>
 * </ul>
 * <p>
 * To make your app compatible with Android Auto, you also need to:
 * <p>
 * <ul>
 * <p>
 * <li> Declare a meta-data tag in AndroidManifest.xml linking to a xml resource
 * with a &lt;automotiveApp&gt; root element. For a media app, this must include
 * an &lt;uses name="media"/&gt; element as a child.
 * For example, in AndroidManifest.xml:
 * &lt;meta-data android:name="com.google.android.gms.car.application"
 * android:resource="@xml/automotive_app_desc"/&gt;
 * And in res/values/automotive_app_desc.xml:
 * &lt;automotiveApp&gt;
 * &lt;uses name="media"/&gt;
 * &lt;/automotiveApp&gt;
 * <p>
 * </ul>
 *
 * @see <a href="README.md">README.md</a> for more details.
 */
public class MusicService extends MediaBrowserServiceCompat implements
    PlaybackManager.PlaybackServiceCallback {

  private static final String TAG = LogHelper.makeLogTag(MusicService.class);

  // The action of the incoming Intent indicating that it contains a command
  // to be executed (see {@link #onStartCommand})
  public static final String ACTION_CMD = "com.example.android.uamp.ACTION_CMD";
  // The key in the extras of the incoming Intent indicating the command that
  // should be executed (see {@link #onStartCommand})
  public static final String CMD_NAME = "CMD_NAME";
  // A value of a CMD_NAME key in the extras of the incoming Intent that
  // indicates that the music playback should be paused (see {@link #onStartCommand})
  public static final String CMD_PAUSE = "CMD_PAUSE";
  // Delay stopSelf by using a handler.
  private static final int STOP_DELAY = 30000;

  private PlaybackManager mPlaybackManager;

  private MediaSessionCompat mSession;
  private MediaNotificationManager mMediaNotificationManager;
  private final DelayedStopHandler mDelayedStopHandler = new DelayedStopHandler(this);
  private QueueManager mQueueManager;

  /*
   * (non-Javadoc)
   * @see android.app.Service#onCreate()
   */
  @Override
  public void onCreate() {
    super.onCreate();
    LogHelper.d(TAG, "onCreate");

    final AppComponent application = ((ThreeThreeFiveApp) getApplicationContext()).component();
    mQueueManager = application.environment().queueManager();
    mQueueManager.addListener(queueListener);

    LocalPlayback playback = new LocalPlayback(this);
    mPlaybackManager = new PlaybackManager(this, getResources(), mQueueManager,
        playback);

    // Start a new MediaSession
    mSession = new MediaSessionCompat(this, "MusicService");
    setSessionToken(mSession.getSessionToken());
    mSession.setCallback(mPlaybackManager.getMediaSessionCallback());
    mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

    Context context = getApplicationContext();
    Intent intent = new Intent(context, StartActivity.class); // Now playing activity
    PendingIntent pi = PendingIntent.getActivity(context, 99 /*request code*/,
        intent, PendingIntent.FLAG_UPDATE_CURRENT);
    mSession.setSessionActivity(pi);

    mPlaybackManager.updatePlaybackState(null);

    try {
      mMediaNotificationManager = new MediaNotificationManager(this);
    } catch (RemoteException e) {
      throw new IllegalStateException("Could not create a MediaNotificationManager", e);
    }
  }

  /**
   * (non-Javadoc)
   *
   * @see android.app.Service#onStartCommand(Intent, int, int)
   */
  @Override
  public int onStartCommand(Intent startIntent, int flags, int startId) {
    if (startIntent != null) {
      String action = startIntent.getAction();
      String command = startIntent.getStringExtra(CMD_NAME);
      if (ACTION_CMD.equals(action)) {
        if (CMD_PAUSE.equals(command)) {
          mPlaybackManager.handlePauseRequest();
        }
      } else {
        // Try to handle the intent as a media button event wrapped by MediaButtonReceiver
        MediaButtonReceiver.handleIntent(mSession, startIntent);
      }
    }
    // Reset the delay handler to enqueue a message to stop the service if
    // nothing is playing.
    mDelayedStopHandler.removeCallbacksAndMessages(null);
    mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
    return START_STICKY;
  }

  /**
   * (non-Javadoc)
   *
   * @see android.app.Service#onDestroy()
   */
  @Override
  public void onDestroy() {
    LogHelper.d(TAG, "onDestroy");
    // Service is being killed, so make sure we release our resources
    mQueueManager.removeListener(queueListener);
    mPlaybackManager.handleStopRequest(null);
    mMediaNotificationManager.stopNotification();

    mDelayedStopHandler.removeCallbacksAndMessages(null);
    mSession.release();
  }

  @Override
  public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, Bundle rootHints) {
    return new BrowserRoot(MEDIA_ID_EMPTY_ROOT, null);
  }

  @Override
  public void onLoadChildren(@NonNull final String parentMediaId,
                             @NonNull final Result<List<MediaItem>> result) {
    result.sendResult(new ArrayList<>());
  }

  /**
   * Callback method called from PlaybackManager whenever the music is about to play.
   */
  @Override
  public void onPlaybackStart() {
    mSession.setActive(true);

    mDelayedStopHandler.removeCallbacksAndMessages(null);

    // The service needs to continue running even after the bound client (usually a
    // MediaController) disconnects, otherwise the music playback will stop.
    // Calling startService(Intent) will keep the service running until it is explicitly killed.
    startService(new Intent(getApplicationContext(), MusicService.class));
  }


  @Override
  public void onPlaybackPause() {
    mSession.setActive(false);
    // Reset the delayed stop handler, so after STOP_DELAY it will be executed again,
    // potentially stopping the service.
    mDelayedStopHandler.removeCallbacksAndMessages(null);
    mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
  }

  /**
   * Callback method called from PlaybackManager whenever the music stops playing.
   */
  @Override
  public void onPlaybackStop() {
    mSession.setActive(false);
    // Reset the delayed stop handler, so after STOP_DELAY it will be executed again,
    // potentially stopping the service.
    mDelayedStopHandler.removeCallbacksAndMessages(null);
    mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
    stopForeground(true);
  }

  @Override
  public void onNotificationRequired() {
    Timber.d("onNotificationRequired %s", this.toString());
    mMediaNotificationManager.startNotification();
  }

  @Override
  public void onPlaybackStateUpdated(PlaybackStateCompat newState) {
    mSession.setPlaybackState(newState);
  }

  @Override public void onCustomEvent(@NonNull String event, @Nullable Bundle extras) {
    mSession.sendSessionEvent(event, extras);
  }

  private QueueManagerImpl.MetadataUpdateListener queueListener = new QueueManagerImpl.MetadataUpdateListener() {
    @Override
    public void onMetadataChanged(MediaMetadataCompat metadata) {
      mSession.setMetadata(metadata);
    }

    @Override
    public void onMetadataRetrieveError() {
      mPlaybackManager.updatePlaybackState(
          getString(R.string.error_no_metadata));
    }

    @Override
    public void onCurrentQueueIndexUpdated(int queueIndex) {
      mPlaybackManager.handlePlayRequest();
    }

    @Override
    public void onQueueUpdated(String title,
                               List<MediaSessionCompat.QueueItem> newQueue) {
      mSession.setQueue(newQueue);
      mSession.setQueueTitle(title);

      // In case the queue was cleared, request playback stop
      if (newQueue == null || newQueue.size() == 0) {
        mPlaybackManager.handleStopRequest(null);
      }
    }
  };

  /**
   * A simple handler that stops the service if playback is not active (playing)
   */
  private static class DelayedStopHandler extends Handler {
    private final WeakReference<MusicService> mWeakReference;

    private DelayedStopHandler(MusicService service) {
      mWeakReference = new WeakReference<>(service);
    }

    @Override
    public void handleMessage(Message msg) {
      MusicService service = mWeakReference.get();
      if (service != null && service.mPlaybackManager.getPlayback() != null) {
        if (service.mPlaybackManager.getPlayback().isPlaying()) {
          LogHelper.d(TAG, "Ignoring delayed stop since the media player is in use.");
          return;
        }
        LogHelper.d(TAG, "Stopping service with delay handler.");
        service.stopSelf();
      }
    }
  }
}
