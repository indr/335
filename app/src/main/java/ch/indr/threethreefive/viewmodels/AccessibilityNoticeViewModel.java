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

import ch.indr.threethreefive.libs.ActivityViewModel;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.utils.StringUtils;
import ch.indr.threethreefive.ui.IntentKey;
import ch.indr.threethreefive.ui.activities.AccessibilityNoticeActivity;
import ch.indr.threethreefive.ui.activities.ButtonGuideActivity;
import ch.indr.threethreefive.ui.activities.ListGuideActivity;
import ch.indr.threethreefive.ui.activities.UiSelectionActivity;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.takeWhen;

public class AccessibilityNoticeViewModel extends ActivityViewModel<AccessibilityNoticeActivity> {

  // INPUTS
  protected final PublishSubject<Object> continueClicked = PublishSubject.create();

  // OUTPUTS
  protected final BehaviorSubject<Integer> resourceId = BehaviorSubject.create();
  protected final PublishSubject<Object> launchButtonsUi = PublishSubject.create();
  protected final PublishSubject<Object> launchListUi = PublishSubject.create();
  protected final PublishSubject<Object> launchUiSelection = PublishSubject.create();

  public AccessibilityNoticeViewModel(@NonNull Environment environment) {
    super(environment);
  }

  @Override protected void onCreate(@NonNull Context context, @Nullable Bundle savedInstanceState) {
    super.onCreate(context, savedInstanceState);

    intent().filter(i -> i.hasExtra(IntentKey.ACCESSIBILITY_NOTICE))
        .map(i -> i.getIntExtra(IntentKey.ACCESSIBILITY_NOTICE, 0))
        .filter(i -> i != 0)
        .compose(bindToLifecycle())
        .subscribe(resourceId);

    intent().filter(i -> i.hasExtra(IntentKey.NEXT_ACTIVITY))
        .map(i -> i.getStringExtra(IntentKey.NEXT_ACTIVITY))
        .filter(StringUtils::isNotEmpty)
        .compose(takeWhen(continueClicked))
        .take(1)
        .compose(bindToLifecycle())
        .subscribe(this::launchNextActivity);
  }

  public Observable<Integer> resourceId() {
    return resourceId;
  }

  public Observable<Object> launchButtonsUi() {
    return launchButtonsUi;
  }

  public Observable<Object> launchListUi() {
    return launchListUi;
  }

  public Observable<Object> launchUiSelection() {
    return launchUiSelection;
  }

  public void continueClicked() {
    continueClicked.onNext(null);
  }

  private void launchNextActivity(final @NonNull String name) {
    Timber.d("launchNextActivity %s", this.toString());

    if (UiSelectionActivity.class.getSimpleName().equals(name)) {
      launchUiSelection.onNext(null);
      return;
    }

    if (ButtonGuideActivity.class.getSimpleName().equals(name)) {
      launchButtonsUi.onNext(null);
      return;
    }

    if (ListGuideActivity.class.getSimpleName().equals(name)) {
      launchListUi.onNext(null);
      return;
    }

    Timber.w("Unknown activity: " + name);
  }
}
