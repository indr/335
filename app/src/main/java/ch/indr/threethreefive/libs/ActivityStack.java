/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import ch.indr.threethreefive.ui.activities.ButtonGuideActivity;
import timber.log.Timber;

public class ActivityStack implements ActivityStackType {

  private List<ButtonGuideActivity> stack = new ArrayList<>();

  @Override public void onActivityCreated(Activity activity, Bundle bundle) {
    if (activity.getClass().equals(ButtonGuideActivity.class)) {
      stack.add((ButtonGuideActivity) activity);
    }
  }

  @Override public void onActivityStarted(Activity activity) {

  }

  @Override public void onActivityResumed(Activity activity) {

  }

  @Override public void onActivityPaused(Activity activity) {

  }

  @Override public void onActivityStopped(Activity activity) {

  }

  @Override public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

  }

  @Override public void onActivityDestroyed(Activity activity) {
    if (activity.getClass().equals(ButtonGuideActivity.class)) {
      if (!stack.remove((ButtonGuideActivity) activity)) {
        Timber.w("Could not remove activity from stack %s", activity.toString());
      }
    }
  }

  @Override public List<String> getTitles() {
    List<String> titles = new ArrayList<>();
    for (int i = stack.size() - 1; i >= 0; i--) {
      ButtonGuideActivity each = stack.get(i);
      titles.add(each.getTitle().toString());
      // if (each.isHome) {
      //  break;
      // }
    }
    return titles;
  }
}
