/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.viewmodels;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle.ActivityEvent;

import java.util.concurrent.TimeUnit;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.ActivityViewModel;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.utils.StringUtils;
import ch.indr.threethreefive.services.Speaker;
import ch.indr.threethreefive.ui.activities.UiSelectionActivity;
import rx.Observable;
import rx.subjects.BehaviorSubject;

import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.combineLatestPair;

public class UiSelectionViewModel extends ActivityViewModel<UiSelectionActivity> {
  private final Speaker speaker;

  public UiSelectionViewModel(@NonNull Environment environment) {
    super(environment);

    this.speaker = environment.speaker();
  }

  @Override protected void onCreate(@NonNull Context context, @Nullable Bundle savedInstanceState) {
    super.onCreate(context, savedInstanceState);

    BehaviorSubject<String> utteranceId = BehaviorSubject.create();

    // Unset utteranceId when done or error
    Observable.merge(speaker.utteranceDone(), speaker.utteranceError())
        .compose(combineLatestPair(utteranceId))
        .filter(p -> StringUtils.equals(p.first, p.second))
        .delay(3, TimeUnit.SECONDS)
        .map(__ -> (String) null)
        .compose(bindToLifecycle())
        .subscribe(utteranceId);

    // Speak "Select user interface..."
    activityEvent().filter(e -> e == ActivityEvent.RESUME)
        .filter(__ -> utteranceId.getValue() == null)
        .map(__ -> R.string.speech_ui_selection)
        .map(speaker::sayUrgent)
        .compose(bindToLifecycle())
        .subscribe(utteranceId);
  }
}
