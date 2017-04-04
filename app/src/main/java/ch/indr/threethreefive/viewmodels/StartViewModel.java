/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.viewmodels;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.ActivityViewModel;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.preferences.IntPreferenceType;
import ch.indr.threethreefive.services.Speaker;
import ch.indr.threethreefive.ui.activities.StartActivity;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class StartViewModel extends ActivityViewModel<StartActivity> {

  private final IntPreferenceType appLaunchCount;
  private String welcomeUtteranceId;

  private BehaviorSubject<Integer> showTextToSpeechError = BehaviorSubject.create();
  private BehaviorSubject<Object> showWelcome = BehaviorSubject.create();
  private BehaviorSubject<Object> startUiSelection = BehaviorSubject.create();

  protected @Inject Speaker speaker;

  public StartViewModel(@NonNull Environment environment) {
    super(environment);

    this.speaker = environment.speaker();
    this.appLaunchCount = environment.preferences().appLaunchCounter();
  }

  @Override protected void onCreate(@NonNull Context context, @Nullable Bundle savedInstanceState) {
    super.onCreate(context, savedInstanceState);

    speaker.status()
        .filter(status -> status != TextToSpeech.SUCCESS)
        .compose(bindToLifecycle())
        .subscribe(showTextToSpeechError);

    speaker.status()
        .filter(status -> status == TextToSpeech.SUCCESS)
        .compose(bindToLifecycle())
        .subscribe(this::speakWelcome);

    speaker.utteranceStart()
        .filter(utteranceId -> utteranceId.equals(welcomeUtteranceId))
        .compose(bindToLifecycle())
        .subscribe(showWelcome);

    speaker.utteranceDone()
        .filter(utteranceId -> utteranceId.equals(welcomeUtteranceId))
        .compose(bindToLifecycle())
        .subscribe(startUiSelection);

    speaker.utteranceError()
        .filter(utteranceId -> utteranceId.equals(welcomeUtteranceId))
        .map(__ -> -3) // TextToSpeech.ERROR_SYNTHESIS
        .compose(bindToLifecycle())
        .subscribe(showTextToSpeechError);
  }

  public Observable<Integer> showTextToSpeechError() {
    return showTextToSpeechError;
  }

  public Observable<Object> showWelcome() {
    return showWelcome;
  }

  public Observable<Object> startUiSelection() {
    return startUiSelection;
  }

  private void speakWelcome(Object __) {
    Timber.d("speakWelcome %s", this.toString());

    speaker.start();

    final int count = appLaunchCount.increment();
    if (count == 1) {
      welcomeUtteranceId = speaker.sayUrgent(R.string.speech_welcome_long);
    } else {
      welcomeUtteranceId = speaker.sayUrgent(R.string.speech_welcome_short);
    }
  }
}
