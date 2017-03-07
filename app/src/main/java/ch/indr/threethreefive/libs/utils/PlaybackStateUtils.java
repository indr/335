/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.utils;

import android.support.annotation.NonNull;
import android.support.v4.media.session.PlaybackStateCompat;

public final class PlaybackStateUtils {
  private PlaybackStateUtils() {
  }

  @NonNull public static PlayPauseAction playPauseAction(int playbackState) {
    switch (playbackState) {
      case PlaybackStateCompat.STATE_BUFFERING:
        return PlayPauseAction.STOP;
      case PlaybackStateCompat.STATE_CONNECTING:
        return PlayPauseAction.STOP;
      case PlaybackStateCompat.STATE_ERROR:
        return PlayPauseAction.PLAY;
      case PlaybackStateCompat.STATE_FAST_FORWARDING:
        return PlayPauseAction.PAUSE;
      case PlaybackStateCompat.STATE_NONE:
        return PlayPauseAction.PLAY;
      case PlaybackStateCompat.STATE_PAUSED:
        return PlayPauseAction.PLAY;
      case PlaybackStateCompat.STATE_PLAYING:
        return PlayPauseAction.PAUSE;
      case PlaybackStateCompat.STATE_REWINDING:
        return PlayPauseAction.PAUSE;
      case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT:
        return PlayPauseAction.STOP;
      case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS:
        return PlayPauseAction.STOP;
      case PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM:
        return PlayPauseAction.STOP;
      case PlaybackStateCompat.STATE_STOPPED:
        return PlayPauseAction.PLAY;
      default:
        return PlayPauseAction.PLAY;
    }
  }

  @NonNull public static String toString(int playbackState) {
    switch (playbackState) {
      case PlaybackStateCompat.STATE_BUFFERING:
        return "Buffering";
      case PlaybackStateCompat.STATE_CONNECTING:
        return "Connecting";
      case PlaybackStateCompat.STATE_ERROR:
        return "Error";
      case PlaybackStateCompat.STATE_FAST_FORWARDING:
        return "Fast Forwarding";
      case PlaybackStateCompat.STATE_NONE:
        return "None";
      case PlaybackStateCompat.STATE_PAUSED:
        return "Paused";
      case PlaybackStateCompat.STATE_PLAYING:
        return "Playing";
      case PlaybackStateCompat.STATE_REWINDING:
        return "Rewinding";
      case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT:
        return "Skipping";
      case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS:
        return "Skipping";
      case PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM:
        return "Skipping";
      case PlaybackStateCompat.STATE_STOPPED:
        return "Stopped";
      default:
        return "Unknown";
    }
  }

  public enum PlayPauseAction {
    PAUSE, PLAY, STOP
  }
}
