/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.viewmodels;

import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.View;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.ui.fragments.ButtonPlaybackFragment;
import rx.subjects.PublishSubject;

import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.takeWhen;

public class ButtonPlaybackFragmentViewModel extends BasePlaybackFragmentViewModel<ButtonPlaybackFragment> {

  // INPUTS
  private final PublishSubject<View> rewind = PublishSubject.create();
  private final PublishSubject<View> fastForward = PublishSubject.create();

  public ButtonPlaybackFragmentViewModel(@NonNull Environment environment) {
    super(environment);

    playbackState
        .compose(takeWhen(fastForward))
        .compose(bindToLifecycle())
        .map(__ -> playbackClient.transportControls())
        .filter(transportControls -> transportControls != null)
        .subscribe(MediaControllerCompat.TransportControls::fastForward);

    playbackState
        .compose(takeWhen(rewind))
        .compose(bindToLifecycle())
        .map(__ -> playbackClient.transportControls())
        .filter(transportControls -> transportControls != null)
        .subscribe(MediaControllerCompat.TransportControls::rewind);
  }

  // INPUTS
  public void fastForward() {
    fastForward.onNext(null);
  }

  public void rewind() {
    rewind.onNext(null);
  }
}
