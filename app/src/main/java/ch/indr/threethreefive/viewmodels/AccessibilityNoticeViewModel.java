/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.viewmodels;

import android.support.annotation.NonNull;

import ch.indr.threethreefive.libs.ActivityViewModel;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.ui.activities.AccessibilityNoticeActivity;

public class AccessibilityNoticeViewModel extends ActivityViewModel<AccessibilityNoticeActivity> {
  public AccessibilityNoticeViewModel(@NonNull Environment environment) {
    super(environment);
  }
}
