/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.services;

import android.support.annotation.NonNull;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Pair;

import java.util.concurrent.TimeUnit;

import rx.Observable;

import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.takePairWhen;

public class PlaybackAnnouncer implements PlaybackAnnouncerType {

  private static final int DEBOUNCE_TIMEOUT = 300;

  private final PlaybackClientType playbackClient;
  private final SpeakerType speaker;
  private final Observable<Integer> playbackState;

  private boolean started = false;
  private Observable<MediaMetadataCompat> mediaMetadata;

  public PlaybackAnnouncer(final @NonNull PlaybackClientType playbackClient, final @NonNull SpeakerType speaker) {
    this.playbackClient = playbackClient;
    this.speaker = speaker;

    // Debounce playback state so that only the last relevant playback state
    // is announced. This is usefull in cases of skipping, or loading a media item
    // from local/external storage.
    this.playbackState = this.playbackClient.playbackState()
        .filter(state -> state != PlaybackStateCompat.STATE_BUFFERING)
        .distinctUntilChanged()
        .throttleWithTimeout(DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS);

    this.mediaMetadata = this.playbackClient.mediaMetadata();

    mediaDescriptionWhen(PlaybackStateCompat.STATE_PLAYING)
        .compose(bindToStatus())
        .subscribe(mediaDescription -> speaker.sayQueued("Playing " + mediaDescription.getTitle()));

    mediaDescriptionWhen(PlaybackStateCompat.STATE_CONNECTING)
        .compose(bindToStatus())
        .subscribe(__ -> this.speaker.sayQueued("Loading stream, please wait."));

    mediaDescriptionWhen(PlaybackStateCompat.STATE_STOPPED)
        .compose(bindToStatus())
        .subscribe(__ -> this.speaker.sayQueued("Playback stopped"));
  }

  @Override public boolean isStarted() {
    return started;
  }

  @Override public void start() {
    if (started) {
      return;
    }
    started = true;
  }

  @Override public void stop() {
    if (!started) {
      return;
    }
    started = false;
  }

  private Observable<MediaDescriptionCompat> mediaDescriptionWhen(int state) {
    return mediaMetadataWhen(state)
        .map(MediaMetadataCompat::getDescription);
  }

  private Observable<MediaMetadataCompat> mediaMetadataWhen(int state) {
    return mediaMetadata
        .compose(bindToStatus())
        .compose(takePairWhen(playbackState))
        .filter(pair -> pair.second == state)
        .map(pair -> pair.first);
  }

  private @NonNull <T> Observable.Transformer<T, T> bindToStatus() {
    return source -> source.filter(__ -> started);
  }
}
