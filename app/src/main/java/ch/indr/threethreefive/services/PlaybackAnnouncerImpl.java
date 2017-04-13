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
import ch.indr.threethreefive.libs.Transition;
import rx.Observable;
import timber.log.Timber;

import static android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_CONNECTING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_ERROR;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_FAST_FORWARDING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_REWINDING;

public class PlaybackAnnouncerImpl implements PlaybackAnnouncer {

  public static final int DEBOUNCE_TIMEOUT = 500;

  private final Speaker speaker;

  private boolean started = false;

  public PlaybackAnnouncerImpl(final @NonNull PlaybackClient playbackClient, final @NonNull Speaker speaker) {
    this.speaker = speaker;

    Observable<Transition<Integer>> transition = playbackClient.playbackState()
        .scan(Transition.<Integer>create(), Transition::next)
        .doOnNext(st -> Timber.d("playback state transition %s, %s", st.toString(), this.toString()))
        .throttleWithTimeout(DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)
        .distinctUntilChanged()
        .compose(bindToStatus())
        .share();

    transition
        .filter(p -> p.getTo() == STATE_BUFFERING)
        .subscribe(__ -> speak(R.string.speech_playback_state_buffering));

    transition
        .filter(p -> p.getTo() == STATE_CONNECTING)
        .subscribe(__ -> speak(R.string.speech_playback_state_connecting));

    transition
        .filter(p -> p.getTo() == STATE_ERROR)
        .subscribe(__ -> speak(R.string.speech_playback_state_error));

    transition
        .filter(p -> p.getTo() == STATE_PAUSED)
        .subscribe(__ -> speak(R.string.speech_playback_state_paused));

    transition
        .filter(p -> p.getTo() == STATE_PLAYING && p.getFrom() != STATE_PLAYING)
        .filter(p -> p.getFrom() != STATE_REWINDING && p.getFrom() != STATE_FAST_FORWARDING)
        .subscribe(__ -> speak(R.string.speech_playback_state_playing));

    transition
        .filter(p -> p.getTo() == PlaybackStateCompat.STATE_STOPPED)
        .subscribe(__ -> speak(R.string.speech_playback_state_stopped));


    playbackClient.customEvent()
        .map(ce -> ce.second)
        .filter(bundle -> bundle.containsKey("resourceId"))
        .map(bundle -> bundle.getInt("resourceId"))
        .compose(bindToStatus())
        .subscribe(speaker::sayUrgent);
  }

  private void speak(int resourceId) {
    Timber.d("speak resourceId %d, %s", resourceId, this.toString());
    speaker.sayQueued(resourceId);
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
}
