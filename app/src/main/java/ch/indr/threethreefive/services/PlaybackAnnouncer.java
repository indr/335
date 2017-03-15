/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.services;

import android.support.annotation.NonNull;
import android.support.v4.media.session.PlaybackStateCompat;

import java.util.concurrent.TimeUnit;

import ch.indr.threethreefive.R;
import rx.Observable;
import timber.log.Timber;

import static android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_CONNECTING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_ERROR;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_FAST_FORWARDING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_REWINDING;

public class PlaybackAnnouncer implements PlaybackAnnouncerType {

  private static final int DEBOUNCE_TIMEOUT = 500;

  private final SpeakerType speaker;

  private boolean started = false;

  public PlaybackAnnouncer(final @NonNull PlaybackClientType playbackClient, final @NonNull SpeakerType speaker) {
    this.speaker = speaker;

    Observable<StateTransition> transition = playbackClient.playbackState()
        .scan(StateTransition.create(0, 0), (previous, to) -> StateTransition.create(previous.to, to))
        .doOnNext(st -> Timber.d("playback state transition %s, %s", st.toString(), this.toString()))
        .throttleWithTimeout(DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)
        .distinctUntilChanged()
        .compose(bindToStatus())
        .share();

    transition
        .filter(p -> p.to == STATE_BUFFERING)
        .subscribe(__ -> speaker.sayQueued(R.string.speech_playback_state_buffering));

    transition
        .filter(p -> p.to == STATE_CONNECTING)
        .subscribe(__ -> this.speaker.sayQueued(R.string.speech_playback_state_connecting));

    transition
        .filter(p -> p.to == STATE_ERROR)
        .subscribe(__ -> this.speaker.sayQueued(R.string.speech_playback_state_error));

    transition
        .filter(p -> p.to == STATE_PAUSED)
        .subscribe(__ -> this.speaker.sayQueued(R.string.speech_playback_state_paused));

    transition
        .filter(p -> p.to == STATE_PLAYING && p.from != STATE_PLAYING)
        .filter(p -> p.from != STATE_REWINDING && p.from != STATE_FAST_FORWARDING)
        .subscribe(__ -> this.speaker.sayQueued(R.string.speech_playback_state_playing));

    transition
        .filter(p -> p.to == PlaybackStateCompat.STATE_STOPPED)
        .subscribe(__ -> speaker.sayQueued(R.string.speech_playback_state_stopped));


    playbackClient.customEvent()
        .map(ce -> ce.second)
        .filter(bundle -> bundle.containsKey("resourceId"))
        .map(bundle -> bundle.getInt("resourceId"))
        .compose(bindToStatus())
        .subscribe(speaker::sayUrgent);
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

  private @NonNull <T> Observable.Transformer<T, T> bindToStatus() {
    return source -> source.filter(__ -> started);
  }

  private static class StateTransition {

    StateTransition(int from, int to) {
      this.from = from;
      this.to = to;
    }

    public static StateTransition create(int from, int to) {
      return new StateTransition(from, to);
    }

    public int from;
    public int to;

    @Override public String toString() {
      return "StateTransition [from=" + from + ", to=" + to + "]";
    }
  }
}
