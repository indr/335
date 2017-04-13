/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.viewmodels;

import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.FragmentLifecycleType;
import ch.indr.threethreefive.libs.FragmentViewModel;
import ch.indr.threethreefive.libs.PlaybackStateTransition;
import ch.indr.threethreefive.libs.utils.ObjectUtils;
import ch.indr.threethreefive.services.PlaybackClient;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.takeWhen;

public class BasePlaybackFragmentViewModel<ViewType extends FragmentLifecycleType> extends FragmentViewModel<ViewType> {

  protected final PlaybackClient playbackClient;

  // INPUTS
  protected final PublishSubject<View> playPause = PublishSubject.create();
  protected final PublishSubject<View> skipToPrevious = PublishSubject.create();
  protected final PublishSubject<View> skipToNext = PublishSubject.create();

  // OUTPUTS
  protected final BehaviorSubject<Integer> playbackState = BehaviorSubject.create(PlaybackStateCompat.STATE_NONE);
  private final BehaviorSubject<PlaybackStateTransition> playbackStateTransition = BehaviorSubject.create();

  public BasePlaybackFragmentViewModel(@NonNull Environment environment) {
    super(environment);

    this.playbackClient = environment.playbackClient();

    playbackClient.playbackState()
        .compose(bindToLifecycle())
        .subscribe(playbackState);

    playbackClient.playbackStateCompat()
        .filter(ObjectUtils::isNotNull)
        .scan(new PlaybackStateTransition(), PlaybackStateTransition::next)
        .compose(bindToLifecycle())
        .subscribe(playbackStateTransition);

    playbackState
        .compose(takeWhen(playPause))
        .compose(bindToLifecycle())
        .subscribe(__ -> playbackClient.playPause());

    playbackState
        .compose(takeWhen(skipToPrevious))
        .compose(bindToLifecycle())
        .map(__ -> playbackClient.transportControls())
        .filter(transportControls -> transportControls != null)
        .subscribe(MediaControllerCompat.TransportControls::skipToPrevious);

    playbackState
        .compose(takeWhen(skipToNext))
        .compose(bindToLifecycle())
        .map(__ -> playbackClient.transportControls())
        .filter(transportControls -> transportControls != null)
        .subscribe(MediaControllerCompat.TransportControls::skipToNext);
  }

  // INPUTS
  public void playPause() {
    playPause.onNext(null);
  }

  public void skipToPrevious() {
    skipToPrevious.onNext(null);
  }

  public void skipToNext() {
    skipToNext.onNext(null);
  }

  // OUTPUTS
  public Observable<Integer> playbackState() {
    return playbackState;
  }

  public Observable<PlaybackStateTransition> playbackStateTransition() {
    return playbackStateTransition;
  }
}
