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
import ch.indr.threethreefive.ui.IntentKey;
import ch.indr.threethreefive.ui.activities.AccessibilityNoticeActivity;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class AccessibilityNoticeViewModel extends ActivityViewModel<AccessibilityNoticeActivity> {

  protected final BehaviorSubject<Integer> resourceId = BehaviorSubject.create();

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
  }

  public Observable<Integer> resourceId() {
    return resourceId;
  }
}
